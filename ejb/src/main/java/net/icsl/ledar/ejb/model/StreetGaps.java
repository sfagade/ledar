/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;
 

/**
 *
 * @author sfagade
 */
@Entity
@Table(name = "street_gaps")
@AttributeOverride(name = "id", column = @Column(name = "street_gap_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
public class StreetGaps extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 200)
    @Column(name = "irregular_address")
    private String irregularAddress;
    @Size(max = 10)
    @Column(name = "house_no")
    private String houseNo;
    @Size(max = 45)
    @Column(name = "remarks")
    private String remarks;
    @Size(max = 25)
    @Column(name = "gap_status")
    private String gapStatus;
    @Size(max = 35)
    @Column(name = "latitude")
    private String latitude;
    @Size(max = 35)
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "discovery_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date discoveryDate;

    @JoinColumn(name = "field_officer_id", referencedColumnName = "logindetail_id")
    @ManyToOne(optional = false)
    private Logindetails fieldOfficerId;
    @JoinColumn(name = "district_id", referencedColumnName = "lcda_ward_id")
    @ManyToOne(optional = false)
    private LcdaWards districtId;
    @JoinColumn(name = "ward_street_id", referencedColumnName = "ward_street_id")
    @ManyToOne
    private WardStreets wardStreetId;
    @JoinColumn(name = "consultant_id", referencedColumnName = "organization_id")
    @ManyToOne(optional = false)
    private Organizations consultantId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "streetGapId")
    private Collection<StreetGapFiles> streetGapFilesCollection;

    public StreetGaps() {
    }

    public StreetGaps(Long streetGapId) {
        this.id = streetGapId;
    }

    public StreetGaps(Long gap_id, String irregularAddress, String houseNo, String remarks, String gapStatus, String latitude, String longitude, Logindetails fieldOfficerId, LcdaWards districtId,
            WardStreets wardStreetId, Date created, Date modified) {
        this.irregularAddress = irregularAddress;
        this.houseNo = houseNo;
        this.remarks = remarks;
        this.gapStatus = gapStatus;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fieldOfficerId = fieldOfficerId;
        this.districtId = districtId;
        this.wardStreetId = wardStreetId;
        this.id = gap_id;
        this.created = created;
        this.modified = modified;
    }

    public String getIrregularAddress() {
        return irregularAddress;
    }

    public void setIrregularAddress(String irregularAddress) {
        this.irregularAddress = irregularAddress;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getGapStatus() {
        return gapStatus;
    }

    public void setGapStatus(String gapStatus) {
        this.gapStatus = gapStatus;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Logindetails getFieldOfficerId() {
        return fieldOfficerId;
    }

    public void setFieldOfficerId(Logindetails fieldOfficerId) {
        this.fieldOfficerId = fieldOfficerId;
    }

    public LcdaWards getDistrictId() {
        return districtId;
    }

    public void setDistrictId(LcdaWards districtId) {
        this.districtId = districtId;
    }

    public WardStreets getWardStreetId() {
        return wardStreetId;
    }

    public void setWardStreetId(WardStreets wardStreetId) {
        this.wardStreetId = wardStreetId;
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
        if (!(object instanceof StreetGaps)) {
            return false;
        }
        StreetGaps other = (StreetGaps) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.StreetGaps[ streetGapId=" + id + " ]";
    }

    public Organizations getConsultantId() {
        return consultantId;
    }

    public void setConsultantId(Organizations consultantId) {
        this.consultantId = consultantId;
    }

    public Date getDiscoveryDate() {
        return discoveryDate;
    }

    public void setDiscoveryDate(Date discoveryDate) {
        this.discoveryDate = discoveryDate;
    }

    /**
     * @return the streetGapFilesCollection
     */
    public Collection<StreetGapFiles> getStreetGapFilesCollection() {
        return streetGapFilesCollection;
    }

    /**
     * @param streetGapFilesCollection the streetGapFilesCollection to set
     */
    public void setStreetGapFilesCollection(Collection<StreetGapFiles> streetGapFilesCollection) {
        this.streetGapFilesCollection = streetGapFilesCollection;
    }
}
