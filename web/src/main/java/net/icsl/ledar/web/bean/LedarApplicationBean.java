package net.icsl.ledar.web.bean;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import javax.inject.Inject;
import net.icsl.ledar.ejb.enums.MarritalStatus;
import net.icsl.ledar.ejb.model.AuthenticationRoles;
import net.icsl.ledar.ejb.model.ComplaintSources;
import net.icsl.ledar.ejb.model.GeographicalBoundaries;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.OrganizationTypes;
import net.icsl.ledar.ejb.model.PersonTitles;

import net.icsl.ledar.ejb.model.SettlementTypes;
import net.icsl.ledar.ejb.model.StreetTypes;
import net.icsl.ledar.ejb.model.UserRoles;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.ReferenceDataService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;

/**
 *
 * @author sfagade
 */
@Named("ledarAppBean")
@ApplicationScoped
public class LedarApplicationBean {

    @Inject
    private ReferenceDataService ref_data;
    @Inject
    private LcdaWardsDataServices lcdaService;
    @Inject
    private RegisteredUsersDataService registeredService;

    private List<GeographicalBoundaries> geo_country_;
    private List<GeographicalBoundaries> geo_state_;
    private List<AuthenticationRoles> auth_roles;
    private List<OrganizationTypes> orgSectors;
    //private List<WardStreets> wardStreets;

    private List<Logindetails> unassignedChairmen;
    private List<ComplaintSources> complaintSources;
    private List<UserRoles> unassignedUserRoles;
    //private List<ProveOfDeliveryStatus> podStatus;
    //private List<FailureDeliveryReasons> failureReasons;

    private List<PersonTitles> personTitle;
    private List<StreetTypes> streetTypes;
    private List<SettlementTypes> settlementTypes;

    private String[] select_gender = new String[2];
    private String[] wasteDisposal = new String[2];
    private String[] tarRoadOption = new String[3];
    private String[] dateSeacrhValues = new String[6];
    private String[] untarredRoadOption = new String[2];
    private String[] monthSearchValues = new String[12];

    private String reportServer = "http://localhost:8080/net.ledar.reporting";
    private Date today = new Date();

    /**
     * Creates a new instance of LedarApplicationBean
     */
    @PostConstruct
    public void init() {
        auth_roles = ref_data.fetchAllAuthenticationRole();
        Collections.sort(auth_roles, AuthenticationRoles.authenticationRoleNameComparator);
        geo_state_ = ref_data.fetchAllCountryStates("Nigeria");
        geo_country_ = ref_data.fetchAllCountries();

        select_gender[0] = "FEMALE";
        select_gender[1] = "MALE";

        getWasteDisposal()[0] = "LAWMA";
        getWasteDisposal()[1] = "PRIVATE";

        tarRoadOption[0] = "GOOD";
        tarRoadOption[1] = "FAIR";
        tarRoadOption[2] = "POOR";

        untarredRoadOption[0] = "MOTORABLE";
        untarredRoadOption[1] = "NOT-MOTORABLE";

        dateSeacrhValues[0] = "TODAY";
        dateSeacrhValues[1] = "YESTERDAY";
        dateSeacrhValues[2] = "PAST WEEK";
        dateSeacrhValues[3] = "PAST MONTH";
        dateSeacrhValues[4] = "THIS YEAR";
        dateSeacrhValues[5] = "SELECT DATE RANGE";

        orgSectors = ref_data.fetchAllOrganizationTypes();
        monthSearchValues[0] = "JANUARY";
        monthSearchValues[1] = "FEBUARY";
        monthSearchValues[2] = "MARCH";
        monthSearchValues[3] = "APRIL"; 
        monthSearchValues[4] = "MAY";
        monthSearchValues[5] = "JUNE";
        monthSearchValues[6] = "JULY";
        monthSearchValues[7] = "AUGUST";
        monthSearchValues[8] = "SEPTEMBER";
        monthSearchValues[9] = "OCTOBER";
        monthSearchValues[10] = "NOVEMBER";
        monthSearchValues[11] = "DECEMBER";

//        setPodStatus(ref_data.fetchAllProveOfDeliveryStatus());
//        failureReasons = ref_data.fetchAllFailureDeliveryReasons();
//        Collections.sort(failureReasons, FailureDeliveryReasons.failReasonComparator);
        if (streetTypes == null) {
            streetTypes = ref_data.fetchAllStreetType();
        }

        settlementTypes = ref_data.fetchAllSettlementType();
        personTitle = ref_data.fetchAllTittle();
        complaintSources = ref_data.fetchAllComplaintSources();
    }

    /**
     * @return the geo_country_
     */
    public List<GeographicalBoundaries> getGeo_country_() {
        return geo_country_;
    }

    /**
     * @param geo_country_ the geo_country_ to set
     */
    public void setGeo_country_(List<GeographicalBoundaries> geo_country_) {
        this.geo_country_ = geo_country_;
    }

