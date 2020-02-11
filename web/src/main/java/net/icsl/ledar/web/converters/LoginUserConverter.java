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
import net.icsl.ledar.ejb.model.Logindetails;

@ManagedBean(name = "loginUserConverterBean")
@FacesConverter(value = "loginUserConverter")
public class LoginUserConverter implements Converter {

    @PersistenceContext
    // I include this because you will need to 
    // lookup  your entities based on submitted values
    private transient EntityManager entManager;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {

        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<Logindetails> criteria = cb.createQuery(Logindetails.class);
        Root<Logindetails> deptObj = criteria.from(Logindetails.class);
        if ((value != null) && (!value.isEmpty()) && (!value.contains("Select"))) {
            criteria = criteria.multiselect(deptObj.get("id"), deptObj.get("username"), deptObj.get("active"), deptObj.get("person"), deptObj.get("created")).where(cb.equal(deptObj.get("id"), 
                    new Long(value)));
            Logindetails login = entManager.createQuery(criteria).getSingleResult();
            return entManager.createQuery(criteria).getSingleResult();
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
        Logindetails dpts = ((Logindetails) o);
        return (dpts != null) ? dpts.getId().toString() : null;
    }
}
