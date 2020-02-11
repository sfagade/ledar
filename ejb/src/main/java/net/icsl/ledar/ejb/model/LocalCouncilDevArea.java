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
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "ref_local_council_dev_area")
@AttributeOverride(name = "id", column = @Column(name = "local_council_dev_area_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "LocalCouncilDevArea.findAll", query = "SELECT l FROM LocalCouncilDevArea l"),
    @NamedQuery(name = "LocalCouncilDevArea.findByLocalCouncilDevAreaId", query = "SELECT l FROM LocalCouncilDevArea l WHERE l.id = :localCouncilDevAreaId"),
    @NamedQuery(name = "LocalCouncilDevArea.findByLcdaName", query = "SELECT l FROM LocalCouncilDevArea l WHERE l.lcdaName = :lcdaName")})
public class LocalCouncilDevArea extends IcslLedarModelBase implements Serializable {
    
    @JoinColumn(name = "contractor_id", referencedColumnName = "organization_id")
    @ManyToOne
    private Organizations contractorId;

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "lcda_name")
    private String lcdaName;
    @Size(max = 500)
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "inits_mapping")
    private String initsMapping;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "localCouncilDevAreaId")
    private List<LcdaWards> lcdaWardsList;
    @JoinColumn(name = "senatorial_district_id", referencedColumnName = "senatorial_district_id")
    @ManyToOne(optional = false)
    private SenatorialDistricts senatorialDistrictId;
    @JoinColumn(name = "lcda_chairman_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails lcdaChairmanId;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "latitude")
    private String latitude;

    public LocalCouncilDevArea() {
    }

    public LocalCouncilDevArea(Long localCouncilDevAreaId) {
        this.id = localCouncilDevAreaId;
    }

    public LocalCouncilDevArea(Long localCouncilDevAreaId, String lcdaName, Date created_) {
        this.id = localCouncilDevAreaId;
        this.lcdaName = lcdaName;
        this.created = created_;
    }

    public LocalCouncilDevArea(Long lcda_id, String lcdaName, SenatorialDistricts senatorialDistrictId, Logindetails lcdaChairmanId, Date created_) {
        this.lcdaName = lcdaName;
        this.senatorialDistrictId = senatorialDistrictId;
        this.lcdaChairmanId = lcdaChairmanId;
        this.id = lcda_id;
        this.created = created_;
    }

    public String getLcdaName() {
        return lcdaName;
    }

    public void setLcdaName(String lcdaName) {
        this.lcdaName = lcdaName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

     
    public List<LcdaWards> getLcdaWardsList() {
        return lcdaWardsList;
    }

    public void setLcdaWardsList(List<LcdaWards> lcdaWardsList) {
        this.lcdaWardsList = lcdaWardsList;
    }

    public SenatorialDistricts getSenatorialDistrictId() {
        return senatorialDistrictId;
    }

    public void setSenatorialDistrictId(SenatorialDistricts senatorialDistrictId) {
        this.senatorialDistrictId = senatorialDistrictId;
    }

    public Logindetails getLcdaChairmanId() {
        return lcdaChairmanId;
    }

    public void setLcdaChairmanId(Logindetails lcdaChairmanId) {
        this.lcdaChairmanId = lcdaChairmanId;
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
        if (!(object instanceof LocalCouncilDevArea)) {
            return false;
        }
        LocalCouncilDevArea other = (LocalCouncilDevArea) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.LocalCouncilDevArea[ localCouncilDevAreaId=" + id + " ]";
    }

    public Organizations getContractorId() {
        return contractorId;
    }

    public void setContractorId(Organizations contractorId) {
        this.contractorId = contractorId;
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

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
}
