
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
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
 * @date Feb 3, 2016
 */
@Entity
@Table(name = "ref_no_title_document_types")
@AttributeOverride(name = "id", column = @Column(name = "no_title_document_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "NoTitleDocumentTypes.findAll", query = "SELECT n FROM NoTitleDocumentTypes n"),
    @NamedQuery(name = "NoTitleDocumentTypes.findByNoTitleDocumentId", query = "SELECT n FROM NoTitleDocumentTypes n WHERE n.id = :noTitleDocumentId"),
    @NamedQuery(name = "NoTitleDocumentTypes.findByNoTitleName", query = "SELECT n FROM NoTitleDocumentTypes n WHERE n.noTitleName = :noTitleName")})
public class NoTitleDocumentTypes extends IcslLedarModelBase implements Serializable {
    
    @OneToMany(mappedBy = "noTitleDocumentId")
    private List<WardLandProperties> wardLandPropertiesList;
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "no_title_name")
    private String noTitleName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    

    public NoTitleDocumentTypes() {
    }

    public NoTitleDocumentTypes(Long noTitleDocumentId) {
        this.id = noTitleDocumentId;
    }

    public NoTitleDocumentTypes(Long noTitleDocumentId, String noTitleName) {
        this.id = noTitleDocumentId;
        this.noTitleName = noTitleName;
    }

    public String getNoTitleName() {
        return noTitleName;
    }

    public void setNoTitleName(String noTitleName) {
        this.noTitleName = noTitleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(object instanceof NoTitleDocumentTypes)) {
            return false;
        }
        NoTitleDocumentTypes other = (NoTitleDocumentTypes) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.NoTitleDocumentTypes[ noTitleDocumentId=" + id + " ]";
    }

     
    public List<WardLandProperties> getWardLandPropertiesList() {
        return wardLandPropertiesList;
    }

    public void setWardLandPropertiesList(List<WardLandProperties> wardLandPropertiesList) {
        this.wardLandPropertiesList = wardLandPropertiesList;
    }

}
