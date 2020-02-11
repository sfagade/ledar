package net.icsl.ledar.web.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;
import net.icsl.ledar.ejb.enums.ApplicationEntityNames;
import net.icsl.ledar.ejb.enums.UserActitiyName;
import net.icsl.ledar.ejb.enums.ValuationStatusEnum;
import net.icsl.ledar.ejb.model.BareLands;
import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.Notifications;
import net.icsl.ledar.ejb.model.PropertyServices;
import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.model.WardLandProperties;
import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.model.WardTowns;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.NotificationDataService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.ejb.util.EmailSender;
import net.icsl.ledar.ejb.util.EptsApiDataProcessor;
import net.icsl.ledar.web.lazyModel.LandPropertiesLazy;
import net.icsl.ledar.web.util.FacesSupportUtil;

import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author sfagade
 */
@Named("lcdaWardsDataBean")
@SessionScoped
public class LcdaWardsDataBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private FacesContext facesContext;

    @Inject
    EmailSender emailSender;
    @Inject
    private LcdaWardsDataServices lcdaService;
    @Inject
    private LandPropertiesDataService landPropService;
    @Inject
    private RegisteredUsersDataService registeredService;
    @Inject
    private EptsApiDataProcessor eptsProcessor;
    @Inject
    private LoginBean loginBean;
    @Inject
    private LedarApplicationBean ledarApp;
    @Inject
    private UploadedFilesBean uploadBean;
    @Inject
    private NotificationDataService noteService;
    private final ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();

    private List<LocalCouncilDevArea> lcdaAreas;
    private LocalCouncilDevArea lcdaArea;
    private List<LcdaWards> lcdaWards;
    private LcdaWards lcdaWard;
    private List<WardTowns> wardTowns;
    private WardTowns wardTown;
    private List<WardStreets> wardStreets;
    private List<WardLandProperties> wardProperties;
    private List<WardLandProperties> selectProperties;
    private List<BareLands> barelands;
    private List<BareLands> selectedBareland;
    private List<Logindetails> allFieldOfficers;
    private List<PropertyServices> propServices;
    private List<Notifications> notifications;

    private LazyDataModel<WardLandProperties> lazyProperties;

    private LcdaWards selectedWard;
    private WardStreets wardStreet;
    private WardStreets streetEstate;
    private Logindetails selectedUser;
    private LocalCouncilDevArea selected_lcda;
    private WardLandProperties landProperty, wardProperty;

    private long recordCount;
    private String wardName, wardCode, searchPropId, selectedStatus, selectedDay, remarks, propertyId;
    private Boolean legacySearch, showSeacrhResult, showBlandResult, fullResultSet, synced;
    private Date starSearchDate, endSearchDate;
    private int resultIndex;

    /**
     * Creates a new instance of ReferenceDataBean
     */
    public LcdaWardsDataBean() {
        showBlandResult = false;
        showSeacrhResult = false;
    }

    @PostConstruct
    public void init() {
        String view_id = facesContext.getViewRoot().getViewId();

        if (view_id.equalsIgnoreCase("/app/appAdmin/listAllLcda.xhtml")) {
            lcdaAreas = lcdaService.fetchAllSenatorialDistrictLCDAs(loginBean.getPerson().getOrganization().getSenatorialDistrictId().getId());
            recordCount = lcdaAreas.size();
        } else if (view_id.equalsIgnoreCase("/app/appAdmin/listAllWards.xhtml")) {

        } else if (view_id.equalsIgnoreCase("/app/appAdmin/listAllWardTown.xhtml")) {
            if (loginBean.getuRoles().getAuthenticationRoleId().getRoleName().equals("APPLICATION ADMINISTRATOR")) {
                wardTowns = lcdaService.fetchWardTowns(null);
                recordCount = wardTowns.size();
            }
        } else if (view_id.equalsIgnoreCase("/app/appAdmin/listAllWardStreets.xhtml")) {

        } else if (view_id.equalsIgnoreCase("/app/properties/listAllProperties.xhtml")) {

        } else if (view_id.equalsIgnoreCase("/app/appAdmin/createNewLcda.xhtml")) {
            if (!loginBean.getuRoles().getAuthenticationRoleId().getRoleName().equals("APPLICATION ADMINISTRATOR")) {
                this.redirectUnauthorizedUser("You're not authorized to access this resource");
            }
            lcdaArea = new LocalCouncilDevArea();
        } else if (view_id.equals("/app/appAdmin/createNewStreet.xhtml")) {
            String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "ADMINISTRATOR"};
            if (FacesSupportUtil.isUserInRole(accepted_roles)) {
                wardStreet = new WardStreets();
            } else {
                this.redirectUnauthorizedUser("You're not authorized to access this resource");
            }

        } else if (view_id.equalsIgnoreCase("/app/properties/allBarelands.xhtml")) {

        } else if (view_id.equalsIgnoreCase("/app/properties/listDataProperties.xhtml")) {

        }
    }

    public void pushLandToInits() {

        if (selectedBareland != null && selectedBareland.size() > 0) {

            String download_dir = uploadBean.getProp().getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/push_files";
            File download_folder = new File(download_dir);
            if (!download_folder.exists()) {
                download_folder.mkdirs(); //create folder
            }

            for (BareLands property : selectedBareland) { //I need to remove the selected from the display view
                barelands.remove(property);
            }

            recordCount = recordCount - selectedBareland.size();

            eptsProcessor.prepareAndPushLandToInit(selectedBareland, loginBean.getPerson().getLogindetailId().getId(), download_dir);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Background process has been kicked off to push " + selectedBareland.size() + " selected properties to EPTS,"
                    + " you will be sent a report email when the process is completed", "Succes"));
            fullResultSet = false;
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No property was selected, please select property to push", "Error"));
        }
        facesContext.getExternalContext().getFlash().setKeepMessages(true);
    }

    public void initPropertyDetailsView() {
        String prop_id = facesContext.getExternalContext().getRequestParameterMap().get("propId");
        String referer = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get("referer");
        if (prop_id != null) {
            setWardProperty(landPropService.findWardPropertyById(Long.valueOf(prop_id)));

            if (getWardProperty().getPropertyTypes().equals("SERVICED")) {
                setPropServices(landPropService.fetchPropertyServicesByPropertyId(getWardProperty().getId()));
            }
            if (getWardProperty().getPropertyValuationStatus() != null && !wardProperty.getPropertyValuationStatus().equals(ValuationStatusEnum.PENDING.toString())) {
                setNotifications(noteService.fetchAllEntityNotifications(getWardProperty().getId(), false));
            }
            this.setSynced(getWardProperty().getIsInitsSynced());
        }
    }

    public void returnToDataEntry(ActionEvent event) {

        if (remarks != null && !remarks.isEmpty()) {
            String email_subject = loginBean.getPerson().getFullName() + " has return a property previously marked ", email_msg;
            String client_agant = FacesSupportUtil.getClientAgent();

            if (selectProperties != null && selectProperties.size() > 0) {
                for (WardLandProperties property : selectProperties) {
                    email_subject += property.getPropertyValuationStatus() + ". <div>";
                    email_msg = email_subject + "Please see comments below </div>";

                    property.setPropertyValuationStatus(ValuationStatusEnum.PENDING.toString());
                    property.setPushStatus(ValuationStatusEnum.PENDING.toString());
                    property.setLastUpdatedById(loginBean.getPerson().getLogindetailId());
                    property.setAdminComment(remarks);
                    property.setIsVerified(Boolean.FALSE);

                    UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.UPDATEDPROPERTY.toString(), new Date(), loginBean.getPerson().getFullName() + " returned Property for"
                            + " update: " + property.getId(), loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, client_agant, loginBean.getPerson().getLogindetailId(), null, null);

                    activity.setEntityName(ApplicationEntityNames.WARDLANDPROPERTIES.toString());
                    activity.setEntityId(property.getId());

                    String[] email_addresses = {property.getVerifiedById().getPerson().getContactInformationsList().get(0).getOfficeEmailAddress()};

                    try {
                        landPropService.updateWardLandProperty(property, activity);
                        emailSender.sendPlainEmailMessage(email_msg, email_subject, email_addresses);
                    } catch (MessagingException ex) {
                        Logger.getLogger(LcdaWardsDataBean.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    email_subject = loginBean.getPerson().getFullName() + " has return a property previously marked ";
                }
                remarks = null;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Returned successfully", "Success"));
                RequestContext.getCurrentInstance().addCallbackParam("loggedIn", true);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select property to return", "Error"));
                RequestContext.getCurrentInstance().addCallbackParam("loggedIn", false);

            }

        }
    }

    public void createNewLcda() {
        //lcdaWard = new LcdaWards(null, wardName, selected_lcda, null);

    }

    public void initNewWardForm() {
        String[] accepted_roles = {"APPLICATION ADMINISTRATOR"};
        if (!FacesSupportUtil.isUserInRole(accepted_roles)) {
            this.redirectUnauthorizedUser("You're not authorized to access this resource");
        }
    }

    public void initAllWardView() {
        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "ADMINISTRATOR"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            if (lcdaWards == null) {
                lcdaWards = lcdaService.fetchAllLcdaWardsByLcda(null, loginBean.getPerson().getOrganization().getId()); //NOTICE only the admin guy should be able to call this method without a param
                recordCount = lcdaWards.size();
            }
        }
    }

    public void initAllStreetsView() {
        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "ADMINISTRATOR"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            if (wardStreets == null) {
                wardStreets = lcdaService.fetchWardStreets(null, loginBean.getPerson().getOrganization().getId(), null);
                recordCount = wardStreets.size();
            }
        }
    }

    public void saveNewStreetInformation() {

        if (wardStreet.getStreetName() != null && (!wardStreet.getStreetName().isEmpty())) {

            UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.NEWSTREET.toString(), new Date(), loginBean.getPerson().getFullName() + " saved new street information",
                    loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, null, loginBean.getPerson().getLogindetailId(), null, null);

            activity.setEntityName(ApplicationEntityNames.WARDSTREET.toString());
            activity.setEntityId(wardStreet.getId());

            wardStreet.setCreatedById(loginBean.getPerson().getLogindetailId());
            wardStreet.setStreetName(wardStreet.getStreetName().toUpperCase());
            wardStreet.setIsVerified(true);
            wardStreet.setVerifiedById(loginBean.getPerson().getLogindetailId());

            if (wardStreet.getEstateName() != null && !wardStreet.getEstateName().isEmpty()) {
                wardStreet.setEstateName(wardStreet.getEstateName().toUpperCase());
            }

            wardStreet = lcdaService.saveNewStreetInformation(wardStreet, activity);

            if (wardStreet != null) {
                facesContext.addMessage("streetForm:asu_btn", new FacesMessage(FacesMessage.SEVERITY_INFO, "Street Information has been saved successfully: " + wardStreet.getId(), ""));
                wardStreet = new WardStreets();
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            } else {
                facesContext.addMessage("streetForm:asu_btn", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save street information, please try again later", ""));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            }

        } else {
            facesContext.addMessage("streetForm:asu_btn", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Street name is rquired please", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void saveNewWardInformation() {
        if (wardName != null) {

            LcdaWards newWard = new LcdaWards(null, wardName.toUpperCase(), selected_lcda, null);
            newWard.setContractorId(loginBean.getPerson().getOrganization());
            newWard.setCreatedById(loginBean.getPerson().getLogindetailId());
            newWard.setWardCode(wardCode.toUpperCase());

            UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.SAVEDDISTRICT.toString(), new Date(), loginBean.getPerson().getFullName() + " saved new District "
                    + "information", loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, null, loginBean.getPerson().getLogindetailId(), null, null);

            activity.setEntityName(ApplicationEntityNames.LCDAWARDS.toString());
            activity.setEntityId(newWard.getId());

            newWard = lcdaService.saveNewLcdaWardInformation(newWard, activity);

            if (newWard != null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "District information has been saved successfully: " + newWard.getId(), ""));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                wardName = null;
                wardCode = null;
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not save district information, please try again later", ""));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            }

        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "District name is required please", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void filterPropertyResult() {

        Long user_id = (selectedUser != null) ? selectedUser.getId() : null;
        String val_status = (selectedStatus != null && !selectedStatus.isEmpty()) ? ValuationStatusEnum.valueOf(selectedStatus).toString() : null;
        String order_dir, estate_name = (streetEstate != null) ? streetEstate.getEstateName() : null;

        if (selectedDay != null && !selectedDay.isEmpty() && !selectedDay.equalsIgnoreCase("SELECT DATE RANGE")) {
            Calendar calendar = Calendar.getInstance();
            switch (selectedDay) {
                case "TODAY":
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    starSearchDate = calendar.getTime();
                    endSearchDate = new Date();
                    break;
                case "YESTERDAY":
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    endSearchDate = calendar.getTime();
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    starSearchDate = calendar.getTime();

                    break;
                case "PAST WEEK":
                    calendar.add(Calendar.DAY_OF_MONTH, -8);
                    starSearchDate = calendar.getTime();
                    endSearchDate = new Date();
                    break;
                case "PAST MONTH":
                    endSearchDate = new Date();
                    calendar.add(Calendar.MONTH, -1);
                    starSearchDate = calendar.getTime();
                    break;
                case "THIS YEAR":
                    endSearchDate = new Date();
                    calendar.set(Calendar.DAY_OF_YEAR, 1);
                    starSearchDate = calendar.getTime();
                    break;
                default:
                    throw new AssertionError();
            }
        } else if (selectedDay != null && selectedDay.equalsIgnoreCase("SELECT DATE RANGE")) {
            if ((starSearchDate == null) || (endSearchDate == null)) { //both fields are needed to continue
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Start Date and End Date are needed to complete this search", ""));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                return;
            }
        } else { //I need to make sure these fields are not holding values from last search
            starSearchDate = null;
            endSearchDate = null;
        }

        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "GEO-CODING OFFICER"};
        order_dir = (FacesSupportUtil.isUserInRole(accepted_roles)) ? "desc" : "asc";

        if (getSearchPropId() != null && (!searchPropId.isEmpty())) {
            wardProperties = lcdaService.fetchAllPropertyByPropertyId(getSearchPropId(), false, loginBean.getPerson().getOrganization().getId());
            recordCount = wardProperties.size();
        } else if (wardStreet != null) {
            wardProperties = lcdaService.fetchAllWardLandPropertyByAreaId(wardStreet.getId(), null, null, user_id, val_status, starSearchDate, endSearchDate, resultIndex, order_dir, estate_name);
            recordCount = lcdaService.countFilteredPropertyResults(wardStreet.getId(), null, null, user_id, val_status, starSearchDate, endSearchDate, order_dir, estate_name);
        } else if (estate_name != null) {
            wardProperties = lcdaService.fetchAllWardLandPropertyByAreaId(null, null, null, user_id, val_status, starSearchDate, endSearchDate, resultIndex, order_dir, estate_name);
            recordCount = lcdaService.countFilteredPropertyResults(null, null, null, user_id, val_status, starSearchDate, endSearchDate, order_dir, estate_name);
        } else if (selectedWard != null) {
            wardProperties = lcdaService.fetchAllWardLandPropertyByAreaId(null, selectedWard.getId(), null, user_id, val_status, starSearchDate, endSearchDate, resultIndex, order_dir, estate_name);
            recordCount = lcdaService.countFilteredPropertyResults(null, selectedWard.getId(), null, user_id, val_status, starSearchDate, endSearchDate, order_dir, estate_name);
        } else if (getSelected_lcda() != null) {
            wardProperties = lcdaService.fetchAllWardLandPropertyByAreaId(null, null, getSelected_lcda().getId(), user_id, val_status, starSearchDate, endSearchDate, resultIndex, order_dir, estate_name);
            recordCount = lcdaService.countFilteredPropertyResults(null, null, getSelected_lcda().getId(), user_id, val_status, starSearchDate, endSearchDate, order_dir, estate_name);
        } else if (user_id != null) {
            wardProperties = lcdaService.fetchAllWardLandPropertyByAreaId(null, null, null, user_id, val_status, starSearchDate, endSearchDate, resultIndex, order_dir, estate_name);
            recordCount = lcdaService.countFilteredPropertyResults(null, null, null, user_id, val_status, starSearchDate, endSearchDate, order_dir, estate_name);
        } else if (selectedStatus != null && !selectedStatus.isEmpty()) {
            wardProperties = lcdaService.fetchAllWardLandPropertyByAreaId(null, null, null, null, val_status, starSearchDate, endSearchDate, resultIndex, order_dir, estate_name);
            recordCount = lcdaService.countFilteredPropertyResults(null, null, null, null, val_status, starSearchDate, endSearchDate, order_dir, estate_name);
        } else if (starSearchDate != null && endSearchDate != null) {
            wardProperties = lcdaService.fetchAllWardLandPropertyByAreaId(null, null, null, null, null, starSearchDate, endSearchDate, resultIndex, order_dir, estate_name);
            recordCount = lcdaService.countFilteredPropertyResults(null, null, null, null, null, starSearchDate, endSearchDate, order_dir, estate_name);
        }
        if (wardProperties != null) {
            //recordCount = wardProperties.size();
            //recordCount = lcdaService.countPropertiesTotal(loginBean.getPerson().getOrganization().getId(), false, val_status);
            lazyProperties = null;
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Search query returned no results", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            recordCount = 0;
        }
        showSeacrhResult = true;

    }

    public void deleteProperty() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        searchPropId = params.get("ppid");
        String property_pin = null;
        List<WardLandProperties> propertiesCheck;

        UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.DELETEDPROPERTY.toString(), new Date(), loginBean.getPerson().getFullName() + " Deleted property information, "
                + "property ID is: " + searchPropId, loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, FacesSupportUtil.getClientAgent(),
                loginBean.getPerson().getLogindetailId(), null, null);

        activity.setEntityName(ApplicationEntityNames.WARDLANDPROPERTIES.toString());
        //activity.setEntityId(landProperty.getId());

        if (searchPropId != null) { //this should always be the case
            Logger.getLogger(LcdaWardsDataBean.class.getName()).log(Level.SEVERE, "{0} deleting Property: {1}", new Object[]{loginBean.getPerson().getFullName(), searchPropId});
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

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Property has been deleted successfully", "Success"));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

                searchPropId = null;

                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("listAllProperties.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(LcdaWardsDataBean.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to delete property, please try again later", "Error"));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            }
        }
    }

    public void deleteBarelandData() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        searchPropId = params.get("ppid");
        /**
         * String property_pin = null; List<BareLands> brlands = null;
         */

        UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.DELETEBARELAND.toString(), new Date(), loginBean.getPerson().getFullName() + " Deleted bare-land information, "
                + "property ID is: " + searchPropId, loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, FacesSupportUtil.getClientAgent(),
                loginBean.getPerson().getLogindetailId(), null, null);

        activity.setEntityName(ApplicationEntityNames.BARELAND.toString());
        //activity.setEntityId(landProperty.getId());

        if (searchPropId != null) { //this should always be the case
            Logger.getLogger(LcdaWardsDataBean.class.getName()).log(Level.SEVERE, "{0} deleting Bareland: {1}", new Object[]{loginBean.getPerson().getFullName(), searchPropId});
            BareLands delBareland = landPropService.findBareLandById(Long.valueOf(searchPropId));
            if (delBareland != null) { //this should always be the case here
                //property_pin = delBareland.getPropertyId();

                if (landPropService.deleteBarelandData(Long.valueOf(searchPropId), activity)) {

                    searchPropId = null;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Property has been deleted successfully", "Success"));
                    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

                    try {
                        FacesContext.getCurrentInstance().getExternalContext().redirect("allBarelands.xhtml");
                    } catch (IOException ex) {
                        Logger.getLogger(LcdaWardsDataBean.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to delete property, please try again later", "Error"));
                    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                }
            }
        }
    }

    public void changedLcda() {

        if (getSelected_lcda() != null) {

            String[] accepted_roles = {"ADMINISTRATOR"};

            if (loginBean.getPerson().getOrganization() != null) {
                loginBean.setLcdaWards(lcdaService.fetchAllLcdaWardsByLcda(getSelected_lcda().getId(), loginBean.getPerson().getOrganization().getId()));
            } else if (FacesSupportUtil.isUserInRole(accepted_roles)) {
                loginBean.setLcdaWards(lcdaService.fetchAllLcdaWardsByLcda(getSelected_lcda().getId(), null));
            }
        }
    }

    public void changedWardStreets() {

        if (selectedWard != null) {
            loginBean.setWardStreets(lcdaService.fetchWardStreets(selectedWard.getId(), null, Boolean.TRUE));
        }
    }

    public void initAllProperties() {
        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "GEO-CODING OFFICER", "ADMINISTRATOR", "INTERNAL CONTROL", "HEAD OF OPERATIONS"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            if (lazyProperties == null && !showSeacrhResult) {
                Long consultant_id = (loginBean.getPerson().getOrganization() != null) ? loginBean.getPerson().getOrganization().getId() : null;
                lazyProperties = new LandPropertiesLazy(consultant_id, lcdaService, false);

                Long[] field_roles = {9L, 19L, 26L}; //all field officers ID?
                setAllFieldOfficers(registeredService.fetchAllLcdaChairmen(Arrays.asList(field_roles), consultant_id));

                if (recordCount <= 0) {
                    recordCount = lcdaService.countPropertiesTotal(consultant_id, false, null);
                }
                showSeacrhResult = false;
                remarks = null;
            }

        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void resetAllPropertiesList() {
        showSeacrhResult = false;
        lazyProperties = null;
        wardProperties = null;
        recordCount = 0;
    }

    public void initDataEntryProperties() {
        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "DATA ENTRY", "FIELD SUPERVISOR"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {

            if (wardProperties == null) {
                wardProperties = landPropService.findWardPropertyNotSentToPortal("PUSHED TO INITS", 0, 400, Boolean.TRUE);

                recordCount = lcdaService.countPropertiesTotal(loginBean.getPerson().getOrganization().getId(), false, ValuationStatusEnum.INITSPUSHED.toString());

                Long[] field_roles = {9L, 19L, 26L}; //all field officers ID?
                setAllFieldOfficers(registeredService.fetchAllLcdaChairmen(Arrays.asList(field_roles), loginBean.getPerson().getOrganization().getId()));

                if (selectedStatus == null || selectedStatus.isEmpty()) {
                    selectedStatus = ValuationStatusEnum.PENDING.toString();
                }

                showSeacrhResult = false;
            }

        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            this.redirectUnauthorizedUser("You're not authorized to access this resource");
        }
    }

    public void initBarelandProperties() {

        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "GEO-CODING OFFICER", "DATA ENTRY", "FIELD SUPERVISOR", "HEAD OF OPERATIONS"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            if (barelands == null && !showBlandResult) {
                barelands = lcdaService.fetchAllBareLands(loginBean.getPerson().getOrganization().getId());
                recordCount = barelands.size();

                Long[] field_roles = {9L, 19L, 26L}; //all field officers ID?
                setAllFieldOfficers(registeredService.fetchAllLcdaChairmen(Arrays.asList(field_roles), loginBean.getPerson().getOrganization().getId()));
                showBlandResult = false;

                starSearchDate = null;
                endSearchDate = null;
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            barelands = null;
            this.redirectUnauthorizedUser("You do not have permission to access this resource");
        }
    }

    public void resetListForm() {
        //showSeacrhResult = true;
        wardProperties = null;
        resultIndex = 0;
        wardStreet = null;
        selectedWard = null;
        streetEstate = null;
        selectedStatus = null;
        selectedUser = null;
        starSearchDate = null;
        endSearchDate = null;
        selectedDay = null;
        showSeacrhResult = false;
        lazyProperties = null;
        wardProperties = null;
        recordCount = 0;
    }

    public List<WardStreets> runStreetAutoComplete(String query) {
        List<WardStreets> allValues = lcdaService.fetchWardStreetsByStreetName(query, loginBean.getPerson().getOrganization().getId(), false);

        return allValues;
    }

    public List<WardStreets> runEstateNameAutoComplete(String query) {
        List<WardStreets> allValues = lcdaService.fetchWardStreetsByEstateName(query, loginBean.getPerson().getOrganization().getId(), false);

        return allValues;
    }

    public void makeReadyForInits() {
        if (selectProperties != null && selectProperties.size() > 0) {
            for (WardLandProperties property : selectProperties) {
                if (!property.getIsVerified()) {
                    property.setIsVerified(Boolean.TRUE);
                    property.setVerifiedById(loginBean.getPerson().getLogindetailId());
                    property.setVerifiedDate(new Date());
                    property.setPushStatus(ValuationStatusEnum.READYINITS.toString());
                    property.setPropertyValuationStatus(ValuationStatusEnum.READYINITS.toString());

                    landPropService.updateWardLandProperty(property, null);
                }
            }
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Properties have been updated to Ready for Push successfully", "Success"));
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No property was selected, please select property to push", "Error"));
        }
        facesContext.getExternalContext().getFlash().setKeepMessages(true);
    }

    public void pushToInits() {

        if (selectProperties != null && selectProperties.size() > 0) {

            String download_dir = uploadBean.getProp().getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/push_files";
            File download_folder = new File(download_dir);
            if (!download_folder.exists()) {
                download_folder.mkdirs(); //create folder
            }

            for (WardLandProperties property : selectProperties) { //I need to remove the selected from the display view
                wardProperties.remove(property);
            }

            recordCount = recordCount - selectProperties.size();

            eptsProcessor.prepareAndPushPropertiesToInit(selectProperties, loginBean.getPerson().getLogindetailId().getId(), download_dir);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Background process has been kicked off to push " + selectProperties.size() + " selected properties to EPTS,"
                    + " you will be sent a report email when the process is completed", "Succes"));
            fullResultSet = false;
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No property was selected, please select property to push", "Error"));
        }
        facesContext.getExternalContext().getFlash().setKeepMessages(true);
    }

    public void filterBarelandResult() {

        Long user_id = (selectedUser != null) ? selectedUser.getId() : null;
        String val_status = (selectedStatus != null && !selectedStatus.isEmpty()) ? ValuationStatusEnum.valueOf(selectedStatus).toString() : null;

        if (selectedDay != null && !selectedDay.isEmpty() && !selectedDay.equalsIgnoreCase("SELECT DATE RANGE")) {
            Calendar calendar = Calendar.getInstance();
            switch (selectedDay) {
                case "TODAY":
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    starSearchDate = calendar.getTime();
                    endSearchDate = new Date();
                    break;
                case "YESTERDAY":
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    endSearchDate = calendar.getTime();
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    starSearchDate = calendar.getTime();

                    break;
                case "PAST WEEK":
                    calendar.add(Calendar.DAY_OF_MONTH, -8);
                    starSearchDate = calendar.getTime();
                    endSearchDate = new Date();
                    break;
                case "PAST MONTH":
                    endSearchDate = new Date();
                    calendar.add(Calendar.MONTH, -1);
                    starSearchDate = calendar.getTime();
                    break;
                case "THIS YEAR":
                    endSearchDate = new Date();
                    calendar.set(Calendar.DAY_OF_YEAR, 1);
                    starSearchDate = calendar.getTime();
                    break;
                default:
                    throw new AssertionError();
            }
        } else if (selectedDay != null && selectedDay.equalsIgnoreCase("SELECT DATE RANGE")) {
            if ((starSearchDate == null) || (endSearchDate == null)) { //both fields are needed to continue
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Start Date and End Date are needed to complete this search", ""));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                return;
            }
        }

        if (getSearchPropId() != null && (!searchPropId.isEmpty())) {
            barelands = landPropService.fetchAllBarelandByPin(getSearchPropId(), loginBean.getPerson().getOrganization().getId());
        } else if (wardStreet != null) {
            barelands = landPropService.filterBarelandLists(wardStreet.getId(), user_id, val_status, starSearchDate, endSearchDate, resultIndex);
        } else if (user_id != null) {
            barelands = landPropService.filterBarelandLists(null, user_id, val_status, starSearchDate, endSearchDate, resultIndex);
        } else if (selectedStatus != null && !selectedStatus.isEmpty()) {
            barelands = landPropService.filterBarelandLists(null, user_id, val_status, starSearchDate, endSearchDate, resultIndex);
        } else if (starSearchDate != null && endSearchDate != null) {
            barelands = landPropService.filterBarelandLists(null, user_id, val_status, starSearchDate, endSearchDate, resultIndex);
        }
        if (barelands != null) {
            recordCount = barelands.size();
            //recordCount = lcdaService.countPropertiesTotal(loginBean.getPerson().getOrganization().getId(), false, val_status);
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Search query returned no results", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            recordCount = 0;
        }
        showBlandResult = true;
    }

    public void resetBarlandView() {
        showBlandResult = false;
    }

    public void redirectUnauthorizedUser(String message) {
        FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
        facesContext.addMessage(null, m);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        try {
            extContext.redirect(extContext.getRequestContextPath() + "/app/index.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(AdministratorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nextRecordSet() {
        resultIndex += 400;
        if (resultIndex < recordCount) {
            if (showSeacrhResult) {
                this.filterPropertyResult();
            } else {
                wardProperties = landPropService.findWardPropertyNotSentToPortal("PUSHED TO INITS", resultIndex, 400, Boolean.TRUE);
            }
        }
    }

    public void previousRecordSet() {
        resultIndex -= 400;
        if (resultIndex >= 0) {
            if (showSeacrhResult) {
                this.filterPropertyResult();
            } else {
                wardProperties = landPropService.findWardPropertyNotSentToPortal("PUSHED TO INITS", resultIndex, 400, Boolean.TRUE);
            }
        } else {
            resultIndex = 0;
        }
    }

    /**
     * @return the lcdaAreas
     */
    public List<LocalCouncilDevArea> getLcdaAreas() {
        return lcdaAreas;
    }

    /**
     * @param lcdaAreas the lcdaAreas to set
     */
    public void setLcdaAreas(List<LocalCouncilDevArea> lcdaAreas) {
        this.lcdaAreas = lcdaAreas;
    }

    public long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    /**
     * @return the lcdaArea
     */
    public LocalCouncilDevArea getLcdaArea() {
        return lcdaArea;
    }

    /**
     * @param lcdaArea the lcdaArea to set
     */
    public void setLcdaArea(LocalCouncilDevArea lcdaArea) {
        this.lcdaArea = lcdaArea;
    }

    /**
     * @return the selectedUser
     */
    public Logindetails getSelectedUser() {
        return selectedUser;
    }

    /**
     * @param selectedUser the selectedUser to set
     */
    public void setSelectedUser(Logindetails selectedUser) {
        this.selectedUser = selectedUser;
    }

    /**
     * @return the lcdaWards
     */
    public List<LcdaWards> getLcdaWards() {
        return lcdaWards;
    }

    /**
     * @param lcdaWards the lcdaWards to set
     */
    public void setLcdaWards(List<LcdaWards> lcdaWards) {
        this.lcdaWards = lcdaWards;
    }

    /**
     * @return the lcdaWard
     */
    public LcdaWards getLcdaWard() {
        return lcdaWard;
    }

    /**
     * @param lcdaWard the lcdaWard to set
     */
    public void setLcdaWard(LcdaWards lcdaWard) {
        this.lcdaWard = lcdaWard;
    }

    /**
     * @return the wardTowns
     */
    public List<WardTowns> getWardTowns() {
        return wardTowns;
    }

    /**
     * @param wardTowns the wardTowns to set
     */
    public void setWardTowns(List<WardTowns> wardTowns) {
        this.wardTowns = wardTowns;
    }

    /**
     * @return the wardTown
     */
    public WardTowns getWardTown() {
        return wardTown;
    }

    /**
     * @param wardTown the wardTown to set
     */
    public void setWardTown(WardTowns wardTown) {
        this.wardTown = wardTown;
    }

    /**
     * @return the wardStreets
     */
    public List<WardStreets> getWardStreets() {
        return wardStreets;
    }

    /**
     * @param wardStreets the wardStreets to set
     */
    public void setWardStreets(List<WardStreets> wardStreets) {
        this.wardStreets = wardStreets;
    }

    /**
     * @return the wardStreet
     */
    public WardStreets getWardStreet() {
        return wardStreet;
    }

    /**
     * @param wardStreet the wardStreet to set
     */
    public void setWardStreet(WardStreets wardStreet) {
        this.wardStreet = wardStreet;
    }

    public List<WardLandProperties> getWardProperties() {
        return wardProperties;
    }

    public void setWardProperties(List<WardLandProperties> wardProperties) {
        this.wardProperties = wardProperties;
    }

    /**
     * @return the selectedWard
     */
    public LcdaWards getSelectedWard() {
        return selectedWard;
    }

    /**
     * @param selectedWard the selectedWard to set
     */
    public void setSelectedWard(LcdaWards selectedWard) {
        this.selectedWard = selectedWard;
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
     * @return the wardName
     */
    public String getWardName() {
        return wardName;
    }

    /**
     * @param wardName the wardName to set
     */
    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    /**
     * @return the wardCode
     */
    public String getWardCode() {
        return wardCode;
    }

    /**
     * @param wardCode the wardCode to set
     */
    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
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

    /**
     * @return the legacySearch
     */
    public Boolean getLegacySearch() {
        return legacySearch;
    }

    /**
     * @param legacySearch the legacySearch to set
     */
    public void setLegacySearch(Boolean legacySearch) {
        this.legacySearch = legacySearch;
    }

    /**
     * @return the barelands
     */
    public List<BareLands> getBarelands() {
        return barelands;
    }

    /**
     * @param barelands the barelands to set
     */
    public void setBarelands(List<BareLands> barelands) {
        this.barelands = barelands;
    }

    /**
     * @return the landProperty
     */
    public WardLandProperties getLandProperty() {
        return landProperty;
    }

    /**
     * @param landProperty the landProperty to set
     */
    public void setLandProperty(WardLandProperties landProperty) {
        this.landProperty = landProperty;
    }

    /**
     * @return the allFieldOfficers
     */
    public List<Logindetails> getAllFieldOfficers() {
        return allFieldOfficers;
    }

    /**
     * @param allFieldOfficers the allFieldOfficers to set
     */
    public void setAllFieldOfficers(List<Logindetails> allFieldOfficers) {
        this.allFieldOfficers = allFieldOfficers;
    }

    public ValuationStatusEnum[] getValuationStatus() {
        return ValuationStatusEnum.values();
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

    public Boolean getShowSeacrhResult() {
        return showSeacrhResult;
    }

    public void setShowSeacrhResult(Boolean showSeacrhResult) {
        this.showSeacrhResult = showSeacrhResult;
    }

    /**
     * @return the selectProperties
     */
    public List<WardLandProperties> getSelectProperties() {
        return selectProperties;
    }

    /**
     * @param selectProperties the selectProperties to set
     */
    public void setSelectProperties(List<WardLandProperties> selectProperties) {
        this.selectProperties = selectProperties;
    }

    /**
     * @return the lazyProperties
     */
    public LazyDataModel<WardLandProperties> getLazyProperties() {
        return lazyProperties;
    }

    /**
     * @param lazyProperties the lazyProperties to set
     */
    public void setLazyProperties(LazyDataModel<WardLandProperties> lazyProperties) {
        this.lazyProperties = lazyProperties;
    }

    /**
     * @return the selectedDay
     */
    public String getSelectedDay() {
        return selectedDay;
    }

    /**
     * @param selectedDay the selectedDay to set
     */
    public void setSelectedDay(String selectedDay) {
        this.selectedDay = selectedDay;
    }

    /**
     * @return the starSearchDate
     */
    public Date getStarSearchDate() {
        return starSearchDate;
    }

    /**
     * @param starSearchDate the starSearchDate to set
     */
    public void setStarSearchDate(Date starSearchDate) {
        this.starSearchDate = starSearchDate;
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

    public int getResultIndex() {
        return resultIndex;
    }

    public void setResultIndex(int resultIndex) {
        this.resultIndex = resultIndex;
    }

    public Boolean getShowBlandResult() {
        return showBlandResult;
    }

    public void setShowBlandResult(Boolean showBlandResult) {
        this.showBlandResult = showBlandResult;
    }

    /**
     * @return the selectedBareland
     */
    public List<BareLands> getSelectedBareland() {
        return selectedBareland;
    }

    /**
     * @param selectedBareland the selectedBareland to set
     */
    public void setSelectedBareland(List<BareLands> selectedBareland) {
        this.selectedBareland = selectedBareland;
    }

    /**
     * @return the streetEstate
     */
    public WardStreets getStreetEstate() {
        return streetEstate;
    }

    /**
     * @param streetEstate the streetEstate to set
     */
    public void setStreetEstate(WardStreets streetEstate) {
        this.streetEstate = streetEstate;
    }

    public Boolean getFullResultSet() {
        return fullResultSet;
    }

    public void setFullResultSet(Boolean fullResultSet) {
        this.fullResultSet = fullResultSet;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
     * @return the wardProperty
     */
    public WardLandProperties getWardProperty() {
        return wardProperty;
    }

    /**
     * @param wardProperty the wardProperty to set
     */
    public void setWardProperty(WardLandProperties wardProperty) {
        this.wardProperty = wardProperty;
    }

    /**
     * @return the propServices
     */
    public List<PropertyServices> getPropServices() {
        return propServices;
    }

    /**
     * @param propServices the propServices to set
     */
    public void setPropServices(List<PropertyServices> propServices) {
        this.propServices = propServices;
    }

    /**
     * @return the notifications
     */
    public List<Notifications> getNotifications() {
        return notifications;
    }

    /**
     * @param notifications the notifications to set
     */
    public void setNotifications(List<Notifications> notifications) {
        this.notifications = notifications;
    }

    /**
     * @return the synced
     */
    public Boolean getSynced() {
        return synced;
    }

    /**
     * @param synced the synced to set
     */
    public void setSynced(Boolean synced) {
        this.synced = synced;
    }
}
