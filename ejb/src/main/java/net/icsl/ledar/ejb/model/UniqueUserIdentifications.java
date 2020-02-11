package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 

/**
 * @author sfagade
 * @date Jan 27, 2016
 */
@Entity
@Table(name = "unique_user_identifications")
@AttributeOverride(name = "id", column = @Column(name = "user_identification_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "UniqueUserIdentifications.findAll", query = "SELECT u FROM UniqueUserIdentifications u")
    ,
    @NamedQuery(name = "UniqueUserIdentifications.findByUserIdentificationId", query = "SELECT u FROM UniqueUserIdentifications u WHERE u.id = :userIdentificationId")
    ,
    @NamedQuery(name = "UniqueUserIdentifications.findByLastCustomerNumber", query = "SELECT u FROM UniqueUserIdentifications u WHERE u.lastCustomerNumber = :lastCustomerNumber")
    ,
    @NamedQuery(name = "UniqueUserIdentifications.findByLastEmployeeNumber", query = "SELECT u FROM UniqueUserIdentifications u WHERE u.lastEmployeeNumber = :lastEmployeeNumber")})
public class UniqueUserIdentifications extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Column(name = "last_customer_number")
    private long lastCustomerNumber;
    @Basic(optional = false)
    @NotNull
    @Column(name = "last_employee_number")
    private long lastEmployeeNumber;
    @Column(name = "last_complaint_number")
    private long lastComplaintNumber;
    @Size(max = 500)
    @Column(name = "remarks")
    private String remarks;

    public UniqueUserIdentifications() {
    }

    public UniqueUserIdentifications(Long userIdentificationId) {
        this.id = userIdentificationId;
    }

    public UniqueUserIdentifications(Long userIdentificationId, long lastCustomerNumber, long lastEmployeeNumber, long complntNumber, Date created) {
        this.id = userIdentificationId;
        this.lastCustomerNumber = lastCustomerNumber;
        this.lastEmployeeNumber = lastEmployeeNumber;
        this.created = created;
        this.lastComplaintNumber = complntNumber;
    }

    public long getLastCustomerNumber() {
        return lastCustomerNumber;
    }

    public void setLastCustomerNumber(long lastCustomerNumber) {
        this.lastCustomerNumber = lastCustomerNumber;
    }

    public long getLastEmployeeNumber() {
        return lastEmployeeNumber;
    }

    public void setLastEmployeeNumber(long lastEmployeeNumber) {
        this.lastEmployeeNumber = lastEmployeeNumber;
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
        if (!(object instanceof UniqueUserIdentifications)) {
            return false;
        }
        UniqueUserIdentifications other = (UniqueUserIdentifications) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.UniqueUserIdentifications[ userIdentificationId=" + id + " ]";
    }

    public long getLastComplaintNumber() {
        return lastComplaintNumber;
    }

    public void setLastComplaintNumber(long lastComplaintNumber) {
        this.lastComplaintNumber = lastComplaintNumber;
    }

}
