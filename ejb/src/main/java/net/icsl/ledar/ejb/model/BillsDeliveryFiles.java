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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
 

/**
 *
 * @author sfagade
 */
@Entity
@Table(name = "bills_delivery_files")
@AttributeOverride(name = "id", column = @Column(name = "bills_delivery_file_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "BillsDeliveryFiles.findAll", query = "SELECT b FROM BillsDeliveryFiles b")
    , @NamedQuery(name = "BillsDeliveryFiles.findByBillsDeliveryFileId", query = "SELECT b FROM BillsDeliveryFiles b WHERE b.id = :billsDeliveryFileId")})
public class BillsDeliveryFiles extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 1000)
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "picture_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pictureDate;
    @Column(name = "gps_longitude")
    private String gpsLongitude;
    @Column(name = "gps_latitude")
    private String gpsLatitude;
    @JoinColumn(name = "printed_bill_id", referencedColumnName = "printed_bill_id")
    @ManyToOne(optional = false)
    private PrintedBills printedBillId;
    @JoinColumn(name = "file_upload_id", referencedColumnName = "file_upload_id")
    @ManyToOne(optional = false)
    private FileUploads fileUploadId;
    @JoinColumn(name = "delivery_logindetail_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails deliveryLogindetailId;

    public BillsDeliveryFiles() {
    }

    public BillsDeliveryFiles(Long billsDeliveryFileId) {
        this.id = billsDeliveryFileId;
    }

    public BillsDeliveryFiles(Long deliv_id, PrintedBills printedBillId, FileUploads fileUploadId, Logindetails deliveryLogindetailId, Date picture_dt, String longitude, String latitude, Date created_,
            Date modified_) {
        this.printedBillId = printedBillId;
        this.fileUploadId = fileUploadId;
        this.deliveryLogindetailId = deliveryLogindetailId;
        this.id = deliv_id;
        this.created = created_;
        this.modified = modified_;
        this.pictureDate = picture_dt;
        this.gpsLatitude = latitude;
        this.gpsLongitude = longitude;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public PrintedBills getPrintedBillId() {
        return printedBillId;
    }

    public void setPrintedBillId(PrintedBills printedBillId) {
        this.printedBillId = printedBillId;
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
        if (!(object instanceof BillsDeliveryFiles)) {
            return false;
        }
        BillsDeliveryFiles other = (BillsDeliveryFiles) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.BillsDeliveryFiles[ billsDeliveryFileId=" + id + " ]";
    }

    /**
     * @return the deliveryLogindetailId
     */
    public Logindetails getDeliveryLogindetailId() {
        return deliveryLogindetailId;
    }

    /**
     * @param deliveryLogindetailId the deliveryLogindetailId to set
     */
    public void setDeliveryLogindetailId(Logindetails deliveryLogindetailId) {
        this.deliveryLogindetailId = deliveryLogindetailId;
    }

    /**
     * @return the pictureDate
     */
    public Date getPictureDate() {
        return pictureDate;
    }

    /**
     * @param pictureDate the pictureDate to set
     */
    public void setPictureDate(Date pictureDate) {
        this.pictureDate = pictureDate;
    }

    /**
     * @return the gpsLongitude
     */
    public String getGpsLongitude() {
        return gpsLongitude;
    }

    /**
     * @param gpsLongitude the gpsLongitude to set
     */
    public void setGpsLongitude(String gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    /**
     * @return the gpsLatitude
     */
    public String getGpsLatitude() {
        return gpsLatitude;
    }

    /**
     * @param gpsLatitude the gpsLatitude to set
     */
    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

}
