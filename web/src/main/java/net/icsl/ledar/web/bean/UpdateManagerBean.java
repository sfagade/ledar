package net.icsl.ledar.web.bean;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.text.WordUtils;
import net.icsl.ledar.ejb.enums.ApplicationEntityNames;
import net.icsl.ledar.ejb.enums.EducationalQualifications;
import net.icsl.ledar.ejb.enums.EmploymentStatus;
import net.icsl.ledar.ejb.enums.MarritalStatus;
import net.icsl.ledar.ejb.enums.PropertyTypes;
import net.icsl.ledar.ejb.enums.TarredRoads;
import net.icsl.ledar.ejb.enums.UntarredRoads;

import net.icsl.ledar.ejb.enums.UserActitiyName;
import net.icsl.ledar.ejb.enums.ValuationStatusEnum;
import net.icsl.ledar.ejb.enums.WasteDisposalSystem;
import net.icsl.ledar.ejb.enums.WaterSupply;
import net.icsl.ledar.ejb.model.BareLands;
import net.icsl.ledar.ejb.model.BarelandFiles;
import net.icsl.ledar.ejb.model.BiodataPersonTypes;
import net.icsl.ledar.ejb.model.CommercialTypes;
import net.icsl.ledar.ejb.model.DocumentTypes;
import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.NotificationPeople;
import net.icsl.ledar.ejb.model.Occupations;
import net.icsl.ledar.ejb.model.OrganizationTypes;
import net.icsl.ledar.ejb.model.Organizations;
import net.icsl.ledar.ejb.model.PersonTitles;
import net.icsl.ledar.ejb.model.PropertyBiodatas;
import net.icsl.ledar.ejb.model.PropertyClassificationDetails;
import net.icsl.ledar.ejb.model.PropertyClassifications;
import net.icsl.ledar.ejb.model.PropertyDocuments;
import net.icsl.ledar.ejb.model.PropertyQualities;
import net.icsl.ledar.ejb.model.PropertyServices;
import net.icsl.ledar.ejb.model.ResidentialTypes;
import net.icsl.ledar.ejb.model.ResidentialUseages;
import net.icsl.ledar.ejb.model.ServiceTypes;

import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.model.WardLandProperties;
import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.NotificationDataService;
import net.icsl.ledar.ejb.service.ReferenceDataService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.web.util.ApplicationUtility;
import net.icsl.ledar.web.util.FacesSupportUtil;
import org.primefaces.model.UploadedFile;

