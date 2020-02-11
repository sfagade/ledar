package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
 

/**
 * @author sfagade
 * @date Feb 3, 2016
 */
@Entity
@Table(name = "property_documents")
@AttributeOverride(name = "id", column = @Column(name = "property_document_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "PropertyDocuments.findAll", query = "SELECT p FROM PropertyDocuments p"),
    @NamedQuery(name = "PropertyDocuments.findByPropertyDocumentId", query = "SELECT p FROM PropertyDocuments p WHERE p.id = :propertyDocumentId"),
    @NamedQuery(name = "PropertyDocuments.findByDocumentName", query = "SELECT p FROM PropertyDocuments p WHERE p.documentName = :documentName")})
public class PropertyDocuments extends IcslLedarModelBase implements Serializable {
   
    @JoinColumn(name = "ward_land_property_id", referencedColumnName = "ward_land_property_id")
    @ManyToOne(optional = false)
    private WardLandProperties wardLandPropertyId;

    private static final long serialVersionUID = 1L;

    @Size(max = 100)
    @Column(name = "document_name")
    private String documentName;
    @Size(max = 500)
    @Column(name = "remarks")
    private String remarks;

    @JoinColumn(name = "document_type_id", referencedColumnName = "document_type_id")
    @ManyToOne(optional = false)
    private DocumentTypes documentTypeId;
    @JoinColumn(name = "file_upload_id", referencedColumnName = "file_upload_id")
    @ManyToOne(optional = false)
    private FileUploads fileUploadId;
    @Column(name = "push_status")
    private String pushStatus;
    @Column(name = "inits_id")
    private Long initsId;

    public PropertyDocuments() {
    }

    public PropertyDocuments(Long propertyDocumentId) {
        this.id = propertyDocumentId;
    }

    public PropertyDocuments(Long propertyDocumentId, String documentName, String remarks, WardLandProperties wardLandPropertyId, DocumentTypes documentTypeId, FileUploads fileUploadId, Date created_, 
            Date modified_) {
        this.id = propertyDocumentId;
        this.documentName = documentName;
        this.remarks = remarks;
        this.documentTypeId = documentTypeId;
        this.fileUploadId = fileUploadId;
        this.created = created_;
        this.modified = modified_;
        this.wardLandPropertyId = wardLandPropertyId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public DocumentTypes getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(DocumentTypes documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public FileUploads getFileUploadId() {
        return fileUploadId;
    }

    public void setFileUploadId(FileUploads fileUploadId) {
        this.fileUploadId = fileUploadId;
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
        if (!(object instanceof PropertyDocuments)) {
            return false;
        }
        PropertyDocuments other = (PropertyDocuments) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.PropertyDocuments[ propertyDocumentId=" + id + " ]";
    }

    public WardLandProperties getWardLandPropertyId() {
        return wardLandPropertyId;
    }

    public void setWardLandPropertyId(WardLandProperties wardLandPropertyId) {
        this.wardLandPropertyId = wardLandPropertyId;
    }

    /**
     * @return the pushStatus
     */
    public String getPushStatus() {
        return pushStatus;
    }

    /**
     * @param pushStatus the pushStatus to set
     */
    public void setPushStatus(String pushStatus) {
        this.pushStatus = pushStatus;
    }

	public Long getInitsId() {
		return initsId;
	}

	public void setInitsId(Long initsId) {
		this.initsId = initsId;
	}
}
