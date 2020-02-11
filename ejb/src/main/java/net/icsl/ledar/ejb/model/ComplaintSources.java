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
@Table(name = "ref_complaint_sources")
@AttributeOverride(name = "id", column = @Column(name = "complaint_source_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "ComplaintSources.findAll", query = "SELECT c FROM ComplaintSources c"),
    @NamedQuery(name = "ComplaintSources.findByComplaintSourceId", query = "SELECT c FROM ComplaintSources c WHERE c.id = :complaintSourceId"),
    @NamedQuery(name = "ComplaintSources.findBySourceName", query = "SELECT c FROM ComplaintSources c WHERE c.sourceName = :sourceName")})
public class ComplaintSources extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "source_name")
    private String sourceName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "complaintSourceId")
    private List<PropertyComplaints> propertyComplaintsList;

    public ComplaintSources() {
    }

    public ComplaintSources(Long complaintSourceId) {
        this.id = complaintSourceId;
    }

    public ComplaintSources(Long complaintSourceId, String sourceName, Date created_) {
        this.id = complaintSourceId;
        this.sourceName = sourceName;
        this.created = created_;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     
    public List<PropertyComplaints> getPropertyComplaintsList() {
        return propertyComplaintsList;
    }

    public void setPropertyComplaintsList(List<PropertyComplaints> propertyComplaintsList) {
        this.propertyComplaintsList = propertyComplaintsList;
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
        if (!(object instanceof ComplaintSources)) {
            return false;
        }
        ComplaintSources other = (ComplaintSources) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.ComplaintSources[ complaintSourceId=" + id + " ]";
    }

}
