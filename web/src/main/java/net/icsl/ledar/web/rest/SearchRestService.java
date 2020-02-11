/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.rest;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.PathParam;
import net.icsl.ledar.ejb.model.BillPayments;
import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.People;
import net.icsl.ledar.ejb.model.PrintedBills;

import net.icsl.ledar.ejb.model.WardLandProperties;
import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.ejb.dto.BillsPaymentsDto;
import net.icsl.ledar.ejb.dto.LcdaWardDto;
import net.icsl.ledar.ejb.dto.PrintedBillsDto;
import net.icsl.ledar.ejb.dto.WardPropertiesDto;
import net.icsl.ledar.ejb.dto.WardStreetsDto;
import net.icsl.ledar.ejb.model.ApplicationConfigurations;
import net.icsl.ledar.ejb.service.OrganizationDataService;
import net.icsl.ledar.web.util.ApplicationUtility;

/**
 * REST Web Service
 *
 * @author sfagade
 */
@Path("restSearch")
@RequestScoped
public class SearchRestService {

    @Inject
    private LcdaWardsDataServices lcdaService;
    @Inject
    private RegisteredUsersDataService regUserService;
    @Inject
    private PrintedBillsService billService;
    @Inject
    private OrganizationDataService orgService;

    /**
     * Creates a new instance of SearchRestService
     */
    public SearchRestService() {
    }

    @GET
    @Path("/fetchCurrentAppConfigs/")
    @Produces("application/json")
    public List<ApplicationConfigurations> fetchCurrentAppConfigs() {
        
        List<ApplicationConfigurations> appConfig = orgService.fetchAppConfigData();
        
        return appConfig;
    }
    
    @GET
    @Path("/searchWardName/{ward_name}/{like_search}")
    @Produces("application/json")
    public List<LcdaWardDto> searchWardByName(@PathParam("ward_name") String ward_name, @PathParam("like_search") boolean like_search) {

        List<LcdaWards> wardlist;
        List<LcdaWardDto> lcdaWards = null;
        if (ward_name != null) {
            wardlist = lcdaService.fetchAllLcdaWardsByName(ward_name, null, like_search);

            if (wardlist != null) {
                lcdaWards = new ArrayList<>();
                for (LcdaWards ward : wardlist) {
                    lcdaWards.add(new LcdaWardDto(ward.getId(), ward.getWardName(), ward.getLocalCouncilDevAreaId().getLcdaName(), ward.getLocalCouncilDevAreaId().getId(), ward.getCreated()));
                }
            }
        }

        return lcdaWards;
    }

    @GET
    @Path("/searchExistingUsernanme/{uname}")
    @Produces("application/json")
    public People searchUsernameExist(@PathParam("uname") String username) {

        People person = null;

        if ((username != null) && (!username.isEmpty())) {
            person = regUserService.findUserByUsername(username);
            if (person != null) {
                person = new People();
                person.setServiceMessage("Username Exist");
            } else {
                person = new People();
                person.setServiceMessage("Username Does not Exist");
            }
        }

        return person;
    }

    @GET
    @Path("/searchNewNotVeririfiedStreet/{street_name}/{like_search}/{verified}")
    @Produces("application/json")
    public List<WardStreetsDto> searchNewNotVeririfiedStreet(@PathParam("street_name") String ward_name, @PathParam("like_search") boolean like_search, @PathParam("verified") boolean verified) {

        List<WardStreets> streetList;
        List<WardStreetsDto> streets = null;

        String verifiedName;
        Long verifiedId;

        if (ward_name != null) {
            streetList = lcdaService.fetchAllWardStreetsByName(ward_name, like_search, verified);

            if (streetList != null) {
                streets = new ArrayList<>();

                for (WardStreets strt : streetList) {
                    verifiedId = (strt.getVerifiedById() != null) ? strt.getVerifiedById().getId() : null;
                    verifiedName = (strt.getVerifiedById() != null) ? strt.getVerifiedById().getPerson().getFullName() : null;

                    streets.add(new WardStreetsDto(strt.getId(), strt.getCreatedById().getId(), strt.getLcdaWardId().getId(), strt.getLcdaWardId().getLocalCouncilDevAreaId().getId(), verifiedId,
                            strt.getStreetName(), strt.getEstateName(), strt.getCreatedById().getPerson().getFullName(), strt.getLcdaWardId().getWardName(),
                            strt.getLcdaWardId().getLocalCouncilDevAreaId().getLcdaName(), verifiedName, strt.getIsVerified(), 0L, strt.getCreated(), strt.getModified()));
                }
            }
        }

        return streets;
    }

