
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
@Table(name = "ref_street_types")
@AttributeOverride(name = "id", column = @Column(name = "street_type_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "StreetTypes.findAll", query = "SELECT s FROM StreetTypes s"),
    @NamedQuery(name = "StreetTypes.findByStreetTypeId", query = "SELECT s FROM StreetTypes s WHERE s.id = :streetTypeId"),
    @NamedQuery(name = "StreetTypes.findByStreetTypeName", query = "SELECT s FROM StreetTypes s WHERE s.streetTypeName = :streetTypeName")})
public class StreetTypes extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "street_type_name")
    private String streetTypeName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "streetTypeId")
    private List<WardStreets> wardStreetsList;

    public StreetTypes() {
    }

    public StreetTypes(Long streetTypeId) {
        this.id = streetTypeId;
    }

    public StreetTypes(Long streetTypeId, String streetTypeName) {
        this.id = streetTypeId;
        this.streetTypeName = streetTypeName;
    }

    public String getStreetTypeName() {
        return streetTypeName;
    }

    public void setStreetTypeName(String streetTypeName) {
        this.streetTypeName = streetTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     
    public List<WardStreets> getWardStreetsList() {
        return wardStreetsList;
    }

    public void setWardStreetsList(List<WardStreets> wardStreetsList) {
        this.wardStreetsList = wardStreetsList;
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
        if (!(object instanceof StreetTypes)) {
            return false;
        }
        StreetTypes other = (StreetTypes) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.StreetTypes[ streetTypeId=" + id + " ]";
    }

}
