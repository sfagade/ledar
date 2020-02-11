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
import net.icsl.ledar.ejb.model.PersonTitles;

@ManagedBean(name = "personTitlesConverterBean")
@FacesConverter(value = "personTitlesConverter")
public class PersonTitleConverter implements Converter {

    @PersistenceContext
    private transient EntityManager entManager;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {

        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<PersonTitles> criteria = cb.createQuery(PersonTitles.class);
        Root<PersonTitles> strType = criteria.from(PersonTitles.class);
        if (!value.contains("Select")) {
            criteria = criteria.multiselect(strType.get("id"), strType.get("titleName")).where(cb.equal(strType.get("id"), new Long(value)));

            return entManager.createQuery(criteria).getSingleResult();
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
        PersonTitles strType = ((PersonTitles) o);
        return (strType != null) ? strType.getId().toString() : null;
    }
}
