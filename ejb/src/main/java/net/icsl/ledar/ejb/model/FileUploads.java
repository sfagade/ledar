package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
 

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "file_uploads")
@AttributeOverride(name = "id", column = @Column(name = "file_upload_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "FileUploads.findAll", query = "SELECT f FROM FileUploads f"),
    @NamedQuery(name = "FileUploads.findByFileUploadId", query = "SELECT f FROM FileUploads f WHERE f.id = :fileUploadId"),
    @NamedQuery(name = "FileUploads.findByName", query = "SELECT f FROM FileUploads f WHERE f.fileName = :name"),
    @NamedQuery(name = "FileUploads.findByType", query = "SELECT f FROM FileUploads f WHERE f.fileType = :type"),
    @NamedQuery(name = "FileUploads.findBySide", query = "SELECT f FROM FileUploads f WHERE f.fileSide = :side")})
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class FileUploads extends IcslLedarModelBase implements Serializable {

    @Lob
    @Column(name = "file_content")
    private byte[] fileContent;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fileUploadId")
    private Collection<StreetGapFiles> streetGapFilesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "uploadPaymentFileId")
    private List<BillPayments> billPaymentsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fileUploadId")
    private List<BillsDeliveryFiles> billsDeliveryFilesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fileUploadId")
    private List<BarelandFiles> barelandFilesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "complaintFileId")
    private List<ComlaintDocuments> comlaintDocumentsList;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "file_name")
    private String fileName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "file_type")
    private String fileType;
    @Column(name = "is_deleted", columnDefinition = "tinyint(1) default 0")
    private Boolean isDeleted;
    @Column(name = "delete_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date deleteDate;

    @Size(min = 1, max = 20)
    @Column(name = "file_side")
    private String fileSide;
    @Column(name = "absolute_path")
    private String absolutePath;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fileUploadId")
    private List<PropertyDocuments> propertyDocumentsList;
    @JoinColumn(name = "uploaded_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne(optional = false)
    private Logindetails uploadedById;
    @JoinColumn(name = "deleted_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails deletedById;

    private static final long serialVersionUID = 1L;

    @Size(min = 1, max = 250)
    @Column(name = "file_path")
    private String filePath;

    @OneToMany(mappedBy = "pictureUploadId")
    private List<People> peopleList;
    @OneToMany(mappedBy = "organizationLogoId")
    private List<Organizations> organizationsList;
    // private StreamedContent uploadImage;

    public FileUploads() {
    }

    public FileUploads(Long fileUploadId) {
        this.id = fileUploadId;
    }

    public FileUploads(Long fileId, byte[] fileContent, String fileName, String fileType, String fileSide, String filePath, Logindetails uploader, Date created_, Date modified_) {
        this.fileContent = fileContent;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSide = fileSide;
        this.filePath = filePath;
        this.uploadedById = uploader;
        this.id = fileId;
        this.isDeleted = false;
        this.created = created_;
        this.modified = modified_;
    }

    public FileUploads(Long fileId, byte[] fileContent, String fileName, String fileType, String fileSide, String filePath, Logindetails uploader, String absolute, Date created_, Date modified_) {
        this.fileContent = fileContent;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSide = fileSide;
        this.filePath = filePath;
        this.uploadedById = uploader;
        this.id = fileId;
        this.isDeleted = false;
        this.created = created_;
        this.modified = modified_;
        this.absolutePath = absolute;
    }

    public FileUploads(Long fileUploadId, String fileName, String fileType, String fileSide, Date created_) {
        this.id = fileUploadId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSide = fileSide;
        this.created = created_;
    }

    public FileUploads(Long file_id, byte[] fileContent, String fileName, String fileType, Date created_) {
        this.fileContent = fileContent;
        this.fileName = fileName;
        this.fileType = fileType;
        this.id = file_id;
        this.created = created_;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

     
    public List<People> getPeopleList() {
        return peopleList;
    }

    public void setPeopleList(List<People> peopleList) {
        this.peopleList = peopleList;
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
        if (!(object instanceof FileUploads)) {
            return false;
        }
        FileUploads other = (FileUploads) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.FileUploads[ fileUploadId=" + id + " ]";
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSide() {
        return fileSide;
    }

    public void setFileSide(String fileSide) {
        this.fileSide = fileSide;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

     
    public List<PropertyDocuments> getPropertyDocumentsList() {
        return propertyDocumentsList;
    }

    public void setPropertyDocumentsList(List<PropertyDocuments> propertyDocumentsList) {
        this.propertyDocumentsList = propertyDocumentsList;
    }

    public Logindetails getUploadedById() {
        return uploadedById;
    }

    public void setUploadedById(Logindetails uploadedById) {
        this.uploadedById = uploadedById;
    }

     
    public List<ComlaintDocuments> getComlaintDocumentsList() {
        return comlaintDocumentsList;
    }

    public void setComlaintDocumentsList(List<ComlaintDocuments> comlaintDocumentsList) {
        this.comlaintDocumentsList = comlaintDocumentsList;
    }

    /**
     * @return the isDeleted
     */
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    /**
     * @param isDeleted the isDeleted to set
     */
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * @return the deletedById
     */
    public Logindetails getDeletedById() {
        return deletedById;
    }

    /**
     * @param deletedById the deletedById to set
     */
    public void setDeletedById(Logindetails deletedById) {
        this.deletedById = deletedById;
    }

    /**
     * @return the deleteDate
     */
    public Date getDeleteDate() {
        return deleteDate;
    }

    /**
     * @param deleteDate the deleteDate to set
     */
    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

     
    public List<BarelandFiles> getBarelandFilesList() {
        return barelandFilesList;
    }

    public void setBarelandFilesList(List<BarelandFiles> barelandFilesList) {
        this.barelandFilesList = barelandFilesList;
    }

    /**
     * @return the absolutePath
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * @param absolutePath the absolutePath to set
     */
    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

     
    public List<BillsDeliveryFiles> getBillsDeliveryFilesList() {
        return billsDeliveryFilesList;
    }

    public void setBillsDeliveryFilesList(List<BillsDeliveryFiles> billsDeliveryFilesList) {
        this.billsDeliveryFilesList = billsDeliveryFilesList;
    }

     
    public List<BillPayments> getBillPaymentsList() {
        return billPaymentsList;
    }

    public void setBillPaymentsList(List<BillPayments> billPaymentsList) {
        this.billPaymentsList = billPaymentsList;
    }

     
    public Collection<StreetGapFiles> getStreetGapFilesCollection() {
        return streetGapFilesCollection;
    }

    public void setStreetGapFilesCollection(Collection<StreetGapFiles> streetGapFilesCollection) {
        this.streetGapFilesCollection = streetGapFilesCollection;
    }
}
