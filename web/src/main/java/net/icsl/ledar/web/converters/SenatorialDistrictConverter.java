package net.icsl.ledar.web.converters;

import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.icsl.ledar.ejb.model.SenatorialDistricts;

@ManagedBean(name = "senatorialDistrictConverterBean")
@FacesConverter(value = "senatorialDistrictConverter")
public class SenatorialDistrictConverter implements Converter {

    @PersistenceContext
    // I include this because you will need to 
    // lookup  your entities based on submitted values
    private transient EntityManager entManager;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<SenatorialDistricts> criteria = cb.createQuery(SenatorialDistricts.class);
        Root<SenatorialDistricts> senDist = criteria.from(SenatorialDistricts.class);
        if (!value.contains("Select")) {
            criteria = criteria.multiselect(senDist.get("id"), senDist.get("districtName"), senDist.get("senatorialCode")).where(cb.equal(senDist.get("id"), new Long(value)));
            return entManager.createQuery(criteria).getSingleResult();
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
        SenatorialDistricts dpts = ((SenatorialDistricts) o);
        return (dpts != null) ? dpts.getId().toString() : null;
    }
}
