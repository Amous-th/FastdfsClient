package net.mikesu.fastdfs;

import net.mikesu.fastdfs.data.FileInfo;

public class FileInfoTest {
	
	public static void main(String[] args) throws Exception {
		
		FastdfsClient fastdfsClient = FastdfsClientFactory.getFastdfsClient("FastdfsClient.properties");
		String fileId = "group1/M01/00/00/wKiLgVXNeqqAa5ONACHyv7B3xqQ904.jpg";
		FileInfo fileInfo = fastdfsClient.queryFileInfo(fileId);
		System.out.println("query:"+fileInfo);
		FileInfo info = fastdfsClient.getFileInfo(fileId);
		System.out.println("get:"+info);
		fastdfsClient.close();
	}

}
