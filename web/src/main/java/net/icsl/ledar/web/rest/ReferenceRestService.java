package net.icsl.ledar.web.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.icsl.ledar.ejb.model.*;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.ReferenceDataService;

import net.icsl.ledar.ejb.dto.LCDADto;
import net.icsl.ledar.ejb.enums.*;
import net.icsl.ledar.ejb.dto.PrintedBillsSmallDto;

/**
 * @author sfagade
 * @date Nov 20, 2015
 */
@Path("/referenceService")
@RequestScoped
public class ReferenceRestService {

    @Inject
    private LcdaWardsDataServices lcdaService;
    @Inject
    private ReferenceDataService refService;

    @GET
    @Path("/fetchAllLcdaWards/{lcda_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LcdaWards> fecthAllLcdaWards(@PathParam("lcda_id") String lcda_id) {

        List<LcdaWards> lcdaWards;
        List<LcdaWards> shortLcdaWard = new ArrayList<>();

        if (lcda_id != null) {
            lcdaWards = lcdaService.fetchAllLcdaWardsByLcda(Long.valueOf(lcda_id), null);

            if (lcdaWards != null) {
                for (LcdaWards lcdWard_ : lcdaWards) {
                    shortLcdaWard.add(new LcdaWards(lcdWard_.getId(), lcdWard_.getWardName(), lcdWard_.getLocalCouncilDevAreaId().getId()));
                }
            }

        }

        return shortLcdaWard;
    }

    @GET
    @Path("/fecthAllSdLcdas/{sd_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LCDADto> fecthAllSdLcdas(@PathParam("sd_id") String sd_id) {

        List<LocalCouncilDevArea> lcdas;
        List<LCDADto> shortLcda = new ArrayList<>();

        if (sd_id == null) {//default the selected senatorial district to Lagos East
            sd_id = "2";
        }
        lcdas = lcdaService.fetchAllSenatorialDistrictLCDAs(Long.valueOf(sd_id));

        if (lcdas != null) {
            for (LocalCouncilDevArea lcda_ : lcdas) {
                shortLcda.add(new LCDADto(lcda_.getId(), lcda_.getLcdaName(), Long.valueOf(sd_id), null, lcda_.getRemarks(), lcda_.getCreated(), lcda_.getModified()));
            }
        }

        return shortLcda;
    }

    @GET
    @Path("/fecthAllQualifications")
    @Produces(MediaType.APPLICATION_JSON)
    public EducationalQualifications[] fecthAllQualifications() {

        /*ArrayList qualification = new ArrayList() ;
         qualification.add(EducationalQualifications.values());*/
        return EducationalQualifications.values();
    }

    @GET
    @Path("/fecthAllEmploymentStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public EmploymentStatus[] fecthAllEmploymentStatus() {

        /*ArrayList qualification = new ArrayList() ;
         qualification.add(EducationalQualifications.values());*/
        return EmploymentStatus.values();
    }

    @GET
    @Path("/fecthAllFacilitySectors")
    @Produces(MediaType.APPLICATION_JSON)
    public FacilitySectors[] fecthAllFacilitySectors() {

        /*ArrayList qualification = new ArrayList() ;
         qualification.add(EducationalQualifications.values());*/
        return FacilitySectors.values();
    }

    @GET
    @Path("/fecthAllMaritalStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public MarritalStatus[] fecthAllMaritalStatus() {

        /*ArrayList qualification = new ArrayList() ;
         qualification.add(EducationalQualifications.values());*/
        return MarritalStatus.values();
    }

    @GET
    @Path("/fecthAllOtherPropertyUse")
    @Produces(MediaType.APPLICATION_JSON)
    public OtherPropertyUse[] fecthAllOtherPropertyUse() {

        /*ArrayList qualification = new ArrayList() ;
         qualification.add(EducationalQualifications.values());*/
        return OtherPropertyUse.values();
    }

    @GET
    @Path("/fecthAllPropertyTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public PropertyTypes[] fecthAllPropertyTypes() {

        /*ArrayList qualification = new ArrayList() ;
         qualification.add(EducationalQualifications.values());*/
        return PropertyTypes.values();
    }

    @GET
    @Path("/fecthAllTarredRoads")
    @Produces(MediaType.APPLICATION_JSON)
    public TarredRoads[] fecthAllTarredRoads() {

        /*ArrayList qualification = new ArrayList() ;
         qualification.add(EducationalQualifications.values());*/
        return TarredRoads.values();
    }

