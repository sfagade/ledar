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
import net.icsl.ledar.ejb.model.ResidentialUseages;

@ManagedBean(name = "residentialUseConverterBean")
@FacesConverter(value = "residentialUseConverter")
public class ResidentialUsageConverter implements Converter {

    @PersistenceContext
    // I include this because you will need to 
    // lookup  your entities based on submitted values
    private transient EntityManager entManager;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<ResidentialUseages> criteria = cb.createQuery(ResidentialUseages.class);
        Root<ResidentialUseages> commrObj = criteria.from(ResidentialUseages.class);
        if (!value.contains("Select")) {
            criteria = criteria.multiselect(commrObj.get("id"), commrObj.get("residentialUseName"), commrObj.get("created")).where(cb.equal(commrObj.get("id"), new Long(value)));
            return entManager.createQuery(criteria).getSingleResult();
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
        ResidentialUseages commrObj = ((ResidentialUseages) o);
        return (commrObj != null) ? commrObj.getId().toString() : null;
    }
}
