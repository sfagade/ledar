package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
 

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "notifications")
@AttributeOverride(name = "id", column = @Column(name = "notification_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "Notifications.findAll", query = "SELECT n FROM Notifications n"),
    @NamedQuery(name = "Notifications.findByNotificationId", query = "SELECT n FROM Notifications n WHERE n.id = :notificationId"),
    @NamedQuery(name = "Notifications.findByBriefMessage", query = "SELECT n FROM Notifications n WHERE n.briefMessage = :briefMessage")})
public class Notifications extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "action_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actionDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "brief_message")
    private String briefMessage;
    @Size(max = 1500)
    @Column(name = "full_message")
    private String fullMessage;
    @Column(name = "entity_id")
    private Long entityId;
    @Size(max = 1000)
    @Column(name = "action_remark")
    private String actionRemark;
    @Column(name = "notification_role")
    private Long notificationRole;
    @Size(max = 15)
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "is_deleted", columnDefinition = "tinyint(1) default 0")
    private Boolean isDeleted;
    @JoinColumn(name = "logindetail_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails logindetailId;
    @JoinColumn(name = "created_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails createdById;
    @JoinColumn(name = "action_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails actionById;
    @OneToMany(mappedBy = "parentNotificationId")
    private List<Notifications> notificationsList;
    @JoinColumn(name = "parent_notification_id", referencedColumnName = "notification_id")
    @ManyToOne
    private Notifications parentNotificationId;
    @JoinColumn(name = "notification_status_id", referencedColumnName = "notification_status_id")
    @ManyToOne(optional = false)
    private NotificationStatus notificationStatusId;
    @JoinColumn(name = "notification_type_id", referencedColumnName = "notification_type_id")
    @ManyToOne(optional = false)
    private NotificationTypes notificationTypeId;

    @Transient
    private String notifiType, notifiStatus;

    public Notifications() {
    }

    public Notifications(Long notificationId) {
        this.id = notificationId;
    }

    public Notifications(Long notificationId, String briefMessage) {
        this.id = notificationId;
        this.briefMessage = briefMessage;
    }

    public Notifications(Long notification_id, NotificationTypes notiType, NotificationStatus notiStatus, String brief_msg, Long entityId, Long role_id, Notifications parent) {

        this.notificationTypeId = notiType;
        this.notificationStatusId = notiStatus;
        this.briefMessage = brief_msg;
        this.parentNotificationId = parent;
        this.entityId = entityId;
        this.notificationRole = role_id;
        this.id = notification_id;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public String getBriefMessage() {
        return briefMessage;
    }

    public void setBriefMessage(String briefMessage) {
        this.briefMessage = briefMessage;
    }

    public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getFullMessage() {
        return fullMessage;
    }

    public void setFullMessage(String fullMessage) {
        this.fullMessage = fullMessage;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getActionRemark() {
        return actionRemark;
    }

    public void setActionRemark(String actionRemark) {
        this.actionRemark = actionRemark;
    }

    public Long getNotificationRole() {
        return notificationRole;
    }

    public void setNotificationRole(Long notificationRole) {
        this.notificationRole = notificationRole;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Logindetails getLogindetailId() {
        return logindetailId;
    }

    public void setLogindetailId(Logindetails logindetailId) {
        this.logindetailId = logindetailId;
    }

    public Logindetails getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Logindetails createdById) {
        this.createdById = createdById;
    }

    public Logindetails getActionById() {
        return actionById;
    }

    public void setActionById(Logindetails actionById) {
        this.actionById = actionById;
    }

     
    public List<Notifications> getNotificationsList() {
        return notificationsList;
    }

    public void setNotificationsList(List<Notifications> notificationsList) {
        this.notificationsList = notificationsList;
    }

    public Notifications getParentNotificationId() {
        return parentNotificationId;
    }

    public void setParentNotificationId(Notifications parentNotificationId) {
        this.parentNotificationId = parentNotificationId;
    }

    public NotificationStatus getNotificationStatusId() {
        return notificationStatusId;
    }

    public void setNotificationStatusId(NotificationStatus notificationStatusId) {
        this.notificationStatusId = notificationStatusId;
    }

    public NotificationTypes getNotificationTypeId() {
        return notificationTypeId;
    }

    public void setNotificationTypeId(NotificationTypes notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
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
        if (!(object instanceof Notifications)) {
            return false;
        }
        Notifications other = (Notifications) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.Notifications[ notificationId=" + id + " ]";
    }

    /**
     * @return the notifiType
     */
    public String getNotifiType() {
        return notifiType;
    }

    /**
     * @param notifiType the notifiType to set
     */
    public void setNotifiType(String notifiType) {
        this.notifiType = notifiType;
    }

    /**
     * @return the notifiStatus
     */
    public String getNotifiStatus() {
        return notifiStatus;
    }

    /**
     * @param notifiStatus the notifiStatus to set
     */
    public void setNotifiStatus(String notifiStatus) {
        this.notifiStatus = notifiStatus;
    }

}
