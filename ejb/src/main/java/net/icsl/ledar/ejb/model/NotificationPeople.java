/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 

/**
 *
 * @author sfagade
 * @created Expression created is undefined on line 13, column 15 in
 * Templates/Classes/Class.java.
 */
@Entity
@Table(name = "notification_people")
@AttributeOverride(name = "id", column = @Column(name = "notification_person_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "NotificationPeople.findAll", query = "SELECT n FROM NotificationPeople n"),
    @NamedQuery(name = "NotificationPeople.findByNotificationPersonId", query = "SELECT n FROM NotificationPeople n WHERE n.id = :notificationPersonId"),
    @NamedQuery(name = "NotificationPeople.findByEmailAddress", query = "SELECT n FROM NotificationPeople n WHERE n.emailAddress = :emailAddress"),
    @NamedQuery(name = "NotificationPeople.findByPhoneNumber", query = "SELECT n FROM NotificationPeople n WHERE n.phoneNumber = :phoneNumber"),
    @NamedQuery(name = "NotificationPeople.findByPersonRole", query = "SELECT n FROM NotificationPeople n WHERE n.personRole = :personRole")})
public class NotificationPeople extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "email_address")
    private String emailAddress;
    @Size(max = 20)
    @Column(name = "phone_number")
    private String phoneNumber;
    @Size(max = 100)
    @Column(name = "backup_email_address")
    private String backupEmailAddress;
    @Size(max = 20)
    @Column(name = "backup_phone_number")
    private String backupPhoneNumber;
    @Size(max = 25)
    @Column(name = "person_role")
    private String personRole;
    @Size(max = 600)
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "is_enabled", columnDefinition = "tinyint(1) default 1")
    private Boolean isEnabled;

    @JoinColumn(name = "email_notification_id", referencedColumnName = "email_notification_id")
    @ManyToOne(optional = false)
    private EmailNotificationConfigs emailNotificationId;
    @JoinColumn(name = "person_title_id", referencedColumnName = "person_title_id")
    @ManyToOne
    private PersonTitles personTitleId;
    @JoinColumn(name = "consultant_group_id", referencedColumnName = "organization_id")
    @ManyToOne
    private Organizations consultantGroupId;

    public NotificationPeople() {
    }

    public NotificationPeople(Long notificationPersonId) {
        this.id = notificationPersonId;
    }

    public NotificationPeople(Long notificationPersonId, String emailAddress, Date created_) {
        this.id = notificationPersonId;
        this.emailAddress = emailAddress;
        this.created = created_;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBackupEmailAddress() {
        return backupEmailAddress;
    }

    public void setBackupEmailAddress(String backupEmailAddress) {
        this.backupEmailAddress = backupEmailAddress;
    }

    public String getBackupPhoneNumber() {
        return backupPhoneNumber;
    }

    public void setBackupPhoneNumber(String backupPhoneNumber) {
        this.backupPhoneNumber = backupPhoneNumber;
    }

    public String getPersonRole() {
        return personRole;
    }

    public void setPersonRole(String personRole) {
        this.personRole = personRole;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public EmailNotificationConfigs getEmailNotificationId() {
        return emailNotificationId;
    }

    public void setEmailNotificationId(EmailNotificationConfigs emailNotificationId) {
        this.emailNotificationId = emailNotificationId;
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
        if (!(object instanceof NotificationPeople)) {
            return false;
        }
        NotificationPeople other = (NotificationPeople) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.NotificationPeople[ notificationPersonId=" + id + " ]";
    }

    /**
     * @return the personTitleId
     */
    public PersonTitles getPersonTitleId() {
        return personTitleId;
    }

    /**
     * @param personTitleId the personTitleId to set
     */
    public void setPersonTitleId(PersonTitles personTitleId) {
        this.personTitleId = personTitleId;
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
     * @return the consultantGroupId
     */
    public Organizations getConsultantGroupId() {
        return consultantGroupId;
    }

    /**
     * @param consultantGroupId the consultantGroupId to set
     */
    public void setConsultantGroupId(Organizations consultantGroupId) {
        this.consultantGroupId = consultantGroupId;
    }

}
