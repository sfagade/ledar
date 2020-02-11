/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;
import net.icsl.ledar.ejb.model.AuthenticationRoles;

import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.RegisteredDevices;
import net.icsl.ledar.ejb.model.WardLandProperties;
import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.ejb.util.EmailSender;
import net.icsl.ledar.web.util.FacesSupportUtil;

import org.apache.shiro.crypto.hash.Sha256Hash;

/**
 *
 * @author sfagade
 */
@Named("administratorBean")
@SessionScoped
public class AdministratorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private LoginBean loginBean;
    @Inject
    private UploadedFilesBean uploadBean;

    @Inject
    private FacesContext facesContext;

    @Inject
    private RegisteredUsersDataService regUserService;
    @Inject
    private LcdaWardsDataServices lcdaService;
    @Inject
    private LandPropertiesDataService landPropService;

    @Inject
    private EmailSender emailSender;

    private List<Logindetails> registeredUsers;
    private List<WardLandProperties> wardProperties;
    private List<FileUploads> fileUploads;
    private List<AuthenticationRoles> authenticationRoles;
    private List<RegisteredDevices> registeredDevices;
    private List<Logindetails> allFieldOfficers;

    private WardStreets selectedStreet;
    private Logindetails selectedLoginUser;
    private AuthenticationRoles selectedRole;
    private RegisteredDevices registeredDevice;
    private Logindetails selectedUser;

    private List<String> uniqueStreets;
    private List<String[]> duplicateList;

    private Long recordCount, unprocessedCount, logindetailId;
    private int batchSize;
    private String macAddress, imeiNumber;

    @PostConstruct
    public void init() {

        String view_id = facesContext.getViewRoot().getViewId();

        if ((view_id.equalsIgnoreCase("/app/appAdmin/listAllUsers.xhtml"))) {

        } else if (view_id.equalsIgnoreCase("/app/administrator/listAllUsers.xhtml")) {
            setRegisteredUsers(regUserService.fetchAllRegisteredUsers(null));
            recordCount = Long.valueOf(registeredUsers.size());
        } else if (view_id.equalsIgnoreCase("/app/administrator/streetManagement.xhtml")) {

        } else if (view_id.equalsIgnoreCase("/app/administrator/photoManagement.xhtml")) {
            recordCount = lcdaService.countPhotosWithBlob();
            unprocessedCount = lcdaService.countPhotosWithoutBlob();
        } else if (view_id.equalsIgnoreCase("/app/administrator/duplicateProperties.xhtml")) {
            duplicateList = lcdaService.fetchDuplicateProperty(null);
            recordCount = Long.valueOf(duplicateList.size());
        }
    }

    public void filterDeviceResult() {

    }

    public void saveNewDevice() {

        if (registeredDevice.getDeviceName() != null && !registeredDevice.getDeviceName().isEmpty()) {
            registeredDevice.setCreatedById(loginBean.getPerson().getLogindetailId());
            registeredDevice.setConsultantId(loginBean.getPerson().getOrganization());
            registeredDevice.setIsEnabled(Boolean.TRUE);

            List<RegisteredDevices> existingDevice = regUserService.fetchRegisteredDevicesFilter(null, registeredDevice.getMacAddress(), null);

            if (existingDevice == null || existingDevice.size() <= 0) {
                if (regUserService.saveNewRegisteredDevice(registeredDevice, null)) {
                    if (registeredDevices != null) {
                        registeredDevices.add(0, registeredDevice);
                    }
                    registeredDevice = new RegisteredDevices();

                    FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "New Device saved successfully", "");
                    facesContext.addMessage(null, m);
                    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

                } else {
                    FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save new Device, please try again later", "");
                    facesContext.addMessage(null, m);
                    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                }
            } else {
                FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Device with MAC ADDRESS already exist!", "");
                facesContext.addMessage(null, m);
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            }

        } else {
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "All fields with asterix (*) are required", "");
            facesContext.addMessage(null, m);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void initNewDevice() {
        if (registeredDevice == null) {
            registeredDevice = new RegisteredDevices();

            Long consultant = (loginBean.getPerson().getOrganization() != null) ? loginBean.getPerson().getOrganization().getId() : null;

            Long[] field_roles = {9L, 19L, 26L}; //all field officers ID?
            setAllFieldOfficers(regUserService.fetchAllLcdaChairmen(Arrays.asList(field_roles), consultant));
        }
    }

    public void initRegisteredDeviceView() {
        //if (registeredDevices == null || registeredDevices.size() <= 0) {
            Long consultant = (loginBean.getPerson().getOrganization() != null) ? loginBean.getPerson().getOrganization().getId() : null;

            registeredDevices = regUserService.fetchAllRegisteredDevices(consultant);
            recordCount = Long.valueOf(registeredDevices.size());

            Long[] field_roles = {9L, 19L, 26L}; //all field officers ID?
            setAllFieldOfficers(regUserService.fetchAllLcdaChairmen(Arrays.asList(field_roles), consultant));
        //}
    }

    public void initStreetManagementView() {
        String[] accepted_roles = {"ADMINISTRATOR", "DATA ENTRY"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            uniqueStreets = lcdaService.fetchAllPropertyDistinctStreets(null);
            if (uniqueStreets != null) {
                recordCount = Long.valueOf(uniqueStreets.size());
            }
        } else {
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", "");
            facesContext.addMessage(null, m);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/app/index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(AdministratorBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void changeUserPassword() {
        Logger.getLogger(AdministratorBean.class.getName()).log(Level.INFO, "Passed change sent: {0}", selectedLoginUser.getPword());
        if (selectedLoginUser.getPword() != null && !selectedLoginUser.getPword().isEmpty()) {
            selectedLoginUser.setPword(new Sha256Hash(selectedLoginUser.getPword()).toHex());

            if (regUserService.update(selectedLoginUser, null)) {
                FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Password changed successfully", "");
                facesContext.addMessage(null, m);
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("userDetails.xhtml?usid=" + selectedLoginUser.getPerson().getId());
                } catch (IOException ex) {
                    Logger.getLogger(AdministratorBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else {
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter a new valid password", "");
            facesContext.addMessage(null, m);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }

    }

    public void preparePasswordChange() {

        if (logindetailId != null && selectedLoginUser == null) {
            selectedLoginUser = regUserService.find(logindetailId);
        }

        selectedLoginUser.setPword(null);

    }

    public List<Logindetails> runPersonAutoComplete(String person_name) {
        List<Logindetails> allValues = regUserService.fetchLoginusersNameLike(person_name);

        return allValues;
    }

    public void filterUsersList() {

        if (selectedLoginUser != null) {
            Long login_id = (selectedLoginUser.getPerson() != null) ? selectedLoginUser.getPerson().getId() : null;
            if (login_id == null) {
                Logindetails login = regUserService.find(selectedLoginUser.getId());
                login_id = login.getPerson().getId();
            }
            selectedLoginUser = null;
            try { //If a user is selected, we'll just show that user's full detail
                FacesContext.getCurrentInstance().getExternalContext().redirect("userDetails.xhtml?usid=" + login_id);
            } catch (IOException ex) {
                Logger.getLogger(UploadManagerBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (selectedRole != null) { //I need to show everybody in a given role
            List<Long> roles = new ArrayList<>();
            roles.add(selectedRole.getId());
            registeredUsers = regUserService.fetchAllLcdaChairmen(roles, loginBean.getPerson().getOrganization().getId());
            recordCount = Long.valueOf(registeredUsers.size());
        }
    }

    public void resetUsersDisplay() {
        registeredUsers = null;
        selectedLoginUser = null;
        selectedRole = null;

        this.initAllUsers();
    }

    public void sendTestMail() {

        System.out.println("About to Send Test Mail");
        String[] to_addresses = {"sfagade@ic-sol.net", "sfagade@gmail.com"};
        String[] bc_addresses = null;
        String[] cc_addresses = null;
        String subject = "Test MMail from Test Unit";
        String message = "This is the mmmail body";

        List<File> attachedFiles = new ArrayList<>();
        attachedFiles.add(new File("/home/sfagade/Opt/Servers/wildfly-9.0.2.Final/standalone/deployments/file_uploads/bills/2017/payment_files/sample_payment_ready_PROCESSED.xlsx"));

        try {
            emailSender.sendEmailWithAttachment(to_addresses, bc_addresses, cc_addresses, subject, message, attachedFiles);

        } catch (MessagingException ex) {
            Logger.getLogger(AdministratorBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void initAllUsers() {
        if (registeredUsers == null) {
            setRegisteredUsers(regUserService.fetchAllRegisteredUsers(loginBean.getPerson().getOrganization().getId()));
            recordCount = Long.valueOf(registeredUsers.size());
        }
    }

    /**
     * Creates a new instance of AdministratorBean
     */
    public AdministratorBean() {

    }

    public List<WardStreets> runAutoComplete(String query) {
        List<WardStreets> allValues = lcdaService.fetchWardStreetsByStreetName(query, null, false);

        return allValues;
    }

    public void processPhotoBatch() {

        if ((batchSize > 0)) {
            fileUploads = lcdaService.fetchBlobPicturesBySize(batchSize);
            int number_done = 0;

            if ((fileUploads != null) && (fileUploads.size() > 0)) { //this should be the case until we're done with the migration

                for (FileUploads fileUpload : fileUploads) {
                    if (uploadBean.processFileUploadPictures(fileUpload)) {
                        fileUpload.setFileContent(null);
                        fileUpload.setAbsolutePath(uploadBean.getProp().getProperty("imageURl"));

                        fileUpload = lcdaService.updateFileUploadInfo(fileUpload, null);
                        number_done++;

                    } else {
                        Logger.getLogger(AdministratorBean.class.getName()).log(Level.SEVERE, null, "Failed to save picture");
                    }
                    //TODO I need to save the file to the hard disk
                }

            }

            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Processing complete, completed count is: " + number_done, ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            batchSize = 0;
            recordCount = lcdaService.countPhotosWithBlob();
            unprocessedCount = lcdaService.countPhotosWithoutBlob();
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select a value greater than zero for Batch Size ", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void cleanPropertyStreet() {

    }

    public void cleanupDuplicateProperties() {

        if (duplicateList == null) {
            duplicateList = lcdaService.fetchDuplicateProperty(null);
        }

        List<WardLandProperties> dupProperties;
        WardLandProperties firstProperty, secondProperty;

        for (Object[] dups : duplicateList) {
            dupProperties = lcdaService.fetchAllPropertyByPropertyId(dups[0].toString(), false, null);
            if ((dupProperties != null) && (dupProperties.size() > 0)) { //duplicates found, this should always be the case
                firstProperty = dupProperties.get(0);
                for (int counter = 1; counter < dupProperties.size(); counter++) { //I need to do a strict comparism between the properties before deciding whether or not to delete
                    secondProperty = dupProperties.get(counter);
                    if ((firstProperty.getPropertyId().equalsIgnoreCase(secondProperty.getPropertyId())) && (firstProperty.getPropertyNumber().equalsIgnoreCase(secondProperty.getPropertyNumber()))
                            && (firstProperty.getWardStreetId().equals(secondProperty.getWardStreetId())) && (firstProperty.getCapturedById().equals(secondProperty.getCapturedById()))) {
                        //this is a legit duplicate, we should delete now
                        landPropService.deletePropertyData(secondProperty.getId(), null);
                    }
                }
            }
        }

        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Duplicates have been cleared ", ""));
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    /**
     * @return the registeredUsers
     */
    public List<Logindetails> getRegisteredUsers() {
        return registeredUsers;
    }

    /**
     * @param registeredUsers the registeredUsers to set
     */
    public void setRegisteredUsers(List<Logindetails> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }

    /**
     * @return the wardProperties
     */
    public List<WardLandProperties> getWardProperties() {
        return wardProperties;
    }

    /**
     * @param wardProperties the wardProperties to set
     */
    public void setWardProperties(List<WardLandProperties> wardProperties) {
        this.wardProperties = wardProperties;
    }

    /**
     * @return the uniqueStreets
     */
    public List<String> getUniqueStreets() {
        return uniqueStreets;
    }

    /**
     * @param uniqueStreets the uniqueStreets to set
     */
    public void setUniqueStreets(List<String> uniqueStreets) {
        this.uniqueStreets = uniqueStreets;
    }

    /**
     * @return the selectedStreet
     */
    public WardStreets getSelectedStreet() {
        return selectedStreet;
    }

    /**
     * @param selectedStreet the selectedStreet to set
     */
    public void setSelectedStreet(WardStreets selectedStreet) {
        this.selectedStreet = selectedStreet;
    }

    /**
     * @return the batchSize
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * @param batchSize the batchSize to set
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * @return the fileUploads
     */
    public List<FileUploads> getFileUploads() {
        return fileUploads;
    }

    /**
     * @param fileUploads the fileUploads to set
     */
    public void setFileUploads(List<FileUploads> fileUploads) {
        this.fileUploads = fileUploads;
    }

    public Long getUnprocessedCount() {
        return unprocessedCount;
    }

    public void setUnprocessedCount(Long unprocessedCount) {
        this.unprocessedCount = unprocessedCount;
    }

    /**
     * @return the duplicateList
     */
    public List<String[]> getDuplicateList() {
        return duplicateList;
    }

    /**
     * @param duplicateList the duplicateList to set
     */
    public void setDuplicateList(List<String[]> duplicateList) {
        this.duplicateList = duplicateList;
    }

    /**
     * @return the selectedLoginUser
     */
    public Logindetails getSelectedLoginUser() {
        return selectedLoginUser;
    }

    /**
     * @param selectedLoginUser the selectedLoginUser to set
     */
    public void setSelectedLoginUser(Logindetails selectedLoginUser) {
        this.selectedLoginUser = selectedLoginUser;
    }

    /**
     * @return the selectedRole
     */
    public AuthenticationRoles getSelectedRole() {
        return selectedRole;
    }

    /**
     * @param selectedRole the selectedRole to set
     */
    public void setSelectedRole(AuthenticationRoles selectedRole) {
        this.selectedRole = selectedRole;
    }

    /**
     * @return the authenticationRoles
     */
    public List<AuthenticationRoles> getAuthenticationRoles() {
        return authenticationRoles;
    }

    /**
     * @param authenticationRoles the authenticationRoles to set
     */
    public void setAuthenticationRoles(List<AuthenticationRoles> authenticationRoles) {
        this.authenticationRoles = authenticationRoles;
    }

    public Long getLogindetailId() {
        return logindetailId;
    }

    public void setLogindetailId(Long logindetailId) {
        this.logindetailId = logindetailId;
    }

    /**
     * @return the registeredDevices
     */
    public List<RegisteredDevices> getRegisteredDevices() {
        return registeredDevices;
    }

    /**
     * @param registeredDevices the registeredDevices to set
     */
    public void setRegisteredDevices(List<RegisteredDevices> registeredDevices) {
        this.registeredDevices = registeredDevices;
    }

    /**
     * @return the registeredDevice
     */
    public RegisteredDevices getRegisteredDevice() {
        return registeredDevice;
    }

    /**
     * @param registeredDevice the registeredDevice to set
     */
    public void setRegisteredDevice(RegisteredDevices registeredDevice) {
        this.registeredDevice = registeredDevice;
    }

    /**
     * @return the allFieldOfficers
     */
    public List<Logindetails> getAllFieldOfficers() {
        return allFieldOfficers;
    }

    /**
     * @param allFieldOfficers the allFieldOfficers to set
     */
    public void setAllFieldOfficers(List<Logindetails> allFieldOfficers) {
        this.allFieldOfficers = allFieldOfficers;
    }

    /**
     * @return the selectedUser
     */
    public Logindetails getSelectedUser() {
        return selectedUser;
    }

    /**
     * @param selectedUser the selectedUser to set
     */
    public void setSelectedUser(Logindetails selectedUser) {
        this.selectedUser = selectedUser;
    }

    /**
     * @return the macAddress
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * @param macAddress the macAddress to set
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * @return the imeiNumber
     */
    public String getImeiNumber() {
        return imeiNumber;
    }

    /**
     * @param imeiNumber the imeiNumber to set
     */
    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

}
