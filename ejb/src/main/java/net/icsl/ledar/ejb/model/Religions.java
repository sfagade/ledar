
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
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
@Table(name = "ref_religions")
@AttributeOverride(name = "id", column = @Column(name = "religion_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "Religions.findAll", query = "SELECT r FROM Religions r"),
    @NamedQuery(name = "Religions.findByReligionId", query = "SELECT r FROM Religions r WHERE r.id = :religionId"),
    @NamedQuery(name = "Religions.findByReligionName", query = "SELECT r FROM Religions r WHERE r.religionName = :religionName")})
public class Religions extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
   
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "religion_name")
    private String religionName;
    @Size(max = 30)
    @Column(name = "created_by")
    private String createdBy;
    
    @OneToMany(mappedBy = "religionId")
    private List<People> peopleList;

    public Religions() {
    }

    public Religions(Long religionId) {
        this.id = religionId;
    }

    public Religions(Long religionId, String religionName, Date created) {
        this.id = religionId;
        this.religionName = religionName;
        this.created = created;
    }
    
    public Religions(Long religionId, String religionName) {
        this.id = religionId;
        this.religionName = religionName;
        //this.created = created;
    }


    public String getReligionName() {
        return religionName;
    }

    public void setReligionName(String religionName) {
        this.religionName = religionName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
        if (!(object instanceof Religions)) {
            return false;
        }
        Religions other = (Religions) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.Religions[ religionId=" + id + " ]";
    }

}
