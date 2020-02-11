package net.icsl.ledar.ejb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.mail.MessagingException;
import net.icsl.ledar.ejb.enums.ValuationStatusEnum;
import net.icsl.ledar.ejb.model.BareLands;
import net.icsl.ledar.ejb.model.BarelandFiles;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.PersonTitles;
import net.icsl.ledar.ejb.model.PropertyClassificationDetails;
import net.icsl.ledar.ejb.model.PropertyDocuments;
import net.icsl.ledar.ejb.model.WardLandProperties;
import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.ReferenceDataService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author sfagade
 */
@Stateless
@LocalBean
public class EptsApiDataProcessor {

    @Inject
    DataProcessorUtil dataProcUtil;
    @Inject
    private RegisteredUsersDataService registeredService;
    @Inject
    private EmailSender emailSender;
    @Inject
    private LandPropertiesDataService propertyService;
    @Inject
    private ReferenceDataService refrService;

    @Asynchronous
    public void processOwnerInformationFile(String file_path, String file_name, Long login_id) {
        Logindetails logindetail;
        Map<String, String> propValues;
        JSONObject api_return;
        XSSFWorkbook excel_sheet_file;
        Row current_row;
        String notice_no;
        JSONArray jsoncargo;
        JSONObject jsonObject1;

        File property_file = new File(file_path + "/" + file_name);

        try {
            excel_sheet_file = new XSSFWorkbook(new FileInputStream(property_file));
            XSSFSheet sheet_ = excel_sheet_file.getSheetAt(0);
            logindetail = registeredService.find(login_id);

            for (int row = 1; row < sheet_.getPhysicalNumberOfRows(); row++) {
                current_row = sheet_.getRow(row);
                if (current_row != null && current_row.getCell(0) != null) {
                    current_row.createCell(25);
                    current_row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                    notice_no = current_row.getCell(0).getStringCellValue();

                    propValues = new HashMap<>();
                    propValues.put("notice_no", notice_no);
                    propValues.put("task", "get property");
                    propValues.put("api_method", "get-property");
                    api_return = LedarOutsideGateway.makeEptsApiGetCall(propValues, logindetail.getPerson().getOrganization().getId());

                    if (api_return != null) {
                        if (api_return.has("success") && api_return.get("success").toString().equalsIgnoreCase("true")) {
                            jsoncargo = api_return.getJSONArray("prop_array");
                            if (jsoncargo.length() == 1) {
                                jsonObject1 = jsoncargo.getJSONObject(0);
                                propValues.put("property_id", jsonObject1.get("id").toString());
                                jsonObject1 = jsonObject1.getJSONObject("extras");
                                if (jsonObject1.has("property_owner")) {
                                    jsonObject1 = jsonObject1.getJSONObject("property_owner");
                                    Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "{0} -> Notice no returned for Owner: {1} -- {2}", new Object[]{row, notice_no, jsonObject1.get("id").toString()});
                                    propValues.remove("notice_no");
                                    propValues.put("property_owner_id", jsonObject1.get("id").toString());
                                    propValues.put("district_name", current_row.getCell(1) + " ");
                                    propValues.put("street_name", current_row.getCell(2) + " ");
                                    propValues.put("contact_email", current_row.getCell(3) + " ");
                                    propValues.put("other_phones", current_row.getCell(4) + " ");
                                    propValues.put("mobile_phone", current_row.getCell(5) + " ");
                                    propValues.put("plot_no", current_row.getCell(6) + " ");
                                    propValues.put("block_no", current_row.getCell(7) + " ");
                                    propValues.put("flat_no", current_row.getCell(8) + " ");
                                    propValues.put("house_no", current_row.getCell(9) + " ");
                                    propValues.put("country_id", current_row.getCell(10) + " ");
                                    propValues.put("local_government_id", current_row.getCell(11) + " ");
                                    propValues.put("owner_classification_id", current_row.getCell(12) + " ");
                                    propValues.put("owner_type_id", current_row.getCell(13) + " ");
                                    propValues.put("title_disc", current_row.getCell(14) + " ");
                                    propValues.put("name_title_id", current_row.getCell(15) + " ");
                                    propValues.put("full_name", current_row.getCell(16) + " ");
                                    propValues.put("middle_name", current_row.getCell(17) + " ");
                                    propValues.put("first_name", current_row.getCell(18) + " ");
                                    propValues.put("last_name", current_row.getCell(19) + " ");
                                    propValues.put("owner_name", current_row.getCell(20) + " ");
                                    propValues.put("payer_id", current_row.getCell(21) + " ");
                                    propValues.put("task", "update owner info");
                                    propValues.put("api_method", "update-property-owner");

                                    api_return = LedarOutsideGateway.makeEptsApiPostCall(propValues, logindetail.getPerson().getOrganization().getId());

                                    if (api_return != null) {
                                        current_row.getCell(25).setCellValue(api_return.getString("message"));
                                    } else {
                                        current_row.getCell(25).setCellValue("OWNER CALL FAILED");
                                    }
                                } else {
                                    current_row.getCell(25).setCellValue("OWNER NOT FOUND");
                                }
                            } else {
                                current_row.getCell(25).setCellValue("DUPLICATE PROPERTY FOUND");
                            }
                        } else {
                            current_row.getCell(25).setCellValue("OWNER NOT FOUND");
                        }
                    }
                } else {
                    break;
                }
            }

            String file_access = file_path + "/" + file_name + "_processed" + ".xlsx";
            Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "Push complete about to send mail: {0}", logindetail.getPerson().getFullName());

