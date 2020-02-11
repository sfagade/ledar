package net.icsl.ledar.ejb.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;

import net.icsl.ledar.ejb.model.*;
import net.icsl.ledar.ejb.util.EmailSender;

@Stateless
public class ReferenceDataService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager entManager;

    @Inject
    private EmailSender emailSender;

    public ReferenceDataService() {
    }

    ;

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

    public List<PropertyUsageCategories> fetchPropertyUsageCategories(Long usage_id, Long classification_id) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        List<PropertyUsageCategories> usageCategories = null;

        CriteriaQuery<PropertyUsageCategories> criteria = cb.createQuery(PropertyUsageCategories.class);
        Root<PropertyUsageCategories> usageCriteria = criteria.from(PropertyUsageCategories.class);
        List<Predicate> predicates = new ArrayList<>();

        if (usage_id != null) {
            predicates.add(cb.equal(usageCriteria.get("id"), usage_id));
        } else if (classification_id != null) {
            Join<PropertyUsageCategories, PropertyClassifications> street = usageCriteria.join("propertyClassificationId");
            predicates.add(cb.equal(street.<Long>get("id"), classification_id));
        }
        try {
            usageCategories = entManager.createQuery(criteria.select(usageCriteria).where(predicates.toArray(new Predicate[]{}))).getResultList();
        } catch (javax.persistence.NoResultException nr) {
            Logger.getLogger(ReferenceDataService.class.getName()).log(Level.SEVERE, "No Property classification usage found! ", nr.getCause());
        }

        return usageCategories;
    }

    public List<AuthenticationRoles> fetchAllAuthenticationRole() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<AuthenticationRoles> criteria = cb.createQuery(AuthenticationRoles.class);
        Root<AuthenticationRoles> auth_roles = criteria.from(AuthenticationRoles.class);

        criteria = criteria.multiselect(auth_roles.get("id"), auth_roles.get("roleName"), auth_roles.get("roleOrder"), auth_roles.get("created"))
                .where(cb.equal(auth_roles.get("isContractorRestricted"), false));

        return entManager.createQuery(criteria).getResultList();
    }

    public AuthenticationRoles fetchAuthenticationRoleById(Long role_id) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<AuthenticationRoles> criteria = cb.createQuery(AuthenticationRoles.class);
        Root<AuthenticationRoles> auth_roles = criteria.from(AuthenticationRoles.class);

        criteria = criteria.multiselect(auth_roles.get("id")).where(cb.equal(auth_roles.get("id"), role_id));

        return entManager.createQuery(criteria).getSingleResult();
    }

    public List<GeographicalBoundaries> fetchAllCountries() {

        Query query = entManager.createQuery("from GeographicalBoundaries g where g.geographicalBoundaryTypeId.id=7"); //7 is key for countries

        return query.getResultList();

    }

    public GeographicalBoundaries fetchDefaultCountry(String country_name) {

        GeographicalBoundaries default_country = entManager.createNamedQuery("GeographicalBoundaries.findByBoundaryName", GeographicalBoundaries.class).setParameter("boundaryName", country_name).
                getSingleResult();
        return default_country;
    }

    public List<GeographicalBoundaries> fetchAllCountryStates(String country_nm) {

        Query query = entManager.createQuery("from GeographicalBoundaries g where g.boundaryParentId.boundaryName=:ctryName").setParameter("ctryName", country_nm); //7 is key for countries

        return query.getResultList();

    }

    public List<OrganizationTypes> fetchAllOrganizationTypes() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<OrganizationTypes> criteria = cb.createQuery(OrganizationTypes.class);
        Root<OrganizationTypes> org_types = criteria.from(OrganizationTypes.class);

        criteria = criteria.multiselect(org_types.get("id"), org_types.get("typeName"), org_types.get("created"));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<ServiceTypes> fetchAllServiceTypes() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<ServiceTypes> criteria = cb.createQuery(ServiceTypes.class);
        Root<ServiceTypes> serviceType = criteria.from(ServiceTypes.class);

        criteria = criteria.multiselect(serviceType.get("id"), serviceType.get("serviceTypeName"), serviceType.get("created"));

        return entManager.createQuery(criteria).getResultList();
    }

    public Organizations fetchOrganizationByCodeName(String org_code) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<Organizations> criteria = cb.createQuery(Organizations.class);
        Root<Organizations> auth_roles = criteria.from(Organizations.class);

        criteria = criteria.multiselect(auth_roles.get("id"), auth_roles.get("organizationNm"), auth_roles.get("organzaitionCode"), auth_roles.get("created")).
                where(cb.equal(auth_roles.get("organzaitionCode"), org_code));

        return entManager.createQuery(criteria).getSingleResult();

    }

    public UniqueUserIdentifications fetchLastUniqueKey(String key_type) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<UniqueUserIdentifications> criteria = cb.createQuery(UniqueUserIdentifications.class);
        Root<UniqueUserIdentifications> auth_roles = criteria.from(UniqueUserIdentifications.class);

        criteria = criteria.multiselect(auth_roles.get("id"), auth_roles.get("lastCustomerNumber"), auth_roles.get("lastEmployeeNumber"), auth_roles.get("lastComplaintNumber"), auth_roles.get("created"));

        return entManager.createQuery(criteria).getSingleResult();
    }

    public UniqueUserIdentifications updateLastuniqueKey(UniqueUserIdentifications uniqueIds) {

        if (uniqueIds != null) {
            entManager.merge(uniqueIds);
        }

        return uniqueIds;
    }

    public RegisteredDevices fetchDeviceByMacAddress(String mac_address) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<RegisteredDevices> criteria = cb.createQuery(RegisteredDevices.class);
        Root<RegisteredDevices> auth_roles = criteria.from(RegisteredDevices.class);

        criteria = criteria.multiselect(auth_roles.get("id"), auth_roles.get("deviceName"), auth_roles.get("macAddress"), auth_roles.get("created")).
                where(cb.equal(auth_roles.get("macAddress"), mac_address));

        return entManager.createQuery(criteria).getSingleResult();
    }

    public List<CommercialTypes> fetchAllcommercialTypes() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<CommercialTypes> criteria = cb.createQuery(CommercialTypes.class);
        Root<CommercialTypes> commercial_types = criteria.from(CommercialTypes.class);
        criteria.orderBy(cb.asc(commercial_types.get("commercialTypeName")));
        criteria = criteria.multiselect(commercial_types.get("id"), commercial_types.get("commercialTypeName"), commercial_types.get("created"));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<GeographicalBoundaryTypes> fetchAllGeoTypes() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<GeographicalBoundaryTypes> criteria = cb.createQuery(GeographicalBoundaryTypes.class);
        Root<GeographicalBoundaryTypes> geo_types = criteria.from(GeographicalBoundaryTypes.class);

        criteria = criteria.multiselect(geo_types.get("id"), geo_types.get("boundaryTypeName"));

        return entManager.createQuery(criteria).getResultList();
    }

    public GeographicalBoundaryTypes fetchAllGeoTypeById(Long id) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<GeographicalBoundaryTypes> criteria = cb.createQuery(GeographicalBoundaryTypes.class);
        Root<GeographicalBoundaryTypes> geo_types = criteria.from(GeographicalBoundaryTypes.class);

        criteria = criteria.multiselect(geo_types.get("id"), geo_types.get("boundaryTypeName")).where(cb.equal(geo_types.get("id"), id));

        return entManager.createQuery(criteria).getSingleResult();
    }

    public GeographicalBoundaries fetchAllGeoBoundries(Long id) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<GeographicalBoundaries> criteria = cb.createQuery(GeographicalBoundaries.class);
        Root<GeographicalBoundaries> geo_boundries = criteria.from(GeographicalBoundaries.class);

        criteria = criteria.multiselect(geo_boundries.get("id"), geo_boundries.get("boundaryName")).where(cb.equal(geo_boundries.get("id"), id));

        return entManager.createQuery(criteria).getSingleResult();
    }

    public List<Occupations> fetchAllOccupations() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<Occupations> criteria = cb.createQuery(Occupations.class);
        Root<Occupations> ocupations = criteria.from(Occupations.class);
        criteria.orderBy(cb.asc(ocupations.get("occupationName")));
        criteria = criteria.multiselect(ocupations.get("id"), ocupations.get("occupationName")).orderBy(cb.asc(ocupations.get("occupationName")));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<OrganizationTypes> fetchAllOrganizationType() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<OrganizationTypes> criteria = cb.createQuery(OrganizationTypes.class);
        Root<OrganizationTypes> organizationType = criteria.from(OrganizationTypes.class);

        criteria = criteria.multiselect(organizationType.get("id"), organizationType.get("typeName")).orderBy(cb.asc(organizationType.get("typeName")));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<PersonTitles> fetchAllTittle() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<PersonTitles> criteria = cb.createQuery(PersonTitles.class);
        Root<PersonTitles> pple_title = criteria.from(PersonTitles.class);
        criteria.orderBy(cb.asc(pple_title.get("titleName")));
        criteria = criteria.multiselect(pple_title.get("id"), pple_title.get("titleName"), pple_title.get("initsMapping"));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<PropertyClassifications> fetchAllPropertyUse() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<PropertyClassifications> criteria = cb.createQuery(PropertyClassifications.class);
        Root<PropertyClassifications> property_use = criteria.from(PropertyClassifications.class);

        criteria = criteria.multiselect(property_use.get("id"), property_use.get("classificationName"));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<Religions> fetchAllReligion() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<Religions> criteria = cb.createQuery(Religions.class);
        Root<Religions> religion = criteria.from(Religions.class);

        criteria = criteria.multiselect(religion.get("id"), religion.get("religionName"));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<ResidentialTypes> fetchAllResidentialTypes() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<ResidentialTypes> criteria = cb.createQuery(ResidentialTypes.class);
        Root<ResidentialTypes> residential_types = criteria.from(ResidentialTypes.class);

        criteria = criteria.multiselect(residential_types.get("id"), residential_types.get("residentialTypeName"), residential_types.get("created"));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<UserRoles> fetchAllUserRoles() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<UserRoles> criteria = cb.createQuery(UserRoles.class);
        Root<UserRoles> user_role = criteria.from(UserRoles.class);

        criteria = criteria.multiselect(user_role.get("id"), user_role.get("authenticationRoleId"));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<SettlementTypes> fetchAllSettlementType() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<SettlementTypes> criteria = cb.createQuery(SettlementTypes.class);
        Root<SettlementTypes> settlement_types = criteria.from(SettlementTypes.class);

        criteria = criteria.multiselect(settlement_types.get("id"), settlement_types.get("settlementTypeName"));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<StreetTypes> fetchAllStreetType() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<StreetTypes> criteria = cb.createQuery(StreetTypes.class);
        Root<StreetTypes> street_types = criteria.from(StreetTypes.class);
        criteria.orderBy(cb.asc(street_types.get("streetTypeName")));
        criteria = criteria.multiselect(street_types.get("id"), street_types.get("streetTypeName"));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<TitleDocumentTypes> fetchAllDocumentTitleTypes() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<TitleDocumentTypes> criteria = cb.createQuery(TitleDocumentTypes.class);
        Root<TitleDocumentTypes> doc_title_types = criteria.from(TitleDocumentTypes.class);

        criteria = criteria.multiselect(doc_title_types.get("id"), doc_title_types.get("titleName"));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<WardStreets> fetchAllStreetByWardId(Long id) {

        String hql_query = "FROM WardStreets w WHERE w.lcdaWardId.id = :wardID AND w.isVerified = true"; //searching by street
        List<WardStreets> streets = entManager.createQuery(hql_query, WardStreets.class).setParameter("wardID", id).getResultList();

        return streets;
    }

    public List<WardTowns> fetchAllTownByWardId(Long id) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<WardTowns> criteria = cb.createQuery(WardTowns.class);
        Root<WardTowns> town_by_ward = criteria.from(WardTowns.class);

        criteria = criteria.multiselect(town_by_ward.get("id"), town_by_ward.get("townName"), town_by_ward.get("townName"), town_by_ward.get("created")).where(cb.equal(town_by_ward.get("id"), id));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<WardLandProperties> fetchAllLandPropertiesByWardId(Long id) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<WardLandProperties> criteria = cb.createQuery(WardLandProperties.class);
        Root<WardLandProperties> land_properties_by_ward = criteria.from(WardLandProperties.class);

        criteria = criteria.multiselect(land_properties_by_ward.get("id"), land_properties_by_ward.get("propertyId"), land_properties_by_ward.get("propertyNumber"),
                land_properties_by_ward.get("roadSide"), land_properties_by_ward.get("propertyLongitude"), land_properties_by_ward.get("propertyLatitude"), land_properties_by_ward.get("dateCaptured"),
                land_properties_by_ward.get("propertyTypes"), land_properties_by_ward.get("gpsLongitude"), land_properties_by_ward.get("ggpsLatitudee")).
                where(cb.equal(land_properties_by_ward.get("id"), id));

        return entManager.createQuery(criteria).getResultList();
    }

    public WardStreets findStreetById(Long street_id) {

        WardStreets street = null;

        if (street_id != null) {

            try {

                street = entManager.createQuery("FROM WardStreets s WHERE s.id = :streetid", WardStreets.class).setParameter("streetid", street_id).getSingleResult();

            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(ReferenceDataService.class.getName()).log(Level.SEVERE, "No street with ID found: " + street_id, nr.getCause());
            }
        }

        return street;
    }

    public PersonTitles findPersonTitlesById(Long title_id) {

        PersonTitles title_ = null;

        if (title_id != null) {

            CriteriaBuilder cb = getEm().getCriteriaBuilder();

            CriteriaQuery<PersonTitles> criteria = cb.createQuery(PersonTitles.class);
            Root<PersonTitles> personTitle = criteria.from(PersonTitles.class);
            criteria.orderBy(cb.asc(personTitle.get("titleName")));
            criteria = criteria.multiselect(personTitle.get("id"), personTitle.get("titleName")).where(cb.equal(personTitle.get("id"), title_id));

            title_ = entManager.createQuery(criteria).getSingleResult();
        }

        return title_;
    }

    public Occupations findOccupationsById(Long occupa_id) {

        Occupations occupation = null;

        if (occupa_id != null) {

            CriteriaBuilder cb = getEm().getCriteriaBuilder();

            CriteriaQuery<Occupations> criteria = cb.createQuery(Occupations.class);
            Root<Occupations> personTitle = criteria.from(Occupations.class);

            criteria = criteria.multiselect(personTitle.get("id"), personTitle.get("occupationName")).where(cb.equal(personTitle.get("id"), occupa_id));

            occupation = entManager.createQuery(criteria).getSingleResult();
        }

        return occupation;
    }

    public ResidentialUseages findResidentialUseById(Long reside_id) {

        ResidentialUseages resideUse = null;

        if (reside_id != null) {

            CriteriaBuilder cb = getEm().getCriteriaBuilder();

            CriteriaQuery<ResidentialUseages> criteria = cb.createQuery(ResidentialUseages.class);
            Root<ResidentialUseages> useageObj = criteria.from(ResidentialUseages.class);

            criteria = criteria.multiselect(useageObj.get("id"), useageObj.get("residentialUseName"), useageObj.get("created")).where(cb.equal(useageObj.get("id"), reside_id));

            resideUse = entManager.createQuery(criteria).getSingleResult();
        }

        return resideUse;
    }

    public List<BiodataPersonTypes> fetchAllBiodataPersonTypes() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<BiodataPersonTypes> criteria = cb.createQuery(BiodataPersonTypes.class);
        Root<BiodataPersonTypes> podStatus = criteria.from(BiodataPersonTypes.class);

        criteria = criteria.multiselect(podStatus.get("id"), podStatus.get("biodataTypeName"), podStatus.get("created"));

        return entManager.createQuery(criteria).getResultList();
    }

    public BiodataPersonTypes findBiodataPersonTypesById(Long bio_id) {

        BiodataPersonTypes bioPerson = null;

        if (bio_id != null) {

            CriteriaBuilder cb = getEm().getCriteriaBuilder();

            CriteriaQuery<BiodataPersonTypes> criteria = cb.createQuery(BiodataPersonTypes.class);
            Root<BiodataPersonTypes> personBio = criteria.from(BiodataPersonTypes.class);

            criteria = criteria.multiselect(personBio.get("id"), personBio.get("biodataTypeName"), personBio.get("created")).where(cb.equal(personBio.get("id"), bio_id));

            bioPerson = entManager.createQuery(criteria).getSingleResult();
        }

        return bioPerson;
    }

    public PropertyClassifications findPropertyUseById(Long prop_use_id) {

        PropertyClassifications propUse = null;

        if (prop_use_id != null) {

            CriteriaBuilder cb = getEm().getCriteriaBuilder();

            CriteriaQuery<PropertyClassifications> criteria = cb.createQuery(PropertyClassifications.class);
            Root<PropertyClassifications> personBio = criteria.from(PropertyClassifications.class);

            criteria = criteria.multiselect(personBio.get("id"), personBio.get("classificationName"), personBio.get("description")).where(cb.equal(personBio.get("id"), prop_use_id));

            propUse = entManager.createQuery(criteria).getSingleResult();
        }

        return propUse;
    }

    public ResidentialTypes findResidentialTypeById(Long type_id) {

        ResidentialTypes residType = null;

        if (type_id != null) {

            CriteriaBuilder cb = getEm().getCriteriaBuilder();

            CriteriaQuery<ResidentialTypes> criteria = cb.createQuery(ResidentialTypes.class);
            Root<ResidentialTypes> personBio = criteria.from(ResidentialTypes.class);

            criteria = criteria.multiselect(personBio.get("id"), personBio.get("residentialTypeName"), personBio.get("created")).where(cb.equal(personBio.get("id"), type_id));

            residType = entManager.createQuery(criteria).getSingleResult();
        }

        return residType;
    }

    public CommercialTypes findCommercialTypeById(Long type_id) {

        CommercialTypes commerceType = null;

        if (type_id != null) {

            CriteriaBuilder cb = getEm().getCriteriaBuilder();

            CriteriaQuery<CommercialTypes> criteria = cb.createQuery(CommercialTypes.class);
            Root<CommercialTypes> personBio = criteria.from(CommercialTypes.class);

            criteria = criteria.multiselect(personBio.get("id"), personBio.get("commercialTypeName"), personBio.get("created")).where(cb.equal(personBio.get("id"), type_id));

            commerceType = entManager.createQuery(criteria).getSingleResult();
        }

        return commerceType;
    }

    public DocumentTypes findDocumentTitleTypeById(Long type_id) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<DocumentTypes> criteria = cb.createQuery(DocumentTypes.class);
        Root<DocumentTypes> doc_title_types = criteria.from(DocumentTypes.class);

        criteria = criteria.multiselect(doc_title_types.get("id"), doc_title_types.get("documentTypeName"), doc_title_types.get("created")).where(cb.equal(doc_title_types.get("id"), type_id));

        return entManager.createQuery(criteria).getSingleResult();
    }

    public ServiceTypes findServiceTypesById(Long service_type_id) {

        ServiceTypes commerceType = null;

        if (service_type_id != null) {

            CriteriaBuilder cb = getEm().getCriteriaBuilder();

            CriteriaQuery<ServiceTypes> criteria = cb.createQuery(ServiceTypes.class);
            Root<ServiceTypes> personBio = criteria.from(ServiceTypes.class);

            criteria = criteria.multiselect(personBio.get("id"), personBio.get("serviceTypeName"), personBio.get("created")).where(cb.equal(personBio.get("id"), service_type_id));

            commerceType = entManager.createQuery(criteria).getSingleResult();
        }

        return commerceType;
    }

    public List<LocalCouncilDevArea> fetchAllUnassignedLcdas() {
        List<LocalCouncilDevArea> lcdas;

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<LocalCouncilDevArea> criteria = cb.createQuery(LocalCouncilDevArea.class);
        Root<LocalCouncilDevArea> lcdaObj = criteria.from(LocalCouncilDevArea.class);

        Predicate predicate = cb.isNull(lcdaObj.get("contractorId"));

        criteria = criteria.multiselect(lcdaObj.get("id"), lcdaObj.get("lcdaName"), lcdaObj.get("created")).where(predicate);

        lcdas = entManager.createQuery(criteria).getResultList();

        return lcdas;
    }

    public List<SenatorialDistricts> fetchAllSenatorialDistricts() {
        List<SenatorialDistricts> snds;

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<SenatorialDistricts> criteria = cb.createQuery(SenatorialDistricts.class);
        Root<SenatorialDistricts> senDist = criteria.from(SenatorialDistricts.class);

        criteria = criteria.multiselect(senDist.get("id"), senDist.get("districtName"), senDist.get("senatorialCode"));

        snds = entManager.createQuery(criteria).getResultList();

        return snds;
    }

    public SenatorialDistricts findSenatorialDistrictById(Long sen_district_id) {

        SenatorialDistricts sen_district = null;

        if (sen_district_id != null) {

            try {
                sen_district = entManager.createQuery("FROM SenatorialDistricts s WHERE s.id=:tempId", SenatorialDistricts.class).setParameter("tempId", sen_district_id).getSingleResult();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(ReferenceDataService.class.getName()).log(Level.SEVERE, "Did not find SenatorialDistricts: ", nr);
            }
        }

        return sen_district;
    }

    public OrganizationTypes findOrganizationTypesById(Long org_type_id) {

        OrganizationTypes organ_type = null;

        if (org_type_id != null) {

            try {
                organ_type = entManager.createQuery("FROM OrganizationTypes o WHERE o.id=:tempId", OrganizationTypes.class).setParameter("tempId", org_type_id).getSingleResult();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Did not find OrganizationTypes: ", nr);
            }
        }

        return organ_type;
    }

    public LocalCouncilDevArea findLocalCouncilDevAreaById(Long org_type_id) {

        LocalCouncilDevArea organ_type = null;

        if (org_type_id != null) {

            try {
                organ_type = entManager.createQuery("FROM LocalCouncilDevArea l WHERE l.id=:tempId", LocalCouncilDevArea.class).setParameter("tempId", org_type_id).getSingleResult();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Did not find LocalCouncilDevArea: ", nr);
            }
        }

        return organ_type;
    }

    public WardStreets findStreetInLocalGovtArea(Long lcda_id, String street_name) {

        WardStreets organ_type = null;

        if (lcda_id != null && street_name != null && !street_name.isEmpty()) {

            try {
                organ_type = entManager.createQuery("FROM WardStreets s WHERE s.lcdaWardId.id=:lcdaID AND s.streetName=:str_name AND s.isVerified=true", WardStreets.class).setParameter("lcdaID", lcda_id)
                        .setParameter("str_name", street_name).getSingleResult();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(LandPropertiesDataService.class.getName()).log(Level.SEVERE, "Street does not exist in LGA: {0}", street_name);
            } catch (NonUniqueResultException nunq) {
                String to_address[] = {"sfagade@ic-sol.net"};
                String email_message = "Duplicate Street found. Street name is: " + street_name + ". Local govt id is: " + lcda_id;
                try {
                    emailSender.sendPlainEmailMessage(email_message, "LEDAR Duplicate Street Report", to_address);
                } catch (MessagingException ex) {
                    Logger.getLogger(ReferenceDataService.class.getName()).log(Level.SEVERE, null, ex);
                }
                Logger.getLogger(ReferenceDataService.class.getName()).log(Level.SEVERE, "Duplicate Street: {0}", street_name);
            }
        }

        return organ_type;
    }

    public List<PropertyQualities> fetchAllPropertyQualities() {
        List<PropertyQualities> propQlty;

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<PropertyQualities> criteria = cb.createQuery(PropertyQualities.class);
        Root<PropertyQualities> senDist = criteria.from(PropertyQualities.class);

        criteria = criteria.multiselect(senDist.get("id"), senDist.get("qualityName"), senDist.get("percentageValue"), senDist.get("created"));

        propQlty = entManager.createQuery(criteria).getResultList();

        return propQlty;
    }

    public PropertyQualities findPropertyQualityById(Long qulty_id) {

        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<PropertyQualities> criteria = cb.createQuery(PropertyQualities.class);
        Root<PropertyQualities> propQtly = criteria.from(PropertyQualities.class);
        if (qulty_id != null) {
            criteria = criteria.multiselect(propQtly.get("id"), propQtly.get("qualityName"), propQtly.get("percentageValue"), propQtly.get("created")).where(cb.equal(propQtly.get("id"), qulty_id));

            return entManager.createQuery(criteria).getSingleResult();
        } else {
            return null;
        }
    }

    public List<ComplaintSources> fetchAllComplaintSources() {
        List<ComplaintSources> complnts;

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<ComplaintSources> criteria = cb.createQuery(ComplaintSources.class);
        Root<ComplaintSources> lcdaObj = criteria.from(ComplaintSources.class);

        criteria = criteria.multiselect(lcdaObj.get("id"), lcdaObj.get("sourceName"), lcdaObj.get("created"));

        complnts = entManager.createQuery(criteria).getResultList();

        return complnts;
    }

    public LcdaWards findLcdaWardsById(Long word_id) {

        LcdaWards district = null;

        if (word_id != null) {

            CriteriaBuilder cb = getEm().getCriteriaBuilder();

            CriteriaQuery<LcdaWards> criteria = cb.createQuery(LcdaWards.class);
            Root<LcdaWards> lcdaWard = criteria.from(LcdaWards.class);

            criteria = criteria.multiselect(lcdaWard.get("id"), lcdaWard.get("wardName"), lcdaWard.get("localCouncilDevAreaId"), lcdaWard.get("created")).where(cb.equal(lcdaWard.get("id"), word_id));

            district = entManager.createQuery(criteria).getSingleResult();
        }

        return district;
    }

    public List<ResidentialUseages> fetchAllResidentialUseages() {
        List<ResidentialUseages> residUsage;

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<ResidentialUseages> criteria = cb.createQuery(ResidentialUseages.class);
        Root<ResidentialUseages> residObj = criteria.from(ResidentialUseages.class);

        criteria = criteria.multiselect(residObj.get("id"), residObj.get("residentialUseName"), residObj.get("created"), residObj.get("initsMapping"));
        residUsage = entManager.createQuery(criteria).getResultList();

        return residUsage;
    }

    public List<UsedPropertyIds> fetchAllUsedPropertyPins(Date last_use_time) {

        if (last_use_time != null) {
            return entManager.createQuery("FROM UsedPropertyIds u WHERE u.useDate > :use_date", UsedPropertyIds.class).setParameter("use_date", last_use_time).getResultList();
        } else {
            return entManager.createQuery("FROM UsedPropertyIds u", UsedPropertyIds.class).getResultList();
        }
    }

    public List<PrintedBills> fetchAllBillsPins(Date last_update_time, String local_govt) {

        List<PrintedBills> bills;
        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<PrintedBills> criteria = cb.createQuery(PrintedBills.class);
        Root<PrintedBills> billObj = criteria.from(PrintedBills.class);

        List<Predicate> predicates = new ArrayList<>();

        if (last_update_time != null) {
            predicates.add(cb.equal(billObj.get("lga"), local_govt));
            predicates.add(cb.greaterThan(billObj.<Date>get("created"), last_update_time));
            criteria = criteria.multiselect(billObj.get("id"), billObj.get("propertyIdn"), billObj.get("lga"), billObj.get("houseNo"), billObj.get("plotNo"), billObj.get("blockNo"),
                    billObj.get("flatNo"), billObj.get("streetName"), billObj.get("districtName"), billObj.get("ownerName"), billObj.get("created")).where(predicates.toArray(new Predicate[]{}));

        } else {
            criteria = criteria.multiselect(billObj.get("id"), billObj.get("propertyIdn"), billObj.get("lga"), billObj.get("houseNo"), billObj.get("plotNo"), billObj.get("blockNo"),
                    billObj.get("flatNo"), billObj.get("streetName"), billObj.get("districtName"), billObj.get("ownerName"), billObj.get("created")).where(cb.equal(billObj.get("lga"), local_govt));
            //return entManager.createQuery("FROM PrintedBills u WHERE u.lga = :localgovt", PrintedBills.class).setParameter("localgovt", local_govt).getResultList();
        }
        bills = entManager.createQuery(criteria).getResultList();
        return bills;
    }

    public List<Organizations> fetchAllOrganizationsByType(Long org_type) {

        if (org_type != null) {
            return entManager.createQuery("FROM Organizations u WHERE u.organizationTypeId.id = :type_id", Organizations.class).setParameter("type_id", org_type).getResultList();
        }
        return null;
    }

    public List<ComplaintTypes> fetchAllComplaintTypes() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<ComplaintTypes> criteria = cb.createQuery(ComplaintTypes.class);
        Root<ComplaintTypes> pple_title = criteria.from(ComplaintTypes.class);
        criteria.orderBy(cb.asc(pple_title.get("typeName")));
        criteria = criteria.multiselect(pple_title.get("id"), pple_title.get("typeName"), pple_title.get("created"));

        return entManager.createQuery(criteria).getResultList();
    }

    public List<DocumentTypes> fetchAllDocumentTypes() {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<DocumentTypes> criteria = cb.createQuery(DocumentTypes.class);
        Root<DocumentTypes> pple_title = criteria.from(DocumentTypes.class);
        criteria.orderBy(cb.asc(pple_title.get("documentTypeName")));
        criteria = criteria.multiselect(pple_title.get("id"), pple_title.get("documentTypeName"), pple_title.get("created"));

        return entManager.createQuery(criteria).getResultList();
    }

    public ComplaintStatus findComplaintStatusById(Long status_id) {

        ComplaintStatus status = null;

        if (status_id != null) {

            CriteriaBuilder cb = getEm().getCriteriaBuilder();

            CriteriaQuery<ComplaintStatus> criteria = cb.createQuery(ComplaintStatus.class);
            Root<ComplaintStatus> lcdaWard = criteria.from(ComplaintStatus.class);

            criteria = criteria.multiselect(lcdaWard.get("id"), lcdaWard.get("statusName"), lcdaWard.get("created")).where(cb.equal(lcdaWard.get("id"), status_id));

            status = entManager.createQuery(criteria).getSingleResult();

        }

        return status;
    }

    public List<VisitPurpose> fetchAllVisitPurposes(Long purpose_id) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<VisitPurpose> criteria = cb.createQuery(VisitPurpose.class);
        Root<VisitPurpose> vi_purp = criteria.from(VisitPurpose.class);
        criteria.orderBy(cb.asc(vi_purp.get("purposeName")));
        if (purpose_id != null) {
            criteria = criteria.multiselect(vi_purp.get("id"), vi_purp.get("purposeName"), vi_purp.get("created")).where(cb.equal(vi_purp.get("id"), purpose_id));
        } else {
            criteria = criteria.multiselect(vi_purp.get("id"), vi_purp.get("purposeName"), vi_purp.get("created"));
        }

        return entManager.createQuery(criteria).getResultList();
    }

    public List<ComplainerRelationship> fetchAllRelationships(Long relationship_id) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();

        CriteriaQuery<ComplainerRelationship> criteria = cb.createQuery(ComplainerRelationship.class);
        Root<ComplainerRelationship> rlship = criteria.from(ComplainerRelationship.class);
        criteria.orderBy(cb.asc(rlship.get("relationshipName")));
        if (relationship_id != null) {
            criteria = criteria.multiselect(rlship.get("id"), rlship.get("relationshipName"), rlship.get("created")).where(cb.equal(rlship.get("id"), relationship_id));
        } else {
            criteria = criteria.multiselect(rlship.get("id"), rlship.get("relationshipName"), rlship.get("created"));
        }

        return entManager.createQuery(criteria).getResultList();
    }

    @TransactionAttribute(REQUIRES_NEW)
    public List<WardStreets> fetchStreetByStreetName(String street_name, Boolean verified) {
        List<WardStreets> streets = null;

        CriteriaBuilder qb = entManager.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<WardStreets> strObj = cq.from(WardStreets.class);
        List<Predicate> predicates = new ArrayList<>();

        if (street_name != null) {
            predicates.add(qb.equal(strObj.<String>get("streetName"), street_name));
            if (verified != null) {
                predicates.add(qb.equal(strObj.<Boolean>get("isVerified"), verified));
            }
            try {
                streets = entManager.createQuery(cq.select(strObj).where(predicates.toArray(new Predicate[]{})).orderBy(qb.desc(strObj.get("created")))).getResultList();
            } catch (javax.persistence.NoResultException nr) {
                Logger.getLogger(ReferenceDataService.class.getName()).log(Level.SEVERE, "Street not found with name: {0}", street_name);
            }
        }

        return streets;
    }
}
