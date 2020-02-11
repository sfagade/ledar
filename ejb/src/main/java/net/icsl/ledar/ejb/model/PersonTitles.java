
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
 

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "ref_person_titles")
@AttributeOverride(name = "id", column = @Column(name = "person_title_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "PersonTitles.findAll", query = "SELECT p FROM PersonTitles p"),
    @NamedQuery(name = "PersonTitles.findByPersonTitleId", query = "SELECT p FROM PersonTitles p WHERE p.id = :personTitleId"),
    @NamedQuery(name = "PersonTitles.findByTitleName", query = "SELECT p FROM PersonTitles p WHERE p.titleName = :titleName")})
public class PersonTitles extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "title_name")
    private String titleName;
    @Size(max = 30)
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "inits_mapping")
    private String initsMapping;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "personTitleId")
    private List<PropertyBiodatas> propertyBiodatasList;
    @OneToMany(mappedBy = "personTitleId")
    private List<People> peopleList;

    public PersonTitles() {
    }

    public PersonTitles(Long personTitleId) {
        this.id = personTitleId;
    }

    public PersonTitles(Long personTitleId, String titleName) {
        this.id = personTitleId;
        this.titleName = titleName;
    }
    
    public PersonTitles(Long personTitleId, String titleName, String mapping) {
        this.id = personTitleId;
        this.titleName = titleName;
        this.initsMapping = mapping;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

     
    public List<PropertyBiodatas> getPropertyBiodatasList() {
        return propertyBiodatasList;
    }

    public void setPropertyBiodatasList(List<PropertyBiodatas> propertyBiodatasList) {
        this.propertyBiodatasList = propertyBiodatasList;
    }

     
    public List<People> getPeopleList() {
        return peopleList;
    }

    public void setPeopleList(List<People> peopleList) {
        this.peopleList = peopleList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersonTitles)) {
            return false;
        }
        PersonTitles other = (PersonTitles) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.PersonTitles[ personTitleId=" + id + " ]";
    }

    /**
     * @return the initsMapping
     */
    public String getInitsMapping() {
        return initsMapping;
    }

    /**
     * @param initsMapping the initsMapping to set
     */
    public void setInitsMapping(String initsMapping) {
        this.initsMapping = initsMapping;
    }

}
