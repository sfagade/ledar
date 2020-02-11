package net.icsl.ledar.web.servlets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.icsl.ledar.ejb.enums.ApplicationEntityNames;
import net.icsl.ledar.ejb.enums.UserActitiyName;
import net.icsl.ledar.ejb.enums.ValuationStatusEnum;
import net.icsl.ledar.ejb.enums.WaterSupply;
import net.icsl.ledar.ejb.model.BareLands;
import net.icsl.ledar.ejb.model.BarelandFiles;
import net.icsl.ledar.ejb.model.BillsDeliveryFiles;
import net.icsl.ledar.ejb.model.BiodataPersonTypes;
import net.icsl.ledar.ejb.model.DocumentTypes;
import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.model.Occupations;
import net.icsl.ledar.ejb.model.People;
import net.icsl.ledar.ejb.model.PersonTitles;
import net.icsl.ledar.ejb.model.PrintedBills;
import net.icsl.ledar.ejb.model.PropertyBiodatas;
import net.icsl.ledar.ejb.model.PropertyClassificationDetails;
import net.icsl.ledar.ejb.model.PropertyDocuments;
import net.icsl.ledar.ejb.model.PropertyServices;
import net.icsl.ledar.ejb.model.PropertyUsageCategories;
import net.icsl.ledar.ejb.model.RegisteredDevices;
import net.icsl.ledar.ejb.model.ResidentialTypes;
import net.icsl.ledar.ejb.model.ServiceTypes;
import net.icsl.ledar.ejb.model.UsedPropertyIds;
import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.model.WardLandProperties;
import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.ReferenceDataService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.web.bean.UploadedFilesBean;
import net.icsl.ledar.web.util.ApplicationUtility;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.crypto.hash.Sha256Hash;

/**
 *
 * @author sfagade
 */
@WebServlet(name = "FieldDataCapture", urlPatterns = {"/fieldDataCapture"})
//@MultipartConfig
public class FieldDataCapture extends HttpServlet {

    private static final long serialVersionUID = 1L;
    @Inject
    private RegisteredUsersDataService registeredService;
    @Inject
    private LandPropertiesDataService landPropService;
    @Inject
    private ReferenceDataService refDataService;
    @Inject
    private LcdaWardsDataServices lcdaWardService;

    @Inject
    private UploadedFilesBean uploadBean;

    private final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS", Locale.ENGLISH);
    private final DateFormat shortDateFormat = new SimpleDateFormat("MMM-dd-yyyy", Locale.ENGLISH);
    private People person;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JSONObject returnMsg = new JSONObject();

