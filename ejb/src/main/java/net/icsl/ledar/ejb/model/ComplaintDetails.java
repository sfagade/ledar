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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 

/**
 *
 * @author sfagade
 * @created Expression created is undefined on line 13, column 15 in
 * Templates/Classes/Class.java.
 */
@Entity
@Table(name = "complaint_details")
@AttributeOverride(name = "id", column = @Column(name = "complaint_detail_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "ComplaintDetails.findAll", query = "SELECT c FROM ComplaintDetails c")
    ,
    @NamedQuery(name = "ComplaintDetails.findByComplaintDetailId", query = "SELECT c FROM ComplaintDetails c WHERE c.id = :complaintDetailId")})
public class ComplaintDetails extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "compaint")
    private String compaint;
    @Lob
    @Size(max = 65535)
    @Column(name = "recommendation")
    private String recommendation;

    @JoinColumn(name = "property_complaint_id", referencedColumnName = "property_complaint_id")
    @ManyToOne(optional = false)
    private PropertyComplaints propertyComplaintId;
    @JoinColumn(name = "complaint_status_id", referencedColumnName = "complaint_status_id")
    @ManyToOne(optional = false)
    private ComplaintStatus complaintStatusId;
    @JoinColumn(name = "complaint_type_id", referencedColumnName = "complaint_type_id")
    @ManyToOne(optional = false)
    private ComplaintTypes complaintTypeId;
    @JoinColumn(name = "complaint_handler_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails complaintHandlerId;
    @JoinColumn(name = "complaint_unit_id", referencedColumnName = "authentication_role_id")
    @ManyToOne
    private AuthenticationRoles complaintUnitId;
    @Column(name = "handle_date")
    @Temporal(TemporalType.DATE)
    private Date handleDate;

    public ComplaintDetails() {
    }

    public ComplaintDetails(Long complaintDetailId) {
        this.id = complaintDetailId;
    }

    public ComplaintDetails(Long complaintDetailId, String compaint) {
        this.id = complaintDetailId;
        this.compaint = compaint;
    }

    public String getCompaint() {
        return compaint;
    }

    public void setCompaint(String compaint) {
        this.compaint = compaint;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public PropertyComplaints getPropertyComplaintId() {
        return propertyComplaintId;
    }

    public void setPropertyComplaintId(PropertyComplaints propertyComplaintId) {
        this.propertyComplaintId = propertyComplaintId;
    }

    public ComplaintStatus getComplaintStatusId() {
        return complaintStatusId;
    }

    public void setComplaintStatusId(ComplaintStatus complaintStatusId) {
        this.complaintStatusId = complaintStatusId;
    }

    public ComplaintTypes getComplaintTypeId() {
        return complaintTypeId;
    }

    public void setComplaintTypeId(ComplaintTypes complaintTypeId) {
        this.complaintTypeId = complaintTypeId;
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
        if (!(object instanceof ComplaintDetails)) {
            return false;
        }
        ComplaintDetails other = (ComplaintDetails) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.ComplaintDetails[ complaintDetailId=" + id + " ]";
    }

    public Logindetails getComplaintHandlerId() {
        return complaintHandlerId;
    }

    public void setComplaintHandlerId(Logindetails complaintHandlerId) {
        this.complaintHandlerId = complaintHandlerId;
    }

    public AuthenticationRoles getComplaintUnitId() {
        return complaintUnitId;
    }

    public void setComplaintUnitId(AuthenticationRoles complaintUnitId) {
        this.complaintUnitId = complaintUnitId;
    }

    public Date getHandleDate() {
        return handleDate;
    }

    public void setHandleDate(Date handleDate) {
        this.handleDate = handleDate;
    }

}
