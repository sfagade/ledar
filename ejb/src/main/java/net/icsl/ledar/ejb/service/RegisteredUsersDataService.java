package net.icsl.ledar.ejb.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import net.icsl.ledar.ejb.model.Addresses;
import net.icsl.ledar.ejb.model.AuthenticationRoles;
import net.icsl.ledar.ejb.model.ContactInformations;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.People;
import net.icsl.ledar.ejb.model.RegisteredDevices;
import net.icsl.ledar.ejb.model.UserRoles;
import net.icsl.ledar.ejb.model.UsersLastActivities;

@Stateless
public class RegisteredUsersDataService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager entManager;

    @Inject
    private ReferenceDataService ref_data;

    @Inject
    NotificationDataService notifiService;

    @TransactionAttribute(REQUIRES_NEW)
    public Logindetails find(Long id) {
        return entManager.find(Logindetails.class, id);
    }

    public People findById(Long id) {
        return entManager.find(People.class, id);
    }

    public Logindetails find(String username, String password) {
        List<Logindetails> found = entManager.createNamedQuery("Logindetails.find", Logindetails.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .getResultList();
        return found.isEmpty() ? null : found.get(0);
    }

    public People findUserByUsernameToken(String username, String activation_token) {

        Query query;

        if (username != null) { //this should always be the case
            query = entManager.createQuery("from People p where p.logindetailId.username=:custmerID and p.logindetailId.activationKey=:activateToken");
            query.setParameter("custmerID", username);
            query.setParameter("activateToken", activation_token);

            return (People) query.getSingleResult();
        }
        return null;
    }

    public Logindetails findUserBySessionId(String sess_id) {

        Logindetails loginData = null;

        if (sess_id != null) { //this should always be the case
            try {
                loginData = entManager.createQuery("from Logindetails p where p.activeSessionId=:user_name", Logindetails.class).setParameter("user_name", sess_id).getSingleResult();

            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(RegisteredUsersDataService.class.getName()).log(Level.INFO, "Hibernate did not find user with Session ID: " + sess_id + "", nr.getMessage());
            }

        }
        return loginData;
    }

    public People findUserByUsername(String username) {

        People person = null;

        if (username != null) { //this should always be the case
            try {
                person = entManager.createQuery("from People p where p.logindetailId.username=:user_name", People.class).setParameter("user_name", username).getSingleResult();

            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(RegisteredUsersDataService.class.getName()).log(Level.INFO, "Hibernate did not find user with username: " + username + "", nr);
            }

        }
        return person;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Long create(Logindetails user, String[] auth_roles) {

        UserRoles u_roles; // = new ArrayList<>();
        entManager.persist(user);

        for (String authRoles : auth_roles) {
            u_roles = new UserRoles(user, ref_data.fetchAuthenticationRoleById(Long.valueOf(authRoles)));
            entManager.persist(u_roles);
        }

        return user.getId();
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Logindetails registerNewUser(Logindetails user, AuthenticationRoles auth_roles, Addresses addresses_) {

        UserRoles u_roles; // = new ArrayList<>();

        entManager.persist(addresses_);
        user.getPerson().setAddressId(addresses_); //set person address

        entManager.persist(user);

        u_roles = new UserRoles(user, auth_roles);
        entManager.persist(u_roles);

        return user;
    }

    public AuthenticationRoles createAuthenticationRole(AuthenticationRoles auth_role) {

        entManager.persist(auth_role);

        return auth_role;
    }

    public People createNewPerson(People person) {

        entManager.merge(person);
        return person;
    }

    public boolean update(Logindetails user, UsersLastActivities activity) {

        if (user != null) {
            entManager.merge(user);

            if (activity != null) {
                entManager.persist(activity);
            }
            return true;
        }

        return false;
    }

    public List<ContactInformations> fetchAllContactByEmailAddress(String email_address) {

        List<ContactInformations> contacts = null;
        if (email_address != null) {
            try {
                contacts = entManager.createNamedQuery("ContactInformations.findByEmail", ContactInformations.class).setParameter("email", email_address).getResultList();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(RegisteredUsersDataService.class.getName()).log(Level.INFO, "Hibernate did not find user with email address: " + email_address + "", nr);
            }
        }
        return contacts;
    }

    public List<UserRoles> findUserRolesByUserId(Long user_id) {
        List<UserRoles> roles_ = entManager.createNamedQuery("UserRoles.findUserId", UserRoles.class)
                .setParameter("userId", user_id)
                .getResultList();
        return roles_;
    }

    public List<Logindetails> fetchAllRegisteredUsers(Long contractor_id) {

        List<Logindetails> regUsers;

        if (contractor_id != null) {
            regUsers = entManager.createQuery("FROM Logindetails l where l.person.organization.id=:orgId ORDER BY l.person.fullName", Logindetails.class).setParameter("orgId", contractor_id).getResultList();
        } else {
            regUsers = entManager.createNamedQuery("Logindetails.findAll", Logindetails.class).getResultList();
        }

        return regUsers;
    }

    public List<Logindetails> fetchAllLcdaChairmen(List<Long> role_id, Long consultant_id) {

        List<Logindetails> unassigned = null;

        try {
            unassigned = entManager.createQuery("FROM Logindetails l where l.userRole.authenticationRoleId.id IN :roleID AND l.person.organization.id = :conslt ORDER BY l.person.fullName", Logindetails.class).
                    setParameter("roleID", role_id).setParameter("conslt", consultant_id).getResultList();
        } catch (javax.persistence.NoResultException ne) {
            Logger.getLogger(RegisteredUsersDataService.class.getName()).log(Level.INFO, "There's no unassigned Chairman: ", ne);
        }

        return unassigned;
    }

    public UsersLastActivities findLastUserLoginInformation(Long user_id) {

        UsersLastActivities activity = null;

        try {
            activity = entManager.createQuery("FROM UsersLastActivities u WHERE u.logindetailId.id = :userId", UsersLastActivities.class).setParameter("userId", user_id).getSingleResult();
        } catch (NoResultException ne) {
            Logger.getLogger(RegisteredUsersDataService.class.getName()).log(Level.INFO, "User has never signed in before: ", ne);
        }

        return activity;
    }

    public List<UsersLastActivities> fetchAllRecentActivitiesByName(List<String> activity_names, Long role) {

        List<UsersLastActivities> activities = null;

        try {

            activities = entManager.createQuery("FROM UsersLastActivities u WHERE u.activitiy IN (:activName) AND u.logindetailId.userRole.authenticationRoleId.id = :roleId", UsersLastActivities.class).
                    setParameter("activName", activity_names).setParameter("roleId", role).getResultList();
        } catch (NoResultException ne) {
            Logger.getLogger(RegisteredUsersDataService.class.getName()).log(Level.INFO, "User has never signed in before: ", ne);
        }

        return activities;
    }

    public List<Logindetails> fetchLoginusersNameLike(String person_name) {

        List<Logindetails> users = null;

        if ((person_name != null) && (!person_name.isEmpty())) {
            try {
                users = entManager.createQuery("FROM Logindetails l WHERE l.person.lastName LIKE :lname OR l.person.firstName LIKE :fname", Logindetails.class).setParameter("lname", "%" + person_name + "%").
                        setParameter("fname", "%" + person_name + "%").getResultList();
            } catch (NoResultException ne) {
                Logger.getLogger(RegisteredUsersDataService.class.getName()).log(Level.INFO, "User has never signed in before: ", ne);
            }
        }

        return users;
    }

    public List<RegisteredDevices> fetchAllRegisteredDevices(Long consultant) {

        List<RegisteredDevices> devices = null;
        String sql_query = "FROM RegisteredDevices r";

        try {
            if ((consultant != null)) {
                devices = entManager.createQuery(sql_query + " WHERE r.consultantId.id = :conts ORDER BY r.deviceOwnerById.person.fullName", RegisteredDevices.class).setParameter("conts", consultant).getResultList();
            } else {
                devices = entManager.createQuery(sql_query, RegisteredDevices.class).getResultList();
            }
        } catch (NoResultException ne) {
            Logger.getLogger(RegisteredUsersDataService.class.getName()).log(Level.INFO, "User has never signed in before: ", ne);
        }

        return devices;
    }

    public Boolean saveNewRegisteredDevice(RegisteredDevices device, UsersLastActivities activity) {

        if (device != null) {
            try {
                if (device.getId() != null) {
                    entManager.merge(device);
                } else {
                    entManager.persist(device);
                }

                if (activity != null) {
                    entManager.persist(activity);
                }

                return true;
            } catch (javax.ejb.EJBTransactionRolledbackException sqle) {
                Logger.getLogger(RegisteredUsersDataService.class.getName()).log(Level.INFO, "Constraint error while trying to register new devive: ", sqle.getMessage());
            }
        }

        return false;
    }

    public RegisteredDevices findRegisteredDeviceById(Long device_id) {

        RegisteredDevices device = null;

        if (device_id != null) {
            try {
                device = entManager.createQuery("FROM RegisteredDevices r WHERE r.id = :dev_id", RegisteredDevices.class).setParameter("dev_id", device_id).getSingleResult();
            } catch (NoResultException ne) {
                Logger.getLogger(RegisteredUsersDataService.class.getName()).log(Level.INFO, "Could not find Device with ID: " + device_id, ne);
            }
        }

        return device;
    }

    public List<RegisteredDevices> fetchRegisteredDevicesFilter(Long owner_id, String mac_address, String imei_number) {

        List<RegisteredDevices> devices = null;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<RegisteredDevices> regDevice = cq.from(RegisteredDevices.class);

        List<Predicate> predicates = new ArrayList<>();

        if ((mac_address != null) && (!mac_address.isEmpty())) {
            predicates.add(qb.equal(regDevice.get("macAddress"), mac_address));
        }

        if ((imei_number != null) && (!imei_number.isEmpty())) {
            predicates.add(qb.equal(regDevice.get("imeiNumber"), imei_number));
        }

        if (owner_id != null) {
            Join<RegisteredDevices, Logindetails> street = regDevice.join("deviceOwnerById");
            predicates.add(qb.equal(street.get("id"), owner_id));
        }
        try {
            devices = entManager.createQuery(cq.select(regDevice).where(predicates.toArray(new Predicate[]{})).orderBy(qb.asc(regDevice.get("created")))).getResultList();
        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(RegisteredUsersDataService.class.getName()).log(Level.SEVERE, "Did not find any Device: ", nr);
        }

        return devices;
    }

}
