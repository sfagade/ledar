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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 

/**
 * @author sfagade
 * @date Feb 23, 2016
 */
@Entity
@Table(name = "users_last_activities")
@AttributeOverride(name = "id", column = @Column(name = "user_last_activity_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "UsersLastActivities.findAll", query = "SELECT u FROM UsersLastActivities u"),
    @NamedQuery(name = "UsersLastActivities.findByUserLastActivityId", query = "SELECT u FROM UsersLastActivities u WHERE u.id = :userLastActivityId"),
    @NamedQuery(name = "UsersLastActivities.findByActivitiy", query = "SELECT u FROM UsersLastActivities u WHERE u.activitiy = :activitiy"),
    @NamedQuery(name = "UsersLastActivities.findByActivitiyTime", query = "SELECT u FROM UsersLastActivities u WHERE u.activitiyTime = :activitiyTime")})
public class UsersLastActivities extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "activitiy")
    private String activitiy;
    @Basic(optional = false)
    @NotNull
    @Column(name = "activitiy_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date activitiyTime;
    @Column(name = "description")
    private String description;
    @Size(max = 18)
    @Column(name = "ip_address")
    private String ipAddress;
    @Size(max = 25)
    @Column(name = "longitude")
    private String longitude;
    @Size(max = 25)
    @Column(name = "latitude")
    private String latitude;
    @Size(max = 25)
    @Column(name = "client")
    private String client;
    @Size(max = 25)
    @Column(name = "entity_name")
    private String entityName;
    @Column(name = "entity_id")
    private Long entityId;

    @JoinColumn(name = "logindetail_id", referencedColumnName = "logindetail_id")
    @ManyToOne(optional = false)
    private Logindetails logindetailId;

    public UsersLastActivities() {
    }

    public UsersLastActivities(Long userLastActivityId) {
        this.id = userLastActivityId;
    }

    public UsersLastActivities(Long userLastActivityId, String activitiy, Date activitiyTime, Logindetails logindetailId, Date created) {
        this.id = userLastActivityId;
        this.activitiy = activitiy;
        this.activitiyTime = activitiyTime;
        this.logindetailId = logindetailId;
        this.created = created;
    }

    public UsersLastActivities(Long activity_id, String activitiy, Date activitiyTime, String description, String ipAddress, String longitude, String latitude, String client, Logindetails logindetailId,
            Date created_, Date modified_) {
        this.activitiy = activitiy;
        this.activitiyTime = activitiyTime;
        this.description = description;
        this.ipAddress = ipAddress;
        this.longitude = longitude;
        this.latitude = latitude;
        this.client = client;
        this.logindetailId = logindetailId;
        this.id = activity_id;
        this.created = created_;
        this.modified = modified_;
    }

    public String getActivitiy() {
        return activitiy;
    }

    public void setActivitiy(String activitiy) {
        this.activitiy = activitiy;
    }

    public Date getActivitiyTime() {
        return activitiyTime;
    }

    public void setActivitiyTime(Date activitiyTime) {
        this.activitiyTime = activitiyTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Logindetails getLogindetailId() {
        return logindetailId;
    }

    public void setLogindetailId(Logindetails logindetailId) {
        this.logindetailId = logindetailId;
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
        if (!(object instanceof UsersLastActivities)) {
            return false;
        }
        UsersLastActivities other = (UsersLastActivities) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.UsersLastActivities[ userLastActivityId=" + id + " ]";
    }

    /**
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * @return the longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the client
     */
    public String getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName the entityName to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return the entityId
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * @param entityId the entityId to set
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

}
