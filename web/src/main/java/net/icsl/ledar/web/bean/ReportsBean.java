/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.bean;

import java.io.File;
import java.text.DateFormat;
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

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import net.icsl.ledar.ejb.dto.GenericModelDto;
import net.icsl.ledar.ejb.dto.PrintedBillsDto;

import org.primefaces.model.LazyDataModel;

import net.icsl.ledar.ejb.enums.UserActitiyName;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.PrintedBills;
import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.model.WardLandProperties;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.ejb.util.DataExportProcessor;
import net.icsl.ledar.ejb.util.EmailSender;
import net.icsl.ledar.web.lazyModel.PrintedBillsLazy;
import net.icsl.ledar.web.util.FacesSupportUtil;

/**
 *
 * @author sfagade
 */
@Named("reportsBean")
@RequestScoped
public class ReportsBean {

    @Inject
    private FacesContext facesContext;

    @Inject
    private PrintedBillsService billsService;
    @Inject
    private RegisteredUsersDataService regUserService;
    @Inject
    private LcdaWardsDataServices lcdaService;
    @Inject
    private LcdaWardsDataServices lcdaWardService;

    //@Inject
    //private WardPropertiesDao propertiesDao;
    @Inject
    private EmailSender emailSender;
    @Inject
    private DataExportProcessor dataExport;
    @Inject
    private LoginBean loginBean;

    private LocalCouncilDevArea selectedLcda;
    private PrintedBillsDto billStreets, billDistrict;
    private List<UsersLastActivities> usersActivities;
    private List<WardLandProperties> landProperties;
    private List<GenericModelDto> deliveriesModel;
    private LazyDataModel<PrintedBills> printedBillsLazy, printedBillsWithoutLazy, deliveredBills;
    private List<Logindetails> allFieldOfficers;

    private PrintedBills selectedBill;
    private Logindetails selectedUser;

    private Properties prop;
    private Map<String, String> filterCriteria;
    private int recordCount;
    private String selectedMapType, selectedRange;
    private Date startSearchDate, endSearchDate;

    private final DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private Boolean isFiltered;

    /**
     * Creates a new instance of ReportsBean
     */
    public ReportsBean() {

    }

