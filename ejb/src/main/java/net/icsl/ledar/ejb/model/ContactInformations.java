
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
 

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "contact_informations")
@AttributeOverride(name = "id", column = @Column(name = "contact_information_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "ContactInformations.findAll", query = "SELECT c FROM ContactInformations c"),
    @NamedQuery(name = "ContactInformations.findByContactInformationId", query = "SELECT c FROM ContactInformations c WHERE c.id = :contactInformationId"),
    @NamedQuery(name = "ContactInformations.findByGsm", query = "SELECT c FROM ContactInformations c WHERE c.gsm = :gsm"),
    @NamedQuery(name = "ContactInformations.findByContactPhoneNumber", query = "SELECT c FROM ContactInformations c WHERE c.contactPhoneNumber = :contactPhoneNumber"),
    @NamedQuery(name = "ContactInformations.findByOfficeEmailAddress", query = "SELECT c FROM ContactInformations c WHERE c.officeEmailAddress = :officeEmailAddress")})
public class ContactInformations extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Column(name = "gsm")
    private Integer gsm;
    @Size(max = 20)
    @Column(name = "contact_phone_number")
    private String contactPhoneNumber;
    @Size(max = 20)
    @Column(name = "backup_phone_number")
    private String backupPhoneNumber;
    @Size(max = 45)
    @Column(name = "office_email_address")
    private String officeEmailAddress;
    @Size(max = 45)
    @Column(name = "personal_email_address")
    private String personalEmailAddress;
    @Size(max = 60)
    @Column(name = "web_address")
    private String webAddress;
    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field contains phone or fax number consider using this annotation to enforce field validation
    @Size(max = 15)
    @Column(name = "fax")
    private String fax;
    @Size(max = 30)
    @Column(name = "created_by")
    private String createdBy;
    
    @JoinColumn(name = "address_id", referencedColumnName = "address_id")
    @ManyToOne
    private Addresses addressId;
    @JoinColumn(name = "person_id", referencedColumnName = "person_id")
    @ManyToOne(optional = false)
    private People personId;

    public ContactInformations() {
    }

    public ContactInformations(Long contactInformationId) {
        this.id = contactInformationId;
    }

    public ContactInformations(Long contactInformationId, String email, People person, Date created) {
        this.id = contactInformationId;
        this.officeEmailAddress = email;
        this.created = created;
        this.personId = person;
    }

    /**
     * this constructor is used for creating new employee
     * @param contactInformationId
     * @param email
     * @param person
     * @param phone_number
     * @param addr
     * @param personal_email
     * @param created
     */
    public ContactInformations(Long contactInformationId, String email, People person, String phone_number, Addresses addr, String personal_email,Date created) {
        this.id = contactInformationId;
        this.officeEmailAddress = email;
        this.created = created;
        this.personId = person;
        this.contactPhoneNumber = phone_number;
        this.addressId = addr;
        this.personalEmailAddress = personal_email;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Addresses getAddressId() {
        return addressId;
    }

    public void setAddressId(Addresses addressId) {
        this.addressId = addressId;
    }

    public People getPersonId() {
        return personId;
    }

    public void setPersonId(People personId) {
        this.personId = personId;
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
        if (!(object instanceof ContactInformations)) {
            return false;
        }
        ContactInformations other = (ContactInformations) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.ContactInformations[ contactInformationId=" + id + " ]";
    }

}
