
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
 

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "notification_types")
@AttributeOverride(name = "id", column = @Column(name = "notification_type_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "NotificationTypes.findAll", query = "SELECT n FROM NotificationTypes n"),
    @NamedQuery(name = "NotificationTypes.findByNotificationTypeId", query = "SELECT n FROM NotificationTypes n WHERE n.id = :notificationTypeId"),
    @NamedQuery(name = "NotificationTypes.findByNotificationTypeName", query = "SELECT n FROM NotificationTypes n WHERE n.notificationTypeName = :notificationTypeName")})
public class NotificationTypes extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 55)
    @Column(name = "notification_type_name")
    private String notificationTypeName;
    @Size(max = 55)
    @Column(name = "notification_type_entity")
    private String notificationTypeEntity;
    @Size(max = 500)
    @Column(name = "remarks")
    private String remarks;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "notificationTypeId")
    private List<Notifications> notificationsList;

    public NotificationTypes() {
    }

    public NotificationTypes(Long notificationTypeId) {
        this.id = notificationTypeId;
    }

    public NotificationTypes(Long notificationTypeId, String notificationTypeName) {
        this.id = notificationTypeId;
        this.notificationTypeName = notificationTypeName;
    }

    public String getNotificationTypeName() {
        return notificationTypeName;
    }

    public void setNotificationTypeName(String notificationTypeName) {
        this.notificationTypeName = notificationTypeName;
    }

    public String getNotificationTypeEntity() {
        return notificationTypeEntity;
    }

    public void setNotificationTypeEntity(String notificationTypeEntity) {
        this.notificationTypeEntity = notificationTypeEntity;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

     
    public List<Notifications> getNotificationsList() {
        return notificationsList;
    }

    public void setNotificationsList(List<Notifications> notificationsList) {
        this.notificationsList = notificationsList;
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
        if (!(object instanceof NotificationTypes)) {
            return false;
        }
        NotificationTypes other = (NotificationTypes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.NotificationTypes[ notificationTypeId=" + id + " ]";
    }

}
