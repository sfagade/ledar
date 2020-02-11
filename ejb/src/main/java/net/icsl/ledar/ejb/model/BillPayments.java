/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "bill_payments")
@AttributeOverride(name = "id", column = @Column(name = "payment_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "BillPayments.findAll", query = "SELECT b FROM BillPayments b")
    , @NamedQuery(name = "BillPayments.findByPaymentId", query = "SELECT b FROM BillPayments b WHERE b.id = :paymentId")
    , @NamedQuery(name = "BillPayments.findByVPayerName", query = "SELECT b FROM BillPayments b WHERE b.vPayerName = :vPayerName")
    , @NamedQuery(name = "BillPayments.findByVPayerId", query = "SELECT b FROM BillPayments b WHERE b.vPayerId = :vPayerId")
    , @NamedQuery(name = "BillPayments.findByEntryDate", query = "SELECT b FROM BillPayments b WHERE b.entryDate = :entryDate")
    , @NamedQuery(name = "BillPayments.findByCrAmount", query = "SELECT b FROM BillPayments b WHERE b.crAmount = :crAmount")
    , @NamedQuery(name = "BillPayments.findByAssessRef", query = "SELECT b FROM BillPayments b WHERE b.assessRef = :assessRef")
    , @NamedQuery(name = "BillPayments.findByMerchantName", query = "SELECT b FROM BillPayments b WHERE b.merchantName = :merchantName")
    , @NamedQuery(name = "BillPayments.findByLgaName", query = "SELECT b FROM BillPayments b WHERE b.lgaName = :lgaName")
    , @NamedQuery(name = "BillPayments.findByDistrictName", query = "SELECT b FROM BillPayments b WHERE b.districtName = :districtName")})
