package net.icsl.ledar.web.bean;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.UploadedFile;

import net.icsl.ledar.ejb.enums.PaymentStatusEnum;
import net.icsl.ledar.ejb.model.BillPayments;
import net.icsl.ledar.ejb.model.BillsDeliveryFiles;
import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.Organizations;
import net.icsl.ledar.ejb.model.PrintedBills;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import net.icsl.ledar.ejb.service.ReferenceDataService;
import net.icsl.ledar.ejb.util.DataExportProcessor;
import net.icsl.ledar.ejb.util.DataProcessorUtil;
import net.icsl.ledar.ejb.util.DataloadProcessor;
import net.icsl.ledar.ejb.util.EmailSender;
import net.icsl.ledar.ejb.util.PaymentsProcessor;
import net.icsl.ledar.ejb.dto.BillsPaymentsDto;
import net.icsl.ledar.ejb.dto.PrintedBillsDto;
import net.icsl.ledar.web.lazyModel.BillsPaymentLazy;
import net.icsl.ledar.web.lazyModel.PrintedBillsLazy;
import net.icsl.ledar.web.util.ApplicationUtility;
import net.icsl.ledar.web.util.FacesSupportUtil;
import net.icsl.ledar.web.wsclient.AlphaBetaWebServiceClient;
import org.primefaces.model.DualListModel;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.PieChartModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author sfagade
 */
