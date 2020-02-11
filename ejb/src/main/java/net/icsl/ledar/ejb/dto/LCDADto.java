/**
 *
 */
package net.icsl.ledar.ejb.dto;

import java.util.Date;

/**
 * @author aojediran
 *
 */
public class LCDADto {

    private Long id;
    private String lcdaName;
    private Long sd_id;//senatorial district id
    private Long lcdaChairman_id;
    private String remark;
    private Date created;
    private Date modified;

    public LCDADto(Long id, String lcdaName, Long sd_id, Long lcdaChairman_id, String remark, Date created, Date modified) {
        super();
        this.id = id;
        this.lcdaName = lcdaName;
        this.sd_id = sd_id;
        this.lcdaChairman_id = lcdaChairman_id;
        this.remark = remark;
        this.created = created;
        this.modified = modified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLcdaName() {
        return lcdaName;
    }

    public void setLcdaName(String lcdaName) {
        this.lcdaName = lcdaName;
    }

    public Long getSd_id() {
        return sd_id;
    }

    public void setSd_id(Long sd_id) {
        this.sd_id = sd_id;
    }

    public Long getLcdaChairman_id() {
        return lcdaChairman_id;
    }

    public void setLcdaChairman_id(Long lcdaChairman_id) {
        this.lcdaChairman_id = lcdaChairman_id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

}
