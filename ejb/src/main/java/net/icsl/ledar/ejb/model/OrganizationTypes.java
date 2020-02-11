
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
@Table(name = "ref_organization_types")
@AttributeOverride(name = "id", column = @Column(name = "organization_type_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "OrganizationTypes.findAll", query = "SELECT o FROM OrganizationTypes o"),
    @NamedQuery(name = "OrganizationTypes.findByOrganizationTypeId", query = "SELECT o FROM OrganizationTypes o WHERE o.id = :organizationTypeId"),
    @NamedQuery(name = "OrganizationTypes.findByTypeName", query = "SELECT o FROM OrganizationTypes o WHERE o.typeName = :typeName")})
public class OrganizationTypes extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 75)
    @Column(name = "type_name")
    private String typeName;
    @Size(max = 200)
    @Column(name = "description")
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organizationTypeId")
    private List<Organizations> organizationsList;

    public OrganizationTypes() {
    }

    public OrganizationTypes(Long organizationTypeId) {
        this.id = organizationTypeId;
    }

    public OrganizationTypes(Long organizationTypeId, String typeName, Date created) {
        this.id = organizationTypeId;
        this.typeName = typeName;
        this.created = created;
    }
    
    public OrganizationTypes(Long organizationTypeId, String typeName) {
        this.id = organizationTypeId;
        this.typeName = typeName;
      //  this.created = created;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     
    public List<Organizations> getOrganizationsList() {
        return organizationsList;
    }

    public void setOrganizationsList(List<Organizations> organizationsList) {
        this.organizationsList = organizationsList;
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
        if (!(object instanceof OrganizationTypes)) {
            return false;
        }
        OrganizationTypes other = (OrganizationTypes) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.OrganizationTypes[ organizationTypeId=" + id + " ]";
    }

}
