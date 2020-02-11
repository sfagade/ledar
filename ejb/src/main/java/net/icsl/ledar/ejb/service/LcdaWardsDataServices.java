package net.icsl.ledar.ejb.service;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import net.icsl.ledar.ejb.enums.ValuationStatusEnum;
import net.icsl.ledar.ejb.model.BareLands;
import net.icsl.ledar.ejb.model.CommercialTypes;

import net.icsl.ledar.ejb.model.EnumeratorWards;
import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.Occupations;
import net.icsl.ledar.ejb.model.Organizations;
import net.icsl.ledar.ejb.model.PersonTitles;
import net.icsl.ledar.ejb.model.ResidentialTypes;
import net.icsl.ledar.ejb.model.ResidentialUseages;
import net.icsl.ledar.ejb.model.ServiceTypes;
import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.model.WardLandProperties;
import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.model.WardTowns;

/**
 * @author sfagade
 * @date Jan 29, 2016
 */
@Stateless
public class LcdaWardsDataServices implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager entManager;

    /**
     * @return the entManager
     */
    public EntityManager getEm() {
        return entManager;
    }

    /**
     * @param em the entManager to set
     */
    public void setEm(EntityManager em) {
        this.entManager = em;
    }

    public List<LcdaWards> fetchAllLcdaWards(Long lcda_id) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<LcdaWards> criteria = cb.createQuery(LcdaWards.class);
        Root<LcdaWards> lcdaWardClass = criteria.from(LcdaWards.class);

        criteria = criteria.multiselect(lcdaWardClass.get("id"), lcdaWardClass.get("wardName"), lcdaWardClass.get("localCouncilDevAreaId"), lcdaWardClass.get("wardSupervisorById"),
                lcdaWardClass.get("created"));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<EnumeratorWards> fetchAllWardEnumerators(List<Long> ward_ids) {

        List<EnumeratorWards> ward_enumerators = entManager.createQuery("from EnumeratorWards e where e.lcdaWardId.id IN :enumWardid", EnumeratorWards.class).setParameter("enumWardid", ward_ids).
                getResultList();

        return ward_enumerators;

    }

    public List<LocalCouncilDevArea> fetchAllSenatorialDistrictLCDAs(Long sen_distrct_id) {

        List<LocalCouncilDevArea> ward_enumerators;

        if (sen_distrct_id != null) {
            ward_enumerators = entManager.createQuery("from LocalCouncilDevArea e where e.senatorialDistrictId.id=:enumWardid ORDER BY e.lcdaName", LocalCouncilDevArea.class).
                    setParameter("enumWardid", sen_distrct_id).getResultList();
        } else {
            ward_enumerators = entManager.createQuery("from LocalCouncilDevArea e ORDER BY e.lcdaName", LocalCouncilDevArea.class).getResultList();
        }

        return ward_enumerators;

    }

    public List<LcdaWards> fetchAllLcdaWardsByLcda(Long lcda_id, Long contractor_id) {

        List<LcdaWards> lcdaWards;

        if ((lcda_id != null) && (contractor_id != null)) {
            lcdaWards = entManager.createQuery("FROM LcdaWards l where l.localCouncilDevAreaId.id=:lcdaId AND l.contractorId.id=:contId ORDER BY l.wardName", LcdaWards.class)
                    .setParameter("contId", contractor_id).setParameter("lcdaId", lcda_id).getResultList();
        } else if (lcda_id != null) {
            lcdaWards = entManager.createQuery("FROM LcdaWards l where l.localCouncilDevAreaId.id=:lcdaId ORDER BY l.wardName, l.localCouncilDevAreaId.lcdaName", LcdaWards.class).setParameter("lcdaId", lcda_id).getResultList();
        } else {
            lcdaWards = entManager.createQuery("FROM LcdaWards l WHERE l.contractorId.id=:contId ORDER BY l.wardName", LcdaWards.class).setParameter("contId", contractor_id).getResultList();
        }

        return lcdaWards;
    }

    public List<LcdaWards> fetchAllSupervisorWards(Long supervisor_ids) {

        List<LcdaWards> supWards = entManager.createQuery("FROM LcdaWards l where l.wardSupervisorById.id = :lcdaId", LcdaWards.class).setParameter("lcdaId", supervisor_ids).getResultList();

        return supWards;

    }

    public LocalCouncilDevArea fetchLocalGovtByCriteria(Long lga_id, String lga_name, Long district_id, String district_name) {

        LocalCouncilDevArea chairman_lcda = null;

        if (lga_id != null) {
            chairman_lcda = entManager.createQuery("FROM LocalCouncilDevArea l where l.id = :lcdaId", LocalCouncilDevArea.class).setParameter("lcdaId", lga_id).getSingleResult();
        } else if(lga_name != null && !lga_name.isEmpty()) {
        	chairman_lcda = entManager.createQuery("FROM LocalCouncilDevArea l where l.lcdaName = :lcdaId", LocalCouncilDevArea.class).setParameter("lcdaId", lga_name).getSingleResult();
        }

        return chairman_lcda;
    }

    public List<WardTowns> fetchWardTowns(Long ward_id) {

        List<WardTowns> wardTowns;

        if (ward_id != null) {
            wardTowns = entManager.createQuery("FROM WardTowns w where w.lcdaWardId.id=:wardId", WardTowns.class).setParameter("wardId", ward_id).getResultList();
        } else {
            wardTowns = entManager.createQuery("FROM WardTowns w", WardTowns.class).getResultList();
        }

        return wardTowns;
    }

    public List<WardStreets> fetchWardStreets(Long ward_id, Long contractor_id, Boolean verified) {

        List<WardStreets> wardTowns;
        CriteriaBuilder cbuilder = getEm().getCriteriaBuilder();
        CriteriaQuery<WardStreets> criteria = cbuilder.createQuery(WardStreets.class);
        Root<WardStreets> lcdaWardClass = criteria.from(WardStreets.class);
        List<Predicate> predicates = new ArrayList<>();

        if (ward_id != null) {
            Join<WardStreets, LcdaWards> orgn = lcdaWardClass.join("lcdaWardId");
            predicates.add(cbuilder.equal(orgn.get("id"), ward_id));
        }
        if (contractor_id != null) {
            Join<WardStreets, LcdaWards> lcda = lcdaWardClass.join("lcdaWardId");
            Join<LcdaWards, Organizations> orgn = lcda.join("contractorId");
            predicates.add(cbuilder.equal(orgn.get("id"), contractor_id));
        }
        if (verified != null) {
            predicates.add(cbuilder.equal(lcdaWardClass.get("isVerified"), verified));
        }

        wardTowns = entManager.createQuery(criteria.select(lcdaWardClass).where(predicates.toArray(new Predicate[]{})).orderBy(cbuilder.asc(lcdaWardClass.get("streetName")))).getResultList();

        return wardTowns;

    }

    @TransactionAttribute(REQUIRES_NEW)
    public List<LcdaWards> fetchAllLcdaWardsByName(String ward_name, Long contractor_id, boolean like_search) {

        List<LcdaWards> lcdaWards = null;

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<LcdaWards> criteria = cb.createQuery(LcdaWards.class);
        Root<LcdaWards> lcdaWardClass = criteria.from(LcdaWards.class);
        EntityType<LcdaWards> wardType_ = entManager.getMetamodel().entity(LcdaWards.class);
        List<Predicate> predicates = new ArrayList<>();

        if (ward_name != null) {
            /**
             * criteria = criteria.multiselect(lcdaWardClass.get("id"),
             * lcdaWardClass.get("wardName"),
             * lcdaWardClass.get("localCouncilDevAreaId"),
             * lcdaWardClass.get("created"));
             */

            if (contractor_id != null) {
                Join<LcdaWards, Organizations> orgn = lcdaWardClass.join("contractorId");
                predicates.add(cb.equal(orgn.get("id"), contractor_id));
            }

            if (like_search) {
                predicates.add(cb.like(cb.upper(lcdaWardClass.get(wardType_.getDeclaredSingularAttribute("wardName", String.class))), "%" + ward_name.toUpperCase() + "%"));
            } else {
                predicates.add(cb.equal(lcdaWardClass.get("wardName"), ward_name));
            }

            lcdaWards = entManager.createQuery(criteria.multiselect(lcdaWardClass.get("id"), lcdaWardClass.get("wardName"), lcdaWardClass.get("localCouncilDevAreaId"),
                    lcdaWardClass.get("created")).where(predicates.toArray(new Predicate[]{}))).getResultList();
        }

        return lcdaWards;
    }

    public List<WardTowns> fetchAllWardTownsByName(String town_name, boolean like_search) {

        List<WardTowns> lcdaWards = null;

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<WardTowns> criteria = cb.createQuery(WardTowns.class);
        Root<WardTowns> lcdaWardClass = criteria.from(WardTowns.class);
        EntityType<WardTowns> wardType_ = entManager.getMetamodel().entity(WardTowns.class);

        if (town_name != null) {
            criteria = criteria.multiselect(lcdaWardClass.get("id"), lcdaWardClass.get("townName"), lcdaWardClass.get("created"));

            if (like_search) {
                criteria = criteria.where(cb.like(cb.upper(lcdaWardClass.get(wardType_.getDeclaredSingularAttribute("townName", String.class))), "%" + town_name.toUpperCase() + "%"));
            } else {
                criteria = criteria.where(cb.equal(lcdaWardClass.get("townName"), town_name));
            }

            lcdaWards = entManager.createQuery(criteria).getResultList();
        }

        return lcdaWards;
    }

    public List<WardLandProperties> fetchAllLandProperties(Long contractor_id, Boolean verified, int startingAt, int maxPerPage) {

        List<WardLandProperties> lndProps;

        if (contractor_id != null) {
            lndProps = entManager.createQuery("FROM WardLandProperties w WHERE w.contractorId.id = :contor ORDER BY w.created DESC", WardLandProperties.class).setParameter("contor", contractor_id)
                    .setFirstResult(startingAt).setMaxResults(maxPerPage).getResultList();

        } else if (verified) {
            lndProps = entManager.createQuery("FROM WardLandProperties w WHERE w.isVerified = true AND w.propertyValuationStatus = :sentEnum ORDER BY w.created ASC", WardLandProperties.class)
                    .setParameter("sentEnum", ValuationStatusEnum.SENT.toString()).setFirstResult(startingAt).setMaxResults(maxPerPage).getResultList();
        } else {
            lndProps = entManager.createQuery("FROM WardLandProperties w ORDER BY w.created DESC", WardLandProperties.class).setFirstResult(startingAt).setMaxResults(maxPerPage).getResultList();
        }

        return lndProps;

    }

    /**
     * Sum the number of players in the DB
     *
     * @param contractor_id
     * @param verified
     * @param status
     * @return an int with the total
     */
    public int countPropertiesTotal(Long contractor_id, Boolean verified, String status) {

        Query query;
        Number result;

        if (contractor_id != null && (status == null)) {
            query = entManager.createQuery("select COUNT(p) from WardLandProperties p WHERE p.contractorId.id = :contor");
            result = (Number) query.setParameter("contor", contractor_id).getSingleResult();
        } else if (verified && status != null) {
            query = entManager.createQuery("select COUNT(p) from WardLandProperties p WHERE p.isVerified = true AND p.propertyValuationStatus = :contor");
            result = (Number) query.setParameter("contor", status).getSingleResult();
        } else if (!verified && status != null) {
            Date current_year = null;
            query = entManager.createQuery("select COUNT(p) from WardLandProperties p WHERE p.propertyValuationStatus <> :contor AND p.contractorId.id = :copoor  AND p.created > :date_");
            try {
                current_year = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).parse("01-Jan-2017");
            } catch (ParseException e) {
                Logger.getLogger(LcdaWardsDataServices.class.getName()).log(Level.SEVERE, "Failed to parse date string ", e.getMessage());
            }
            result = (Number) query.setParameter("contor", status).setParameter("copoor", contractor_id).setParameter("date_", current_year).getSingleResult();
        } else {
            query = entManager.createQuery("select COUNT(p) from WardLandProperties p");
            result = (Number) query.getSingleResult();
        }

        return result.intValue();
    }

    public List<BareLands> fetchAllBareLands(Long consultant_id) {

        List<BareLands> brlands;
        if (consultant_id != null) {
            brlands = entManager.createQuery("FROM BareLands b WHERE b.contractorId.id = :con_id", BareLands.class).setParameter("con_id", consultant_id).getResultList();
        } else {
            brlands = entManager.createQuery("FROM BareLands b", BareLands.class).getResultList();
        }

        return brlands;

    }

    @TransactionAttribute(REQUIRES_NEW)
    public WardStreets saveNewStreetInformation(WardStreets street_, UsersLastActivities activitiy) {

        if (street_.getLcdaWardId() == null) { // we don't have Ward information, the user is expected to provide the Ward name
            if ((street_.getWardName() != null) && (!street_.getWardName().isEmpty())) {
                List<LcdaWards> lcdaWards = this.fetchAllLcdaWardsByName(street_.getWardName(), null, false);
                if ((lcdaWards != null) && (lcdaWards.size() > 0)) {
                    street_.setLcdaWardId(lcdaWards.get(0));
                } else {
                    return null; //we did not find any ward, this is a serious problem
                }
            } else {
                return null; //we can't save street information without the WARD
            }
        }

        if (street_.getTownName() != null) { //we're going to try and find the right town to attached to this record
            List<WardTowns> wardTowns = this.fetchAllWardTownsByName(street_.getTownName(), false);
            if ((wardTowns != null) && (wardTowns.size() > 0)) {
                street_.setWardTownId(wardTowns.get(0));
            }
        }

        try {
            entManager.persist(street_);

            if (activitiy != null) {
                entManager.persist(activitiy);
            }

            return street_;
        } catch (Exception ex) {
            Logger.getLogger(LcdaWardsDataServices.class.getName()).log(Level.SEVERE, "Failed to save Street Info: ", ex);
        }

        return null;
    }

    public WardStreets fetchWardStreetsByNameAndWard(String street_name, String ward_name) {

        WardStreets wardStreet = null;

        if ((street_name != null) && (ward_name != null)) {
            try {
                wardStreet = entManager.createQuery("FROM WardStreets w WHERE w.streetName=:streetname AND w.lcdaWardId.wardName=:wardname", WardStreets.class).setParameter("streetname", street_name).
                        setParameter("wardname", ward_name).getSingleResult();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(LcdaWardsDataServices.class.getName()).log(Level.SEVERE, "Did not find street in Ward: ", nr);
            }
        }

        return wardStreet;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public LcdaWards saveNewLcdaWardInformation(LcdaWards new_ward, UsersLastActivities activity) {

        try {
            if (new_ward != null) {

                entManager.persist(new_ward);
                entManager.persist(activity);

                return new_ward;
            }
        } catch (EJBTransactionRolledbackException cv) {
            Logger.getLogger(LcdaWardsDataServices.class.getName()).log(Level.SEVERE, "Duplicate District entered: " + new_ward.getWardName(), cv.getMessage());
        }

        return null;
    }

    public List<WardLandProperties> fetchAllWardLandPropertyByAreaId(Long street_id, Long ward_id, Long lcda_id, Long officer_id, String val_status, Date startDate, Date endDate, int firstResult,
            String order_dir, String estate_name) {

        List<WardLandProperties> properties = null;
        //String hql_query;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<WardLandProperties> lproperty = cq.from(WardLandProperties.class);

        List<Predicate> predicates = new ArrayList<>();

        try {

            if (street_id != null) {
                Join<WardLandProperties, WardStreets> street = lproperty.join("wardStreetId");
                predicates.add(qb.equal(street.get("id"), street_id));
            } else if ((estate_name != null) && (!estate_name.isEmpty())) {
                Join<WardLandProperties, WardStreets> street = lproperty.join("wardStreetId");
                predicates.add(qb.equal(street.get("estateName"), estate_name));
            } else if (ward_id != null) {
                Join<WardLandProperties, WardStreets> street = lproperty.join("wardStreetId");
                Join<WardStreets, LcdaWards> ward = street.join("lcdaWardId");
                predicates.add(qb.equal(ward.get("id"), ward_id));
            } else if (lcda_id != null) {
                Join<WardLandProperties, WardStreets> street = lproperty.join("wardStreetId");
                Join<WardStreets, LcdaWards> ward = street.join("lcdaWardId");
                Join<LcdaWards, LocalCouncilDevArea> lcda = ward.join("localCouncilDevAreaId");
                predicates.add(qb.equal(lcda.get("id"), lcda_id));
            }

            if (officer_id != null) {
                Join<WardLandProperties, Logindetails> logDetail = lproperty.join("capturedById");
                predicates.add(qb.equal(logDetail.get("id"), officer_id));
            }

            if (val_status != null && !val_status.isEmpty()) {
                predicates.add(qb.equal(lproperty.get("propertyValuationStatus"), val_status));
            }

            if (startDate != null) {
                predicates.add(qb.greaterThanOrEqualTo(lproperty.<Date>get("dateCaptured"), startDate));
            }
            if (endDate != null) {
                predicates.add(qb.lessThanOrEqualTo(lproperty.<Date>get("dateCaptured"), endDate));
            }

            if ((order_dir != null) && (order_dir.equalsIgnoreCase("desc"))) {
                properties = entManager.createQuery(cq.select(lproperty).where(predicates.toArray(new Predicate[]{})).orderBy(qb.desc(lproperty.get("created")))).setMaxResults(400)
                        .setFirstResult(firstResult).getResultList();
            } else {
                properties = entManager.createQuery(cq.select(lproperty).where(predicates.toArray(new Predicate[]{})).orderBy(qb.asc(lproperty.get("created")))).setMaxResults(400)
                        .setFirstResult(firstResult).getResultList();
            }

        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(LcdaWardsDataServices.class.getName()).log(Level.SEVERE, "Did not find property: ", nr);
        }

        return properties;
    }

    public List<WardLandProperties> fetchAllPropertyByPropertyId(String prop_id, boolean legacy, Long contractor_id) {

        List<WardLandProperties> propBills = null;

        String hql_query = "FROM WardLandProperties p ";

        if (prop_id != null) {

            if (legacy) {

                hql_query += "WHERE p.legacyPropertyId LIKE :legacy";
                propBills = entManager.createQuery(hql_query, WardLandProperties.class).setParameter("legacy", "%" + prop_id + "%").getResultList();

            } else if (contractor_id != null) {
                hql_query += "WHERE p.propertyId LIKE :propID AND p.contractorId.id = :contrct ORDER BY p.propertyId";
                propBills = entManager.createQuery(hql_query, WardLandProperties.class).setParameter("propID", "%" + prop_id + "%").setParameter("contrct", contractor_id).getResultList();
            } else {
                hql_query += "WHERE p.propertyId LIKE :propID ORDER BY p.propertyId";
                propBills = entManager.createQuery(hql_query, WardLandProperties.class).setParameter("propID", "%" + prop_id + "%").getResultList();
            }

        }

        return propBills;
    }

    public List<WardStreets> fetchWardStreetsByStreetName(String street_name, Long contractor_id, Boolean exactSearch) {

        List<WardStreets> streets_;

        if (contractor_id != null) {
            streets_ = entManager.createQuery("FROM WardStreets w WHERE w.lcdaWardId.contractorId.id=:concId AND w.streetName LIKE :streetn_ AND w.isVerified = true ORDER BY w.streetName",
                    WardStreets.class).setParameter("concId", contractor_id).setParameter("streetn_", "%" + street_name + "%").getResultList();
        } else {
            streets_ = entManager.createQuery("FROM WardStreets w where w.streetName LIKE :wardId AND w.isVerified = true ORDER BY w.streetName", WardStreets.class).
                    setParameter("wardId", "%" + street_name + "%").getResultList();
        }

        return streets_;

    }

    public List<WardLandProperties> fetchAllIrregularStreetProperties(Long contractor_id) {

        List<WardLandProperties> lndProps;
        if (contractor_id != null) {
            lndProps = entManager.createQuery("FROM WardLandProperties w WHERE w.irregularAddress IS NOT NULL AND w.irregularAddress <> '' AND w.contractorId.id = :contractor ORDER BY "
                    + "w.irregularAddress, w.created DESC",
                    WardLandProperties.class).setParameter("contractor", contractor_id).getResultList();
        } else {
            lndProps = entManager.createQuery("FROM WardLandProperties w WHERE w.irregularAddress IS NOT NULL ORDER BY w.irregularAddress, w.created DESC", WardLandProperties.class).getResultList();
        }

        return lndProps;
    }

    public List<WardLandProperties> fetchAllValuatedProperties(Long contractor_id) {

        List<WardLandProperties> lndProps;

        if (contractor_id != null) {
            lndProps = entManager.createQuery("FROM WardLandProperties w WHERE w.contractorId.id = :contor AND w.propertyValuationStatus = :procStatus ORDER BY w.created DESC", WardLandProperties.class).
                    setParameter("contor", contractor_id).setParameter("procStatus", ValuationStatusEnum.PROCESSED.toString()).getResultList();

        } else {
            lndProps = entManager.createQuery("FROM WardLandProperties w WHERE w.propertyValuationStatus = :procStatus ORDER BY w.created DESC", WardLandProperties.class)
                    .setParameter("procStatus", ValuationStatusEnum.PROCESSED.toString()).getResultList();
        }

        return lndProps;

    }

    public List<String> fetchAllPropertyDistinctStreets(Long contractor_id) {

        List<String> provDelv = new ArrayList<>();

        String hql = "SELECT DISTINCT (w.irregularAddress) FROM WardLandProperties w WHERE w.isIrregularAddress = true ORDER BY w.irregularAddress";

        if (contractor_id != null) {

        } else {
            provDelv = entManager.createQuery(hql, String.class).getResultList();

        }

        return provDelv;

    }

    public List<WardStreets> fetchAllWardStreetsByName(String street_name, Boolean like_search, Boolean verified) {

        List<WardStreets> wardStreets = null;

        try {
            if ((street_name != null) && (like_search)) {
                wardStreets = entManager.createQuery("FROM WardStreets w WHERE w.streetName LIKE :streetname AND w.isVerified =:verified", WardStreets.class).
                        setParameter("streetname", "%" + street_name + "%").setParameter("verified", verified).getResultList();

            } else if (street_name != null) {
                wardStreets = entManager.createQuery("FROM WardStreets w WHERE w.streetName=:streetname AND w.isVerified =:verified", WardStreets.class).setParameter("streetname", street_name).
                        setParameter("verified", verified).getResultList();

            }
        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(LcdaWardsDataServices.class.getName()).log(Level.SEVERE, "Did not find street with given name: ", nr);
        }

        return wardStreets;
    }

    public List<WardLandProperties> fetchAllPropertyByIrregularStreetName(String street_name, boolean like_search) {

        List<WardLandProperties> properties = null;

        String hql_query = "FROM WardLandProperties p ";

        try {
            if ((street_name != null) && (like_search)) {
                properties = entManager.createQuery(hql_query + "WHERE p.irregularAddress LIKE :legacy", WardLandProperties.class).setParameter("legacy", "%" + street_name + "%").getResultList();
            } else {
                properties = entManager.createQuery(hql_query + "WHERE p.irregularAddress = :legacy", WardLandProperties.class).setParameter("legacy", street_name).getResultList();

            }
        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(LcdaWardsDataServices.class.getName()).log(Level.SEVERE, "Did not find property with given name: ", nr);
        }

        return properties;
    }

    public WardStreets findWardStreetByStreetId(Long street_id) {

        WardStreets street = null;

        if (street_id != null) {
            street = entManager.createQuery("FROM WardStreets ws WHERE ws.id = :streetID", WardStreets.class).setParameter("streetID", street_id).getSingleResult();
        }

        return street;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public boolean deleteUnverifiedField(Long street_id, UsersLastActivities activity) {

        if (street_id != null) {
            entManager.remove(this.findWardStreetByStreetId(street_id));

            if (activity != null) {
                entManager.persist(activity);
            }

            return true;
        }

        return false;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public WardStreets updateWardStreetInfo(WardStreets street, UsersLastActivities activity) {

        if (street != null) {
            entManager.merge(street);

            if (activity != null) {
                entManager.persist(activity);
            }

            return street;
        }

        return null;
    }

    public Long countPhotosWithBlob() {

        Query query;
        Long result;

        query = entManager.createQuery("select COUNT(f) from FileUploads f WHERE f.fileContent IS NOT NULL");
        result = (Long) query.getSingleResult();

        return result;
    }

    public List<FileUploads> fetchBlobPicturesBySize(int batch_size) {

        List<FileUploads> pictures = null;

        if (batch_size > 0) { //I want to limit the size of pictures I pic
            pictures = entManager.createQuery("FROM FileUploads f WHERE f.fileContent IS NOT NULL ORDER BY f.created", FileUploads.class).
                    setMaxResults(batch_size).getResultList();
        }

        return pictures;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public FileUploads updateFileUploadInfo(FileUploads file_upload, UsersLastActivities activity) {

        if (file_upload != null) {
            entManager.merge(file_upload);

            if (activity != null) {
                entManager.persist(activity);
            }

            return file_upload;
        }

        return null;
    }

    public Long countPhotosWithoutBlob() {

        Query query;
        Long result;

        query = entManager.createQuery("select COUNT(f) from FileUploads f WHERE f.fileContent IS NULL");
        result = (Long) query.getSingleResult();

        return result;
    }

    public List<String[]> fetchDuplicateProperty(Long contractor_id) {

        List<String[]> provDelv = new ArrayList<>();

        String hql = "SELECT w.propertyId, w.wardStreetId.streetName, w.irregularAddress, COUNT(*) FROM WardLandProperties w GROUP BY w.propertyId, w.wardStreetId.streetName, w.irregularAddress "
                + "HAVING  COUNT(*) > 1";

        if (contractor_id != null) {

        } else {
            provDelv = entManager.createQuery(hql).getResultList();

        }

        return provDelv;

    }

    public long countFilteredPropertyResults(Long street_id, Long ward_id, Long lcda_id, Long officer_id, String val_status, Date startDate, Date endDate, String order_dir, String estate_name) {

        long record_count = 0;
        //String hql_query;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<WardLandProperties> lproperty = cq.from(WardLandProperties.class);

        List<Predicate> predicates = new ArrayList<>();

        try {

            if (street_id != null) {
                Join<WardLandProperties, WardStreets> street = lproperty.join("wardStreetId");
                predicates.add(qb.equal(street.get("id"), street_id));
            } else if ((estate_name != null) && (!estate_name.isEmpty())) {
                Join<WardLandProperties, WardStreets> street = lproperty.join("wardStreetId");
                predicates.add(qb.equal(street.get("estateName"), estate_name));
            } else if (ward_id != null) {
                Join<WardLandProperties, WardStreets> street = lproperty.join("wardStreetId");
                Join<WardStreets, LcdaWards> ward = street.join("lcdaWardId");
                predicates.add(qb.equal(ward.get("id"), ward_id));
            } else if (lcda_id != null) {
                Join<WardLandProperties, WardStreets> street = lproperty.join("wardStreetId");
                Join<WardStreets, LcdaWards> ward = street.join("lcdaWardId");
                Join<LcdaWards, LocalCouncilDevArea> lcda = ward.join("localCouncilDevAreaId");
                predicates.add(qb.equal(lcda.get("id"), lcda_id));
            }

            if (officer_id != null) {
                Join<WardLandProperties, Logindetails> logDetail = lproperty.join("capturedById");
                predicates.add(qb.equal(logDetail.get("id"), officer_id));
            }

            if (val_status != null && !val_status.isEmpty()) {
                predicates.add(qb.equal(lproperty.get("propertyValuationStatus"), val_status));
            }

            if (startDate != null) {
                predicates.add(qb.greaterThanOrEqualTo(lproperty.<Date>get("dateCaptured"), startDate));
            }
            if (endDate != null) {
                predicates.add(qb.lessThanOrEqualTo(lproperty.<Date>get("dateCaptured"), endDate));
            }

            record_count = (long) entManager.createQuery(cq.select(qb.count(lproperty)).where(predicates.toArray(new Predicate[]{}))).getSingleResult();

        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(LcdaWardsDataServices.class.getName()).log(Level.SEVERE, "Did not find property: ", nr);
        }

        return record_count;
    }

    public List<WardStreets> fetchWardStreetsByEstateName(String etstae_name, Long contractor_id, Boolean exact_search) {

        List<WardStreets> streets_;

        if (!exact_search) {
            if (contractor_id != null) {
                streets_ = entManager.createQuery("FROM WardStreets w WHERE w.lcdaWardId.contractorId.id=:concId AND w.estateName LIKE :streetn_ AND w.isVerified = true ORDER BY w.streetName",
                        WardStreets.class).setParameter("concId", contractor_id).setParameter("streetn_", "%" + etstae_name + "%").getResultList();
            } else {
                streets_ = entManager.createQuery("FROM WardStreets w where w.estateName LIKE :wardId AND w.isVerified = true ORDER BY w.streetName", WardStreets.class).
                        setParameter("wardId", "%" + etstae_name + "%").getResultList();
            }

        } else if (contractor_id != null) {
            streets_ = entManager.createQuery("FROM WardStreets w WHERE w.lcdaWardId.contractorId.id=:concId AND w.estateName = :streetn_ AND w.isVerified = true ORDER BY w.streetName",
                    WardStreets.class).setParameter("concId", contractor_id).setParameter("streetn_", etstae_name).getResultList();
        } else {
            streets_ = entManager.createQuery("FROM WardStreets w where w.estateName = :wardId AND w.isVerified = true ORDER BY w.streetName", WardStreets.class).
                    setParameter("wardId", etstae_name).getResultList();
        }

        return streets_;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public CommercialTypes createNewCommercialType(CommercialTypes type, UsersLastActivities activity) {
        if (type != null) {
            entManager.persist(type);
            if (activity != null) {
                entManager.persist(activity);
            }

            return type;
        }
        return null;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Occupations createNewOccupations(Occupations occptn, UsersLastActivities activity) {
        if (occptn != null) {
            entManager.persist(occptn);
            if (activity != null) {
                entManager.persist(activity);
            }

            return occptn;
        }
        return null;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public ResidentialTypes createNewResidentialTypes(ResidentialTypes rdtlType, UsersLastActivities activity) {
        if (rdtlType != null) {
            entManager.persist(rdtlType);
            if (activity != null) {
                entManager.persist(activity);
            }

            return rdtlType;
        }
        return null;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public ResidentialUseages createNewResidentialUsage(ResidentialUseages rdtuse, UsersLastActivities activity) {
        if (rdtuse != null) {
            entManager.persist(rdtuse);
            if (activity != null) {
                entManager.persist(activity);
            }

            return rdtuse;
        }
        return null;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public ServiceTypes createNewServiceTypes(ServiceTypes srviTpe, UsersLastActivities activity) {
        if (srviTpe != null) {
            entManager.persist(srviTpe);
            if (activity != null) {
                entManager.persist(activity);
            }

            return srviTpe;
        }
        return null;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public PersonTitles createNewPersonTitles(PersonTitles srviTpe, UsersLastActivities activity) {
        if (srviTpe != null) {
            entManager.persist(srviTpe);
            if (activity != null) {
                entManager.persist(activity);
            }

            return srviTpe;
        }
        return null;
    }
}
