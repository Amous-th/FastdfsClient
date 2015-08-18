package net.mikesu.fastdfs;


public class UploadSlaveTest {
	
	public static void main(String[] args) throws Exception {
		
		FastdfsClient fastdfsClient = FastdfsClientFactory.getFastdfsClient("FastdfsClient.properties");
		String ff = "group2/M02/00/08/wKiLhFXOppGALI-KAAHDfzZSW1o296.jpg";
		byte[] download = fastdfsClient.download(ff);
		if(download!=null){
			String slave = fastdfsClient.uploadSlave(ff, download, "_haha");
			System.out.println(slave);
		}
		fastdfsClient.close();
	}

}
