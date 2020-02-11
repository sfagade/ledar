/**
 * 
 */
package net.icsl.ledar.ejb.dto;

import java.sql.Blob;
import java.sql.Date;


/**
 * @author aojediran
 *
 */
public class FileUploadsDto {
	
		private Long fileUploadId;
		private String fileName;
	  	private String fileType;
	  	private String fileSide;
	    private String filePath;
	    private Long fileUploadById;
	    private Blob fileContent;
	    private Date created;
	    private Date modified; 
	    
		public FileUploadsDto(String fileName, String fileType, String fileSide, String filePath) {
			super();
			this.fileName = fileName;
			this.fileType = fileType;
			this.fileSide = fileSide;
			this.filePath = filePath;
		}
		
		

		public FileUploadsDto(Long fileUploadId, String fileName, String fileType, String fileSide, String filePath,
				Long fileUploadById, Blob fileContent, Date created, Date modified) {
			super();
			this.fileUploadId = fileUploadId;
			this.fileName = fileName;
			this.fileType = fileType;
			this.fileSide = fileSide;
			this.filePath = filePath;
			this.fileUploadById = fileUploadById;
			this.fileContent = fileContent;
			this.created = created;
			this.modified = modified;
		}

		

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}

		public String getFileSide() {
			return fileSide;
		}

		public void setFileSide(String fileSide) {
			this.fileSide = fileSide;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}



		public Long getFileUploadId() {
			return fileUploadId;
		}



		public void setFileUploadId(Long fileUploadId) {
			this.fileUploadId = fileUploadId;
		}



		public Long getFileUploadById() {
			return fileUploadById;
		}



		public void setFileUploadById(Long fileUploadById) {
			this.fileUploadById = fileUploadById;
		}



		public Blob getFileContent() {
			return fileContent;
		}



		public void setFileContent(Blob fileContent) {
			this.fileContent = fileContent;
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
