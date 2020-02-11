/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.bean;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.icsl.ledar.ejb.model.ContactInformations;
import net.icsl.ledar.ejb.model.Notifications;
import net.icsl.ledar.ejb.model.People;
import net.icsl.ledar.ejb.model.UserRoles;
import net.icsl.ledar.ejb.service.NotificationDataService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;

import org.apache.shiro.SecurityUtils;

/**
 *
 * @author sfagade
 */
@Named("notificBean")
@RequestScoped
public class NotificationsBean {

    @Inject
    private NotificationDataService noteService;
    /**
     * @EJB private EmployeeData empData;
     */
    @Inject
    private RegisteredUsersDataService service;
    /**
     * @Inject EmailSender emailSender;
     */

    @Inject
    private FacesContext facesContext;

    @ManagedProperty(value = "#{param.notid}")
    private String notificationId;
    @Inject
    private LoginBean loginBean;

    private Notifications notification;
    private People person;
    private ContactInformations personContact;

    private List<Notifications> myNotifications;

    /**
     * Creates a new instance of NotificationsBean
     */
    public NotificationsBean() {
    }

    @PostConstruct
    public void init() {

        String view_id = facesContext.getViewRoot().getViewId();

        if (notificationId != null) {
            setNotification(noteService.findNotificationById(new Long(notificationId)));

        }
    }

    public void initializeUpdate() {

        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        FacesMessage msg_;
        List<ContactInformations> contact_info; // = (List<ContactInformations>) this.getPerson_profile().getContactInformationsCollection();

        if (currentUser.hasRole("Human Resources and Admin")) {
            setNotification(noteService.findNotificationById(new Long(notificationId)));

            if ((getNotification() != null) && (getNotification().getNotificationTypeId().getNotificationTypeName().equals("Employee Update Approval"))) {
                setPerson(service.findById(getNotification().getEntityId()));

                if (getPerson() != null) {
                    contact_info = (List<ContactInformations>) this.getPerson().getContactInformationsList();
                    setPersonContact(contact_info.get(0));
                } else {
                    msg_ = new FacesMessage(FacesMessage.SEVERITY_ERROR, "We're sorry but we cannot find message details.", "");
                    facesContext.addMessage("dash_form:msgs", msg_);
                    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

                    try {
                        FacesContext.getCurrentInstance().getExternalContext().redirect("app/index.xhtml?faces-redirect=true");
                    } catch (IOException ex) {
                        Logger.getLogger(NotificationsBean.class.getName()).log(Level.SEVERE, "Cannot find home page", ex);
                    }
                }
            } else {
                msg_ = new FacesMessage(FacesMessage.SEVERITY_ERROR, "We're sorry but we cannot find your message", "");
                facesContext.addMessage("dash_form:msgs", msg_);
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("app/index.xhtml?faces-redirect=true");
                } catch (IOException ex) {
                    Logger.getLogger(NotificationsBean.class.getName()).log(Level.SEVERE, "Cannot find home page", ex);
                }
            }

        }

    }

    public List<Notifications> getMyNotifications() {

        People person_profile = loginBean.getPerson();

        List<UserRoles> uRoles_ = service.findUserRolesByUserId(person_profile.getLogindetailId().getId());

        String user_roles = uRoles_.get(0).getAuthenticationRoleId().getId().toString();

        for (int x = 1; x < uRoles_.size(); x++) {
            user_roles += "," + uRoles_.get(x).getAuthenticationRoleId().getId();
        }

        //NOTICE this line might break the day we have a user that has more than 1 role
        myNotifications = noteService.findUserNotifications(person_profile.getLogindetailId().getId(), user_roles, 10);

        return myNotifications;
    }

    public String removeNotification(String notific_id) {

        FacesMessage msg_;

        if (notific_id != null) { //this should always be the case
            setNotification(noteService.findNotificationById(new Long(notific_id))); //fetch the notification in ?

            if (getNotification() != null) { //this should always be the case
            	
            	if ((notification.getLogindetailId() != null) && (notification.getLogindetailId().getUsername().equals(loginBean.getPerson().getLogindetailId().getUsername()))) {
                    //this notification is for this user and she has the permission to delete it
                    notification.setNotificationStatusId(noteService.findNotiStatusByName("Completed"));
                } 

                setNotification(noteService.updateNotificationData(getNotification()));

                if (getNotification() != null) {
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Message has been deleted successfully.", "Error"));
                    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                }

            } else {
                msg_ = new FacesMessage(FacesMessage.SEVERITY_ERROR, "We're sorry but we cannot find the notification.", "");
                facesContext.addMessage("dash_form:msgs", msg_);
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            }
        }

        return null;
    }

    /**
     * @return the notificationId
     */
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * @param notificationId the notificationId to set
     */
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * @return the person
     */
    public People getPerson() {
        return person;
    }

    /**
     * @param person the person to set
     */
    public void setPerson(People person) {
        this.person = person;
    }

    /**
     * @return the personContact
     */
    public ContactInformations getPersonContact() {
        return personContact;
    }

    /**
     * @param personContact the personContact to set
     */
    public void setPersonContact(ContactInformations personContact) {
        this.personContact = personContact;
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

}