    @GET
    @Path("/searchPropertyByIrregularStreet/{street_name}/{like_search}")
    @Produces("application/json")
    public List<WardPropertiesDto> searchPropertyByIrregularStreet(@PathParam("street_name") String ward_name, @PathParam("like_search") boolean like_search) {

        List<WardLandProperties> wardProperties;
        List<WardPropertiesDto> properties = null;

        String verifiedName, lastUpdatedName, streetName, district, lcdaName, enteredByName;
        Long verifiedId, streetId, enteredById, lastUpdatedId;

        if (ward_name != null) {
            wardProperties = lcdaService.fetchAllPropertyByIrregularStreetName(ward_name, like_search);

            if (wardProperties != null) {
                properties = new ArrayList<>();

                for (WardLandProperties strt : wardProperties) {
                    verifiedId = (strt.getVerifiedById() != null) ? strt.getVerifiedById().getId() : null;
                    verifiedName = (strt.getVerifiedById() != null) ? strt.getVerifiedById().getPerson().getFullName() : null;
                    streetId = (strt.getWardStreetId() != null) ? strt.getWardStreetId().getId() : null;
                    streetName = (strt.getWardStreetId() != null) ? strt.getWardStreetId().getStreetName() : null;
                    lastUpdatedName = (strt.getLastUpdatedById() != null) ? strt.getLastUpdatedById().getPerson().getFullName() : null;
                    lastUpdatedId = (strt.getLastUpdatedById() != null) ? strt.getLastUpdatedById().getId() : null;
                    district = (strt.getWardStreetId() != null) ? strt.getWardStreetId().getLcdaWardId().getWardName() : null;
                    lcdaName = (strt.getWardStreetId() != null) ? strt.getWardStreetId().getLcdaWardId().getLocalCouncilDevAreaId().getLcdaName() : null;
                    enteredById = (strt.getEnteredById() != null) ? strt.getEnteredById().getId() : null;
                    enteredByName = (strt.getEnteredById() != null) ? strt.getEnteredById().getPerson().getFullName() : null;

                    properties.add(new WardPropertiesDto(strt.getId(), lastUpdatedId, verifiedId, enteredById, strt.getCapturedById().getId(),
                            strt.getContractorId().getId(), streetId, lastUpdatedName, verifiedName, enteredByName, strt.getPropertyId(), strt.getPropertyNumber(),
                            strt.getIrregularAddress(), strt.getCapturedById().getPerson().getFullName(), strt.getContractorId().getOrganizationNm(), streetName, strt.getIsIrregularAddress(),
                            strt.getIsVerified(), district, lcdaName, strt.getDateCaptured(), strt.getCreated(), strt.getModified(), strt.getEstateName(), strt.getIsInitsSynced()));
                }
            }
        }

        return properties;
    }