@Named(value = "billingBean")
@SessionScoped
public class BillingBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EmailSender emailSender;

    @Inject
    private PrintedBillsService billsService;
    @Inject
    private ReferenceDataService refService;
    @Inject
    private LcdaWardsDataServices lcdaService;
    @Inject
    private LandPropertiesDataService propsService;
    @Inject
    private DataloadProcessor dataLoader;
    @Inject
    private DataExportProcessor dataExport;
    @Inject
    private DataProcessorUtil dataProcUtil;
    @Inject
    private PaymentsProcessor paymentProcs;
    @Inject
    private LoginBean loginBean;
    @Inject
    private LedarApplicationBean leaderApp;
    @Inject
    private F5Detector f5detector;

    private UploadedFile excelFile;
    private Properties prop;

    private final DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private final FacesContext context = FacesContext.getCurrentInstance();
    private final ExternalContext extContaxt = context.getExternalContext();

    private PieChartModel deliveryChart;
    private LineChartModel processChart;
    private BarChartModel barModel;
    private DualListModel<String> sortString;

    private List<Organizations> organizationList;
    private List<LocalCouncilDevArea> lcdArea;
    private LazyDataModel<PrintedBills> lazyPrintedBills;
    private LazyDataModel<BillPayments> lazyBillsPayment;
    private List<BillPayments> billsPayment;
    private List<BillsDeliveryFiles> deliveryFiles;
    private List<PrintedBills> printedBills;
    private List<FileUploads> uploadedFiles;
    private List<BillsPaymentsDto> paymentDto;

    private PrintedBillsDto billStreets, billDistrict;
    private LocalCouncilDevArea selectedLcda;
    private Organizations selectedOrganization;
    private PrintedBills selectedBill;
    private BillPayments paymentData;

    private List<Object[]> duplicateBillList;

    private BigDecimal totalExpectedAmount, totalLateAmount, totalSecLateAmount, totalThirdLateAmount, totalLucAmount, totalAmountPaid;
    private long recordCount, duplicatesCount, deliveryCount, billId, hasPayment, totalBillsCount, dayCount;
    private String propertyId, selectedStatus, selectedStreetName, selectedDistrictName, selectedDate, selectedLoadType, paymentAvailable, isDelivered, payerId, receipt, processedStatus, selectedMonth;
    private String propertyOwnerName, bankPaymentCode;

    private Date startSearchDate, endSearchDate, deliveryDate, syncDate;
    private int resultIndex;
    private Boolean showSeacrhResult;

    private String[] data__load_type = {"Individuals", "Companies"}, paymentProcess = {"All Payments", "Proceesed Payments", "Unprocessed Payments", "Duplicate Payment", "Bills Found", "No Bills Found"};

    /**
     * Creates a new instance of BillingBean
     */
    public BillingBean() {

        showSeacrhResult = false;
    }

    @PostConstruct
    public void init() {
        if (showSeacrhResult == null) {
            showSeacrhResult = Boolean.FALSE;
        }

        if (prop == null && loginBean != null && loginBean.getPerson() != null) {
            emailSender = new EmailSender(loginBean.getPerson().getOrganization().getId());
            prop = emailSender.getProperties();
        }

    }

    public void initBillBillsDetails() {
        if (billId > 0) {
            Map<String, String> criteria = new HashMap<>();
            criteria.put("bill_id", billId + "");
            List<PrintedBills> bills = billsService.fetchAllBillsForProcessing(criteria);

            if (bills != null) {
                selectedBill = bills.get(0);

                billsPayment = billsService.fetchAllBillsPaymentByBillId(selectedBill.getId(), null);
                deliveryFiles = billsService.fetchBillsDeliveryFilesByBillId(selectedBill.getId());
            } else {
                selectedBill = null;
                deliveryFiles = null;
            }
        }
    }

    public void exportMonthDuplicatePayment() {
        if (selectedMonth != null && !selectedMonth.isEmpty()) {
            String file_path;
            int month = Arrays.asList(leaderApp.getMonthSearchValues()).indexOf(selectedMonth) + 1; //bcos sql months start counting from 1

            file_path = prop.getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/dup_payments";
            File download_folder = new File(file_path);

            if (!download_folder.exists()) {
                download_folder.mkdirs(); //create folder
            }

            dataExport.processMonthlyDuplicatePaymentReport(month, Calendar.getInstance().get(Calendar.YEAR), loginBean.getPerson().getLogindetailId().getId(), file_path);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Data export process has started, you will get an email with the report file when it's done", ""));
            selectedMonth = null;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select month to export", ""));
        }
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    public void processDuplicateBills() {
        String[] accepted_roles = {"ADMINISTRATOR", "APPLICATION ADMINISTRATOR"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            dataLoader.processDuplicateBills(duplicateBillList, loginBean.getPerson().getLogindetailId().getId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Duplicate Payment processing has started, an email will be sent to you once processing"
                    + " is done.", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to perform this action", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void processDuplicatePayments() {
        //
        String[] accepted_roles = {"ADMINISTRATOR"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            dataLoader.processDuplicatePayment(duplicateBillList, loginBean.getPerson().getLogindetailId().getId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Duplicate Payment processing has started, an email will be sent to you once processing"
                    + " is done.", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to perform this action", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void resetPaymentsService() {
        syncDate = null;
    }

    public void initAbcPayments() {
        Document xml_element = AlphaBetaWebServiceClient.fetchAbcDailyPayment((syncDate != null) ? syncDate : new Date());
        NodeList nList = xml_element.getElementsByTagName("OutputData"); //get all the rows
        Date pay_date;
        DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        BigDecimal cr_amount;
        String bank_payment_code, payer_id, full_name, dp_slip, trans_ref, entry_id, appl_date, agency_ref, rev_code, bank_name, bank_acct, channel;
        startSearchDate = null;
        if (nList != null) {
            AlphaBetaWebServiceClient.printDocument(nList.item(0), System.out);
            int length = nList.getLength();
            paymentDto = new ArrayList<>();
            totalAmountPaid = BigDecimal.ZERO;
            for (int i = 0; i < length; i++) {
                if (nList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nList.item(i);
                    bank_payment_code = el.getElementsByTagName("BankPaymentCode").item(0).getTextContent();
                    full_name = el.getElementsByTagName("PayerFullName").item(0).getTextContent();
                    cr_amount = dataProcUtil.parseStringToBigDecimal(el.getElementsByTagName("AmountPaid").item(0).getTextContent());
                    dp_slip = el.getElementsByTagName("ChqDepSlipRef").item(0).getTextContent();
                    trans_ref = el.getElementsByTagName("BankTransRef").item(0).getTextContent();
                    entry_id = el.getElementsByTagName("EntryID").item(0).getTextContent();
                    appl_date = el.getElementsByTagName("AppliedDate").item(0).getTextContent();
                    agency_ref = el.getElementsByTagName("AgencyRef").item(0).getTextContent();
                    rev_code = el.getElementsByTagName("RevCode").item(0).getTextContent();
                    bank_name = el.getElementsByTagName("BankName").item(0).getTextContent();
                    bank_acct = el.getElementsByTagName("BankAcctNo").item(0).getTextContent();
                    channel = el.getElementsByTagName("PmtChannel").item(0).getTextContent();
                    payer_id = el.getElementsByTagName("PayerType").item(0).getTextContent() + "-" + el.getElementsByTagName("PayerID").item(0).getTextContent();
                    try {
                        pay_date = dateFormater.parse(el.getElementsByTagName("PaymentDate").item(0).getTextContent());
                    } catch (ParseException ex) {
                        pay_date = null;
                        Logger.getLogger(BillingBean.class.getName()).log(Level.SEVERE, "Failed to parse date: " + el.getElementsByTagName("PaymentDate").item(0).getTextContent(), ex);
                    }

                    paymentDto.add(new BillsPaymentsDto(null, bank_payment_code, pay_date, payer_id, full_name, cr_amount, dp_slip, trans_ref, entry_id, appl_date, agency_ref, rev_code, null, bank_acct,
                            bank_name, channel, null, null));
                    totalAmountPaid = totalAmountPaid.add(cr_amount);
                }
            }
            recordCount = paymentDto.size();
        }
    }

    public void initPaymentFiles() {
        if (uploadedFiles == null) {
            uploadedFiles = billsService.fetchAllPaymentFiles("PAYMENT FILE");
            recordCount = uploadedFiles.size();
        }
    }

    public void filterPaymentResult() {

        //Boolean is_processed = null, is_duplicate = null;
        Map<String, String> criteria = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        criteria.put("year", cal.get(Calendar.YEAR) + "");

        if (selectedDate != null && !selectedDate.isEmpty() && !selectedDate.equalsIgnoreCase("SELECT DATE RANGE")) {

            Map<String, Date> dateMap = FacesSupportUtil.fetchAndSetStartAndEndDate(selectedDate);
            startSearchDate = dateMap.get("start");
            endSearchDate = dateMap.get("end");

        } else {
            if (FacesSupportUtil.setDateRangeCustomError(selectedDate != null, selectedDate.equalsIgnoreCase("SELECT DATE RANGE"), startSearchDate == null, endSearchDate == null))
                return;
        }

        if (startSearchDate != null && endSearchDate != null) {
            criteria.put("start_payment", shortFormat.format(startSearchDate));
            criteria.put("end_payment", shortFormat.format(endSearchDate));
        }

        if (processedStatus != null) {
            switch (processedStatus) {
                case "Proceesed Payments":
                    criteria.put("processed", "yes");
                    break;
                case "Unprocessed Payments":
                    criteria.put("processed", "no");
                    break;
                case "Duplicate Payment":
                    criteria.put("duplicate", "yes");
                    break;
                case "Bills Found":
                    criteria.put("bill_status", PaymentStatusEnum.MATCHBILL.toString());
                    break;
                case "No Bills Found":
                    criteria.put("bill_status", PaymentStatusEnum.NOMATCHBILL.toString());
                    criteria.remove("year");
                    break;
                default:
                    //is_duplicate = null;
                    //is_processed = null;
                    break;
            }
        }

        if (payerId != null && !payerId.isEmpty()) {
            criteria.put("payer_id", payerId);
        }
        if (propertyId != null && !propertyId.isEmpty()) {
            criteria.put("property_id", propertyId);
        }
        if (receipt != null && !receipt.isEmpty()) {
            criteria.put("receipt", receipt);
        }
        if (selectedLcda != null) {
            criteria.put("lga_name", selectedLcda.getLcdaName());
        }
        if (billDistrict != null) {
            criteria.put("district", billDistrict.getDistrictName());
        }
        if (propertyOwnerName != null && !propertyOwnerName.isEmpty()) {
            criteria.put("payer_name", propertyOwnerName);
        }

        criteria.put("consultant", loginBean.getPerson().getOrganization().getId() + "");
        lazyBillsPayment = new BillsPaymentLazy(loginBean.getPerson().getOrganization().getId(), billsService, criteria, null, null, Boolean.FALSE);

        recordCount = billsService.countTotalPayments(criteria);

    }

    public void resetPaymentsForm() {
        processedStatus = null;
        billDistrict = null;
        selectedLcda = null;
        receipt = null;
        propertyId = null;
        payerId = null;
        showSeacrhResult = false;
        endSearchDate = null;
        startSearchDate = null;
        selectedDate = null;
        propertyOwnerName = null;
        showSeacrhResult = Boolean.FALSE;
        lazyBillsPayment = null;
    }

    public void filterBillsResult() {
        System.out.println("selected values are: " + billDistrict + " " + billStreets);

        Map<String, String> criteria = new HashMap<>();
        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "GEO-CODING OFFICER"};
        String order_dir = (FacesSupportUtil.isUserInRole(accepted_roles)) ? "desc" : "asc";
        String selected_status = (selectedStatus != null) ? PaymentStatusEnum.valueOf(selectedStatus).toString() : null;
        if (selected_status != null) {
            criteria.put("status", selected_status);
        }

        Long consultant = (loginBean.getPerson().getOrganization() != null) ? loginBean.getPerson().getOrganization().getId() : null;
        if (consultant != null) {
            criteria.put("consultant", consultant.toString());
        }
        Boolean is_delivered, has_payment;

        if (selectedDate != null && !selectedDate.isEmpty() && !selectedDate.equalsIgnoreCase("SELECT DATE RANGE")) {

            Map<String, Date> dateMap = FacesSupportUtil.fetchAndSetStartAndEndDate(selectedDate);
            startSearchDate = dateMap.get("start");
            endSearchDate = dateMap.get("end");

        } else if (selectedDate != null && selectedDate.equalsIgnoreCase("SELECT DATE RANGE")) {
            if (FacesSupportUtil.setDateRangeCustomError(selectedDate != null, selectedDate.equalsIgnoreCase("SELECT DATE RANGE"), startSearchDate == null, endSearchDate == null))
                return;
        }

        if (startSearchDate != null) {
            criteria.put("start_payment", shortFormat.format(startSearchDate));
        }

        if (endSearchDate != null) {
            criteria.put("end_payment", shortFormat.format(endSearchDate));
        }

        if (paymentAvailable != null) {
            has_payment = (paymentAvailable.equals("YES"));
            criteria.put("has_payment", has_payment.toString());
        }

        if (isDelivered != null) {
            is_delivered = (isDelivered.equals("YES"));
            criteria.put("is_delivered", is_delivered.toString());
        }

        FacesSupportUtil.setFilterCriteriaArea(billStreets, billDistrict, selectedLcda, criteria);

        if (propertyOwnerName != null && !propertyOwnerName.isEmpty()) {
            criteria.put("owner_name", propertyOwnerName);
        }

        if (bankPaymentCode != null && !bankPaymentCode.isEmpty()) {
            criteria.put("bank_code", bankPaymentCode);
        }

        criteria.put("deleted", "false");
        List<String> sorting = sortString.getTarget();

        if ((propertyId != null) && (!propertyId.isEmpty())) { //property ID is the most important variable
            printedBills = propsService.findPrintedBillsByPropertyId(propertyId);
            if (printedBills != null && printedBills.size() > 0) {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("viewBillDetails.xhtml?billPayId=" + printedBills.get(0).getId());
                } catch (IOException ex) {
                    Logger.getLogger(BillingBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot find any bill with notice number: " + propertyId, ""));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            }
        } else { //street is more important that other variables
            lazyPrintedBills = new PrintedBillsLazy(consultant, billsService, criteria, sorting, order_dir, Boolean.FALSE);
            //printedBills = billsService.fetchPrintedBillsFilter(criteria, resultIndex, 0, order_dir);
            recordCount = billsService.countFilteredBillResults(criteria);
        }

    }

    public void resetBillsForm() {
        showSeacrhResult = false;
        printedBills = null;
        lazyPrintedBills = null;
    }

    public void initBillPaymentDetails() {
        if (billId > 0) {
            Map<String, String> criteria = new HashMap<>();
            criteria.put("payment_id", billId + "");
            billsPayment = billsService.fetchPaymentsSearchResult(criteria);

            paymentData = billsPayment.get(0); //there should always be @ least 1 element in this list
            if (paymentData.getPrintedBillId() != null) {
                selectedBill = propsService.findPrintedBillsById(paymentData.getPrintedBillId().getId());
                deliveryFiles = billsService.fetchBillsDeliveryFilesByBillId(paymentData.getPrintedBillId().getId());
                propertyId = null;
            } else {
                selectedBill = null;
                deliveryFiles = null;
            }
        }
    }

    public void processBillPayments() {
        String[] accepted_roles = {"ADMINISTRATOR"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            paymentProcs.processBillsPayment(loginBean.getPerson().getLogindetailId().getId(), null, null, null);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Payment processing has started, an email will be sent to you once processing is done.", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to perform this action", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void initDuplicateSummary() {
        duplicateBillList = billsService.fetchDuplicatePayments(null, Boolean.FALSE);
        duplicatesCount = duplicateBillList.size();
    }

    public void initDuplicateBillsSummary() {
        Calendar cal = Calendar.getInstance();
        duplicateBillList = billsService.fetchDuplicateBillsSummary(loginBean.getPerson().getOrganization().getId(), cal.get(Calendar.YEAR) + "");
        duplicatesCount = duplicateBillList.size();
    }

    public void initAllBills() {
        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "ADMINISTRATOR", "BILLING", "GEO-CODING OFFICER", "HEAD OF OPERATIONS", "INTERNAL CONTROL"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            Long consultant = null, senate_district = null;
            if (loginBean.getPerson().getOrganization() != null) {
                consultant = loginBean.getPerson().getOrganization().getId();
                senate_district = loginBean.getPerson().getOrganization().getSenatorialDistrictId().getId();
            }

            String view_id = FacesContext.getCurrentInstance().getViewRoot().getViewId();

            if ((lazyPrintedBills == null && !showSeacrhResult) || (!f5detector.getPreviousPage().equals(view_id))) {
                lazyPrintedBills = new PrintedBillsLazy(consultant, billsService, null, null, null, Boolean.FALSE);

                recordCount = billsService.countBillsTotal(consultant, null);
                lcdArea = lcdaService.fetchAllSenatorialDistrictLCDAs(senate_district);
                printedBills = null;
                paymentAvailable = null;
                isDelivered = null;

                if (sortString == null) {
                    List<String> citiesSource = new ArrayList<>();
                    List<String> citiesTarget = new ArrayList<>();

                    citiesSource.add("propertyIdn");
                    citiesSource.add("districtName");
                    citiesSource.add("luc");
                    citiesSource.add("amountDue");
                    citiesSource.add("nextYrBf");
                    citiesSource.add("streetName");
                    citiesSource.add("isDelivered");
                    //citiesSource.add("hasNotDelivered");

                    sortString = new DualListModel<>(citiesSource, citiesTarget);
                }

                //resetBillsForm();
            }

            //showSeacrhResult = false;
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(extContaxt.getRequestContextPath() + "/app/index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UploadManagerBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void initAllPayments() {
        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "ADMINISTRATOR", "BILLING", "HEAD OF OPERATIONS"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            if (lazyBillsPayment == null && !showSeacrhResult) {
                //Long consultant, PrintedBillsService pService, Map<String, String> criteria, List<String> sorting, String order_field, Boolean duplicate_
                Map<String, String> criteria = new HashMap<>();
                criteria.put("consultant", loginBean.getPerson().getOrganization().getId() + "");
                lazyBillsPayment = new BillsPaymentLazy(loginBean.getPerson().getOrganization().getId(), billsService, criteria, null, null, Boolean.FALSE);

                recordCount = billsService.countTotalPayments(criteria);
                showSeacrhResult = false;
            }

        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(extContaxt.getRequestContextPath() + "/app/index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UploadManagerBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void initBillsSummary() {
        String[] accepted_roles = {"ADMINISTRATOR", "APPLICATION ADMINISTRATOR", "ADMINISTRATOR USER", "BILLING", "HEAD OF OPERATIONS"};
        if (!FacesSupportUtil.isUserInRole(accepted_roles)) {
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", "");
            FacesContext.getCurrentInstance().addMessage(null, m);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            /**
             * try {
             * FacesContext.getCurrentInstance().getExternalContext().redirect(extContaxt.getRequestContextPath()
             * + "/app/index.xhtml"); } catch (IOException ex) {
             * Logger.getLogger(AdministratorBean.class.getName()).log(Level.SEVERE,
             * null, ex); }
             */
        }
        if (totalExpectedAmount == null && loginBean.getPerson() != null) {

            Long consultant = null;
            String consultant_name = null;
            if (loginBean.getPerson().getOrganization() != null) {
                consultant = loginBean.getPerson().getOrganization().getId();
                consultant_name = loginBean.getPerson().getOrganization().getOrganzaitionCode();
            }

            Object[] summaryData = billsService.fetchPrintedBillsSummary(consultant, null);
            Object[] paymentSummary = billsService.fetchPaymentSummary(consultant_name, null);
            totalExpectedAmount = new BigDecimal(summaryData[0].toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
            totalLateAmount = new BigDecimal(summaryData[2].toString());
            totalSecLateAmount = new BigDecimal(summaryData[3].toString());
            totalThirdLateAmount = new BigDecimal(summaryData[4].toString());
            totalLucAmount = new BigDecimal(summaryData[1].toString());
            totalBillsCount = Long.valueOf(summaryData[6].toString());
            deliveryCount = billsService.countTotalDeliveredBill(consultant, null, Boolean.TRUE);
            hasPayment = billsService.countBillPaymentByStatus(null, null, "payments");
            totalAmountPaid = (paymentSummary[0] != null) ? new BigDecimal(paymentSummary[0].toString()) : BigDecimal.ZERO;

            deliveryChart = new PieChartModel();

            deliveryChart.set("Total", totalBillsCount);
            deliveryChart.set("Delivered", deliveryCount);
            deliveryChart.set("Has Payment", hasPayment);

            deliveryChart.setTitle("Operation Chart");
            deliveryChart.setLegendPosition("w");
            //deliveryChart.setFill(false);
            deliveryChart.setShowDataLabels(true);
            deliveryChart.setDiameter(150);

            if ((organizationList == null) || (organizationList.size() <= 0)) {
                organizationList = refService.fetchAllOrganizationsByType(46L); //get all consultants
            }
        }
    }

    public void initDeliverySummary() {
        if (deliveryDate == null) {
            List<Object[]> summaryData = billsService.fetchBillsDeliverySummary(null, null, null);
            List<Object[]> paymentSummary = billsService.fetchBillsPaymentSummary(null, null, null);

            barModel = new BarChartModel();
            ChartSeries days = new ChartSeries();
            ChartSeries pay_count_series = new ChartSeries();
            ChartSeries pay_amount_series = new ChartSeries();
            days.setLabel("Delivery Count");
            pay_count_series.setLabel("Payment Count");
            pay_amount_series.setLabel("Total Amount");

            String amount_;

            for (Object[] row : summaryData) {
                days.set(new DateFormatSymbols().getMonths()[Integer.valueOf(row[1] + "") - 1], Integer.valueOf(row[0] + ""));
                pay_count_series.set("Month " + row[2], Integer.valueOf(row[1] + ""));
            }

            for (Object[] pay_row : paymentSummary) {
                amount_ = pay_row[1] + "";
                amount_ = amount_.substring(0, amount_.indexOf("."));
                pay_amount_series.set(new DateFormatSymbols().getMonths()[Integer.valueOf(pay_row[2] + "") - 1], Integer.valueOf(amount_));
                pay_count_series.set(new DateFormatSymbols().getMonths()[Integer.valueOf(pay_row[2] + "") - 1], Integer.valueOf(pay_row[0] + ""));
            }

            barModel.addSeries(days);
            barModel.addSeries(pay_count_series);
            //barModel.addSeries(pay_amount_series);

            barModel.setTitle("Delivery Chart");
            barModel.setLegendPosition("ne");

            Axis xAxis = barModel.getAxis(AxisType.X);
            xAxis.setLabel("Month");

            Axis yAxis = barModel.getAxis(AxisType.Y);
            yAxis.setLabel("Delivered");
            /**
             * yAxis.setMin(0); yAxis.setMax(200);
             */

        }
    }

    public void resetBillsSummary() {
        organizationList = null;
        totalExpectedAmount = null;
        selectedOrganization = null;
        lcdArea = null;
        selectedLcda = null;

        this.initBillsSummary();
    }

    public void filterBillsSummary() {

        if ((selectedOrganization != null) || (selectedLcda != null)) {

            Object[] summaryData = null;
            Number delivery_count = 0;
            Object[] paymentSummary = null;

            if (selectedLcda != null) {
                summaryData = billsService.fetchPrintedBillsSummary(null, selectedLcda.getLcdaName());
                delivery_count = billsService.countTotalDeliveredBill(null, selectedLcda.getLcdaName(), Boolean.TRUE);
                hasPayment = billsService.countBillPaymentByStatus(null, selectedLcda.getLcdaName(), "payments");
                paymentSummary = billsService.fetchPaymentSummary(null, selectedLcda.getLcdaName());
            } else if (selectedOrganization != null) {
                summaryData = billsService.fetchPrintedBillsSummary(selectedOrganization.getId(), null);
                delivery_count = billsService.countTotalDeliveredBill(selectedOrganization.getId(), null, Boolean.TRUE);
                hasPayment = billsService.countBillPaymentByStatus(selectedOrganization.getId(), null, "payments");
                paymentSummary = billsService.fetchPaymentSummary(selectedOrganization.getOrganzaitionCode(), null);
            }

            if (summaryData != null) {
                totalExpectedAmount = new BigDecimal(summaryData[0].toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
                totalLateAmount = new BigDecimal(summaryData[2].toString());
                totalSecLateAmount = new BigDecimal(summaryData[3].toString());
                totalThirdLateAmount = new BigDecimal(summaryData[4].toString());
                totalLucAmount = new BigDecimal(summaryData[1].toString());
                totalBillsCount = Long.valueOf(summaryData[6].toString());
                deliveryCount = Long.valueOf(delivery_count + "");
                totalAmountPaid = (paymentSummary[0] != null) ? new BigDecimal(paymentSummary[0].toString()) : BigDecimal.ZERO;

                deliveryChart.clear();
                deliveryChart.set("Total", totalBillsCount);
                deliveryChart.set("Delivered", delivery_count);
                deliveryChart.set("Has Payment", hasPayment);

            }
        }
    }

    public void changedConsultant() {

        if (selectedOrganization != null) {
            lcdArea = lcdaService.fetchAllSenatorialDistrictLCDAs(selectedOrganization.getSenatorialDistrictId().getId());
        }
    }

    public void changedLcda() {

        if (selectedLcda != null) {

            if (loginBean.getPerson().getOrganization() != null) { //this will always be true when the user is logged in
                loginBean.setLcdaWards(lcdaService.fetchAllLcdaWardsByLcda(selectedLcda.getId(), loginBean.getPerson().getOrganization().getId()));
            }
        }
    }

    public List<PrintedBillsDto> runStreetAutoComplete(String street_name) {

        List<PrintedBillsDto> billStreet_ = new ArrayList<>();

        if ((street_name != null) && (!street_name.isEmpty()) && (street_name.length() >= 3)) {
            List<Object[]> streetNames = billsService.fetchBillsStreetByName(street_name, null, true);

            if ((streetNames != null) && (streetNames.size() > 0)) {
                for (Object[] bill : streetNames) {
                    //Long bill_id, String prop_id, String street_name, String district, String lga, Date created_
                    billStreet_.add(new PrintedBillsDto(null, null, bill[0].toString(), bill[1].toString(), bill[2].toString(), null));
                }
            }
            return billStreet_;

        }

        return null;
    }

    public List<PrintedBillsDto> runDistrictAutoComplete(String district_name) {

        List<PrintedBillsDto> billDistrict_ = new ArrayList<>();

        if ((district_name != null) && (!district_name.isEmpty()) && district_name.length() > 3) { //we want to start searching after third character is entered
            Map<String, String> criteria = new HashMap<>();
            criteria.put("district", district_name);
            if (selectedLcda != null) {
                criteria.put("lga", selectedLcda.getLcdaName());
            }
            criteria.put("like_search", "yes");

            List<Object[]> districts = billsService.fetchBillsDistrictByName(criteria);

            if ((districts != null) && (districts.size() > 0)) {
                for (Object[] bill : districts) {
                    //Long bill_id, String prop_id, String street_name, String district, String lga, Date created_
                    billDistrict_.add(new PrintedBillsDto(null, null, null, bill[0].toString(), bill[1].toString(), null));
                }
            }
            return billDistrict_;
        }

        return null;
    }

    public void proceeExcelSheet() {
        Logger.getLogger(BillingBean.class.getName()).log(Level.INFO, "type is: " + excelFile.getContentType(), "");
        if ((selectedLoadType != null) && (excelFile != null) && ((excelFile.getContentType().equals("application/excel"))
                || (excelFile.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")))) {

            String download_dir, file_name;

            File upload_folder;
            DateFormat format = new SimpleDateFormat("MMddyyyyHHmmss", Locale.ENGLISH);

            download_dir = prop.getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/payer_id_files";
            File download_folder = new File(download_dir);

            if (!download_folder.exists()) {
                download_folder.mkdirs(); //create folder
            }
            file_name = ApplicationUtility.renameImageFile(excelFile.getFileName(), format.format(new Date()));
            upload_folder = new File(download_dir + "/" + file_name);

            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(upload_folder))) {
                stream.write(excelFile.getContents());
                dataLoader.proceeExcelSheetForPayerId(download_dir, file_name, selectedLoadType, loginBean.getPerson().getLogindetailId().getId());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Payer ID has started processing, you will be notified via email when processing"
                        + " is complete and a file will be provided for download", "Success"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(BillingBean.class.getName()).log(Level.SEVERE, null, ex);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to upload file, please check the file and try again", "Error"));
            } catch (IOException ex) {
                Logger.getLogger(BillingBean.class.getName()).log(Level.SEVERE, null, ex);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to upload file, please check the file and try again", "Error"));
            }

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select a valid excel file", "Error"));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    /**
     * @return the excelFile
     */
    public UploadedFile getExcelFile() {
        return excelFile;
    }

    /**
     * @param excelFile the excelFile to set
     */
    public void setExcelFile(UploadedFile excelFile) {
        this.excelFile = excelFile;
    }

    /**
     * @return the totalExpectedAmount
     */
    public BigDecimal getTotalExpectedAmount() {
        return totalExpectedAmount;
    }

    /**
     * @param totalExpectedAmount the totalExpectedAmount to set
     */
    public void setTotalExpectedAmount(BigDecimal totalExpectedAmount) {
        this.totalExpectedAmount = totalExpectedAmount;
    }

    /**
     * @return the totalLateAmount
     */
    public BigDecimal getTotalLateAmount() {
        return totalLateAmount;
    }

    /**
     * @param totalLateAmount the totalLateAmount to set
     */
    public void setTotalLateAmount(BigDecimal totalLateAmount) {
        this.totalLateAmount = totalLateAmount;
    }

    public BigDecimal getTotalSecLateAmount() {
        return totalSecLateAmount;
    }

    public void setTotalSecLateAmount(BigDecimal totalSecLateAmount) {
        this.totalSecLateAmount = totalSecLateAmount;
    }

    public BigDecimal getTotalThirdLateAmount() {
        return totalThirdLateAmount;
    }

    public void setTotalThirdLateAmount(BigDecimal totalThirdLateAmount) {
        this.totalThirdLateAmount = totalThirdLateAmount;
    }

    public BigDecimal getTotalLucAmount() {
        return totalLucAmount;
    }

    public void setTotalLucAmount(BigDecimal totalLucAmount) {
        this.totalLucAmount = totalLucAmount;
    }

    public long getTotalBillsCount() {
        return totalBillsCount;
    }

    public void setTotalBillsCount(long totalBillsCount_) {
        this.totalBillsCount = totalBillsCount_;
    }

    /**
     * @return the selectedLcda
     */
    public LocalCouncilDevArea getSelectedLcda() {
        return selectedLcda;
    }

    /**
     * @param selectedLcda the selectedLcda to set
     */
    public void setSelectedLcda(LocalCouncilDevArea selectedLcda) {
        this.selectedLcda = selectedLcda;
    }

    /**
     * @return the organizationList
     */
    public List<Organizations> getOrganizationList() {
        return organizationList;
    }

    /**
     * @param organizationList the organizationList to set
     */
    public void setOrganizationList(List<Organizations> organizationList) {
        this.organizationList = organizationList;
    }

    /**
     * @return the selectedOrganization
     */
    public Organizations getSelectedOrganization() {
        return selectedOrganization;
    }

    /**
     * @param selectedOrganization the selectedOrganization to set
     */
    public void setSelectedOrganization(Organizations selectedOrganization) {
        this.selectedOrganization = selectedOrganization;
    }

    public List<LocalCouncilDevArea> getLcdArea() {
        if (lcdArea == null && loginBean.getPerson() != null) {
            lcdArea = lcdaService.fetchAllSenatorialDistrictLCDAs(null);
        }

        return lcdArea;
    }

    public void setLcdArea(List<LocalCouncilDevArea> lcdArea) {
        this.lcdArea = lcdArea;
    }

    /**
     * @return the loginBean
     */
    public LoginBean getLoginBean() {
        return loginBean;
    }

    /**
     * @param loginBean the loginBean to set
     */
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public String getPropertyOwnerName() {
        return propertyOwnerName;
    }

    public void setPropertyOwnerName(String propertyOwnerName) {
        this.propertyOwnerName = propertyOwnerName;
    }

    /**
     * @return the lazyPrintedBills
     */
    public LazyDataModel<PrintedBills> getLazyPrintedBills() {
        return lazyPrintedBills;
    }

    /**
     * @param lazyPrintedBills the lazyPrintedBills to set
     */
    public void setLazyPrintedBills(LazyDataModel<PrintedBills> lazyPrintedBills) {
        this.lazyPrintedBills = lazyPrintedBills;
    }

    /**
     * @return the selectedBill
     */
    public PrintedBills getSelectedBill() {
        return selectedBill;
    }

    /**
     * @param selectedBill the selectedBill to set
     */
    public void setSelectedBill(PrintedBills selectedBill) {
        this.selectedBill = selectedBill;
    }

    /**
     * @return the recordCount
     */
    public long getRecordCount() {
        return recordCount;
    }

    /**
     * @param recordCount the recordCount to set
     */
    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

    /**
     * @return the billStreets
     */
    public PrintedBillsDto getBillStreets() {
        return billStreets;
    }

    /**
     * @param billStreets the billStreets to set
     */
    public void setBillStreets(PrintedBillsDto billStreets) {
        this.billStreets = billStreets;
    }

    /**
     * @return the propertyId
     */
    public String getPropertyId() {
        return propertyId;
    }

    /**
     * @param propertyId the propertyId to set
     */
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    /**
     * @return the selectedStatus
     */
    public String getSelectedStatus() {
        return selectedStatus;
    }

    /**
     * @param selectedStatus the selectedStatus to set
     */
    public void setSelectedStatus(String selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public PaymentStatusEnum[] getPaymentStatus() {
        return PaymentStatusEnum.values();
    }

    /**
     * @return the selectedDate
     */
    public String getSelectedDate() {
        return selectedDate;
    }

    /**
     * @param selectedDate the selectedDate to set
     */
    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    /**
     * @return the startSearchDate
     */
    public Date getStartSearchDate() {
        return startSearchDate;
    }

    /**
     * @param startSearchDate the startSearchDate to set
     */
    public void setStartSearchDate(Date startSearchDate) {
        this.startSearchDate = startSearchDate;
    }

    /**
     * @return the endSearchDate
     */
    public Date getEndSearchDate() {
        return endSearchDate;
    }

    /**
     * @param endSearchDate the endSearchDate to set
     */
    public void setEndSearchDate(Date endSearchDate) {
        this.endSearchDate = endSearchDate;
    }

    /**
     * @return the resultIndex
     */
    public int getResultIndex() {
        return resultIndex;
    }

    /**
     * @param resultIndex the resultIndex to set
     */
    public void setResultIndex(int resultIndex) {
        this.resultIndex = resultIndex;
    }

    /**
     * @return the showSeacrhResult
     */
    public Boolean getShowSeacrhResult() {
        return showSeacrhResult;
    }

    /**
     * @param showSeacrhResult the showSeacrhResult to set
     */
    public void setShowSeacrhResult(Boolean showSeacrhResult) {
        this.showSeacrhResult = showSeacrhResult;
    }

    /**
     * @return the billDistrict
     */
    public PrintedBillsDto getBillDistrict() {
        return billDistrict;
    }

    /**
     * @param billDistrict the billDistrict to set
     */
    public void setBillDistrict(PrintedBillsDto billDistrict) {
        this.billDistrict = billDistrict;
    }

    public String getSelectedStreetName() {
        return selectedStreetName;
    }

    public void setSelectedStreetName(String selectedStreetName) {
        this.selectedStreetName = selectedStreetName;
    }

    public String getSelectedDistrictName() {
        return selectedDistrictName;
    }

    public void setSelectedDistrictName(String selectedDistrictName) {
        this.selectedDistrictName = selectedDistrictName;
    }

    public LazyDataModel<BillPayments> getLazyBillsPayment() {
        return lazyBillsPayment;
    }

    public void setLazyBillsPayment(LazyDataModel<BillPayments> lazyBillsPayment) {
        this.lazyBillsPayment = lazyBillsPayment;
    }

    /**
     * @return the duplicatesCount
     */
    public long getDuplicatesCount() {
        return duplicatesCount;
    }

    /**
     * @param duplicatesCount the duplicatesCount to set
     */
    public void setDuplicatesCount(long duplicatesCount) {
        this.duplicatesCount = duplicatesCount;
    }

    /**
     * @return the duplicateBillList
     */
    public List<Object[]> getDuplicateBillList() {
        return duplicateBillList;
    }

    /**
     * @param duplicateBillList the duplicateBillList to set
     */
    public void setDuplicateBillList(List<Object[]> duplicateBillList) {
        this.duplicateBillList = duplicateBillList;
    }

    /**
     * @return the deliveryCount
     */
    public long getDeliveryCount() {
        return deliveryCount;
    }

    /**
     * @param deliveryCount the deliveryCount to set
     */
    public void setDeliveryCount(long deliveryCount) {
        this.deliveryCount = deliveryCount;
    }

    /**
     * @return the deliveryChart
     */
    public PieChartModel getDeliveryChart() {
        return deliveryChart;
    }

    /**
     * @param deliveryChart the deliveryChart to set
     */
    public void setDeliveryChart(PieChartModel deliveryChart) {
        this.deliveryChart = deliveryChart;
    }

    /**
     * @return the billId
     */
    public long getBillId() {
        return billId;
    }

    /**
     * @param billId the billId to set
     */
    public void setBillId(long billId) {
        this.billId = billId;
    }

    /**
     * @return the billsPayment
     */
    public List<BillPayments> getBillsPayment() {
        return billsPayment;
    }

    /**
     * @param billsPayment the billsPayment to set
     */
    public void setBillsPayment(List<BillPayments> billsPayment) {
        this.billsPayment = billsPayment;
    }

    /**
     * @return the deliveryFiles
     */
    public List<BillsDeliveryFiles> getDeliveryFiles() {
        return deliveryFiles;
    }

    /**
     * @param deliveryFiles the deliveryFiles to set
     */
    public void setDeliveryFiles(List<BillsDeliveryFiles> deliveryFiles) {
        this.deliveryFiles = deliveryFiles;
    }

    /**
     * @return the hasPayment
     */
    public long getHasPayment() {
        return hasPayment;
    }

    /**
     * @param hasPayment the hasPayment to set
     */
    public void setHasPayment(long hasPayment) {
        this.hasPayment = hasPayment;
    }

    public BigDecimal getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    /**
     * @return the printedBills
     */
    public List<PrintedBills> getPrintedBills() {
        return printedBills;
    }

    /**
     * @param printedBills the printedBills to set
     */
    public void setPrintedBills(List<PrintedBills> printedBills) {
        this.printedBills = printedBills;
    }

    /**
     * @return the data__load_type
     */
    public String[] getData__load_type() {
        return data__load_type;
    }

    /**
     * @param data__load_type the data__load_type to set
     */
    public void setData__load_type(String[] data__load_type) {
        this.data__load_type = data__load_type;
    }

    /**
     * @return the selectedLoadType
     */
    public String getSelectedLoadType() {
        return selectedLoadType;
    }

    /**
     * @param selectedLoadType the selectedLoadType to set
     */
    public void setSelectedLoadType(String selectedLoadType) {
        this.selectedLoadType = selectedLoadType;
    }

    public String getPaymentAvailable() {
        return paymentAvailable;
    }

    public void setPaymentAvailable(String paymentAvailable) {
        this.paymentAvailable = paymentAvailable;
    }

    public String getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(String isDelivered) {
        this.isDelivered = isDelivered;
    }

    /**
     * @return the payerId
     */
    public String getPayerId() {
        return payerId;
    }

    /**
     * @param payerId the payerId to set
     */
    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    /**
     * @return the receipt
     */
    public String getReceipt() {
        return receipt;
    }

    /**
     * @param receipt the receipt to set
     */
    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    /**
     * @return the uploadedFiles
     */
    public List<FileUploads> getUploadedFiles() {
        return uploadedFiles;
    }

    /**
     * @param uploadedFiles the uploadedFiles to set
     */
    public void setUploadedFiles(List<FileUploads> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public String getProcessedStatus() {
        return processedStatus;
    }

    public void setProcessedStatus(String processedStatus) {
        this.processedStatus = processedStatus;
    }

    public String[] getPaymentProcess() {
        return paymentProcess;
    }

    public void setPaymentProcess(String[] paymentProcess) {
        this.paymentProcess = paymentProcess;
    }

    /**
     * @return the processChart
     */
    public LineChartModel getProcessChart() {
        return processChart;
    }

    /**
     * @param processChart the processChart to set
     */
    public void setProcessChart(LineChartModel processChart) {
        this.processChart = processChart;
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
     * @return the syncDate
     */
    public Date getSyncDate() {
        return syncDate;
    }

    /**
     * @param syncDate the syncDate to set
     */
    public void setSyncDate(Date syncDate) {
        this.syncDate = syncDate;
    }

    /**
     * @return the dayCount
     */
    public long getDayCount() {
        return dayCount;
    }

    /**
     * @param dayCount the dayCount to set
     */
    public void setDayCount(long dayCount) {
        this.dayCount = dayCount;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public void setBarModel(BarChartModel barModel) {
        this.barModel = barModel;
    }

    public List<BillsPaymentsDto> getPaymentDto() {
        return paymentDto;
    }

    public void setPaymentDto(List<BillsPaymentsDto> paymentDto) {
        this.paymentDto = paymentDto;
    }

    /**
     * @return the selectedMonth
     */
    public String getSelectedMonth() {
        return selectedMonth;
    }

    /**
     * @param selectedMonth the selectedMonth to set
     */
    public void setSelectedMonth(String selectedMonth) {
        this.selectedMonth = selectedMonth;
    }

    /**
     * @return the sortString
     */
    public DualListModel<String> getSortString() {
        return sortString;
    }

    /**
     * @param sortString the sortString to set
     */
    public void setSortString(DualListModel<String> sortString) {
        this.sortString = sortString;
    }

    public F5Detector getF5detector() {
        return f5detector;
    }

    public void setF5detector(F5Detector f5detector) {
        this.f5detector = f5detector;
    }

    /**
     * @return the paymentData
     */
    public BillPayments getPaymentData() {
        return paymentData;
    }

    /**
     * @param paymentData the paymentData to set
     */
    public void setPaymentData(BillPayments paymentData) {
        this.paymentData = paymentData;
    }

    public String getBankPaymentCode() {
        return bankPaymentCode;
    }

    public void setBankPaymentCode(String bankPaymentCode) {
        this.bankPaymentCode = bankPaymentCode;
    }
}