public class BillPayments extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 300)
    @Column(name = "v_payer_name")
    private String vPayerName;
    @Size(max = 300)
    @Column(name = "v_payer_id")
    private String vPayerId;
    @Size(max = 300)
    @Column(name = "entry_id")
    private String entryId;
    @Size(max = 300)
    @Column(name = "applied_date")
    private String appliedDate;
    @Column(name = "entry_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    @Column(name = "value_date")
    @Temporal(TemporalType.DATE)
    private Date valueDate;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cr_amount")
    private BigDecimal crAmount;
    @Size(max = 300)
    @Column(name = "receipt_bir")
    private String receiptBir;
    @Size(max = 300)
    @Column(name = "d_slip_ref")
    private String dSlipRef;
    @Size(max = 300)
    @Column(name = "agency_ref")
    private String agencyRef;
    @Size(max = 300)
    @Column(name = "rev_code")
    private String revCode;
    @Size(max = 300)
    @Column(name = "trans_ref")
    private String transRef;
    @Column(name = "sys_date")
    @Temporal(TemporalType.DATE)
    private Date sysDate;
    @Size(max = 300)
    @Column(name = "assess_ref")
    private String assessRef;
    @Size(max = 300)
    @Column(name = "ref")
    private String referenceStr;
    @Size(max = 300)
    @Column(name = "short_name")
    private String shortName;
    @Size(max = 300)
    @Column(name = "merchant_name")
    private String merchantName;
    @Size(max = 300)
    @Column(name = "lga_name")
    private String lgaName;
    @Size(max = 300)
    @Column(name = "district_name")
    private String districtName;
    @Size(max = 300)
    @Column(name = "address_to_use")
    private String addressToUse;
    @Size(max = 300)
    @Column(name = "payer_address")
    private String payerAddress;
    @Size(max = 300)
    @Column(name = "payer_phone_no")
    private String payerPhoneNo;
    @Size(max = 300)
    @Column(name = "lrc_pin")
    private String lrcPin;
    @Size(max = 300)
    @Column(name = "ppt_address")
    private String pptAddress;
    @Column(name = "lga_location")
    private String lgaLocation;
    @Column(name = "is_processed", columnDefinition = "tinyint(1) default 0")
    private Boolean isProcessed;
    @Column(name = "is_duplicate", columnDefinition = "tinyint(1) default 0")
    private Boolean isDuplicate;
    @Column(name = "payment_status")
    private String paymentStatus;
    @JoinColumn(name = "upload_payment_file_id", referencedColumnName = "file_upload_id")
    @ManyToOne(optional = false)
    private FileUploads uploadPaymentFileId;
    @JoinColumn(name = "uploaded_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne(optional = false)
    private Logindetails uploadedById;
    @JoinColumn(name = "printed_bill_id", referencedColumnName = "printed_bill_id")
    @ManyToOne //(optional = false)
    private PrintedBills printedBillId;
    @JoinColumn(name = "consultant_id", referencedColumnName = "organization_id")
    @ManyToOne
    private Organizations consultantId;

    public BillPayments() {
    }

    public BillPayments(Long paymentId) {
        this.id = paymentId;
    }

    public BillPayments(Long payment_id, String vPayerName, String vPayerId, String entryId, String appliedDate, Date entryDate, Date valueDate, BigDecimal crAmount, String receiptBir, String dSlipRef,
            String agencyRef, String revCode, String transRef, Date sysDate, String assessRef, String referenceStr, String shortName, String merchantName, String lgaName, String districtName,
            String addressToUse, String payerAddress, String payerPhoneNo, String lrcPin, String pptAddress, String lgaLocation, FileUploads uploadPaymentFileId, Logindetails uploadedById,
            PrintedBills printedBillId, Date created_, Date modified_) {
        this.vPayerName = vPayerName;
        this.vPayerId = vPayerId;
        this.entryId = entryId;
        this.appliedDate = appliedDate;
        this.entryDate = entryDate;
        this.valueDate = valueDate;
        this.crAmount = crAmount;
        this.receiptBir = receiptBir;
        this.dSlipRef = dSlipRef;
        this.agencyRef = agencyRef;
        this.revCode = revCode;
        this.transRef = transRef;
        this.sysDate = sysDate;
        this.assessRef = assessRef;
        this.referenceStr = referenceStr;
        this.shortName = shortName;
        this.merchantName = merchantName;
        this.lgaName = lgaName;
        this.districtName = districtName;
        this.addressToUse = addressToUse;
        this.payerAddress = payerAddress;
        this.payerPhoneNo = payerPhoneNo;
        this.lrcPin = lrcPin;
        this.pptAddress = pptAddress;
        this.lgaLocation = lgaLocation;
        this.uploadPaymentFileId = uploadPaymentFileId;
        this.uploadedById = uploadedById;
        this.printedBillId = printedBillId;
        this.id = payment_id;
        this.created = created_;
        this.modified = modified_;
    }

    public String getVPayerName() {
        return vPayerName;
    }

    public void setVPayerName(String vPayerName) {
        this.vPayerName = vPayerName;
    }

    public String getVPayerId() {
        return vPayerId;
    }

    public void setVPayerId(String vPayerId) {
        this.vPayerId = vPayerId;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(String appliedDate) {
        this.appliedDate = appliedDate;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public BigDecimal getCrAmount() {
        return crAmount;
    }

    public void setCrAmount(BigDecimal crAmount) {
        this.crAmount = crAmount;
    }

    public String getReceiptBir() {
        return receiptBir;
    }

    public void setReceiptBir(String receiptBir) {
        this.receiptBir = receiptBir;
    }

    public String getDSlipRef() {
        return dSlipRef;
    }

    public void setDSlipRef(String dSlipRef) {
        this.dSlipRef = dSlipRef;
    }

    public String getAgencyRef() {
        return agencyRef;
    }

    public void setAgencyRef(String agencyRef) {
        this.agencyRef = agencyRef;
    }

    public String getRevCode() {
        return revCode;
    }

    public void setRevCode(String revCode) {
        this.revCode = revCode;
    }

    public String getTransRef() {
        return transRef;
    }

    public void setTransRef(String transRef) {
        this.transRef = transRef;
    }

    public Date getSysDate() {
        return sysDate;
    }

    public void setSysDate(Date sysDate) {
        this.sysDate = sysDate;
    }

    public String getAssessRef() {
        return assessRef;
    }

    public void setAssessRef(String assessRef) {
        this.assessRef = assessRef;
    }

    public String getReferenceStr() {
        return referenceStr;
    }

    public void setReferenceStr(String ref) {
        this.referenceStr = ref;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getLgaName() {
        return lgaName;
    }

    public void setLgaName(String lgaName) {
        this.lgaName = lgaName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getAddressToUse() {
        return addressToUse;
    }

    public void setAddressToUse(String addressToUse) {
        this.addressToUse = addressToUse;
    }

    public String getPayerAddress() {
        return payerAddress;
    }

    public void setPayerAddress(String payerAddress) {
        this.payerAddress = payerAddress;
    }

    public String getPayerPhoneNo() {
        return payerPhoneNo;
    }

    public void setPayerPhoneNo(String payerPhoneNo) {
        this.payerPhoneNo = payerPhoneNo;
    }

    public String getLrcPin() {
        return lrcPin;
    }

    public void setLrcPin(String lrcPin) {
        this.lrcPin = lrcPin;
    }

    public String getPptAddress() {
        return pptAddress;
    }

    public void setPptAddress(String pptAddress) {
        this.pptAddress = pptAddress;
    }

    public FileUploads getUploadPaymentFileId() {
        return uploadPaymentFileId;
    }

    public void setUploadPaymentFileId(FileUploads uploadPaymentFileId) {
        this.uploadPaymentFileId = uploadPaymentFileId;
    }

    public Logindetails getUploadedById() {
        return uploadedById;
    }

    public void setUploadedById(Logindetails uploadedById) {
        this.uploadedById = uploadedById;
    }

    public PrintedBills getPrintedBillId() {
        return printedBillId;
    }

    public void setPrintedBillId(PrintedBills printedBillId) {
        this.printedBillId = printedBillId;
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
        if (!(object instanceof BillPayments)) {
            return false;
        }
        BillPayments other = (BillPayments) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.BillPayments[ paymentId=" + id + " ]";
    }

    /**
     * @return the lgaLocation
     */
    public String getLgaLocation() {
        return lgaLocation;
    }

    /**
     * @param lgaLocation the lgaLocation to set
     */
    public void setLgaLocation(String lgaLocation) {
        this.lgaLocation = lgaLocation;
    }

    /**
     * @return the isProcessed
     */
    public Boolean getIsProcessed() {
        return isProcessed;
    }

    /**
     * @param isProcessed the isProcessed to set
     */
    public void setIsProcessed(Boolean isProcessed) {
        this.isProcessed = isProcessed;
    }

    /**
     * @return the isDuplicate
     */
    public Boolean getIsDuplicate() {
        return isDuplicate;
    }

    /**
     * @param isDuplicate the isDuplicate to set
     */
    public void setIsDuplicate(Boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

    /**
     * @return the consultantId
     */
    public Organizations getConsultantId() {
        return consultantId;
    }

    /**
     * @param consultantId the consultantId to set
     */
    public void setConsultantId(Organizations consultantId) {
        this.consultantId = consultantId;
    }

    /**
     * @return the paymentStatus
     */
    public String getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * @param paymentStatus the paymentStatus to set
     */
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
