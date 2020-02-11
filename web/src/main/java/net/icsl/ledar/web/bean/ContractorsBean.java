/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.bean;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;

import org.apache.shiro.crypto.hash.Sha256Hash;

import net.icsl.ledar.ejb.enums.ApplicationEntityNames;
import net.icsl.ledar.ejb.enums.UserActitiyName;
import net.icsl.ledar.ejb.model.Addresses;
import net.icsl.ledar.ejb.model.AuthenticationRoles;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.Organizations;
import net.icsl.ledar.ejb.model.People;
import net.icsl.ledar.ejb.model.SenatorialDistricts;
import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.service.OrganizationDataService;
import net.icsl.ledar.ejb.service.ReferenceDataService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.ejb.util.EmailSender;

/**
 *
 * @author sfagade
 */
@Named("contractorsBean")
@RequestScoped
public class ContractorsBean {

    @Inject
    private FacesContext facesContext;

    @Inject
    private ReferenceDataService ref_data;
    @Inject
    RegisteredUsersDataService regService;
    @Inject
    private OrganizationDataService orgService;

    @Inject
    private LoginBean loginBean;

    private List<LocalCouncilDevArea> selectedLcda; // +getter +setter
    private List<LocalCouncilDevArea> availableLcdas;
    private List<SenatorialDistricts> allSenatorialDistricts;
    private List<Organizations> allContractors;

    private Organizations organization;
    private Addresses address_;
    private SenatorialDistricts selectedSenateDist;
    private People loginPerson;

    /**
     * Creates a new instance of ContractorsBean
     */
    public ContractorsBean() {
    }

    @PostConstruct
    public void init() {
        String view_id = facesContext.getViewRoot().getViewId();

        if (view_id.equals("/app/administrator/createNewContractor.xhtml")) {
            if (organization == null) {
                organization = new Organizations();
                loginPerson = new People();
                loginPerson.setLogindetailId(new Logindetails());

                setAddress_(new Addresses());
                getAddress_().setGeographicalCountryId(ref_data.fetchDefaultCountry("Nigeria"));

                availableLcdas = ref_data.fetchAllUnassignedLcdas();
                allSenatorialDistricts = ref_data.fetchAllSenatorialDistricts();
            }
        } else if (view_id.equals("/app/administrator/allContractors.xhtml")) {
            allContractors = orgService.fetchAllOrganizationByTypeId(46L);
        }
    }

    public void createNewContractor() {

        String email_message, password = "pwd" + new Random().nextInt(10000000 + 1) + "_";
        String to_address[] = {organization.getEmailAddress(), "sfagade@ic-sol.net"};

        organization.setAddressId(address_);
        if (organization.getSenatorialDistrictId() != null) {
            SenatorialDistricts sen_district = ref_data.findSenatorialDistrictById(organization.getSenatorialDistrictId().getId());
            if (sen_district.getOrganizationsCollection().size() <= 0) {
                UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.SAVEDCONTRACTOR.toString(), new Date(), loginBean.getPerson().getFullName() + " saved new contractor "
                        + "information", loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, null, loginBean.getPerson().getLogindetailId(), null, null);

                activity.setEntityName(ApplicationEntityNames.ORGANIZATION.toString());
                activity.setEntityId(organization.getId());

                address_.setCreatedBy(loginBean.getPerson().getLogindetailId().getUsername());
                organization.setOrganizationTypeId(ref_data.findOrganizationTypesById(46L));
                organization.setOrganizationNm(organization.getOrganizationNm().toUpperCase());

                loginPerson.setOrganization(organization);
                loginPerson.getLogindetailId().setPword((new Sha256Hash(password).toHex()));
                loginPerson.getLogindetailId().setCreatedBy(loginBean.getUsername());
                loginPerson.setCreatedBy(loginBean.getUsername());
                loginPerson.getLogindetailId().setActivationKey(password);
                loginPerson.setAddressId(address_);
                loginPerson.setNationalityId(ref_data.fetchDefaultCountry("Nigeria"));
                loginPerson.getLogindetailId().setActive(true);
                loginPerson.getLogindetailId().setActivateStatus(true);

                AuthenticationRoles authRole = ref_data.fetchAuthenticationRoleById(17L);

                organization = orgService.saveNewContractorInformation(organization, loginPerson, authRole, selectedLcda, activity);

                if (organization != null) {

                    email_message = "<div>You have been created as " + authRole.getRoleName() + " on the LEDAR Application. Your login credentials are as follows<ul><li><b>Username:</b> " + 
                            loginPerson.getLogindetailId().getUsername() + "</li><li><b>Password:</b> " + password + "</li></ul>Once you're logged in, you will be able to create other users and "
                            + "manage users' account. You will also be able to manage your Company's account</div>To login please <a href=\"http://" + 
                            FacesContext.getCurrentInstance().getExternalContext().getRequestServerName() + ":" + FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort() + 
                            FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "\">Click here to view details.</a> in your browser and enter your credentials.<div></div>";

                    try {
                        new EmailSender().sendPlainEmailMessage(email_message, "LEDAR Account Creation", to_address);
                    } catch (MessagingException ex) {
                        Logger.getLogger(ContractorsBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New Contractor has been saved successfully and email sent to the address provided " + organization.getId(), null));
                    organization = null;
                    this.init();

                } else {
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save contractor information, please try again or contact support", null));
                }

            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SenatorialDistricts is already assigned to another Contractor, remove it from the other contractor before"
                        + " reassigning it ", null));
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SenatorialDistricts is required please! ", null));
        }
    }

