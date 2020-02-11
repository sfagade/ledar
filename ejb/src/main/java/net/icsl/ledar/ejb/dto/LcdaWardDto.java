package net.icsl.ledar.ejb.dto;

import java.util.Date;

/**
 * @author sfagade
 * @date Feb 4, 2016
 */
public class LcdaWardDto {

    private Long wardId;
    private String wardName;
    private String remarks;
    private Date wardCreated;
    private Date wardModified;

    private String lcdaName;
    private Long lcdaId;

    private Long supervisorId;
    private String supervisorFullName;
    private String supervisorUsername;

    public LcdaWardDto(Long wardId, String wardName, String lcdaName, Long lcdaId, Date wardCreated) {

        this.wardId = wardId;
        this.wardName = wardName;
        this.wardCreated = wardCreated;
        this.lcdaName = lcdaName;
        this.lcdaId = lcdaId;
    }

    public LcdaWardDto(Long wardId, String wardName, String remarks, Date wardCreated, Date wardModified,
            String lcdaName, Long lcdaId, Long supervisorId, String supervisorFullName, String supervisorUsername) {

        this.wardId = wardId;
        this.wardName = wardName;
        this.remarks = remarks;
        this.wardCreated = wardCreated;
        this.wardModified = wardModified;
        this.lcdaName = lcdaName;
        this.lcdaId = lcdaId;
        this.supervisorId = supervisorId;
        this.supervisorFullName = supervisorFullName;
        this.supervisorUsername = supervisorUsername;
    }

    public Long getWardId() {
        return wardId;
    }

    public void setWardId(Long wardId) {
        this.wardId = wardId;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getWardCreated() {
        return wardCreated;
    }

    public void setWardCreated(Date wardCreated) {
        this.wardCreated = wardCreated;
    }

    public Date getWardModified() {
        return wardModified;
    }

    public void setWardModified(Date wardModified) {
        this.wardModified = wardModified;
    }

    public String getLcdaName() {
        return lcdaName;
    }

    public void setLcdaName(String lcdaName) {
        this.lcdaName = lcdaName;
    }

    public Long getLcdaId() {
        return lcdaId;
    }

    public void setLcdaId(Long lcdaId) {
        this.lcdaId = lcdaId;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(Long supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getSupervisorFullName() {
        return supervisorFullName;
    }

    public void setSupervisorFullName(String supervisorFullName) {
        this.supervisorFullName = supervisorFullName;
    }

    public String getSupervisorUsername() {
        return supervisorUsername;
    }

    public void setSupervisorUsername(String supervisorUsername) {
        this.supervisorUsername = supervisorUsername;
    }

}