    @PostConstruct
    public void init() {
        String view_id = facesContext.getViewRoot().getViewId();

        if (prop == null) {
            emailSender = new EmailSender(loginBean.getPerson().getOrganization().getId());
        }
        prop = emailSender.getProperties();

        switch (view_id) {
            case "/app/reports/propertyChanged.xhtml":
                if (loginBean.getuRoles().getAuthenticationRoleId().getRoleName().equals("FIELD CO-ORDINATOR")) {
                    //propBills = landPropService.fetchPropertyBillByStatus(4L);
                }
                break;
            case "/app/internalControl/fieldOfficers.xhtml":
                String[] role_activities = {UserActitiyName.MOBILELOGIN.toString(), UserActitiyName.SAVEDPOD.toString(), UserActitiyName.SAVEDENUMERATION.toString()};
                usersActivities = regUserService.fetchAllRecentActivitiesByName(Arrays.asList(role_activities), 9L);
                recordCount = usersActivities.size();
                break;
            case "/app/reports/property/irregularStreetProperties.xhtml": //TODO this block is no longer needed
                String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "GEO-CODING OFFICER", "DATA ENTRY"};
                if (FacesSupportUtil.isUserInRole(accepted_roles)) {
                    setLandProperties(lcdaService.fetchAllIrregularStreetProperties(loginBean.getPerson().getOrganization().getId()));
                    recordCount = landProperties.size();

                } else {
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", ""));
                    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                }
                break;
        }
    }

    public void resetDeliveredBillsForm() {
        selectedRange = null;
        startSearchDate = null;
        endSearchDate = null;
        billStreets = null;
        billDistrict = null;
        selectedLcda = null;
        this.selectedUser = null;
        isFiltered = Boolean.FALSE;
    }

    public void filterDeliveredBillsResult() {
        System.out.println("selected values are: " + billDistrict + " " + billStreets);
        filterCriteria = new HashMap<>();
        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "GEO-CODING OFFICER"};
        String order_dir = (FacesSupportUtil.isUserInRole(accepted_roles)) ? "desc" : "asc";

        if (selectedRange != null && !selectedRange.isEmpty() && !selectedRange.equalsIgnoreCase("SELECT DATE RANGE")) {

            Map<String, Date> dateMap = FacesSupportUtil.fetchAndSetStartAndEndDate(selectedRange);
            startSearchDate = dateMap.get("start");
            endSearchDate = dateMap.get("end");

        } else if (selectedRange != null && selectedRange.equalsIgnoreCase("SELECT DATE RANGE")) {
            if (FacesSupportUtil.setDateRangeCustomError(selectedRange != null, selectedRange.equalsIgnoreCase("SELECT DATE RANGE"), startSearchDate == null, endSearchDate == null)) {
                return;
            }
        }

        if (startSearchDate != null) {
            filterCriteria.put("delivery_start", shortFormat.format(startSearchDate));
        }

        if (endSearchDate != null) {
            filterCriteria.put("delivery_end", shortFormat.format(endSearchDate));
        }

        //filterCriteria.put("update_required", "Yes");
        List<String> sortFields = new ArrayList<>();
        sortFields.add("deliveryDate");
        sortFields.add("updateStatus");

        Long consultant = (loginBean.getPerson().getOrganization() != null) ? loginBean.getPerson().getOrganization().getId() : null;
        if (consultant != null) {
            filterCriteria.put("consultant", consultant.toString());
        }

        FacesSupportUtil.setFilterCriteriaArea(billStreets, billDistrict, selectedLcda, filterCriteria);

        if (selectedUser != null) {
            filterCriteria.put("delivery_officer", selectedUser.getId() + "");
        }

        filterCriteria.put("deleted", "false");

        printedBillsWithoutLazy = new PrintedBillsLazy(consultant, billsService, filterCriteria, sortFields, order_dir, Boolean.FALSE);
        isFiltered = Boolean.TRUE;
        recordCount = (int) billsService.countFilteredBillResults(filterCriteria);
    }

    public void exportDeliveryGpsData() {
        String file_path;

        file_path = prop.getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/gps_files";
        File download_folder = new File(file_path);

        if (!download_folder.exists()) {
            download_folder.mkdirs(); //create folder
        }

        if (filterCriteria == null) {
            filterCriteria = new HashMap<>();
        }

        filterCriteria.put("is_delivered", "true");
        filterCriteria.put("deleted", "false");
        filterCriteria.put("year", Calendar.getInstance().get(Calendar.YEAR) + "");
        filterCriteria.put("latitude_empty", "false");

        dataExport.processDeliveredBillsExportRecords(filterCriteria, loginBean.getPerson().getLogindetailId().getId(), file_path, Boolean.FALSE);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Data export process has started, you will get an email with the report file when it's done", ""));

        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    public void exportBillsData(String payment_status) {

        if (payment_status != null) {

            String file_path;
            int month = - 1; //bcos sql months start counting from 1

            file_path = prop.getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/biils_export";
            File download_folder = new File(file_path);

            if (!download_folder.exists()) {
                download_folder.mkdirs(); //create folder
            }

            dataExport.processBillPaymentStatus(Calendar.getInstance().get(Calendar.YEAR), loginBean.getPerson().getLogindetailId().getId(), payment_status, file_path);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Export process has started, you will receive an email with the generated file attached when"
                    + " this process is done", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unknown request param", ""));

        }
    }

    public void initBillsReportView() {
        Long consultant;
        String view_id = facesContext.getViewRoot().getViewId();
        consultant = loginBean.getPerson().getOrganization().getId();
        List<String> order = new ArrayList<>();
        if (isFiltered == null || !isFiltered) {
            filterCriteria = new HashMap<>();
            switch (view_id) {
                case "/app/reports/bills/billsWithPayments.xhtml":
                    filterCriteria.put("has_payment", "true");
                    order.add("districtName");
                    setPrintedBillsLazy(new PrintedBillsLazy(consultant, billsService, filterCriteria, order, null, Boolean.FALSE));
                    recordCount = billsService.countBillsTotal(consultant, "has payment");
                    break;
                case "/app/reports/bills/billsWithoutPayments.xhtml":
                    filterCriteria.put("has_payment", "false");
                    order.add("lga");
                    printedBillsWithoutLazy = new PrintedBillsLazy(consultant, billsService, filterCriteria, order, null, Boolean.FALSE);
                    recordCount = billsService.countBillsTotal(consultant, "no payment");
                    break;
                case "/app/reports/bills/deliveredBills.xhtml":

                    if (allFieldOfficers == null) {
                        Long[] field_roles = {9L, 19L, 26L}; //all field officers ID?
                        setAllFieldOfficers(regUserService.fetchAllLcdaChairmen(Arrays.asList(field_roles), consultant));
                    }

                    filterCriteria.put("is_delivered", "true");
                    filterCriteria.put("deleted", "false");
                    order.add("deliveryDate");
                    deliveredBills = new PrintedBillsLazy(consultant, billsService, filterCriteria, order, null, Boolean.FALSE);
                    recordCount = (int) billsService.countFilteredBillResults(filterCriteria);
                    break;
                default:
                    break;
            }
        }

    }

    public void initBillsDelivered() {
        Long consultant;
        Map<String, String> criteria = new HashMap<>();
        List<String> order = new ArrayList<>();

        if (printedBillsWithoutLazy == null) {
            consultant = loginBean.getPerson().getOrganization().getId();
            criteria.put("is_delivered", "true");
            criteria.put("deleted", "false");
            order.add("luc");
            printedBillsWithoutLazy = new PrintedBillsLazy(consultant, billsService, criteria, order, null, Boolean.FALSE);
            recordCount = billsService.countBillsTotal(consultant, null);
        }
    }

    public void changedLcda() {
        System.out.println("called changeLcda: " + selectedLcda);
        if (selectedLcda != null) {
            loginBean.setLcdaWards(lcdaWardService.fetchAllLcdaWardsByLcda(selectedLcda.getId(), null));
        }
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

    /**
     * @return the usersActivities
     */
    public List<UsersLastActivities> getUsersActivities() {
        return usersActivities;
    }

    /**
     * @param usersActivities the usersActivities to set
     */
    public void setUsersActivities(List<UsersLastActivities> usersActivities) {
        this.usersActivities = usersActivities;
    }

    /**
     * @return the recordCount
     */
    public int getRecordCount() {
        return recordCount;
    }

    /**
     * @param recordCount the recordCount to set
     */
    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    /**
     * @return the landProperties
     */
    public List<WardLandProperties> getLandProperties() {
        return landProperties;
    }

    /**
     * @param landProperties the landProperties to set
     */
    public void setLandProperties(List<WardLandProperties> landProperties) {
        this.landProperties = landProperties;
    }

    /**
     * @return the printedBillsLazy
     */
    public LazyDataModel<PrintedBills> getPrintedBillsLazy() {
        return printedBillsLazy;
    }

    /**
     * @param printedBillsLazy the printedBillsLazy to set
     */
    public void setPrintedBillsLazy(LazyDataModel<PrintedBills> printedBillsLazy) {
        this.printedBillsLazy = printedBillsLazy;
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

    public LazyDataModel<PrintedBills> getPrintedBillsWithoutLazy() {
        return printedBillsWithoutLazy;
    }

    public void setPrintedBillsWithoutLazy(LazyDataModel<PrintedBills> printedBillsWithoutLazy) {
        this.printedBillsWithoutLazy = printedBillsWithoutLazy;
    }

    /**
     * @return the deliveredBills
     */
    public LazyDataModel<PrintedBills> getDeliveredBills() {
        return deliveredBills;
    }

    /**
     * @param deliveredBills the deliveredBills to set
     */
    public void setDeliveredBills(LazyDataModel<PrintedBills> deliveredBills) {
        this.deliveredBills = deliveredBills;
    }

    public String getSelectedMapType() {
        return selectedMapType;
    }

    public void setSelectedMapType(String selectedMapType) {
        this.selectedMapType = selectedMapType;
    }

    /**
     * @return the deliveriesModel
     */
    public List<GenericModelDto> getDeliveriesModel() {
        return deliveriesModel;
    }

    /**
     * @param deliveriesModel the deliveriesModel to set
     */
    public void setDeliveriesModel(List<GenericModelDto> deliveriesModel) {
        this.deliveriesModel = deliveriesModel;
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
     * @return the selectedRange
     */
    public String getSelectedRange() {
        return selectedRange;
    }

    /**
     * @param selectedRange the selectedRange to set
     */
    public void setSelectedRange(String selectedRange) {
        this.selectedRange = selectedRange;
    }

    public Logindetails getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(Logindetails selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<Logindetails> getAllFieldOfficers() {
        return allFieldOfficers;
    }

    public void setAllFieldOfficers(List<Logindetails> allFieldOfficers) {
        this.allFieldOfficers = allFieldOfficers;
    }
}
