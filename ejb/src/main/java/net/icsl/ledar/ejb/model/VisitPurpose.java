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
 * @date Mar 30, 2017
 */
@Entity
@Table(name = "ref_visit_purpose")
@AttributeOverride(name = "id", column = @Column(name = "visit_purpose_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "VisitPurpose.findAll", query = "SELECT v FROM VisitPurpose v")
    , @NamedQuery(name = "VisitPurpose.findByVisitPurposeId", query = "SELECT v FROM VisitPurpose v WHERE v.id = :visitPurposeId")
    , @NamedQuery(name = "VisitPurpose.findByPurposeName", query = "SELECT v FROM VisitPurpose v WHERE v.purposeName = :purposeName")})
public class VisitPurpose extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "purpose_name")
    private String purposeName;
    @Size(max = 200)
    @Column(name = "purpose_description")
    private String purposeDescription;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "visitPurposeId")
    private List<Visitors> visitorsList;

    public VisitPurpose() {
    }

    public VisitPurpose(Long visitPurposeId) {
        this.id = visitPurposeId;
    }

    public VisitPurpose(Long visitPurposeId, String purposeName, Date created_) {
        this.id = visitPurposeId;
        this.purposeName = purposeName;
        this.created = created_;
    }

    public String getPurposeName() {
        return purposeName;
    }

    public void setPurposeName(String purposeName) {
        this.purposeName = purposeName;
    }

    public String getPurposeDescription() {
        return purposeDescription;
    }

    public void setPurposeDescription(String purposeDescription) {
        this.purposeDescription = purposeDescription;
    }

     
    public List<Visitors> getVisitorsList() {
        return visitorsList;
    }

    public void setVisitorsList(List<Visitors> visitorsList) {
        this.visitorsList = visitorsList;
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
        if (!(object instanceof VisitPurpose)) {
            return false;
        }
        VisitPurpose other = (VisitPurpose) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.VisitPurpose[ visitPurposeId=" + id + " ]";
    }

}
