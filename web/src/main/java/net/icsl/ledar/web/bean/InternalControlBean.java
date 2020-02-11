/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.bean;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.icsl.ledar.ejb.dto.GenericModelDto;
import net.icsl.ledar.ejb.dto.PrintedBillsDto;
import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.PrintedBills;
import net.icsl.ledar.ejb.model.WardLandProperties;

import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.web.lazyModel.PrintedBillsLazy;
import net.icsl.ledar.web.util.FacesSupportUtil;
import org.primefaces.model.LazyDataModel;

/**
 * @author sfagade
 */
@Named("internalCtrlBean")
@SessionScoped
public class InternalControlBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private LandPropertiesDataService landPropService;
    @Inject
    private LcdaWardsDataServices lcdaWardService;
    @Inject
    private PrintedBillsService billsService;
    @Inject
    private RegisteredUsersDataService registeredService;

    @Inject
    private LoginBean loginBean;
    @Inject
    private LedarApplicationBean ledarApp;
    @Inject
    private F5Detector f5detector;

    private final DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private WardStreets selectedStreet;
    private LcdaWards selected_ward;
    private LocalCouncilDevArea selected_lcda;
    private Logindetails selectedEntryOfficer;
    private PrintedBills selectedBill;
    private PrintedBillsDto billStreets, billDistrict;

    private List<Logindetails> allScanOfficers;
    private List<WardLandProperties> wardProperties;
    private List<GenericModelDto> propertiesList;
    private List<PrintedBills> printedBillsList;
    private LazyDataModel<PrintedBills> lazyUpdateProperties;
    private List<LocalCouncilDevArea> lcdArea;

    private String[] dateRangeValues = {"Today", "Yesterday", "Past Week", "Past Month", "Last Quarter", "Select Date Range"};

    private String searchPropId, selectedRange, deliveryDate;
    private boolean likeSearch, isFiltered;
    private Date dataEntryDate, startDate, endDate;
    private long recordsCount;
    private Long officerId;

    @PostConstruct
    public void init() {

        FacesMessage msg_;

        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "INTERNAL CONTROL", "GEO-CODING OFFICER", "HEAD OF OPERATIONS"};

        if (!FacesSupportUtil.isUserInRole(accepted_roles)) {
            msg_ = new FacesMessage(FacesMessage.SEVERITY_INFO, "", " has been assigned to field officer");
            FacesContext.getCurrentInstance().addMessage("dash_form:hmsgs", msg_);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/app/index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(InternalControlBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        isFiltered = Boolean.FALSE;

    }

    public void deliveryOfficerBills() {
        if (officerId != null) {
            Map<String, String> criteria = new HashMap<>();
            criteria.put("delivery_start", shortFormat.format(startDate));
            criteria.put("delivery_end", shortFormat.format(endDate));
            criteria.put("delivery_officer", officerId + "");

            printedBillsList = billsService.fetchPrintedBillsFilter(criteria, 0, 0, null, null);
            recordsCount = billsService.countFilteredBillResults(criteria);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ".", "Required params missing"));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("deliveryActivities.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(InternalControlBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void filterBillsResult() {
        System.out.println("selected values are: " + billDistrict + " " + billStreets);
        Map<String, String> criteria = new HashMap<>();
        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "GEO-CODING OFFICER"};
        String order_dir = (FacesSupportUtil.isUserInRole(accepted_roles)) ? "desc" : "asc";

        if (selectedRange != null && !selectedRange.isEmpty() && !selectedRange.equalsIgnoreCase("SELECT DATE RANGE")) {

            Map<String, Date> dateMap = FacesSupportUtil.fetchAndSetStartAndEndDate(selectedRange);
            startDate = dateMap.get("start");
            endDate = dateMap.get("end");

        } else if (selectedRange != null && selectedRange.equalsIgnoreCase("SELECT DATE RANGE")) {

            if (FacesSupportUtil.setDateRangeCustomError(selectedRange != null, selectedRange.equalsIgnoreCase("SELECT DATE RANGE"), startDate == null, startDate == null)) {
                return;
            }

        }

        if (startDate != null) {
            criteria.put("delivery_start", shortFormat.format(startDate));
        }

        if (endDate != null) {
            criteria.put("delivery_end", shortFormat.format(endDate));
        }

        criteria.put("update_required", "Yes");
        List<String> sortFields = new ArrayList<>();
        sortFields.add("deliveryDate");
        sortFields.add("updateStatus");

        Long consultant = (loginBean.getPerson().getOrganization() != null) ? loginBean.getPerson().getOrganization().getId() : null;
        if (consultant != null) {
            criteria.put("consultant", consultant.toString());
        }

        FacesSupportUtil.setFilterCriteriaArea(billStreets, billDistrict, selected_lcda, criteria);

        criteria.put("deleted", "false");
        lazyUpdateProperties = null;
        lazyUpdateProperties = new PrintedBillsLazy(consultant, billsService, criteria, sortFields, order_dir, Boolean.FALSE);
        isFiltered = Boolean.TRUE;
        recordsCount = billsService.countFilteredBillResults(criteria);
    }

    public void resetBillsForm() {
        billStreets = null;
        billDistrict = null;
        isFiltered = Boolean.FALSE;
        startDate = null;
        selectedRange = null;
        endDate = null;
    }

    public void initPropertyUpdate() {
        Long consultant = null, senate_district = null;
        if (loginBean.getPerson().getOrganization() != null) {
            consultant = loginBean.getPerson().getOrganization().getId();
            senate_district = loginBean.getPerson().getOrganization().getSenatorialDistrictId().getId();
        }
        //String view_id = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        Map<String, String> criteria = new HashMap<>();
        criteria.put("update_required", "Yes");
        List<String> sortFields = new ArrayList<>();
        sortFields.add("deliveryDate");
        sortFields.add("updateStatus");
        if (!isFiltered) {
            //if ((getLazyUpdateProperties() == null) || (!f5detector.getPreviousPage().equals(view_id))) {
            setLazyUpdateProperties(new PrintedBillsLazy(consultant, billsService, criteria, sortFields, null, Boolean.FALSE));
            recordsCount = billsService.countFilteredBillResults(criteria);
        }
        //}
    }

    public void deliveryReport() {

        if (startDate == null || endDate == null) {
            Calendar cal = Calendar.getInstance();
            try {
                startDate = shortFormat.parse(FacesSupportUtil.getTodayWithoutTime());
                cal.add(Calendar.DAY_OF_MONTH, 1); //tomorrow's date
                endDate = cal.getTime();
            } catch (ParseException ex) {
                Logger.getLogger(InternalControlBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        propertiesList = landPropService.fetchDailyDeliveryReport(startDate, endDate);

    }

    public void initDailyReportBean() {
        if (propertiesList == null || propertiesList.size() <= 0) {
            propertiesList = landPropService.fetchDailyEnumerationReport();
        }
    }

    public void initOfficerDelivery() {
        String param_query = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ldtid");
        Long login_id = Long.valueOf(param_query);
        Calendar cal = Calendar.getInstance();

        Map<String, String> criteria = new HashMap<>();
        criteria.put("year", cal.get(Calendar.YEAR) + "");
        criteria.put("captured_by", login_id + "");

        wardProperties = landPropService.filterWardLandPropertyList(criteria);
        if (wardProperties != null) {
            recordsCount = wardProperties.size();
        }
    }

    public void filterDeliveryReport() {
        if (selectedRange != null) { //this is about to get real
            Map<String, Date> dateMap = FacesSupportUtil.fetchAndSetStartAndEndDate(selectedRange);
            startDate = dateMap.get("start");
            endDate = dateMap.get("end");

        }
    }

    /**
     * Creates a new instance of InternalControlBean
     */
    public InternalControlBean() {
    }

    public void changedLcda() {
        System.out.println("called changeLcda: " + selected_lcda);
        if (selected_lcda != null) {
            loginBean.setLcdaWards(lcdaWardService.fetchAllLcdaWardsByLcda(selected_lcda.getId(), null));
        }
    }

    /**
     * public void changedWardStreets() {
     * <p>
     * if (selected_ward != null) {
     * loginBean.setWardStreets(lcdaWardService.fetchWardStreets(selected_ward.getId(),
     * null, Boolean.TRUE)); } }
     */
    public void loadAllDataEntryOfficers() {
        if (allScanOfficers == null) {
            Long[] field_roles = {25L};
            setAllScanOfficers(registeredService.fetchAllLcdaChairmen(Arrays.asList(field_roles), loginBean.getPerson().getOrganization().getId()));
        }
    }

    public void loadAllScanningOfficers() {
        if (allScanOfficers == null) {
            Long[] field_roles = {20L};
            setAllScanOfficers(registeredService.fetchAllLcdaChairmen(Arrays.asList(field_roles), loginBean.getPerson().getOrganization().getId()));
        }
    }

    /**
     * @return the searchPropId
     */
    public String getSearchPropId() {
        return searchPropId;
    }

    /**
     * @param searchPropId the searchPropId to set
     */
    public void setSearchPropId(String searchPropId) {
        this.searchPropId = searchPropId;
    }

    public Logindetails getSelectedEntryOfficer() {
        return selectedEntryOfficer;
    }

    public void setSelectedEntryOfficer(Logindetails selectedEntryOfficer) {
        this.selectedEntryOfficer = selectedEntryOfficer;
    }

    /**
     * @return the selectedStreet
     */
    public WardStreets getSelectedStreet() {
        return selectedStreet;
    }

    /**
     * @param selectedStreet the selectedStreet to set
     */
    public void setSelectedStreet(WardStreets selectedStreet) {
        this.selectedStreet = selectedStreet;
    }

    /**
     * @return the selected_ward
     */
    public LcdaWards getSelected_ward() {
        return selected_ward;
    }

    /**
     * @param selected_ward the selected_ward to set
     */
    public void setSelected_ward(LcdaWards selected_ward) {
        this.selected_ward = selected_ward;
    }

    public Date getDataEntryDate() {
        return dataEntryDate;
    }

    public void setDataEntryDate(Date dataEntryDate) {
        this.dataEntryDate = dataEntryDate;
    }

    /**
     * @return the selected_lcda
     */
    public LocalCouncilDevArea getSelected_lcda() {
        return selected_lcda;
    }

    /**
     * @param selected_lcda the selected_lcda to set
     */
    public void setSelected_lcda(LocalCouncilDevArea selected_lcda) {
        this.selected_lcda = selected_lcda;
    }

    /**
     * @return the likeSearch
     */
    public boolean isLikeSearch() {
        return likeSearch;
    }

    /**
     * @param likeSearch the likeSearch to set
     */
    public void setLikeSearch(boolean likeSearch) {
        this.likeSearch = likeSearch;
    }

    /**
     * @return the recordsCount
     */
    public long getRecordsCount() {
        return recordsCount;
    }

    /**
     * @param recordsCount the recordsCount to set
     */
    public void setRecordsCount(long recordsCount) {
        this.recordsCount = recordsCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public String[] getDateRangeValues() {
        return dateRangeValues;
    }

    public void setDateRangeValues(String[] dateRangeValues) {
        this.dateRangeValues = dateRangeValues;
    }

    public PrintedBills getSelectedBill() {
        return selectedBill;
    }

    public void setSelectedBill(PrintedBills selectedBill) {
        this.selectedBill = selectedBill;
    }

    public List<Logindetails> getAllScanOfficers() {
        return allScanOfficers;
    }

    public void setAllScanOfficers(List<Logindetails> allScanOfficers) {
        this.allScanOfficers = allScanOfficers;
    }

    /**
     * @return the ledarApp
     */
    public LedarApplicationBean getLedarApp() {
        return ledarApp;
    }

    /**
     * @param ledarApp the ledarApp to set
     */
    public void setLedarApp(LedarApplicationBean ledarApp) {
        this.ledarApp = ledarApp;
    }

    public String getSelectedRange() {
        return selectedRange;
    }

    public void setSelectedRange(String selectedRange) {
        this.selectedRange = selectedRange;
    }

    /**
     * @return the propertiesList
     */
    public List<GenericModelDto> getPropertiesList() {
        return propertiesList;
    }

    /**
     * @param propertiesList the propertiesList to set
     */
    public void setPropertiesList(List<GenericModelDto> propertiesList) {
        this.propertiesList = propertiesList;
    }

    /**
     * @return the wardProperties
     */
    public List<WardLandProperties> getWardProperties() {
        return wardProperties;
    }

    /**
     * @param wardProperties the wardProperties to set
     */
    public void setWardProperties(List<WardLandProperties> wardProperties) {
        this.wardProperties = wardProperties;
    }

    public List<PrintedBills> getPrintedBillsList() {
        return printedBillsList;
    }

    public void setPrintedBillsList(List<PrintedBills> printedBillsList) {
        this.printedBillsList = printedBillsList;
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
     * @return the lazyUpdateProperties
     */
    public LazyDataModel<PrintedBills> getLazyUpdateProperties() {
        return lazyUpdateProperties;
    }

    /**
     * @param lazyUpdateProperties the lazyUpdateProperties to set
     */
    public void setLazyUpdateProperties(LazyDataModel<PrintedBills> lazyUpdateProperties) {
        this.lazyUpdateProperties = lazyUpdateProperties;
    }

    /**
     * @return the lcdArea
     */
    public List<LocalCouncilDevArea> getLcdArea() {
        return lcdArea;
    }

    /**
     * @param lcdArea the lcdArea to set
     */
    public void setLcdArea(List<LocalCouncilDevArea> lcdArea) {
        this.lcdArea = lcdArea;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long getOfficerId() {
        return officerId;
    }

    public void setOfficerId(Long officerId) {
        this.officerId = officerId;
    }
}
