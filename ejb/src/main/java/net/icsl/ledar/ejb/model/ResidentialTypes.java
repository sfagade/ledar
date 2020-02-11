package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
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
@Table(name = "ref_residential_types")
@AttributeOverride(name = "id", column = @Column(name = "residential_type_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "ResidentialTypes.findAll", query = "SELECT r FROM ResidentialTypes r"),
    @NamedQuery(name = "ResidentialTypes.findByResidentialTypeId", query = "SELECT r FROM ResidentialTypes r WHERE r.id = :residentialTypeId"),
    @NamedQuery(name = "ResidentialTypes.findByResidentialTypeName", query = "SELECT r FROM ResidentialTypes r WHERE r.residentialTypeName = :residentialTypeName")})
public class ResidentialTypes extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "residential_type_name")
    private String residentialTypeName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "residentialTypeId")
    private List<PropertyClassificationDetails> propertyClassificationDetailsList;

    public ResidentialTypes() {
    }

    public ResidentialTypes(Long residentialTypeId) {
        this.id = residentialTypeId;
    }

    public ResidentialTypes(Long residentialTypeId, String residentialTypeName, Date created_) {
        this.id = residentialTypeId;
        this.residentialTypeName = residentialTypeName;
        this.created = created_;
    }

    public String getResidentialTypeName() {
        return residentialTypeName;
    }

    public void setResidentialTypeName(String residentialTypeName) {
        this.residentialTypeName = residentialTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     
    public List<PropertyClassificationDetails> getPropertyClassificationDetailsList() {
        return propertyClassificationDetailsList;
    }

    public void setPropertyClassificationDetailsList(List<PropertyClassificationDetails> propertyClassificationDetailsList) {
        this.propertyClassificationDetailsList = propertyClassificationDetailsList;
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
        if (!(object instanceof ResidentialTypes)) {
            return false;
        }
        ResidentialTypes other = (ResidentialTypes) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.ResidentialTypes[ residentialTypeId=" + id + " ]";
    }

}
