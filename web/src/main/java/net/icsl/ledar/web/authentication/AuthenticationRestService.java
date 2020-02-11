package net.icsl.ledar.web.authentication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.icsl.ledar.ejb.enums.UserActitiyName;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;

import net.icsl.ledar.ejb.model.ContactInformations;
import net.icsl.ledar.ejb.model.People;
import net.icsl.ledar.ejb.model.RegisteredDevices;
import net.icsl.ledar.ejb.model.UserRoles;
import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.ejb.dto.AuthenticatedDataDto;

@Path("authenticationRestService")
@RequestScoped
public class AuthenticationRestService {

    @Inject
    private RegisteredUsersDataService registeredService;

    //private RegisteredUsersDataService
    private String username;
    private String password;
    private String mac_address;

    private People person;
    private AuthenticatedDataDto authData;
    private UserRoles uRoles;
    private ContactInformations personContact;

    private String currentUserName;

    @GET
    @Path("/authenticate/{username_type}/{pwd_type}/{ma_type}/{lat_type}/{log_type}")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedDataDto authenticate(@PathParam("username_type") String uname, @PathParam("pwd_type") String pwd, @PathParam("ma_type") String madr, @PathParam("lat_type") String lt,
            @PathParam("log_type") String lg) {
        try {

            SecurityUtils.getSubject().login(new UsernamePasswordToken(uname, pwd, false));
            mac_address = madr;
            person = registeredService.findUserByUsername(uname);
            List<RegisteredDevices> devices = registeredService.fetchRegisteredDevicesFilter(person.getLogindetailId().getId(), mac_address, null);
            //Boolean is_valid_device = Boolean.FALSE;

            if (devices != null && devices.size() > 0) {

                //if (is_valid_device) {
                if ((person.getOrganization() != null) && (!person.getOrganization().getEnabled())) {
                    Logger.getLogger(AuthenticationRestService.class.getName()).log(Level.SEVERE, null, "Disabled Organization account: " + person.getOrganization().getOrganizationNm());
                    authData = new AuthenticatedDataDto();
                    authData.setServiceMessage("Consultant disabled");
                    return authData;
                }

                if (!person.getLogindetailId().getActive()) {
                    Logger.getLogger(AuthenticationRestService.class.getName()).log(Level.SEVERE, null, "Disabled Login account: " + person.getOrganization().getOrganizationNm());
                    authData = new AuthenticatedDataDto();
                    authData.setServiceMessage("User account disabled");
                    return authData;
                }

                person.getLogindetailId().setLastlogindate(new Date());
                person.getLogindetailId().setLastLoginLatitude(lt);
                person.getLogindetailId().setLastLoginLongitude(lg);
                person.getLogindetailId().setLastLoginIp(madr);

                String person_title = null;
                String telephone_no = null, email_adrr = null;

                if (person.getPersonTitleId() != null) {
                    person_title = person.getPersonTitleId().getTitleName();
                }

                if (person.getContactInformationsList().size() > 0) {
                    telephone_no = person.getContactInformationsList().get(0).getContactPhoneNumber();
                    email_adrr = person.getContactInformationsList().get(0).getOfficeEmailAddress();

                }

                person.getLogindetailId().setUsersLastActivitiesList(new ArrayList<UsersLastActivities>() {
                    {
                        add(new UsersLastActivities(null, UserActitiyName.MOBILELOGIN.toString(), new Date(), "Remote login using Mobile Application for"+person.getFullName()+" with mac address: " + mac_address, null,
                                person.getLogindetailId().getLastLoginLongitude(), person.getLogindetailId().getLastLoginLatitude(), null, person.getLogindetailId(), null, null));
                    }
                });

                List<UserRoles> role = registeredService.findUserRolesByUserId(person.getLogindetailId().getId());

                authData = new AuthenticatedDataDto(person.getLogindetailId().getId(), person.getCreated(), person.getModified(), person.getLastName(), person.getFirstName(),
                        person.getMiddleName(), person.getDob(), person.getGender(), person.getMaritalStatus(), person_title, "Success",
                        telephone_no, email_adrr, person.getLogindetailId().getUsername(),
                        person.getLogindetailId().getActive(), person.getLogindetailId().getPwordResetRequired(), person.getLogindetailId().getUserNumber(),
                        person.getLogindetailId().getLastLoginLatitude(), person.getLogindetailId().getLastLoginLongitude(), "", "", "" + role.get(0).getId(),
                        role.get(0).getAuthenticationRoleId().getRoleName());

                authData.setOrganizationId(person.getOrganization().getId());
                authData.setOrganizationName(person.getOrganization().getOrganizationNm());
                authData.setSenatorialDistrictId(person.getOrganization().getSenatorialDistrictId().getId());
                authData.setSenatorialDistrict(person.getOrganization().getSenatorialDistrictId().getSenatorialCode());

                Logger.getLogger(AuthenticationRestService.class.getName()).log(Level.INFO, "{0} logged in successfully from Mobile App on {1}", new Object[]{uname, new Date()});

                registeredService.update(person.getLogindetailId(), null);
                /**
                 * } else { authData = new AuthenticatedData();
                 * authData.setServiceMessage("unauthorized User<->Device"); }
                 */
            } else {
                authData = new AuthenticatedDataDto();
                authData.setServiceMessage("Unregistered Device");
            }
            SecurityUtils.getSubject().logout();
        } catch (AuthenticationException e) {
            Logger.getLogger(AuthenticationRestService.class.getName()).log(Level.SEVERE, "{0} failed login credentials", uname);

            authData = new AuthenticatedDataDto();
            authData.setServiceMessage("Invalid login credentials");
        }
        return authData;
    }

    public RegisteredUsersDataService getRegisteredService() {
        return registeredService;
    }

    public void setRegisteredService(RegisteredUsersDataService registeredService) {
        this.registeredService = registeredService;
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

    public String getMark_addr() {
        return mac_address;
    }

    public void setMark_addr(String mark_addr) {
        this.mac_address = mark_addr;
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
        return uRoles;
    }

    public void setuRoles(UserRoles uRoles) {
        this.uRoles = uRoles;
    }

    public ContactInformations getPersonContact() {
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

}
