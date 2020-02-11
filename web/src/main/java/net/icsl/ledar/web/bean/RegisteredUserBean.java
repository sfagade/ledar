package net.icsl.ledar.web.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import net.icsl.ledar.ejb.enums.ApplicationEntityNames;
import net.icsl.ledar.ejb.enums.UserActitiyName;
import net.icsl.ledar.ejb.model.ContactInformations;
import net.icsl.ledar.ejb.model.EnumeratorWards;
import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.People;
import net.icsl.ledar.ejb.model.RegisteredDevices;
import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.web.util.FacesSupportUtil;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Sha256Hash;

/**
 *
 * @author sfagade
 */
@Named("registeredUser")
@SessionScoped
public class RegisteredUserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private RegisteredUsersDataService registeredService;
    @Inject
    private LcdaWardsDataServices lcdaService;

    @Inject
    private FacesContext facesContext;

    @Inject
    private LoginBean loginBean;

    private String paword;
    private String new_paword;
    private String confirm_paword;
    private String wrong_pwd;
    private String notificationId;
    private Boolean markComplete = false;
    private String username;

    private People person, person_profile;
    private RegisteredDevices selectedDevice;
    private ContactInformations person_contact;

    private List<LcdaWards> lcdaWardList;
    private List<EnumeratorWards> enumWards;
    private List<RegisteredDevices> userDevices;
    private List<Logindetails> allFieldOfficers;

    @PostConstruct
    public void init() {

        String view_id = facesContext.getViewRoot().getViewId();
        

        if (view_id.equalsIgnoreCase("/app/lcda/listAllEnumerators.xhtml")) {
            List<Long> ward_ids = new ArrayList<>();
            if (loginBean.getuRoles().getAuthenticationRoleId().getRoleName().equalsIgnoreCase("WARD SUPERVISOR")) {
                lcdaWardList = lcdaService.fetchAllSupervisorWards(loginBean.getPerson().getLogindetailId().getId());

                if (lcdaWardList != null) {
                    //long[] ward_ids = new long[lcdaWardList.size()];
                    //int counter_ = 0;
                    for (LcdaWards ward_ : lcdaWardList) {
                        ward_ids.add(ward_.getId());
                    }

                    enumWards = lcdaService.fetchAllWardEnumerators(ward_ids);

                } else {
                    System.out.println("user with name: " + getLoginBean().getCurrentUserName() + " hasn't been assigned a ward");
                }
            } else if (loginBean.getuRoles().getAuthenticationRoleId().getRoleName().equalsIgnoreCase("LCDA CHAIRMAN")) {
                lcdaWardList = lcdaService.fetchAllLcdaWards(loginBean.getChairmanLcda().getId());
                for (LcdaWards ward_ : lcdaWardList) {
                    ward_ids.add(ward_.getId());
                }

                enumWards = lcdaService.fetchAllWardEnumerators(ward_ids);
            }

        } else if (view_id.equalsIgnoreCase("/app/appAdmin/userDetails.xhtml")) {
            
        }

        markComplete = false;
    }
    
    public void initUserDetailView() {
    	String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "ADMINISTRATOR USER"};
    	String pers_id = facesContext.getExternalContext().getRequestParameterMap().get("usid");
    	
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            if (pers_id != null) {
                person = registeredService.findById(Long.valueOf(pers_id));
                userDevices = registeredService.fetchRegisteredDevicesFilter(person.getLogindetailId().getId(), null, null);
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void updateDeviceInformation() {
        if (selectedDevice != null && selectedDevice.getDeviceName() != null && !selectedDevice.getDeviceName().isEmpty()) {
            selectedDevice.setLastUpdatedById(loginBean.getPerson().getLogindetailId());

            UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.UPDATEDDEVICEINFO.toString(), new Date(), loginBean.getPerson().getFullName() + " updated device information for "
                    + selectedDevice.getDeviceOwnerById().getUsername(), loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, null,
                    loginBean.getPerson().getLogindetailId(), null, null);

            activity.setEntityName(ApplicationEntityNames.REGISTEREDDEVICE.toString());
            activity.setEntityId(selectedDevice.getId());

            if (registeredService.saveNewRegisteredDevice(selectedDevice, activity)) {

                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Device information has been updated successfully!", "Success"));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                selectedDevice = null;

                try {
                    facesContext.getExternalContext().redirect("registeredDevices.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(RegisteredUserBean.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to update Device informaion, please check form and try again!", ""));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            }

        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Form validation failed, please check form and try again!", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void initDeviceUpdate(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String device_str = params.get("action");

        if (device_str != null && !device_str.isEmpty()) {

            selectedDevice = registeredService.findRegisteredDeviceById(Long.valueOf(device_str));

            if (selectedDevice != null) {
                Long consultant = (loginBean.getPerson().getOrganization() != null) ? loginBean.getPerson().getOrganization().getId() : null;
                Long[] field_roles = {9L, 19L, 26L}; //all field officers ID?
                setAllFieldOfficers(registeredService.fetchAllLcdaChairmen(Arrays.asList(field_roles), consultant));
                try {
                    context.getExternalContext().redirect("updateRegisteredDevice.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(RegisteredUserBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not find Device information, please try again later", "Error"));
                context.getExternalContext().getFlash().setKeepMessages(true);
            }
        } else {
            try {
                context.getExternalContext().redirect("registeredDevices.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(RegisteredUserBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void changeNow() {

        if (new_paword.equals(confirm_paword)) {

            setUsername(SecurityUtils.getSubject().getPrincipal().toString());
            try {
                SecurityUtils.getSubject().login(new UsernamePasswordToken(getUsername(), paword, false));

                setPerson(registeredService.findUserByUsername(getUsername()));
                getPerson().getLogindetailId().setPword(new Sha256Hash(new_paword).toHex());
                getPerson().getLogindetailId().setPwordResetRequired(false);

                registeredService.update(getPerson().getLogindetailId(), null);

                FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Password has been changed successfully, you will be required to enter the new password on next login!", "");
                facesContext.addMessage("pwordchng:sbtbtn", m);

                this.resetForm();
            } catch (org.apache.shiro.authc.IncorrectCredentialsException ic) {
                FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "The current password you entered is incorrect, did you forget your password!?", "");
                wrong_pwd = "isWrong";
                facesContext.addMessage("pwordchng:sbtbtn", m);
            }

        } else { //password entries do not match
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password entries do not match!", "");
            facesContext.addMessage("pwordchng:sbtbtn", m);
        }
    }

    public void initializeUpdate() {
        markComplete = false;
    }

    public void resetForm() {
        paword = null;
        confirm_paword = null;
        new_paword = null;
    }

    /**
     * @return the paword
     */
    public String getPaword() {
        return paword;
    }

    /**
     * @param paword the paword to set
     */
    public void setPaword(String paword) {
        this.paword = paword;
    }

    public List<LcdaWards> getLcdaWardList() {
        return lcdaWardList;
    }

    public void setLcdaWardList(List<LcdaWards> lcdaWardList) {
        this.lcdaWardList = lcdaWardList;
    }

    /**
     * @return the new_paword
     */
    public String getNew_paword() {
        return new_paword;
    }

    /**
     * @param new_paword the new_paword to set
     */
    public void setNew_paword(String new_paword) {
        this.new_paword = new_paword;
    }

    /**
     * @return the confirm_paword
     */
    public String getConfirm_paword() {
        return confirm_paword;
    }

    /**
     * @param confirm_paword the confirm_paword to set
     */
    public void setConfirm_paword(String confirm_paword) {
        this.confirm_paword = confirm_paword;
    }

    public String getWrong_pwd() {
        return wrong_pwd;
    }

    public void setWrong_pwd(String wrong_pwd) {
        this.wrong_pwd = wrong_pwd;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public Boolean getMarkComplete() {
        return markComplete;
    }

    public void setMarkComplete(Boolean markComplete) {
        this.markComplete = markComplete;
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
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the person
     */
    public People getPerson() {
        return person;
    }

    /**
     * @param person the person to set
     */
    public void setPerson(People person) {
        this.person = person;
    }

    /**
     * @return the person_profile
     */
    public People getPerson_profile() {
        return person_profile;
    }

    /**
     * @param person_profile the person_profile to set
     */
    public void setPerson_profile(People person_profile) {
        this.person_profile = person_profile;
    }

    /**
     * @return the enumWards
     */
    public List<EnumeratorWards> getEnumWards() {
        return enumWards;
    }

    /**
     * @param enumWards the enumWards to set
     */
    public void setEnumWards(List<EnumeratorWards> enumWards) {
        this.enumWards = enumWards;
    }

    /**
     * @return the person_contact
     */
    public ContactInformations getPerson_contact() {
        return person_contact;
    }

    /**
     * @param person_contact the person_contact to set
     */
    public void setPerson_contact(ContactInformations person_contact) {
        this.person_contact = person_contact;
    }

    public List<RegisteredDevices> getUserDevices() {
        return userDevices;
    }

    public void setUserDevices(List<RegisteredDevices> userDevices) {
        this.userDevices = userDevices;
    }

    public RegisteredDevices getSelectedDevice() {
        return selectedDevice;
    }

    public void setSelectedDevice(RegisteredDevices selectedDevice) {
        this.selectedDevice = selectedDevice;
    }

    public List<Logindetails> getAllFieldOfficers() {
        return allFieldOfficers;
    }

    public void setAllFieldOfficers(List<Logindetails> allFieldOfficers) {
        this.allFieldOfficers = allFieldOfficers;
    }

}
