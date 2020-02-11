
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
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
@Table(name = "ref_occupations")
@AttributeOverride(name = "id", column = @Column(name = "occupation_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "Occupations.findAll", query = "SELECT o FROM Occupations o"),
    @NamedQuery(name = "Occupations.findByOccupationId", query = "SELECT o FROM Occupations o WHERE o.id = :occupationId"),
    @NamedQuery(name = "Occupations.findByOccupationName", query = "SELECT o FROM Occupations o WHERE o.occupationName = :occupationName")})
public class Occupations extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "occupation_name")
    private String occupationName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "occupationId")
    private List<PropertyBiodatas> propertyBiodatasList;

    public Occupations() {
    }

    public Occupations(Long occupationId) {
        this.id = occupationId;
    }

    public Occupations(Long occupationId, String occupationName) {
        this.id = occupationId;
        this.occupationName = occupationName;
    }

    public String getOccupationName() {
        return occupationName;
    }

    public void setOccupationName(String occupationName) {
        this.occupationName = occupationName;
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
        if (!(object instanceof Occupations)) {
            return false;
        }
        Occupations other = (Occupations) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.Occupations[ occupationId=" + id + " ]";
    }

}
