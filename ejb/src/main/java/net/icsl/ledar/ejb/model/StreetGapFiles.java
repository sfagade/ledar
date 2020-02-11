/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 

/**
 *
 * @author sfagade
 */
@Entity
@Table(name = "street_gap_files")
@AttributeOverride(name = "id", column = @Column(name = "street_gap_file_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "StreetGapFiles.findAll", query = "SELECT s FROM StreetGapFiles s")
    , @NamedQuery(name = "StreetGapFiles.findByStreetGapFileId", query = "SELECT s FROM StreetGapFiles s WHERE s.id = :streetGapFileId")})
public class StreetGapFiles extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "file_upload_id", referencedColumnName = "file_upload_id")
    @ManyToOne(optional = false)
    private FileUploads fileUploadId;
    @JoinColumn(name = "street_gap_id", referencedColumnName = "street_gap_id")
    @ManyToOne(optional = false)
    private StreetGaps streetGapId;

    public StreetGapFiles() {
    }

    public StreetGapFiles(Long streetGapFileId) {
        this.id = streetGapFileId;
    }

    public StreetGapFiles(Long gap_file_id, FileUploads fileUploadId, StreetGaps streetGapId, Date created_, Date modified_) {
        this.fileUploadId = fileUploadId;
        this.streetGapId = streetGapId;
        this.id = gap_file_id;
        this.created = created_;
        this.modified = modified_;
    }

    public FileUploads getFileUploadId() {
        return fileUploadId;
    }

    public void setFileUploadId(FileUploads fileUploadId) {
        this.fileUploadId = fileUploadId;
    }

    public StreetGaps getStreetGapId() {
        return streetGapId;
    }

    public void setStreetGapId(StreetGaps streetGapId) {
        this.streetGapId = streetGapId;
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
        if (!(object instanceof StreetGapFiles)) {
            return false;
        }
        StreetGapFiles other = (StreetGapFiles) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.StreetGapFiles[ streetGapFileId=" + id + " ]";
    }

}
