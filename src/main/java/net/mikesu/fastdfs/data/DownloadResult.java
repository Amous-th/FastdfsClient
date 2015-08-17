package net.mikesu.fastdfs.data;

public class DownloadResult {

	private byte[] data;
	public DownloadResult(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
