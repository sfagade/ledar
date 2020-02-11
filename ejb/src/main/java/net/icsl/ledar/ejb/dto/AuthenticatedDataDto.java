/**
 *
 */
package net.icsl.ledar.ejb.dto;

import java.util.Date;
import java.util.List;

import net.icsl.ledar.ejb.model.Addresses;
import net.icsl.ledar.ejb.model.ContactInformations;
import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.model.GeographicalBoundaries;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.Organizations;
import net.icsl.ledar.ejb.model.PersonTitles;
import net.icsl.ledar.ejb.model.Religions;

/**
 * @author aojediran
 *
 */
public class AuthenticatedDataDto {

    protected Long id;

    protected Date created;

    protected Date modified;

    private String lastName;

    private String firstName;

    private String middleName;

    private Date dob;

    private Integer nextOfKin;

    private String maidenName;

    private String gender;

    private String maritalStatus;

    // private String createdBy;
    private Boolean updateRequired;

    private String stateOfOrigin;

    private String nationality;

    private Addresses addressId;

    private String personTitle;

    private String religion;
    private Long organizationId, senatorialDistrictId;
    private String organizationName;
    private String senatorialDistrict;

    private String serviceMessage;

    private Integer gsm;

    private String contactPhoneNumber;

    private String backupPhoneNumber;

    private String officeEmailAddress;

    private String personalEmailAddress;

    private String webAddress;

    private String fax;

    //  private String createdBy;
    private String username;

    private String pword;

    private boolean active;

    private Boolean loginStatus;

    private String activationKey;

    private Boolean activateStatus;

    private Date lastlogindate;

    private Boolean pwordResetRequired;

    private String resetKey;

    private String userNumber;

    private String lastLoginLatitude;

    private String lastLoginLongitude;

    private String lastLoginIp;

    private String registerDeviceId;

    private String role;

    private String roleDescription;

    public AuthenticatedDataDto() {
    }

