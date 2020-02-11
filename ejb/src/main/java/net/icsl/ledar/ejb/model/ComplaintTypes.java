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
 * @created Expression created is undefined on line 13, column 15 in Templates/Classes/Class.java.
 */
@Entity
@Table(name = "ref_complaint_types")
@AttributeOverride(name = "id", column = @Column(name = "complaint_type_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "ComplaintTypes.findAll", query = "SELECT c FROM ComplaintTypes c"),
    @NamedQuery(name = "ComplaintTypes.findByComplaintTypeId", query = "SELECT c FROM ComplaintTypes c WHERE c.id = :complaintTypeId"),
    @NamedQuery(name = "ComplaintTypes.findByTypeName", query = "SELECT c FROM ComplaintTypes c WHERE c.typeName = :typeName"),
    @NamedQuery(name = "ComplaintTypes.findByHandlingGroup", query = "SELECT c FROM ComplaintTypes c WHERE c.handlingGroup = :handlingGroup")})
public class ComplaintTypes extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "type_name")
    private String typeName;
    @Size(max = 35)
    @Column(name = "handling_group")
    private String handlingGroup;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "complaintTypeId")
    private List<ComplaintDetails> complaintDetailsList;

    public ComplaintTypes() {
    }

    public ComplaintTypes(Long complaintTypeId) {
        this.id = complaintTypeId;
    }

    public ComplaintTypes(Long complaintTypeId, String typeName, Date created_) {
        this.id = complaintTypeId;
        this.typeName = typeName;
        this.created = created_;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getHandlingGroup() {
        return handlingGroup;
    }

    public void setHandlingGroup(String handlingGroup) {
        this.handlingGroup = handlingGroup;
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
        if (!(object instanceof ComplaintTypes)) {
            return false;
        }
        ComplaintTypes other = (ComplaintTypes) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.ComplaintTypes[ complaintTypeId=" + id + " ]";
    }

}
