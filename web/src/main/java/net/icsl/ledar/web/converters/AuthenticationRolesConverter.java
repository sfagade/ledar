package net.icsl.ledar.web.converters;

import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.icsl.ledar.ejb.model.AuthenticationRoles;

@ManagedBean(name = "authRoleConverterBean")
@FacesConverter(value = "authRoleConverter")
public class AuthenticationRolesConverter implements Converter {

    @PersistenceContext
    // I include this because you will need to 
    // lookup  your entities based on submitted values
    private transient EntityManager em;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<AuthenticationRoles> criteria = cb.createQuery(AuthenticationRoles.class);
        Root<AuthenticationRoles> deptObj = criteria.from(AuthenticationRoles.class);
        if ((value != null) && (!value.isEmpty()) && (!value.contains("Select"))) {
            criteria = criteria.multiselect(deptObj.get("id"), deptObj.get("roleName"), deptObj.get("roleOrder"), deptObj.get("created")).where(cb.equal(deptObj.get("id"), new Long(value)));
            return em.createQuery(criteria).getSingleResult();
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
        AuthenticationRoles sec_qs = ((AuthenticationRoles) o);
        return (sec_qs != null) ? sec_qs.getId().toString() : null;
    }
}
