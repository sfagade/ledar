
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
 * @date Feb 3, 2016
 */
@Entity
@Table(name = "ref_document_types")
@AttributeOverride(name = "id", column = @Column(name = "document_type_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "DocumentTypes.findAll", query = "SELECT d FROM DocumentTypes d"),
    @NamedQuery(name = "DocumentTypes.findByDocumentTypeId", query = "SELECT d FROM DocumentTypes d WHERE d.id = :documentTypeId"),
    @NamedQuery(name = "DocumentTypes.findByDocumentTypeName", query = "SELECT d FROM DocumentTypes d WHERE d.documentTypeName = :documentTypeName")})
public class DocumentTypes extends IcslLedarModelBase implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "documentTypeId")
    private List<ComlaintDocuments> comlaintDocumentsList;
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "document_type_name")
    private String documentTypeName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "documentTypeId")
    private List<PropertyDocuments> propertyDocumentsList;

    public DocumentTypes() {
    }

    public DocumentTypes(Long documentTypeId) {
        this.id = documentTypeId;
    }

    public DocumentTypes(Long documentTypeId, String documentTypeName, Date created_) {
        this.id = documentTypeId;
        this.documentTypeName = documentTypeName;
        this.created = created_;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     
    public List<PropertyDocuments> getPropertyDocumentsList() {
        return propertyDocumentsList;
    }

    public void setPropertyDocumentsList(List<PropertyDocuments> propertyDocumentsList) {
        this.propertyDocumentsList = propertyDocumentsList;
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
        if (!(object instanceof DocumentTypes)) {
            return false;
        }
        DocumentTypes other = (DocumentTypes) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.DocumentTypes[ documentTypeId=" + id + " ]";
    }

     
    public List<ComlaintDocuments> getComlaintDocumentsList() {
        return comlaintDocumentsList;
    }

    public void setComlaintDocumentsList(List<ComlaintDocuments> comlaintDocumentsList) {
        this.comlaintDocumentsList = comlaintDocumentsList;
    }

}
