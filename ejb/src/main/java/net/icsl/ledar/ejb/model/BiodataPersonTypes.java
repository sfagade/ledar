
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
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "biodata_person_types")
@AttributeOverride(name = "id", column = @Column(name = "biodata_person_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "BiodataPersonTypes.findAll", query = "SELECT b FROM BiodataPersonTypes b"),
    @NamedQuery(name = "BiodataPersonTypes.findByBiodataPersonId", query = "SELECT b FROM BiodataPersonTypes b WHERE b.id = :biodataPersonId"),
    @NamedQuery(name = "BiodataPersonTypes.findByBiodataTypeName", query = "SELECT b FROM BiodataPersonTypes b WHERE b.biodataTypeName = :biodataTypeName")})
public class BiodataPersonTypes extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "biodata_type_name")
    private String biodataTypeName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    @Column(name = "inits_mapping")
    private String initsMapping;
   
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "biodataPersonId")
    private List<PropertyBiodatas> propertyBiodatasList;

    public BiodataPersonTypes() {
    }

    public BiodataPersonTypes(Long biodataPersonId) {
        this.id = biodataPersonId;
    }

    public BiodataPersonTypes(Long biodataPersonId, String biodataTypeName) {
        this.id = biodataPersonId;
        this.biodataTypeName = biodataTypeName;
    }
    
    public BiodataPersonTypes(Long biodataPersonId, String biodataTypeName, Date created_) {
        this.id = biodataPersonId;
        this.biodataTypeName = biodataTypeName;
        this.created = created_;
    }

    public String getBiodataTypeName() {
        return biodataTypeName;
    }

    public void setBiodataTypeName(String biodataTypeName) {
        this.biodataTypeName = biodataTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     
    public List<PropertyBiodatas> getPropertyBiodatasList() {
        return propertyBiodatasList;
    }

    public void setPropertyBiodatasList(List<PropertyBiodatas> propertyBiodatasList) {
        this.propertyBiodatasList = propertyBiodatasList;
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
        if (!(object instanceof BiodataPersonTypes)) {
            return false;
        }
        BiodataPersonTypes other = (BiodataPersonTypes) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.BiodataPersonTypes[ biodataPersonId=" + id + " ]";
    }

	public String getInitsMapping() {
		return initsMapping;
	}

	public void setInitsMapping(String initsMapping) {
		this.initsMapping = initsMapping;
	}

}