    @GET
    @Path("/searchPropertyByPropertyId/{property_id}/{like_search}")
    @Produces("application/json")
    public List<WardPropertiesDto> searchPropertyByPropertyId(@PathParam("property_id") String prop_id, @PathParam("like_search") boolean like_search) {

        List<WardLandProperties> wardProperties;
        List<WardPropertiesDto> properties = null;

        String verifiedName, lastUpdatedName, streetName, district, lcdaName, enteredByName;
        Long verifiedId, streetId, enteredById, lastUpdatedId;

        if (prop_id != null) {
            wardProperties = lcdaService.fetchAllPropertyByPropertyId(prop_id, false, null);

            if (wardProperties != null) {
                properties = new ArrayList<>();

                for (WardLandProperties strt : wardProperties) {
                    verifiedId = (strt.getVerifiedById() != null) ? strt.getVerifiedById().getId() : null;
                    verifiedName = (strt.getVerifiedById() != null) ? strt.getVerifiedById().getPerson().getFullName() : null;
                    streetId = (strt.getWardStreetId() != null) ? strt.getWardStreetId().getId() : null;
                    streetName = (strt.getWardStreetId() != null) ? strt.getWardStreetId().getStreetName() : null;
                    lastUpdatedName = (strt.getLastUpdatedById() != null) ? strt.getLastUpdatedById().getPerson().getFullName() : null;
                    lastUpdatedId = (strt.getLastUpdatedById() != null) ? strt.getLastUpdatedById().getId() : null;
                    district = (strt.getWardStreetId() != null) ? strt.getWardStreetId().getLcdaWardId().getWardName() : null;
                    lcdaName = (strt.getWardStreetId() != null) ? strt.getWardStreetId().getLcdaWardId().getLocalCouncilDevAreaId().getLcdaName() : null;
                    enteredById = (strt.getEnteredById() != null) ? strt.getEnteredById().getId() : null;
                    enteredByName = (strt.getEnteredById() != null) ? strt.getEnteredById().getPerson().getFullName() : null;

                    properties.add(new WardPropertiesDto(strt.getId(), lastUpdatedId, verifiedId, enteredById, strt.getCapturedById().getId(),
                            strt.getContractorId().getId(), streetId, lastUpdatedName, verifiedName, enteredByName, strt.getPropertyId(), strt.getPropertyNumber(),
                            strt.getIrregularAddress(), strt.getCapturedById().getPerson().getFullName(), strt.getContractorId().getOrganizationNm(), streetName, strt.getIsIrregularAddress(),
                            strt.getIsVerified(), district, lcdaName, strt.getDateCaptured(), strt.getCreated(), strt.getModified(), strt.getEstateName(), strt.getIsInitsSynced()));
                }
            }
        }

        return properties;
    }

