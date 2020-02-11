package net.icsl.ledar.web.bean;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

import net.icsl.ledar.ejb.model.ComplaintSources;
import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.PersonTitles;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import net.icsl.ledar.ejb.model.ComlaintDocuments;
import net.icsl.ledar.ejb.model.ComplainerRelationship;
import net.icsl.ledar.ejb.model.ComplaintDetails;
import net.icsl.ledar.ejb.model.ComplaintTypes;
import net.icsl.ledar.ejb.model.DocumentTypes;
import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.model.PropertyComplaints;
import net.icsl.ledar.ejb.model.UniqueUserIdentifications;
import net.icsl.ledar.ejb.model.VisitPurpose;
import net.icsl.ledar.ejb.model.Visitors;
import net.icsl.ledar.ejb.service.ComplaintDataService;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.ReferenceDataService;
import net.icsl.ledar.ejb.util.EmailSender;
import net.icsl.ledar.web.util.ApplicationUtility;
import net.icsl.ledar.web.util.FacesSupportUtil;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author sfagade
 * @created Apr 18, 2016
 */
@Named(value = "complaintsBean")
@SessionScoped
public class ComplaintsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private LcdaWardsDataServices lcdaWardService;
    @Inject
    private ReferenceDataService refDataService;
    @Inject
    private ComplaintDataService compliService;
    @Inject
    private LoginBean loginBean;
    @Inject
    private EmailSender emailSender;

    private final FacesContext context = FacesContext.getCurrentInstance();
    private final DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private List<ComlaintDocuments> compntDocuments;
    private List<ComplaintDetails> compntDetails;
    private List<PersonTitles> personTitle;
    private List<LocalCouncilDevArea> lcdArea;
    private List<LcdaWards> lcdaWards;
    private List<DocumentTypes> docTypes;
    private List<ComplaintTypes> complaintType;
    private List<Visitors> visitorsList, foundVisitors;
    private List<VisitPurpose> purposeList;
    private List<ComplainerRelationship> relationshipList;

    private ComplaintDetails compntDetail;
    private ComplaintSources selectedComplaint;
    private PropertyComplaints propComplaint;
    private LocalCouncilDevArea selectedLcda;
    private LcdaWards selectedWard;
    private UploadedFile complaintFile, complaintFile2, complaintFile3, complaintFile4;
    private DocumentTypes selectedDocType, selectedDocType1, selectedDocType2, selectedDocType3;
    private Visitors selectedVisior, currentVisitor;
    private VisitPurpose selectedPurpose;
    private ComplainerRelationship selectedRelationship;

    private Properties prop;

    private String ownerType, filledQuestionair, propertyId, propertyAddress, visitorName, visitorPhone, selectedDay;
    private String descrip1, descrip2, descrip3, descrip4;
    private Date visit_date, end_date;
    private long recordCount;

    /**
     * Creates a new instance of ComplaintsBean
     */
    public ComplaintsBean() {
        if (prop == null) {
            emailSender = new EmailSender(loginBean.getPerson().getOrganization().getId());
        }
        prop = emailSender.getProperties();
    }

    @PostConstruct
    public void init() {
        String view_id = FacesContext.getCurrentInstance().getViewRoot().getViewId();

        if (view_id.equals("/app/complaints/createComplaints.xhtml")) {

        }

    }

    public void filterVisitorsResult() {

        if (selectedDay != null && !selectedDay.isEmpty() && !selectedDay.equalsIgnoreCase("SELECT DATE RANGE")) {
            Calendar calendar = Calendar.getInstance();

            Map<String, Date> dateMap = FacesSupportUtil.fetchAndSetStartAndEndDate(selectedDay);
            visit_date = dateMap.get("start");
            end_date = dateMap.get("end");

        } else if (FacesSupportUtil.setDateRangeCustomError(selectedDay != null, selectedDay.equalsIgnoreCase("SELECT DATE RANGE"), visit_date == null, end_date == null))
            return;

        visitorsList = compliService.filterVisitorsList(propertyId, propertyAddress, visitorName, visitorPhone, ((selectedPurpose != null) ? selectedPurpose.getId() : null), ((selectedRelationship != null) ? selectedRelationship.getId() : null), visit_date, end_date, ((selectedWard != null) ? selectedWard.getId() : null), ((selectedLcda != null) ? selectedLcda.getId() : null));
        if (visitorsList != null) {
            recordCount = visitorsList.size();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Criteria did not return any results", ""));
        }
    }

    public void resetVisitorsList() {

    }

    public List<Visitors> runVisitorAutoComplete(String query) {
        Calendar cal = Calendar.getInstance();
        String dateValue = cal.get(Calendar.YEAR) + "-" + (Calendar.MONTH + 2) + "-" + cal.get(Calendar.DAY_OF_MONTH);
        List<Visitors> foundVisitors = null;

        try {
            foundVisitors = compliService.fetchVisitorByNameAndDate(shortFormat.parse(dateValue), Boolean.TRUE, query);
        } catch (ParseException ex) {
            Logger.getLogger(ComplaintsBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return foundVisitors;
    }

    public List<LcdaWards> runDistrictAutoComplete(String query) {
        Long consultant = (loginBean.getPerson().getOrganization() != null) ? loginBean.getPerson().getOrganization().getId() : null;
        List<LcdaWards> lcdaDistrict = lcdaWardService.fetchAllLcdaWardsByName(query, consultant, true);

        return lcdaDistrict;
    }

    public void saveNewVisitorInfo() {
        if ((selectedVisior != null) && (!selectedVisior.getComplainerRelationship().getRelationshipName().equals("OWNER"))) {
            if ((selectedVisior.getOwnerFirstName() == null) || selectedVisior.getOwnerLastName() == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide Property Owner information!", "Fail"));
            }
        }

        selectedVisior.setReceptionistId(loginBean.getPerson().getLogindetailId());
        selectedVisior.setConsultant(loginBean.getPerson().getOrganization());
        selectedVisior.setTimeIn(new Date());

        selectedVisior = compliService.saveNewVisitorInformation(selectedVisior, null, Boolean.TRUE);
        if (selectedVisior != null) {
            selectedVisior = new Visitors();
            visitorsList = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Visitor information saved successfully!", "Success"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save visitor information, please try again later!", "Fail"));
        }
    }

    public void initNewVistorsForm() {
        if (purposeList == null) {
            purposeList = refDataService.fetchAllVisitPurposes(null);
        }
        if (selectedVisior == null) {
            selectedVisior = new Visitors();
        }
        if (relationshipList == null) {
            relationshipList = refDataService.fetchAllRelationships(null);
        }
    }

    public void initVistorsView() {
        if (visitorsList == null || visitorsList.size() <= 0) {
            Calendar cal = Calendar.getInstance();
            int day_ = Calendar.MONTH;
            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.DAY_OF_MONTH, +1);
            Logger.getLogger(ComplaintsBean.class.getName()).log(Level.SEVERE, "Month is: {0} -- {1}", new Object[]{cal.get(Calendar.MONTH), cal.get(Calendar.MONTH)});
            String dateValue = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);

            if (purposeList == null) {
                purposeList = refDataService.fetchAllVisitPurposes(null);
            }
            if (relationshipList == null) {
                relationshipList = refDataService.fetchAllRelationships(null);
            }

            try {
                visit_date = shortFormat.parse(FacesSupportUtil.getTodayWithoutTime());
                end_date = shortFormat.parse(dateValue);
                visitorsList = compliService.fetchAllVisitorsInformation(visit_date, end_date, loginBean.getPerson().getOrganization().getId());
                recordCount = visitorsList.size();

                dateValue = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + day_;
                foundVisitors = compliService.fetchVisitorByNameAndDate(shortFormat.parse(dateValue), Boolean.TRUE, null);

            } catch (ParseException ex) {
                Logger.getLogger(ComplaintsBean.class.getName()).log(Level.SEVERE, "Date conversion failed", ex);
            }

            selectedLcda = null;
            lcdaWards = null;
        }
    }

    public void initNewComplaintForm() {
        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "CUSTOMER SERVICE OFFICER"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            if (propComplaint == null) {
                propComplaint = new PropertyComplaints();
                compntDocuments = new ArrayList<>(1);
                compntDetail = new ComplaintDetails();
            }

            if (personTitle == null) {
                personTitle = refDataService.fetchAllTittle();
            }

            if (complaintType == null) {
                complaintType = refDataService.fetchAllComplaintTypes();
            }

            if (docTypes == null) {
                docTypes = refDataService.fetchAllDocumentTypes();
            }
            if (relationshipList == null) {
                relationshipList = refDataService.fetchAllRelationships(null);
            }

        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/app/index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UploadManagerBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void saveNewComplaint() {

        File upload_folder, download_folder;
        String download_dir = prop.getProperty("bills.folder") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/complaints/" + selectedLcda.getLcdaName();
        String file_name, orginal_name;

        download_folder = new File(download_dir);

        if (!download_folder.exists()) {
            download_folder.mkdirs(); //create folder
        }

        compntDocuments = new ArrayList<>();

        try {
            if (complaintFile != null && selectedDocType != null) {
                orginal_name = complaintFile.getFileName();
                file_name = ApplicationUtility.renameImageFile(complaintFile.getFileName(), propComplaint.getPropertyId());
                upload_folder = new File(download_dir + "/" + file_name);

                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(upload_folder))) {
                    stream.write(complaintFile.getContents());

                    compntDocuments.add(new ComlaintDocuments(null, orginal_name, descrip1, new FileUploads(null, null, complaintFile.getFileName(), complaintFile.getContentType(), "top",
                            download_dir, loginBean.getPerson().getLogindetailId(), download_dir, null, null), propComplaint, selectedDocType));
                }
            }

            if (complaintFile2 != null && selectedDocType1 != null) {
                orginal_name = complaintFile2.getFileName();
                file_name = ApplicationUtility.renameImageFile(complaintFile2.getFileName(), propComplaint.getPropertyId());
                upload_folder = new File(download_dir + "/" + file_name);

                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(upload_folder))) {
                    stream.write(complaintFile2.getContents());

                    compntDocuments.add(new ComlaintDocuments(null, orginal_name, descrip2, new FileUploads(null, null, complaintFile2.getFileName(), complaintFile2.getContentType(), "top",
                            download_dir, loginBean.getPerson().getLogindetailId(), download_dir, null, null), propComplaint, selectedDocType1));
                }
            }

            if (complaintFile3 != null && selectedDocType2 != null) {
                orginal_name = complaintFile3.getFileName();
                file_name = ApplicationUtility.renameImageFile(complaintFile3.getFileName(), propComplaint.getPropertyId());
                upload_folder = new File(download_dir + "/" + file_name);

                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(upload_folder))) {
                    stream.write(complaintFile3.getContents());

                    compntDocuments.add(new ComlaintDocuments(null, orginal_name, descrip3, new FileUploads(null, null, complaintFile3.getFileName(), complaintFile3.getContentType(), "top",
                            download_dir, loginBean.getPerson().getLogindetailId(), download_dir, null, null), propComplaint, selectedDocType2));
                }
            }

            if (complaintFile4 != null && selectedDocType3 != null) {
                orginal_name = complaintFile4.getFileName();
                file_name = ApplicationUtility.renameImageFile(complaintFile4.getFileName(), propComplaint.getPropertyId());
                upload_folder = new File(download_dir + "/" + file_name);

                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(upload_folder))) {
                    stream.write(complaintFile4.getContents());

                    compntDocuments.add(new ComlaintDocuments(null, orginal_name, descrip4, new FileUploads(null, null, complaintFile4.getFileName(), complaintFile4.getContentType(), "top",
                            download_dir, loginBean.getPerson().getLogindetailId(), download_dir, null, null), propComplaint, selectedDocType3));
                }
            }

            compntDetail.setPropertyComplaintId(propComplaint);
            if (compntDetail.getComplaintUnitId() != null) {
                compntDetail.setComplaintStatusId(refDataService.findComplaintStatusById(2L)); //complaint has been assigned to unit, status set is Assigned
            } else {
                compntDetail.setComplaintStatusId(refDataService.findComplaintStatusById(1L)); //complaint hasn't been assigned to unit, status set is Pending	
            }
            propComplaint.setComplaintNumber(generateUserUniqueNumber("Complaints"));
            propComplaint.setCreatedById(loginBean.getPerson().getLogindetailId());
            propComplaint.setOrganization(loginBean.getPerson().getOrganization());
            propComplaint.setLocalCouncilDevAreaId(selectedLcda);
            propComplaint.setLcdaWardId(selectedWard);

            if (compliService.saveNewComplaintInformation(propComplaint, compntDetail, compntDocuments, null) != null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Complaints has been saved successfully!", "Success"));
                propComplaint = null;
                compntDetail = null;
                compntDocuments = null;
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save new Complaint try again!", "Error"));
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ComplaintsBean.class.getName()).log(Level.SEVERE, "Failed to Upload File", ex);
        } catch (IOException ex) {
            Logger.getLogger(ComplaintsBean.class.getName()).log(Level.SEVERE, "Failed to Upload File", ex);

        }
    }

    public String generateUserUniqueNumber(String key_type) {

        long last_number = 0;

        UniqueUserIdentifications uniqueId = refDataService.fetchLastUniqueKey(key_type);

        if (key_type.equalsIgnoreCase("Complaints")) {
            last_number = uniqueId.getLastComplaintNumber() + 1;
            uniqueId.setLastComplaintNumber(last_number);
        }

        refDataService.updateLastuniqueKey(uniqueId);

        return last_number + "";
    }

    public void changedLcda() {
        //System.out.println("called changeLcda: " + selected_lcda);
        if (selectedLcda != null) {
            this.setLcdaWards(lcdaWardService.fetchAllLcdaWardsByLcda(selectedLcda.getId(), null));
        }
    }

    /**
     * @return the selectedComplaint
     */
    public ComplaintSources getSelectedComplaint() {
        return selectedComplaint;
    }

    /**
     * @param selectedComplaint the selectedComplaint to set
     */
    public void setSelectedComplaint(ComplaintSources selectedComplaint) {
        this.selectedComplaint = selectedComplaint;
    }

    /**
     * @return the propComplaint
     */
    public PropertyComplaints getPropComplaint() {
        return propComplaint;
    }

    /**
     * @param propComplaint the propComplaint to set
     */
    public void setPropComplaint(PropertyComplaints propComplaint) {
        this.propComplaint = propComplaint;
    }

    /**
     * @return the ownerType
     */
    public String getOwnerType() {
        return ownerType;
    }

    /**
     * @param ownerType the ownerType to set
     */
    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    /**
     * @return the filledQuestionair
     */
    public String getFilledQuestionair() {
        return filledQuestionair;
    }

    /**
     * @param filledQuestionair the filledQuestionair to set
     */
    public void setFilledQuestionair(String filledQuestionair) {
        this.filledQuestionair = filledQuestionair;
    }

    /**
     * @return the compntDocuments
     */
    public List<ComlaintDocuments> getCompntDocuments() {
        return compntDocuments;
    }

    /**
     * @param compntDocuments the compntDocuments to set
     */
    public void setCompntDocuments(List<ComlaintDocuments> compntDocuments) {
        this.compntDocuments = compntDocuments;
    }

    /**
     * @return the complaintFile
     */
    public UploadedFile getComplaintFile() {
        return complaintFile;
    }

    /**
     * @param complaintFile the complaintFile to set
     */
    public void setComplaintFile(UploadedFile complaintFile) {
        this.complaintFile = complaintFile;
    }

    public List<PersonTitles> getPersonTitle() {
        return personTitle;
    }

    public void setPersonTitle(List<PersonTitles> personTitle) {
        this.personTitle = personTitle;
    }

    public List<ComplaintDetails> getCompntDetails() {
        return compntDetails;
    }

    public void setCompntDetails(List<ComplaintDetails> compntDetails) {
        this.compntDetails = compntDetails;
    }

    public UploadedFile getComplaintFile2() {
        return complaintFile2;
    }

    public void setComplaintFile2(UploadedFile complaintFile2) {
        this.complaintFile2 = complaintFile2;
    }

    public UploadedFile getComplaintFile3() {
        return complaintFile3;
    }

    public void setComplaintFile3(UploadedFile complaintFile3) {
        this.complaintFile3 = complaintFile3;
    }

    public UploadedFile getComplaintFile4() {
        return complaintFile4;
    }

    public void setComplaintFile4(UploadedFile complaintFile4) {
        this.complaintFile4 = complaintFile4;
    }

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

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public List<LcdaWards> getLcdaWards() {
        return lcdaWards;
    }

    public void setLcdaWards(List<LcdaWards> lcdaWards) {
        this.lcdaWards = lcdaWards;
    }

    public LocalCouncilDevArea getSelectedLcda() {
        return selectedLcda;
    }

    public void setSelectedLcda(LocalCouncilDevArea selectedLcda) {
        this.selectedLcda = selectedLcda;
    }

    public LcdaWards getSelectedWard() {
        return selectedWard;
    }

    public void setSelectedWard(LcdaWards selectedWard) {
        this.selectedWard = selectedWard;
    }

    /**
     * @return the selectedDocType
     */
    public DocumentTypes getSelectedDocType() {
        return selectedDocType;
    }

    /**
     * @param selectedDocType the selectedDocType to set
     */
    public void setSelectedDocType(DocumentTypes selectedDocType) {
        this.selectedDocType = selectedDocType;
    }

    /**
     * @return the selectedDocType1
     */
    public DocumentTypes getSelectedDocType1() {
        return selectedDocType1;
    }

    /**
     * @param selectedDocType1 the selectedDocType1 to set
     */
    public void setSelectedDocType1(DocumentTypes selectedDocType1) {
        this.selectedDocType1 = selectedDocType1;
    }

    /**
     * @return the selectedDocType2
     */
    public DocumentTypes getSelectedDocType2() {
        return selectedDocType2;
    }

    /**
     * @param selectedDocType2 the selectedDocType2 to set
     */
    public void setSelectedDocType2(DocumentTypes selectedDocType2) {
        this.selectedDocType2 = selectedDocType2;
    }

    /**
     * @return the selectedDocType3
     */
    public DocumentTypes getSelectedDocType3() {
        return selectedDocType3;
    }

    /**
     * @param selectedDocType3 the selectedDocType3 to set
     */
    public void setSelectedDocType3(DocumentTypes selectedDocType3) {
        this.selectedDocType3 = selectedDocType3;
    }

    /**
     * @return the docTypes
     */
    public List<DocumentTypes> getDocTypes() {
        return docTypes;
    }

    /**
     * @param docTypes the docTypes to set
     */
    public void setDocTypes(List<DocumentTypes> docTypes) {
        this.docTypes = docTypes;
    }

    /**
     * @return the descrip1
     */
    public String getDescrip1() {
        return descrip1;
    }

    /**
     * @param descrip1 the descrip1 to set
     */
    public void setDescrip1(String descrip1) {
        this.descrip1 = descrip1;
    }

    /**
     * @return the descrip2
     */
    public String getDescrip2() {
        return descrip2;
    }

    /**
     * @param descrip2 the descrip2 to set
     */
    public void setDescrip2(String descrip2) {
        this.descrip2 = descrip2;
    }

    /**
     * @return the descrip3
     */
    public String getDescrip3() {
        return descrip3;
    }

    /**
     * @param descrip3 the descrip3 to set
     */
    public void setDescrip3(String descrip3) {
        this.descrip3 = descrip3;
    }

    /**
     * @return the descrip4
     */
    public String getDescrip4() {
        return descrip4;
    }

    /**
     * @param descrip4 the descrip4 to set
     */
    public void setDescrip4(String descrip4) {
        this.descrip4 = descrip4;
    }

    /**
     * @return the compntDetail
     */
    public ComplaintDetails getCompntDetail() {
        return compntDetail;
    }

    /**
     * @param compntDetail the compntDetail to set
     */
    public void setCompntDetail(ComplaintDetails compntDetail) {
        this.compntDetail = compntDetail;
    }

    /**
     * @return the complaintType
     */
    public List<ComplaintTypes> getComplaintType() {
        return complaintType;
    }

    /**
     * @param complaintType the complaintType to set
     */
    public void setComplaintType(List<ComplaintTypes> complaintType) {
        this.complaintType = complaintType;
    }

    /**
     * @return the visitorsList
     */
    public List<Visitors> getVisitorsList() {
        return visitorsList;
    }

    /**
     * @param visitorsList the visitorsList to set
     */
    public void setVisitorsList(List<Visitors> visitorsList) {
        this.visitorsList = visitorsList;
    }

    /**
     * @return the visit_date
     */
    public Date getVisit_date() {
        return visit_date;
    }

    /**
     * @param visit_date the visit_date to set
     */
    public void setVisit_date(Date visit_date) {
        this.visit_date = visit_date;
    }

    /**
     * @return the end_date
     */
    public Date getEnd_date() {
        return end_date;
    }

    /**
     * @param end_date the end_date to set
     */
    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    /**
     * @return the selectedVisior
     */
    public Visitors getSelectedVisior() {
        return selectedVisior;
    }

    /**
     * @param selectedVisior the selectedVisior to set
     */
    public void setSelectedVisior(Visitors selectedVisior) {
        this.selectedVisior = selectedVisior;
    }

    /**
     * @return the purposeList
     */
    public List<VisitPurpose> getPurposeList() {
        return purposeList;
    }

    /**
     * @param purposeList the purposeList to set
     */
    public void setPurposeList(List<VisitPurpose> purposeList) {
        this.purposeList = purposeList;
    }

    /**
     * @return the relationshipList
     */
    public List<ComplainerRelationship> getRelationshipList() {
        return relationshipList;
    }

    /**
     * @param relationshipList the relationshipList to set
     */
    public void setRelationshipList(List<ComplainerRelationship> relationshipList) {
        this.relationshipList = relationshipList;
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
     * @return the currentVisitor
     */
    public Visitors getCurrentVisitor() {
        return currentVisitor;
    }

    /**
     * @param currentVisitor the currentVisitor to set
     */
    public void setCurrentVisitor(Visitors currentVisitor) {
        this.currentVisitor = currentVisitor;
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
     * @return the propertyAddress
     */
    public String getPropertyAddress() {
        return propertyAddress;
    }

    /**
     * @param propertyAddress the propertyAddress to set
     */
    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    /**
     * @return the visitorName
     */
    public String getVisitorName() {
        return visitorName;
    }

    /**
     * @param visitorName the visitorName to set
     */
    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    /**
     * @return the visitorPhone
     */
    public String getVisitorPhone() {
        return visitorPhone;
    }

    /**
     * @param visitorPhone the visitorPhone to set
     */
    public void setVisitorPhone(String visitorPhone) {
        this.visitorPhone = visitorPhone;
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
     * @return the selectedPurpose
     */
    public VisitPurpose getSelectedPurpose() {
        return selectedPurpose;
    }

    /**
     * @param selectedPurpose the selectedPurpose to set
     */
    public void setSelectedPurpose(VisitPurpose selectedPurpose) {
        this.selectedPurpose = selectedPurpose;
    }

    /**
     * @return the selectedRelationship
     */
    public ComplainerRelationship getSelectedRelationship() {
        return selectedRelationship;
    }

    /**
     * @param selectedRelationship the selectedRelationship to set
     */
    public void setSelectedRelationship(ComplainerRelationship selectedRelationship) {
        this.selectedRelationship = selectedRelationship;
    }

    public List<Visitors> getFoundVisitors() {
        return foundVisitors;
    }

    public void setFoundVisitors(List<Visitors> foundVisitors) {
        this.foundVisitors = foundVisitors;
    }

}
