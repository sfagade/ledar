package net.icsl.ledar.ejb.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import net.icsl.ledar.ejb.dto.WardPropertiesDto;
import net.icsl.ledar.ejb.model.WardLandProperties;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;

@Stateless
@LocalBean
public class WardPropertiesDao {

    @Inject
    private LandPropertiesDataService propertyService;

    /**
     * Default constructor.
     */
    public WardPropertiesDao() {
        // TODO Auto-generated constructor stub
    }

    public List<WardPropertiesDto> enumerationDetails(Map<String, String> criteria) {

        List<WardPropertiesDto> propertiesDto = null;
        List<WardLandProperties> properties = propertyService.filterWardLandPropertyList(criteria);
        WardPropertiesDto propDto;
        String address;

        if (properties != null && properties.size() > 0) {
            propertiesDto = new ArrayList<>();
            for (WardLandProperties property : properties) {
                //Long prop_id, String property_idn, String longitude, String latitude, String address, String captured_by_name, Date sync_date, Date captured_date
                address = (property.getPropertyNumber() != null) ? property.getPropertyNumber() + "," : null;
                if (property.getWardStreetId() != null) {
                    address += " " + property.getWardStreetId().getStreetName() + ((property.getWardStreetId().getEstateName() != null) ? ", " + property.getWardStreetId().getEstateName() : "")
                            + ", " + property.getWardStreetId().getLcdaWardId().getWardName() + property.getWardStreetId().getLcdaWardId().getLocalCouncilDevAreaId().getLcdaName();
                } else {
                    address += property.getIrregularAddress();
                }
                propDto = new WardPropertiesDto(property.getId(), property.getPropertyId(), property.getPropertyLongitude(), property.getPropertyLatitude(), address, 
                        property.getCapturedById().getPerson().getFullName(), property.getCreated(), property.getDateCaptured());
                propertiesDto.add(propDto);
            }
        }

        return propertiesDto;
    }

}
