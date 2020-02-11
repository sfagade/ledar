package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
 

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "logindetails")
@AttributeOverride(name = "id", column = @Column(name = "logindetail_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "Logindetails.findAll", query = "SELECT l FROM Logindetails l"),
    @NamedQuery(name = "Logindetails.findByLogindetailId", query = "SELECT l FROM Logindetails l WHERE l.id = :logindetailId"),
    @NamedQuery(name = "Logindetails.findByUsername", query = "SELECT l FROM Logindetails l WHERE l.username = :username"),
    @NamedQuery(name = "Logindetails.findByActive", query = "SELECT l FROM Logindetails l WHERE l.active = :active"),
    @NamedQuery(name = "Logindetails.findByLoginStatus", query = "SELECT l FROM Logindetails l WHERE l.isLoggedIn = :loginStatus"),
    @NamedQuery(name = "Logindetails.findByLastlogindate", query = "SELECT l FROM Logindetails l WHERE l.lastlogindate = :lastlogindate")})
public class Logindetails extends IcslLedarModelBase implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fieldOfficerId")
    private Collection<StreetGaps> streetGapsCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "receptionistId")
    private List<Visitors> visitorsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "uploadedById")
    private List<BillPayments> billPaymentsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accessedById")
    private List<PropertyValuations> propertyValuationsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "createdById")
    private List<BareLands> bareLandsList;
    @OneToMany(mappedBy = "verifiedById")
    private List<BareLands> bareLandsList1;

    @OneToMany(mappedBy = "createdById")
    private Collection<WardStreets> wardStreetsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logindetailId")
    private List<UsersLastActivities> usersLastActivitiesList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "uploadedById")
    private List<FileUploads> fileUploadsList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logindetailId")
    private List<EnumeratorWards> enumeratorWardsList;

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "pword")
    private String pword;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private boolean active;
    @Column(name = "is_logged_in")
    private Boolean isLoggedIn;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "activationKey")
    private String activationKey;
    @Column(name = "activate_status")
    private Boolean activateStatus;
    @Column(name = "lastlogindate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastlogindate;
    @Column(name = "pword_reset_required")
    private Boolean pwordResetRequired;
    @Size(max = 35)
    @Column(name = "reset_key")
    private String resetKey;
    @Size(max = 35)
    @Column(name = "user_number")
    private String userNumber;
    @Size(max = 25)
    @Column(name = "last_login_latitude")
    private String lastLoginLatitude;
    @Size(max = 25)
    @Column(name = "last_login_longitude")
    private String lastLoginLongitude;
    @Column(name = "last_login_ip")
    private String lastLoginIp;
    @Column(name = "active_session_id")
    private String activeSessionId;
    @Size(max = 45)
    @NotNull
    @Column(name = "created_by")
    private String createdBy;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logindetailId")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<UserRoles> userRolesList;
    @OneToMany(mappedBy = "wardSupervisorById")
    private List<LcdaWards> lcdaWardsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "verifiedById")
    private List<WardStreets> wardStreetsList;
    @OneToMany(mappedBy = "lcdaChairmanId")
    private List<LocalCouncilDevArea> localCouncilDevAreaList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "capturedById")
    private List<WardLandProperties> wardLandPropertiesList;
    @OneToMany(mappedBy = "logindetailId")
    private List<Notifications> notificationsList;
    @OneToMany(mappedBy = "createdById")
    private List<Notifications> notificationsList1;
    @OneToMany(mappedBy = "actionById")
    private List<Notifications> notificationsList2;

    @OneToOne(cascade = CascadeType.ALL, optional = false, mappedBy = "logindetailId")
    private People person;
    @OneToOne(cascade = CascadeType.ALL, optional = false, mappedBy = "logindetailId")
    private UserRoles userRole;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "deviceOwnerById")
    private List<RegisteredDevices> registeredDeviceList;
    @OneToMany(mappedBy = "deliveryLogindetailId")
    private List<HistPrintedBills> histPrintedBillsList;

    public Logindetails() {
    }

    public Logindetails(Long logindetailId) {
        this.id = logindetailId;
    }

    public Logindetails(Long logindetailId, String username, String pword, boolean active, String activationKey) {
        this.id = logindetailId;
        this.username = username;
        this.pword = pword;
        this.active = active;
        this.activationKey = activationKey;
    }

    public Logindetails(Long user_id, String username, boolean active, String userNumber, People person, Date created_) {
        this.username = username;
        this.id = user_id;
        this.created = created_;
        this.active = active;
        this.userNumber = userNumber;
        this.person = person;
    }

    public Logindetails(Long user_id, String username_, boolean is_active, People person_, Date created_) {

        this.id = user_id;
        this.username = username_;
        this.active = is_active;
        this.person = person_;
        this.created = created_;
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

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public UserRoles getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRoles userRole) {
        this.userRole = userRole;
    }

     
    public List<UserRoles> getUserRolesList() {
        return userRolesList;
    }

    public void setUserRolesList(List<UserRoles> userRolesList) {
        this.userRolesList = userRolesList;
    }

     
    public List<LcdaWards> getLcdaWardsList() {
        return lcdaWardsList;
    }

    public void setLcdaWardsList(List<LcdaWards> lcdaWardsList) {
        this.lcdaWardsList = lcdaWardsList;
    }

     
    public List<WardStreets> getWardStreetsList() {
        return wardStreetsList;
    }

    public void setWardStreetsList(List<WardStreets> wardStreetsList) {
        this.wardStreetsList = wardStreetsList;
    }

     
    public List<LocalCouncilDevArea> getLocalCouncilDevAreaList() {
        return localCouncilDevAreaList;
    }

    public void setLocalCouncilDevAreaList(List<LocalCouncilDevArea> localCouncilDevAreaList) {
        this.localCouncilDevAreaList = localCouncilDevAreaList;
    }

     
    public List<WardLandProperties> getWardLandPropertiesList() {
        return wardLandPropertiesList;
    }

    public void setWardLandPropertiesList(List<WardLandProperties> wardLandPropertiesList) {
        this.wardLandPropertiesList = wardLandPropertiesList;
    }

     
    public List<Notifications> getNotificationsList() {
        return notificationsList;
    }

    public void setNotificationsList(List<Notifications> notificationsList) {
        this.notificationsList = notificationsList;
    }

     
    public List<Notifications> getNotificationsList1() {
        return notificationsList1;
    }

    public void setNotificationsList1(List<Notifications> notificationsList1) {
        this.notificationsList1 = notificationsList1;
    }

     
    public List<Notifications> getNotificationsList2() {
        return notificationsList2;
    }

    public void setNotificationsList2(List<Notifications> notificationsList2) {
        this.notificationsList2 = notificationsList2;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Logindetails)) {
            return false;
        }
        Logindetails other = (Logindetails) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.Logindetails[ logindetailId=" + id + " ]";
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
     * @return the userNumber
     */
    public String getUserNumber() {
        return userNumber;
    }

    /**
     * @param userNumber the userNumber to set
     */
    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

     
    public List<EnumeratorWards> getEnumeratorWardsList() {
        return enumeratorWardsList;
    }

    public void setEnumeratorWardsList(List<EnumeratorWards> enumeratorWardsList) {
        this.enumeratorWardsList = enumeratorWardsList;
    }

     
    public List<FileUploads> getFileUploadsList() {
        return fileUploadsList;
    }

    public void setFileUploadsList(List<FileUploads> fileUploadsList) {
        this.fileUploadsList = fileUploadsList;
    }

    public static Comparator<Logindetails> logindetailsUsernameComparator = new Comparator<Logindetails>() {

        @Override
        public int compare(Logindetails authRole1, Logindetails authRole2) {

            String auth_name1 = authRole1.getUsername().toUpperCase();
            String auth_name2 = authRole2.getUsername().toUpperCase();

            return auth_name1.compareTo(auth_name2);

        }

    };

     
    public List<UsersLastActivities> getUsersLastActivitiesList() {
        return usersLastActivitiesList;
    }

    public void setUsersLastActivitiesList(List<UsersLastActivities> usersLastActivitiesList) {
        this.usersLastActivitiesList = usersLastActivitiesList;
    }

    /**
     * @return the wardStreetsCollection
     */
    public Collection<WardStreets> getWardStreetsCollection() {
        return wardStreetsCollection;
    }

    /**
     * @param wardStreetsCollection the wardStreetsCollection to set
     */
    public void setWardStreetsCollection(Collection<WardStreets> wardStreetsCollection) {
        this.wardStreetsCollection = wardStreetsCollection;
    }

     
    public List<BareLands> getBareLandsList() {
        return bareLandsList;
    }

    public void setBareLandsList(List<BareLands> bareLandsList) {
        this.bareLandsList = bareLandsList;
    }

     
    public List<BareLands> getBareLandsList1() {
        return bareLandsList1;
    }

    public void setBareLandsList1(List<BareLands> bareLandsList1) {
        this.bareLandsList1 = bareLandsList1;
    }

     
    public List<PropertyValuations> getPropertyValuationsList() {
        return propertyValuationsList;
    }

    public void setPropertyValuationsList(List<PropertyValuations> propertyValuationsList) {
        this.propertyValuationsList = propertyValuationsList;
    }

     
    public List<BillPayments> getBillPaymentsList() {
        return billPaymentsList;
    }

    public void setBillPaymentsList(List<BillPayments> billPaymentsList) {
        this.billPaymentsList = billPaymentsList;
    }

     
    public List<Visitors> getVisitorsList() {
        return visitorsList;
    }

    public void setVisitorsList(List<Visitors> visitorsList) {
        this.visitorsList = visitorsList;
    }

    /**
     * @return the isLoggedIn
     */
    public Boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    /**
     * @param isLoggedIn the isLoggedIn to set
     */
    public void setIsLoggedIn(Boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    /**
     * @return the activeSessionId
     */
    public String getActiveSessionId() {
        return activeSessionId;
    }

    /**
     * @param activeSessionId the activeSessionId to set
     */
    public void setActiveSessionId(String activeSessionId) {
        this.activeSessionId = activeSessionId;
    }

    /**
     * @return the registeredDeviceList
     */
    public List<RegisteredDevices> getRegisteredDeviceList() {
        return registeredDeviceList;
    }

    /**
     * @param registeredDeviceList the registeredDeviceList to set
     */
    public void setRegisteredDeviceList(List<RegisteredDevices> registeredDeviceList) {
        this.registeredDeviceList = registeredDeviceList;
    }

     
    public List<HistPrintedBills> getHistPrintedBillsList() {
        return histPrintedBillsList;
    }

    public void setHistPrintedBillsList(List<HistPrintedBills> histPrintedBillsList) {
        this.histPrintedBillsList = histPrintedBillsList;
    }

     
    public Collection<StreetGaps> getStreetGapsCollection() {
        return streetGapsCollection;
    }

    public void setStreetGapsCollection(Collection<StreetGaps> streetGapsCollection) {
        this.streetGapsCollection = streetGapsCollection;
    }
}