        String formAction = request.getParameter("form_action");
        try {

            if ((formAction != null) && (!formAction.isEmpty())) { //this should always be the case
                if (formAction.equalsIgnoreCase("saveNewEnumeration")) {
                    returnMsg = this.uploadAndSaveEnumData(request);
                } else if (formAction.equalsIgnoreCase("updateUserPassword")) {
                    returnMsg = this.changeUserPassword(request);
                } else if (formAction.equalsIgnoreCase("saveBillPictureFile")) {
                    returnMsg = this.uploadAndSaveNewPropertyPicture(request);
                }
            } else {
                returnMsg.put("errorMessage", "You need to tell me what u want to do dude!");
            }
        } catch (JSONException ex) {
            Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.setContentType("application/json");
        response.getWriter().write(returnMsg.toString());

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private JSONObject uploadAndSaveEnumData(HttpServletRequest request) throws IOException, ServletException {

        JSONObject returnMessage = new JSONObject();
        JSONObject descrObj;

        Date dateTimeCaptured, date_of_birth;
        byte[] decoded;

        WardLandProperties wardProperty;
        WardStreets street = null;
        BareLands bareLand;
        PropertyBiodatas bioPerson = null;
        List<PropertyClassificationDetails> propClassifics = new ArrayList<>();
        PropertyClassificationDetails propClassific;
        List<PropertyServices> propServices = new ArrayList<>();
        List<PropertyDocuments> propDocs = new ArrayList<>();
        List<BarelandFiles> blandFiles = new ArrayList<>();
        DocumentTypes docType = refDataService.findDocumentTitleTypeById(1L); //id for property pic
        PropertyUsageCategories usageCategory = null;

        String imageStr = request.getParameter("frontView");
        String imageStr2 = request.getParameter("sideView");
        String imageStr3 = request.getParameter("rearView");

        String fvFilename = request.getParameter("fv_filename");
        String svFilename = request.getParameter("sv_filename");
        String rvFilename = request.getParameter("rv_filename");

        String fvMimetype = request.getParameter("fv_mimetype");
        String svMimetype = request.getParameter("sv_mimetype");
        String rvMimetype = request.getParameter("rv_mimetype");

        //Boolean newStreet = Boolean.valueOf(request.getParameter("is_new_street"));
        Boolean isFenced = Boolean.valueOf(request.getParameter("bareland_fenced"));
        String street_id = request.getParameter("street_id");
        String irregularAddr = request.getParameter("irregular_address");
        String ward_id = request.getParameter("ward");
        String blockNumber = request.getParameter("block_plot");
        //String plotNumber = request.getParameter("plot_number");
        String mac_address = request.getParameter("mac_address");
        String propertyNo = request.getParameter("property_no");
        String dob = request.getParameter("dob");

        Boolean isBareLand = (!request.getParameter("is_bareland").isEmpty() && request.getParameter("is_bareland").equalsIgnoreCase("true"));
        //Boolean isBareLand = false;
        String roadSide = request.getParameter("side_of_road");
        String longitude = request.getParameter("longitude");
        String latitude = request.getParameter("latitude");
        String strCaptured = request.getParameter("date_time_camptured");
        String pword = request.getParameter("pwd");
        String username = request.getParameter("username");
        String surname = request.getParameter("surname");
        String firstName = request.getParameter("firstname");
        String middleName = request.getParameter("othernames");
        String gender = request.getParameter("gender");
        String maritalStatus = request.getParameter("marital_status");
        String highestEducation = request.getParameter("qualification");
        String telephoneHome = (request.getParameter("telephone_home") != null) ? ApplicationUtility.formatPhoneNumber(request.getParameter("telephone_home"), null) : null;
        String telephoneMobile = (request.getParameter("telephone_mobile") != null) ? ApplicationUtility.formatPhoneNumber(request.getParameter("telephone_mobile"), null) : null;
        String eailAddr = request.getParameter("email");
        String employmentStatus = request.getParameter("employment_status");
        String title_id = request.getParameter("title");
        Occupations occupation = ((request.getParameter("occupation") != null) && (!request.getParameter("occupation").isEmpty()))
                ? refDataService.findOccupationsById(Long.valueOf(request.getParameter("occupation"))) : null; //biodata_person_types
        BiodataPersonTypes bioType = ((request.getParameter("type") != null) && (!request.getParameter("type").isEmpty()))
                ? refDataService.findBiodataPersonTypesById(Long.valueOf(request.getParameter("type"))) : null; //

        ServiceTypes serviceType = ((request.getParameter("service_type") != null) && (!request.getParameter("service_type").isEmpty()))
                ? refDataService.findServiceTypesById(Long.valueOf(request.getParameter("service_type"))) : null; //
        String propertyType = (request.getParameter("serviced").equalsIgnoreCase("true")) ? "SERVICED" : "UNSERVICED";
        String settlemenType = request.getParameter("settlement_type");
        String electricityType = request.getParameter("electricity_supply_type");
        Boolean hasElectricity = Boolean.valueOf(request.getParameter("electricity_supply"));
        String remarks = request.getParameter("remarks");
        String waterSupply = request.getParameter("water_supply");
        Boolean streetLight = Boolean.valueOf(request.getParameter("street_light"));
        Boolean hasDrainage = Boolean.valueOf(request.getParameter("drainage_facilities"));
        Boolean drainageCovered = Boolean.valueOf(request.getParameter("drainage_covered"));
        Boolean hasWalkway = Boolean.valueOf(request.getParameter("walk_ways"));
        String lswc_id = request.getParameter("lswc_id");
        String owner_type = request.getParameter("property_owner");
        String phcn_id = request.getParameter("phcn_id");
        Boolean streetLandscape = Boolean.valueOf(request.getParameter("street_landscaping"));

        Long noOfFlats = (request.getParameter("no_of_flats") != null && !request.getParameter("no_of_flats").isEmpty()) ? Long.valueOf(request.getParameter("no_of_flats")) : null;
        Long propQty = (request.getParameter("property_pcr") != null && !request.getParameter("property_pcr").isEmpty()) ? Long.valueOf(request.getParameter("property_pcr")) : null;

        String land_size = request.getParameter("size");
        String waterTerminus = request.getParameter("nearest_water_terminus");
        String railTerminus = request.getParameter("nearest_rail_terminus");
        String busTerminus = request.getParameter("nearest_bus_stop");

        String streetLightType = request.getParameter("street_light_type");
        String wasteDisposal = request.getParameter("waste_disposal");
        Boolean tarredRoad, untarredRoad;
        Integer noOfBuilding = (!request.getParameter("no_of_buildings").isEmpty()) ? Integer.valueOf(request.getParameter("no_of_buildings")) : 1;
        String documentationTitle = request.getParameter("documentation_title");
        String propertyId = request.getParameter("property_id").replaceAll("[-+.^:,]", "");
        Boolean hasDocumentType = (request.getParameter("has_documentation_title").equals("true"));
        Boolean isBusinessPrivate = (!request.getParameter("is_business_private").isEmpty() && request.getParameter("is_business_private").equalsIgnoreCase("true"));
        Boolean isBlockNumber = (blockNumber.equalsIgnoreCase("BLOCK"));
        Boolean isIrregularAddr = (irregularAddr != null);
        String roadCondition = request.getParameter("road_state");
        String estate_name = (!request.getParameter("estate_name").isEmpty() ? request.getParameter("estate_name") : null);
        String lga_name = request.getParameter("lga_name");
        String download_dir = uploadBean.getProp().getProperty("imageURl") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/property_pictures/" + shortDateFormat.format(new Date()) + "/" + lga_name;

        if (request.getParameter("road_tarred").equalsIgnoreCase("true")) {
            tarredRoad = true;
            untarredRoad = false;
        } else {
            tarredRoad = false;
            untarredRoad = true;
        }

        try {
            if ((username != null) && (pword != null)) {

                SecurityUtils.getSubject().login(new UsernamePasswordToken(username, pword, false));
                org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();

                if ((currentUser.hasRole("FIELD OFFICER")) || (currentUser.hasRole("FIELD CO-ORDINATOR")) || (currentUser.hasRole("FIELD TEAM LEAD"))) {

                    UsedPropertyIds usedProperty = landPropService.findUsedPropertyIdsByPropertyPin(propertyId.replaceAll("[^\\d.]", ""));

                    person = registeredService.findUserByUsername(username);

                    List<RegisteredDevices> devices = registeredService.fetchRegisteredDevicesFilter(person.getLogindetailId().getId(), mac_address, null);
                    Boolean is_valid_device = Boolean.FALSE;

                    if (devices != null && devices.size() > 0) {

                        //if (is_valid_device) {
                        List<WardLandProperties> existingProperties = lcdaWardService.fetchAllPropertyByPropertyId(propertyId, false, null);
                        if (((existingProperties == null) || (existingProperties.size() < 1)) && usedProperty == null) { //property already exist, we should bounce this back

                            dateTimeCaptured = dateFormat.parse(strCaptured);
                            date_of_birth = (dob != null && !dob.isEmpty()) ? new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(dob) : null;
                            WardStreets wardStreet = null;
                            if (irregularAddr != null && !irregularAddr.isEmpty()) { //this is a new street, we should create it and then continue

                                wardStreet = refDataService.findStreetInLocalGovtArea(Long.valueOf(ward_id), irregularAddr);

                                if (wardStreet != null && wardStreet.getEstateName() != null && !wardStreet.getEstateName().equals(estate_name)) {
                                    wardStreet = null; //street is found, but not in the right estate
                                } else if (wardStreet != null) {
                                    Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, null, "User trying to create existing Street: " + wardStreet.getStreetName() + " - "
                                            + wardStreet.getEstateName());
                                }

                                if (wardStreet == null) { //street doesn't exits b4 today
                                    wardStreet = new WardStreets(); //we need to create the new street and send notification email to admin people
                                    UsersLastActivities street_activity = new UsersLastActivities(null, UserActitiyName.NEWSTREET.toString(), new Date(), person.getFullName() + " saved new street"
                                            + " information", person.getLogindetailId().getLastLoginIp(), null, null, null, person.getLogindetailId(), null, null);

                                    street_activity.setEntityName(ApplicationEntityNames.WARDSTREET.toString());
                                    street_activity.setEntityId(wardStreet.getId());

                                    wardStreet.setCreatedById(person.getLogindetailId());
                                    wardStreet.setStreetName(irregularAddr.toUpperCase());
                                    wardStreet.setIsVerified(false);
                                    wardStreet.setLcdaWardId(refDataService.findLcdaWardsById(Long.valueOf(ward_id)));
                                    wardStreet.setEstateName(estate_name);

                                    irregularAddr = irregularAddr.toUpperCase();
                                    Logger.getLogger(FieldDataCapture.class.getName()).log(Level.INFO, "Saved new street information: {0} - {1} - {2}",
                                            new Object[]{wardStreet.getStreetName(), wardStreet.getEstateName(), person.getFullName()});

                                    wardStreet = lcdaWardService.saveNewStreetInformation(wardStreet, street_activity);
                                    if (wardStreet != null) {
                                        wardStreet = null;
                                    }
                                } else {
                                    isIrregularAddr = false;
                                }

                            } else if ((street_id != null) && (!street_id.isEmpty())) { //we're going to find the street from our records
                                street = refDataService.findStreetById(Long.valueOf(street_id));
                            }

                            if (wardStreet != null && (street_id == null || street_id.isEmpty())) {
                                street = wardStreet;
                                irregularAddr = null;
                                isIrregularAddr = false;
                            }

                            File download_folder = new File(download_dir);

                            if (!download_folder.exists()) { //this will be the case for the first upload done for the day
                                download_folder.mkdirs(); //create folder
                            }

                            if (!isBareLand) { //we don't want to go through any of this wahala is its a bare land

                                if ((street != null) || (irregularAddr != null)) { //street is provided
                                    PersonTitles ptitles = (title_id != null && !title_id.isEmpty()) ? refDataService.findPersonTitlesById(Long.valueOf(title_id)) : null;
                                    if ((surname != null && !surname.isEmpty()) && (firstName != null && !firstName.isEmpty())) {
                                        bioPerson = new PropertyBiodatas(null, firstName, surname, middleName, gender.toUpperCase(), maritalStatus.toUpperCase(), highestEducation.toUpperCase(),
                                                telephoneHome, eailAddr, telephoneMobile, employmentStatus, ptitles, bioType, occupation, null, null);
                                        bioPerson.setDateOfBirth(date_of_birth);
                                        bioPerson.setLastUpdatedById(person.getLogindetailId());
                                    }

                                    //TODO I need to generate property ID
                                    wardProperty = new WardLandProperties(null, null, isBlockNumber, hasDocumentType, null, null, null, null, propertyId, propertyNo, isIrregularAddr, false, roadSide,
                                            longitude, latitude, dateTimeCaptured, null, propertyType.toUpperCase(), electricityType, hasElectricity, remarks,
                                            WaterSupply.valueOf(waterSupply).toString(), person.getLogindetailId(), bioPerson, street, land_size, null, null);
                                    wardProperty.setIrregularAddress(irregularAddr);
                                    wardProperty.setNoOfBuildings(noOfBuilding);
                                    wardProperty.setSettlementTypeName(settlemenType);
                                    wardProperty.setHasStreetLight(streetLight);
                                    wardProperty.setStreetLightType(streetLightType);
                                    wardProperty.setWasteDisposalSystem(wasteDisposal);
                                    wardProperty.setTarredRoad(tarredRoad);
                                    wardProperty.setUntarredRoad(untarredRoad);
                                    wardProperty.setHasDrainageFacility(hasDrainage);
                                    wardProperty.setIsDrainageCovered(drainageCovered);
                                    wardProperty.setHasWalkways(hasWalkway);
                                    wardProperty.setHasStreetLandscaping(streetLandscape);
                                    wardProperty.setNearestBusStop(busTerminus);
                                    wardProperty.setNearestRailTerminus(railTerminus);
                                    wardProperty.setNearestWaterRerminus(waterTerminus);
                                    wardProperty.setRoadCondition(roadCondition);
                                    wardProperty.setPropertyQualities(refDataService.findPropertyQualityById(propQty));
                                    wardProperty.setLswcId(lswc_id);
                                    wardProperty.setPhcnId(phcn_id);
                                    wardProperty.setOwnershipType(owner_type);
                                    wardProperty.setContractorId(person.getOrganization());
                                    wardProperty.setIsInitsSynced(false);
                                    wardProperty.setLastUpdatedById(person.getLogindetailId());
                                    wardProperty.setPropertyValuationStatus(ValuationStatusEnum.PENDING.toString());
                                    wardProperty.setEstateName(estate_name);
                                    wardProperty.setLastUpdatedById(person.getLogindetailId());
                                    wardProperty.setDistrictName(street.getLcdaWardId().getWardName());
                                    wardProperty.setLgaName(street.getLcdaWardId().getLocalCouncilDevAreaId().getLcdaName());

                                    if (!request.getParameter("property_id").isEmpty()) {
                                        wardProperty.setLegacyPropertyId(propertyId);
                                    }

                                    JSONObject innerObj;
                                    ResidentialTypes residType;
                                    //CommercialTypes commercType;
                                    //ResidentialUseages resideUse;
                                    Long noOfRooms, noOfRoomsPerFlat, noOfFloors, noOfPumps;
                                    double buildingFootprint, building_area = 0;
                                    String propertyName, propertyUse, propertyDescription, forecourtArea;

                                    try {
                                        descrObj = new JSONObject(request.getParameter("property_description"));

                                        for (Iterator iterator = descrObj.keys(); iterator.hasNext();) {
                                            String key = (String) iterator.next();

                                            innerObj = (JSONObject) descrObj.get(key);
                                            noOfRooms = (innerObj.has("no_of_rooms") && !innerObj.getString("no_of_rooms").isEmpty()) ? innerObj.getLong("no_of_rooms") : null;
                                            noOfRoomsPerFlat = (innerObj.has("rooms_per_flat") && !innerObj.getString("rooms_per_flat").isEmpty()) ? innerObj.getLong("rooms_per_flat") : null;
                                            noOfFloors = innerObj.getLong("no_of_floors");
                                            //sizePerFloor = innerObj.getString("size");
                                            propertyName = innerObj.getString("business_name");
                                            //facilities = innerObj.getString("facilities");
                                            noOfPumps = innerObj.getLong("no_of_pumps");
                                            propertyUse = innerObj.getString("property_use");
                                            buildingFootprint = innerObj.getDouble("building_footprint");
                                            propertyDescription = innerObj.getString("property_description");
                                            forecourtArea = innerObj.getString("forecourt_area");
                                            building_area = (buildingFootprint * noOfFloors) + building_area;

                                            residType = ((innerObj.get("residential_type") != null) && (!innerObj.getString("residential_type").isEmpty()))
                                                    ? refDataService.findResidentialTypeById(innerObj.getLong("residential_type")) : null; //
//                                            commercType = ((innerObj.get("commercial_type") != null) && (!innerObj.getString("commercial_type").isEmpty()))
//                                                    ? refDataService.findCommercialTypeById(innerObj.getLong("commercial_type")) : null; //
                                            if ((innerObj.get("usage_id") != null)) {
                                                List<PropertyUsageCategories> usageCategories = refDataService.fetchPropertyUsageCategories(innerObj.getLong("usage_id"), null);
                                                if (usageCategories != null && usageCategories.size() > 0) {
                                                    usageCategory = usageCategories.get(0);
                                                } else {
                                                    returnMessage.put("errorMessage", "PROPERTY usage is required!");
                                                    Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, "Last Error by: {0}", person.getFullName());
                                                    return returnMessage;
                                                }
                                            }
                                            System.out.println("Residential use is: " + innerObj.get("residential_use").toString());
//                                            resideUse = ((innerObj.get("residential_use").toString() != null) && (!innerObj.get("residential_use").toString().isEmpty()))
//                                                    ? refDataService.findResidentialUseById(Long.valueOf(innerObj.get("residential_use").toString())) : null;

                                            propClassific = new PropertyClassificationDetails(null, noOfRooms, null, null, noOfFlats, noOfRoomsPerFlat, noOfFloors, null, propertyName,
                                                    null, noOfPumps, forecourtArea, wardProperty, residType, usageCategory, buildingFootprint, (propertyDescription.equalsIgnoreCase("Completed")),
                                                    refDataService.findPropertyUseById(Long.valueOf(propertyUse)), null, null);

                                            propClassific.setIsPrivateBusiness(isBusinessPrivate);
                                            //propClassific.setResidentialUseId(resideUse);
                                            propClassific.setLastUpdatedById(person.getLogindetailId());
                                            propClassifics.add(propClassific);
                                        }

                                        wardProperty.setBuildingArea(building_area);

                                    } catch (JSONException ex) {
                                        returnMessage.put("errorMessage", "Something went wrong processing JSON object, WHAT DID U DO!");
                                        Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, "Last Error by: " + person.getFullName(), ex);
                                        return returnMessage;
                                    }

                                    File exist_file = new File(download_dir + "/" + fvFilename.trim());
                                    if (imageStr != null && (!exist_file.exists())) {
                                        decoded = Base64.decodeBase64(imageStr);
                                        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(download_dir + "/" + fvFilename.trim())))) {
                                            stream.write(decoded);
                                        }

                                        propDocs.add(new PropertyDocuments(null, fvFilename, null, wardProperty, docType, new FileUploads(null, null, fvFilename, fvMimetype, "Front View", null,
                                                person.getLogindetailId(), download_dir, null, null), null, null));
                                    }

                                    exist_file = new File(download_dir + "/" + svFilename.trim());
                                    if (imageStr2 != null && (!exist_file.exists())) {
                                        decoded = Base64.decodeBase64(imageStr2);

                                        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(download_dir + "/" + svFilename.trim())))) {
                                            stream.write(decoded);
                                        }

                                        propDocs.add(new PropertyDocuments(null, svFilename, null, wardProperty, docType, new FileUploads(null, null, svFilename, svMimetype, "Side View", null,
                                                person.getLogindetailId(), download_dir, null, null), null, null));
                                    }

