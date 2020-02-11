/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.service;

import java.io.File;
import java.io.Serializable;
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
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import net.icsl.ledar.ejb.dto.GenericModelDto;

import net.icsl.ledar.ejb.model.*;

/**
 *
 * @author sfagade
 */
@Stateless
@LocalBean
public class LandPropertiesDataService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager entManager;

    @TransactionAttribute(REQUIRES_NEW)
    public WardLandProperties savePropertyData(WardLandProperties wardLandProperties, PropertyBiodatas propertyBiodatas, List<PropertyClassificationDetails> propDetails, List<PropertyDocuments> propDocs,
            List<PropertyServices> propServices, UsersLastActivities activity) {

        if ((wardLandProperties != null)) {
            try {
                if (propertyBiodatas != null) {
                    entManager.persist(propertyBiodatas);
                }

                if (wardLandProperties.getOwnerOrganizationId() != null) {
                    entManager.persist(wardLandProperties.getOwnerOrganizationId());
                }

                entManager.persist(wardLandProperties);

                UsedPropertyIds usedPin = new UsedPropertyIds(null, wardLandProperties.getPropertyId().replaceAll("[^\\d.]", ""), wardLandProperties.getCreated());
                entManager.persist(usedPin);

                if (propDetails.size() > 0) { //this should always be the case
                    for (PropertyClassificationDetails propDetail : propDetails) {
                        entManager.persist(propDetail);
                    }
                }

                if (propDocs.size() > 0) {
                    for (PropertyDocuments propDoc : propDocs) {
                        entManager.persist(propDoc.getFileUploadId());
                        entManager.persist(propDoc);
                    }
                }

                if (propServices.size() > 0) {
                    for (PropertyServices propSerivce : propServices) {
                        entManager.persist(propSerivce);
                    }
                }

                //entManager.merge(wardLandProperties.getCapturedById());
                if (activity != null) {
                    entManager.persist(activity);
                }

                return wardLandProperties;
            } catch (org.hibernate.exception.ConstraintViolationException cns) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "ConstraintViolationException while trying to save New Enumeration Information", cns.getCause());
            }
        }

        return null;
    }

    public FileUploads findFileByUploadId(Long file_id) {

        FileUploads fileUpload = null;

        if (file_id != null) {
            fileUpload = entManager.createQuery("FROM FileUploads f WHERE f.id = :fileID", FileUploads.class).setParameter("fileID", file_id).getSingleResult();
        }

        return fileUpload;

    }

    public WardLandProperties findWardPropertyById(Long prop_id) {

        WardLandProperties wardProperty = null;

        if (prop_id != null) {
            wardProperty = entManager.createQuery("FROM WardLandProperties w WHERE w.id = :billID ", WardLandProperties.class).setParameter("billID", prop_id).getSingleResult();
        }

        return wardProperty;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public BareLands saveBareLandData(BareLands landInfo, List<BarelandFiles> blands, UsersLastActivities activity) {

        if ((landInfo != null)) {
            if (landInfo.getPropertyBiodataId() != null) {
                entManager.persist(landInfo.getPropertyBiodataId());
            }

            if (landInfo.getOwnerOrganizationId() != null) {
                entManager.persist(landInfo.getOwnerOrganizationId());
            }

            entManager.persist(landInfo);

            UsedPropertyIds usedPin = new UsedPropertyIds(null, landInfo.getPropertyId().replaceAll("[^\\d.]", ""), landInfo.getCreated());
            entManager.persist(usedPin);

            if ((blands != null) && (blands.size() > 0)) {
                for (BarelandFiles bland : blands) {
                    entManager.persist(bland.getFileUploadId());
                    entManager.persist(bland);
                }
            }

            if (activity != null) {
                entManager.persist(activity);
            }

            return landInfo;
        }

        return null;
    }

    public List<PropertyServices> fetchPropertyServicesByPropertyId(Long prop_id) {

        List<PropertyServices> propServices = null;

        if (prop_id != null) {
            try {
                propServices = entManager.createQuery("FROM PropertyServices p WHERE p.wardLandPropertyId.id = :wardId", PropertyServices.class).setParameter("wardId", prop_id).getResultList();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Property does not have any services: ", nr);
            }
        }

        return propServices;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Boolean updateWardLandPropertyInfo(WardLandProperties landProperty, List<PropertyServices> propServices, UsersLastActivities activity) {

        if (landProperty != null) {

            if (landProperty.getOwnershipType().equals("INDIVIDUAL")) {
                if (landProperty.getOwnerOrganizationId() != null) {
                    //entManager.remove(landProperty.getOwnerOrganizationId());
                    entManager.remove(entManager.contains(landProperty.getOwnerOrganizationId()) ? landProperty.getOwnerOrganizationId() : entManager.merge(landProperty.getOwnerOrganizationId()));
                    //entManager.createQuery("delete from Organizations o where o.id = :orgId").setParameter("orgId", landProperty.getOwnerOrganizationId().getId()).executeUpdate();
                    landProperty.setOwnerOrganizationId(null);
                    entManager.persist(landProperty.getPropertyBiodataId());
                    entManager.flush();
                } else {
                    entManager.merge(landProperty.getPropertyBiodataId());
                }
            } else if (landProperty.getOwnershipType() != null && landProperty.getOwnershipType().equals("ORGANIZATION")) {
                if (landProperty.getPropertyBiodataId() != null) {
                    //entManager.remove(landProperty.getPropertyBiodataId());
                    entManager.remove(entManager.contains(landProperty.getPropertyBiodataId()) ? landProperty.getPropertyBiodataId() : entManager.merge(landProperty.getPropertyBiodataId()));
                    //entManager.createQuery("delete from PropertyBiodata p where p.id = :probId").setParameter("probId", landProperty.getPropertyBiodataId().getId()).executeUpdate();
                    landProperty.setPropertyBiodataId(null);
                    entManager.persist(landProperty.getOwnerOrganizationId());
                    entManager.flush();
                } else {
                    entManager.merge(landProperty.getOwnerOrganizationId());
                }
            }

            entManager.merge(landProperty);
            entManager.persist(activity);

            if (landProperty.getPropertyTypes().equals("UNSERVICED")) { //the property is not serviced, we should try removing all services it might have
                entManager.createQuery("delete from PropertyServices p where p.wardLandPropertyId.id = :propId").setParameter("propId", landProperty.getId()).executeUpdate();
            }

            if ((propServices != null) && propServices.size() > 0) {
                //remove prior services from the property
                entManager.createQuery("delete from PropertyServices p where p.wardLandPropertyId.id = :propId").setParameter("propId", landProperty.getId()).executeUpdate();

                for (PropertyServices propServ : propServices) {
                    entManager.persist(propServ);
                }
            }

            return true;
        }

        return false;
    }

    public PropertyBiodatas findPropertyBiodatasById(Long lcda_id) {

        PropertyBiodatas properties = null;
        String hql_query;

        if (lcda_id != null) {
            try {
                hql_query = "FROM PropertyBiodatas p WHERE p.id = :lcdaId"; //searching by localgovt
                properties = entManager.createQuery(hql_query, PropertyBiodatas.class).setParameter("lcdaId", lcda_id).getSingleResult();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Did not find Property Biodatas: ", nr);
            }
        }

        return properties;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public List<PropertyDocuments> findAllPropertyDocumentsByPropertyId(Long prop_id, Boolean deleted) {

        List<PropertyDocuments> propServices = null;

        if (prop_id != null) {
            try {
                propServices = entManager.createQuery("FROM PropertyDocuments p WHERE p.wardLandPropertyId.id = :wardId AND p.fileUploadId.isDeleted = :deleted", PropertyDocuments.class).
                        setParameter("wardId", prop_id).setParameter("deleted", deleted).getResultList();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Property does not have any Documents: ", nr);
            }
        }

        return propServices;
    }

    public Boolean updateFileUploads(FileUploads file_upload) {

        if (file_upload != null) {

            entManager.merge(file_upload);
            return true;
        }
        return false;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Boolean deletePropertyData(Long prop_id, UsersLastActivities activity) {

        if (prop_id != null) {
            WardLandProperties property = this.findWardPropertyById(prop_id);
            File upload_file;

            if (property != null) {
                if (property.getPropertyBiodataId() != null) { //this should always be the case
                    entManager.remove(property.getPropertyBiodataId());
                }
                if (property.getPropertyDocumentsCollection() != null && property.getPropertyDocumentsCollection().size() > 0) {
                    for (PropertyDocuments documents : property.getPropertyDocumentsCollection()) {
                        upload_file = new File(documents.getFileUploadId().getAbsolutePath() + "/" + documents.getFileUploadId().getFileName());
                        if (upload_file.exists()) {
                            upload_file.delete();
                        }
                        entManager.remove(documents.getFileUploadId());
                    }
                }
                entManager.remove(property);
            }

            if (activity != null) {
                entManager.persist(activity);
            }

            return true;
        }

        return false;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public PropertyValuations saveUpdatePropertyValuationInfo(PropertyValuations propValuation, Notifications notific, UsersLastActivities activity) {

        if (propValuation != null) {
            if (propValuation.getId() != null) {
                entManager.merge(propValuation);
            } else {
                entManager.persist(propValuation);
            }

            entManager.merge(propValuation.getWardLandPropertyId());

            if (notific != null) {
                entManager.persist(notific);
            }

            if (activity != null) {
                entManager.persist(activity);
            }

            return propValuation;
        }

        return null;
    }

    public PropertyValuations findValuationsByPropertyId(Long prop_id) {

        PropertyValuations valuation = null;
        String hql_query;

        if (prop_id != null) {
            try {
                hql_query = "FROM PropertyValuations p WHERE p.wardLandPropertyId.id = :lcdaId"; //searching by property id
                valuation = entManager.createQuery(hql_query, PropertyValuations.class).setParameter("lcdaId", prop_id).getSingleResult();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Did not find Property Valuation: ", nr);
            }
        }

        return valuation;
    }

    public BareLands findBareLandById(Long land_id) {

        BareLands bland = null;
        if (land_id != null) {
            bland = entManager.createQuery("FROM BareLands b WHERE b.id=:lid", BareLands.class).setParameter("lid", land_id).getSingleResult();
        }
        return bland;
    }

    public List<BarelandFiles> fetchAllBarelandFilesByLandId(Long land_id) {
        List<BarelandFiles> blandFiles = null;

        if (land_id != null) {
            blandFiles = entManager.createQuery("FROM BarelandFiles b WHERE b.bareLandId.id=:landID", BarelandFiles.class).setParameter("landID", land_id).getResultList();
        }

        return blandFiles;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public List<PropertyClassificationDetails> findAllPropertyBuildingsByPropertyId(Long prop_id, Boolean deleted) {

        List<PropertyClassificationDetails> buildings = null;

        if (prop_id != null) {
            try {
                buildings = entManager.createQuery("FROM PropertyClassificationDetails p WHERE p.wardLandPropertyId.id = :wardId", PropertyClassificationDetails.class).
                        setParameter("wardId", prop_id).getResultList();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Property does not have any buildings, somebody has done wayo: ", nr);
            }
        }

        return buildings;
    }

    public List<BareLands> fetchAllBarelandByPin(String property_id, Long contractor_id) {

        List<BareLands> blands = null;
        if (property_id != null) {
            if (contractor_id != null) {
                blands = entManager.createQuery("FROM BareLands b WHERE b.propertyId LIKE :lid AND b.contractorId.id = :contrid", BareLands.class).setParameter("lid", "%" + property_id + "%")
                        .setParameter("contrid", contractor_id).getResultList();
            } else {
                blands = entManager.createQuery("FROM BareLands b WHERE b.propertyId LIKE :lid", BareLands.class).setParameter("lid", "%" + property_id + "%").getResultList();
            }
        }
        return blands;
    }

    public List<BareLands> findBarelandByPinAndAddress(String property_id, String address, Long street_id) {

        List<BareLands> bland;

        String hsql = "FROM BareLands b WHERE b.propertyId=:lid";

        if (street_id != null) {
            hsql += " AND b.wardStreetId.id = :strId";
            bland = entManager.createQuery(hsql, BareLands.class).setParameter("lid", property_id).setParameter("strId", street_id).getResultList();
        } else if (address != null && !address.isEmpty()) {
            hsql += " AND b.irregularAddress = :irrAddr";
            bland = entManager.createQuery(hsql, BareLands.class).setParameter("lid", property_id).setParameter("irrAddr", address).getResultList();
        } else {
            bland = entManager.createQuery(hsql, BareLands.class).setParameter("lid", property_id).getResultList();
        }

        return bland;
    }

    public List<WardLandProperties> findWardPropertyByPinAndStreet(String property_id, String address, Long street_id) {

        List<WardLandProperties> wardProperty;

        String hsql = "FROM WardLandProperties w WHERE w.propertyId = :propID";

        if (street_id != null) {
            hsql += " AND w.wardStreetId.id = :strId";
            wardProperty = entManager.createQuery(hsql, WardLandProperties.class).setParameter("propID", property_id).setParameter("strId", street_id).getResultList();
        } else if ((address != null) && (!address.isEmpty())) {
            hsql += " AND w.irregularAddress = :irrAddr";
            wardProperty = entManager.createQuery(hsql, WardLandProperties.class).setParameter("propID", property_id).setParameter("irrAddr", address).getResultList();
        } else {
            wardProperty = entManager.createQuery(hsql, WardLandProperties.class).setParameter("propID", property_id).getResultList();
        }

        return wardProperty;
    }

    public List<WardLandProperties> findWardPropertyNotSentToPortal(String status, int start_index, int max_result, Boolean restrict_year) {

        List<WardLandProperties> wardProperty = null;

        if (status != null && !restrict_year) {
            wardProperty = entManager.createQuery("FROM WardLandProperties w WHERE w.propertyValuationStatus <> :propID", WardLandProperties.class).setParameter("propID", status)
                    .setFirstResult(start_index).setMaxResults(max_result).getResultList();
        } else if (status != null && restrict_year) {
            try {
                Date current_year = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).parse("01-Jan-2017");
                wardProperty = entManager.createQuery("FROM WardLandProperties w WHERE w.propertyValuationStatus <> :propID AND w.created > :date_", WardLandProperties.class).setParameter("propID", status)
                        .setParameter("date_", current_year).setFirstResult(start_index).setMaxResults(max_result).getResultList();
            } catch (ParseException e) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Failed parsing date ", e.getMessage());
            }
        }

        return wardProperty;
    }

    public List<BareLands> filterBarelandLists(Long street_id, Long officer_id, String val_status, Date startDate, Date endDate, int firstResult) {

        List<BareLands> bland = null;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<BareLands> lproperty = cq.from(BareLands.class);

        List<Predicate> predicates = new ArrayList<>();

        try {

            if (street_id != null) {
                Join<BareLands, WardStreets> street = lproperty.join("wardStreetId");
                predicates.add(qb.equal(street.get("id"), street_id));
            }

            if (officer_id != null) {
                Join<BareLands, Logindetails> logDetail = lproperty.join("createdById");
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

            bland = entManager.createQuery(cq.select(lproperty).where(predicates.toArray(new Predicate[]{})).orderBy(qb.desc(lproperty.get("created")))).getResultList();

        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Did not find property: ", nr);
        }

        return bland;
    }

    public List<WardLandProperties> fetchAllPropertiesByPin(String property_id, Long contractor_id) {

        List<WardLandProperties> wardProperty = null;

        if ((property_id != null) && (contractor_id != null)) {
            wardProperty = entManager.createQuery("FROM WardLandProperties w WHERE w.propertyId = :propID AND w.contractorId.id = :contractor", WardLandProperties.class).setParameter("propID", property_id)
                    .setParameter("contractor", contractor_id).getResultList();
        } else if (property_id != null) {
            wardProperty = entManager.createQuery("FROM WardLandProperties w WHERE w.propertyId = :propID", WardLandProperties.class).setParameter("propID", property_id).getResultList();
        }

        return wardProperty;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Boolean deletePropertyUseData(String prop_id, UsersLastActivities activity) {

        if (prop_id != null) {

            try {
                UsedPropertyIds property = entManager.createQuery("FROM UsedPropertyIds u WHERE u.propertyId = :propid", UsedPropertyIds.class).setParameter("propid", prop_id).getSingleResult();
                if (property != null) {
                    entManager.remove(property);
                }

                if (activity != null) {
                    entManager.persist(activity);
                }
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Did not find property: ", nr);
            }

            return true;
        }

        return false;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Boolean updateWardLandProperty(WardLandProperties landProperty, UsersLastActivities activity) {

        if (landProperty != null) {
            if (landProperty.getPropertyBiodataId() != null) {
                entManager.merge(landProperty.getPropertyBiodataId());
            }
            entManager.merge(landProperty);
            if (activity != null) {
                entManager.persist(activity);
            }
            //entManager.flush();
            return true;
        }

        return false;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Boolean updateBareLandInfo(BareLands bareLand, UsersLastActivities activity) {

        if (bareLand != null) {
            if (bareLand.getPropertyBiodataId() != null) {
                entManager.merge(bareLand.getPropertyBiodataId());
            }
            entManager.merge(bareLand);
            if (activity != null) {
                entManager.persist(activity);
            }
            //entManager.flush();
            return true;
        }

        return false;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Boolean updatePropertyOwner(PropertyBiodatas propOwner, UsersLastActivities activity) {

        if (propOwner != null) {
            entManager.merge(propOwner);
            if (activity != null) {
                entManager.persist(activity);
            }
            //entManager.flush();
            return true;
        }

        return false;
    }

    public UsedPropertyIds findUsedPropertyIdsByPropertyPin(String property_pin) {

        UsedPropertyIds usedProp = null;

        if (property_pin != null && !property_pin.isEmpty()) {
            try {
                usedProp = entManager.createQuery("FROM UsedPropertyIds u WHERE u.propertyId = :propID", UsedPropertyIds.class).setParameter("propID", property_pin).getSingleResult();
            } catch (javax.persistence.NoResultException ne) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Property not used before: {0}", property_pin);
            }
        }

        return usedProp;
    }

    public PrintedBills findPrintedBillsById(Long bill_id) {

        PrintedBills properties = null;

        if (bill_id != null) {
            try {
                properties = entManager.createQuery("FROM PrintedBills p WHERE p.id = :lcdaId", PrintedBills.class).setParameter("lcdaId", bill_id).getSingleResult();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.INFO, "Did not find Printed Bill with ID: {0}", new Object[]{bill_id});
            }
        }

        return properties;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Boolean saveMultipleBillsDeliveryFiles(PrintedBills selectedBill, List<BillsDeliveryFiles> delvFiles) {

        Boolean saveAll = false;

        if ((delvFiles != null) && selectedBill != null) { //this should always be the case

            entManager.merge(selectedBill);

            if (delvFiles.size() > 0) { //this should always be the case
                //entManager.persist(fileUpload);
                for (BillsDeliveryFiles delvFile : delvFiles) {
                    entManager.persist(delvFile.getFileUploadId());
                    entManager.persist(delvFile);
                }

                saveAll = true;
            }
        }

        return saveAll;
    }

    public List<PrintedBills> findPrintedBillsByPropertyId(String property_id) {

        List<PrintedBills> properties = null;

        if (property_id != null) {
            try {
                properties = entManager.createQuery("FROM PrintedBills p WHERE p.propertyIdn = :lcdaId", PrintedBills.class).setParameter("lcdaId", property_id).getResultList();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Did not find Printed Bill: ", nr);
            }
        }

        return properties;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Boolean deleteBarelandData(Long prop_id, UsersLastActivities activity) {

        if (prop_id != null) {
            BareLands bareland = this.findBareLandById(prop_id);
            File upload_file;

            if (bareland != null) {
                if (bareland.getPropertyBiodataId() != null) { //this should always be the case
                    entManager.remove(bareland.getPropertyBiodataId());
                }
                if (bareland.getBarelandFilesList() != null && bareland.getBarelandFilesList().size() > 0) {
                    for (BarelandFiles bland : bareland.getBarelandFilesList()) {
                        if (bland.getFileUploadId() != null) { //this should always be the case here
                            upload_file = new File(bland.getFileUploadId().getAbsolutePath() + "/" + bland.getFileUploadId().getFileName());
                            if (upload_file.exists()) {
                                upload_file.delete();
                            }
                            entManager.remove(bland.getFileUploadId());
                        }
                    }
                }

                entManager.remove(bareland);
            }

            if (activity != null) {
                entManager.persist(activity);
            }

            return true;
        }

        return false;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Boolean updateBuildingDetailsInfo(List<PropertyClassificationDetails> buildings, UsersLastActivities activity) {

        if (buildings != null && buildings.size() > 0) {
            for (PropertyClassificationDetails building : buildings) {
                if (building.getId() != null) {
                    entManager.merge(building);
                } else {
                    entManager.persist(building);
                }
            }
            if (activity != null) {
                entManager.persist(activity);
            }

            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public Boolean updatePropertyDocumentsInfo(List<PropertyDocuments> documents, UsersLastActivities activity) {

        if (documents != null && documents.size() > 0) {
            for (PropertyDocuments building : documents) {
                if (building.getId() != null) {
                    entManager.merge(building);
                } else {
                    entManager.persist(building);
                }
            }
            if (activity != null) {
                entManager.persist(activity);
            }

            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public PropertyClassificationDetails findBuildingDetailsById(Long lcda_id) {

        PropertyClassificationDetails properties = null;
        String hql_query;

        if (lcda_id != null) {
            try {
                hql_query = "FROM PropertyClassificationDetails p WHERE p.id = :lcdaId"; //searching by localgovt
                properties = entManager.createQuery(hql_query, PropertyClassificationDetails.class).setParameter("lcdaId", lcda_id).getSingleResult();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Could not find Property Detail: ", nr);
            }
        }

        return properties;
    }

    public List<GenericModelDto> fetchDailyEnumerationReport() {
        List<GenericModelDto> deliveries = null;
        List<String[]> holder;
        DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String hql_query = "SELECT DATE_FORMAT(w.dateCaptured,'%Y-%m-%d'), w.capturedById, count(w) FROM WardLandProperties w WHERE year(w.dateCaptured) = '2017' GROUP BY DATE_FORMAT(w.dateCaptured,"
                + "'%Y-%m-%d'), w.capturedById";
        try {
            holder = entManager.createQuery(hql_query).getResultList();
            deliveries = new ArrayList<>();
            for (Object[] row : holder) {
                //System.out.println("Getting values: " + row[0]);
                deliveries.add(new GenericModelDto(shortDateFormat.parse(row[0].toString()), (Long) row[2], (Logindetails) row[1]));
            }
        } catch (NoResultException ne) {
            Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Did not find any result for Daily Enum: ", ne);
        } catch (ParseException ex) {
            Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return deliveries;
    }

    public List<GenericModelDto> fetchDailyDeliveryReport(Date start_date, Date end_date) {
        List<GenericModelDto> deliveries = null;
        List<String[]> holder;
        DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String hql_query = "SELECT DATE_FORMAT(w.deliveryDate,'%Y-%m-%d'), w.deliveryLogindetailId, count(w) FROM PrintedBills w WHERE w.deliveryDate BETWEEN :startDate AND :endDate GROUP BY DATE_FORMAT(w.deliveryDate,"
                + "'%Y-%m-%d'), w.deliveryLogindetailId";
        try {
            holder = entManager.createQuery(hql_query).setParameter("startDate", start_date).setParameter("endDate", end_date).getResultList();
            deliveries = new ArrayList<>();
            for (Object[] row : holder) {
                //(Long modelId, Logindetails logindetail, PrintedBills printedBillId, String gpsLongitude, String gpsLatitude
                deliveries.add(new GenericModelDto(shortDateFormat.parse(row[0].toString()), (Long) row[2], (Logindetails) row[1]));
            }
        } catch (NoResultException ne) {
            Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Did not find any result for Daily Enum: ", ne);
        } catch (ParseException ex) {
            Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return deliveries;
    }

    public List<WardLandProperties> filterWardLandPropertyList(Map<String, String> criteria) {
        List<WardLandProperties> properties = null;

        List<Predicate> predicates = new ArrayList<>();
        DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<WardLandProperties> lproperty = cq.from(WardLandProperties.class);

        if (criteria.containsKey("captured_by") && criteria.get("captured_by") != null) {
            Join<WardLandProperties, Logindetails> street = lproperty.join("capturedById");
            predicates.add(qb.equal(street.get("id"), Long.valueOf(criteria.get("captured_by"))));
        }

        if (criteria.containsKey("year") && criteria.get("year") != null) {
            predicates.add(qb.equal(qb.function("year", Integer.class, lproperty.get("dateCaptured")), criteria.get("year")));
        }

        try {
            if (criteria.containsKey("startDate") && criteria.get("startDate") != null) {
                predicates.add(qb.greaterThanOrEqualTo(lproperty.<Date>get("dateCaptured"), shortFormat.parse(criteria.get("startDate"))));
            }
            if (criteria.containsKey("endDate") && criteria.get("endDate") != null) {
                predicates.add(qb.lessThanOrEqualTo(lproperty.<Date>get("dateCaptured"), shortFormat.parse(criteria.get("endDate"))));
            }
        } catch (ParseException ex) {
            Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            properties = entManager.createQuery(cq.select(lproperty).where(predicates.toArray(new Predicate[]{})).orderBy(qb.desc(lproperty.get("dateCaptured")))).getResultList();

        } catch (NoResultException nr) {
            Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Did not find property: ", nr);
        }

        return properties;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public StreetGaps saveUpdateStreetGapData(StreetGaps gap, List<StreetGapFiles> gapFiles, UsersLastActivities activities, Boolean new_record) {
        if (new_record) {
            entManager.persist(gap);
        } else {
            entManager.merge(gap);
        }

        if (activities != null) {
            entManager.persist(activities);
        }

        if (gapFiles != null && gapFiles.size() > 0) {
            for (StreetGapFiles file : gapFiles) {
                entManager.persist(file.getFileUploadId());
                entManager.persist(file);
            }
        }

        return gap;
    }

    public List<StreetGaps> fetchStreetGapsWithCriteria(Map<String, String> criteria, List<String> sortFields, String order_dir) {
        List<StreetGaps> properties = null;

        List<Predicate> predicates = new ArrayList<>();
        DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<StreetGaps> from_ = cq.from(StreetGaps.class);
        List<Order> orderList = new ArrayList();

        if (criteria.containsKey("consultant") && criteria.get("consultant") != null) {
            Join<StreetGaps, Organizations> consultant = from_.join("consultantId");
            predicates.add(qb.equal(consultant.get("id"), Long.valueOf(criteria.get("consultant"))));
        }
        
        if (criteria.containsKey("gapId") && criteria.get("gapId") != null) {
            predicates.add(qb.equal(from_.get("id"), Long.valueOf(criteria.get("gapId"))));
        }

        if (sortFields != null && sortFields.size() > 0) {
            for (String field : sortFields) {
                if(order_dir != null && order_dir.equalsIgnoreCase("desc")) {
            	orderList.add(qb.desc(from_.get(field)));
                } else {
                	orderList.add(qb.asc(from_.get(field)));	
                }
            }
        }

        try {
            properties = entManager.createQuery(cq.select(from_).where(predicates.toArray(new Predicate[]{})).orderBy(orderList)).getResultList();

        } catch (NoResultException nr) {
            Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Did not find street gap: ", nr);
        }

        return properties;
    }

    public List<StreetGapFiles> fecthStreetGapFilesByGapId(Long street_gap) {

        List<StreetGapFiles> gapFiles = null;
        try {

            gapFiles = entManager.createQuery("FROM StreetGapFiles s WHERE s.streetGapId.id = :street", StreetGapFiles.class).setParameter("street", street_gap).getResultList();

        } catch (NoResultException nr) {
            Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Did not find any files for this street gap: ", nr);
        }
        return gapFiles;

    }
}
