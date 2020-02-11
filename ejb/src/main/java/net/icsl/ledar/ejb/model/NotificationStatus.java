
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
 

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "notification_status")
@AttributeOverride(name = "id", column = @Column(name = "notification_status_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "NotificationStatus.findAll", query = "SELECT n FROM NotificationStatus n"),
    @NamedQuery(name = "NotificationStatus.findByNotificationStatusId", query = "SELECT n FROM NotificationStatus n WHERE n.id = :notificationStatusId"),
    @NamedQuery(name = "NotificationStatus.findByStatusName", query = "SELECT n FROM NotificationStatus n WHERE n.statusName = :statusName")})
public class NotificationStatus extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "status_name")
    private String statusName;
    @Size(max = 500)
    @Column(name = "status_description")
    private String statusDescription;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "notificationStatusId")
    private List<Notifications> notificationsList;

    public NotificationStatus() {
    }

    public NotificationStatus(Long notificationStatusId) {
        this.id = notificationStatusId;
    }

    public NotificationStatus(Long notificationStatusId, String statusName) {
        this.id = notificationStatusId;
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
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
        if (!(object instanceof NotificationStatus)) {
            return false;
        }
        NotificationStatus other = (NotificationStatus) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.NotificationStatus[ notificationStatusId=" + id + " ]";
    }

}