    @GET
    @Path("/fecthAllUntarredRoads")
    @Produces(MediaType.APPLICATION_JSON)
    public UntarredRoads[] fecthAllUntarredRoads() {

        /*ArrayList qualification = new ArrayList() ;
         qualification.add(EducationalQualifications.values());*/
        return UntarredRoads.values();
    }

    @GET
    @Path("/fecthAllWasteDisposalSystem")
    @Produces(MediaType.APPLICATION_JSON)
    public WasteDisposalSystem[] fecthAllWasteDisposalSystem() {

        /*ArrayList qualification = new ArrayList() ;
         qualification.add(EducationalQualifications.values());*/
        return WasteDisposalSystem.values();
    }

    @GET
    @Path("/fecthAllWaterSupply")
    @Produces(MediaType.APPLICATION_JSON)
    public WaterSupply[] fecthAllfecthAllWaterSupply() {

        /*ArrayList qualification = new ArrayList() ;
         qualification.add(EducationalQualifications.values());*/
        return WaterSupply.values();
    }

    @GET
    @Path("/fecthAllCommercialType")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CommercialTypes> fecthAllCommercialType() {

        List<CommercialTypes> comTypes;
        List<CommercialTypes> shortcomType = new ArrayList<>();

        // if (lcda_id != null) {
        comTypes = refService.fetchAllcommercialTypes();

        if (comTypes != null) {
            for (CommercialTypes comType_ : comTypes) {
                shortcomType.add(new CommercialTypes(comType_.getId(), comType_.getCommercialTypeName(), comType_.getCreated()));
            }
        }

        // }
        return shortcomType;
    }

    @GET
    @Path("/fecthAlLGeoType")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GeographicalBoundaryTypes> fecthAllGeoType() {

        List<GeographicalBoundaryTypes> geoTypes;
        List<GeographicalBoundaryTypes> shortgeoType = new ArrayList<>();

        // if (lcda_id != null) {
        geoTypes = refService.fetchAllGeoTypes();

        if (geoTypes != null) {
            for (GeographicalBoundaryTypes geoType_ : geoTypes) {
                shortgeoType.add(new GeographicalBoundaryTypes(geoType_.getId(), geoType_.getBoundaryTypeName()));
            }
        }

        // }
        return shortgeoType;
    }

    @GET
    @Path("/fecthGeotypeById/{gt_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeographicalBoundaryTypes fecthGeotypeById(@PathParam("gt_id") String gt_id) {

        GeographicalBoundaryTypes geoTypes;
        GeographicalBoundaryTypes shortgeoType = new GeographicalBoundaryTypes();

        geoTypes = refService.fetchAllGeoTypeById(Long.valueOf(gt_id));

        return geoTypes;
    }

    @GET
    @Path("/fecthGeoBoundaryById/{gt_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeographicalBoundaries fecthGeoBoundaryById(@PathParam("gt_id") String gt_id) {

        GeographicalBoundaries geoBundries;

        geoBundries = refService.fetchAllGeoBoundries(Long.valueOf(gt_id));

        return geoBundries;
    }

    @GET
    @Path("/fecthOccupations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Occupations> fecthOccupations() {

        List<Occupations> occupations;
        List<Occupations> arr_occupations = new ArrayList<>();

        occupations = refService.fetchAllOccupations();

        if (occupations != null) {
            for (Occupations occupation_ : occupations) {
                arr_occupations.add(new Occupations(occupation_.getId(), occupation_.getOccupationName()));
            }
        }

        return arr_occupations;
    }

    @GET
    @Path("/fecthOrganizationTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrganizationTypes> fecthOrganizationTypes() {

        List<OrganizationTypes> organizationTypes;
        List<OrganizationTypes> arr_organizationTypes = new ArrayList<>();

        organizationTypes = refService.fetchAllOrganizationType();

        if (organizationTypes != null) {
            for (OrganizationTypes organizationTypes_ : organizationTypes) {
                arr_organizationTypes.add(new OrganizationTypes(organizationTypes_.getId(), organizationTypes_.getTypeName()));
            }
        }

        return arr_organizationTypes;
    }

    @GET
    @Path("/fecthPeopleTitle")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PersonTitles> fecthPeopleTitle() {

        List<PersonTitles> pple_title;
        List<PersonTitles> arr_pple_title = new ArrayList<>();

        pple_title = refService.fetchAllTittle();

        if (pple_title != null) {
            for (PersonTitles personTitle_ : pple_title) {
                arr_pple_title.add(new PersonTitles(personTitle_.getId(), personTitle_.getTitleName()));
            }
        }

        return arr_pple_title;
    }

