/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.icsl.ledar.ejb.enums.ApplicationEntityNames;
import net.icsl.ledar.ejb.enums.UserActitiyName;
import net.icsl.ledar.ejb.model.BareLands;
import net.icsl.ledar.ejb.model.BarelandFiles;
import net.icsl.ledar.ejb.model.LcdaWards;

import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.Logindetails;

import net.icsl.ledar.ejb.model.PropertyServices;
import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.model.WardLandProperties;
import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.NotificationDataService;
import net.icsl.ledar.web.util.FacesSupportUtil;

/**
 *
 * @author sfagade
 */
@Named("landPropsBean")
@SessionScoped
public class LandPropertiesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private LandPropertiesDataService landPropService;
    @Inject
    private NotificationDataService noteService;
    @Inject
    private LcdaWardsDataServices lcdaService;

    @Inject
    private LoginBean loginBean;
    @Inject
    private LedarApplicationBean ledarApp;

    //@ManagedProperty(value = "#{param.propId}")
    private String propertyBillId;
    //@ManagedProperty(value = "#{param.propId}")
    private String propertyId;

    private List<PropertyServices> propServices;
    //private List<Notifications> notifications;
    private List<BarelandFiles> blandFiles;
    private List<String[]> duplicateList;

    private Logindetails selectedFieldOfficer;
    private LocalCouncilDevArea selected_lcda;
    private LcdaWards selected_ward;
    private WardStreets selectedStreet;
    //private WardLandProperties wardProperty;
    private BareLands bareland;

    private String[] dataSearchType = {"Show Only Current", "Show Only Old", "Show All"};
    private String[] dateRangeValues = {"Today", "Yesterday", "Past Week", "Past Month", "Last Quarter", "Select Date Range"};
    private long recordsCount, propBillId;
    private String searchType, searchPropId, selectedRange;

    private boolean likeSearch;
    private Date startDate, endDate;

    /**
     * Creates a new instance of LandPropertiesBean
     */
    public LandPropertiesBean() {
    }

    @PostConstruct
    public void init() {
        String view_id = FacesContext.getCurrentInstance().getViewRoot().getViewId();

        //String prop_id = facesContext.getExternalContext().getRequestParameterMap().get("propId");
        switch (view_id) {
            case "/app/properties/viewBarelandDetails.xhtml":

                break;
            case "/app/properties/viewPropertyDetails.xhtml":
            case "/app/administrator/viewPropertyDetails.xhtml":

                break;
            case "/app/properties/duplicateProperties.xhtml":

                break;

        }
    }

    public void initDuplicateProperty() {
        String[] accepted_roles = {"GEO-CODING OFFICER"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            duplicateList = lcdaService.fetchDuplicateProperty(null);
            recordsCount = Long.valueOf(duplicateList.size());
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void initBareLandDetailsView() {
        String prop_id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("propId");
        if (prop_id != null) {
            bareland = landPropService.findBareLandById(Long.valueOf(prop_id));
            if (bareland != null) {
                blandFiles = landPropService.fetchAllBarelandFilesByLandId(bareland.getId());
            }
        }
    }

    public void deleteProperty() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        searchPropId = params.get("ppid");
        String property_pin = null;
        List<WardLandProperties> propertiesCheck = null;

        UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.DELETEDPROPERTY.toString(), new Date(), loginBean.getPerson().getFullName() + " Deleted property information, "
                + "property ID is: " + searchPropId, loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, FacesSupportUtil.getClientAgent(),
                loginBean.getPerson().getLogindetailId(), null, null);

        activity.setEntityName(ApplicationEntityNames.WARDLANDPROPERTIES.toString());
        //activity.setEntityId(landProperty.getId());

        if (searchPropId != null) { //this should always be the case

            WardLandProperties delProperty = landPropService.findWardPropertyById(Long.valueOf(searchPropId));
            if (delProperty != null) { //this should always be the case here
                property_pin = delProperty.getPropertyId();
            }

            if (landPropService.deletePropertyData(Long.valueOf(searchPropId), activity)) {

                if (property_pin != null) {
                    propertiesCheck = landPropService.fetchAllPropertiesByPin(searchPropId, null);
                    if ((propertiesCheck == null) || (propertiesCheck.size() <= 0)) {
                        landPropService.deletePropertyUseData(property_pin, null);
                    }
                }

                //lazyProperties = new LandPropertiesLazy(loginBean.getPerson().getOrganization().getId(), lcdaService, false);
                // recordCount = lcdaService.countPropertiesTotal(loginBean.getPerson().getOrganization().getId(), false, null);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Property has been deleted successfully", "Success"));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

                searchPropId = null;

                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("listAllProperties.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(AdministratorBean.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to delete property, please try again later", "Error"));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            }
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

    public boolean isLikeSearch() {
        return likeSearch;
    }

    public void setLikeSearch(boolean likeSearch) {
        this.likeSearch = likeSearch;
    }

    /**
     * @return the dataSearchType
     */
    public String[] getDataSearchType() {
        return dataSearchType;
    }

    /**
     * @param dataSearchType the dataSearchType to set
     */
    public void setDataSearchType(String[] dataSearchType) {
        this.dataSearchType = dataSearchType;
    }

    public String getSearchPropId() {
        return searchPropId;
    }

    public void setSearchPropId(String searchPropId) {
        this.searchPropId = searchPropId;
    }

    public LocalCouncilDevArea getSelected_lcda() {
        return selected_lcda;
    }

    public void setSelected_lcda(LocalCouncilDevArea selected_lcda) {
        this.selected_lcda = selected_lcda;
    }

    public long getRecordsCount() {
        return recordsCount;
    }

    public void setRecordsCount(long recordsCount) {
        this.recordsCount = recordsCount;
    }

    public Logindetails getSelectedFieldOfficer() {
        return selectedFieldOfficer;
    }

    public void setSelectedFieldOfficer(Logindetails selectedFieldOfficer) {
        this.selectedFieldOfficer = selectedFieldOfficer;
    }

    public LedarApplicationBean getLedarApp() {
        return ledarApp;
    }

    public void setLedarApp(LedarApplicationBean ledarApp) {
        this.ledarApp = ledarApp;
    }

    /**
     * @return the searchType
     */
    public String getSearchType() {
        return searchType;
    }

    /**
     * @param searchType the searchType to set
     */
    public void setSearchType(String searchType) {
        this.searchType = searchType;
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

    /**
     * @return the propBillId
     */
    public long getPropBillId() {
        return propBillId;
    }

    /**
     * @param propBillId the propBillId to set
     */
    public void setPropBillId(long propBillId) {
        this.propBillId = propBillId;
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

    public List<PropertyServices> getPropServices() {
        return propServices;
    }

    public void setPropServices(List<PropertyServices> propServices) {
        this.propServices = propServices;
    }

    /**
     * @return the propertyBillId
     */
    public String getPropertyBillId() {
        return propertyBillId;
    }

    /**
     * @param propertyBillId the propertyBillId to set
     */
    public void setPropertyBillId(String propertyBillId) {
        this.propertyBillId = propertyBillId;
    }

    /**
     * @return the dateRangeValues
     */
    public String[] getDateRangeValues() {
        return dateRangeValues;
    }

    /**
     * @param dateRangeValues the dateRangeValues to set
     */
    public void setDateRangeValues(String[] dateRangeValues) {
        this.dateRangeValues = dateRangeValues;
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

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
     * @return the bareland
     */
    public BareLands getBareland() {
        return bareland;
    }

    /**
     * @param bareland the bareland to set
     */
    public void setBareland(BareLands bareland) {
        this.bareland = bareland;
    }

    public List<BarelandFiles> getBlandFiles() {
        return blandFiles;
    }

    public void setBlandFiles(List<BarelandFiles> blandFiles) {
        this.blandFiles = blandFiles;
    }

    /**
     * @return the duplicateList
     */
    public List<String[]> getDuplicateList() {
        return duplicateList;
    }

    /**
     * @param duplicateList the duplicateList to set
     */
    public void setDuplicateList(List<String[]> duplicateList) {
        this.duplicateList = duplicateList;
    }

}
