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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "ref_complaint_status")
@AttributeOverride(name = "id", column = @Column(name = "complaint_status_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "ComplaintStatus.findAll", query = "SELECT c FROM ComplaintStatus c")
    ,
    @NamedQuery(name = "ComplaintStatus.findByComplaintStatusId", query = "SELECT c FROM ComplaintStatus c WHERE c.id = :complaintStatusId")
    ,
    @NamedQuery(name = "ComplaintStatus.findByStatusName", query = "SELECT c FROM ComplaintStatus c WHERE c.statusName = :statusName")})
public class ComplaintStatus extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "status_name")
    private String statusName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "complaintStatusId")
    private List<ComplaintDetails> complaintDetailsList;

    public ComplaintStatus() {
    }

    public ComplaintStatus(Long complaintStatusId) {
        this.id = complaintStatusId;
    }

    public ComplaintStatus(Long complaintStatusId, String statusName, Date created_) {
        this.id = complaintStatusId;
        this.statusName = statusName;
        this.created = created_;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     
    public List<ComplaintDetails> getComplaintDetailsList() {
        return complaintDetailsList;
    }

    public void setComplaintDetailsList(List<ComplaintDetails> complaintDetailsList) {
        this.complaintDetailsList = complaintDetailsList;
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
        if (!(object instanceof ComplaintStatus)) {
            return false;
        }
        ComplaintStatus other = (ComplaintStatus) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.ComplaintStatus[ complaintStatusId=" + id + " ]";
    }

}
