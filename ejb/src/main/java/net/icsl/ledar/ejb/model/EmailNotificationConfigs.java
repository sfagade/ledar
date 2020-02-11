/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
 

/**
 *
 * @author sfagade
 * @created Expression created is undefined on line 13, column 15 in Templates/Classes/Class.java.
 */
@Entity
@Table(name = "email_notification_configs")
@AttributeOverride(name = "id", column = @Column(name = "email_notification_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "EmailNotificationConfigs.findAll", query = "SELECT e FROM EmailNotificationConfigs e"),
    @NamedQuery(name = "EmailNotificationConfigs.findByEmailNotificationId", query = "SELECT e FROM EmailNotificationConfigs e WHERE e.id = :emailNotificationId"),
    @NamedQuery(name = "EmailNotificationConfigs.findByActionName", query = "SELECT e FROM EmailNotificationConfigs e WHERE e.actionName = :actionName"),
    @NamedQuery(name = "EmailNotificationConfigs.findByEmailType", query = "SELECT e FROM EmailNotificationConfigs e WHERE e.emailType = :emailType")})
public class EmailNotificationConfigs extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "action_name")
    private String actionName;
    @Lob
    @Size(max = 16777215)
    @Column(name = "email_message")
    private String emailMessage;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "email_type")
    private String emailType;
    @Column(name = "has_attachment")
    private Boolean hasAttachment;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "emailNotificationId")
    private List<NotificationPeople> notificationPeopleList;

    public EmailNotificationConfigs() {
    }

    public EmailNotificationConfigs(Long emailNotificationId) {
        this.id = emailNotificationId;
    }

    public EmailNotificationConfigs(Long emailNotificationId, String actionName, String emailType, Date created_) {
        this.id = emailNotificationId;
        this.actionName = actionName;
        this.emailType = emailType;
        this.created = created_;
                 
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(String emailMessage) {
        this.emailMessage = emailMessage;
    }

    public String getEmailType() {
        return emailType;
    }

    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }

    public Boolean getHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(Boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

     
    public List<NotificationPeople> getNotificationPeopleList() {
        return notificationPeopleList;
    }

    public void setNotificationPeopleList(List<NotificationPeople> notificationPeopleList) {
        this.notificationPeopleList = notificationPeopleList;
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
        if (!(object instanceof EmailNotificationConfigs)) {
            return false;
        }
        EmailNotificationConfigs other = (EmailNotificationConfigs) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.EmailNotificationConfigs[ emailNotificationId=" + id + " ]";
    }

}
