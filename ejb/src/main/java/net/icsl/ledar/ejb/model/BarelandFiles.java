/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
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
 */
@Entity
@Table(name = "bareland_files")
@AttributeOverride(name = "id", column = @Column(name = "bareland_file_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "BarelandFiles.findAll", query = "SELECT b FROM BarelandFiles b"),
    @NamedQuery(name = "BarelandFiles.findByBarelandFileId", query = "SELECT b FROM BarelandFiles b WHERE b.id = :barelandFileId"),
    @NamedQuery(name = "BarelandFiles.findByFileType", query = "SELECT b FROM BarelandFiles b WHERE b.fileType = :fileType"),
    @NamedQuery(name = "BarelandFiles.findByDescription", query = "SELECT b FROM BarelandFiles b WHERE b.description = :description"),
    @NamedQuery(name = "BarelandFiles.findByCreated", query = "SELECT b FROM BarelandFiles b WHERE b.created = :created"),
    @NamedQuery(name = "BarelandFiles.findByModified", query = "SELECT b FROM BarelandFiles b WHERE b.modified = :modified")})
public class BarelandFiles extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "file_type")
    private String fileType;
    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @JoinColumn(name = "bare_land_id", referencedColumnName = "bare_land_id")
    @ManyToOne(optional = false)
    private BareLands bareLandId;
    @JoinColumn(name = "file_upload_id", referencedColumnName = "file_upload_id")
    @ManyToOne(optional = false)
    private FileUploads fileUploadId;

    public BarelandFiles() {
    }

    public BarelandFiles(Long barelandFileId) {
        this.id = barelandFileId;
    }

    public BarelandFiles(Long barelandFileId, String fileType, Date created_) {
        this.id = barelandFileId;
        this.fileType = fileType;
        this.created = created_;
    }

    public BarelandFiles(Long bland_id, String fileType, BareLands bareLandId, FileUploads fileUploadId, Date created_, Date modifed_) {
		super();
		this.fileType = fileType;
		this.bareLandId = bareLandId;
		this.fileUploadId = fileUploadId;
		this.id = bland_id;
		this.created = created_;
		this.modified = modifed_;
	}

	public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BareLands getBareLandId() {
        return bareLandId;
    }

    public void setBareLandId(BareLands bareLandId) {
        this.bareLandId = bareLandId;
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
        if (!(object instanceof BarelandFiles)) {
            return false;
        }
        BarelandFiles other = (BarelandFiles) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.BarelandFiles[ barelandFileId=" + id + " ]";
    }

}
