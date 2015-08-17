package net.mikesu.fastdfs;

import java.io.File;

public class DownLoadTest {
	
	public static void main(String[] args) throws Exception {
		
		FastdfsClient fastdfsClient = FastdfsClientFactory.getFastdfsClient("FastdfsClient.properties");
		String fileId = "group2/M02/00/08/wKiLhFXOppGALI-KAAHDfzZSW1o296.jpg";
		boolean result = fastdfsClient.transferTo(fileId, new File("d:\\imgs\\haha.jpg"));
		if(result){
			System.out.println("download complete");
		}else{
			System.out.println("download fail");
		}
		fastdfsClient.close();
	}

}
