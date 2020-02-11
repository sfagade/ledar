
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
 

/**
 * @author sfagade
 * @date Feb 3, 2016
 */
@Entity
@Table(name = "ref_ward_towns")
@AttributeOverride(name = "id", column = @Column(name = "ward_town_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "WardTowns.findAll", query = "SELECT w FROM WardTowns w"),
    @NamedQuery(name = "WardTowns.findByWardTownId", query = "SELECT w FROM WardTowns w WHERE w.id = :wardTownId"),
    @NamedQuery(name = "WardTowns.findByTownName", query = "SELECT w FROM WardTowns w WHERE w.townName = :townName")})
public class WardTowns extends IcslLedarModelBase implements Serializable {
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wardTownId")
    private List<WardStreets> wardStreetsList;
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "town_name")
    private String townName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    
    @JoinColumn(name = "lcda_ward_id", referencedColumnName = "lcda_ward_id")
    @ManyToOne(optional = false)
    private LcdaWards lcdaWardId;

    public WardTowns() {
    }

    public WardTowns(Long wardTownId) {
        this.id = wardTownId;
    }

    public WardTowns(Long wardTownId, String townName, Date created_) {
        this.id = wardTownId;
        this.townName = townName;
        this.created = created_;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LcdaWards getLcdaWardId() {
        return lcdaWardId;
    }

    public void setLcdaWardId(LcdaWards lcdaWardId) {
        this.lcdaWardId = lcdaWardId;
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
        if (!(object instanceof WardTowns)) {
            return false;
        }
        WardTowns other = (WardTowns) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.WardTowns[ wardTownId=" + id + " ]";
    }

     
    public List<WardStreets> getWardStreetsList() {
        return wardStreetsList;
    }

    public void setWardStreetsList(List<WardStreets> wardStreetsList) {
        this.wardStreetsList = wardStreetsList;
    }
}
