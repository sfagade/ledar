/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import net.icsl.ledar.ejb.model.ComlaintDocuments;
import net.icsl.ledar.ejb.model.ComplainerRelationship;
import net.icsl.ledar.ejb.model.ComplaintDetails;
import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.PropertyComplaints;
import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.model.VisitPurpose;
import net.icsl.ledar.ejb.model.Visitors;

/**
 *
 * @author sfagade
 * @date Mar 10, 2017
 */
@Stateless
public class ComplaintDataService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager entManager;

    @TransactionAttribute(REQUIRES_NEW)
    public PropertyComplaints saveNewComplaintInformation(PropertyComplaints propComplaint, ComplaintDetails compDetail, List<ComlaintDocuments> compDocuments, UsersLastActivities activity) {

        if (propComplaint != null && compDetail != null) {
            entManager.persist(propComplaint);
            entManager.persist(compDetail);

            for (ComlaintDocuments document : compDocuments) {
                entManager.persist(document.getComplaintFileId());
                entManager.persist(document);
            }

            if (activity != null) {
                entManager.persist(activity);
            }

            return propComplaint;
        }

        return null;
    }

    public List<Visitors> fetchAllVisitorsInformation(Date visit_date, Date end_date, Long consultant_id) {

        List<Visitors> visitors_list;
        String query_str = "FROM Visitors v";

        if (consultant_id != null) { //this should always be the case
            query_str += " WHERE v.consultant.id = :conslt";
            if (visit_date != null) {
                visitors_list = entManager.createQuery(query_str + " AND v.created >= :start AND v.created <= :end_", Visitors.class).setParameter("conslt", consultant_id).setParameter("start", visit_date)
                        .setParameter("end_", end_date).getResultList();
            } else {
                visitors_list = entManager.createQuery(query_str, Visitors.class).setParameter("conslt", consultant_id).getResultList();
            }
        } else if (visit_date != null) {
            visitors_list = entManager.createQuery(query_str + " WHERE v.created >= :start AND v.created <= :end_", Visitors.class).setParameter("start", visit_date).setParameter("end_", end_date).getResultList();
        } else {
            visitors_list = entManager.createQuery(query_str, Visitors.class).getResultList();
        }

        return visitors_list;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Visitors saveNewVisitorInformation(Visitors visitor, UsersLastActivities activity, Boolean save_new) {

        if (visitor != null) {
            if (save_new) {
                entManager.persist(visitor);
            } else {
                entManager.merge(visitor);
            }

            if (activity != null) {
                activity.setEntityId(visitor.getId());
                entManager.persist(activity);
            }

            return visitor;
        }

        return null;
    }

    public List<Visitors> fetchVisitorByNameAndDate(Date date_, Boolean only_not_signed_out, String name_) {
        List<Visitors> visitors = null;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<Visitors> lproperty = cq.from(Visitors.class);

        List<Predicate> predicates = new ArrayList<>();

        if (date_ != null) {
            predicates.add(qb.greaterThanOrEqualTo(lproperty.<Date>get("timeIn"), date_));
        }
        if (name_ != null && !name_.isEmpty()) { //this should always be the case
            predicates.add(qb.or(qb.like(lproperty.<String>get("firstName"), "%" + name_ + "%"), qb.like(lproperty.<String>get("lastName"), "%" + name_ + "%")));
        }
        if (only_not_signed_out) {
            predicates.add(qb.isNull(lproperty.<String>get("timeOut")));
        }

        visitors = entManager.createQuery(cq.select(lproperty).where(predicates.toArray(new Predicate[]{})).orderBy(qb.desc(lproperty.get("lastName")))).getResultList();

        return visitors;
    }

    public Visitors findVisitorById(Long visitor_id) {
        Visitors visitor = null;

        if (visitor_id != null) {
            visitor = entManager.createQuery("FROM Visitors v WHERE v.id = :vid_", Visitors.class).setParameter("vid_", visitor_id).getSingleResult();
        }

        return visitor;
    }

    public List<Visitors> filterVisitorsList(String property_id, String address, String visitor_name, String phone, Long purpose_id, Long relationship_id, Date start, Date end_, Long district_id,
            Long lga_id) {
        List<Visitors> visitors = null;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<Visitors> lproperty = cq.from(Visitors.class);

        List<Predicate> predicates = new ArrayList<>();

        if ((property_id != null) && (!property_id.isEmpty())) {
            predicates.add(qb.like(lproperty.<String>get("propertyId"), "%" + property_id + "%"));
        }
        if (start != null) {
            predicates.add(qb.greaterThanOrEqualTo(lproperty.<Date>get("timeIn"), start));
        }
        if (end_ != null) {
            predicates.add(qb.lessThanOrEqualTo(lproperty.<Date>get("timeOut"), end_));
        }
        if (visitor_name != null && !visitor_name.isEmpty()) { //this should always be the case
            predicates.add(qb.or(qb.like(lproperty.<String>get("firstName"), "%" + visitor_name + "%"), qb.like(lproperty.<String>get("lastName"), "%" + visitor_name + "%")));
        }
        if ((address != null) && (!address.isEmpty())) {
            predicates.add(qb.like(lproperty.<String>get("propertyAddress"), "%" + address + "%"));
        }
        if (purpose_id != null) {
            Join<Visitors, VisitPurpose> street = lproperty.join("visitPurposeId");
            predicates.add(qb.equal(street.get("id"), purpose_id));
        }
        if (relationship_id != null) {
            Join<Visitors, ComplainerRelationship> street = lproperty.join("complainerRelationship");
            predicates.add(qb.equal(street.get("id"), relationship_id));
        }
        if (district_id != null) {
            Join<Visitors, LcdaWards> street = lproperty.join("lcdaWardId");
            predicates.add(qb.equal(street.get("id"), district_id));
        }
        if (lga_id != null) {
            Join<Visitors, LcdaWards> street = lproperty.join("lcdaWardId");
            Join<LcdaWards, LocalCouncilDevArea> lcdaJoin = street.join("localCouncilDevAreaId");
            predicates.add(qb.equal(lcdaJoin.get("id"), lga_id));
        }

        visitors = entManager.createQuery(cq.select(lproperty).where(predicates.toArray(new Predicate[]{})).orderBy(qb.desc(lproperty.get("created")))).getResultList();

        return visitors;
    }
}
