/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
 
 

/**
 *
 * @author sfagade
 */
@Entity
@Cacheable(true)
@Table(name = "printed_bills")
@AttributeOverride(name = "id", column = @Column(name = "printed_bill_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "PrintedBills.findAll", query = "SELECT p FROM PrintedBills p")
    , @NamedQuery(name = "PrintedBills.findByPrintedBillId", query = "SELECT p FROM PrintedBills p WHERE p.id = :printedBillId")
    , @NamedQuery(name = "PrintedBills.findByPropertyIdn", query = "SELECT p FROM PrintedBills p WHERE p.propertyIdn = :propertyIdn")})
public class PrintedBills extends IcslLedarModelBase implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "printedBillId")
    private List<BillPayments> billPaymentsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "printedBillId")
    private List<BillsDeliveryFiles> billsDeliveryFilesList;
    private static final long serialVersionUID = 1L;

    @Size(max = 300)
    @Column(name = "property_idn")
    private String propertyIdn;
    @Size(max = 300)
    @Column(name = "old_property_idn")
    private String oldPropertyIdn;
    @Size(max = 300)
    @Column(name = "epayment_code")
    private String epaymentCode;
    @Size(max = 300)
    @Column(name = "bank_payment_code")
    private String bankPaymentCode;
    @Size(max = 300)
    @Column(name = "parcel_idn")
    private String parcelIdn;
    @Size(max = 300)
    @Column(name = "parcel_sheet_number")
    private String parcelSheetNumber;
    @Size(max = 300)
    @Column(name = "house_no")
    private String houseNo;
    @Size(max = 300)
    @Column(name = "plot_no")
    private String plotNo;
    @Size(max = 300)
    @Column(name = "block_no")
    private String blockNo;
    @Size(max = 300)
    @Column(name = "flat_no")
    private String flatNo;
    @Size(max = 300)
    @Column(name = "street_name")
    private String streetName;
    @Size(max = 300)
    @Column(name = "district_name")
    private String districtName;
    @Size(max = 300)
    @Column(name = "lga")
    private String lga;
    @Size(max = 300)
    @Column(name = "owner_house_no")
    private String ownerHouseNo;
    @Size(max = 300)
    @Column(name = "owner_plot_no")
    private String ownerPlotNo;
    @Size(max = 300)
    @Column(name = "owner_block_no")
    private String ownerBlockNo;
    @Size(max = 300)
    @Column(name = "owner_flat_no")
    private String ownerFlatNo;
    @Size(max = 300)
    @Column(name = "owner_street_name")
    private String ownerStreetName;
    @Size(max = 300)
    @Column(name = "owner_district_name")
    private String ownerDistrictName;
    @Size(max = 300)
    @Column(name = "owner_address")
    private String ownerAddress;
    @Size(max = 300)
    @Column(name = "tax_category")
    private String taxCategory;
    @Size(max = 300)
    @Column(name = "usage_description")
    private String usageDescription;
    @Size(max = 300)
    @Column(name = "owner_name")
    private String ownerName;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "mill_rate")
    private BigDecimal millRate;
    @Column(name = "assessed_value")
    private BigDecimal assessedValue;
    @Column(name = "balance_cf")
    private BigDecimal balanceCf;
    @Column(name = "luc")
    private BigDecimal luc;
    @Column(name = "amount_due")
    private BigDecimal amountDue;
    @Size(max = 45)
    @Column(name = "billing_year")
    private String billingYear;
    @Column(name = "trigger_date")
    @Temporal(TemporalType.DATE)
    private Date triggerDate;
    @Column(name = "disc_amount")
    private BigDecimal discAmount;
    @Column(name = "disc_date")
    @Temporal(TemporalType.DATE)
    private Date discDate;
    @Column(name = "fullpay_date")
    @Temporal(TemporalType.DATE)
    private Date fullpayDate;
    @Column(name = "start_latepaydate1")
    @Temporal(TemporalType.DATE)
    private Date startLatepaydate1;
    @Column(name = "end_latepaydate1")
    @Temporal(TemporalType.DATE)
    private Date endLatepaydate1;
    @Column(name = "amt_latepay1")
    private BigDecimal amtLatepay1;
    @Column(name = "start_latepaydate2")
    @Temporal(TemporalType.DATE)
    private Date startLatepaydate2;
    @Column(name = "end_latepaydate2")
    @Temporal(TemporalType.DATE)
    private Date endLatepaydate2;
    @Column(name = "amt_latepay2")
    private BigDecimal amtLatepay2;
    @Column(name = "start_latepaydate3")
    @Temporal(TemporalType.DATE)
    private Date startLatepaydate3;
    @Column(name = "end_latepaydate3")
    @Temporal(TemporalType.DATE)
    private Date endLatepaydate3;
    @Column(name = "amt_latepay3")
    private BigDecimal amtLatepay3;
    @Size(max = 300)
    @Column(name = "payer_id")
    private String payerId;
    @Size(max = 300)
    @Column(name = "name")
    private String name;
    @Size(max = 300)
    @Column(name = "Name_exp_47")
    private String nameexp47;
    @Size(max = 300)
    @Column(name = "Name_exp_48")
    private String nameexp48;
    @Size(max = 300)
    @Column(name = "Name_exp_49")
    private String nameexp49;
    @Size(max = 300)
    @Column(name = "Name_exp_50")
    private String nameexp50;
    @Size(max = 300)
    @Column(name = "Name_exp_51")
    private String nameexp51;
    @Size(max = 300)
    @Column(name = "barcode")
    private String barcode;
    @Column(name = "payment_status")
    private String paymentStatus;
    @Column(name = "current_picture_name")
    private String currentPictureName;
    @Column(name = "reconciliation_status")
    private String reconciliationStatus;
    @JoinColumn(name = "consultant_id", referencedColumnName = "organization_id")
    @ManyToOne
    private Organizations consultantId;
    @Column(name = "delivery_date")
    @Temporal(TemporalType.DATE)
    private Date deliveryDate;
    @Column(name = "is_delivered")
    private Boolean isDelivered;
    @Column(name = "is_update_required")
    private Boolean isUpdateRequired;
    @Column(name = "total_amount_paid")
    private BigDecimal totalAmountPaid;
    @Column(name = "next_yr_bf")
    private BigDecimal nextYrBf;
    @Column(name = "delivery_comments")
    private String deliveryComments;
    @Column(name = "has_match_payment", columnDefinition = "tinyint(1) default 0")
    private Boolean hasMatchPayment;
    @Column(name = "is_deleted", columnDefinition = "tinyint(1) default 0")
    private Boolean isDeleted;
    @Column(name = "print_count")
    private int printCount;
    @JoinColumn(name = "created_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails createdById;
    @JoinColumn(name = "delivery_logindetail_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails deliveryLogindetailId;
//    @Column(name = "is_structure_changed")
//    private Boolean isStructureChanged;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "update_status")
    private String updateStatus;

    public PrintedBills() {
    }

    public PrintedBills(Long printedBillId) {
        this.id = printedBillId;
    }

    public PrintedBills(String district, String lga_name) {
        this.districtName = district;
        this.lga = lga_name;
    }

    public PrintedBills(Long bill_id, String propertyIdn, String lga, String house_no, String plot_no, String block_no, String flat_no, String street_name, Date created) {
        this.propertyIdn = propertyIdn;
        this.id = bill_id;
        this.created = created;
        this.lga = lga;
        this.houseNo = house_no;
        this.plotNo = plot_no;
        this.streetName = street_name;
        this.blockNo = block_no;
        this.flatNo = flat_no;
    }

    public PrintedBills(Long bill_id, String propertyIdn, String lga, String house_no, String plot_no, String block_no, String flat_no, String street_name, String district, String owner_name, Date created) {
        this.propertyIdn = propertyIdn;
        this.id = bill_id;
        this.created = created;
        this.lga = lga;
        this.houseNo = house_no;
        this.plotNo = plot_no;
        this.streetName = street_name;
        this.blockNo = block_no;
        this.flatNo = flat_no;
        this.districtName = district;
        this.ownerName = owner_name;
    }

    public PrintedBills(Long bill_id, String propertyIdn, String bankPaymentCode, String streetName, String districtName, String lga, String ownerName, BigDecimal amountDue, String billingYear,
            Date triggerDate, BigDecimal discAmount, Date discDate, Date fullpayDate, Date startLatepaydate1, Date endLatepaydate1, BigDecimal amtLatepay1, Date startLatepaydate2, Date endLatepaydate2,
            BigDecimal amtLatepay2, Date startLatepaydate3, Date endLatepaydate3, BigDecimal amtLatepay3, String payerId, Organizations consultantId, Boolean is_delivered, Boolean has_attach, Boolean is_deleted) {
        this.propertyIdn = propertyIdn;
        this.bankPaymentCode = bankPaymentCode;
        this.streetName = streetName;
        this.districtName = districtName;
        this.lga = lga;
        this.ownerName = ownerName;
        this.amountDue = amountDue;
        this.billingYear = billingYear;
        this.triggerDate = triggerDate;
        this.discAmount = discAmount;
        this.discDate = discDate;
        this.fullpayDate = fullpayDate;
        this.startLatepaydate1 = startLatepaydate1;
        this.endLatepaydate1 = endLatepaydate1;
        this.amtLatepay1 = amtLatepay1;
        this.startLatepaydate2 = startLatepaydate2;
        this.endLatepaydate2 = endLatepaydate2;
        this.amtLatepay2 = amtLatepay2;
        this.startLatepaydate3 = startLatepaydate3;
        this.endLatepaydate3 = endLatepaydate3;
        this.amtLatepay3 = amtLatepay3;
        this.payerId = payerId;
        this.consultantId = consultantId;
        this.id = bill_id;
        this.hasMatchPayment = has_attach;
        this.isDeleted = is_deleted;
        this.isDelivered = is_delivered;
    }

    public String getPropertyIdn() {
        return propertyIdn;
    }

    public void setPropertyIdn(String propertyIdn) {
        this.propertyIdn = propertyIdn;
    }

    public String getOldPropertyIdn() {
        return oldPropertyIdn;
    }

    public void setOldPropertyIdn(String oldPropertyIdn) {
        this.oldPropertyIdn = oldPropertyIdn;
    }

    public String getEpaymentCode() {
        return epaymentCode;
    }

    public void setEpaymentCode(String epaymentCode) {
        this.epaymentCode = epaymentCode;
    }

    public String getBankPaymentCode() {
        return bankPaymentCode;
    }

    public void setBankPaymentCode(String bankPaymentCode) {
        this.bankPaymentCode = bankPaymentCode;
    }

    public String getParcelIdn() {
        return parcelIdn;
    }

    public void setParcelIdn(String parcelIdn) {
        this.parcelIdn = parcelIdn;
    }

    public String getParcelSheetNumber() {
        return parcelSheetNumber;
    }

    public void setParcelSheetNumber(String parcelSheetNumber) {
        this.parcelSheetNumber = parcelSheetNumber;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getPlotNo() {
        return plotNo;
    }

    public void setPlotNo(String plotNo) {
        this.plotNo = plotNo;
    }

    public String getBlockNo() {
        return blockNo;
    }

    public void setBlockNo(String blockNo) {
        this.blockNo = blockNo;
    }

    public String getFlatNo() {
        return flatNo;
    }

    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getLga() {
        return lga;
    }

    public void setLga(String lga) {
        this.lga = lga;
    }

    public String getOwnerHouseNo() {
        return ownerHouseNo;
    }

    public void setOwnerHouseNo(String ownerHouseNo) {
        this.ownerHouseNo = ownerHouseNo;
    }

    public String getOwnerPlotNo() {
        return ownerPlotNo;
    }

    public void setOwnerPlotNo(String ownerPlotNo) {
        this.ownerPlotNo = ownerPlotNo;
    }

    public String getOwnerBlockNo() {
        return ownerBlockNo;
    }

    public void setOwnerBlockNo(String ownerBlockNo) {
        this.ownerBlockNo = ownerBlockNo;
    }

    public String getOwnerFlatNo() {
        return ownerFlatNo;
    }

    public void setOwnerFlatNo(String ownerFlatNo) {
        this.ownerFlatNo = ownerFlatNo;
    }

    public String getOwnerStreetName() {
        return ownerStreetName;
    }

    public void setOwnerStreetName(String ownerStreetName) {
        this.ownerStreetName = ownerStreetName;
    }

    public String getOwnerDistrictName() {
        return ownerDistrictName;
    }

    public void setOwnerDistrictName(String ownerDistrictName) {
        this.ownerDistrictName = ownerDistrictName;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public String getTaxCategory() {
        return taxCategory;
    }

    public void setTaxCategory(String taxCategory) {
        this.taxCategory = taxCategory;
    }

    public String getUsageDescription() {
        return usageDescription;
    }

    public void setUsageDescription(String usageDescription) {
        this.usageDescription = usageDescription;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public BigDecimal getMillRate() {
        return millRate;
    }

    public void setMillRate(BigDecimal millRate) {
        this.millRate = millRate;
    }

    public BigDecimal getAssessedValue() {
        return assessedValue;
    }

    public void setAssessedValue(BigDecimal assessedValue) {
        this.assessedValue = assessedValue;
    }

    public BigDecimal getBalanceCf() {
        return balanceCf;
    }

    public void setBalanceCf(BigDecimal balanceCf) {
        this.balanceCf = balanceCf;
    }

    public BigDecimal getLuc() {
        return luc;
    }

    public void setLuc(BigDecimal luc) {
        this.luc = luc;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    public String getBillingYear() {
        return billingYear;
    }

    public void setBillingYear(String billingYear) {
        this.billingYear = billingYear;
    }

    public Date getTriggerDate() {
        return triggerDate;
    }

    public void setTriggerDate(Date triggerDate) {
        this.triggerDate = triggerDate;
    }

    public BigDecimal getDiscAmount() {
        return discAmount;
    }

    public void setDiscAmount(BigDecimal discAmount) {
        this.discAmount = discAmount;
    }

    public Date getDiscDate() {
        return discDate;
    }

    public void setDiscDate(Date discDate) {
        this.discDate = discDate;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getFullpayDate() {
        return fullpayDate;
    }

    public void setFullpayDate(Date fullpayDate) {
        this.fullpayDate = fullpayDate;
    }

    public Date getStartLatepaydate1() {
        return startLatepaydate1;
    }

    public void setStartLatepaydate1(Date startLatepaydate1) {
        this.startLatepaydate1 = startLatepaydate1;
    }

    public Date getEndLatepaydate1() {
        return endLatepaydate1;
    }

    public void setEndLatepaydate1(Date endLatepaydate1) {
        this.endLatepaydate1 = endLatepaydate1;
    }

    public BigDecimal getAmtLatepay1() {
        return amtLatepay1;
    }

    public void setAmtLatepay1(BigDecimal amtLatepay1) {
        this.amtLatepay1 = amtLatepay1;
    }

    public Date getStartLatepaydate2() {
        return startLatepaydate2;
    }

    public void setStartLatepaydate2(Date startLatepaydate2) {
        this.startLatepaydate2 = startLatepaydate2;
    }

    public Date getEndLatepaydate2() {
        return endLatepaydate2;
    }

    public void setEndLatepaydate2(Date endLatepaydate2) {
        this.endLatepaydate2 = endLatepaydate2;
    }

    public BigDecimal getAmtLatepay2() {
        return amtLatepay2;
    }

    public void setAmtLatepay2(BigDecimal amtLatepay2) {
        this.amtLatepay2 = amtLatepay2;
    }

    public Date getStartLatepaydate3() {
        return startLatepaydate3;
    }

    public void setStartLatepaydate3(Date startLatepaydate3) {
        this.startLatepaydate3 = startLatepaydate3;
    }

    public Date getEndLatepaydate3() {
        return endLatepaydate3;
    }

    public void setEndLatepaydate3(Date endLatepaydate3) {
        this.endLatepaydate3 = endLatepaydate3;
    }

    public BigDecimal getAmtLatepay3() {
        return amtLatepay3;
    }

    public void setAmtLatepay3(BigDecimal amtLatepay3) {
        this.amtLatepay3 = amtLatepay3;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameexp47() {
        return nameexp47;
    }

    public void setNameexp47(String nameexp47) {
        this.nameexp47 = nameexp47;
    }

    public String getNameexp48() {
        return nameexp48;
    }

    public void setNameexp48(String nameexp48) {
        this.nameexp48 = nameexp48;
    }

    public String getNameexp49() {
        return nameexp49;
    }

    public void setNameexp49(String nameexp49) {
        this.nameexp49 = nameexp49;
    }

    public String getNameexp50() {
        return nameexp50;
    }

    public void setNameexp50(String nameexp50) {
        this.nameexp50 = nameexp50;
    }

    public String getNameexp51() {
        return nameexp51;
    }

    public void setNameexp51(String nameexp51) {
        this.nameexp51 = nameexp51;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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
        if (!(object instanceof PrintedBills)) {
            return false;
        }
        PrintedBills other = (PrintedBills) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.PrintedBills[ printedBillId=" + id + " ]";
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Organizations getConsultantId() {
        return consultantId;
    }

    public void setConsultantId(Organizations consultantId) {
        this.consultantId = consultantId;
    }

    /**
     * @return the deliveryDate
     */
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * @param deliveryDate the deliveryDate to set
     */
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * @return the isDelivered
     */
    public Boolean getIsDelivered() {
        return isDelivered;
    }

    /**
     * @param isDelivered the isDelivered to set
     */
    public void setIsDelivered(Boolean isDelivered) {
        this.isDelivered = isDelivered;
    }

    /**
     * @return the totalAmountPaid
     */
    public BigDecimal getTotalAmountPaid() {
        return totalAmountPaid;
    }

    /**
     * @param totalAmountPaid the totalAmountPaid to set
     */
    public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    /**
     * @return the nextYrBf
     */
    public BigDecimal getNextYrBf() {
        return nextYrBf;
    }

    /**
     * @param nextYrBf the nextYrBf to set
     */
    public void setNextYrBf(BigDecimal nextYrBf) {
        this.nextYrBf = nextYrBf;
    }

    /**
     * @return the deliveryComments
     */
    public String getDeliveryComments() {
        return deliveryComments;
    }

    /**
     * @param deliveryComments the deliveryComments to set
     */
    public void setDeliveryComments(String deliveryComments) {
        this.deliveryComments = deliveryComments;
    }

    /**
     * @return the hasMatchPayment
     */
    public Boolean getHasMatchPayment() {
        return hasMatchPayment;
    }

    /**
     * @param hasMatchPayment the hasMatchPayment to set
     */
    public void setHasMatchPayment(Boolean hasMatchPayment) {
        this.hasMatchPayment = hasMatchPayment;
    }

    /**
     * @return the printCount
     */
    public int getPrintCount() {
        return printCount;
    }

    /**
     * @param printCount the printCount to set
     */
    public void setPrintCount(int printCount) {
        this.printCount = printCount;
    }

    /**
     * @return the createdById
     */
    public Logindetails getCreatedById() {
        return createdById;
    }

    /**
     * @param createdById the createdById to set
     */
    public void setCreatedById(Logindetails createdById) {
        this.createdById = createdById;
    }

    /**
     * @return the reconciliationStatus
     */
    public String getReconciliationStatus() {
        return reconciliationStatus;
    }

    /**
     * @param reconciliationStatus the reconciliationStatus to set
     */
    public void setReconciliationStatus(String reconciliationStatus) {
        this.reconciliationStatus = reconciliationStatus;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
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
     * @return the isUpdateRequired
     */
    public Boolean getIsUpdateRequired() {
        return isUpdateRequired;
    }

    /**
     * @param isUpdateRequired the isUpdateRequired to set
     */
    public void setIsUpdateRequired(Boolean isUpdateRequired) {
        this.isUpdateRequired = isUpdateRequired;
    }
    
    /**
     * @return the updateStatus
     */
    public String getUpdateStatus() {
        return updateStatus;
    }

    /**
     * @param updateStatus the updateStatus to set
     */
    public void setUpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getCurrentPictureName() {
        return currentPictureName;
    }

    public void setCurrentPictureName(String currentPictureName) {
        this.currentPictureName = currentPictureName;
    }
}