    /**
     * @return the organization
     */
    public Organizations getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization(Organizations organization) {
        this.organization = organization;
    }

    /**
     * @return the address_
     */
    public Addresses getAddress_() {
        return address_;
    }

    /**
     * @param address_ the address_ to set
     */
    public void setAddress_(Addresses address_) {
        this.address_ = address_;
    }

    /**
     * @return the selectedLcda
     */
    public List<LocalCouncilDevArea> getSelectedLcda() {
        return selectedLcda;
    }

    /**
     * @param selectedLcda the selectedLcda to set
     */
    public void setSelectedLcda(List<LocalCouncilDevArea> selectedLcda) {
        this.selectedLcda = selectedLcda;
    }

    /**
     * @return the availableLcdas
     */
    public List<LocalCouncilDevArea> getAvailableLcdas() {
        return availableLcdas;
    }

    /**
     * @param availableLcdas the availableLcdas to set
     */
    public void setAvailableLcdas(List<LocalCouncilDevArea> availableLcdas) {
        this.availableLcdas = availableLcdas;
    }

    /**
     * @return the allSenatorialDistricts
     */
    public List<SenatorialDistricts> getAllSenatorialDistricts() {
        return allSenatorialDistricts;
    }

    /**
     * @param allSenatorialDistricts the allSenatorialDistricts to set
     */
    public void setAllSenatorialDistricts(List<SenatorialDistricts> allSenatorialDistricts) {
        this.allSenatorialDistricts = allSenatorialDistricts;
    }

    /**
     * @return the selectedSenateDist
     */
    public SenatorialDistricts getSelectedSenateDist() {
        return selectedSenateDist;
    }

    /**
     * @param selectedSenateDist the selectedSenateDist to set
     */
    public void setSelectedSenateDist(SenatorialDistricts selectedSenateDist) {
        this.selectedSenateDist = selectedSenateDist;
    }

    /**
     * @return the loginBean
     */
    public LoginBean getLoginBean() {
        return loginBean;
    }

    /**
     * @param loginBean the loginBean to set
     */
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    /**
     * @return the loginPerson
     */
    public People getLoginPerson() {
        return loginPerson;
    }

    /**
     * @param loginPerson the loginPerson to set
     */
    public void setLoginPerson(People loginPerson) {
        this.loginPerson = loginPerson;
    }

    /**
     * @return the allContractors
     */
    public List<Organizations> getAllContractors() {
        return allContractors;
    }

    /**
     * @param allContractors the allContractors to set
     */
    public void setAllContractors(List<Organizations> allContractors) {
        this.allContractors = allContractors;
    }

}
