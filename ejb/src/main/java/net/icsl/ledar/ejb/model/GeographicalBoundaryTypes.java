
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
 
 

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "ref_geographical_boundary_types")
@AttributeOverride(name = "id", column = @Column(name = "geographical_boundary_type_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "GeographicalBoundaryTypes.findAll", query = "SELECT g FROM GeographicalBoundaryTypes g"),
    @NamedQuery(name = "GeographicalBoundaryTypes.findByGeographicalBoundaryTypeId", query = "SELECT g FROM GeographicalBoundaryTypes g WHERE g.id = :geographicalBoundaryTypeId"),
    @NamedQuery(name = "GeographicalBoundaryTypes.findByBoundaryTypeName", query = "SELECT g FROM GeographicalBoundaryTypes g WHERE g.boundaryTypeName = :boundaryTypeName")})
public class GeographicalBoundaryTypes extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Size(max = 45)
    @Column(name = "boundary_type_name")
    private String boundaryTypeName;
    @Size(max = 25)
    @Column(name = "entity_name")
    private String entityName;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "geographicalBoundaryTypeId")
    private List<GeographicalBoundaries> geographicalBoundariesList;

    public GeographicalBoundaryTypes() {
    }

    public GeographicalBoundaryTypes(Long geographicalBoundaryTypeId) {
        this.id = geographicalBoundaryTypeId;
    }

    public String getBoundaryTypeName() {
        return boundaryTypeName;
    }
    
    public GeographicalBoundaryTypes(Long geographicalBoundaryTypeId, String boundaryTypeName) {
        this.id = geographicalBoundaryTypeId;
        this.boundaryTypeName = boundaryTypeName;
    }

    public void setBoundaryTypeName(String boundaryTypeName) {
        this.boundaryTypeName = boundaryTypeName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

     
    public List<GeographicalBoundaries> getGeographicalBoundariesList() {
        return geographicalBoundariesList;
    }

    public void setGeographicalBoundariesList(List<GeographicalBoundaries> geographicalBoundariesList) {
        this.geographicalBoundariesList = geographicalBoundariesList;
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
        if (!(object instanceof GeographicalBoundaryTypes)) {
            return false;
        }
        GeographicalBoundaryTypes other = (GeographicalBoundaryTypes) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.GeographicalBoundaryTypes[ geographicalBoundaryTypeId=" + id + " ]";
    }

}
