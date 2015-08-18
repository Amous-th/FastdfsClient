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
	 * �ϴ��ļ�slave֧�� 
	 * group2/M02/00/08/wKiLhFXOppGALI-KAAHDfzZSW1o296.jpg
	 * group2/M02/00/08/wKiLhFXOppGALI-KAAHDfzZSW1o296_100x200.jpg
	 * @param fileId
	 * @param file
	 * @param suffix
	 * @return
	 * @throws Exception
	 */
	public String uploadSlave(String fileId,File file,String suffix,String ext) throws Exception;
	
	/**
	 * �ϴ�slave�ļ�֧��
	 * @param fileId
	 * @param bytes
	 * @param suffix
	 * @return
	 * @throws Exception
	 */
	public String uploadSlave(String fileId,byte[] bytes,String suffix,String ext) throws Exception;
	/**
	 * 
	 * @param fileId:group2/M01/00/08/wKiLhFXOppGAO0FZAAPWNDHeYj4609.jpg
	 * @return �������ָ�ʽfileId
	 * @throws Exception
	 */
	public byte[] download(String fileId) throws Exception;
	
	/**
	 * �����أ�Ȼ�󱣴��ڱ���
	 * @param fileId
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public boolean transferTo(String fileId,File file) throws Exception;
	
	public void close();

}
