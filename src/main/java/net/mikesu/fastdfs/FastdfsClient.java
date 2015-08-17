package net.mikesu.fastdfs;

import java.io.File;
import java.util.Map;

public interface FastdfsClient {
	
	public String upload(File file) throws Exception;
	
	public String upload(File file,String fileName) throws Exception;
	
	public String upload(byte[] bytes,String fileName) throws Exception;
	
	public String upload(File file,String fileName,String group) throws Exception;
	
	public String upload(byte[] bytes,String fileName,String group) throws Exception;
	
	public String getUrl(String fileId) throws Exception;
	
	public Boolean setMeta(String fileId,Map<String,String> meta) throws Exception;
	
	public Map<String,String> getMeta(String fileId) throws Exception;
	
	public Boolean delete(String fileId) throws Exception;
	
	/**
	 * 
	 * @param fileId:group2/M01/00/08/wKiLhFXOppGAO0FZAAPWNDHeYj4609.jpg
	 * @return 必须这种格式fileId
	 * @throws Exception
	 */
	public byte[] download(String fileId) throws Exception;
	
	/**
	 * 先下载，然后保存在本地
	 * @param fileId
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public boolean transferTo(String fileId,File file) throws Exception;
	
	public void close();

}
