package net.icsl.ledar.web.bean;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.icsl.ledar.ejb.model.CommercialTypes;
import net.icsl.ledar.ejb.model.Occupations;
import net.icsl.ledar.ejb.model.PersonTitles;
import net.icsl.ledar.ejb.model.PropertyClassifications;
import net.icsl.ledar.ejb.model.ResidentialTypes;
import net.icsl.ledar.ejb.model.ResidentialUseages;
import net.icsl.ledar.ejb.model.ServiceTypes;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.ReferenceDataService;
import net.icsl.ledar.web.util.ApplicationUtility;

import org.primefaces.json.JSONObject;

/**
 *
 * @author sfagade
 * @author May 4, 2017
 */
@Named(value = "referenceBean")
@SessionScoped
public class ReferenceDataBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    ReferenceDataService refDataService;
    @Inject
    private LcdaWardsDataServices lcdaService;

    @Inject
    private LoginBean loginBean;
    @Inject
    private FacesContext facesContext;

    private List<CommercialTypes> commercialTypes;
    private List<Occupations> occupations;
    private List<PropertyClassifications> propertyUsages;
    private List<ResidentialTypes> residentialTypes;
    private List<ResidentialUseages> residentialUses;
    private List<ServiceTypes> serviceTypes;
    private List<PersonTitles> personTitles;

    private CommercialTypes commercialType;
    private Occupations occupation;
    private PropertyClassifications propertyUsage;
    private ResidentialTypes residentialType;
    private ResidentialUseages residentialUse;
    private ServiceTypes serviceType;
    private PersonTitles personTitle;

    private long recordCount;

    /**
     * Creates a new instance of ReferenceDataBean
     */
    public ReferenceDataBean() {
    }

    @PostConstruct
    public void init() {

    }

    public void saveNewPersonTitle() {
        if ((personTitle.getTitleName() != null) && (!personTitle.getTitleName().isEmpty())) {
            personTitle = lcdaService.createNewPersonTitles(personTitle, null);
            if (personTitle != null) {
                JSONObject sevitypeJson = new JSONObject(personTitle);
                if (personTitles != null && personTitles.size() > 0) {
                    personTitles.add(personTitle);
                }
                sevitypeJson.put("action", "create");

                JSONObject msg_json = new JSONObject();
                msg_json.put("to", "/topics/titles");
                msg_json.put("data", sevitypeJson.toString());
//                ApplicationUtility.pushToGoogleApi(msg_json.toString());
                String api_call = ApplicationUtility.pushMessageToDevices(msg_json);
                String message = "New Person Title saved successfully!";
                if (api_call.equalsIgnoreCase("success")) {
                    message += " And new entry pushed to Devices";
                } else {
                    message += " But could not push new entry to devices, you'll have to push that later";
                }

                personTitle = new PersonTitles();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, "Success"));
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save new Person Title, please try again later!", "Error"));
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Person Title name is required!", "Error"));
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void initNewPersonTitle() {
        if (personTitle == null) {
            personTitle = new PersonTitles();
        }
    }

    public void initPersonTitle() {
        if (personTitles == null) {
            personTitles = refDataService.fetchAllTittle();
            recordCount = personTitles.size();
        }
    }

    public void saveNewserviceType() {
        if ((serviceType.getServiceTypeName() != null) && (!serviceType.getServiceTypeName().isEmpty())) {
            serviceType = lcdaService.createNewServiceTypes(serviceType, null);
            if (serviceType != null) {
                JSONObject sevitypeJson = new JSONObject(serviceType);
                serviceTypes.add(serviceType);
                serviceType = new ServiceTypes();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New Service Type saved successfully!", "Success"));
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save new Service Type, please try again later!", "Error"));
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Service Type name is required!", "Error"));
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void initNewServicetype() {
        if (serviceType == null) {
            serviceType = new ServiceTypes();
        }
    }

    public void initServiceTypes() {
        if (serviceTypes == null) {
            serviceTypes = refDataService.fetchAllServiceTypes();
            recordCount = serviceTypes.size();
        }
    }

    public void saveNewResidentialUse() {
        if ((residentialUse.getResidentialUseName() != null) && (!residentialUse.getResidentialUseName().isEmpty())) {
            residentialUse = lcdaService.createNewResidentialUsage(residentialUse, null);
            if (residentialUse != null) {
                JSONObject restiUse = new JSONObject(residentialUse);
                residentialUses.add(residentialUse);
                residentialUse = new ResidentialUseages();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Residential Usage saved successfully!", "Success"));
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save Residential Usage, please try again later!", "Error"));
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Residential Use name is required!", "Error"));
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void initNewResidentialUse() {
        if (residentialUse == null) {
            residentialUse = new ResidentialUseages();
        }
    }

    public void initResidentialUse() {
        if (residentialUses == null) {
            residentialUses = refDataService.fetchAllResidentialUseages();
            recordCount = residentialUses.size();
        }
    }

    public void saveNewResidentialType() {
        if ((residentialType.getResidentialTypeName() != null) && (!residentialType.getResidentialTypeName().isEmpty())) {
            residentialType = lcdaService.createNewResidentialTypes(residentialType, null);
            if (residentialType != null) {
                JSONObject restiType = new JSONObject(residentialType);
                residentialType = new ResidentialTypes();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Residential Type saved successfully!", "Success"));
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save Residential Type, please try again later!", "Error"));
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Residential Types name is required!", "Error"));
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void initNewPropertyUsage() {
        if (residentialType == null) {
            residentialType = new ResidentialTypes();
        }
    }

    public void initResidentialTypes() {
        if (residentialTypes == null) {
            residentialTypes = refDataService.fetchAllResidentialTypes();
            recordCount = residentialTypes.size();
        }
    }

    public void initPropertyUsages() {
        if (propertyUsages == null || propertyUsages.size() <= 0) {
            propertyUsages = refDataService.fetchAllPropertyUse();
            recordCount = propertyUsages.size();
        }
    }

    public void initNewOccupation() {
        if (occupation == null) {
            occupation = new Occupations();
        }
    }

    public void initOccupation() {
        if (occupations == null) {
            occupations = refDataService.fetchAllOccupations();
            recordCount = occupations.size();
        }
    }

    public void saveNewOccupation() {
        if (occupation.getOccupationName() != null && !occupation.getOccupationName().isEmpty()) {
            occupation = lcdaService.createNewOccupations(occupation, null);
            if (occupation != null) {

                JSONObject occptn = new JSONObject(occupation);
                occupation = new Occupations();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Occupation saved successfully!", "Success"));
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save Occupation, please try again later!", "Error"));
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Occupation name is required!", "Error"));
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void saveNewCommercialType() {
        if (commercialType.getCommercialTypeName() != null && !commercialType.getCommercialTypeName().isEmpty()) {
            commercialType = lcdaService.createNewCommercialType(commercialType, null);
            if (commercialType != null) {
                //TODO I need to send push to device now
                JSONObject commercial = new JSONObject(commercialType);
                commercialType = new CommercialTypes();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Commercial Type saved successfully", ""));
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to save Commercial type, please try again later!", "Error"));
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Commercial type name is required!", "Error"));
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void initNewCommercialType() {
        if (commercialType == null) {
            commercialType = new CommercialTypes(null);
        }
    }

    public void initCommercialType() {
        if (commercialTypes == null || commercialTypes.size() > 0) {
            commercialTypes = refDataService.fetchAllcommercialTypes();
            recordCount = commercialTypes.size();
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
     * @return the commercialTypes
     */
    public List<CommercialTypes> getCommercialTypes() {
        return commercialTypes;
    }

    /**
     * @param commercialTypes the commercialTypes to set
     */
    public void setCommercialTypes(List<CommercialTypes> commercialTypes) {
        this.commercialTypes = commercialTypes;
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
     * @return the commercialType
     */
    public CommercialTypes getCommercialType() {
        return commercialType;
    }

    /**
     * @param commercialType the commercialType to set
     */
    public void setCommercialType(CommercialTypes commercialType) {
        this.commercialType = commercialType;
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

    /**
     * @return the occupation
     */
    public Occupations getOccupation() {
        return occupation;
    }

    /**
     * @param occupation the occupation to set
     */
    public void setOccupation(Occupations occupation) {
        this.occupation = occupation;
    }

    /**
     * @return the propertyUsages
     */
    public List<PropertyClassifications> getPropertyUsages() {
        return propertyUsages;
    }

    /**
     * @param propertyUsages the propertyUsages to set
     */
    public void setPropertyUsages(List<PropertyClassifications> propertyUsages) {
        this.propertyUsages = propertyUsages;
    }

    /**
     * @return the propertyUsage
     */
    public PropertyClassifications getPropertyUsage() {
        return propertyUsage;
    }

    /**
     * @param propertyUsage the propertyUsage to set
     */
    public void setPropertyUsage(PropertyClassifications propertyUsage) {
        this.propertyUsage = propertyUsage;
    }

    public List<ResidentialTypes> getResidentialTypes() {
        return residentialTypes;
    }

    public void setResidentialTypes(List<ResidentialTypes> residentialTypes) {
        this.residentialTypes = residentialTypes;
    }

    public ResidentialTypes getResidentialType() {
        return residentialType;
    }

    public void setResidentialType(ResidentialTypes residentialType) {
        this.residentialType = residentialType;
    }

    public List<ResidentialUseages> getResidentialUses() {
        return residentialUses;
    }

    public void setResidentialUses(List<ResidentialUseages> residentialUses) {
        this.residentialUses = residentialUses;
    }

    public ResidentialUseages getResidentialUse() {
        return residentialUse;
    }

    public void setResidentialUse(ResidentialUseages residentialUse) {
        this.residentialUse = residentialUse;
    }

    public List<ServiceTypes> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(List<ServiceTypes> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    public ServiceTypes getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceTypes serviceType) {
        this.serviceType = serviceType;
    }

    public List<PersonTitles> getPersonTitles() {
        return personTitles;
    }

    public void setPersonTitles(List<PersonTitles> personTitles) {
        this.personTitles = personTitles;
    }

    public PersonTitles getPersonTitle() {
        return personTitle;
    }

    public void setPersonTitle(PersonTitles personTitle) {
        this.personTitle = personTitle;
    }

}
