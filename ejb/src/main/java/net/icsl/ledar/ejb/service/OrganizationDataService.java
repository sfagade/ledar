package net.icsl.ledar.ejb.service;

import java.io.Serializable;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.icsl.ledar.ejb.model.ApplicationConfigurations;
import net.icsl.ledar.ejb.model.AuthenticationRoles;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.Organizations;
import net.icsl.ledar.ejb.model.People;
import net.icsl.ledar.ejb.model.UserRoles;
import net.icsl.ledar.ejb.model.UsersLastActivities;

/**
 * @author sfagade
 * @date Oct 29, 2015
 */
@Stateless
public class OrganizationDataService implements Serializable {

    @PersistenceContext
    private EntityManager entManager;

    @Inject
    private ReferenceDataService ref_data;
    
    private static final long serialVersionUID = 1L;

    @TransactionAttribute(REQUIRES_NEW)
    public Organizations saveNewContractorInformation(Organizations contractor, People adminPerson, AuthenticationRoles auth_roles, List<LocalCouncilDevArea> sel_lcda, UsersLastActivities activity) {

        if (contractor != null && activity != null) {

            UserRoles u_roles;

            entManager.persist(contractor.getAddressId());
            entManager.persist(contractor);
            entManager.persist(activity);

            entManager.persist(adminPerson.getLogindetailId());
            entManager.persist(adminPerson);
            u_roles = new UserRoles(adminPerson.getLogindetailId(), auth_roles);
            entManager.persist(u_roles);

            if (sel_lcda != null && sel_lcda.size() > 0) {
                for (LocalCouncilDevArea lcdaArea : sel_lcda) {
                    lcdaArea = ref_data.findLocalCouncilDevAreaById(lcdaArea.getId());
                    lcdaArea.setContractorId(contractor);
                    entManager.merge(lcdaArea);
                }
            }

            return contractor;
        }

        return null;
    }

    public List<Organizations> fetchAllOrganizationByTypeId(Long type_id) {

        List<Organizations> supWards = entManager.createQuery("FROM Organizations o where o.organizationTypeId.id=:typeId", Organizations.class).setParameter("typeId", type_id).getResultList();

        return supWards;

    }

    public List<ApplicationConfigurations> fetchAppConfigData() {

        List<ApplicationConfigurations> appConfig = entManager.createQuery("FROM ApplicationConfigurations a", ApplicationConfigurations.class).getResultList();
        return appConfig;
    }
}
