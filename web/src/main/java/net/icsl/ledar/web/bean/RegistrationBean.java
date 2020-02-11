package net.icsl.ledar.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import net.icsl.ledar.ejb.model.Addresses;
import net.icsl.ledar.ejb.model.AuthenticationRoles;
import net.icsl.ledar.ejb.model.ContactInformations;
import net.icsl.ledar.ejb.model.EnumeratorWards;
import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.People;
import net.icsl.ledar.ejb.model.UniqueUserIdentifications;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.ReferenceDataService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.ejb.util.EmailSender;
import net.icsl.ledar.web.util.ApplicationUtility;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.SecurityUtils;

@Named("register")
@ConversationScoped
public class RegistrationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private LedarApplicationBean ledarApp;
    @Inject
    private LoginBean loginBean;
    @Inject
    private AdministratorBean adminBean;

    private Logindetails user;
    private People person;
    private AuthenticationRoles authRole;
    private Addresses address_;

    private String[] customerSelected;
    private AuthenticationRoles selected_roles;
    private LocalCouncilDevArea selected_lcda;
    private LcdaWards selected_ward;
    private String email_address, phoneNumber, personal_email, ward_id;
    private Boolean generatePwd = false, isActive = true, sendNotifyEmail;
    private Date dob_;

    @Inject
    private RegisteredUsersDataService registeredService;
    @Inject
    private ReferenceDataService ref_data;
    @Inject
    private LcdaWardsDataServices lcdaWardService;

    @Inject
    private FacesContext facesContext;

    @PostConstruct
    public void init() {
        user = new Logindetails();
        user.setPerson(new People());
        person = new People();

        address_ = new Addresses();
        address_.setGeographicalCountryId(ref_data.fetchDefaultCountry("Nigeria"));

        email_address = null;
        authRole = new AuthenticationRoles();
        setSelected_roles(null);
        generatePwd = false;
        isActive = false;
        dob_ = null;

        phoneNumber = null;
        sendNotifyEmail = false;
    }

    public String createNewUser() {

        try {
            Random rand = new Random();
            String email_message, password;
            String to_address[] = {getEmail_address(), "sfagade@ic-sol.net"};

            org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();

            phoneNumber = ApplicationUtility.formatPhoneNumber(phoneNumber, "Nigeria");

            if (phoneNumber != null) {
                password = user.getPword();
                user.setPword(new Sha256Hash(user.getPword()).toHex());
                user.getPerson().setLastName(user.getPerson().getLastName().toUpperCase());
                user.getPerson().setFirstName(user.getPerson().getFirstName().toUpperCase());
                user.setActive(isActive);
                user.setCreatedBy(currentUser.getPrincipal().toString());
                user.setActivationKey(rand.nextInt(10000000 + 1) + ":" + rand.nextInt(10000000 + 1));
                user.getPerson().setLogindetailId(user);
                user.getPerson().setContactInformationsList(new ArrayList<ContactInformations>() {
                    {
                        add(new ContactInformations(null, getEmail_address(), user.getPerson(), phoneNumber, address_, personal_email, null));
                    }
                });
                address_.setCreatedBy(currentUser.getPrincipal().toString());
                user.getPerson().setAddressId(address_);
                user.getPerson().setDob(dob_);
                user.getPerson().setOrganization(loginBean.getPerson().getOrganization());
                user.setUserNumber(this.generateUserUniqueNumber("Employee"));
                user.getPerson().setCreatedBy(currentUser.getPrincipal().toString());
                user.getPerson().setNationalityId(ref_data.fetchDefaultCountry("Nigeria"));

                if (selected_roles.getRoleName().equalsIgnoreCase("ENUMERATOR")) {
                    user.setEnumeratorWardsList(new ArrayList<EnumeratorWards>() {
                        {
                            add(new EnumeratorWards(null, selected_ward, user, null));
                        }
                    });
                }

                user = registeredService.registerNewUser(user, getSelected_roles(), address_); //3 is the ID for normal user

                FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration succeed, new user ID is: " + user.getUserNumber(), null);
                facesContext.addMessage("createUser:asu_btn", m);

                if (sendNotifyEmail) {
                    email_message = "Your LEDAR account has been created Your login credentials are as follows:<br /><ul><li>Username: " + user.getUsername() + " </li><li>Password: " + password + "</li></ul>" //TODO I should consider changing this to SMS instead
                            + "You can now login with your credentials anytime into the LEDAR Application";

                    new EmailSender().sendPlainEmailMessage(email_message, "New LEDAR Account Created", to_address);
                }
                this.init();
                adminBean.setRegisteredUsers(registeredService.fetchAllRegisteredUsers(loginBean.getPerson().getOrganization().getId()));
                adminBean.setRecordCount(Long.valueOf(adminBean.getRegisteredUsers().size()));
                return "confirmation.xhtml";
            } else {
                FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid phone number specified ", null);
                facesContext.addMessage("createUser:asu_btn", m);
            }
        } catch (AddressException e) {
            Logger.getLogger(RegistrationBean.class.getName()).log(Level.SEVERE, null, e);
        } catch (MessagingException e) {
            Logger.getLogger(RegistrationBean.class.getName()).log(Level.SEVERE, null, e);
        } catch (EJBTransactionRolledbackException ce) {
            Throwable t = ce.getCause();
            while ((t != null) && !(t instanceof org.hibernate.exception.ConstraintViolationException)) {
                t = t.getCause();
            }
            FacesMessage retMsg;
            if (t instanceof org.hibernate.exception.ConstraintViolationException) {
                retMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please make sure the username and email address you've entered have not been used before", "Please make sure the "
                        + "username and email address you've entered have not been used before");
            } else {
                retMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save employee information", "Failed to save employee information, please try again later");

            }
            facesContext.addMessage("createUser:asu_btn", retMsg);
            Logger.getLogger(RegistrationBean.class.getName()).log(Level.SEVERE, null, ce);
        } catch (RuntimeException e) {
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed: {0}", e.getMessage());
            facesContext.addMessage(null, m);
            Logger.getLogger(RegistrationBean.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public String generateUserUniqueNumber(String key_type) {

        String unique_number = null;
        long last_number = 0;

        UniqueUserIdentifications uniqueId = ref_data.fetchLastUniqueKey(key_type);

        if (key_type.equalsIgnoreCase("Employee")) {
            last_number = uniqueId.getLastEmployeeNumber() + 1;
            unique_number = "B-";
            uniqueId.setLastEmployeeNumber(last_number);
        } else if (key_type.equalsIgnoreCase("Customer")) {
            last_number = uniqueId.getLastCustomerNumber() + 1;
            unique_number = "C-";
            uniqueId.setLastCustomerNumber(last_number);
        }

        if (last_number < 10) {
            unique_number += "000" + last_number;
        } else if (last_number < 100) {
            unique_number += "00" + last_number;
        }

        ref_data.updateLastuniqueKey(uniqueId);

        return unique_number;
    }

    public void savePersonInfo() {

        getPerson().setLastName(WordUtils.capitalizeFully(getPerson().getLastName()));
        getPerson().setFirstName(WordUtils.capitalizeFully(getPerson().getFirstName()));

        getPerson().setLogindetailId(registeredService.find(36L));
        //getPerson().setLoginId("36");
        getPerson().setContactInformationsList(new ArrayList<ContactInformations>() {
            {
                add(new ContactInformations(null, getEmail_address(), getPerson(), null));
            }
        });

        person = registeredService.createNewPerson(person);

        FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "New Job title created successfully: {0}", person.getId().toString());
        facesContext.addMessage("updateJobTitle:asu_btn", m);

        this.init();
    }

    public void changedLcda() {
        System.out.println("called changeLcda: " + selected_lcda);
        if (selected_lcda != null) {
            loginBean.setLcdaWards(lcdaWardService.fetchAllLcdaWardsByLcda(selected_lcda.getId(), null));
        }
    }

    public Logindetails getUser() {
        return user;
    }

    public String[] getCustomerSelected() {
        return customerSelected;
    }

    public void setCustomerSelected(String[] customerSelected) {
        this.customerSelected = customerSelected;
    }

    public String getWard_id() {
        return ward_id;
    }

    public void setWard_id(String ward_id) {
        this.ward_id = ward_id;
    }

    public People getPerson() {
        return person;
    }

    public void setPerson(People person) {
        this.person = person;
    }

    public Boolean getGeneratePwd() {
        return generatePwd;
    }

    public void setGeneratePwd(Boolean generatePwd) {
        this.generatePwd = generatePwd;
    }

    /**
     * @return the email_address
     */
    public String getEmail_address() {
        return email_address;
    }

    /**
     * @param email_address the email_address to set
     */
    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public Date getDob_() {
        return dob_;
    }

    public void setDob_(Date dob_) {
        this.dob_ = dob_;
    }

    /**
     * @return the authRole
     */
    public AuthenticationRoles getAuthRole() {
        return authRole;
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
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the personal_email
     */
    public String getPersonal_email() {
        return personal_email;
    }

    /**
     * @param personal_email the personal_email to set
     */
    public void setPersonal_email(String personal_email) {
        this.personal_email = personal_email;
    }

    public LedarApplicationBean getLedarApp() {
        return ledarApp;
    }

    public void setLedarApp(LedarApplicationBean ledarApp) {
        this.ledarApp = ledarApp;
    }

    /**
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    /**
     * @return the selected_roles
     */
    public AuthenticationRoles getSelected_roles() {
        return selected_roles;
    }

    /**
     * @param selected_roles the selected_roles to set
     */
    public void setSelected_roles(AuthenticationRoles selected_roles) {
        this.selected_roles = selected_roles;
    }

    /**
     * @return the selected_lcda
     */
    public LocalCouncilDevArea getSelected_lcda() {
        return selected_lcda;
    }

    /**
     * @param selected_lcda the selected_lcda to set
     */
    public void setSelected_lcda(LocalCouncilDevArea selected_lcda) {
        this.selected_lcda = selected_lcda;
    }

    /**
     * @return the selected_ward
     */
    public LcdaWards getSelected_ward() {
        return selected_ward;
    }

    /**
     * @param selected_ward the selected_ward to set
     */
    public void setSelected_ward(LcdaWards selected_ward) {
        this.selected_ward = selected_ward;
    }

    public Boolean getSendNotifyEmail() {
        return sendNotifyEmail;
    }

    public void setSendNotifyEmail(Boolean sendNotifyEmail) {
        this.sendNotifyEmail = sendNotifyEmail;
    }

    public AdministratorBean getAdminBean() {
        return adminBean;
    }

    public void setAdminBean(AdministratorBean adminBean) {
        this.adminBean = adminBean;
    }
}