@Named("updateManageBean")
@SessionScoped
public class UpdateManagerBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final FacesContext facesContext = FacesContext.getCurrentInstance();

    @Inject
    private LandPropertiesDataService landPropService;
    @Inject
    private LcdaWardsDataServices lcdaWardService;
    @Inject
    private ReferenceDataService refDataService;
    @Inject
    private NotificationDataService notificService;
    @Inject
    private RegisteredUsersDataService registeredService;

    @Inject
    private LoginBean loginBean;
    @Inject
    private LedarApplicationBean ledarApp;
    @Inject
    private UploadedFilesBean uploadBean;

    private LcdaWards selected_ward;
    private LocalCouncilDevArea selected_lcda;
    private PersonTitles selectedTitle;
    private WardLandProperties landProperty;
    private WardStreets wardStreet;
    private PropertyBiodatas biodataPerson;
    private Organizations organization;
    private BareLands beland;
    private PropertyClassificationDetails buildingDetail;

    private List<WardStreets> wardStreets;
    private List<LocalCouncilDevArea> lcdArea;
    private List<LcdaWards> lcdaWards;
    private List<PropertyServices> propServices;
    private List<ServiceTypes> serviceTypes;
    private List<ServiceTypes> selectedServiceTypes;
    private List<PropertyQualities> propertyRatings;
    private List<PropertyClassificationDetails> propDetails;
    private List<BiodataPersonTypes> bioPersonTypes;
    private List<Occupations> occupations;
    private List<CommercialTypes> commerceType;
    private List<ResidentialTypes> residenceType;
    private List<Logindetails> allFieldOfficers;
    private List<PropertyClassifications> propertyUses;
    private List<PersonTitles> personTitle;
    private List<PropertyQualities> propQualities;
    private List<OrganizationTypes> organTypes;
    private List<PropertyDocuments> propertyDocs;
    private List<ResidentialUseages> resideUsage;

    private String blockPlot, propertyBillId, biodataId, tarredRoadCondition, untarredRoadCondition, buildingCondition, firstName, lastName, selectedGender;
    private Boolean isIrregular;
    private UploadedFile rviewPic, fviewPic, sviewPic;

    public UpdateManagerBean() {
        // TODO Auto-generated constructor stub
    }

    @PostConstruct
    public void init() {
        String view_id = facesContext.getViewRoot().getViewId();

        if (view_id.equals("/app/appAdmin/updatePropertyInfo.xhtml")) {

        } else if (view_id.equals("/app/dataEntry/createNewProperty.xhtml")) {

        }

    }

    public void updateBuildingInfo() {
        List<PropertyClassificationDetails> buildings = new ArrayList<>();

        buildingDetail.setLastUpdatedById(loginBean.getPerson().getLogindetailId());
        buildings.add(buildingDetail);

        UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.UPDATEBILDINGINFO.toString(), new Date(), loginBean.getPerson().getFullName() + " edited building information, "
                + "property ID is: " + buildingDetail.getWardLandPropertyId().getPropertyId(), loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, null, loginBean.getPerson().getLogindetailId(), null, null);

        activity.setEntityName(ApplicationEntityNames.PROPERTYBUILDING.toString());
        activity.setEntityId(buildingDetail.getId());

        FacesContext context = FacesContext.getCurrentInstance();

        if (landPropService.updateBuildingDetailsInfo(buildings, activity)) {

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Building information updated successfully", "Success"));
            context.getExternalContext().getFlash().setKeepMessages(true);

            try {
                context.getExternalContext().redirect("viewPropertyDetails.xhtml?propId=" + buildingDetail.getWardLandPropertyId().getId());
            } catch (IOException ex) {
                Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to update building information, please try again later", "Error"));
            context.getExternalContext().getFlash().setKeepMessages(true);
        }

    }

    public void initializeUpdate() {

        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        propertyBillId = params.get("action");

        if (propertyBillId != null) {
            landProperty = landPropService.findWardPropertyById(Long.valueOf(propertyBillId));
            if (landProperty.getWardStreetId() != null) {
                wardStreets = lcdaWardService.fetchWardStreets(landProperty.getWardStreetId().getLcdaWardId().getId(), null, Boolean.TRUE);
            }

            organization = (landProperty.getOwnerOrganizationId() != null) ? landProperty.getOwnerOrganizationId() : new Organizations();
            biodataPerson = (landProperty.getPropertyBiodataId() != null) ? landProperty.getPropertyBiodataId() : new PropertyBiodatas();

            if (biodataPerson != null) {
                this.firstName = biodataPerson.getFirstName();
                this.lastName = biodataPerson.getLastName();
            }

            propertyRatings = refDataService.fetchAllPropertyQualities();
            selectedServiceTypes = new ArrayList<>();

            if (landProperty.getPropertyTypes().equals("SERVICED")) {
                propServices = landPropService.fetchPropertyServicesByPropertyId(landProperty.getId());
                //selectedServiceTypes = propServices.
                if (propServices != null) {
                    for (PropertyServices propServ : propServices) {
                        selectedServiceTypes.add(propServ.getServiceTypeId());
                    }
                }
            }

            serviceTypes = refDataService.fetchAllServiceTypes();
            if (bioPersonTypes == null) {
                bioPersonTypes = refDataService.fetchAllBiodataPersonTypes();
            }
            if (personTitle == null) {
                personTitle = refDataService.fetchAllTittle();
            }
            if (organTypes == null) {
                organTypes = refDataService.fetchAllOrganizationTypes();
            }

            try {
                context.getExternalContext().redirect("updatePropertyInfo.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void updatePropertyInfo() {

        String client_agant = FacesSupportUtil.getClientAgent();

        List<WardLandProperties> existingProp = landPropService.fetchAllPropertiesByPin(landProperty.getPropertyId(), null);
        Boolean is_valid_pin = Boolean.FALSE;
        FacesContext context = FacesContext.getCurrentInstance();

        if (existingProp != null && existingProp.size() > 0) {
            for (WardLandProperties property : existingProp) {
                if (property.getId().compareTo(landProperty.getId()) == 0) { //there's another property with the same pin
                    is_valid_pin = Boolean.TRUE;
                    break;
                }
            }
        } else {
            is_valid_pin = Boolean.TRUE;
        }
        if (is_valid_pin) {
            if (landProperty.getWardStreetId() != null || (landProperty.getIrregularAddress() != null && !landProperty.getIrregularAddress().isEmpty())) {
                UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.UPDATEDPROPERTY.toString(), new Date(), loginBean.getPerson().getFullName() + " edited property information, "
                        + "property ID is: " + landProperty.getId(), loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, client_agant, loginBean.getPerson().getLogindetailId(), null, null);

                activity.setEntityName(ApplicationEntityNames.WARDLANDPROPERTIES.toString());
                activity.setEntityId(landProperty.getId());

                landProperty.setUntarredRoad(!landProperty.getTarredRoad());

                landProperty.setRoadCondition((landProperty.getTarredRoad()) ? tarredRoadCondition : untarredRoadCondition);
                landProperty.setIsBlockNumber((blockPlot != null && blockPlot.equals("Block")));
                landProperty.setLastUpdatedById(loginBean.getPerson().getLogindetailId());
                List<PropertyServices> newServices = new ArrayList<>();

                if (landProperty.getPropertyTypes().equals("SERVICED")) {
                    if (selectedServiceTypes.size() <= 0) { //user has not selected any server, we should return error
                        context.addMessage("updProp:grid", new FacesMessage(FacesMessage.SEVERITY_ERROR, "You must select at least one Service Type for SERVICED properties! ", "Error"));
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        return;
                    }
                    for (ServiceTypes servType : selectedServiceTypes) {
                        newServices.add(new PropertyServices(null, null, servType, landProperty, null));
                    }
                }

                if (landProperty.getOwnershipType() != null && landProperty.getOwnershipType().equals("INDIVIDUAL")) {
                    if ((getFirstName() != null && getLastName() != null && !getLastName().isEmpty() && !getFirstName().isEmpty()) && (biodataPerson.getBiodataPersonId().getBiodataTypeName() != null)) {
                        biodataPerson.setFirstName(getFirstName().toUpperCase());
                        biodataPerson.setLastName(getLastName().toUpperCase());
                        biodataPerson.setMobilePhoneNumber((biodataPerson.getMobilePhoneNumber() != null) ? ApplicationUtility.formatPhoneNumber(biodataPerson.getMobilePhoneNumber(), null) : null);
                        biodataPerson.setHomePhoneNumber((biodataPerson.getHomePhoneNumber() != null) ? ApplicationUtility.formatPhoneNumber(biodataPerson.getHomePhoneNumber(), null) : null);
                        biodataPerson.setGender(selectedGender);
                        landProperty.setPropertyBiodataId(biodataPerson);
                        biodataPerson.setLastUpdatedById(loginBean.getPerson().getLogindetailId());
                    } else {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please make sure you fill every required field in the Biodata Section", "Error"));
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        return;
                    }
                } else if (landProperty.getOwnershipType() != null && landProperty.getOwnershipType().equals("ORGANIZATION")) {
                    if ((organization != null) && (organization.getOrganizationNm() != null && !organization.getOrganizationNm().isEmpty()) && (organization.getOrganizationNumber() != null)) {
                        organization.setOrganzaitionCode(WordUtils.initials(organization.getOrganizationNm()).toUpperCase());
                        landProperty.setOwnerOrganizationId(organization);
                        biodataPerson = null;
                    } else {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please make sure you fill every required field in the Organization Section", "Error"));
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        return;
                    }
                }

                Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.INFO, "{0} about to update User Information", loginBean.getPerson().getFullName());
                if (landPropService.updateWardLandPropertyInfo(landProperty, newServices, activity)) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Property Information has been updated successfully: " + landProperty.getId(), "Success"));
                    context.getExternalContext().getFlash().setKeepMessages(true);

                    String prop_id = landProperty.getId().toString();
                    landProperty = null;
                    propServices = null;
                    selectedServiceTypes = null;
                    propertyRatings = null;

                    try {
                        context.getExternalContext().redirect("viewPropertyDetails.xhtml?propId=" + prop_id);
                    } catch (IOException ex) {
                        Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to update property information, please try again later", "Error"));
                    context.getExternalContext().getFlash().setKeepMessages(true);
                }
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Street validation failed, please try again later", "Error"));
                context.getExternalContext().getFlash().setKeepMessages(true);
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pin Entered has been used for another Property, please try again later", "Error"));
            context.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void initializeBuildingUpdate() {
        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String building_id = params.get("bldid");

        if (building_id != null) {
            buildingDetail = landPropService.findBuildingDetailsById(Long.valueOf(building_id));
            if (propertyUses == null) {
                propertyUses = refDataService.fetchAllPropertyUse();
                commerceType = refDataService.fetchAllcommercialTypes();
                residenceType = refDataService.fetchAllResidentialTypes();
                resideUsage = refDataService.fetchAllResidentialUseages();
            }
            try {
                context.getExternalContext().redirect("updateBuildingInfo.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void initializePicUpdate() {
        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        biodataId = params.get("prpid");

        if (biodataId != null) {
            propertyDocs = landPropService.findAllPropertyDocumentsByPropertyId(Long.valueOf(biodataId), false);
            try {
                context.getExternalContext().redirect("updatePropertyDocs.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void initLandUpdate() {
        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        propertyBillId = params.get("action");

        if (propertyBillId != null) {
            beland = landPropService.findBareLandById(Long.valueOf(propertyBillId));
        }
    }

    public List<WardStreets> testAutoComplete(String query) {
        List<WardStreets> allValues = lcdaWardService.fetchWardStreetsByStreetName(query, loginBean.getPerson().getOrganization().getId(), false);

        return allValues;
    }

    public void changedLcda() {
        //System.out.println("called changeLcda: " + selected_lcda);
        if (selected_lcda != null) {
            this.setLcdaWards(lcdaWardService.fetchAllLcdaWardsByLcda(selected_lcda.getId(), null));
        }
    }

    public void changedWardStreets() {

        if (selected_ward != null) {
            this.setWardStreets(lcdaWardService.fetchWardStreets(selected_ward.getId(), null, Boolean.TRUE));
        }
    }

    public void addMoreBuilding() {
        if (propDetails == null) {
            propDetails = new ArrayList<>();
        }
        propDetails.add(new PropertyClassificationDetails());
    }

    public void saveNewPropertyData() {

        if (landProperty != null) {

            List<PropertyDocuments> propDocs = new ArrayList<>();
            DocumentTypes docType = refDataService.findDocumentTitleTypeById(1L); //id for property pic
            List<PropertyServices> propServices = new ArrayList<>();
            FacesContext context = FacesContext.getCurrentInstance();
            boolean pic_provided = false;
            byte[] contents;

            if (fviewPic != null) {
                try {
                    contents = FacesSupportUtil.resizeImage(fviewPic).toByteArray();

                    try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(uploadBean.getProp().getProperty("imageURl") + "/" + fviewPic.getFileName())))) {
                        stream.write(contents);
                    }

                    propDocs.add(new PropertyDocuments(null, fviewPic.getFileName(), null, landProperty, docType, new FileUploads(null, null, fviewPic.getFileName(), fviewPic.getContentType(),
                            "Front View", null, loginBean.getPerson().getLogindetailId(), null, null), null, null));
                    pic_provided = true;
                } catch (IOException ex) {
                    Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (sviewPic != null) {
                try {
                    contents = FacesSupportUtil.resizeImage(sviewPic).toByteArray();

                    try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(uploadBean.getProp().getProperty("imageURl") + "/" + sviewPic.getFileName())))) {
                        stream.write(contents);
                    }
                    propDocs.add(new PropertyDocuments(null, sviewPic.getFileName(), null, landProperty, docType, new FileUploads(null, null, sviewPic.getFileName(), sviewPic.getContentType(),
                            "Side View", null, loginBean.getPerson().getLogindetailId(), null, null), null, null));
                    pic_provided = true;
                } catch (IOException ex) {
                    Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rviewPic != null) {
                try {
                    contents = FacesSupportUtil.resizeImage(rviewPic).toByteArray();

                    try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(uploadBean.getProp().getProperty("imageURl") + "/" + rviewPic.getFileName())))) {
                        stream.write(contents);
                    }
                    propDocs.add(new PropertyDocuments(null, rviewPic.getFileName(), null, landProperty, docType, new FileUploads(null, null, rviewPic.getFileName(), rviewPic.getContentType(),
                            "Rear View", null, loginBean.getPerson().getLogindetailId(), null, null), null, null));
                    pic_provided = true;
                } catch (IOException ex) {
                    Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (pic_provided) {
                UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.SAVEDENUMERATION.toString(), new Date(), "Saved Enumeration Information for property",
                        FacesSupportUtil.getClientIpAddress(), loginBean.getPerson().getLogindetailId().getLastLoginLongitude(), loginBean.getPerson().getLogindetailId().getLastLoginLatitude(),
                        null, loginBean.getPerson().getLogindetailId(), null, null);

                activity.setEntityName(ApplicationEntityNames.WARDLANDPROPERTIES.toString());
                activity.setEntityId(landProperty.getId());
                activity.setLatitude(landProperty.getPropertyLatitude());
                activity.setLongitude(landProperty.getPropertyLongitude());

                if (landProperty.getPropertyTypes().equals("SERVICED")) {
                    for (ServiceTypes servType : selectedServiceTypes) {
                        propServices.add(new PropertyServices(null, null, servType, landProperty, null));
                    }
                }

                landProperty.setIsIrregularAddress(isIrregular);
                landProperty.setIsInitsSynced(false);
                landProperty.setContractorId(loginBean.getPerson().getOrganization());
                landProperty.setEnteredById(loginBean.getPerson().getLogindetailId());
                landProperty.setPropertyNumber(landProperty.getPropertyNumber());
                landProperty.setPropertyValuationStatus(ValuationStatusEnum.PENDING.toString());
                landProperty.setPropertyId(landProperty.getPropertyId().replaceAll("[-+.^:,]", ""));

                if (isIrregular) {
                    //TODO I need to save the new street here
                    if (selected_ward != null && (landProperty.getIrregularAddress() != null)) {
                        wardStreet = new WardStreets();

                        UsersLastActivities street_activity = new UsersLastActivities(null, UserActitiyName.NEWSTREET.toString(), new Date(), loginBean.getPerson().getFullName() + " saved new street"
                                + " information", loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, null, loginBean.getPerson().getLogindetailId(), null, null);

                        street_activity.setEntityName(ApplicationEntityNames.WARDSTREET.toString());
                        street_activity.setEntityId(wardStreet.getId());

                        wardStreet.setCreatedById(loginBean.getPerson().getLogindetailId());
                        wardStreet.setStreetName(landProperty.getIrregularAddress().toUpperCase());
                        wardStreet.setIsVerified(false);
                        wardStreet.setLcdaWardId(selected_ward);

                        landProperty.setIrregularAddress(landProperty.getIrregularAddress().toUpperCase());
                        landProperty.setPropertyValuationStatus(ValuationStatusEnum.PENDING.toString());

                        wardStreet = lcdaWardService.saveNewStreetInformation(wardStreet, street_activity);

                        if (wardStreet != null) {
                            wardStreet = null;
                        }

                    } else {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide District and Irregular Street name before saving the form", "Error"));
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        return;
                    }
                } else if (landProperty.getWardStreetId() == null) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please either select a Street from the dropdown provided or enter a new street name", "Error"));
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    return;
                }

                if ((propDetails != null) && (propDetails.size() > 0)) {
                    for (PropertyClassificationDetails propDetail : propDetails) {

                        if ((propDetail.getPropertyClassificationId() == null)) {

                        }

                        propDetail.setPropertyName(propDetail.getPropertyName().toUpperCase());
                        propDetail.setWardLandPropertyId(landProperty);
                    }

                    if (landProperty.getOwnershipType() != null && (landProperty.getOwnershipType().equals("INDIVIDUAL"))) {
                        if (biodataPerson.getFirstName() != null && biodataPerson.getLastName() != null
                                && (biodataPerson.getBiodataPersonId().getBiodataTypeName() != null)) {
                            biodataPerson.setFirstName(biodataPerson.getFirstName().toUpperCase());
                            biodataPerson.setLastName(biodataPerson.getLastName().toUpperCase());
                            biodataPerson.setMobilePhoneNumber((biodataPerson.getMobilePhoneNumber() != null) ? ApplicationUtility.formatPhoneNumber(biodataPerson.getMobilePhoneNumber(), null) : null);
                            biodataPerson.setHomePhoneNumber((biodataPerson.getHomePhoneNumber() != null) ? ApplicationUtility.formatPhoneNumber(biodataPerson.getHomePhoneNumber(), null) : null);
                            landProperty.setPropertyBiodataId(biodataPerson);
                            landProperty.setOwnerOrganizationId(null);
                        } else {
                            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please make sure you fill every required field in the Biodata Section", "Error"));
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            return;
                        }
                    } else if (landProperty.getOwnershipType() != null && (landProperty.getOwnershipType().equals("ORGANIZATION"))) {
                        if ((organization != null) && (organization.getOrganizationNm() != null) && (organization.getOrganizationNumber() != null)) {
                            organization.setOrganizationNm(organization.getOrganizationNm().toUpperCase());
                            organization.setOrganzaitionCode(WordUtils.initials(organization.getOrganizationNm()));
                            organization.setOrganizationNumber(ApplicationUtility.formatPhoneNumber(organization.getOrganizationNumber(), null));
                            landProperty.setOwnerOrganizationId(organization);
                            biodataPerson = null;
                        } else {
                            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please make sure you fill every required field in the Organization Section", "Error"));
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            return;
                        }
                    }

                    landProperty = landPropService.savePropertyData(landProperty, biodataPerson, propDetails, propDocs, propServices, activity);

                    if (landProperty != null) {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Property Information has been saved successfully: " + landProperty.getId(), "Success"));
                        context.getExternalContext().getFlash().setKeepMessages(true);

                        landProperty = new WardLandProperties();
                        propDetails = new ArrayList<>();
                        biodataPerson = new PropertyBiodatas(null);

                        try {
                            context.getExternalContext().redirect("createNewProperty.xhtml");
                        } catch (IOException ex) {
                            Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save property information, please try again later", "Error"));
                        context.getExternalContext().getFlash().setKeepMessages(true);
                    }
                } else {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Building information cannot be empty", "Error"));
                    context.getExternalContext().getFlash().setKeepMessages(true);
                }

                Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.INFO, "submitted form for new property", "submitted form for new property");
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You need to provide at least one Property Picture", "Error"));
                context.getExternalContext().getFlash().setKeepMessages(true);
            }
        }
    }

    public void verifyProperty() {

        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        biodataId = params.get("prpid");

        if (biodataId != null) {
            landProperty = landPropService.findWardPropertyById(Long.valueOf(biodataId));

            landProperty.setIsVerified(true);
            landProperty.setVerifiedById(loginBean.getPerson().getLogindetailId());
            landProperty.setVerifiedDate(new Date());
            landProperty.setPropertyValuationStatus(ValuationStatusEnum.SENT.toString());
            landProperty.setLastUpdatedById(loginBean.getPerson().getLogindetailId());

            UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.UPDATEDPROPERTY.toString(), new Date(), loginBean.getPerson().getFullName() + " has updated property"
                    + " information to Verified, property ID is: " + landProperty.getId(), loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, FacesSupportUtil.getClientAgent(),
                    loginBean.getPerson().getLogindetailId(), null, null);

            activity.setEntityName(ApplicationEntityNames.WARDLANDPROPERTIES.toString());
            activity.setEntityId(landProperty.getId());

            if (landPropService.updateWardLandPropertyInfo(landProperty, null, activity)) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Property Information has been pushed successfully: " + landProperty.getId(), "Success"));
                context.getExternalContext().getFlash().setKeepMessages(true);

                String prop_id = landProperty.getId().toString();

                //List<NotificationPeople> notificPeople = notificService.fetchAllNotificationPeopleByAction("VALUATION PUSH");
                List<NotificationPeople> notificPeople = notificService.fetchAllNotificationPeopleByAction("TEST MAILER");
                String[] email_addresses = new String[notificPeople.size()];
                int counter = 0;
                for (NotificationPeople notificPerson : notificPeople) {
                    email_addresses[counter] = notificPerson.getEmailAddress();
                    counter++;
                }

                try {
                    String email_message = loginBean.getPerson().getFullName() + " in " + loginBean.getPerson().getOrganization().getOrganzaitionCode() + " has sent a newly enumerated property "
                            + "information for valuation. The information can be accessed by <a href=\"http://" + FacesContext.getCurrentInstance().getExternalContext().getRequestServerName() + ":"
                            + FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort() + FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()
                            + "/app/valuations/viewPropertyDetails.xhtml?propId=" + prop_id + "\">clicking here</a>. Please note that you will be required to provide your login credentials before seeing "
                            + "this property information.<br /><br />Thank You.";

                    //new EmailSender().sendPlainEmailMessage(email_message, "New " + loginBean.getPerson().getOrganization().getOrganzaitionCode() + " Enumeration Sent", null);
                    context.getExternalContext().redirect("viewPropertyDetails.xhtml?propId=" + prop_id);
                } catch (IOException ex) {
                    Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                landProperty = new WardLandProperties(null);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to push property information, please try again later", "Error"));
                context.getExternalContext().getFlash().setKeepMessages(true);
            }

        }

    }

    public void initNewProperty() {

        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "DATA ENTRY"};
        if (!FacesSupportUtil.isUserInRole(accepted_roles)) {

            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("../index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (landProperty == null || biodataPerson == null || organization == null) {
            landProperty = new WardLandProperties();

            organization = new Organizations();
            biodataPerson = new PropertyBiodatas(null);

            propDetails = new ArrayList<>();
            propDetails.add(new PropertyClassificationDetails());

        }

        if (bioPersonTypes == null) {
            bioPersonTypes = refDataService.fetchAllBiodataPersonTypes();
            occupations = refDataService.fetchAllOccupations();
            commerceType = refDataService.fetchAllcommercialTypes();
            residenceType = refDataService.fetchAllResidentialTypes();
            propertyUses = refDataService.fetchAllPropertyUse();
            serviceTypes = refDataService.fetchAllServiceTypes();
            personTitle = refDataService.fetchAllTittle();
            propQualities = refDataService.fetchAllPropertyQualities();
            organTypes = refDataService.fetchAllOrganizationTypes();
            resideUsage = refDataService.fetchAllResidentialUseages();

            Long[] field_roles = {9L, 19L, 26L}; //all field officers ID?
            allFieldOfficers = registeredService.fetchAllLcdaChairmen(Arrays.asList(field_roles), loginBean.getPerson().getOrganization().getId());
        }
    }

    public void initNewBareland() {

        if (beland == null) {
            beland = new BareLands(null);
            biodataPerson = new PropertyBiodatas(null);

            organization = new Organizations();
        }

        if (bioPersonTypes == null) {
            bioPersonTypes = refDataService.fetchAllBiodataPersonTypes();
            occupations = refDataService.fetchAllOccupations();
            personTitle = refDataService.fetchAllTittle();
            organTypes = refDataService.fetchAllOrganizationTypes();
            propQualities = refDataService.fetchAllPropertyQualities();

            Long[] field_roles = {9L, 19L, 26L}; //all field officers ID?
            allFieldOfficers = registeredService.fetchAllLcdaChairmen(Arrays.asList(field_roles), loginBean.getPerson().getOrganization().getId());
        }
    }

    public void saveNewBarelandData() {
        if (beland != null) { //this should always be the case
            FacesContext context = FacesContext.getCurrentInstance();
            List<BarelandFiles> blandFiles = new ArrayList<>();
            System.out.println("Abuot to save BareLand");
            if (beland.getOwnershipType() != null && beland.getOwnershipType().equals("ORGANIZATION")) {
                if (organization.getOrganizationNm() != null && organization.getOrganizationTypeId() != null && organization.getOrganizationNumber() != null && organization.getEmailAddress() != null) {
                    organization.setOrganizationNm(organization.getOrganizationNm().toUpperCase());
                    organization.setOrganizationNumber(ApplicationUtility.formatPhoneNumber(organization.getOrganizationNumber(), null));
                    organization.setOrganzaitionCode(WordUtils.initials(organization.getOrganizationNm()));
                    beland.setOwnerOrganizationId(organization);
                    beland.setPropertyBiodataId(null);
                } else {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please make sure you've fill the organization section properly", "Error"));
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    return;
                }
            } else if (beland.getOwnershipType() != null && beland.getOwnershipType().equals("INDIVIDUAL")) {
                if (biodataPerson.getFirstName() != null && biodataPerson.getLastName() != null && (biodataPerson.getGender() != null)
                        && (biodataPerson.getBiodataPersonId().getBiodataTypeName() != null)) {
                    biodataPerson.setFirstName(biodataPerson.getFirstName().toUpperCase());
                    biodataPerson.setLastName(biodataPerson.getLastName().toUpperCase());
                    biodataPerson.setMobilePhoneNumber((biodataPerson.getMobilePhoneNumber() != null) ? ApplicationUtility.formatPhoneNumber(biodataPerson.getMobilePhoneNumber(), null) : null);
                    biodataPerson.setHomePhoneNumber((biodataPerson.getHomePhoneNumber() != null) ? ApplicationUtility.formatPhoneNumber(biodataPerson.getHomePhoneNumber(), null) : null);
                    beland.setPropertyBiodataId(biodataPerson);
                    beland.setOwnerOrganizationId(null);
                } else {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please make sure you've fill the individual owner section properly", "Error"));
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    return;
                }
            }

            if (fviewPic != null) {
                blandFiles.add(new BarelandFiles(null, fviewPic.getContentType(), beland, new FileUploads(null, fviewPic.getContents(), fviewPic.getFileName(), fviewPic.getContentType(), "Front View", null,
                        loginBean.getPerson().getLogindetailId(), null, null), null, null));
            }

            if (sviewPic != null) {
                blandFiles.add(new BarelandFiles(null, sviewPic.getContentType(), beland, new FileUploads(null, sviewPic.getContents(), sviewPic.getFileName(), sviewPic.getContentType(), "Side View", null,
                        loginBean.getPerson().getLogindetailId(), null, null), null, null));
            }
            if (rviewPic != null) {
                blandFiles.add(new BarelandFiles(null, rviewPic.getContentType(), beland, new FileUploads(null, rviewPic.getContents(), rviewPic.getFileName(), rviewPic.getContentType(), "Rear View", null,
                        loginBean.getPerson().getLogindetailId(), null, null), null, null));
            }

            UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.SAVEDBARELAND.toString(), new Date(), loginBean.getPerson().getFullName() + " has saved new Bareland information, the Bareland id is: " + beland.getId(), loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, FacesSupportUtil.getClientAgent(),
                    loginBean.getPerson().getLogindetailId(), null, null);

            activity.setEntityName(ApplicationEntityNames.BARELAND.toString());
            activity.setEntityId(beland.getId());

            beland = landPropService.saveBareLandData(beland, blandFiles, activity);

            if (beland != null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Land Information has been saved successfully: " + beland.getId(), "Success"));
                context.getExternalContext().getFlash().setKeepMessages(true);

                beland = null;
                this.initNewBareland();

                try {
                    context.getExternalContext().redirect("createBareland.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save land information, please try again later", "Error"));
                context.getExternalContext().getFlash().setKeepMessages(true);
            }
        }

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

    public String getBuildingCondition() {
        return buildingCondition;
    }

    public void setBuildingCondition(String buildingCondition) {
        this.buildingCondition = buildingCondition;
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

    public List<WardStreets> getWardStreets() {
        return wardStreets;
    }

    public void setWardStreets(List<WardStreets> wardStreets) {
        this.wardStreets = wardStreets;
    }

    /**
     * @return the selectedTitle
     */
    public PersonTitles getSelectedTitle() {
        return selectedTitle;
    }

    /**
     * @param selectedTitle the selectedTitle to set
     */
    public void setSelectedTitle(PersonTitles selectedTitle) {
        this.selectedTitle = selectedTitle;
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

    /**
     * @return the blockPlot
     */
    public String getBlockPlot() {
        return blockPlot;
    }

    /**
     * @param blockPlot the blockPlot to set
     */
    public void setBlockPlot(String blockPlot) {
        this.blockPlot = blockPlot;
    }

    /**
     * @return the lcdArea
     */
    public List<LocalCouncilDevArea> getLcdArea() {

        if (lcdArea == null && loginBean.getPerson() != null) {
            lcdArea = lcdaWardService.fetchAllSenatorialDistrictLCDAs(loginBean.getPerson().getOrganization().getSenatorialDistrictId().getId());
        }

        return lcdArea;
    }

    /**
     * @param lcdArea the lcdArea to set
     */
    public void setLcdArea(List<LocalCouncilDevArea> lcdArea) {
        this.lcdArea = lcdArea;
    }

    public List<LcdaWards> getLcdaWards() {
        return lcdaWards;
    }

    public void setLcdaWards(List<LcdaWards> lcdaWards) {
        this.lcdaWards = lcdaWards;
    }

    /**
     * @return the biodataId
     */
    public String getBiodataId() {
        return biodataId;
    }

    /**
     * @param biodataId the biodataId to set
     */
    public void setBiodataId(String biodataId) {
        this.biodataId = biodataId;
    }

    /**
     * @return the biodataPerson
     */
    public PropertyBiodatas getBiodataPerson() {
        return biodataPerson;
    }

    /**
     * @param biodataPerson the biodataPerson to set
     */
    public void setBiodataPerson(PropertyBiodatas biodataPerson) {
        this.biodataPerson = biodataPerson;
    }

    public String getTarredRoadCondition() {
        return tarredRoadCondition;
    }

    public void setTarredRoadCondition(String tarredRoadCondition) {
        this.tarredRoadCondition = tarredRoadCondition;
    }

    public String getUntarredRoadCondition() {
        return untarredRoadCondition;
    }

    public void setUntarredRoadCondition(String untarredRoadCondition) {
        this.untarredRoadCondition = untarredRoadCondition;
    }

    public List<PropertyServices> getPropServices() {
        return propServices;
    }

    public void setPropServices(List<PropertyServices> propServices) {
        this.propServices = propServices;
    }

    public List<ServiceTypes> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(List<ServiceTypes> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    public List<ServiceTypes> getSelectedServiceTypes() {
        return selectedServiceTypes;
    }

    public void setSelectedServiceTypes(List<ServiceTypes> selectedServiceTypes) {
        this.selectedServiceTypes = selectedServiceTypes;
    }

    /**
     * @return the propertyRatings
     */
    public List<PropertyQualities> getPropertyRatings() {
        return propertyRatings;
    }

    /**
     * @param propertyRatings the propertyRatings to set
     */
    public void setPropertyRatings(List<PropertyQualities> propertyRatings) {
        this.propertyRatings = propertyRatings;
    }

    public List<PropertyClassificationDetails> getPropDetails() {
        return propDetails;
    }

    public void setPropDetails(List<PropertyClassificationDetails> propDetails) {
        this.propDetails = propDetails;
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

    /**
     * @return the commerceType
     */
    public List<CommercialTypes> getCommerceType() {
        return commerceType;
    }

    /**
     * @param commerceType the commerceType to set
     */
    public void setCommerceType(List<CommercialTypes> commerceType) {
        this.commerceType = commerceType;
    }

    /**
     * @return the rviewPic
     */
    public UploadedFile getRviewPic() {
        return rviewPic;
    }

    /**
     * @param rviewPic the rviewPic to set
     */
    public void setRviewPic(UploadedFile rviewPic) {
        this.rviewPic = rviewPic;
    }

    public List<PropertyClassifications> getPropertyUses() {
        return propertyUses;
    }

    public void setPropertyUses(List<PropertyClassifications> propertyUses) {
        this.propertyUses = propertyUses;
    }

    /**
     * @return the fviewPic
     */
    public UploadedFile getFviewPic() {
        return fviewPic;
    }

    /**
     * @param fviewPic the fviewPic to set
     */
    public void setFviewPic(UploadedFile fviewPic) {
        this.fviewPic = fviewPic;
    }

    /**
     * @return the sviewPic
     */
    public UploadedFile getSviewPic() {
        return sviewPic;
    }

    /**
     * @param sviewPic the sviewPic to set
     */
    public void setSviewPic(UploadedFile sviewPic) {
        this.sviewPic = sviewPic;
    }

    /**
     * @return the bioPersonTypes
     */
    public List<BiodataPersonTypes> getBioPersonTypes() {
        return bioPersonTypes;
    }

    /**
     * @param bioPersonTypes the bioPersonTypes to set
     */
    public void setBioPersonTypes(List<BiodataPersonTypes> bioPersonTypes) {
        this.bioPersonTypes = bioPersonTypes;
    }

    /**
     * @return the occupations
     */
    public List<Occupations> getOccupations() {
        return occupations;
    }

    /**
     * @param occupations the occupations to set
     */
    public void setOccupations(List<Occupations> occupations) {
        this.occupations = occupations;
    }

    public MarritalStatus[] getMstatus() {
        return MarritalStatus.values();
    }

    public EducationalQualifications[] getEducationQualifications() {
        return EducationalQualifications.values();
    }

    public EmploymentStatus[] getEmploymentStatus() {
        return EmploymentStatus.values();
    }

    public WaterSupply[] getWaterSupply() {
        return WaterSupply.values();
    }

    public WasteDisposalSystem[] getWasteDisposalSystem() {
        return WasteDisposalSystem.values();
    }

    public TarredRoads[] getTarredRoads() {
        return TarredRoads.values();
    }

    public UntarredRoads[] getUntarredRoads() {
        return UntarredRoads.values();
    }

    /**
     * @return the residenceType
     */
    public List<ResidentialTypes> getResidenceType() {
        return residenceType;
    }

    /**
     * @param residenceType the residenceType to set
     */
    public void setResidenceType(List<ResidentialTypes> residenceType) {
        this.residenceType = residenceType;
    }

    /**
     * @return the isIrregular
     */
    public Boolean getIsIrregular() {
        return isIrregular;
    }

    /**
     * @param isIrregular the isIrregular to set
     */
    public void setIsIrregular(Boolean isIrregular) {
        this.isIrregular = isIrregular;
    }

    public PropertyTypes[] getPropertyTypes() {
        return PropertyTypes.values();
    }

    /**
     * @return the personTitle
     */
    public List<PersonTitles> getPersonTitle() {
        return personTitle;
    }

    /**
     * @param personTitle the personTitle to set
     */
    public void setPersonTitle(List<PersonTitles> personTitle) {
        this.personTitle = personTitle;
    }

    public List<PropertyQualities> getPropQualities() {
        return propQualities;
    }

    public void setPropQualities(List<PropertyQualities> propQualities) {
        this.propQualities = propQualities;
    }

    /**
     * @return the organization
     */
    public Organizations getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization(Organizations organization) {
        this.organization = organization;
    }

    /**
     * @return the organTypes
     */
    public List<OrganizationTypes> getOrganTypes() {
        return organTypes;
    }

    /**
     * @param organTypes the organTypes to set
     */
    public void setOrganTypes(List<OrganizationTypes> organTypes) {
        this.organTypes = organTypes;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSelectedGender() {
        return selectedGender;
    }

    public void setSelectedGender(String selectedGender) {
        this.selectedGender = selectedGender;
    }

    /**
     * @return the propertyDocs
     */
    public List<PropertyDocuments> getPropertyDocs() {
        return propertyDocs;
    }

    /**
     * @param propertyDocs the propertyDocs to set
     */
    public void setPropertyDocs(List<PropertyDocuments> propertyDocs) {
        this.propertyDocs = propertyDocs;
    }

    /**
     * @return the resideUsage
     */
    public List<ResidentialUseages> getResideUsage() {
        return resideUsage;
    }

    /**
     * @param resideUsage the resideUsage to set
     */
    public void setResideUsage(List<ResidentialUseages> resideUsage) {
        this.resideUsage = resideUsage;
    }

    /**
     * @return the beland
     */
    public BareLands getBeland() {
        return beland;
    }

    /**
     * @param beland the beland to set
     */
    public void setBeland(BareLands beland) {
        this.beland = beland;
    }

    /**
     * @return the buildingDetail
     */
    public PropertyClassificationDetails getBuildingDetail() {
        return buildingDetail;
    }

    /**
     * @param buildingDetail the buildingDetail to set
     */
    public void setBuildingDetail(PropertyClassificationDetails buildingDetail) {
        this.buildingDetail = buildingDetail;
    }
}
