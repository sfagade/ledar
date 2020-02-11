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
import net.icsl.ledar.ejb.model.PropertyClassifications;

@ManagedBean(name = "propertyUseConverterBean")
@FacesConverter(value = "propertyUseConverter")
public class PropertyUseConverter implements Converter {

    @PersistenceContext
    // I include this because you will need to 
    // lookup  your entities based on submitted values
    private transient EntityManager entManager;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<PropertyClassifications> criteria = cb.createQuery(PropertyClassifications.class);
        Root<PropertyClassifications> commrObj = criteria.from(PropertyClassifications.class);
        if (!value.contains("Select")) {
            criteria = criteria.multiselect(commrObj.get("id"), commrObj.get("classificationName")).where(cb.equal(commrObj.get("id"), new Long(value)));
            return entManager.createQuery(criteria).getSingleResult();
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
        PropertyClassifications commrObj = ((PropertyClassifications) o);
        return (commrObj != null) ? commrObj.getId().toString() : null;
    }
}