            try (FileOutputStream fis = new FileOutputStream(file_access)) {
                excel_sheet_file.write(fis);

                String to_address[] = {logindetail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress()};
                //String bc_address[] = {"sfagade@ic-sol.net"};

                List<File> result_files = new ArrayList<>();
                result_files.add(new File(file_access));

                String email_msg = "<div>EPTS building information build report. Please see attachment for breakdown report</div>";

                emailSender.sendEmailWithAttachment(to_address, null, null, "LEDAR EPTS OWNER INFOMRATION Report", email_msg, result_files);

            } catch (IOException | MessagingException ex) {
                Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | JSONException ex) {
            Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Asynchronous
    public void processNewBuildingFile(String file_path, String file_name, Long login_id) {
        Logindetails logindetail;
        Map<String, String> propValues;
        JSONObject api_return;
        XSSFWorkbook excel_sheet_file;
        Row current_row;
        String notice_no, no_of_floors;
        JSONArray jsoncargo;
        JSONObject jsonObject1;

        if (file_path != null && file_name != null && login_id != null) { //this should always be the case
            File property_file = new File(file_path + "/" + file_name);
            if (property_file.exists()) {
                try {
                    excel_sheet_file = new XSSFWorkbook(new FileInputStream(property_file));
                    XSSFSheet sheet_ = excel_sheet_file.getSheetAt(0);
                    logindetail = registeredService.find(login_id);

                    for (int row = 1; row < sheet_.getPhysicalNumberOfRows(); row++) {
                        current_row = sheet_.getRow(row);
                        if (current_row != null && current_row.getCell(0) != null && !current_row.getCell(0).getStringCellValue().isEmpty()) {
                            current_row.createCell(5);

                            current_row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                            notice_no = current_row.getCell(0).getStringCellValue();

                            propValues = new HashMap<>();
                            propValues.put("notice_no", notice_no);
                            propValues.put("task", "get property");
                            propValues.put("api_method", "get-property");
                            api_return = LedarOutsideGateway.makeEptsApiGetCall(propValues, logindetail.getPerson().getOrganization().getId());

                            if (api_return != null) {
                                Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "{0} -> Notice no returned for building: {1} -- {2}", new Object[]{row, notice_no, api_return.toString()});
                                if (api_return.has("success") && api_return.get("success").toString().equalsIgnoreCase("true")) {
                                    jsoncargo = api_return.getJSONArray("prop_array");
                                    if (jsoncargo.length() == 1) {
                                        jsonObject1 = jsoncargo.getJSONObject(0);

                                        no_of_floors = current_row.getCell(3) + "";
                                        if (no_of_floors != null && !no_of_floors.isEmpty() && no_of_floors.contains(".")) {
                                            no_of_floors = no_of_floors.substring(0, no_of_floors.indexOf("."));
                                        }

                                        propValues.put("property_id", jsonObject1.get("id").toString());
                                        propValues.put("floors", no_of_floors);
                                        propValues.put("building_footprint", current_row.getCell(1) + " ");
                                        propValues.put("building_idn", " ");
                                        propValues.put("comments", " ");
                                        propValues.put("building_usage_id", current_row.getCell(2) + " ");
                                        propValues.put("building_state_id", current_row.getCell(4) + " ");
                                        propValues.put("task", "create building");
                                        propValues.put("api_method", "post-property-building");

                                        api_return = LedarOutsideGateway.makeEptsApiPostCall(propValues, logindetail.getPerson().getOrganization().getId());

                                        if (api_return != null) {
                                            current_row.getCell(5).setCellValue(api_return.getString("message"));
                                        } else {
                                            current_row.getCell(5).setCellValue("BUILDING CALL FAILED");
                                        }
                                    } else {
                                        current_row.getCell(5).setCellValue("DUPLICATE PROPERTY FOUND");
                                    }
                                } else {
                                    current_row.getCell(5).setCellValue("PROPERTY NOT FOUND");
                                }
                            }
                        } else {
                            break;
                        }
                    }

                    String file_access = file_path + "/" + file_name + "_processed" + ".xlsx";
                    Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "Push complete about to send mail: {0}", logindetail.getPerson().getFullName());

                    try (FileOutputStream fis = new FileOutputStream(file_access)) {
                        excel_sheet_file.write(fis);

                        String to_address[] = {logindetail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress()};
                        //String bc_address[] = {"sfagade@ic-sol.net"};

                        List<File> result_files = new ArrayList<>();
                        result_files.add(new File(file_access));

                        String email_msg = "<div>EPTS building information build report. Please see attachment for breakdown report</div>";

                        emailSender.sendEmailWithAttachment(to_address, null, null, "LEDAR EPTS BUILDING INFOMRATION Report", email_msg, result_files);

                    } catch (IOException | MessagingException ex) {
                        Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException | JSONException e) {
                    Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }

    @Asynchronous
    public void prepareAndPushLandToInit(List<BareLands> bareLands, Long login_id, String file_path) {

        Map<String, String> propValues;
        Map<String, String> propOwner;
        Map<String, String> propFiles;
        //Map<String, String> building = new HashMap<>();

        DateFormat date_formate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        String usage_id, full_name, street_name, district_name = "", local_government_id = "", property_id = null;
        PersonTitles person_title;
        //List<PropertyClassificationDetails> propBuildings;
        List<BarelandFiles> propDocuments;
        List<WardStreets> street;
        Logindetails login_detail;
        XSSFWorkbook work_book = new XSSFWorkbook();
        XSSFSheet sheet1 = work_book.createSheet("Push Report");
        Row current_row;
        int row_count = 0, success_count = 0;
        current_row = sheet1.createRow(row_count);
        current_row.createCell(0).setCellValue("Property ID");
        current_row.createCell(1).setCellValue("Push Status");
        row_count++;
        JSONObject api_return, owner_return, building_return;
        Boolean all_well;

        if (bareLands != null && bareLands.size() > 0) {
            login_detail = registeredService.find(login_id);
            for (BareLands bareLand : bareLands) {
                current_row = sheet1.createRow(row_count);
                person_title = null;
                propValues = new HashMap<>();
                propFiles = new HashMap<>();
                propOwner = new HashMap<>();
                all_well = Boolean.FALSE;
                Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "{0} property: {1}", new Object[]{row_count, bareLand.getPropertyId()});

                //propBuildings = propertyService.findAllPropertyBuildingsByPropertyId(bareLand.getId(), false);
                current_row.createCell(0).setCellValue(bareLand.getPropertyId());

                if (bareLand.getWardStreetId() != null) {
                    street_name = bareLand.getWardStreetId().getStreetName();
                    district_name = bareLand.getWardStreetId().getLcdaWardId().getWardName();
                    local_government_id = bareLand.getWardStreetId().getLcdaWardId().getLocalCouncilDevAreaId().getInitsMapping();
                    if (bareLand.getWardStreetId().getEstateName() != null) {
                        street_name += "(" + bareLand.getWardStreetId().getEstateName() + ")";
                    }
                } else { //street id is not set, we need to find the street object created for this enum
                    street_name = bareLand.getIrregularAddress();
                    street = refrService.fetchStreetByStreetName(street_name, null);
                    if (street != null && street.size() > 0) { //this should always be the case here
                        district_name = street.get(0).getLcdaWardId().getWardName();
                        local_government_id = street.get(0).getLcdaWardId().getLocalCouncilDevAreaId().getInitsMapping();
                        if (street.get(0).getEstateName() != null) {
                            street_name += "(" + street.get(0).getEstateName() + ")";
                        }
                    }
                }

                if (!bareLand.getPropertyValuationStatus().equalsIgnoreCase(ValuationStatusEnum.PARTIAL.toString())
                        && !bareLand.getPropertyValuationStatus().equalsIgnoreCase(ValuationStatusEnum.INITSPUSHED.toString())) { //property hasn't been pushed to INITS before

                    usage_id = "405"; //ID for vacant on EPTS

                    propValues.put("pcrvalue_id", bareLand.getPropertyQualities().getInitsMapping());
                    propValues.put("local_government_id", local_government_id); //NOTICE this will faill for irregular_streets
                    propValues.put("property_usage_id", usage_id);
                    propValues.put("property_idn", bareLand.getPropertyId());
                    propValues.put("enumeration_date", date_formate.format(bareLand.getDateCaptured()));
                    propValues.put("parcel_idn", " "); //TODO I need to fix this
                    propValues.put("parcel_sheet_number", " "); //TODO I need to fix this
                    propValues.put("house_name", " "); //TODO I need to fix this
                    propValues.put("flat_no", " ");
                    propValues.put("house_no", bareLand.getPropertyNumber());
                    propValues.put("block_no", (bareLand.getIsBlockNumber()) ? bareLand.getPropertyNumber() : " ");
                    propValues.put("plot_no", " ");
                    propValues.put("deed_registration", " ");
                    propValues.put("mapping_type_id", " ");
                    propValues.put("street_name", street_name);
                    propValues.put("district_name", district_name);
                    propValues.put("enumerator", bareLand.getCreatedById().getPerson().getFullName());
                    propValues.put("postal_code", "");
                    propValues.put("property_verified", "Yes");
                    propValues.put("supervisor", login_detail.getPerson().getFullName());
                    propValues.put("land_area", bareLand.getLandSizeArea());
                    propValues.put("api_method", "post-property");
                    propValues.put("task", "push property");
                    //propValues.put("total_building_footprint", property.getBuildingArea() + ""); //TODO I need to fix this
                    propValues.put("comments", (bareLand.getDescription() != null) ? bareLand.getDescription() : " ");

                    try {
                        api_return = LedarOutsideGateway.makeEptsApiPostCall(propValues, login_detail.getPerson().getOrganization().getId());
                        Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "Bareland Property Push returned {0}", new Object[]{api_return});
                        if (api_return != null) {
                            if (api_return.getString("message").equalsIgnoreCase("Property saved")) {
                                property_id = api_return.getString("property_id");

                                bareLand.setPushStatus(ValuationStatusEnum.PARTIAL.toString());
                                bareLand.setPropertyValuationStatus(ValuationStatusEnum.PARTIAL.toString());
                                bareLand.setInitsId(api_return.getLong("property_id"));
                                bareLand.setSyncedById(login_detail);
                                //property.setLastUpdatedById(login_detail);
                                //property.getPropertyBiodataId().setLastUpdatedById(login_detail);
                                bareLand.setIsVerified(Boolean.TRUE);
                                propertyService.updateBareLandInfo(bareLand, null);
                                current_row.createCell(1).setCellValue(property_id);
                            } else {
                                if (api_return.getString("message").contains("validation.unique")) {
                                    current_row.createCell(1).setCellValue("PIN EXISTS ON EPTS");
                                } else {
                                    current_row.createCell(1).setCellValue(api_return.getString("message"));
                                }
                            }
                        } else {
                            Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, "Property save api failed");
                            current_row.createCell(1).setCellValue("API NETWORK ERROR");
                        }
                    } catch (IOException | JSONException ex) {
                        Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        current_row.createCell(1).setCellValue("API NETWORK ERROR");
                    }

                } else { //property has been pushed to INITS before, we need to only check the other components
                    //  property_id = property.getInitsId() + "";
                }

                if (property_id == null || property_id.isEmpty()) {
                    continue;
                }
                if (bareLand.getPropertyBiodataId().getPushStatus() == null || !bareLand.getPropertyBiodataId().getPushStatus().equalsIgnoreCase(ValuationStatusEnum.INITSPUSHED.toString())) {
                    try {
                        //property owner has not been push
                        if (bareLand.getPropertyBiodataId().getPersonTitleId() != null) {
                            person_title = bareLand.getPropertyBiodataId().getPersonTitleId();
                        }

                        if (bareLand.getOwnershipType().equalsIgnoreCase("INDIVIDUAL")) {
                            full_name = ((person_title != null) ? person_title.getTitleName() : "") + " " + bareLand.getPropertyBiodataId().getLastName() + " "
                                    + bareLand.getPropertyBiodataId().getFirstName();
                            propOwner.put("full_name", full_name);
                            propOwner.put("name_title_id", ((person_title != null) ? person_title.getInitsMapping() : ""));
                            propOwner.put("owner_name", full_name);
                            propOwner.put("last_name", bareLand.getPropertyBiodataId().getLastName());
                            propOwner.put("first_name", bareLand.getPropertyBiodataId().getFirstName());
                            propOwner.put("middle_name", bareLand.getPropertyBiodataId().getMiddleName());
                            propOwner.put("owner_type_id", "1"); //fix this
                            propOwner.put("mobile_phone", (bareLand.getPropertyBiodataId().getMobilePhoneNumber() != null ? bareLand.getPropertyBiodataId().getMobilePhoneNumber() : " "));
                            propOwner.put("other_phones", (bareLand.getPropertyBiodataId().getHomePhoneNumber() != null ? bareLand.getPropertyBiodataId().getHomePhoneNumber() : " "));
                            propOwner.put("contact_email", (bareLand.getPropertyBiodataId().getEmailAddress() != null ? bareLand.getPropertyBiodataId().getEmailAddress() : " "));
                        } else {
                            propOwner.put("owner_name", (bareLand.getOwnerOrganizationId() != null) ? bareLand.getOwnerOrganizationId().getOrganizationNm() : " ");
                            propOwner.put("full_name", (bareLand.getOwnerOrganizationId() != null) ? bareLand.getOwnerOrganizationId().getOrganizationNm() : " ");
                            propOwner.put("contact_email", (bareLand.getOwnerOrganizationId() != null) ? bareLand.getOwnerOrganizationId().getEmailAddress() : " ");
                            propOwner.put("mobile_phone", (bareLand.getOwnerOrganizationId() != null) ? bareLand.getOwnerOrganizationId().getOrganizationNm() : " ");
                        }
                        propOwner.put("owner_classification_id", bareLand.getPropertyBiodataId().getBiodataPersonId().getInitsMapping());
                        propOwner.put("country_id", "148"); //hard code nigeria ID from INITS
                        propOwner.put("district_name", district_name);
                        propOwner.put("created", date_formate.format(bareLand.getCreated()));
                        propOwner.put("local_government_id", local_government_id); //NOTICE this will faill for irregular_streets
                        propOwner.put("house_no", bareLand.getPropertyNumber());
                        propOwner.put("block_no", "");
                        propOwner.put("flat_no", "");
                        propOwner.put("street_name", street_name);
                        propOwner.put("property_id", property_id);
                        propOwner.put("task", "owner information");
                        propOwner.put("api_method", "post-property-owner");

                        owner_return = LedarOutsideGateway.makeEptsApiPostCall(propOwner, login_detail.getPerson().getOrganization().getId());
                        Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "Bareland owner Push returned {0}", new Object[]{owner_return});
                        if (owner_return != null) {
                            if (owner_return.getString("message").equalsIgnoreCase("Property Owner saved")) { //property saved successfully, we should go ahead and save the other components
                                bareLand.getPropertyBiodataId().setPushStatus(ValuationStatusEnum.INITSPUSHED.toString());
                                bareLand.getPropertyBiodataId().setInitsId(Long.valueOf(property_id));
                                bareLand.getPropertyBiodataId().setLastUpdatedById(login_detail);
                                propertyService.updatePropertyOwner(bareLand.getPropertyBiodataId(), null);
                                all_well = Boolean.TRUE;
                            } else {
                                current_row.createCell(1).setCellValue(owner_return.getString("message"));
                            }
                        }
                    } catch (IOException | JSONException ex) {
                        Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    current_row.createCell(1).setCellValue("API NULL");
                }

                if (all_well) {

                    propDocuments = propertyService.fetchAllBarelandFilesByLandId(bareLand.getId());

                    if (propDocuments != null && propDocuments.size() > 0) {
                        for (BarelandFiles document : propDocuments) {
                            propFiles.put("picture", document.getFileUploadId().getAbsolutePath() + "/" + document.getFileUploadId().getFileName());
                            propFiles.put("property_id", property_id);
                            propFiles.put("photo_comment", (document.getDescription() != null ? document.getDescription() : " "));

                            try {
                                LedarOutsideGateway.pushPropertyPictureToInits(propFiles, login_detail.getPerson().getOrganization().getId());

                            } catch (IOException | JSONException ex) {
                                Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                        //propertyService.updatePropertyDocumentsInfo(propDocuments, null);
                    }

                    bareLand.setIsInitsSynced(Boolean.TRUE);
                    bareLand.setPushStatus(ValuationStatusEnum.INITSPUSHED.toString());
                    bareLand.setPropertyValuationStatus(ValuationStatusEnum.INITSPUSHED.toString());
                    bareLand.setSyncedById(login_detail);
                    //property.setLastUpdatedById(login_detail);
                    bareLand.setIsVerified(Boolean.TRUE);
                    bareLand.setVerifiedById(login_detail);
                    bareLand.setInitsSyncDate(new Date());
                    propertyService.updateBareLandInfo(bareLand, null);
                    current_row.createCell(1).setCellValue("PUSHED TO INITS");
                    success_count++;
                }

                row_count++;
                property_id = null;
            }
            String file_access = file_path + "/" + "inits_push_report" + new SimpleDateFormat("MM_dd,_yyyy", Locale.ENGLISH).format(new Date()) + ".xlsx";
            Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "Push complete about to send mail: {0}", login_detail.getPerson().getFullName());
            try (FileOutputStream fis = new FileOutputStream(file_access)) {
                work_book.write(fis);

                String to_address[] = {login_detail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress()};
                String bc_address[] = {"rileleji-luc@ic-sol.net"};

                List<File> result_files = new ArrayList<>();
                result_files.add(new File(file_access));

                String email_msg = "<div> Of " + bareLands.size() + " Properties " + success_count + " was successfully pushed to INITS. Please see attachment for breakdown report</div>";

                emailSender.sendEmailWithAttachment(to_address, bc_address, null, "LEDAR INITS Enumeration Push Report", email_msg, result_files);
            } catch (IOException | MessagingException ex) {
                Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * This method is used to push property update information to INITS, this is
     * usually information that will affect the monitory figures on the Bill
     *
     * @param file_path -> the location of the file that contains information to
     * update
     * @param file_name -> the name of the file that contains the information
     * @param login_id -> Login ID of the user who initiated the process
     *
     * created by sFagade
     */
    @Asynchronous
    public void processAndUpdateBillUpdateFile(String file_path, String file_name, Long login_id) {
        Logindetails logindetail;
        Map<String, String> propValues;
        JSONObject api_return;
        org.apache.poi.ss.usermodel.Workbook excel_sheet_file;
        Row current_row;
        String notice_no;
        JSONArray jsoncargo;
        JSONObject jsonObject1;

        if (file_path != null && file_name != null && login_id != null) {
            File property_file = new File(file_path + "/" + file_name);
            if (property_file.exists()) { //this should always be the case
                try {
                    excel_sheet_file = new XSSFWorkbook(new FileInputStream(property_file));
                    org.apache.poi.ss.usermodel.Sheet sheet_ = excel_sheet_file.getSheetAt(0);
                    logindetail = registeredService.find(login_id);

                    for (int row = 1; row < sheet_.getPhysicalNumberOfRows(); row++) {
                        current_row = sheet_.getRow(row);
                        if (current_row != null) {

                            current_row.createCell(14);

                            current_row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                            notice_no = current_row.getCell(0).getStringCellValue();
                            try {
                                Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "{0} -> Processing bill update: {1}", new Object[]{row, notice_no});
                                propValues = new HashMap<>();
                                propValues.put("notice_no", notice_no);
                                propValues.put("task", "get property");
                                propValues.put("api_method", "get-property");
                                api_return = LedarOutsideGateway.makeEptsApiGetCall(propValues, logindetail.getPerson().getOrganization().getId());
                                if (api_return != null) {

                                    if (api_return.has("success") && api_return.get("success").toString().equalsIgnoreCase("true")) {
                                        jsoncargo = api_return.getJSONArray("prop_array");
                                        if (jsoncargo.length() == 1) {
                                            jsonObject1 = jsoncargo.getJSONObject(0);
                                            propValues.put("property_id", jsonObject1.get("id").toString());
                                            propValues.put("recalculate", current_row.getCell(12) + "");
                                            if (current_row.getCell(2) != null) {
                                                current_row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                                                propValues.put("land_area", current_row.getCell(2).getStringCellValue());
                                            }
                                            if (current_row.getCell(1) != null) {
                                                current_row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                                                propValues.put("property_usage_id", current_row.getCell(1).getStringCellValue());
                                            }
                                            if (current_row.getCell(3) != null) {
                                                propValues.put("existing_amount_paid", current_row.getCell(3) + "");
                                            }
                                            if (current_row.getCell(4) != null) {
                                                propValues.put("balance_cf", current_row.getCell(4) + "");
                                            }
                                            if (current_row.getCell(5) != null) {
                                                propValues.put("pcr_value_id", current_row.getCell(5) + "");
                                            }
                                            if (current_row.getCell(6) != null) {
                                                propValues.put("building_value", current_row.getCell(6) + "");
                                            }
                                            if (current_row.getCell(7) != null) {
                                                propValues.put("building_area", current_row.getCell(7) + "");
                                            }
                                            if (current_row.getCell(8) != null) {
                                                propValues.put("land_value", current_row.getCell(8) + "");
                                            }
                                            if (current_row.getCell(9) != null) {
                                                propValues.put("mill_rate", current_row.getCell(9) + "");
                                            }
                                            if (current_row.getCell(10) != null) {
                                                propValues.put("trigger_date", current_row.getCell(10) + "");
                                            }
                                            if (current_row.getCell(11) != null) {
                                                propValues.put("billing_year", current_row.getCell(11) + "");
                                            }

                                            propValues.put("flag", current_row.getCell(13) + "");
                                            propValues.put("task", "update land area");
                                            propValues.put("api_method", "update-property-land-area-or-usage");

                                            api_return = LedarOutsideGateway.makeEptsApiPostCall(propValues, logindetail.getPerson().getOrganization().getId());
                                            Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "{0} -> Bill update returned: {1}", new Object[]{row, api_return});
                                            if (api_return != null) {
                                                current_row.getCell(14).setCellValue(api_return.getString("message"));
                                            } else {
                                                current_row.getCell(14).setCellValue("BILL CALL FAILED");
                                            }

                                        } else {
                                            current_row.getCell(14).setCellValue("DUPLICATE PIN ERROR");
                                        }

                                    } else {
                                        current_row.getCell(14).setCellValue(api_return.getString("message"));
                                    }

                                } else {
                                    current_row.getCell(14).setCellValue("API CALL FAIL");
                                }
                            } catch (IOException | JSONException ex) {
                                Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                                current_row.getCell(14).setCellValue("INTERNET ERROR");
                            }
                        } else {
                            break;
                        }
                    }

                    String file_access = file_path + "/" + file_name + "_processed" + ".xlsx";
                    Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "Push complete about to send mail: {0}", logindetail.getPerson().getFullName());

                    try (FileOutputStream fis = new FileOutputStream(file_access)) {
                        excel_sheet_file.write(fis);

                        String to_address[] = {logindetail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress()};
                        //String bc_address[] = {"sfagade@ic-sol.net"};

                        List<File> result_files = new ArrayList<>();
                        result_files.add(new File(file_access));

                        String email_msg = "<div> Bill update information has been successfully pushed to EPTS. Please see attachment for breakdown report</div>";

                        emailSender.sendEmailWithAttachment(to_address, null, null, "LEDAR EPTS BILLS UPDATE  Report", email_msg, result_files);

                    } catch (IOException | MessagingException ex) {
                        Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    /**
     * This method is used to push new property information to EPTS, it only
     * push the information and does not do anything else. It sends an email
     * with the status outcome for each record at the end of the process
     *
     * @param file_path
     * @param file_name
     * @param login_id
     * @created sfagade
     */
    @Asynchronous
    public void procesPropertyInformationFile(String file_path, String file_name, Long login_id) {

        Logindetails logindetail;
        Map<String, String> propValues;
        Map<String, String> propOwner;
        Map<String, String> building = new HashMap<>();
        Row current_row;
        Boolean all_well;
        JSONObject api_return, owner_return, building_return;

        String full_name, district_name, local_govt, property_id, temp_name;
        int success_count = 0, row_count = 0;

//        DateFormat date_formate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        if (file_path != null && file_name != null && login_id != null) {
            File property_file = new File(file_path + "/" + file_name);
            temp_name = dataProcUtil.renameImageFile(file_name, "PROCESSING");

            if (property_file.exists()) {

                try {
                    XSSFWorkbook excel_sheet_file = new XSSFWorkbook(new FileInputStream(property_file));
                    XSSFSheet sheet_ = excel_sheet_file.getSheetAt(0);

                    logindetail = registeredService.find(login_id);
                    for (int row = 1; row < sheet_.getPhysicalNumberOfRows(); row++) {
                        current_row = sheet_.getRow(row);
                        if (current_row != null && current_row.getCell(0) != null) {

                            local_govt = current_row.getCell(3) + "";
                            property_id = current_row.getCell(4) + "";
                            district_name = current_row.getCell(5) + "";

                            current_row.createCell(59);
                            current_row.createCell(60);

                            propValues = new HashMap<>();
                            propOwner = new HashMap<>();
                            all_well = Boolean.FALSE;

                            Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "{0}: Pushing {1} bills: {2} - {3} - {4}",
                                    new Object[]{row, logindetail.getPerson().getOrganization().getOrganzaitionCode(), property_id, district_name, current_row.getCell(0)});

                            propValues.put("pcrvalue_id", current_row.getCell(54) + "");
                            propValues.put("local_government_id", local_govt); //NOTICE this will faill for irregular_streets
                            propValues.put("property_usage_id", current_row.getCell(13) + "");
                            propValues.put("property_idn", property_id);
                            //propValues.put("enumeration_date", date_formate.format(current_row.getCell(0).getStringCellValue()));
                            propValues.put("enumeration_date", current_row.getCell(0) + "");
                            propValues.put("parcel_idn", current_row.getCell(16) + " ");
                            propValues.put("parcel_sheet_number", current_row.getCell(17) + " "); //TODO I need to fix this
                            propValues.put("house_name", current_row.getCell(11) + " ");
                            //if (current_row.getCell(10) != null) {
                            propValues.put("flat_no", current_row.getCell(10) + "");
                            propValues.put("house_no", current_row.getCell(7) + " ");
                            propValues.put("block_no", current_row.getCell(9) + " ");
                            propValues.put("plot_no", current_row.getCell(8) + " ");
                            propValues.put("deed_registration", " ");
                            propValues.put("mapping_type_id", " ");
                            propValues.put("street_name", current_row.getCell(6).getStringCellValue());
                            propValues.put("district_name", district_name);
                            propValues.put("enumerator", current_row.getCell(37).getStringCellValue());
                            propValues.put("postal_code", " ");
                            propValues.put("property_verified", "Yes");
                            propValues.put("supervisor", current_row.getCell(38).getStringCellValue());
                            propValues.put("land_area", current_row.getCell(39) + " ");
                            propValues.put("total_building_footprint", current_row.getCell(40) + " ");
                            propValues.put("comments", current_row.getCell(58) + " ");
                            propValues.put("task", "post property");
                            propValues.put("api_method", "post-property");

                            try {
                                //api_return = LedarOutsideGateway.pushPropertyToInits(propValues, logindetail.getPerson().getOrganization().getId());
                                api_return = LedarOutsideGateway.makeEptsApiPostCall(propValues, logindetail.getPerson().getOrganization().getId());
                                if (api_return != null) {
                                    if (api_return.getString("message").equalsIgnoreCase("Property saved")) {
                                        property_id = api_return.getString("property_id");

                                        full_name = ((current_row.getCell(24) != null) ? current_row.getCell(24).getStringCellValue() + " " : " ") + ((current_row.getCell(23) != null) ? current_row.getCell(23).getStringCellValue() + " " : " ") + current_row.getCell(22).getStringCellValue();

                                        propOwner.put("full_name", full_name);
                                        propOwner.put("name_title_id", current_row.getCell(16) + " ");
                                        propOwner.put("owner_name", full_name);
                                        propOwner.put("last_name", current_row.getCell(22).getStringCellValue());
                                        propOwner.put("first_name", (current_row.getCell(24) != null) ? current_row.getCell(24).getStringCellValue() : " ");
                                        propOwner.put("middle_name", (current_row.getCell(23) != null) ? current_row.getCell(23).getStringCellValue() : " ");
                                        propOwner.put("owner_type_id", current_row.getCell(20) + "");
                                        propOwner.put("mobile_phone", current_row.getCell(28) + " ");
                                        propOwner.put("other_phones", current_row.getCell(29) + " ");
                                        propOwner.put("contact_email", current_row.getCell(27) + " ");

                                        propOwner.put("owner_classification_id", current_row.getCell(14) + "");
                                        propOwner.put("country_id", "148"); //hard code nigeria ID from INITS
                                        propOwner.put("district_name", current_row.getCell(35) + "");
                                        propOwner.put("created", current_row.getCell(0) + "");
                                        propOwner.put("local_government_id", local_govt); //NOTICE this will faill for irregular_streets
                                        propOwner.put("house_no", current_row.getCell(30) + " ");
                                        propOwner.put("block_no", current_row.getCell(32) + " ");
                                        propOwner.put("flat_no", current_row.getCell(33) + " ");
                                        propOwner.put("street_name", current_row.getCell(34) + " ");
                                        propOwner.put("property_id", property_id);
                                        propOwner.put("task", "create owner info");
                                        propOwner.put("api_method", "post-property-owner");

                                        //owner_return = LedarOutsideGateway.pushPropertyOwnerToInits(propOwner, logindetail.getPerson().getOrganization().getId());
                                        owner_return = LedarOutsideGateway.makeEptsApiPostCall(propOwner, logindetail.getPerson().getOrganization().getId());

                                        if (owner_return != null) {
                                            if (owner_return.getString("message").equalsIgnoreCase("Property Owner saved")) {

                                                building.put("building_state_id", current_row.getCell(45) + ""); //2 for completed, 1 for uncompleted
                                                building.put("building_usage_id", current_row.getCell(43) + "");
                                                building.put("property_id", property_id);
                                                building.put("building_idn", current_row.getCell(51) + " ");
                                                building.put("building_footprint", current_row.getCell(42) + "");
                                                building.put("floors", current_row.getCell(52) + "");
                                                building.put("comments", current_row.getCell(58) + " ");
                                                building.put("task", "create building info");
                                                building.put("api_method", "post-property-building");

                                                //building_return = LedarOutsideGateway.pushPropertyBuildingToInits(building, logindetail.getPerson().getOrganization().getId());
                                                building_return = LedarOutsideGateway.makeEptsApiPostCall(building, logindetail.getPerson().getOrganization().getId());

                                                if (building_return != null) {
                                                    current_row.createCell(59).setCellValue("PUSHED TO INITS");
                                                    current_row.createCell(60).setCellValue(property_id);
                                                    success_count++;
                                                } else {
                                                    all_well = Boolean.FALSE;
                                                }

                                            } else {
                                                current_row.createCell(59).setCellValue(owner_return.getString("message"));
                                            }
                                        }

                                    } else {
                                        if (api_return.getString("message").contains("validation.unique")) {
                                            current_row.createCell(59).setCellValue("PIN EXISTS ON EPTS");
                                        } else {
                                            current_row.createCell(59).setCellValue(api_return.getString("message"));
                                        }
                                    }
                                } else {
                                    Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, "Property save api failed");
                                }
                            } catch (JSONException ex) {
                                Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ue) {
                                Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ue.getMessage());
                                current_row.createCell(59).setCellValue("Internet Error");
                            }
                            row_count++;
                            property_id = null;

                            if (row % 10 == 0) {
                                try (FileOutputStream fis = new FileOutputStream(file_path + "/" + temp_name)) {
                                    excel_sheet_file.write(fis);
                                }
                            }
                        }
                    }

                    String file_access = file_path + "/" + "inits_push_report_" + new SimpleDateFormat("MM_dd,_yyyy", Locale.ENGLISH).format(new Date()) + ".xlsx";
                    Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "Push complete about to send mail: {0}", logindetail.getPerson().getFullName());

                    try (FileOutputStream fis = new FileOutputStream(file_access)) {
                        excel_sheet_file.write(fis);

                        String to_address[] = {logindetail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress()};
                        //String bc_address[] = {"sfagade@ic-sol.net"};

                        List<File> result_files = new ArrayList<>();
                        result_files.add(new File(file_access));

                        String email_msg = "<div> Of " + row_count + " Properties " + success_count + " was successfully pushed to INITS. Please see attachment for breakdown report</div>";

                        emailSender.sendEmailWithAttachment(to_address, null, null, "LEDAR INITS Enumeration Push Report", email_msg, result_files);

                    } catch (IOException | MessagingException ex) {
                        Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    @Asynchronous
    public void prepareAndPushPropertiesToInit(List<WardLandProperties> properties, Long login_id, String file_path) {

        Map<String, String> propValues;
        Map<String, String> propOwner;
        Map<String, String> propFiles;
        Map<String, String> building = new HashMap<>();

        DateFormat date_formate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        String usage_id, full_name, street_name, district_name = "", local_government_id = "", property_id = null;
        PersonTitles person_title;
        List<PropertyClassificationDetails> propBuildings;
        List<PropertyDocuments> propDocuments;
        List<WardStreets> street;
        Logindetails login_detail;
        XSSFWorkbook work_book = new XSSFWorkbook();
        XSSFSheet sheet1 = work_book.createSheet("Push Report");
        Row current_row;
        int row_count = 0, success_count = 0;
        current_row = sheet1.createRow(row_count);
        current_row.createCell(0).setCellValue("Property ID");
        current_row.createCell(1).setCellValue("Push Status");
        row_count++;
        JSONObject api_return, owner_return, building_return;
        Boolean all_well;

        if (properties != null && properties.size() > 0) {
            login_detail = registeredService.find(login_id);
            for (WardLandProperties property : properties) {
                current_row = sheet1.createRow(row_count);
                person_title = null;
                propValues = new HashMap<>();
                propFiles = new HashMap<>();
                propOwner = new HashMap<>();
                all_well = Boolean.FALSE;
                Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "{0} property: {1}", new Object[]{row_count, property.getPropertyId()});

                propBuildings = propertyService.findAllPropertyBuildingsByPropertyId(property.getId(), false);

                current_row.createCell(0).setCellValue(property.getPropertyId());

                if (property.getWardStreetId() != null) {
                    street_name = property.getWardStreetId().getStreetName();
                    district_name = property.getWardStreetId().getLcdaWardId().getWardName();
                    local_government_id = property.getWardStreetId().getLcdaWardId().getLocalCouncilDevAreaId().getInitsMapping();
                    if (property.getWardStreetId().getEstateName() != null) {
                        street_name += "(" + property.getWardStreetId().getEstateName() + ")";
                    }
                } else { //street id is not set, we need to find the street object created for this enum
                    street_name = property.getIrregularAddress();
                    street = refrService.fetchStreetByStreetName(street_name, null);
                    if (street != null && street.size() > 0) { //this should always be the case here
                        district_name = street.get(0).getLcdaWardId().getWardName();
                        local_government_id = street.get(0).getLcdaWardId().getLocalCouncilDevAreaId().getInitsMapping();
                        if (street.get(0).getEstateName() != null) {
                            street_name += "(" + street.get(0).getEstateName() + ")";
                        }
                    }
                }

                if (!property.getPropertyValuationStatus().equalsIgnoreCase(ValuationStatusEnum.PARTIAL.toString())
                        && !property.getPropertyValuationStatus().equalsIgnoreCase(ValuationStatusEnum.INITSPUSHED.toString())) { //property hasn't been pushed to INITS before
                    /**
                     * if (propBuildings.get(0).getResidentialUseId() != null) {
                     * usage_id =
                     * propBuildings.get(0).getResidentialUseId().getInitsMapping();
                     * } else
                     */
                    if (propBuildings.get(0).getUsageCategory() != null) {
                        usage_id = propBuildings.get(0).getUsageCategory().getInitsMapping().toString();
                    } else {
                        usage_id = "";
                    }

                    propValues.put("pcrvalue_id", property.getPropertyQualities().getInitsMapping());
                    propValues.put("local_government_id", local_government_id); //NOTICE this will faill for irregular_streets
                    propValues.put("property_usage_id", usage_id);
                    propValues.put("property_idn", property.getPropertyId());
                    propValues.put("enumeration_date", date_formate.format(property.getDateCaptured()));
                    propValues.put("parcel_idn", ""); //TODO I need to fix this
                    propValues.put("parcel_sheet_number", ""); //TODO I need to fix this
                    propValues.put("house_name", propBuildings.get(0).getPropertyName()); //TODO I need to fix this
                    propValues.put("flat_no", "");
                    propValues.put("house_no", property.getPropertyNumber());
                    propValues.put("block_no", (property.getIsBlockNumber()) ? property.getPropertyNumber() : "");
                    propValues.put("plot_no", "");
                    propValues.put("deed_registration", "");
                    propValues.put("mapping_type_id", "");
                    propValues.put("street_name", street_name);
                    propValues.put("district_name", district_name);
                    propValues.put("enumerator", property.getCapturedById().getPerson().getFullName());
                    propValues.put("postal_code", "");
                    propValues.put("property_verified", "Yes");
                    propValues.put("supervisor", login_detail.getPerson().getFullName());
                    propValues.put("land_area", property.getLandSize());
                    propValues.put("total_building_footprint", property.getBuildingArea() + ""); //TODO I need to fix this
                    propValues.put("comments", (property.getDescription() != null) ? property.getDescription() : "");

                    try {
                        api_return = LedarOutsideGateway.pushPropertyToInits(propValues, login_detail.getPerson().getOrganization().getId());
                        if (api_return != null) {
                            if (api_return.getString("message").equalsIgnoreCase("Property saved")) {
                                property_id = api_return.getString("property_id");

                                property.setPushStatus(ValuationStatusEnum.PARTIAL.toString());
                                property.setPropertyValuationStatus(ValuationStatusEnum.PARTIAL.toString());
                                property.setInitsId(api_return.getLong("property_id"));
                                property.setSyncedById(login_detail);
                                property.setLastUpdatedById(login_detail);
                                property.getPropertyBiodataId().setLastUpdatedById(login_detail);
                                property.setIsVerified(Boolean.TRUE);
                                propertyService.updateWardLandProperty(property, null);
                                current_row.createCell(1).setCellValue(property_id);
                            } else {
                                if (api_return.getString("message").contains("validation.unique")) {
                                    current_row.createCell(1).setCellValue("PIN EXISTS ON EPTS");
                                } else {
                                    current_row.createCell(1).setCellValue(api_return.getString("message"));
                                }
                            }
                        } else {
                            Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, "Property save api failed");
                            current_row.createCell(1).setCellValue("API NETWORK ERROR");
                        }
                    } catch (IOException | JSONException ex) {
                        Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        current_row.createCell(1).setCellValue("API NETWORK ERROR");
                    }

                } else { //property has been pushed to INITS before, we need to only check the other components
                    property_id = property.getInitsId() + "";
                }

                if (property_id == null || property_id.isEmpty()) {
                    row_count++;
                    continue;
                }
                if (property.getPropertyBiodataId().getPushStatus() == null || !property.getPropertyBiodataId().getPushStatus().equalsIgnoreCase(ValuationStatusEnum.INITSPUSHED.toString())) {
                    try {
                        //property owner has not been push
                        if (property.getPropertyBiodataId().getPersonTitleId() != null) {
                            person_title = property.getPropertyBiodataId().getPersonTitleId();
                        }

                        if (property.getOwnershipType().equalsIgnoreCase("INDIVIDUAL")) {
                            full_name = ((person_title != null) ? person_title.getTitleName() : "") + " " + property.getPropertyBiodataId().getLastName() + " "
                                    + property.getPropertyBiodataId().getFirstName();
                            propOwner.put("full_name", full_name);
                            propOwner.put("name_title_id", ((person_title != null) ? person_title.getInitsMapping() : ""));
                            propOwner.put("owner_name", full_name);
                            propOwner.put("last_name", property.getPropertyBiodataId().getLastName());
                            propOwner.put("first_name", property.getPropertyBiodataId().getFirstName());
                            propOwner.put("middle_name", property.getPropertyBiodataId().getMiddleName());
                            propOwner.put("owner_type_id", "1"); //fix this
                            propOwner.put("mobile_phone", (property.getPropertyBiodataId().getMobilePhoneNumber() != null ? property.getPropertyBiodataId().getMobilePhoneNumber() : ""));
                            propOwner.put("other_phones", (property.getPropertyBiodataId().getHomePhoneNumber() != null ? property.getPropertyBiodataId().getHomePhoneNumber() : ""));
                            propOwner.put("contact_email", (property.getPropertyBiodataId().getEmailAddress() != null ? property.getPropertyBiodataId().getEmailAddress() : ""));
                        } else {
                            propOwner.put("owner_name", (property.getOwnerOrganizationId() != null) ? property.getOwnerOrganizationId().getOrganizationNm() : "");
                            propOwner.put("full_name", (property.getOwnerOrganizationId() != null) ? property.getOwnerOrganizationId().getOrganizationNm() : "");
                            propOwner.put("contact_email", (property.getOwnerOrganizationId() != null) ? property.getOwnerOrganizationId().getEmailAddress() : "");
                            propOwner.put("mobile_phone", (property.getOwnerOrganizationId() != null) ? property.getOwnerOrganizationId().getOrganizationNm() : "");
                        }
                        propOwner.put("owner_classification_id", property.getPropertyBiodataId().getBiodataPersonId().getInitsMapping());
                        propOwner.put("country_id", "148"); //hard code nigeria ID from INITS
                        propOwner.put("district_name", district_name);
                        propOwner.put("created", date_formate.format(property.getCreated()));
                        propOwner.put("local_government_id", local_government_id); //NOTICE this will faill for irregular_streets
                        propOwner.put("house_no", property.getPropertyNumber());
                        propOwner.put("block_no", "");
                        propOwner.put("flat_no", "");
                        propOwner.put("street_name", street_name);
                        propOwner.put("property_id", property_id);

                        owner_return = LedarOutsideGateway.pushPropertyOwnerToInits(propOwner, login_detail.getPerson().getOrganization().getId());
                        if (owner_return != null) {
                            if (owner_return.getString("message").equalsIgnoreCase("Property Owner saved")) { //property saved successfully, we should go ahead and save the other components
                                property.getPropertyBiodataId().setPushStatus(ValuationStatusEnum.INITSPUSHED.toString());
                                property.getPropertyBiodataId().setInitsId(Long.valueOf(property_id));
                                property.getPropertyBiodataId().setLastUpdatedById(login_detail);
                                propertyService.updatePropertyOwner(property.getPropertyBiodataId(), null);
                            } else {
                                current_row.createCell(1).setCellValue(owner_return.getString("message"));
                            }
                        } else {
                            current_row.createCell(1).setCellValue("API NETWORK ERROR");
                        }
                    } catch (IOException | JSONException ex) {
                        Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    current_row.createCell(1).setCellValue("API NULL");
                }

                all_well = Boolean.TRUE;
                for (PropertyClassificationDetails buildingDetail : propBuildings) {
                    if (buildingDetail.getUsageCategory() != null) {
                        usage_id = buildingDetail.getUsageCategory().getInitsMapping().toString();
                    } /**
                     * else if (buildingDetail.getCommercialTypeId() != null) {
                     * usage_id =
                     * buildingDetail.getCommercialTypeId().getInitsMapping(); }
                     */
                    else {
                        usage_id = "";
                    }
                    building.put("building_state_id", (buildingDetail.getIsCompletedStructure()) ? "2" : "1"); //2 for completed, 1 for uncompleted
                    building.put("building_usage_id", usage_id);
                    building.put("property_id", property_id);
                    building.put("building_idn", "");
                    building.put("building_footprint", buildingDetail.getBuildingFootprint() + "");
                    building.put("floors", buildingDetail.getNoOfFloors().toString());
                    building.put("comments", (property.getDescription() != null) ? property.getDescription() : "");

                    try {
                        building_return = LedarOutsideGateway.pushPropertyBuildingToInits(building, login_detail.getPerson().getOrganization().getId());
                        if (building_return != null) {
                            if (building_return.getString("message").equalsIgnoreCase("Property building saved")) {
                                buildingDetail.setInitsId(building_return.getLong("propertyBuilding_id"));
                                buildingDetail.setLastUpdatedById(login_detail);
                                buildingDetail.setPushStatus(ValuationStatusEnum.INITSPUSHED.toString());
                            } else {
                                current_row.createCell(2).setCellValue(building_return.getString("message"));
                                all_well = Boolean.FALSE;
                            }
                        } else {
                            current_row.createCell(2).setCellValue("");
                            all_well = Boolean.FALSE;
                        }
                    } catch (IOException | JSONException ex) {
                        Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                propertyService.updateBuildingDetailsInfo(propBuildings, null);
                Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "Updated building information: {0}", propBuildings.size());

                if (all_well) {

                    propDocuments = propertyService.findAllPropertyDocumentsByPropertyId(property.getId(), Boolean.FALSE);

                    if (propDocuments != null && propDocuments.size() > 0) {
                        for (PropertyDocuments document : propDocuments) {
                            propFiles.put("picture", document.getFileUploadId().getAbsolutePath() + "/" + document.getFileUploadId().getFileName());

                            propFiles.put("property_id", property_id);
                            propFiles.put("photo_comment", (document.getRemarks() != null ? document.getRemarks() : ""));

                            try {
                                LedarOutsideGateway.pushPropertyPictureToInits(propFiles, login_detail.getPerson().getOrganization().getId());

                            } catch (IOException | JSONException ex) {
                                Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                        //propertyService.updatePropertyDocumentsInfo(propDocuments, null);
                    }

                    property.setIsInitsSynced(Boolean.TRUE);
                    property.setPushStatus(ValuationStatusEnum.INITSPUSHED.toString());
                    property.setPropertyValuationStatus(ValuationStatusEnum.INITSPUSHED.toString());
                    property.setSyncedById(login_detail);
                    property.setLastUpdatedById(login_detail);
                    //property.setIsVerified(Boolean.TRUE);
                    //property.setVerifiedById(login_detail);
                    property.setInitsSyncDate(new Date());
                    propertyService.updateWardLandProperty(property, null);
                    current_row.createCell(1).setCellValue("PUSHED TO INITS");
                    success_count++;
                }

                row_count++;
                property_id = null;
            }
            String file_access = file_path + "/" + "inits_push_report" + new SimpleDateFormat("MM_dd,_yyyy", Locale.ENGLISH).format(new Date()) + ".xlsx";
            Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.INFO, "Push complete about to send mail: {0}", login_detail.getPerson().getFullName());
            try (FileOutputStream fis = new FileOutputStream(file_access)) {
                work_book.write(fis);

                String to_address[] = {login_detail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress()};
                String bc_address[] = {"rileleji-luc@ic-sol.net"};

                List<File> result_files = new ArrayList<>();
                result_files.add(new File(file_access));

                String email_msg = "<div> Of " + properties.size() + " Properties " + success_count + " was successfully pushed to INITS. Please see attachment for breakdown report</div>";

                emailSender.sendEmailWithAttachment(to_address, bc_address, null, "LEDAR INITS Enumeration Push Report", email_msg, result_files);
            } catch (IOException | MessagingException ex) {
                Logger.getLogger(EptsApiDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public String fetchEptsNoticeId(String propertyIdn) throws IOException {

        String noticeId = null;

        Map<String, String> propValues = new HashMap<>();
        propValues.put("notice_no", propertyIdn);
        propValues.put("task", "get property");
        propValues.put("api_method", "get-property");

        JSONArray jsoncargo;
        JSONObject jsonObject1;
        JSONObject api_return;

        api_return = LedarOutsideGateway.makeEptsApiGetCall(propValues, null);

        if (api_return != null) {
            if (api_return.has("success") && api_return.get("success").toString().equalsIgnoreCase("true")) {
                jsoncargo = api_return.getJSONArray("prop_array");
                if (jsoncargo.length() == 1) {
                    jsonObject1 = jsoncargo.getJSONObject(0);
                    noticeId = jsonObject1.get("id").toString();
                }
            }
        }

        return noticeId;
    }
}