    @GET
    @Path("/fecthAllPropertyUse")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyClassifications> fecthAllPropertyUse() {

        List<PropertyClassifications> property_use;
        List<PropertyClassifications> arr_property_use = new ArrayList<>();

        // if (lcda_id != null) {
        property_use = refService.fetchAllPropertyUse();

        if (property_use != null) {
            for (PropertyClassifications property_use_ : property_use) {
                arr_property_use.add(new PropertyClassifications(property_use_.getId(), property_use_.getClassificationName()));
            }
        }

        // }
        return arr_property_use;
    }

    @GET
    @Path("/fecthAllReligions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Religions> fecthAllReligions() {

        List<Religions> religions;
        List<Religions> arr_religions = new ArrayList<>();

        // if (lcda_id != null) {
        religions = refService.fetchAllReligion();

        if (religions != null) {
            for (Religions religions_ : religions) {
                arr_religions.add(new Religions(religions_.getId(), religions_.getReligionName()));
            }
        }

        // }
        return arr_religions;
    }

    @GET
    @Path("/fecthAllResidentialTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ResidentialTypes> fecthAllResidentialTypes() {

        List<ResidentialTypes> residentialTypes;
        List<ResidentialTypes> arr_residentialTypes = new ArrayList<>();

        // if (lcda_id != null) {
        residentialTypes = refService.fetchAllResidentialTypes();

        if (residentialTypes != null) {
            for (ResidentialTypes residentialTypes_ : residentialTypes) {
                arr_residentialTypes.add(new ResidentialTypes(residentialTypes_.getId(), residentialTypes_.getResidentialTypeName(), residentialTypes_.getCreated()));
            }
        }

        // }
        return arr_residentialTypes;
    }

    @GET
    @Path("/fecthAllUserRoles")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserRoles> fecthAllUserRoles() {

        List<UserRoles> userRoles;
        List<UserRoles> arr_userRoles = new ArrayList<>();

        // if (lcda_id != null) {
        userRoles = refService.fetchAllUserRoles();

        if (userRoles != null) {
            for (UserRoles userRoles_ : userRoles) {
                arr_userRoles.add(new UserRoles(userRoles_.getId(), userRoles_.getDescription()));
            }
        }

        // }
        return arr_userRoles;
    }

    @GET
    @Path("/fecthAllSettlementTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SettlementTypes> fecthAllSettlementTypes() {

        List<SettlementTypes> settlementTypes;
        List<SettlementTypes> arr_settlementTypes = new ArrayList<>();

        // if (lcda_id != null) {
        settlementTypes = refService.fetchAllSettlementType();

        if (settlementTypes != null) {
            for (SettlementTypes settlementTypes_ : settlementTypes) {
                arr_settlementTypes.add(new SettlementTypes(settlementTypes_.getId(), settlementTypes_.getSettlementTypeName()));
            }
        }

        // }
        return arr_settlementTypes;
    }

    @GET
    @Path("/fecthAllStreetTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StreetTypes> fecthAllStreetTypes() {

        List<StreetTypes> streetTypes;
        List<StreetTypes> arr_streetTypes = new ArrayList<>();

        // if (lcda_id != null) {
        streetTypes = refService.fetchAllStreetType();

        if (streetTypes != null) {
            for (StreetTypes streetTypes_ : streetTypes) {
                arr_streetTypes.add(new StreetTypes(streetTypes_.getId(), streetTypes_.getStreetTypeName()));
            }
        }

        // }
        return arr_streetTypes;
    }

    @GET
    @Path("/fecthAllDocumetTitleTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TitleDocumentTypes> fecthAllDocumetTitleTypes() {

        List<TitleDocumentTypes> docTitleTypes;
        List<TitleDocumentTypes> arr_docTitleTypes = new ArrayList<>();

        // if (lcda_id != null) {
        docTitleTypes = refService.fetchAllDocumentTitleTypes();

        if (docTitleTypes != null) {
            for (TitleDocumentTypes docTitleTypes_ : docTitleTypes) {
                arr_docTitleTypes.add(new TitleDocumentTypes(docTitleTypes_.getId(), docTitleTypes_.getTitleName()));
            }
        }

        // }
        return arr_docTitleTypes;
    }

