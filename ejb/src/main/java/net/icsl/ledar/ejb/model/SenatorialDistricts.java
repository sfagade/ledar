
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "ref_senatorial_districts")
@AttributeOverride(name = "id", column = @Column(name = "senatorial_district_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "SenatorialDistricts.findAll", query = "SELECT s FROM SenatorialDistricts s"),
    @NamedQuery(name = "SenatorialDistricts.findBySenatorialDistrictId", query = "SELECT s FROM SenatorialDistricts s WHERE s.id = :senatorialDistrictId"),
    @NamedQuery(name = "SenatorialDistricts.findByDistrictName", query = "SELECT s FROM SenatorialDistricts s WHERE s.districtName = :districtName")})
public class SenatorialDistricts extends IcslLedarModelBase implements Serializable {
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "senatorialDistrictId")
    private Collection<Organizations> organizationsCollection;
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "district_name")
    private String districtName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "senatorial_code")
    private String senatorialCode;
    @Size(max = 75)
    @Column(name = "senatorial_representative")
    private String senatorialRepresentative;
    @Size(max = 500)
    @Column(name = "remarks")
    private String remarks;
    
    @JoinColumn(name = "geographical_state_id", referencedColumnName = "geographical_boundary_id")
    @ManyToOne(optional = false)
    private GeographicalBoundaries geographicalStateId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "senatorialDistrictId")
    private List<LocalCouncilDevArea> localCouncilDevAreaList;

    public SenatorialDistricts() {
    }

    public SenatorialDistricts(Long senatorialDistrictId) {
        this.id = senatorialDistrictId;
    }

    public SenatorialDistricts(Long senatorialDistrictId, String districtName, String senatorialCode) {
        this.id = senatorialDistrictId;
        this.districtName = districtName;
        this.senatorialCode = senatorialCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getSenatorialCode() {
        return senatorialCode;
    }

    public void setSenatorialCode(String senatorialCode) {
        this.senatorialCode = senatorialCode;
    }

    public String getSenatorialRepresentative() {
        return senatorialRepresentative;
    }

    public void setSenatorialRepresentative(String senatorialRepresentative) {
        this.senatorialRepresentative = senatorialRepresentative;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public GeographicalBoundaries getGeographicalStateId() {
        return geographicalStateId;
    }

    public void setGeographicalStateId(GeographicalBoundaries geographicalStateId) {
        this.geographicalStateId = geographicalStateId;
    }

     
    public List<LocalCouncilDevArea> getLocalCouncilDevAreaList() {
        return localCouncilDevAreaList;
    }

    public void setLocalCouncilDevAreaList(List<LocalCouncilDevArea> localCouncilDevAreaList) {
        this.localCouncilDevAreaList = localCouncilDevAreaList;
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
        if (!(object instanceof SenatorialDistricts)) {
            return false;
        }
        SenatorialDistricts other = (SenatorialDistricts) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.SenatorialDistricts[ senatorialDistrictId=" + id + " ]";
    }

     
    public Collection<Organizations> getOrganizationsCollection() {
        return organizationsCollection;
    }

    public void setOrganizationsCollection(Collection<Organizations> organizationsCollection) {
        this.organizationsCollection = organizationsCollection;
    }
}