    @GET
    @Path("/searchBillsPaymentsByCriteria/{payer_name}/{payer_id}/{value_date}/{amount}")
    @Produces("application/json")
    public List<BillsPaymentsDto> searchBillsPaymentsByCriteria(@PathParam("payer_name") String payer_name, @PathParam("payer_id") String payer_id, @PathParam("value_date") String value_date,
            @PathParam("amount") String amount) {

        List<BillPayments> billsPayments;
        List<BillsPaymentsDto> paymentsDto = null;

        DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        payer_name = payer_name.replace("&amp;", "&");

        try {
            Date valueDate = shortDateFormat.parse(value_date);
            BigDecimal crAmount = new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP);

            billsPayments = billService.fetchDuplicateRecord(payer_id, payer_name, crAmount, valueDate);

            if ((billsPayments != null) && (billsPayments.size() > 0)) {
                paymentsDto = new ArrayList<>();
                for (BillPayments payment : billsPayments) {
                    paymentsDto.add(new BillsPaymentsDto(payment.getVPayerName(), payment.getVPayerId(), payment.getEntryId(), payment.getAppliedDate(), payment.getUploadedById().getUserNumber(),
                            payment.getUploadedById().getId().toString(), payment.getEntryDate(), payment.getValueDate(), payment.getSysDate(), payment.getPrintedBillId().getTriggerDate(),
                            payment.getCreated(), payment.getModified(), payment.getCrAmount(), payment.getReceiptBir(), payment.getDSlipRef(), payment.getAgencyRef(), payment.getRevCode(),
                            payment.getTransRef(), payment.getAssessRef(), payment.getReferenceStr(), payment.getShortName(), payment.getMerchantName(), payment.getLgaName(), payment.getDistrictName(),
                            payment.getAddressToUse(), payment.getPayerAddress(), payment.getPayerPhoneNo(), payment.getLrcPin(), payment.getPptAddress(), payment.getLgaLocation(),
                            payment.getPrintedBillId().getPropertyIdn(), payment.getPrintedBillId().getBankPaymentCode(), payment.getIsProcessed(), payment.getIsDuplicate(), payment.getId()));
                }
            }

        } catch (ParseException ex) {
            Logger.getLogger(SearchRestService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return paymentsDto;
    }

    @GET
    @Path("/searchDupBillsByCriteria/{bank_code}/{billing_year}")
    @Produces("application/json")
    public List<PrintedBillsDto> searchBillsPaymentsByCriteria(@PathParam("bank_code") String bank_code, @PathParam("billing_year") String billing_year) {
        List<PrintedBillsDto> billDtos = null;
        List<PrintedBills> printedBill;
        String address_, ownerAddress;

        printedBill = billService.fetchPaymentBills(bank_code, billing_year);

        if ((printedBill != null) && (printedBill.size() > 0)) {
            billDtos = new ArrayList<>();
            for (PrintedBills bill : printedBill) {
                address_ = "";
                address_ = ApplicationUtility.returnBillAddress(address_, bill);

                address_ += ", " + bill.getStreetName();

                ownerAddress = ApplicationUtility.returnBillOwnerAddress(bill);

                ownerAddress += ", " + bill.getOwnerStreetName();
                PrintedBillsDto billDto = new PrintedBillsDto(bill.getId(), bill.getPropertyIdn(), bill.getTaxCategory(), bill.getBillingYear(), bill.getPayerId(), bill.getLga(), bill.getBankPaymentCode(), address_,
                        bill.getName(), bill.getConsultantId().getOrganzaitionCode(), bill.getConsultantId().getId(), bill.getCreated(), bill.getTriggerDate(), bill.getDistrictName(), bill.getBalanceCf(),
                        bill.getLuc(), bill.getAmountDue(), bill.getOwnerName(), ownerAddress);
                billDto.setHasMatchPayment(bill.getHasMatchPayment());
                billDto.setIsDelivered(bill.getIsDelivered());

                billDtos.add(billDto);
            }
        }

        return billDtos;
    }

    @GET
    @Path("/fetchBillNoticeNumber/{notice_number}/{bill_year}")
    @Produces("application/json")
    public List<PrintedBillsDto> fetchBillWithNoticeNumber(@PathParam("notice_number") String notice_no, @PathParam("bill_year") String billing_year) {
        List<PrintedBillsDto> billDto;
        List<PrintedBills> printedBill;

        printedBill = billService.fetchBillsWithSingleCriteria(notice_no, billing_year);

        String address_, ownerAddress;
        billDto = new ArrayList<>();
        if ((printedBill != null) && (printedBill.size() > 0)) {

            for (PrintedBills bill : printedBill) {
                address_ = "";
                address_ = ApplicationUtility.returnBillAddress(address_, bill);

                address_ += ", " + bill.getStreetName();

                ownerAddress = ApplicationUtility.returnBillOwnerAddress(bill);

                ownerAddress += ", " + bill.getOwnerStreetName();

                billDto.add(new PrintedBillsDto(bill.getId(), bill.getPropertyIdn(), bill.getTaxCategory(), bill.getBillingYear(), bill.getPayerId(), bill.getLga(), bill.getBankPaymentCode(), address_,
                        bill.getName(), bill.getConsultantId().getOrganzaitionCode(), bill.getConsultantId().getId(), bill.getCreated(), bill.getTriggerDate(), bill.getDistrictName(), bill.getBalanceCf(),
                        bill.getLuc(), bill.getAmountDue(), bill.getOwnerName(), ownerAddress));
            }
        } else {
            billDto.add(new PrintedBillsDto(null, notice_no + " does not have a recent bill"));
        }

        return billDto;
    }

}