    public List<UserRoles> getUnassignedUserRoles() {
        return unassignedUserRoles;
    }

    public void setUnassignedUserRoles(List<UserRoles> unassignedUserRoles) {
        this.unassignedUserRoles = unassignedUserRoles;
    }

    /**
     * @return the geo_state_
     */
    public List<GeographicalBoundaries> getGeo_state_() {
        return geo_state_;
    }

    /**
     * @param geo_state_ the geo_state_ to set
     */
    public void setGeo_state_(List<GeographicalBoundaries> geo_state_) {
        this.geo_state_ = geo_state_;
    }

    /**
     * @return the auth_roles
     */
    public List<AuthenticationRoles> getAuth_roles() {
        return auth_roles;
    }

    /**
     * @param auth_roles the auth_roles to set
     */
    public void setAuth_roles(List<AuthenticationRoles> auth_roles) {
        this.auth_roles = auth_roles;
    }

    /**
     * @return the select_gender
     */
    public String[] getSelect_gender() {
        return select_gender;
    }

    /**
     * @param select_gender the select_gender to set
     */
    public void setSelect_gender(String[] select_gender) {
        this.select_gender = select_gender;
    }

    public MarritalStatus[] getMstatus() {
        return MarritalStatus.values();
    }

    /**
     * @return the orgSectors
     */
    public List<OrganizationTypes> getOrgSectors() {
        return orgSectors;
    }

    /**
     * @param orgSectors the orgSectors to set
     */
    public void setOrgSectors(List<OrganizationTypes> orgSectors) {
        this.orgSectors = orgSectors;
    }

    public String getReportServer() {
        return reportServer;
    }

    public void setReportServer(String reportServer) {
        this.reportServer = reportServer;
    }

    /**
     * @return the unassignedChairmen
     */
    public List<Logindetails> getUnassignedChairmen() {
        return unassignedChairmen;
    }

    /**
     * @param unassignedChairmen the unassignedChairmen to set
     */
    public void setUnassignedChairmen(List<Logindetails> unassignedChairmen) {
        this.unassignedChairmen = unassignedChairmen;
    }

    /**
     * @return the streetTypes
     */
    public List<StreetTypes> getStreetTypes() {
        return streetTypes;
    }

    /**
     * @param streetTypes the streetTypes to set
     */
    public void setStreetTypes(List<StreetTypes> streetTypes) {
        this.streetTypes = streetTypes;
    }

    /**
     * @return the settlementTypes
     */
    public List<SettlementTypes> getSettlementTypes() {
        return settlementTypes;
    }

    /**
     * @param settlementTypes the settlementTypes to set
     */
    public void setSettlementTypes(List<SettlementTypes> settlementTypes) {
        this.settlementTypes = settlementTypes;
    }

    /**
     * @return the wasteDisposal
     */
    public String[] getWasteDisposal() {
        return wasteDisposal;
    }

    /**
     * @param wasteDisposal the wasteDisposal to set
     */
    public void setWasteDisposal(String[] wasteDisposal) {
        this.wasteDisposal = wasteDisposal;
    }

    /**
     * @return the tarRoadOption
     */
    public String[] getTarRoadOption() {
        return tarRoadOption;
    }

    /**
     * @param tarRoadOption the tarRoadOption to set
     */
    public void setTarRoadOption(String[] tarRoadOption) {
        this.tarRoadOption = tarRoadOption;
    }

    /**
     * @return the untarredRoadOption
     */
    public String[] getUntarredRoadOption() {
        return untarredRoadOption;
    }

    /**
     * @param untarredRoadOption the untarredRoadOption to set
     */
    public void setUntarredRoadOption(String[] untarredRoadOption) {
        this.untarredRoadOption = untarredRoadOption;
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

    /**
     * @return the complaintSources
     */
    public List<ComplaintSources> getComplaintSources() {
        return complaintSources;
    }

    /**
     * @param complaintSources the complaintSources to set
     */
    public void setComplaintSources(List<ComplaintSources> complaintSources) {
        this.complaintSources = complaintSources;
    }

    /**
     * @return the dateSeacrhValues
     */
    public String[] getDateSeacrhValues() {
        return dateSeacrhValues;
    }

    /**
     * @param dateSeacrhValues the dateSeacrhValues to set
     */
    public void setDateSeacrhValues(String[] dateSeacrhValues) {
        this.dateSeacrhValues = dateSeacrhValues;
    }

    public Date getToday() {
        //return new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        return today;
    }

    public void setToday(Date today_) {
        this.today = today_;
    }

    /**
     * @return the monthSearchValues
     */
    public String[] getMonthSearchValues() {
        return monthSearchValues;
    }

    /**
     * @param monthSearchValues the monthSearchValues to set
     */
    public void setMonthSearchValues(String[] monthSearchValues) {
        this.monthSearchValues = monthSearchValues;
    }

}
