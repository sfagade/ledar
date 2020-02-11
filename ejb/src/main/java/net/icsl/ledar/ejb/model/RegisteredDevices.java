package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 

/**
 * @author sfagade
 * @date Jan 28, 2016
 */
@Entity
@Table(name = "registered_devices", uniqueConstraints = @UniqueConstraint(columnNames = {"mac_address"}))
@AttributeOverride(name = "id", column = @Column(name = "registered_device_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "RegisteredDevices.findAll", query = "SELECT r FROM RegisteredDevices r")
    ,
    @NamedQuery(name = "RegisteredDevices.findByRegisteredDeviceId", query = "SELECT r FROM RegisteredDevices r WHERE r.id = :registeredDeviceId")
    ,
    @NamedQuery(name = "RegisteredDevices.findByDeviceName", query = "SELECT r FROM RegisteredDevices r WHERE r.deviceName = :deviceName")
    ,
    @NamedQuery(name = "RegisteredDevices.findByMacAddress", query = "SELECT r FROM RegisteredDevices r WHERE r.macAddress = :macAddress")
    ,
    @NamedQuery(name = "RegisteredDevices.findByImeiNumber", query = "SELECT r FROM RegisteredDevices r WHERE r.imeiNumber = :imeiNumber")})
public class RegisteredDevices extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "device_name")
    private String deviceName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "mac_address")
    private String macAddress;
    @Size(max = 25)
    @Column(name = "imei_number")
    private String imeiNumber;
    @Column(name = "os_version")
    private String osVersion;
    @Size(max = 1000)
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "is_enabled", columnDefinition = "tinyint(1) default 1")
    private Boolean isEnabled;
    @JoinColumn(name = "created_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails createdById;
    @JoinColumn(name = "device_owner_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails deviceOwnerById;
    @JoinColumn(name = "consultant_id", referencedColumnName = "organization_id")
    @ManyToOne
    private Organizations consultantId;
    @JoinColumn(name = "last_updated_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails lastUpdatedById;

    public RegisteredDevices() {
    }

    public RegisteredDevices(Long registeredDeviceId) {
        this.id = registeredDeviceId;
    }

    public RegisteredDevices(Long registeredDeviceId, String deviceName, String macAddress, Date created_) {
        this.id = registeredDeviceId;
        this.deviceName = deviceName;
        this.macAddress = macAddress;
        this.created = created_;
    }

    public RegisteredDevices(Long device_id, String deviceName, String macAddress, String imeiNumber, String remarks, Boolean isEnabled, Logindetails createdById, Logindetails deviceOwnerById,
            Organizations consultant, Date created_, Date modified_) {
        this.id = device_id;
        this.deviceName = deviceName;
        this.macAddress = macAddress;
        this.imeiNumber = imeiNumber;
        this.remarks = remarks;
        this.isEnabled = isEnabled;
        this.createdById = createdById;
        this.deviceOwnerById = deviceOwnerById;
        this.created = created_;
        this.modified = modified_;
        this.consultantId = consultant;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
        if (!(object instanceof RegisteredDevices)) {
            return false;
        }
        RegisteredDevices other = (RegisteredDevices) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.RegisteredDevices[ registeredDeviceId=" + id + " ]";
    }

    /**
     * @return the isEnabled
     */
    public Boolean getIsEnabled() {
        return isEnabled;
    }

    /**
     * @param isEnabled the isEnabled to set
     */
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * @return the createdById
     */
    public Logindetails getCreatedById() {
        return createdById;
    }

    /**
     * @param createdById the createdById to set
     */
    public void setCreatedById(Logindetails createdById) {
        this.createdById = createdById;
    }

    /**
     * @return the deviceOwnerById
     */
    public Logindetails getDeviceOwnerById() {
        return deviceOwnerById;
    }

    /**
     * @param deviceOwnerById the deviceOwnerById to set
     */
    public void setDeviceOwnerById(Logindetails deviceOwnerById) {
        this.deviceOwnerById = deviceOwnerById;
    }

    /**
     * @return the consultantId
     */
    public Organizations getConsultantId() {
        return consultantId;
    }

    /**
     * @param consultantId the consultantId to set
     */
    public void setConsultantId(Organizations consultantId) {
        this.consultantId = consultantId;
    }

    /**
     * @return the osVersion
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * @param osVersion the osVersion to set
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public Logindetails getLastUpdatedById() {
        return lastUpdatedById;
    }

    public void setLastUpdatedById(Logindetails lastUpdatedById) {
        this.lastUpdatedById = lastUpdatedById;
    }
}
