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
import net.icsl.ledar.ejb.model.PropertyQualities;

@ManagedBean(name = "propertyQtyConverterBean")
@FacesConverter(value = "propertyQtyConverter")
public class PropertyQualityConverter implements Converter {

    @PersistenceContext
    // I include this because you will need to 
    // lookup  your entities based on submitted values
    private transient EntityManager entManager;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {

        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<PropertyQualities> criteria = cb.createQuery(PropertyQualities.class);
        Root<PropertyQualities> propQtly = criteria.from(PropertyQualities.class);
        if (!value.contains("Select")) {
            criteria = criteria.multiselect(propQtly.get("id"), propQtly.get("qualityName"), propQtly.get("percentageValue"), propQtly.get("created")).where(cb.equal(propQtly.get("id"), new Long(value)));

            return entManager.createQuery(criteria).getSingleResult();
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
        PropertyQualities lcdaWardObj = ((PropertyQualities) o);
        return (lcdaWardObj != null) ? lcdaWardObj.getId().toString() : null;
    }
}
