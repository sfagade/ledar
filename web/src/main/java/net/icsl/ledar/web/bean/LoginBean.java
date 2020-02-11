package net.icsl.ledar.web.bean;

import com.maxmind.geoip2.model.CityResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.icsl.ledar.ejb.model.ContactInformations;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.People;
import net.icsl.ledar.ejb.model.UserRoles;
import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.web.util.FacesSupportUtil;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import net.icsl.ledar.ejb.dto.PrintedBillsDto;
import net.icsl.ledar.ejb.enums.PropertyTypes;
import net.icsl.ledar.ejb.enums.TarredRoads;
import net.icsl.ledar.ejb.enums.UntarredRoads;
import net.icsl.ledar.ejb.enums.UserActitiyName;
import net.icsl.ledar.ejb.enums.WasteDisposalSystem;
import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.SettlementTypes;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import net.icsl.ledar.ejb.service.ReferenceDataService;
import net.icsl.ledar.web.util.ApplicationUtility;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private FacesContext facesContext;

    @Inject
    private RegisteredUsersDataService registeredService;
    @Inject
    private LcdaWardsDataServices lcdaService;
    @Inject
    private ReferenceDataService ref_data;
    @Inject
    private PrintedBillsService billsService;

    private List<LocalCouncilDevArea> lcdArea;
    private List<LcdaWards> lcdaWards;
    private List<SettlementTypes> settlementTypes;
    private List<WardStreets> wardStreets;

    private String HOME_URL = "app/index.xhtml";

    private String username, password, currentUserName, city;
    private boolean remember;

    private People person;
    private UserRoles uRoles;
    private ContactInformations personContact;
    private LocalCouncilDevArea chairmanLcda;

    @PostConstruct
    public void init() {
        this.settlementTypes = ref_data.fetchAllSettlementType();
    }

    public void submit() throws IOException {
        try {

            SecurityUtils.getSubject().login(new UsernamePasswordToken(username, password, remember));
            SavedRequest savedRequest = WebUtils.getAndClearSavedRequest((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());

            person = registeredService.findUserByUsername(username);
            person.getLogindetailId().setLastlogindate(new Date());
            person.getLogindetailId().setLastLoginIp(FacesSupportUtil.getClientIpAddress());
            person.getLogindetailId().setIsLoggedIn(Boolean.TRUE);
            CityResponse response = ApplicationUtility.fetchLocationFromIp(FacesSupportUtil.getClientIpAddress());
            Logger.getLogger(LoginBean.class.getName()).log(Level.INFO, "{0} logged in successfully from WEB at: {1} from: {2}", new Object[]{username, new Date(), ((response != null)? response.getCity().getName():"")});

            city = (response != null)? response.getCity().getName():"";
            
            if ((person.getOrganization() != null) && (!person.getOrganization().getEnabled())) {

                FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "We're sorry but your Organization has been disabled, please contact your Administrator", "Org Disabled");
                facesContext.addMessage("login:sbtbtn", m);
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, username + " failed to login: " + person.getOrganization().getOrganizationNm());
                SecurityUtils.getSubject().logout();
                return;
            }

            if ((person.getLogindetailId().getPwordResetRequired() != null) && (person.getLogindetailId().getPwordResetRequired())) {

                FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Your password has expired, please reset your password before continuing", "Reset Password");
                facesContext.addMessage("pwordchng:sbtbtn", m);
                //SecurityUtils.getSubject().logout();
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().getExternalContext().redirect("app/changepwd.xhtml");
                return;
            }

            org.apache.shiro.subject.Subject presentUser = SecurityUtils.getSubject();
            this.setCurrentUserName(username);
            //HttpSession session = (HttpSession) presentUser.getSession();
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            person.getLogindetailId().setActiveSessionId(session.getId() + "");
            session.setAttribute("person", person.getFullName());

            /** if (presentUser.hasRole("LCDA CHAIRMAN")) { //this person is a LCDA chairman, I need to get his LCDA
                chairmanLcda = lcdaService.fetchChairmanLcda(person.getLogindetailId().getId());
            } */

            if (!person.getLogindetailId().getActive()) {
                FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Your account has been deactivated, please contact Admin!", "Reset Password");
                facesContext.addMessage("login:sbtbtn", m);
                SecurityUtils.getSubject().logout();
                return;
            }

            if (presentUser.hasRole("ADMINISTRATOR") && HOME_URL.equals("app/index.xhtml")) {
                registeredService.update(person.getLogindetailId(), null);
                HOME_URL = "app/administrator/index.xhtml";
            } else if (presentUser.hasRole("APPLICATION ADMINISTRATOR") && HOME_URL.equals("app/index.xhtml")) {
                registeredService.update(person.getLogindetailId(), null);
                HOME_URL = "app/appAdmin/index.xhtml";
            }
            
            person.getLogindetailId().setUsersLastActivitiesList(new ArrayList<UsersLastActivities>() {
                {
                    add(new UsersLastActivities(null, UserActitiyName.WEBLOGIN.toString(), new Date(), person.getFullName()+" logged into the System on the WEB platform using " + FacesSupportUtil.getClientIpAddress()+" IP address in "+city, null,
                            person.getLogindetailId().getLastLoginLongitude(), person.getLogindetailId().getLastLoginLatitude(), null, person.getLogindetailId(), null, null));
                }
            });

            registeredService.update(person.getLogindetailId(), null);

            if (savedRequest != null) {
                if (!savedRequest.getRequestUrl().contains("login")) {
                    HOME_URL = savedRequest.getRequestUrl();
                }
            }

            FacesContext.getCurrentInstance().getExternalContext().redirect(HOME_URL);
        } catch (AuthenticationException e) {
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unknown user, please try again", "Login Fail");
            facesContext.addMessage("login:sbtbtn", m);
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<PrintedBillsDto> runDistrictAutoComplete(String district_name) {

        List<PrintedBillsDto> billDistrict_ = new ArrayList<>();

        if ((district_name != null) && (!district_name.isEmpty()) && district_name.length() > 3) { //we want to start searching after third character is entered
            Map<String, String> criteria = new HashMap<>();
            criteria.put("district", district_name);
            /** if (selectedLcda != null) {
                criteria.put("lga", selectedLcda.getLcdaName());
            } */
            criteria.put("like_search", "yes");

            List<Object[]> districts = billsService.fetchBillsDistrictByName(criteria);

            if ((districts != null) && (districts.size() > 0)) {
                for (Object[] bill : districts) {
                    //Long bill_id, String prop_id, String street_name, String district, String lga, Date created_
                    billDistrict_.add(new PrintedBillsDto(null, null, null, bill[0].toString(), bill[1].toString(), null));
                }
            }
            return billDistrict_;
        }

        return null;
    }
    
    public List<PrintedBillsDto> runStreetAutoComplete(String street_name) {

        List<PrintedBillsDto> billStreet_ = new ArrayList<>();

        if ((street_name != null) && (!street_name.isEmpty()) && (street_name.length() >= 3)) {
            List<Object[]> streetNames = billsService.fetchBillsStreetByName(street_name, null, true);

            if ((streetNames != null) && (streetNames.size() > 0)) {
                for (Object[] bill : streetNames) {
                    //Long bill_id, String prop_id, String street_name, String district, String lga, Date created_
                    billStreet_.add(new PrintedBillsDto(null, null, bill[0].toString(), bill[1].toString(), bill[2].toString(), null));
                }
            }
            return billStreet_;

        }

        return null;
    }
    
    public People getPerson() {

        if (person == null) {
            person = registeredService.findUserByUsername(currentUserName);
        }

        return person;
    }

    public void setPerson(People person) {
        this.person = person;
    }

    public UserRoles getuRoles() {

        if (uRoles == null && getPerson() != null) {
            List<UserRoles> uRoles_ = registeredService.findUserRolesByUserId(getPerson().getLogindetailId().getId());
            uRoles = uRoles_.get(0);
        }

        return uRoles;
    }

    public void setuRoles(UserRoles uRoles) {
        this.uRoles = uRoles;
    }

    public ContactInformations getPersonContact() {
        List<ContactInformations> contact_info = (List<ContactInformations>) getPerson().getContactInformationsList();
        personContact = contact_info.get(0);

        return personContact;
    }

    public void setPersonContact(ContactInformations personContact) {
        this.personContact = personContact;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    /**
     * @return the chairmanLcda
     */
    public LocalCouncilDevArea getChairmanLcda() {
        return chairmanLcda;
    }

    /**
     * @param chairmanLcda the chairmanLcda to set
     */
    public void setChairmanLcda(LocalCouncilDevArea chairmanLcda) {
        this.chairmanLcda = chairmanLcda;
    }

    /**
     * @return the lcdArea
     */
    public List<LocalCouncilDevArea> getLcdArea() {

        String[] accepted_roles = {"ADMINISTRATOR"};

        if (lcdArea == null && this.getPerson() != null && (this.getPerson().getOrganization() != null)) {
            lcdArea = lcdaService.fetchAllSenatorialDistrictLCDAs(this.getPerson().getOrganization().getSenatorialDistrictId().getId());
        } else if (FacesSupportUtil.isUserInRole(accepted_roles)) {
            lcdArea = lcdaService.fetchAllSenatorialDistrictLCDAs(null);
        }

        return lcdArea;
    }

    /**
     * @param lcdArea the lcdArea to set
     */
    public void setLcdArea(List<LocalCouncilDevArea> lcdArea) {
        this.lcdArea = lcdArea;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }

    public List<LcdaWards> getLcdaWards() {
        return lcdaWards;
    }

    public void setLcdaWards(List<LcdaWards> lcdaWards) {
        this.lcdaWards = lcdaWards;
    }

    public PropertyTypes[] getPropertyTypes() {
        return PropertyTypes.values();
    }

    public WasteDisposalSystem[] getWasteDisposalSystem() {
        return WasteDisposalSystem.values();
    }

    public TarredRoads[] getTarredRoads() {
        return TarredRoads.values();
    }

    public UntarredRoads[] getUntarredRoads() {
        return UntarredRoads.values();
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

    public List<WardStreets> getWardStreets() {
        return wardStreets;
    }

    public void setWardStreets(List<WardStreets> wardStreets) {
        this.wardStreets = wardStreets;
    }
}