    public AuthenticatedDataDto(Long id, Date created, Date modified, String lastName, String firstName, String middleName,
            Date dob, String gender, String maritalStatus, String personTitle, String serviceMessage,
            String contactPhoneNumber, String officeEmailAddress, String username, boolean active,
            Boolean pwordResetRequired, String userNumber, String lastLoginLatitude, String lastLoginLongitude,
            String lastLoginIp, String registerDeviceId, String role, String roleDescription) {
        super();
        this.id = id;
        this.created = created;
        this.modified = modified;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.dob = dob;
        this.gender = gender;
        this.maritalStatus = maritalStatus;
        this.personTitle = personTitle;
        this.serviceMessage = serviceMessage;
        this.contactPhoneNumber = contactPhoneNumber;
        this.officeEmailAddress = officeEmailAddress;
        this.username = username;
        this.active = active;
        this.pwordResetRequired = pwordResetRequired;
        this.userNumber = userNumber;
        this.lastLoginLatitude = lastLoginLatitude;
        this.lastLoginLongitude = lastLoginLongitude;
        this.lastLoginIp = lastLoginIp;
        this.registerDeviceId = registerDeviceId;
        this.role = role;
        this.roleDescription = roleDescription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Integer getNextOfKin() {
        return nextOfKin;
    }

    public void setNextOfKin(Integer nextOfKin) {
        this.nextOfKin = nextOfKin;
    }

    public String getMaidenName() {
        return maidenName;
    }

    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Boolean getUpdateRequired() {
        return updateRequired;
    }

    public void setUpdateRequired(Boolean updateRequired) {
        this.updateRequired = updateRequired;
    }

    public Addresses getAddressId() {
        return addressId;
    }

    public void setAddressId(Addresses addressId) {
        this.addressId = addressId;
    }

    public String getServiceMessage() {
        return serviceMessage;
    }

    public void setServiceMessage(String serviceMessage) {
        this.serviceMessage = serviceMessage;
    }

    public Integer getGsm() {
        return gsm;
    }

    public void setGsm(Integer gsm) {
        this.gsm = gsm;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getBackupPhoneNumber() {
        return backupPhoneNumber;
    }

    public void setBackupPhoneNumber(String backupPhoneNumber) {
        this.backupPhoneNumber = backupPhoneNumber;
    }

    public String getOfficeEmailAddress() {
        return officeEmailAddress;
    }

    public void setOfficeEmailAddress(String officeEmailAddress) {
        this.officeEmailAddress = officeEmailAddress;
    }

    public String getPersonalEmailAddress() {
        return personalEmailAddress;
    }

    public void setPersonalEmailAddress(String personalEmailAddress) {
        this.personalEmailAddress = personalEmailAddress;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPword() {
        return pword;
    }

    public void setPword(String pword) {
        this.pword = pword;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Boolean getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public Boolean getActivateStatus() {
        return activateStatus;
    }

    public void setActivateStatus(Boolean activateStatus) {
        this.activateStatus = activateStatus;
    }

    public Date getLastlogindate() {
        return lastlogindate;
    }

    public void setLastlogindate(Date lastlogindate) {
        this.lastlogindate = lastlogindate;
    }

    public Boolean getPwordResetRequired() {
        return pwordResetRequired;
    }

    public void setPwordResetRequired(Boolean pwordResetRequired) {
        this.pwordResetRequired = pwordResetRequired;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getLastLoginLatitude() {
        return lastLoginLatitude;
    }

    public void setLastLoginLatitude(String lastLoginLatitude) {
        this.lastLoginLatitude = lastLoginLatitude;
    }

    public String getLastLoginLongitude() {
        return lastLoginLongitude;
    }

    public void setLastLoginLongitude(String lastLoginLongitude) {
        this.lastLoginLongitude = lastLoginLongitude;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getRegisterDeviceId() {
        return registerDeviceId;
    }

    public void setRegisterDeviceId(String registerDeviceId) {
        this.registerDeviceId = registerDeviceId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

   // private String createdBy
    /**
     * @return the stateOfOrigin
     */
    public String getStateOfOrigin() {
        return stateOfOrigin;
    }

    /**
     * @param stateOfOrigin the stateOfOrigin to set
     */
    public void setStateOfOrigin(String stateOfOrigin) {
        this.stateOfOrigin = stateOfOrigin;
    }

    /**
     * @return the nationality
     */
    public String getNationality() {
        return nationality;
    }

    /**
     * @param nationality the nationality to set
     */
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    /**
     * @return the personTitle
     */
    public String getPersonTitle() {
        return personTitle;
    }

    /**
     * @param personTitle the personTitle to set
     */
    public void setPersonTitle(String personTitle) {
        this.personTitle = personTitle;
    }

    /**
     * @return the religion
     */
    public String getReligion() {
        return religion;
    }

    /**
     * @param religion the religion to set
     */
    public void setReligion(String religion) {
        this.religion = religion;
    }

    /**
     * @return the organizationId
     */
    public Long getOrganizationId() {
        return organizationId;
    }

    /**
     * @param organizationId the organizationId to set
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * @return the senatorialDistrictId
     */
    public Long getSenatorialDistrictId() {
        return senatorialDistrictId;
    }

    /**
     * @param senatorialDistrictId the senatorialDistrictId to set
     */
    public void setSenatorialDistrictId(Long senatorialDistrictId) {
        this.senatorialDistrictId = senatorialDistrictId;
    }

    /**
     * @return the organizationName
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * @param organizationName the organizationName to set
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * @return the senatorialDistrict
     */
    public String getSenatorialDistrict() {
        return senatorialDistrict;
    }

    /**
     * @param senatorialDistrict the senatorialDistrict to set
     */
    public void setSenatorialDistrict(String senatorialDistrict) {
        this.senatorialDistrict = senatorialDistrict;
    }
}
