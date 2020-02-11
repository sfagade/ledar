/**
 *
 */
package net.icsl.ledar.ejb.dto;

import java.math.BigDecimal;
import java.util.Date;

import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.WardLandProperties;

/**
 * @author aojediran
 *
 */
public class PropertySmallBillsDto {

    private String deliveryCode;
    private Date dofn;
    private String epaymentCode;
    private BigDecimal yearCharge;
    private BigDecimal previousYearBalance;
    private String billingYear;
    private Date deliveryDate;
    private Logindetails deliveryOfficerId;
    private WardLandProperties landPropertyId;

    public PropertySmallBillsDto(String deliveryCode, Date dofn, String epaymentCode, BigDecimal yearCharge,
            BigDecimal previousYearBalance, String billingYear, Date deliveryDate, Logindetails deliveryOfficerId,
            WardLandProperties landPropertyId) {
        super();
        this.deliveryCode = deliveryCode;
        this.dofn = dofn;
        this.epaymentCode = epaymentCode;
        this.yearCharge = yearCharge;
        this.previousYearBalance = previousYearBalance;
        this.billingYear = billingYear;
        this.deliveryDate = deliveryDate;
        this.deliveryOfficerId = deliveryOfficerId;
        this.landPropertyId = landPropertyId;
    }

    public String getDeliveryCode() {
        return deliveryCode;
    }

    public void setDeliveryCode(String deliveryCode) {
        this.deliveryCode = deliveryCode;
    }

    public Date getDofn() {
        return dofn;
    }

    public void setDofn(Date dofn) {
        this.dofn = dofn;
    }

    public String getEpaymentCode() {
        return epaymentCode;
    }

    public void setEpaymentCode(String epaymentCode) {
        this.epaymentCode = epaymentCode;
    }

    public BigDecimal getYearCharge() {
        return yearCharge;
    }

    public void setYearCharge(BigDecimal yearCharge) {
        this.yearCharge = yearCharge;
    }

    public BigDecimal getPreviousYearBalance() {
        return previousYearBalance;
    }

    public void setPreviousYearBalance(BigDecimal previousYearBalance) {
        this.previousYearBalance = previousYearBalance;
    }

    public String getBillingYear() {
        return billingYear;
    }

    public void setBillingYear(String billingYear) {
        this.billingYear = billingYear;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Logindetails getDeliveryOfficerId() {
        return deliveryOfficerId;
    }

    public void setDeliveryOfficerId(Logindetails deliveryOfficerId) {
        this.deliveryOfficerId = deliveryOfficerId;
    }

    public WardLandProperties getLandPropertyId() {
        return landPropertyId;
    }

    public void setLandPropertyId(WardLandProperties landPropertyId) {
        this.landPropertyId = landPropertyId;
    }

}
