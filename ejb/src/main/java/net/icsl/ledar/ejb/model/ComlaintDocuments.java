/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 

/**
 *
 * @author sfagade
 * @created Expression created is undefined on line 13, column 15 in
 * Templates/Classes/Class.java.
 */
@Entity
@Table(name = "comlaint_documents")
@AttributeOverride(name = "id", column = @Column(name = "comlaint_document_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "ComlaintDocuments.findAll", query = "SELECT c FROM ComlaintDocuments c"),
    @NamedQuery(name = "ComlaintDocuments.findByComlaintDocumentId", query = "SELECT c FROM ComlaintDocuments c WHERE c.id = :comlaintDocumentId"),
    @NamedQuery(name = "ComlaintDocuments.findByDocumentName", query = "SELECT c FROM ComlaintDocuments c WHERE c.documentName = :documentName")})
public class ComlaintDocuments extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "document_name")
    private String documentName;
    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @JoinColumn(name = "complaint_file_id", referencedColumnName = "file_upload_id")
    @ManyToOne(optional = false)
    private FileUploads complaintFileId;
    @JoinColumn(name = "property_complaint_id", referencedColumnName = "property_complaint_id")
    @ManyToOne(optional = false)
    private PropertyComplaints propertyComplaintId;
    @JoinColumn(name = "document_type_id", referencedColumnName = "document_type_id")
    @ManyToOne(optional = false)
    private DocumentTypes documentTypeId;

    public ComlaintDocuments() {
    }

    public ComlaintDocuments(Long comlaintDocumentId) {
        this.id = comlaintDocumentId;
    }

    public ComlaintDocuments(Long comlaintDocumentId, String documentName) {
        this.id = comlaintDocumentId;
        this.documentName = documentName;
    }

    public ComlaintDocuments(Long doc_id, String documentName, String description, FileUploads complaintFileId, PropertyComplaints propertyComplaintId, DocumentTypes documentTypeId) {
        this.documentName = documentName;
        this.description = description;
        this.complaintFileId = complaintFileId;
        this.propertyComplaintId = propertyComplaintId;
        this.documentTypeId = documentTypeId;
        this.id = doc_id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FileUploads getComplaintFileId() {
        return complaintFileId;
    }

    public void setComplaintFileId(FileUploads complaintFileId) {
        this.complaintFileId = complaintFileId;
    }

    public PropertyComplaints getPropertyComplaintId() {
        return propertyComplaintId;
    }

    public void setPropertyComplaintId(PropertyComplaints propertyComplaintId) {
        this.propertyComplaintId = propertyComplaintId;
    }

    public DocumentTypes getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(DocumentTypes documentTypeId) {
        this.documentTypeId = documentTypeId;
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
        if (!(object instanceof ComlaintDocuments)) {
            return false;
        }
        ComlaintDocuments other = (ComlaintDocuments) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.ComlaintDocuments[ comlaintDocumentId=" + id + " ]";
    }

}