    @GET
    @Path("/fecthAllStreetByWardId/{ward_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<WardStreets> fecthAllStreetByWardId(@PathParam("ward_id") String ward_id) {

        List<WardStreets> streetByWards;
        List<WardStreets> arr_streetByWards = new ArrayList<>();

        // if (lcda_id != null) {
        streetByWards = refService.fetchAllStreetByWardId(Long.valueOf(ward_id));

        if (streetByWards != null) {
            for (WardStreets streetByWards_ : streetByWards) {
                arr_streetByWards.add(new WardStreets(streetByWards_.getId(), streetByWards_.getStreetName(), streetByWards_.getOffStreetOne(), streetByWards_.getNearestBusStop(),
                        streetByWards_.getLcdaWardId().getId(), streetByWards_.getCreated()));
            }
        }

        // }
        return arr_streetByWards;
    }

    @GET
    @Path("/fecthAllTownByWardId/{ward_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<WardTowns> fecthAllTownByWardId(@PathParam("ward_id") String ward_id) {

        List<WardTowns> townByWards;
        List<WardTowns> arr_townByWards = new ArrayList<>();

        // if (lcda_id != null) {
        townByWards = refService.fetchAllTownByWardId(Long.valueOf(ward_id));

        if (townByWards != null) {
            for (WardTowns townByWards_ : townByWards) {
                arr_townByWards.add(new WardTowns(townByWards_.getId(), townByWards_.getTownName(), townByWards_.getCreated()));
            }
        }

        // }
        return arr_townByWards;
    }

    @GET
    @Path("/fecthAllLandPropertiesByWardId/{ward_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<WardLandProperties> fecthAllLandPropertiesByWardId(@PathParam("ward_id") String ward_id) {

        List<WardLandProperties> landPropertiesByWards;
        List<WardLandProperties> arr_landPropertiesByWards = new ArrayList<>();

        // if (lcda_id != null) {
        landPropertiesByWards = refService.fetchAllLandPropertiesByWardId(Long.valueOf(ward_id));

        if (landPropertiesByWards != null) {
            for (WardLandProperties landPropertiesByWards_ : landPropertiesByWards) {
                arr_landPropertiesByWards.add(new WardLandProperties(landPropertiesByWards_.getId(), landPropertiesByWards_.getPropertyId(),
                        landPropertiesByWards_.getPropertyNumber(), landPropertiesByWards_.getRoadSide(), landPropertiesByWards_.getPropertyLongitude(),
                        landPropertiesByWards_.getPropertyLatitude(), landPropertiesByWards_.getDateCaptured(), landPropertiesByWards_.getPropertyTypes(),
                        landPropertiesByWards_.getDistrictName(),
                        landPropertiesByWards_.getLgaName()));
            }
        }

        // }
        return arr_landPropertiesByWards;
    }

    @GET
    @Path("/fecthBiodataTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BiodataPersonTypes> fecthAllBiodataPersonTypes() {

        List<BiodataPersonTypes> bioType;
        List<BiodataPersonTypes> retBioType;

        bioType = refService.fetchAllBiodataPersonTypes();

        if (bioType != null) {
            for (BiodataPersonTypes bio : bioType) {

            }
        }

        return bioType;
    }

    @GET
    @Path("/fecthServiceTypes")//ServiceTypes
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceTypes> fecthAllServiceTypes() {

        List<ServiceTypes> servType;
        List<ServiceTypes> retServType = new ArrayList<>();

        servType = refService.fetchAllServiceTypes();

        if (servType != null) {
            for (ServiceTypes st : servType) {//Long serviceTypeId, String serviceTypeName
                retServType.add(new ServiceTypes(st.getId(), st.getServiceTypeName(), st.getCreated()));

            }
        }

        return retServType;
    }

    @GET
    @Path("/fecthResidentialUsages")//ServiceTypes
    @Produces(MediaType.APPLICATION_JSON)
    public List<ResidentialUseages> fecthAllResidentialUseages() {

        List<ResidentialUseages> residUse;
        List<ResidentialUseages> residUseObj = new ArrayList<>();

        residUse = refService.fetchAllResidentialUseages();

        if (residUse != null) {
            for (ResidentialUseages resdUse : residUse) {//Long serviceTypeId, String serviceTypeName
                residUseObj.add(new ResidentialUseages(resdUse.getId(), resdUse.getResidentialUseName(), resdUse.getCreated()));

            }
        }

        return residUseObj;
    }

    @GET
    @Path("/fetchAllUsedPins/{used_time}")//ServiceTypes
    @Produces(MediaType.APPLICATION_JSON)
    public List<UsedPropertyIds> fetchAllUsedPins(@PathParam("used_time") String last_use_time) {

        List<UsedPropertyIds> residUse;

        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH);
        Date lastUsedDate = null;
        last_use_time = (last_use_time != null) ? last_use_time.replace("+", " ") : null;

