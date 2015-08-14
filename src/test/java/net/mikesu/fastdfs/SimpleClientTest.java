package net.mikesu.fastdfs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;

public class SimpleClientTest {
	
	public static void main(String[] args) throws ConfigurationException {
		FastdfsClient fastdfsClient = FastdfsClientFactory.getFastdfsClient("FastdfsClient.properties");
		try {
			String fileId = fastdfsClient.upload(new File("F:\\home\\longjin\\haha.jpg"), "haha.jpg");
			System.out.println(fileId);
			HashMap<String, String> meta = new HashMap<String,String>();
			meta.put("name", "haha.jpg");
			fastdfsClient.setMeta(fileId, meta);
			Map<String, String> map = fastdfsClient.getMeta(fileId);
			System.out.println(map);
			fastdfsClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
