/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;

import javax.faces.context.FacesContext;

import javax.inject.Inject;
import javax.inject.Named;

import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.PersonTitles;

import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;

/**
 *
 * @author sfagade
 */
@Named("dataEntryBean")
@ConversationScoped
public class DataEntryBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private FacesContext facesContext;

    @Inject
    private LandPropertiesDataService landPropService;
    @Inject
    private LcdaWardsDataServices lcdaWardService;

    @Inject
    private LoginBean loginBean;
    @Inject
    private LedarApplicationBean ledarApp;
//    @ManagedProperty(value = "#{param.propId}")
    private String propertyBillId;

    //private Logindetails selectedEntryOfficer;
    private LcdaWards selected_ward;
    private LocalCouncilDevArea selected_lcda;
    private WardStreets selectedStreet;
    private PersonTitles selectedTitle;

    private int recordCount;
    private Date dataEntryDate;
    private Boolean isEditing;

    private final DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    @PostConstruct
    public void init() {
        String view_id = facesContext.getViewRoot().getViewId();

    }

    public void changedLcda() {
        System.out.println("called changeLcda: " + selected_lcda);
        if (selected_lcda != null) {
            loginBean.setLcdaWards(lcdaWardService.fetchAllLcdaWardsByLcda(selected_lcda.getId(), null));
        }
    }

    public void changedWardStreets() {

        if (selected_ward != null) {
            loginBean.setWardStreets(lcdaWardService.fetchWardStreets(selected_ward.getId(), null, Boolean.TRUE));
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

    public Date getDataEntryDate() {
        return dataEntryDate;
    }

    public void setDataEntryDate(Date dataEntryDate) {
        this.dataEntryDate = dataEntryDate;
    }

    public WardStreets getSelectedStreet() {
        return selectedStreet;
    }

    public void setSelectedStreet(WardStreets selectedStreet) {
        this.selectedStreet = selectedStreet;
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

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
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
     * @return the isEditing
     */
    public Boolean getIsEditing() {
        return isEditing;
    }

    /**
     * @param isEditing the isEditing to set
     */
    public void setIsEditing(Boolean isEditing) {
        this.isEditing = isEditing;
    }

}