        try {
            lastUsedDate = formatter.parse(last_use_time);
        } catch (ParseException pe) {
            Logger.getLogger(ReferenceRestService.class.getName()).log(Level.SEVERE, "Failed to parse date for used pins", pe);
        }

        residUse = refService.fetchAllUsedPropertyPins(lastUsedDate);

        return residUse;
    }

    @GET
    @Path("/fecthAllPropertyQualities")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyQualities> fecthAllPropertyQualities() {

        List<PropertyQualities> proQties;

        proQties = refService.fetchAllPropertyQualities();

        return proQties;
    }

    @GET
    @Path("/fetchAllBillsPin/{last_update}/{local_govt}")//ServiceTypes
    @Produces(MediaType.APPLICATION_JSON)
    public List<PrintedBillsSmallDto> fetchAllBillsPin(@PathParam("last_update") String last_update_time, @PathParam("local_govt") String local_govt) {

        List<PrintedBills> bills_pins;
        List<PrintedBillsSmallDto> refined_bills = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH);
        Date lastUsedDate = null;
        String address_;
        last_update_time = (last_update_time != null) ? last_update_time.replace("+", " ") : null;

        try {
            lastUsedDate = formatter.parse(last_update_time);
        } catch (ParseException pe) {
            Logger.getLogger(ReferenceRestService.class.getName()).log(Level.SEVERE, "Failed to parse date for used pins", pe);
        }

        bills_pins = refService.fetchAllBillsPins(lastUsedDate, local_govt);

        if (bills_pins != null && bills_pins.size() > 0) {
            for (PrintedBills bill : bills_pins) {

                address_ = "Owner: " + bill.getOwnerName() + ". Address: ";

                if (bill.getHouseNo() != null && !bill.getHouseNo().isEmpty()) {
                    address_ += bill.getHouseNo()+", ";
                } else if (bill.getPlotNo() != null && (!bill.getPlotNo().isEmpty())) {
                    address_ += bill.getPlotNo()+", ";
                } else if (bill.getBlockNo() != null && (!bill.getBlockNo().isEmpty())) {
                    address_ = bill.getBlockNo()+", ";
                } else if(bill.getFlatNo() != null && (!bill.getFlatNo().isEmpty())){
                    address_ += bill.getFlatNo()+", ";
                } 

                address_ += bill.getStreetName() + ", " + bill.getDistrictName() + ", " + bill.getLga();

                refined_bills.add(new PrintedBillsSmallDto(bill.getId(), bill.getPropertyIdn(), bill.getLga(), address_, bill.getCreated()));
            }
        }

        return refined_bills;
    }

    @GET
    @Path("/fecthAllVisitPurpose")
    @Produces(MediaType.APPLICATION_JSON)
    public List<VisitPurpose> fecthAllVisitPurpose() {

        List<VisitPurpose> proQties;

        proQties = refService.fetchAllVisitPurposes(null);

        return proQties;
    }

    @GET
    @Path("/fecthAllComplainerRelationship")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ComplainerRelationship> fecthAllComplainerRelationship() {

        List<ComplainerRelationship> proQties;

        proQties = refService.fetchAllRelationships(null);

        return proQties;
    }

    @GET
    @Path("/fecthAllUpdateStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Map> fecthAllUpdateStatus() {

        //String[] status = new String[PropertyUpdateStatusEnum.values().length];
        Map<String, String> status;
        ArrayList<Map> allStatus = new ArrayList<>();
        //int counter = 1;

        for (PropertyUpdateStatusEnum st : PropertyUpdateStatusEnum.values()) {
            status = new HashMap<>();
            status.put("name", st.getStatus());
            allStatus.add(status);
            //counter++;
        }

        return allStatus;
    }

    @GET
    @Path("/fetchUsageCategories/{usage_id}/{category_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyUsageCategories> fetchPropertyUsageCategories(@PathParam("usage_id")String usage, @PathParam("category_id")String category) {
        List<PropertyUsageCategories> usageCategories;
        Long usage_id = null, category_id = null;

        if (!usage.equals("0")) {
            usage_id = Long.valueOf(usage);
        }

        if (!category.equals("0")) {
            category_id = Long.valueOf(category);
        }

        usageCategories = refService.fetchPropertyUsageCategories(usage_id, category_id);

        return usageCategories;
    }

}
