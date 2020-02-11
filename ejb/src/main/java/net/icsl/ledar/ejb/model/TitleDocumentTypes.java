
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
@Table(name = "ref_title_document_types")
@AttributeOverride(name = "id", column = @Column(name = "title_document_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "TitleDocumentTypes.findAll", query = "SELECT t FROM TitleDocumentTypes t"),
    @NamedQuery(name = "TitleDocumentTypes.findByTitleDocumentId", query = "SELECT t FROM TitleDocumentTypes t WHERE t.id = :titleDocumentId"),
    @NamedQuery(name = "TitleDocumentTypes.findByTitleName", query = "SELECT t FROM TitleDocumentTypes t WHERE t.titleName = :titleName")})
public class TitleDocumentTypes extends IcslLedarModelBase implements Serializable {
    
    @OneToMany(mappedBy = "titleDocumentId")
    private List<WardLandProperties> wardLandPropertiesList;
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "title_name")
    private String titleName;
    @Size(max = 1000)
    @Column(name = "description")
    private String description;
    

    public TitleDocumentTypes() {
    }

    public TitleDocumentTypes(Long titleDocumentId) {
        this.id = titleDocumentId;
    }

    public TitleDocumentTypes(Long titleDocumentId, String titleName) {
        this.id = titleDocumentId;
        this.titleName = titleName;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
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
        if (!(object instanceof TitleDocumentTypes)) {
            return false;
        }
        TitleDocumentTypes other = (TitleDocumentTypes) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.TitleDocumentTypes[ titleDocumentId=" + id + " ]";
    }

     
    public List<WardLandProperties> getWardLandPropertiesList() {
        return wardLandPropertiesList;
    }

    public void setWardLandPropertiesList(List<WardLandProperties> wardLandPropertiesList) {
        this.wardLandPropertiesList = wardLandPropertiesList;
    }
}
