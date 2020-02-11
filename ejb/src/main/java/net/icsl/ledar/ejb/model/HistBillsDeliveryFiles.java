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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 

/**
 *
 * @author sfagade
 */
@Entity
@Table(name = "hist_bills_delivery_files")
@AttributeOverride(name = "id", column = @Column(name = "bills_delivery_file_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "HistBillsDeliveryFiles.findAll", query = "SELECT h FROM HistBillsDeliveryFiles h")
    , @NamedQuery(name = "HistBillsDeliveryFiles.findByBillsDeliveryFileId", query = "SELECT h FROM HistBillsDeliveryFiles h WHERE h.id = :billsDeliveryFileId")
    , @NamedQuery(name = "HistBillsDeliveryFiles.findByPrintedBillId", query = "SELECT h FROM HistBillsDeliveryFiles h WHERE h.printedBillId = :printedBillId")})
public class HistBillsDeliveryFiles extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "printed_bill_id")
    private long printedBillId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "delivery_logindetail_id")
    private long deliveryLogindetailId;
    @Column(name = "picture_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pictureDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "file_upload_id")
    private long fileUploadId;
    @Size(max = 100)
    @Column(name = "gps_longitude")
    private String gpsLongitude;
    @Size(max = 100)
    @Column(name = "gps_latitude")
    private String gpsLatitude;
    @Size(max = 1000)
    @Column(name = "remarks")
    private String remarks;
    

    public HistBillsDeliveryFiles() {
    }

    public HistBillsDeliveryFiles(Long billsDeliveryFileId) {
        this.id = billsDeliveryFileId;
    }

    public HistBillsDeliveryFiles(Long billsDeliveryFileId, long printedBillId, long deliveryLogindetailId, long fileUploadId) {
        this.id = billsDeliveryFileId;
        this.printedBillId = printedBillId;
        this.deliveryLogindetailId = deliveryLogindetailId;
        this.fileUploadId = fileUploadId;
    }

    public long getPrintedBillId() {
        return printedBillId;
    }

    public void setPrintedBillId(long printedBillId) {
        this.printedBillId = printedBillId;
    }

    public long getDeliveryLogindetailId() {
        return deliveryLogindetailId;
    }

    public void setDeliveryLogindetailId(long deliveryLogindetailId) {
        this.deliveryLogindetailId = deliveryLogindetailId;
    }

    public Date getPictureDate() {
        return pictureDate;
    }

    public void setPictureDate(Date pictureDate) {
        this.pictureDate = pictureDate;
    }

    public long getFileUploadId() {
        return fileUploadId;
    }

    public void setFileUploadId(long fileUploadId) {
        this.fileUploadId = fileUploadId;
    }

    public String getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(String gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public String getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
        if (!(object instanceof HistBillsDeliveryFiles)) {
            return false;
        }
        HistBillsDeliveryFiles other = (HistBillsDeliveryFiles) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.HistBillsDeliveryFiles[ billsDeliveryFileId=" + id + " ]";
    }
    
}
