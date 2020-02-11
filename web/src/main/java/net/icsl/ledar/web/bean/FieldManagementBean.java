package net.icsl.ledar.web.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;

import javax.faces.context.FacesContext;

import javax.inject.Inject;
import javax.inject.Named;

import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;

/**
 *
 * @author sfagade
 */
@Named("fieldManageBean")
@SessionScoped
public class FieldManagementBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private FacesContext facesContext;
    @Inject
    private LandPropertiesDataService landPropService;
    @Inject
    private LcdaWardsDataServices lcdaWardService;
    @Inject
    private RegisteredUsersDataService registeredService;

    @Inject
    private LoginBean loginBean;
    @Inject
    private LedarApplicationBean ledarApp;

    private Map<Long, Boolean> checked = new HashMap<>();

    private LocalCouncilDevArea selected_lcda;
    private LcdaWards selected_ward;
    private WardStreets selectedStreet;
    private Logindetails selectedFieldOfficer;
    private List<Logindetails> allFieldOfficers;

    private long recordsCount;
    private String searchPropId;
    private boolean likeSearch;
    private boolean[] selectedRows;

    /**
     * Creates a new instance of FieldManagementBean
     */
    public FieldManagementBean() {
    }

    @PostConstruct
    public void init() {
        String view_id = facesContext.getViewRoot().getViewId();

    }

    public void changedLcda() {
        System.out.println("called changeLcda: " + getSelected_lcda());
        if (getSelected_lcda() != null) {
            loginBean.setLcdaWards(lcdaWardService.fetchAllLcdaWardsByLcda(getSelected_lcda().getId(), null));
        }
    }

    public void changedWardStreets() {

        if (getSelected_ward() != null) {
            loginBean.setWardStreets(lcdaWardService.fetchWardStreets(getSelected_ward().getId(), null, Boolean.TRUE));
        }
    }

    /**
     * @return the recordsCount
     */
    public long getRecordsCount() {
        return recordsCount;
    }

    /**
     * @param recordsCount the recordsCount to set
     */
    public void setRecordsCount(long recordsCount) {
        this.recordsCount = recordsCount;
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
     * @return the selectedStreet
     */
    public WardStreets getSelectedStreet() {
        return selectedStreet;
    }

    /**
     * @param selectedStreet the selectedStreet to set
     */
    public void setSelectedStreet(WardStreets selectedStreet) {
        this.selectedStreet = selectedStreet;
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

    public boolean isLikeSearch() {
        return likeSearch;
    }

    public void setLikeSearch(boolean likeSearch) {
        this.likeSearch = likeSearch;
    }

    public Map<Long, Boolean> getChecked() {
        return checked;
    }

    public void setChecked(Map<Long, Boolean> checked) {
        this.checked = checked;
    }

    /**
     * @return the selectedFieldOfficer
     */
    public Logindetails getSelectedFieldOfficer() {
        return selectedFieldOfficer;
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
     * @param selectedFieldOfficer the selectedFieldOfficer to set
     */
    public void setSelectedFieldOfficer(Logindetails selectedFieldOfficer) {
        this.selectedFieldOfficer = selectedFieldOfficer;
    }

    public boolean[] getSelectedRows() {
        return selectedRows;
    }

    public void setSelectedRows(boolean[] selectedRows) {
        this.selectedRows = selectedRows;
    }
}
