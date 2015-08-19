package net.mikesu.fastdfs.data;

import java.util.Date;

public class FileInfo {

	private String sourceStorageIp;
	private long fileSize;
	private Date createTime;
	private int crc32;
	
	public FileInfo(long fileSize, int createTime, int crc32,
			String sourceStorageIp) {
		this.fileSize = fileSize;
		this.createTime = new Date(createTime * 1000L);
		this.crc32 = crc32;
		this.sourceStorageIp = sourceStorageIp;
	}

	public String getSourceStorageIp() {
		return sourceStorageIp;
	}

	public void setSourceStorageIp(String sourceStorageIp) {
		this.sourceStorageIp = sourceStorageIp;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getCrc32() {
		return crc32;
	}

	public void setCrc32(int crc32) {
		this.crc32 = crc32;
	}

	@Override
	public String toString() {
		return "FileInfo [sourceStorageIp=" + sourceStorageIp + ", fileSize="
				+ fileSize + ", createTime=" + createTime + ", crc32=" + crc32
				+ "]";
	}
}
