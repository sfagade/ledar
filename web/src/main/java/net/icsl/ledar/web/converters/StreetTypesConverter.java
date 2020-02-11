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
import net.icsl.ledar.ejb.model.StreetTypes;

@ManagedBean(name = "streetTypesConverterBean")
@FacesConverter(value = "streetTypesConverter")
public class StreetTypesConverter implements Converter {

    @PersistenceContext
    // I include this because you will need to 
    // lookup  your entities based on submitted values
    private transient EntityManager entManager;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {

        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<StreetTypes> criteria = cb.createQuery(StreetTypes.class);
        Root<StreetTypes> strType = criteria.from(StreetTypes.class);
        if (!value.contains("Select")) {
            criteria = criteria.multiselect(strType.get("id"), strType.get("streetTypeName")).where(cb.equal(strType.get("id"), new Long(value)));

            return entManager.createQuery(criteria).getSingleResult();
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
        StreetTypes strType = ((StreetTypes) o);
        return (strType != null) ? strType.getId().toString() : null;
    }
}
