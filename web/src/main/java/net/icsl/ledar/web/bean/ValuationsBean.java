package net.icsl.ledar.web.bean;

import javax.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.mail.MessagingException;

import org.primefaces.model.LazyDataModel;

import net.icsl.ledar.ejb.enums.ApplicationEntityNames;
import net.icsl.ledar.ejb.enums.UserActitiyName;
import net.icsl.ledar.ejb.enums.ValuationStatusEnum;
import net.icsl.ledar.ejb.model.Notifications;

import net.icsl.ledar.ejb.model.PropertyServices;
import net.icsl.ledar.ejb.model.PropertyValuations;
import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.model.WardLandProperties;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.NotificationDataService;
import net.icsl.ledar.ejb.util.EmailSender;
import net.icsl.ledar.web.lazyModel.LandPropertiesLazy;
import net.icsl.ledar.web.util.FacesSupportUtil;

/**
 *
 * @author sfagade
 * @created May 5, 2016
 */
@Named(value = "valuationsBean")
@SessionScoped
public class ValuationsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private FacesContext facesContext;

    @Inject
    private LcdaWardsDataServices lcdaService;
    @Inject
    private LandPropertiesDataService landPropService;
    @Inject
    private NotificationDataService noteService;

    @Inject
    private LoginBean loginBean;

    private List<WardLandProperties> wardProperties;
    private LazyDataModel<WardLandProperties> lazyProperties;
    private List<PropertyServices> propServices;

    private WardLandProperties wardProperty;
    private PropertyValuations propValuation;
    private Notifications notification;

    private long recordCount;
    private String formRemarks, propertyId;

    /**
     * Creates a new instance of ValuationsBean
     */
    public ValuationsBean() {
    }

    @PostConstruct
    public void init() {
        //String view_id = facesContext.getViewRoot().getViewId();
        //String prop_id = facesContext.getExternalContext().getRequestParameterMap().get("propId");

        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "VALUATION OFFICER"};
        if (!FacesSupportUtil.isUserInRole(accepted_roles)) {

            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/app/index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UpdateManagerBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void updateValuationStatus() {

        FacesContext context = FacesContext.getCurrentInstance();
        Boolean all_fine;

        String message = "Property sent for Valuation has been " + wardProperty.getPropertyValuationStatus() + ". ";
        String full_message = message + "You can view the full details of the property by <a href=\"http://" + FacesContext.getCurrentInstance().getExternalContext().getRequestServerName() + ":"
                + FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort() + FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/app/properties"
                + "/viewPropertyDetails.xhtml?propId=" + wardProperty.getId() + "\">clicking here</a>.";

        String notific_msg = message += " <a href=\"http://" + FacesContext.getCurrentInstance().getExternalContext().getRequestServerName() + ":"
                + FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort() + FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/app/properties"
                + "/viewPropertyDetails.xhtml?propId=" + wardProperty.getId() + "\">Click here to view details.</a>";

        UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.UPDATEDPROPERTYSTATUS.toString(), new Date(), loginBean.getPerson().getFullName() + " has updated property"
                + " valuation status to " + wardProperty.getPropertyValuationStatus() + ", property ID is: " + wardProperty.getId(),
                loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, FacesSupportUtil.getClientAgent(), loginBean.getPerson().getLogindetailId(), null, null);

        activity.setEntityName(ApplicationEntityNames.WARDLANDPROPERTIES.toString());
        activity.setEntityId(wardProperty.getId());

        notification = new Notifications(null, noteService.findNotifiTypeByName("FYI"), noteService.findNotiStatusByName("Approved"), notific_msg, wardProperty.getId(), null, null);
        notification.setLogindetailId(wardProperty.getVerifiedById());
        notification.setFullMessage(full_message);
        notification.setCreatedById(loginBean.getPerson().getLogindetailId());

        if (wardProperty.getPropertyValuationStatus().equalsIgnoreCase(ValuationStatusEnum.PROCESSED.name())) {
            if ((propValuation.getAccessedValue() == null) || propValuation.getLucCharge() == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Accessed Value or LUC Charge not set!", "Error"));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return;
            } else {
                full_message += "<br />Remarks attached to the Property is below:<br /><br />" + formRemarks;

                propValuation.setWardLandPropertyId(wardProperty);
                propValuation.setValuationDate(new Date());
                propValuation.setAccessedById(loginBean.getPerson().getLogindetailId());
                propValuation.setValuationRemarks(formRemarks);
                wardProperty.setLastUpdatedById(loginBean.getPerson().getLogindetailId());
                wardProperty.setPropertyValuationStatus(ValuationStatusEnum.PROCESSED.toString());

                propValuation = landPropService.saveUpdatePropertyValuationInfo(propValuation, notification, activity);
                all_fine = (propValuation != null);
            }
        } else if (wardProperty.getPropertyValuationStatus().equalsIgnoreCase(ValuationStatusEnum.RETURNED.name())) {
            if (formRemarks == null || formRemarks.isEmpty()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Remarks required for cases where Valuation is not processed!", "Error"));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return;
            } else {
                notification.setNotificationStatusId(noteService.findNotiStatusByName("Pending Action"));
                notification.setNotificationTypeId(noteService.findNotifiTypeByName("PENDING PROPERTY ACTION"));
                wardProperty.setIsVerified(false);
                wardProperty.setPropertyValuationStatus(ValuationStatusEnum.RETURNED.toString());
                all_fine = landPropService.updateWardLandPropertyInfo(wardProperty, null, activity);

                notification = noteService.createNewNotification(notification, null);
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You need to change the status of the Property before sending it!", "Error"));
            context.getExternalContext().getFlash().setKeepMessages(true);
            return;

        }

        if (all_fine != null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Property Valuation has been sent successfully: ", "Success"));
            context.getExternalContext().getFlash().setKeepMessages(true);

            String[] email_addresses = {wardProperty.getVerifiedById().getPerson().getContactInformationsList().get(0).getOfficeEmailAddress()};
            try {
                new EmailSender().sendPlainEmailMessage(full_message, message, email_addresses);
            } catch (MessagingException ex) {
                Logger.getLogger(ValuationsBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to push property information, please try again later", "Error"));
            context.getExternalContext().getFlash().setKeepMessages(true);
        }

    }

    public void initPropertyDetail() {

        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        setPropertyId(params.get("prpid"));

        if (getPropertyId() != null) {
            wardProperty = landPropService.findWardPropertyById(Long.valueOf(getPropertyId()));
            propValuation = new PropertyValuations(null);
            if (wardProperty.getPropertyTypes().equals("SERVICED")) {
                propServices = landPropService.fetchPropertyServicesByPropertyId(wardProperty.getId());
            }

            try {
                context.getExternalContext().redirect("viewPropertyDetails.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(ValuationsBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void initValuatedProperty() {
        String[] accepted_roles = {"ADMINISTRATOR", "VALUATION OFFICER"};
        if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            setWardProperties(lcdaService.fetchAllValuatedProperties(null));
            setRecordCount(getWardProperties().size());

        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have permission to access this resource", ""));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public void initPropertyList() {

        if (lazyProperties == null) {
            lazyProperties = new LandPropertiesLazy(null, lcdaService, true);

            recordCount = lcdaService.countPropertiesTotal(null, true, ValuationStatusEnum.SENT.toString());

        }

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

    public WardLandProperties getWardProperty() {
        return wardProperty;
    }

    public void setWardProperty(WardLandProperties wardProperty) {
        this.wardProperty = wardProperty;
    }

    public List<PropertyServices> getPropServices() {
        return propServices;
    }

    public void setPropServices(List<PropertyServices> propServices) {
        this.propServices = propServices;
    }

    public ValuationStatusEnum[] getValuationStatus() {
        return ValuationStatusEnum.values();
    }

    /**
     * @return the propValuation
     */
    public PropertyValuations getPropValuation() {
        return propValuation;
    }

    /**
     * @param propValuation the propValuation to set
     */
    public void setPropValuation(PropertyValuations propValuation) {
        this.propValuation = propValuation;
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
     * @return the notification
     */
    public Notifications getNotification() {
        return notification;
    }

    /**
     * @param notification the notification to set
     */
    public void setNotification(Notifications notification) {
        this.notification = notification;
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
}
