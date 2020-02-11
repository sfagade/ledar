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
import net.icsl.ledar.ejb.model.ResidentialTypes;

@ManagedBean(name = "residentialTypesConverterBean")
@FacesConverter(value = "residentialTypesConverter")
public class ResidenceTypeConverter implements Converter {

    @PersistenceContext
    // I include this because you will need to 
    // lookup  your entities based on submitted values
    private transient EntityManager entManager;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<ResidentialTypes> criteria = cb.createQuery(ResidentialTypes.class);
        Root<ResidentialTypes> commrObj = criteria.from(ResidentialTypes.class);
        if (!value.contains("Select")) {
            criteria = criteria.multiselect(commrObj.get("id"), commrObj.get("residentialTypeName"), commrObj.get("created")).where(cb.equal(commrObj.get("id"), new Long(value)));
            return entManager.createQuery(criteria).getSingleResult();
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
        ResidentialTypes commrObj = ((ResidentialTypes) o);
        return (commrObj != null) ? commrObj.getId().toString() : null;
    }
}