                                    exist_file = new File(download_dir + "/" + rvFilename.trim());
                                    if (imageStr3 != null && (!exist_file.exists())) {
                                        decoded = Base64.decodeBase64(imageStr3);
                                        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(download_dir + "/" + rvFilename.trim())))) {
                                            stream.write(decoded);
                                        }
                                        propDocs.add(new PropertyDocuments(null, rvFilename, null, wardProperty, docType, new FileUploads(null, null, rvFilename, rvMimetype, "Rear View", null,
                                                person.getLogindetailId(), download_dir, null, null), null, null));
                                    }

                                    person.getLogindetailId().setUsersLastActivitiesList(new ArrayList<UsersLastActivities>() {
                                        {
                                            add(new UsersLastActivities(null, UserActitiyName.SAVEDENUMERATION.toString(), new Date(), "Saved Enumeration Information for property", null,
                                                    person.getLogindetailId().getLastLoginLongitude(), person.getLogindetailId().getLastLoginLatitude(), null, person.getLogindetailId(), null, null));
                                        }
                                    });
                                    person.getLogindetailId().getUsersLastActivitiesList().get(0).setEntityName(ApplicationEntityNames.WARDLANDPROPERTIES.toString());
                                    person.getLogindetailId().getUsersLastActivitiesList().get(0).setEntityId(wardProperty.getId());
                                    person.getLogindetailId().getUsersLastActivitiesList().get(0).setLatitude(latitude);
                                    person.getLogindetailId().getUsersLastActivitiesList().get(0).setLongitude(longitude);

                                    if (propertyType.equals("SERVICED")) {
                                        propServices.add(new PropertyServices(null, null, serviceType, wardProperty, null));
                                    }

                                    Logger.getLogger(FieldDataCapture.class.getName()).log(Level.INFO, "About to save data from: {0}", person.getFullName());
                                    wardProperty = landPropService.savePropertyData(wardProperty, bioPerson, propClassifics, propDocs, propServices, null);
                                    Logger.getLogger(FieldDataCapture.class.getName()).log(Level.INFO, "Saved new Property: {0}", wardProperty.getPropertyId());

                                    if (wardProperty != null) {
                                        returnMessage.put("successMessage", "Property information has been saved successfully, property number is: " + propertyId);
                                    } else {
                                        returnMessage.put("errorMessage", "Could not save property information, please try again later");
                                    }

                                } else {
                                    returnMessage.put("errorMessage", "Invalid property specified");
                                }

                            } else { //we need to save bareland information here
                                List<BareLands> existingLands = landPropService.fetchAllBarelandByPin(propertyId, null);
                                propQty = (propQty == null) ? propQty : 22L;
                                if ((existingLands == null) || (existingLands.size() < 1)) {
                                    bareLand = new BareLands(null, isFenced, propertyNo, remarks, street, person.getLogindetailId(), dateTimeCaptured, propertyId, land_size, latitude, longitude, roadCondition,
                                            tarredRoad, drainageCovered, streetLandscape, busTerminus, railTerminus, waterTerminus, settlemenType, null, null);
                                    bareLand.setIrregularAddress(irregularAddr);
                                    bareLand.setIsInitsSynced(false);
                                    bareLand.setPropertyValuationStatus(ValuationStatusEnum.PENDING.toString());
                                    bareLand.setContractorId(person.getOrganization());
                                    bareLand.setOwnershipType(owner_type);
                                    bareLand.setPropertyQualities(refDataService.findPropertyQualityById(propQty));

                                    if ((request.getParameter("usage_id") != null)) {
                                        List<PropertyUsageCategories> usageCategories = refDataService.fetchPropertyUsageCategories(Long.valueOf(request.getParameter("usage_id")), null);
                                        if (usageCategories != null && usageCategories.size() > 0) {
                                            usageCategory = usageCategories.get(0);
                                        }
                                    }

                                    bareLand.setUsageCategory(usageCategory);
                                    person.getLogindetailId().setUsersLastActivitiesList(new ArrayList<UsersLastActivities>() {
                                        {
                                            add(new UsersLastActivities(null, UserActitiyName.SAVEDBARELAND.toString(), new Date(), "Saved Bareland Information for Bareland", null,
                                                    person.getLogindetailId().getLastLoginLongitude(), person.getLogindetailId().getLastLoginLatitude(), null, person.getLogindetailId(), null, null));
                                        }
                                    });

                                    person.getLogindetailId().getUsersLastActivitiesList().get(0).setEntityName(ApplicationEntityNames.BARELAND.toString());
                                    person.getLogindetailId().getUsersLastActivitiesList().get(0).setEntityId(bareLand.getId());
                                    person.getLogindetailId().getUsersLastActivitiesList().get(0).setLatitude(latitude);
                                    person.getLogindetailId().getUsersLastActivitiesList().get(0).setLongitude(longitude);

                                    PersonTitles ptitles = (title_id != null && !title_id.isEmpty()) ? refDataService.findPersonTitlesById(Long.valueOf(title_id)) : null;
                                    if ((surname != null && !surname.isEmpty()) && (firstName != null && !firstName.isEmpty())) {
                                        bioPerson = new PropertyBiodatas(null, firstName, surname, middleName, gender.toUpperCase(), maritalStatus.toUpperCase(), highestEducation.toUpperCase(), telephoneHome,
                                                eailAddr, telephoneMobile, employmentStatus, ptitles, bioType, occupation, null, null);
                                        bareLand.setPropertyBiodataId(bioPerson);
                                        bioPerson.setDateOfBirth(date_of_birth);
                                    }

                                    if (imageStr != null) {
                                        decoded = Base64.decodeBase64(imageStr);
                                        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(download_dir + "/" + fvFilename.trim())))) {
                                            stream.write(decoded);
                                        }
                                        blandFiles.add(new BarelandFiles(null, fvMimetype, bareLand, new FileUploads(null, null, fvFilename, fvMimetype, "Front View", null,
                                                person.getLogindetailId(), download_dir, null, null), null, null));
                                    }

                                    if (imageStr2 != null) {
                                        decoded = Base64.decodeBase64(imageStr2);
                                        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(download_dir + "/" + svFilename.trim())))) {
                                            stream.write(decoded);
                                        }
                                        blandFiles.add(new BarelandFiles(null, svMimetype, bareLand, new FileUploads(null, null, svFilename, svMimetype, "Side View", null,
                                                person.getLogindetailId(), download_dir, null, null), null, null));
                                    }
                                    if (imageStr3 != null) {
                                        decoded = Base64.decodeBase64(imageStr3);
                                        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(download_dir + "/" + rvFilename.trim())))) {
                                            stream.write(decoded);
                                        }
                                        blandFiles.add(new BarelandFiles(null, rvMimetype, bareLand, new FileUploads(null, null, rvFilename, rvMimetype, "Rear View", null,
                                                person.getLogindetailId(), download_dir, null, null), null, null));
                                    }

                                    Logger.getLogger(FieldDataCapture.class.getName()).log(Level.INFO, "Saved new Bareland Information: {0}", bareLand.getPropertyId());
                                    bareLand = landPropService.saveBareLandData(bareLand, blandFiles, null);

                                    if (bareLand != null) {
                                        returnMessage.put("successMessage", "Property information has been saved successfully, property number is: " + propertyId);
                                    } else {
                                        returnMessage.put("errorMessage", "Could not save property information, please try again later");
                                    }
                                } else {
                                    Long str_id = (street != null) ? street.getId() : null;
                                    List<BareLands> bareLand_ = landPropService.findBarelandByPinAndAddress(propertyId, irregularAddr, str_id);

                                    if (bareLand_ != null && bareLand_.size() > 0) {
                                        returnMessage.put("errorMessage", "Duplicate Property error");
                                    } else {
                                        returnMessage.put("errorMessage", "Duplicate PIN error");
                                    }
                                    Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, person.getLogindetailId().getUsername() + " is sending duplicate pin: " + propertyId, "");
                                }
                            }
                        } else {
                            Long str_id = (street_id != null && !street_id.isEmpty()) ? Long.valueOf(street_id) : null;
                            List<WardLandProperties> wprops = landPropService.findWardPropertyByPinAndStreet(propertyId, irregularAddr, str_id);

                            if (wprops != null && wprops.size() > 0) {
                                returnMessage.put("errorMessage", "Duplicate Property error");
                            } else {
                                returnMessage.put("errorMessage", "Duplicate PIN error");
                            }

                            Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, person.getLogindetailId().getUsername() + " is sending duplicate pin: " + propertyId, "");
                        }

                    } else {
                        returnMessage.put("errorMessage", "Unregistered Device");
                    }
                } else {
                    returnMessage.put("errorMessage", "Officer does not have the appropriate role to perform this action");
                }
            } else {
                returnMessage.put("errorMessage", "Officer username or password is missing");
            }
        } catch (JSONException | ParseException ex) {
            Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnMessage;
    }

    private JSONObject changeUserPassword(HttpServletRequest request) throws IOException, ServletException {

        JSONObject returnMessage = new JSONObject();

        String pword = request.getParameter("pwd");
        String username = request.getParameter("username");
        String new_password = request.getParameter("new_pwd");
        String longitude = request.getParameter("longitude");
        String latitude = request.getParameter("latitude");
        String mac_address = request.getParameter("mac_address");

        try {
            if ((!pword.isEmpty()) && (!new_password.isEmpty()) && (!username.isEmpty())) {

                SecurityUtils.getSubject().login(new UsernamePasswordToken(username, pword, false));
                org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();

                if (currentUser.isAuthenticated()) {

                    person = registeredService.findUserByUsername(username);
                    List<RegisteredDevices> devices = registeredService.fetchRegisteredDevicesFilter(person.getLogindetailId().getId(), mac_address, null);
                    //Boolean is_valid_device = Boolean.FALSE;
                    if (devices != null && devices.size() > 0) {
                        /**
                         * for (RegisteredDevices device : devices) { if
                         * (device.getMacAddress().equalsIgnoreCase(mac_address))
                         * { is_valid_device = Boolean.TRUE; break; } } if
                         * (is_valid_device) {
                         */
                        person.getLogindetailId().setLastLoginLatitude(latitude);
                        person.getLogindetailId().setLastLoginLongitude(longitude);

                        UsersLastActivities street_activity = new UsersLastActivities(null, UserActitiyName.UPDATEDUSERINFO.toString(), new Date(), person.getFullName() + " has updated his/her "
                                + "password  information using the mobile app", person.getLogindetailId().getLastLoginIp(), null, null, null, person.getLogindetailId(), null, null);

                        street_activity.setEntityName(ApplicationEntityNames.LOGINDETAILS.toString());
                        street_activity.setEntityId(person.getId());
                        street_activity.setLatitude(latitude);
                        street_activity.setLongitude(longitude);

                        person.getLogindetailId().setPword(new Sha256Hash(new_password).toHex());

                        if (registeredService.update(person.getLogindetailId(), street_activity)) {
                            returnMessage.put("successMessage", "Password has been changed successfully, user should use the new password on next login");
                        } else {
                            returnMessage.put("errorMessage", "Failed to change password now, please try again later");
                        }

                    } else {
                        returnMessage.put("errorMessage", "Device not registered");
                    }
                } else {
                    returnMessage.put("errorMessage", "Authentication failed for user");
                }
            } else {
                returnMessage.put("errorMessage", "One or more required fields is not specified");
            }
        } catch (org.apache.shiro.authc.IncorrectCredentialsException ic) {
            Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, "Invalid login credentials username: {0} - message {1}", new Object[]{username, ic.getMessage()});
            returnMessage.put("errorMessage", "Authentication failed for user");
        } catch (JSONException ex) {
            Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnMessage;
    }

    private JSONObject uploadAndSaveNewPropertyPicture(HttpServletRequest request) throws IOException, ServletException {

        JSONObject returnMessage = new JSONObject();

        PrintedBills printedBill = null;
        List<PrintedBills> printedBills;
        BillsDeliveryFiles delivFiles;

        List<BillsDeliveryFiles> delvFiles;
        Date dateTimeCaptured;

        String imageStr = request.getParameter("frontView");
        String imageStr2 = request.getParameter("sideView");
        String imageStr3 = request.getParameter("rearView");

        String fvFilename = request.getParameter("fv_filename");
        String svFilename = request.getParameter("sv_filename");
        String rvFilename = request.getParameter("rv_filename");

        String fvMimetype = request.getParameter("fv_mimetype");
        String svMimetype = request.getParameter("sv_mimetype");
        String rvMimetype = request.getParameter("rv_mimetype");
        String lga_name = request.getParameter("lga_name");
        String comments = request.getParameter("comments");
        String update_type = request.getParameter("update_type");
        String update_needed = request.getParameter("is_update");

        String propertyId = request.getParameter("property_id");
        String property_pin = request.getParameter("property");
        String pword = request.getParameter("pwd");
        String username = request.getParameter("username");
        String longitude = request.getParameter("longitude");
        String latitude = request.getParameter("latitude");
        String strCaptured = request.getParameter("date_time_camptured");
        String mac_address = request.getParameter("mac_address");
        String download_dir = uploadBean.getProp().getProperty("imageURl") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/" + shortDateFormat.format(new Date())
                + ((lga_name != null) ? "/" + lga_name : "");
        Boolean is_delivered = (update_needed == null || !update_needed.equalsIgnoreCase("yes"));

        try {

            if (((propertyId != null) || (property_pin != null)) && (imageStr != null)) {
                if ((username != null) && (pword != null)) {
                    SecurityUtils.getSubject().login(new UsernamePasswordToken(username.trim(), pword, false));
                    org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
                    if ((currentUser.hasRole("FIELD OFFICER")) || (currentUser.hasRole("FIELD CO-ORDINATOR")) || (currentUser.hasRole("FIELD TEAM LEAD"))) {
                        person = registeredService.findUserByUsername(username.trim());
                        Logger.getLogger(FieldDataCapture.class.getName()).log(Level.INFO, "{0} about to save bill delivery: {1}", new Object[]{person.getFullName(), propertyId});
                        List<RegisteredDevices> devices = registeredService.fetchRegisteredDevicesFilter(person.getLogindetailId().getId(), mac_address, null);
                        if (devices != null && devices.size() > 0) {
                            delvFiles = new ArrayList<>();

                            File download_folder = new File(download_dir);

                            if (!download_folder.exists()) { //this will be the case for the first upload done for the day
                                download_folder.mkdirs(); //create folder
                            }

                            download_folder = new File(download_dir + "/" + person.getFullName());

                            if (!download_folder.exists()) { //this will be the case if this user has not uploaded any file for today
                                download_folder.mkdirs();
                            }

                            try {
                                printedBill = landPropService.findPrintedBillsById(Long.valueOf(propertyId));
                            } catch (NumberFormatException ne) { //propertyId is not set, I should try and use property
                                printedBills = landPropService.findPrintedBillsByPropertyId(property_pin);
                                if ((printedBills != null) && (printedBills.size() > 0)) {
                                    if (printedBills.size() == 1) {
                                        printedBill = printedBills.get(0);
                                    } else {
                                        for (PrintedBills bill : printedBills) {
                                            if (bill.getOwnerName() != null && !bill.getOwnerName().isEmpty()) {
                                                printedBill = bill;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            if (printedBill != null && fvFilename != null) {
                                dateTimeCaptured = dateFormat.parse(strCaptured);

                                byte[] decoded = Base64.decodeBase64(imageStr);

                                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(download_dir + "/" + person.getFullName() + "/" + fvFilename.trim())))) {
                                    stream.write(decoded);
                                }

                                delvFiles.add(new BillsDeliveryFiles(null, printedBill, new FileUploads(null, null, fvFilename, fvMimetype, "Front View", null, person.getLogindetailId(),
                                        download_dir + "/" + person.getFullName(), null, null), person.getLogindetailId(), dateTimeCaptured, longitude, latitude, null, null));

                                if (imageStr2 != null) { //uploading second pic
                                    decoded = Base64.decodeBase64(imageStr2);
                                    try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(download_dir + "/" + person.getFullName() + "/" + svFilename.trim())))) {
                                        stream.write(decoded);
                                    }

                                    delvFiles.add(new BillsDeliveryFiles(null, printedBill, new FileUploads(null, null, svFilename, svMimetype, "Side View", null, person.getLogindetailId(),
                                            download_dir + "/" + person.getFullName(), null, null), person.getLogindetailId(), dateTimeCaptured, longitude, latitude, null, null));
                                }

                                if (imageStr3 != null) { //uploading third pic

                                    decoded = Base64.decodeBase64(imageStr3);
                                    try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(download_dir + "/" + person.getFullName() + "/" + rvFilename.trim())))) {
                                        stream.write(decoded);
                                    }

                                    delvFiles.add(new BillsDeliveryFiles(null, printedBill, new FileUploads(null, null, rvFilename, rvMimetype, "Rear View", null, person.getLogindetailId(),
                                            download_dir + "/" + person.getFullName(), null, null), person.getLogindetailId(), dateTimeCaptured, longitude, latitude, null, null));
                                }

                                person.getLogindetailId().setUsersLastActivitiesList(new ArrayList<UsersLastActivities>() {
                                    {
                                        add(new UsersLastActivities(null, UserActitiyName.MOBILELOGIN.toString(), new Date(), "Uploaded Bill Property Picture", null,
                                                person.getLogindetailId().getLastLoginLongitude(), person.getLogindetailId().getLastLoginLatitude(), null, person.getLogindetailId(), null, null));
                                    }
                                });
                                person.getLogindetailId().getUsersLastActivitiesList().get(0).setEntityName(ApplicationEntityNames.BILLSDELIVERY.toString());
                                person.getLogindetailId().getUsersLastActivitiesList().get(0).setEntityId(printedBill.getId());
                                person.getLogindetailId().getUsersLastActivitiesList().get(0).setLatitude(latitude);
                                person.getLogindetailId().getUsersLastActivitiesList().get(0).setLongitude(longitude);

                                printedBill.setIsDelivered(is_delivered);
                                printedBill.setDeliveryDate(dateTimeCaptured);
                                printedBill.setDeliveryComments(comments);
                                printedBill.setLatitude(latitude);
                                printedBill.setLongitude(longitude);
                                printedBill.setDeliveryLogindetailId(person.getLogindetailId());
                                printedBill.setUpdateStatus((update_type != null && !update_type.isEmpty()) ? update_type : null);
                                printedBill.setIsUpdateRequired((update_needed != null && update_needed.equalsIgnoreCase("yes")));

                                if (landPropService.saveMultipleBillsDeliveryFiles(printedBill, delvFiles) != null) {
                                    Logger.getLogger(FieldDataCapture.class.getName()).log(Level.INFO, "Saved {0}", new Object[]{property_pin});
                                    returnMessage.put("successMessage", "File(s) have been saved successfully and attached to the property");
                                } else {
                                    returnMessage.put("errorMessage", "Failed to save uploaded file, please try again later");
                                }

                            } else {
                                Logger.getLogger(FieldDataCapture.class.getName()).log(Level.INFO, "Invalid PIN {0} by {1}", new Object[]{property_pin, person.getFullName()});
                                returnMessage.put("errorMessage", "Invalid property specified");
                            }
                        } else {
                            returnMessage.put("errorMessage", "Device not registered");
                        }
                    } else {
                        returnMessage.put("errorMessage", "Officer does not have the appropriate role to perform this action");
                    }
                } else {
                    returnMessage.put("errorMessage", "Officer username or password is missing");
                }
            } else {
                returnMessage.put("errorMessage", "First Image missing or Property ID is missing");
            }
        } catch (AuthenticationException e) {
            try {
                returnMessage.put("errorMessage", "Invalid login credentials provided: ");
                Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, null, e);
            } catch (JSONException ex) {
                Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (JSONException ex) {
            Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            try {
                returnMessage.put("errorMessage", "Date provided is not a valid date: ");
            } catch (JSONException ex1) {
                Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(FieldDataCapture.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnMessage;
    }
}
