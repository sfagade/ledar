package net.icsl.ledar.web.bean;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.UploadedFile;

import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.Notifications;
import net.icsl.ledar.ejb.service.NotificationDataService;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.ejb.util.DataloadProcessor;
import net.icsl.ledar.ejb.util.EmailSender;
import net.icsl.ledar.ejb.util.EptsApiDataProcessor;
import net.icsl.ledar.ejb.dto.PropertySmallBillsDto;
import net.icsl.ledar.web.util.ApplicationUtility;
import net.icsl.ledar.web.util.DataLoadProcessor;
import net.icsl.ledar.web.util.FacesSupportUtil;

/**
 * @author sfagade
 */
@Named(value = "uploadManager")
@ConversationScoped
public class UploadManagerBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private NotificationDataService noteService;

    @Inject
    private RegisteredUsersDataService registeredService;
    @Inject
    PrintedBillsService billService;
    @Inject
    private DataloadProcessor dataLoader;
    @Inject
    private EmailSender emailSender;

    @Inject
    private FacesContext facesContext;

    @Inject
    private DataLoadProcessor dataProcessor;
    @Inject
    private EptsApiDataProcessor eptsProcessor;

    @Inject
    private LoginBean loginBean;
    @Inject
    private LedarApplicationBean ledarApp;

    private final FacesContext context = FacesContext.getCurrentInstance();

    private PropertySmallBillsDto selectedProperty;
    private Properties prop;
    private Notifications notification;
    private Logindetails selectedFieldOfficer;

    private List<Logindetails> allFieldOfficers;

    private UploadedFile file_, rviewPic, fviewPic, sviewPic;

    private String[] data_type = {"Bill Update Information", "Bill Validation", "New Bills Information", "New Building Information", "Payment Information", "Property Information", "Streets Information",
        "Update Owner Information"};
    private String selected_data_type, selectedYear, searchProperty, selected_file, formRemarks, firstName, lastName, emailAddr, phoneNo;
    private Date dateDelivered;
    private Boolean updateRequired;
    private int recordCount;
    private List<String> sheetNames, fieldYears;

    @PostConstruct
    public void init() {
        String view_id = facesContext.getViewRoot().getViewId();

        selectedProperty = null;
        firstName = null;
        lastName = null;
        dateDelivered = null;
        emailAddr = null;
        phoneNo = null;
        selectedFieldOfficer = null;
        Long[] field_roles = {9L, 19L, 26L};
        allFieldOfficers = registeredService.fetchAllLcdaChairmen(Arrays.asList(field_roles), loginBean.getPerson().getOrganization().getId());

        if (view_id.equals("/app/appAdmin/uploadData.xhtml")) {
            String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "ADMINISTRATOR"}; //I need to restrict access to this page to just the Admin person
            if (!FacesSupportUtil.isUserInRole(accepted_roles) || loginBean == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", ""));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/app/index.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(UploadManagerBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        if (prop == null) {
            emailSender = new EmailSender(loginBean.getPerson().getOrganization().getId());
            prop = emailSender.getProperties();
        }
    }

    public void uploadDataFile() {

        HashMap<String, ArrayList<String>> returnedInfo;
        String error_message = "", success_message, download_dir;
        File upload_folder;
        FileUploads paymentFile;

        if ((file_ != null) && (selected_data_type != null)) {

            selected_file = file_.getFileName();
            success_message = selected_file;
            long fileSizeInKB = file_.getSize() / 1024;

            returnedInfo = new HashMap<>();
            returnedInfo.put("errors", new ArrayList<String>());
            returnedInfo.put("success", new ArrayList<String>());

            switch (selected_data_type) {
                case "New Bills Information":
                    //user is uploading Property Bills Information
                    if (fileSizeInKB <= 7287) {
                        if (selectedYear != null) {
                            download_dir = prop.getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/bill_files";
                            //upload_folder = new File(download_dir + "/" + ApplicationUtility.renameImageFile(file_.getFileName(), "UPLOADED"));
                            upload_folder = ApplicationUtility.uploadUserFile(download_dir, file_);
                            if (upload_folder != null) {
                                //I need to do this here because I don't want to do it more than once
                                paymentFile = billService.saveNewFileUpload(new FileUploads(null, null, file_.getFileName(), file_.getContentType(), "BILLS FILE", null,
                                        loginBean.getPerson().getLogindetailId(), download_dir, null, null));

                                dataLoader.loadAndProcessBillInformation(download_dir, upload_folder.getName(), selectedYear, loginBean.getPerson().getLogindetailId().getId(), paymentFile.getId());
                                returnedInfo.get("success").add("Bills file has been uploaded successfully and processing has started, you will be sent an email with the result when processing is done.");
                            } else {
                                FacesSupportUtil.writeFilterMessage("Failed to upload file, please contact ADMIN", FacesMessage.SEVERITY_ERROR, "upldform:upl_btn");
                            }

                        } else {
                            Logger.getLogger(UploadManagerBean.class.getName()).log(Level.SEVERE, null, "Billing year not selected");
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select billing year before upload", "Error Message");
                            FacesContext.getCurrentInstance().addMessage("upldform:upl_btn", message);
                        }
                    } else {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Selected file is too large, please select file not larger than 2MB.", "Error Message");
                        FacesContext.getCurrentInstance().addMessage("upldform:upl_btn", message);
                    }
                    break;
                case "Property Information":
                    if (fileSizeInKB <= 5187) {
                        download_dir = prop.getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/" + loginBean.getPerson().getOrganization().getOrganzaitionCode()
                                + "/property_files/";
                        upload_folder = ApplicationUtility.uploadUserFile(download_dir, file_);
                        if (upload_folder != null) {

                            eptsProcessor.procesPropertyInformationFile(download_dir, upload_folder.getName(), loginBean.getPerson().getLogindetailId().getId());

                            returnedInfo.get("success").add("Properties file has started processing, you will be sent an email with the result when processing is done.");

                        } else {
                            FacesSupportUtil.writeFilterMessage("Failed to upload file, please contact ADMIN", FacesMessage.SEVERITY_ERROR, "upldform:upl_btn");
                        }
                    } else {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Selected file is too large, please select file not larger than 2MB.", "Error Message");
                        FacesContext.getCurrentInstance().addMessage("upldform:upl_btn", message);
                    }
                    //returnedInfo = getDataProcessor().procesPropertyInformationFile(file_);
                    break;
                case "Payment Information":
                    String[] accepted_roles = {"ADMINISTRATOR"};
                    if (FacesSupportUtil.isUserInRole(accepted_roles)) { //only Administrator can load PAYMENT
                        if (selectedYear != null) {
                            download_dir = prop.getProperty("bills.folder") + "/" + selectedYear + "/payment_files";
                            File download_folder = new File(download_dir);
                            if (!download_folder.exists()) {
                                download_folder.mkdirs(); //create folder
                            }
                            
                            upload_folder = new File(download_dir + "/" + file_.getFileName());

                            if (!upload_folder.exists()) { //this will be the case for the first upload done for the day

                                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(download_dir + "/" + file_.getFileName())))) {
                                    stream.write(file_.getContents());
                                    //I need to do this here because I don't want to do it more than once
                                    paymentFile = billService.saveNewFileUpload(new FileUploads(null, null, upload_folder.getName(), file_.getContentType(), "PAYMENT FILE", null,
                                            loginBean.getPerson().getLogindetailId(), download_dir, null, null));

                                    dataLoader.processBillsPaymentInformation(download_dir, file_.getFileName(), selectedYear, loginBean.getPerson().getLogindetailId().getId(), paymentFile.getId());
                                    returnedInfo.get("success").add("Payment file has been uploaded successfully and processing has started, you will be sent an email with the result when processing is done.");

                                } catch (FileNotFoundException ex) {
                                    Logger.getLogger(UploadManagerBean.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(UploadManagerBean.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            } else {
                                returnedInfo.get("errors").add("Uploaded file already exist on server, please rename the file and try again");
                            }
                            //returnedInfo = getDataProcessor().processBillsPaymentInformation(file_, selectedYear);
                        } else {
                            Logger.getLogger(UploadManagerBean.class.getName()).log(Level.SEVERE, null, "Billing year not selected");
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select billing year before upload", "Error Message");
                            FacesContext.getCurrentInstance().addMessage("upldform:upl_btn", message);
                        }
                    } else {
                        Logger.getLogger(UploadManagerBean.class.getName()).log(Level.SEVERE, null, "Wrong payment user");
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to upload payment data", "Error Message");
                        FacesContext.getCurrentInstance().addMessage("upldform:upl_btn", message);
                    }
                    break;
                case "Bill Validation":
                    download_dir = prop.getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/bill_validation";

                    //upload_folder = new File(download_dir + "/" + ApplicationUtility.renameImageFile(file_.getFileName(), "UPLOADED"));
                    upload_folder = ApplicationUtility.uploadUserFile(download_dir, file_);

                    if (upload_folder != null) {
                        billService.saveNewFileUpload(new FileUploads(null, null, file_.getFileName(), file_.getContentType(), "VALIDATION FILE", null,
                                loginBean.getPerson().getLogindetailId(), download_dir, null, null));

                        dataLoader.loadAndValidateBillRegistrationStatus(download_dir, upload_folder.getName(), loginBean.getPerson().getLogindetailId().getId());
                        returnedInfo.get("success").add("Bills validation process has started, you will be sent an email with the result when processing is done.");
                    } else {
                        FacesSupportUtil.writeFilterMessage("Failed to upload file, please contact ADMIN", FacesMessage.SEVERITY_ERROR, "upldform:upl_btn");
                    }

                    break;
                case "Streets Information":
                    download_dir = prop.getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/streets_load";

                    //upload_folder = new File(download_dir + "/" + ApplicationUtility.renameImageFile(file_.getFileName(), "UPLOADED"));
                    upload_folder = ApplicationUtility.uploadUserFile(download_dir, file_);

                    if (upload_folder != null) {
                        billService.saveNewFileUpload(new FileUploads(null, null, file_.getFileName(), file_.getContentType(), "STREET INFORMATION", null,
                                loginBean.getPerson().getLogindetailId(), download_dir, null, null));

                        dataLoader.loadDistrictStreet(download_dir, upload_folder.getName(), loginBean.getPerson().getLogindetailId().getId());
                        returnedInfo.get("success").add("Street Information load process has started, you will be sent an email with the result when processing is done.");
                    } else {
                        FacesSupportUtil.writeFilterMessage("Failed to upload file, please contact ADMIN", FacesMessage.SEVERITY_ERROR, "upldform:upl_btn");
                    }

                    break;
                case "Bill Update Information":

                    if (fileSizeInKB <= 5187) {

                        download_dir = prop.getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/bill_files";
                        upload_folder = ApplicationUtility.uploadUserFile(download_dir, file_);

                        if (upload_folder != null) {
                            //I need to do this here because I don't want to do it more than once

                            eptsProcessor.processAndUpdateBillUpdateFile(download_dir, upload_folder.getName(), loginBean.getPerson().getLogindetailId().getId());
                            returnedInfo.get("success").add("Bills file has been uploaded successfully and processing has started, you will be sent an email with the result when processing is done.");
                        } else {
                            FacesSupportUtil.writeFilterMessage("Failed to upload file, please contact ADMIN", FacesMessage.SEVERITY_ERROR, "upldform:upl_btn");
                        }

                    } else {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Selected file is too large, please select file not larger than 2MB.", "Error Message");
                        FacesContext.getCurrentInstance().addMessage("upldform:upl_btn", message);
                    }
                    break;
                case "New Building Information":
                    download_dir = prop.getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/building_files";
                    upload_folder = ApplicationUtility.uploadUserFile(download_dir, file_);

                    if (upload_folder != null) { //upload is successful
                        billService.saveNewFileUpload(new FileUploads(null, null, file_.getFileName(), file_.getContentType(), "BILLS FILE", null,
                                loginBean.getPerson().getLogindetailId(), download_dir, null, null));
                        eptsProcessor.processNewBuildingFile(download_dir, upload_folder.getName(), loginBean.getPerson().getLogindetailId().getId());
                        returnedInfo.get("success").add("Buildings file has been uploaded successfully and processing has started, you will be sent an email with the result when processing is done.");
                    } else {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Selected file is too large, please select file not larger than 2MB.", "Error Message");
                        FacesContext.getCurrentInstance().addMessage("upldform:upl_btn", message);
                    }
                    break;
                case "Update Owner Information":
                    download_dir = prop.getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/owner_files";
                    upload_folder = ApplicationUtility.uploadUserFile(download_dir, file_);

                    if (upload_folder != null) { //upload is successful
                        billService.saveNewFileUpload(new FileUploads(null, null, file_.getFileName(), file_.getContentType(), "BILLS FILE", null,
                                loginBean.getPerson().getLogindetailId(), download_dir, null, null));
                        eptsProcessor.processOwnerInformationFile(download_dir, upload_folder.getName(), loginBean.getPerson().getLogindetailId().getId());
                        returnedInfo.get("success").add("Owner Information file has been uploaded successfully and processing has started, you will be sent an email with the result when processing"
                                + " is done.");
                    } else {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Selected file is too large, please select file not larger than 2MB.", "Error Message");
                        FacesContext.getCurrentInstance().addMessage("upldform:upl_btn", message);
                    }
                    break;
                default:
                    break;
            }

            //if ((returnedInfo != null)) {
            if ((returnedInfo.get("errors").size() > 0)) {

                for (int x = 0; x < returnedInfo.get("errors").size(); x++) {
                    error_message += "\n" + returnedInfo.get("errors").get(x);
                }

                notification = new Notifications();
                notification.setNotifiStatus("4");
                notification.setNotifiType("1");
                notification.setFullMessage(error_message);
                notification.setBriefMessage("Dataload Errors Report");
                notification.setLogindetailId(getLoginBean().getPerson().getLogindetailId());
                notification.setCreatedById(getLoginBean().getPerson().getLogindetailId());

                notification = noteService.createNewNotification(notification, null);
            }
            if (returnedInfo.get("success").size() > 0) {

                for (int x = 0; x < returnedInfo.get("success").size(); x++) {
                    success_message += "\n" + returnedInfo.get("success").get(x);
                }
            }

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, success_message + "\n" + error_message, "Success Message");
            FacesContext.getCurrentInstance().addMessage("upldform:upl_btn", message);

        } else {
            error_message = "Please select a valid excel (.xls) file.";
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, error_message, "Error Message");
            FacesContext.getCurrentInstance().addMessage("upldform:upl_btn", message);
        }

    }

    public void findProperties(ActionEvent actionEvent) {

        if (searchProperty != null) {
            //ledarApp.setPropertyBills(null);
            //ledarApp.setPropertyBills(landPropertyService.fetchAllPropertyBillsByPropertyId(searchProperty, true));
        }
    }

    /**
     * @return the file_
     */
    public UploadedFile getFile_() {
        return file_;
    }

    /**
     * @param file_ the file_ to set
     */
    public void setFile_(UploadedFile file_) {
        this.file_ = file_;
    }

    /**
     * @return the data_type
     */
    public String[] getData_type() {
        return data_type;
    }

    /**
     * @param data_type the data_type to set
     */
    public void setData_type(String[] data_type) {
        this.data_type = data_type;
    }

    /**
     * @return the selected_data_type
     */
    public String getSelected_data_type() {
        return selected_data_type;
    }

    /**
     * @param selected_data_type the selected_data_type to set
     */
    public void setSelected_data_type(String selected_data_type) {
        this.selected_data_type = selected_data_type;
    }

    public String getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(String selectedYear) {
        this.selectedYear = selectedYear;
    }

    public Date getDateDelivered() {
        return dateDelivered;
    }

    public void setDateDelivered(Date dateDelivered) {
        this.dateDelivered = dateDelivered;
    }

    public LedarApplicationBean getLedarApp() {
        return ledarApp;
    }

    public void setLedarApp(LedarApplicationBean ledarApp) {
        this.ledarApp = ledarApp;
    }

    public List<String> getFieldYears() {

        if (fieldYears == null) {
            fieldYears = new ArrayList<>();
            final int future_years = 2;
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            fieldYears.add((currentYear - 1) + "");
            for (int year = 0; year < future_years; year++) {
                fieldYears.add((currentYear++) + "");
            }
        }

        return fieldYears;
    }

    public List<String> getSheetNames() {
        return sheetNames;
    }

    public void setSheetNames(List<String> sheetNames) {
        this.sheetNames = sheetNames;
    }

    public String getSelected_file() {
        return selected_file;
    }

    public void setSelected_file(String selected_file) {
        this.selected_file = selected_file;
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
     * @return the selectedProperty
     */
    public PropertySmallBillsDto getSelectedProperty() {
        return selectedProperty;
    }

    /**
     * @param selectedProperty the selectedProperty to set
     */
    public void setSelectedProperty(PropertySmallBillsDto selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    /**
     * @return the searchProperty
     */
    public String getSearchProperty() {
        return searchProperty;
    }

    /**
     * @param searchProperty the searchProperty to set
     */
    public void setSearchProperty(String searchProperty) {
        this.searchProperty = searchProperty;
    }

    /**
     * @return the formRemarks
     */
    public String getFormRemarks() {
        return formRemarks;
    }

    /**
     * @param formRemarks the formRemarks to set
     */
    public void setFormRemarks(String formRemarks) {
        this.formRemarks = formRemarks;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the emailAddr
     */
    public String getEmailAddr() {
        return emailAddr;
    }

    /**
     * @param emailAddr the emailAddr to set
     */
    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }

    /**
     * @return the phoneNo
     */
    public String getPhoneNo() {
        return phoneNo;
    }

    /**
     * @param phoneNo the phoneNo to set
     */
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
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

    public Logindetails getSelectedFieldOfficer() {
        return selectedFieldOfficer;
    }

    public void setSelectedFieldOfficer(Logindetails selectedFieldOfficer) {
        this.selectedFieldOfficer = selectedFieldOfficer;
    }

    public List<Logindetails> getAllFieldOfficers() {
        return allFieldOfficers;
    }

    public void setAllFieldOfficers(List<Logindetails> allFieldOfficers) {
        this.allFieldOfficers = allFieldOfficers;
    }

    /**
     * @return the updateRequired
     */
    public Boolean getUpdateRequired() {
        return updateRequired;
    }

    /**
     * @param updateRequired the updateRequired to set
     */
    public void setUpdateRequired(Boolean updateRequired) {
        this.updateRequired = updateRequired;
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
     * @return the dataProcessor
     */
    public DataLoadProcessor getDataProcessor() {
        return dataProcessor;
    }

    /**
     * @param dataProcessor the dataProcessor to set
     */
    public void setDataProcessor(DataLoadProcessor dataProcessor) {
        this.dataProcessor = dataProcessor;
    }

}
