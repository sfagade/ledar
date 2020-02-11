package net.icsl.ledar.ejb.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import net.icsl.ledar.ejb.enums.PaymentStatusEnum;
import net.icsl.ledar.ejb.model.BillPayments;
import net.icsl.ledar.ejb.model.BillsDeliveryFiles;
import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.Organizations;
import net.icsl.ledar.ejb.model.PrintedBills;
import net.icsl.ledar.ejb.model.UsersLastActivities;

/**
 *
 * @author sfagade
 */
@Stateless
public class PrintedBillsService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager entManager;

    public Object[] fetchPrintedBillsSummary(Long contractor_id, String lcda_name) {

        Object[] results;
        String qlString = "SELECT SUM(p.amountDue), SUM(p.luc), SUM(p.amtLatepay1), SUM(p.amtLatepay2), SUM(p.amtLatepay3), Count(p.propertyIdn), Count(p) FROM PrintedBills p";
        Query query;
        if (lcda_name != null) {
            query = entManager.createQuery(qlString + " WHERE p.lga = :lga_").setParameter("lga_", lcda_name);
            results = (Object[]) query.getSingleResult();
        } else if (contractor_id != null) { //TODO I need to implement block for consultant query
            query = entManager.createQuery(qlString + " WHERE p.consultantId.id = :consultt").setParameter("consultt", contractor_id);
            results = (Object[]) query.getSingleResult();
        } else { //we're getting all data
            query = entManager.createQuery(qlString);
            results = (Object[]) query.getSingleResult();
        }

        return results;
    }

    public List<PrintedBills> fetchAllPrintedBills(Long contractor_id, int startingAt, int maxPerPage, Boolean deleted_) {

        List<PrintedBills> pBills;

        if ((contractor_id != null) && (maxPerPage > 0)) {
            pBills = entManager.createQuery("FROM PrintedBills w WHERE w.consultantId.id = :contor AND w.isDeleted = :del ORDER BY w.propertyIdn DESC", PrintedBills.class).setParameter("contor", contractor_id)
                    .setParameter("del", deleted_).setFirstResult(startingAt).setMaxResults(maxPerPage).getResultList();

        } else if (contractor_id != null) {
            pBills = entManager.createQuery("FROM PrintedBills w WHERE w.consultantId.id = :contor AND w.isDeleted ORDER BY w.propertyIdn DESC", PrintedBills.class)
                    .setParameter("contor", contractor_id).getResultList();
        } else {
            pBills = entManager.createQuery("FROM PrintedBills w ORDER BY w.propertyIdn DESC", PrintedBills.class).setFirstResult(startingAt).setMaxResults(maxPerPage).getResultList();
        }

        return pBills;

    }

    public int countBillsTotal(Long contractor_id, String status) {

        Query query;
        Number result;

        if (contractor_id != null && status == null) {
            query = entManager.createQuery("select COUNT(p) from PrintedBills p WHERE p.consultantId.id = :contor");
            result = (Number) query.setParameter("contor", contractor_id).getSingleResult();
        } else if (status != null && contractor_id != null) {
            if (status.equals("has payment") || status.equals("no payment")) {
                query = entManager.createQuery("select COUNT(p) from PrintedBills p WHERE p.consultantId.id = :contor AND p.hasMatchPayment = :param");
                result = (Number) query.setParameter("param", status.equals("has payment")).setParameter("contor", contractor_id).getSingleResult();
            } else {
                query = entManager.createQuery("select COUNT(p) from PrintedBills p WHERE p.consultantId.id = :contor AND p.paymentStatus <> :param");
                result = (Number) query.setParameter("param", status).setParameter("contor", contractor_id).getSingleResult();
            }

        } else {
            query = entManager.createQuery("select COUNT(p) from PrintedBills p");
            result = (Number) query.getSingleResult();
        }

        return result.intValue();
    }

    public List<Object[]> fetchBillsStreetByName(String street_name, String district_name, boolean like_search) {

        List<Object[]> streetNames = null;

        if (street_name != null) { //this should always be the case
            String hsql = "SELECT DISTINCT p.streetName, p.districtName, p.lga FROM PrintedBills p";

            if (like_search) {
                if (district_name != null) {
                    streetNames = entManager.createQuery(hsql + " WHERE p.streetName LIKE :str_name AND p.districtName = :district ORDER BY p.streetName").setParameter("str_name", "%" + street_name
                            + "%").setParameter("district", district_name).getResultList();
                } else {
                    streetNames = entManager.createQuery(hsql + " WHERE p.streetName LIKE :str_name ORDER BY p.streetName").setParameter("str_name", "%" + street_name + "%").getResultList();
                }
            } else {
                if (district_name != null) {
                    streetNames = entManager.createQuery(hsql + " WHERE p.streetName = :str_name AND p.districtName = :district ORDER BY p.streetName").setParameter("district", district_name)
                            .setParameter("str_name", street_name).getResultList();
                } else {
                    streetNames = entManager.createQuery(hsql + " WHERE p.streetName = :str_name ORDER BY p.streetName").setParameter("str_name", street_name).getResultList();
                }
            }

        }

        return streetNames;
    }

    public List<Object[]> fetchBillsDistrictByName(Map<String, String> criteria) {

        List<Object[]> districtNames = null;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<PrintedBills> lproperty = cq.from(PrintedBills.class);

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.containsKey("district")) {
            if (criteria.get("like_search").equals("yes")) {
                predicates.add(qb.like(lproperty.<String>get("districtName"), "%" + criteria.get("district") + "%"));
            } else {
                predicates.add(qb.equal(lproperty.get("districtName"), criteria.get("district")));
            }
        }

        if (criteria.containsKey("lga")) {
            predicates.add(qb.equal(lproperty.get("lga"), criteria.get("lga")));
        }

        districtNames = entManager.createQuery(cq.multiselect(lproperty.get("districtName"), lproperty.get("lga")).distinct(true).where(predicates.toArray(new Predicate[]{}))).getResultList();

        return districtNames;
    }

    public List<Object[]> fetchBillsDistrictByLgName(String lg_name) {

        List<Object[]> districtNames = null;

        if (lg_name != null) { //this should always be the case
            String hsql = "SELECT DISTINCT (p.districtName),p.lga FROM PrintedBills p WHERE p.lga = :str_name ORDER BY p.districtName";

            districtNames = entManager.createQuery(hsql).setParameter("str_name", lg_name).getResultList();
        }

        return districtNames;
    }

    public PrintedBills findSingleBillByArea(Long contractor_id, String street_name, String district_name) {

        PrintedBills printed_bill = null;
        List<PrintedBills> bills = null;
        String hquery = "FROM PrintedBills p";

        if (street_name != null) {
            bills = entManager.createQuery(hquery + " WHERE p.streetName = :contor", PrintedBills.class).setParameter("contor", street_name).setMaxResults(2).getResultList();
            //printed_bill = bills.get(0); //I only want to return one var
        } else if (district_name != null) {
            bills = entManager.createQuery(hquery + " WHERE p.districtName = :contor", PrintedBills.class).setParameter("contor", district_name).setMaxResults(2).getResultList();
            //printed_bill = bills.get(0);
        } else if (contractor_id != null) { //I may neva need this
            bills = entManager.createQuery(hquery + " WHERE p.consultantId.id = :contor", PrintedBills.class).setParameter("contor", contractor_id).setMaxResults(2).getResultList();
        }

        if (bills != null && bills.size() > 0) {
            printed_bill = bills.get(0);
        }

        return printed_bill;

    }

    @TransactionAttribute(REQUIRES_NEW)
    public List<PrintedBills> fetchPaymentBills(String bank_code, String billing_year) {
        List<PrintedBills> bills;
        String hquery = "FROM PrintedBills p";

        if (bank_code != null) {
            bills = entManager.createQuery(hquery + " WHERE p.bankPaymentCode = :contor AND p.billingYear = :year_ AND p.isDeleted = false", PrintedBills.class).setParameter("contor", bank_code)
                    .setParameter("year_", billing_year).getResultList();
        } else {
            bills = entManager.createQuery(hquery + " WHERE p.billingYear = :year_", PrintedBills.class).setParameter("year_", billing_year).getResultList();
        }

        return bills;
    }

    public FileUploads saveNewFileUpload(FileUploads file_upload) {

        if (file_upload != null) {
            entManager.persist(file_upload);
            return file_upload;
        }

        return null;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public BillPayments saveBillPaymentInformation(BillPayments payment, PrintedBills bill, Boolean new_entity) {

        if (payment != null) { //this should always be the case
            if (new_entity) {
                entManager.persist(payment);
            } else {
                entManager.merge(payment);
            }
            if (bill != null) {
                entManager.merge(bill);
            }

            return payment;
        }

        return null;
    }

    public List<BillPayments> fetchAllBillsPayments(Map<String, String> criteria, int startingAt, int maxPerPage, List<String> sortFields, String order_dir) {

        List<BillPayments> pBills;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<BillPayments> lproperty = cq.from(BillPayments.class);

        List<Predicate> predicates = new ArrayList<>();
        List<Order> orderList = new ArrayList();
        int max_result = (maxPerPage > 0) ? maxPerPage : 2000;

        DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        if (criteria.containsKey("consultant")) {
            //Join<BillPayments, PrintedBills> priBillObj = lproperty.join("printedBillId", JoinType.LEFT);
            //Join<BillPayments, Organizations> orgnObj = lproperty.join("consultantId", JoinType.LEFT);
            //predicates.add(qb.equal(orgnObj.get("id"), Long.valueOf(criteria.get("consultant"))));
        }

        if (criteria.containsKey("district")) {
            predicates.add(qb.equal(lproperty.get("districtName"), criteria.get("district")));
        }
        if (criteria.containsKey("lga_name")) {
            predicates.add(qb.equal(lproperty.get("lgaName"), criteria.get("lga_name")));
        }

        if (criteria.containsKey("bill_status")) {
            predicates.add(qb.equal(lproperty.get("paymentStatus"), criteria.get("bill_status")));
        }

        if (criteria.containsKey("payment_id")) {
            predicates.add(qb.equal(lproperty.get("id"), Long.valueOf(criteria.get("payment_id"))));
        }

        if (criteria.containsKey("payer_id")) {
            predicates.add(qb.like(lproperty.<String>get("vPayerId"), "%" + criteria.get("payer_id") + "%"));
        }
        if (criteria.containsKey("payer_name")) {
            predicates.add(qb.like(lproperty.<String>get("vPayerName"), "%" + criteria.get("payer_name") + "%"));
        }
        if (criteria.containsKey("receipt")) {
            predicates.add(qb.like(lproperty.<String>get("transRef"), "%" + criteria.get("receipt") + "%"));
        }
        if (criteria.containsKey("property_id")) {
            Join<BillPayments, PrintedBills> street = lproperty.join("printedBillId");
            predicates.add(qb.like(street.<String>get("propertyIdn"), "%" + criteria.get("property_id") + "%"));
        }
        if (criteria.containsKey("processed")) {
            predicates.add(qb.equal(lproperty.<Boolean>get("isProcessed"), criteria.get("processed").equals("yes")));
        }
        if (criteria.containsKey("duplicate")) {
            predicates.add(qb.equal(lproperty.<Boolean>get("isDuplicate"), criteria.get("duplicate").equals("yes")));
        }
        if (criteria.containsKey("year")) {
            Join<BillPayments, PrintedBills> street = lproperty.join("printedBillId");
            predicates.add(qb.equal(street.<String>get("billingYear"), criteria.get("year")));
        }
        try {
            if (criteria.containsKey("start_payment")) {
                predicates.add(qb.greaterThanOrEqualTo(lproperty.<Date>get("entryDate"), shortFormat.parse(criteria.get("start_payment"))));
            }
            if (criteria.containsKey("end_payment")) {
                predicates.add(qb.lessThanOrEqualTo(lproperty.<Date>get("entryDate"), shortFormat.parse(criteria.get("end_payment"))));
            }
        } catch (ParseException ex) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (sortFields != null && sortFields.size() > 0) {
            for (String field : sortFields) {
                orderList.add(qb.desc(lproperty.get(field)));
            }
        }

        pBills = entManager.createQuery(cq.select(lproperty).where(predicates.toArray(new Predicate[]{})).orderBy(orderList)).setMaxResults(max_result).setFirstResult(startingAt).getResultList();

        return pBills;

    }

    public int countTotalPayments(Map<String, String> criteria) {

        Number record_count = 0;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<BillPayments> lproperty = cq.from(BillPayments.class);

        List<Predicate> predicates = new ArrayList<>();
        DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        if (criteria.containsKey("consultant")) {
            //Join<BillPayments, PrintedBills> priBillObj = lproperty.join("printedBillId");
            //Join<PrintedBills, Organizations> orgnObj = priBillObj.join("consultantId");
            // predicates.add(qb.equal(orgnObj.get("id"), Long.valueOf(criteria.get("consultant"))));
        }

        if (criteria.containsKey("district")) {
            predicates.add(qb.equal(lproperty.get("districtName"), criteria.get("district")));
        }
        if (criteria.containsKey("lga_name")) {
            predicates.add(qb.equal(lproperty.get("lgaName"), criteria.get("lga_name")));
        }

        if (criteria.containsKey("bill_status")) {
            predicates.add(qb.equal(lproperty.get("paymentStatus"), criteria.get("bill_status")));
        }

        if (criteria.containsKey("payment_id")) {
            predicates.add(qb.equal(lproperty.get("id"), Long.valueOf(criteria.get("payment_id"))));
        }

        if (criteria.containsKey("payer_id")) {
            predicates.add(qb.like(lproperty.<String>get("vPayerId"), "%" + criteria.get("payer_id") + "%"));
        }
        if (criteria.containsKey("payer_name")) {
            predicates.add(qb.like(lproperty.<String>get("vPayerName"), "%" + criteria.get("payer_name") + "%"));
        }
        if (criteria.containsKey("receipt")) {
            predicates.add(qb.like(lproperty.<String>get("transRef"), "%" + criteria.get("receipt") + "%"));
        }
        if (criteria.containsKey("property_id")) {
            Join<BillPayments, PrintedBills> street = lproperty.join("printedBillId");
            predicates.add(qb.like(street.<String>get("propertyIdn"), "%" + criteria.get("property_id") + "%"));
        }
        if (criteria.containsKey("processed")) {
            predicates.add(qb.equal(lproperty.<Boolean>get("isProcessed"), criteria.get("processed").equals("yes")));
        }
        if (criteria.containsKey("duplicate")) {
            predicates.add(qb.equal(lproperty.<Boolean>get("isDuplicate"), criteria.get("duplicate").equals("yes")));
        }
        if (criteria.containsKey("year")) {
            Join<BillPayments, PrintedBills> street = lproperty.join("printedBillId");
            predicates.add(qb.equal(street.<String>get("billingYear"), criteria.get("year")));
        }
        try {
            if (criteria.containsKey("start_payment")) {
                predicates.add(qb.greaterThanOrEqualTo(lproperty.<Date>get("entryDate"), shortFormat.parse(criteria.get("start_payment"))));
            }
            if (criteria.containsKey("end_payment")) {
                predicates.add(qb.lessThanOrEqualTo(lproperty.<Date>get("entryDate"), shortFormat.parse(criteria.get("end_payment"))));
            }
        } catch (ParseException ex) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, null, ex);
        }

        record_count = (long) entManager.createQuery(cq.select(qb.count(lproperty)).where(predicates.toArray(new Predicate[]{}))).getSingleResult();

        return record_count.intValue();
    }

    public List<Object[]> fetchDuplicateBillsSummary(Long contractor_id, String billing_year) {

        List<Object[]> results = null;
        String qlString = "SELECT b.bankPaymentCode, b.billingYear, COUNT(*) FROM PrintedBills b WHERE b.isDeleted = false AND b.billingYear = :year";

        try {
            if (contractor_id != null) { //billing year should always be set
                results = entManager.createQuery(qlString + " AND b.consultantId.id = :consul GROUP BY b.billingYear, b.bankPaymentCode HAVING  COUNT(*) > 1").setParameter("year", billing_year)
                        .setParameter("consul", contractor_id).getResultList();
            } else { //we're getting all data
                results = entManager.createQuery(qlString + " GROUP BY b.billingYear, b.bankPaymentCode HAVING  COUNT(*) > 1").setParameter("year", billing_year).getResultList();
            }
        } catch (javax.persistence.NoResultException ne) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, "No Duplicate bills found: ", ne);
        }

        return results;
    }

    public List<Object[]> fetchDuplicatePayments(Long contractor_id, Boolean findDups) {

        List<Object[]> results = null;
        String qlString;

        if (contractor_id != null) {

        } else { //we're getting all data
            if (findDups != null) {
                qlString = "SELECT b.vPayerName, b.vPayerId, b.valueDate, b.crAmount, b.shortName, count(*) FROM BillPayments b WHERE b.isDuplicate = :dupi GROUP BY b.vPayerName, b.vPayerId, b.valueDate, b.crAmount, "
                        + "b.shortName HAVING COUNT(*) > 1";
                results = entManager.createQuery(qlString).setParameter("dupi", findDups).getResultList();
            } else {
                qlString = "SELECT b.vPayerName, b.vPayerId, b.valueDate, b.crAmount, b.shortName, count(*) FROM BillPayments b GROUP BY b.vPayerName, b.vPayerId, b.valueDate, b.crAmount, "
                        + "b.shortName HAVING COUNT(*) > 1";
                results = entManager.createQuery(qlString).getResultList();
            }
        }

        return results;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public List<BillPayments> fetchDuplicateRecord(String payer_id, String payer_name, BigDecimal amount, Date valueDate) {

        List<BillPayments> dupBills = null;
        String hql_string = "FROM BillPayments b";

        try {
            if ((payer_id != null) && (!payer_id.isEmpty()) && (payer_name != null) && (!payer_name.isEmpty()) && (amount != null) && (valueDate != null)) {
                dupBills = entManager.createQuery(hql_string + " WHERE b.vPayerName = :payName AND b.vPayerId = :payID AND b.valueDate = :vdate AND b.crAmount = :cr").setParameter("payName", payer_name)
                        .setParameter("payID", payer_id).setParameter("vdate", valueDate).setParameter("cr", amount).getResultList();
            }
        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.INFO, "No duplicate record found ");
        }

        return dupBills;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public BillPayments findBillsPaymentById(Long payment_id, String trans_ref) {

        BillPayments pBills = null;

        try {
            if (payment_id != null) {
                pBills = entManager.createQuery("FROM BillPayments b WHERE b.id = :contor", BillPayments.class).setParameter("contor", payment_id).getSingleResult();
            } else if (trans_ref != null) {
                pBills = entManager.createQuery("FROM BillPayments b WHERE b.transRef = :contor AND b.isDuplicate = false", BillPayments.class).setParameter("contor", trans_ref).getSingleResult();
            }
        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.INFO, "No Prior Payment: ");
        }

        return pBills;

    }

    public int countTotalDeliveredBill(Long contractor_id, String lga_name, Boolean status) {

        Query query;
        Number result;

        if (lga_name != null) {
            if (status != null) {
                query = entManager.createQuery("select COUNT(p) from PrintedBills p WHERE p.isDelivered = :dlvr AND p.lga = :lga");
                result = (Number) query.setParameter("dlvr", status).setParameter("lga", lga_name).getSingleResult();
            } else {
                query = entManager.createQuery("select COUNT(p) from PrintedBills p WHERE p.lga = :lga");
                result = (Number) query.setParameter("lga", lga_name).getSingleResult();
            }
        } else if (contractor_id != null) {
            if (status != null) {
                query = entManager.createQuery("select COUNT(p) from PrintedBills p WHERE p.consultantId.id = :contor AND p.isDelivered = :dlvr");
                result = (Number) query.setParameter("contor", contractor_id).setParameter("dlvr", status).getSingleResult();
            } else {
                query = entManager.createQuery("select COUNT(p) from PrintedBills p WHERE p.consultantId.id = :contor");
                result = (Number) query.setParameter("contor", contractor_id).getSingleResult();
            }
        } else {
            query = entManager.createQuery("select COUNT(p) from PrintedBills p");
            result = (Number) query.getSingleResult();
        }

        return result.intValue();
    }

    @TransactionAttribute(REQUIRES_NEW)
    public List<BillPayments> fetchAllBillsPaymentByBillId(Long bill_id, Boolean fetch_dups) {

        List<BillPayments> pBills = null;

        if (bill_id != null) {
            try {
                if (fetch_dups != null) { //user is interested in this
                    pBills = entManager.createQuery("FROM BillPayments b WHERE b.printedBillId.id = :contor AND b.isDuplicate = :dupf ORDER BY b.sysDate ASC", BillPayments.class)
                            .setParameter("contor", bill_id).setParameter("dupf", fetch_dups).getResultList();
                } else {
                    pBills = entManager.createQuery("FROM BillPayments b WHERE b.printedBillId.id = :contor ORDER BY b.sysDate ASC", BillPayments.class).setParameter("contor", bill_id).getResultList();
                }
            } catch (javax.persistence.NoResultException ne) {
                Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, "No Payment found for Bill: ", ne);
            }
        }

        return pBills;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public List<PrintedBills> fetchAllBillsForProcessing(Map<String, String> criteria) {

        List<PrintedBills> payments = null;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<PrintedBills> lproperty = cq.from(PrintedBills.class);

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.containsKey("district")) {
            predicates.add(qb.equal(lproperty.get("districtName"), criteria.get("district")));
        } else if (criteria.containsKey("lga_name")) {
            predicates.add(qb.equal(lproperty.get("lga"), criteria.get("lga_name")));
        }

        if (criteria.containsKey("contractor_id")) {
            Join<PrintedBills, Organizations> street = lproperty.join("consultantId");
            predicates.add(qb.equal(street.get("id"), Long.valueOf(criteria.get("contractor_id"))));
        }

        if (criteria.containsKey("status_name")) {
            predicates.add(qb.equal(lproperty.get("paymentStatus"), criteria.get("status_name")));
        }
        if (criteria.containsKey("has_payment")) {
            predicates.add(qb.equal(lproperty.get("hasMatchPayment"), criteria.get("has_payment").equals("true"))); //we only need to process bills that have payments attached to them
        }
        if (criteria.containsKey("delivered")) {
            predicates.add(qb.equal(lproperty.get("isDelivered"), criteria.get("delivered").equals("yes"))); //we only need to process bills that have payments attached to them
        }

        if (criteria.containsKey("is_deleted")) {
            predicates.add(qb.equal(lproperty.get("isDeleted"), criteria.get("is_deleted").equals("yes")));
        }

        if (criteria.containsKey("bill_id")) {
            predicates.add(qb.equal(lproperty.get("id"), Long.valueOf(criteria.get("bill_id"))));
        }

        try {
            payments = entManager.createQuery(cq.select(lproperty).where(predicates.toArray(new Predicate[]{})).orderBy(qb.asc(lproperty.get("lga")))).getResultList();

        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, "Did not find property: ", nr);
        }

        return payments;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Boolean saveUpdateBillInformation(PrintedBills billInfo, UsersLastActivities activity) {

        if (billInfo != null) { //this should always be the case
            if (billInfo.getId() != null) { //this is a bill that existed b4, we need to perform an update
                entManager.merge(billInfo);
            } else { //save new bill info
                entManager.persist(billInfo);
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public List<PrintedBills> fetchBillsByPayerIdOrAddress(String payer_id, String address, String street_name, Boolean deleted_) {
        List<PrintedBills> bills = null;
        String hquery = "FROM PrintedBills p";

        if (payer_id != null && !payer_id.isEmpty()) {
            bills = entManager.createQuery(hquery + " WHERE p.payerId = :contor AND p.isDeleted = :deleted", PrintedBills.class).setParameter("contor", payer_id).setParameter("deleted", deleted_)
                    .getResultList();
        } else if ((address != null) && (!address.isEmpty())) {
            bills = entManager.createQuery(hquery + " WHERE p.ownerAddress = :addr_ AND p.isDeleted = :deleted", PrintedBills.class).setParameter("addr_", address).setParameter("deleted", deleted_)
                    .getResultList();
        } else if ((street_name != null) && (!street_name.isEmpty())) {
            bills = entManager.createQuery(hquery + " WHERE p.streetName = :addr_ AND p.isDeleted = :deleted", PrintedBills.class).setParameter("addr_", street_name).setParameter("deleted", deleted_)
                    .getResultList();
        }

        return bills;
    }

    public List<BillsDeliveryFiles> fetchBillsDeliveryFilesByBillId(Long bill_id) {

        List<BillsDeliveryFiles> bills = null;

        if (bill_id != null) {
            try {
                bills = entManager.createQuery("FROM BillsDeliveryFiles b WHERE b.printedBillId.id = :bill", BillsDeliveryFiles.class).setParameter("bill", bill_id).getResultList();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, "Did not find any delivery file: ", nr);
            }
        }

        return bills;
    }

    public Long countBillPaymentByStatus(Long contractor_id, String lga_name, String status) {

        String query = "select COUNT(p) from PrintedBills p";
        Long result = 0L;

        if ((status != null) && (!status.isEmpty())) {
            switch (status) {
                case "payments":
                    if (lga_name != null) {
                        result = (Long) entManager.createQuery(query + " WHERE p.hasMatchPayment = true AND p.lga = :contr").setParameter("contr", lga_name).getSingleResult();
                    } else if (contractor_id != null) {
                        result = (Long) entManager.createQuery(query + " WHERE p.hasMatchPayment = true AND p.consultantId.id = :contr").setParameter("contr", contractor_id).getSingleResult();
                    } else {
                        result = (Long) entManager.createQuery(query + " WHERE p.hasMatchPayment = true").getSingleResult();
                    }
                    break;
                case "partial":
                    if (lga_name != null) {
                        result = (Long) entManager.createQuery(query + " WHERE p.paymentStatus = :pstat AND p.lga = :contr").setParameter("pstat", PaymentStatusEnum.PARTIAL.toString())
                                .setParameter("contr", lga_name).getSingleResult();
                    } else if (contractor_id != null) {
                        result = (Long) entManager.createQuery(query + " WHERE p.paymentStatus = :pstat AND p.consultantId.id = :contr").setParameter("pstat", PaymentStatusEnum.PARTIAL.toString())
                                .setParameter("contr", contractor_id).getSingleResult();
                    } else {
                        result = (Long) entManager.createQuery(query + " WHERE p.paymentStatus = :pstat").setParameter("pstat", PaymentStatusEnum.PARTIAL.toString()).getSingleResult();
                    }
                    break;
                case "fully":
                    if (lga_name != null) {
                        result = (Long) entManager.createQuery(query + " WHERE p.paymentStatus = :pstat AND p.lga = :contr").setParameter("pstat", PaymentStatusEnum.PAID.toString())
                                .setParameter("contr", lga_name).getSingleResult();
                    } else if (contractor_id != null) {
                        result = (Long) entManager.createQuery(query + " WHERE p.paymentStatus = :pstat AND p.consultantId.id = :contr").setParameter("pstat", PaymentStatusEnum.PAID.toString())
                                .setParameter("contr", contractor_id).getSingleResult();
                    } else {
                        result = (Long) entManager.createQuery(query + " WHERE p.paymentStatus = :pstat").setParameter("pstat", PaymentStatusEnum.PAID.toString()).getSingleResult();
                    }
                    break;
                case "no pay":
                    if (lga_name != null) {
                        result = (Long) entManager.createQuery(query + " WHERE p.hasMatchPayment = false AND p.lga = :contr").setParameter("contr", lga_name).getSingleResult();

                    } else if (contractor_id != null) {
                        result = (Long) entManager.createQuery(query + " WHERE p.hasMatchPayment = false AND p.consultantId.id = :contr").setParameter("contr", contractor_id).getSingleResult();
                    } else {
                        result = (Long) entManager.createQuery(query + " WHERE p.hasMatchPayment = false").getSingleResult();
                    }
                    break;
                default:
                    break;
            }
        }

        return result;
    }

    public Object[] fetchPaymentSummary(String contractor, String lcda_name) {

        Object[] results;
        String qlString = "SELECT SUM(b.crAmount), Count(b) FROM BillPayments b";
        Query query;
        if (lcda_name != null) {
            query = entManager.createQuery(qlString + " WHERE b.lgaName = :lga_").setParameter("lga_", lcda_name);
            results = (Object[]) query.getSingleResult();
        } else if (contractor != null) { //TODO I need to implement block for consultant query
            query = entManager.createQuery(qlString + " WHERE b.merchantName = :consultt").setParameter("consultt", contractor);
            results = (Object[]) query.getSingleResult();
        } else { //we're getting all data
            query = entManager.createQuery(qlString);
            results = (Object[]) query.getSingleResult();
        }

        return results;
    }

    public List<PrintedBills> fetchPrintedBillsFilter(Map<String, String> criteria, int firstResult, int max_, List<String> sortFields, String order_dir) {

        List<PrintedBills> filteredBills = null;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<PrintedBills> lproperty = cq.from(PrintedBills.class);

        List<Predicate> predicates = new ArrayList<>();
        List<Order> orderList = new ArrayList();
        int max_result = (max_ > 0) ? max_ : 2000;

        DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        try {
            if (criteria.containsKey("street_name")) {
                predicates.add(qb.like(lproperty.<String>get("streetName"), "%"+criteria.get("street_name")+"%"));
                //predicates.add(qb.equal(lproperty.<String>get("streetName"), criteria.get("street_name")));
            }

            if ((criteria.containsKey("district"))) {
                predicates.add(qb.equal(lproperty.<String>get("districtName"), criteria.get("district")));
            }

            if (criteria.containsKey("lga_name")) {
                predicates.add(qb.equal(lproperty.<String>get("lga"), criteria.get("lga_name")));
            }

            if (criteria.containsKey("status")) {
                predicates.add(qb.equal(lproperty.<String>get("paymentStatus"), criteria.get("status")));
            }

            if (criteria.containsKey("year")) {
                predicates.add(qb.equal(lproperty.<String>get("billingYear"), criteria.get("year")));
            }

            if (criteria.containsKey("consultant") && (criteria.containsKey("status") && criteria.get("status").equalsIgnoreCase("COULD NOT FIND BILL"))) {
                criteria.remove("consultant");
            }

            if (criteria.containsKey("is_delivered")) {
                predicates.add(qb.equal(lproperty.<Boolean>get("isDelivered"), Boolean.valueOf(criteria.get("is_delivered"))));
            }

            if (criteria.containsKey("has_payment")) {
                predicates.add(qb.equal(lproperty.<Boolean>get("hasMatchPayment"), Boolean.valueOf(criteria.get("has_payment"))));
            }
            if (criteria.containsKey("update_required")) {
                predicates.add(qb.equal(lproperty.<Boolean>get("isUpdateRequired"), (criteria.get("update_required").equalsIgnoreCase("Yes"))));
            }

            if (criteria.containsKey("deleted")) {
                predicates.add(qb.equal(lproperty.<Boolean>get("isDeleted"), Boolean.valueOf(criteria.get("deleted"))));
            }

            if (criteria.containsKey("start_payment")) {
                predicates.add(qb.greaterThanOrEqualTo(lproperty.<Date>get("dateCaptured"), shortFormat.parse(criteria.get("start_payment"))));
            }
            if (criteria.containsKey("end_payment")) {
                predicates.add(qb.lessThanOrEqualTo(lproperty.<Date>get("dateCaptured"), shortFormat.parse(criteria.get("end_payment"))));
            }

            if (criteria.containsKey("owner_name")) {
                predicates.add(qb.like(lproperty.<String>get("ownerName"), "%" + criteria.get("owner_name") + "%"));
            }

            if (criteria.containsKey("bank_code")) {
                predicates.add(qb.like(lproperty.<String>get("bankPaymentCode"), "%" + criteria.get("bank_code") + "%"));
            }

            if (criteria.containsKey("latitude_empty")) {
                predicates.add(qb.isNotNull(lproperty.<String>get("latitude")));
            }

            if (criteria.containsKey("consultant")) {
                Join<PrintedBills, Organizations> orgnObj = lproperty.join("consultantId");
                predicates.add(qb.equal(orgnObj.get("id"), Long.valueOf(criteria.get("consultant"))));
            }

            if (criteria.containsKey("delivery_officer")) {
                Join<PrintedBills, Logindetails> offrObj = lproperty.join("deliveryLogindetailId");
                predicates.add(qb.equal(offrObj.get("id"), Long.valueOf(criteria.get("delivery_officer"))));
            }

            if (criteria.containsKey("delivery_start")) {
                predicates.add(qb.greaterThanOrEqualTo(lproperty.<Date>get("deliveryDate"), shortFormat.parse(criteria.get("delivery_start"))));
            }

            if (criteria.containsKey("delivery_end")) {
                predicates.add(qb.lessThanOrEqualTo(lproperty.<Date>get("deliveryDate"), shortFormat.parse(criteria.get("delivery_end"))));
            }

            if (sortFields != null && sortFields.size() > 0) {
                for (String field : sortFields) {
                    orderList.add(qb.desc(lproperty.get(field)));
                }
            }

            if ((order_dir != null) && (order_dir.equalsIgnoreCase("desc"))) {
                orderList.add(qb.desc(lproperty.get("propertyIdn")));
            } else {
                orderList.add(qb.asc(lproperty.get("propertyIdn")));
            }

            filteredBills = entManager.createQuery(cq.select(lproperty).where(predicates.toArray(new Predicate[]{})).orderBy(orderList)).setMaxResults(max_result)
                    .setFirstResult(firstResult).getResultList();

        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, "Did not find Printed-Bill: ", nr);
        } catch (ParseException ex) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return filteredBills;
    }

    public long countFilteredBillResults(Map<String, String> criteria) {
        long record_count = 0;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<PrintedBills> lproperty = cq.from(PrintedBills.class);

        List<Predicate> predicates = new ArrayList<>();
        DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        try {
            if (criteria.containsKey("street_name")) {
                //predicates.add(qb.like(lproperty.<String>get("streetName"), "%"+Street_name+"%"));
                predicates.add(qb.equal(lproperty.<String>get("streetName"), criteria.get("street_name")));
            }
            if ((criteria.containsKey("district"))) {
                predicates.add(qb.equal(lproperty.<String>get("districtName"), criteria.get("district")));
            }
            if (criteria.containsKey("lga_name")) {
                predicates.add(qb.equal(lproperty.<String>get("lga"), criteria.get("lga_name")));
            }

            if (criteria.containsKey("status")) {
                predicates.add(qb.equal(lproperty.<String>get("paymentStatus"), criteria.get("status")));
            }

            if (criteria.containsKey("start_payment")) {
                predicates.add(qb.greaterThanOrEqualTo(lproperty.<Date>get("dateCaptured"), shortFormat.parse(criteria.get("start_payment"))));
            }
            if (criteria.containsKey("end_payment")) {
                predicates.add(qb.lessThanOrEqualTo(lproperty.<Date>get("dateCaptured"), shortFormat.parse(criteria.get("end_payment"))));
            }

            if (criteria.containsKey("is_delivered")) {
                predicates.add(qb.equal(lproperty.<Boolean>get("isDelivered"), Boolean.valueOf(criteria.get("is_delivered"))));
            }

            if (criteria.containsKey("has_payment")) {
                predicates.add(qb.equal(lproperty.<Boolean>get("hasMatchPayment"), Boolean.valueOf(criteria.get("has_payment"))));
            }

            if (criteria.containsKey("deleted")) {
                predicates.add(qb.equal(lproperty.<Boolean>get("isDeleted"), Boolean.valueOf(criteria.get("deleted"))));
            }

            if (criteria.containsKey("owner_name")) {
                predicates.add(qb.like(lproperty.<String>get("ownerName"), "%" + criteria.get("owner_name") + "%"));
            }

            if (criteria.containsKey("bank_code")) {
                predicates.add(qb.like(lproperty.<String>get("bankPaymentCode"), "%" + criteria.get("bank_code") + "%"));
            }

            if (criteria.containsKey("consultant")) {
                Join<PrintedBills, Organizations> orgnObj = lproperty.join("consultantId");
                predicates.add(qb.equal(orgnObj.get("id"), Long.valueOf(criteria.get("consultant"))));
            }

            if (criteria.containsKey("update_required")) {
                predicates.add(qb.equal(lproperty.<Boolean>get("isUpdateRequired"), (criteria.get("update_required").equalsIgnoreCase("Yes"))));
            }

            if (criteria.containsKey("delivery_start")) {
                predicates.add(qb.greaterThanOrEqualTo(lproperty.<Date>get("deliveryDate"), shortFormat.parse(criteria.get("delivery_start"))));
            }

            if (criteria.containsKey("delivery_end")) {
                predicates.add(qb.lessThanOrEqualTo(lproperty.<Date>get("deliveryDate"), shortFormat.parse(criteria.get("delivery_end"))));
            }

            if (criteria.containsKey("latitude_empty")) {
                predicates.add(qb.isNotNull(lproperty.<String>get("latitude")));
            }

            if (criteria.containsKey("year")) {
                predicates.add(qb.equal(lproperty.<String>get("billingYear"), criteria.get("year")));
            }
            
            if (criteria.containsKey("delivery_officer")) {
                Join<PrintedBills, Logindetails> offrObj = lproperty.join("deliveryLogindetailId");
                predicates.add(qb.equal(offrObj.get("id"), Long.valueOf(criteria.get("delivery_officer"))));
            }

            record_count = (long) entManager.createQuery(cq.select(qb.count(lproperty)).where(predicates.toArray(new Predicate[]{}))).getSingleResult();

        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, "Did not find Printed-Bill: ", nr);
        } catch (ParseException ex) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return record_count;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public List<PrintedBills> fetchBillsWithSingleCriteria(String notice_number, String billing_year) {

        List<PrintedBills> bills = null;

        try {
            if ((notice_number != null) && (billing_year != null)) {
                bills = entManager.createQuery("FROM PrintedBills p WHERE p.propertyIdn = :code AND p.billingYear = :year AND p.isDeleted = false", PrintedBills.class)
                        .setParameter("code", notice_number).setParameter("year", billing_year).getResultList();
            } else if (notice_number != null) {
                bills = entManager.createQuery("FROM PrintedBills p WHERE p.propertyIdn = :code", PrintedBills.class)
                        .setParameter("code", notice_number).getResultList();
            }
        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, "Did not find Bill with Criteria: ", nr);
        }
        return bills;
    }

    public List<FileUploads> fetchAllPaymentFiles(String file_side) {

        List<FileUploads> upFiles = null;
        String query = "FROM FileUploads f";

        try {
            if (file_side != null) {
                upFiles = entManager.createQuery(query + " WHERE f.fileSide = :side", FileUploads.class).setParameter("side", file_side).getResultList();
            } else {
                upFiles = entManager.createQuery(query, FileUploads.class).getResultList();
            }
        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, "Did not finda any Payment File: ", nr);
        }

        return upFiles;
    }

    public List<BillPayments> fetchPaymentsSearchResult(Map<String, String> criteria) {
        List<BillPayments> paymentsList = null;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<BillPayments> payObj = cq.from(BillPayments.class);

        List<Predicate> predicates = new ArrayList<>();
        DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        if (criteria.containsKey("district")) {
            predicates.add(qb.equal(payObj.get("districtName"), criteria.get("district")));
        }
        if (criteria.containsKey("lga_name")) {
            predicates.add(qb.equal(payObj.get("lgaName"), criteria.get("lga_name")));
        }
        if (criteria.containsKey("bill_status")) {
            predicates.add(qb.equal(payObj.get("paymentStatus"), criteria.get("bill_status")));
        }

        if (criteria.containsKey("payment_id")) {
            predicates.add(qb.equal(payObj.get("id"), Long.valueOf(criteria.get("payment_id"))));
        }

        if (criteria.containsKey("payer_id")) {
            predicates.add(qb.like(payObj.<String>get("vPayerId"), "%" + criteria.get("payer_id") + "%"));
        }
        if (criteria.containsKey("payer_name")) {
            predicates.add(qb.like(payObj.<String>get("vPayerName"), "%" + criteria.get("payer_name") + "%"));
        }
        if (criteria.containsKey("receipt")) {
            predicates.add(qb.like(payObj.<String>get("transRef"), "%" + criteria.get("receipt") + "%"));
        }
        if (criteria.containsKey("property_id")) {
            Join<BillPayments, PrintedBills> street = payObj.join("printedBillId");
            predicates.add(qb.like(street.<String>get("propertyIdn"), "%" + criteria.get("property_id") + "%"));
        }
        if (criteria.containsKey("processed")) {
            predicates.add(qb.equal(payObj.<Boolean>get("isProcessed"), criteria.get("processed").equals("yes")));
        }
        if (criteria.containsKey("duplicate")) {
            predicates.add(qb.equal(payObj.<Boolean>get("isDuplicate"), criteria.get("duplicate").equals("yes")));
        }
        if (criteria.containsKey("year")) {
            Join<BillPayments, PrintedBills> street = payObj.join("printedBillId");
            predicates.add(qb.equal(street.<String>get("billingYear"), criteria.get("year")));
        }
        try {
            if (criteria.containsKey("start_payment")) {
                predicates.add(qb.greaterThanOrEqualTo(payObj.<Date>get("entryDate"), shortFormat.parse(criteria.get("start_payment"))));
            }
            if (criteria.containsKey("end_payment")) {
                predicates.add(qb.lessThanOrEqualTo(payObj.<Date>get("entryDate"), shortFormat.parse(criteria.get("end_payment"))));
            }
        } catch (ParseException ex) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            paymentsList = entManager.createQuery(cq.select(payObj).where(predicates.toArray(new Predicate[]{})).orderBy(qb.asc(payObj.get("created")))).getResultList();

        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(PrintedBillsService.class.getName()).log(Level.SEVERE, "Did not find property: ", nr);
        }

        return paymentsList;
    }

    public List<Object[]> fetchBillsDeliverySummary(Long contractor_id, String lcda_name, String district) {

        List<Object[]> results;
        String qlString = "SELECT (COUNT(*) / 3), MONTH(b.created), YEAR(b.created) FROM BillsDeliveryFiles b GROUP BY MONTH(b.created), YEAR(b.created)";
        //Query query;
        results = getSummaryObjects(contractor_id, lcda_name, qlString);

        return results;
    }

    private List<Object[]> getSummaryObjects(Long contractor_id, String lcda_name, String qlString) {
        Query query;
        List<Object[]> results;
        if (lcda_name != null) { //TODO provide proper implementation for this block
            query = entManager.createQuery(qlString + " WHERE b.lga = :lga_").setParameter("lga_", lcda_name);
            results = query.getResultList();
        } else if (contractor_id != null) { //TODO I need to implement block for consultant query
            query = entManager.createQuery(qlString + " WHERE b.consultantId.id = :consultt").setParameter("consultt", contractor_id);
            results = query.getResultList();
        } else { //we're getting all data
            query = entManager.createQuery(qlString);
            results = query.getResultList();
        }
        return results;
    }

    public List<Object[]> fetchBillsPaymentSummary(Long contractor_id, String lcda_name, String district) {

        //List<Object[]> results;
        String qlString = "SELECT COUNT(*), SUM(b.crAmount),  MONTH(b.valueDate), YEAR(b.valueDate) FROM BillPayments b GROUP BY MONTH(b.valueDate), YEAR(b.valueDate)";
        //Query query;
        List<Object[]> results = getSummaryObjects(contractor_id, lcda_name, qlString);

        return results;
    }

    public List<Object[]> fetchDuplicateMonthlyPayments(Long contractor_id, int month_, int year) {

        List<Object[]> results = null;
        String qlString;

        if (contractor_id != null) {

        } else { //we're getting all data

            qlString = "SELECT b.vPayerName, b.vPayerId, b.valueDate, b.crAmount, b.shortName, count(*) FROM BillPayments b WHERE MONTH(b.entryDate) = :dupi AND YEAR(b.entryDate) = :year "
                    + "GROUP BY b.vPayerName, b.vPayerId, b.valueDate, b.crAmount, b.shortName HAVING COUNT(*) > 1";
            results = entManager.createQuery(qlString).setParameter("dupi", month_).setParameter("year", year).getResultList();

        }

        return results;
    }
}
