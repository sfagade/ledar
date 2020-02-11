package net.icsl.ledar.ejb.service;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.icsl.ledar.ejb.model.NotificationPeople;

import net.icsl.ledar.ejb.model.NotificationStatus;
import net.icsl.ledar.ejb.model.NotificationTypes;
import net.icsl.ledar.ejb.model.Notifications;

@Stateless
public class NotificationDataService {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager entManager;

    public List<Notifications> findUserNotifications(Long user_id, String user_role, int max_result) {

        List<Notifications> notifi = null;
        try {
            notifi = entManager.createQuery("FROM Notifications n WHERE (n.logindetailId.id = :logindetailId or n.notificationRole IN (:nrole)) AND "
                    + "n.notificationStatusId.statusName <> 'Completed' ORDER BY n.created", Notifications.class).setParameter("logindetailId", user_id).setParameter("nrole", Long.valueOf(user_role)).
                    setMaxResults(max_result).getResultList();
        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(NotificationDataService.class.getName()).log(Level.INFO, "User does not have any notifications: " + user_id + " ", nr);
        }
        return notifi;

    }

    public Notifications findNotificationById(Long notification_id) {

        return entManager.find(Notifications.class, notification_id);
    }

    public NotificationStatus findNotiStatusByName(String status_name) {

        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<NotificationStatus> criteria = cb.createQuery(NotificationStatus.class);
        Root<NotificationStatus> servRoot = criteria.from(NotificationStatus.class);

        criteria = criteria.multiselect(servRoot.get("id"), servRoot.get("statusName")).where(cb.equal(servRoot.get("statusName"), status_name));

        return entManager.createQuery(criteria).getSingleResult();

    }

    public NotificationTypes findNotifiTypeByName(String type_name) {
        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<NotificationTypes> criteria = cb.createQuery(NotificationTypes.class);
        Root<NotificationTypes> servRoot = criteria.from(NotificationTypes.class);

        criteria = criteria.multiselect(servRoot.get("id"), servRoot.get("notificationTypeName")).where(cb.equal(servRoot.get("notificationTypeName"), type_name));

        return entManager.createQuery(criteria).getSingleResult();
    }

    public NotificationTypes findNotifiTypeById(Long type_id) {
        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<NotificationTypes> criteria = cb.createQuery(NotificationTypes.class);
        Root<NotificationTypes> servRoot = criteria.from(NotificationTypes.class);

        criteria = criteria.multiselect(servRoot.get("id"), servRoot.get("notificationTypeName")).where(cb.equal(servRoot.get("id"), type_id));

        return entManager.createQuery(criteria).getSingleResult();
    }

    public NotificationStatus findNotiStatusById(Long status_id) {

        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<NotificationStatus> criteria = cb.createQuery(NotificationStatus.class);
        Root<NotificationStatus> servRoot = criteria.from(NotificationStatus.class);

        criteria = criteria.multiselect(servRoot.get("id"), servRoot.get("statusName")).where(cb.equal(servRoot.get("id"), status_id));

        return entManager.createQuery(criteria).getSingleResult();

    }

    @TransactionAttribute(REQUIRES_NEW)
    public Notifications createNewNotification(Notifications notifi, Notifications parentNotifi) {

        if (notifi.getNotifiType() != null) {
            notifi.setNotificationTypeId(findNotifiTypeById(Long.valueOf(notifi.getNotifiType())));
        }

        if (notifi.getNotifiStatus() != null) {
            notifi.setNotificationStatusId(findNotiStatusById(Long.valueOf(notifi.getNotifiStatus())));
        }

        if (parentNotifi != null) {
            entManager.merge(parentNotifi);
        }

        entManager.persist(notifi);

        return notifi;
    }

    public Notifications updateNotificationData(Notifications notofic) {

        if (notofic != null) {
            entManager.merge(notofic);

            return notofic;
        }

        return null;
    }

    public List<NotificationPeople> fetchAllNotificationPeopleByAction(String notific_action) {

        List<NotificationPeople> propBills = null;

        if (notific_action != null) {
            try {
                propBills = entManager.createQuery("FROM NotificationPeople n WHERE n.emailNotificationId.actionName = :action AND n.isEnabled = true", NotificationPeople.class)
                        .setParameter("action", notific_action).getResultList();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(NotificationDataService.class.getName()).log(Level.SEVERE, "This field guy does not have any property assigned: ", nr);
            }
        }

        return propBills;
    }

    public List<Notifications> fetchAllEntityNotifications(Long entity_id, Boolean deleted) {

        List<Notifications> notifics = null;

        if (entity_id != null) {
            try {
                notifics = entManager.createQuery("FROM Notifications n WHERE n.entityId = :action AND n.isDeleted=:deleted ORDER By n.created desc", Notifications.class).setParameter("action", entity_id).setParameter("deleted", deleted).getResultList();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(NotificationDataService.class.getName()).log(Level.SEVERE, "No Notification Created for this Entity: ", nr);
            }
        }

        return notifics;
    }
}
