#fastdfs�ͻ���

* ���ӳ����ӿ����ò���
* �ϴ�bytes֧��
* �����ϴ�group֧��
* �޸�tracker list storageЭ������
* ����ͨ��fileId����file api
* �����ϴ��ļ�slave֧��
* ���ӻ�ȡ�ļ���Ϣ֧��

>api�б�

```java
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
	
	public byte[] download(String fileId) throws Exception;
		
	public boolean transferTo(String fileId,File file) throws Exception;
	
	public String uploadSlave(String fileId,File file,String suffix) throws Exception;
	
	public String uploadSlave(String fileId,byte[] bytes,String suffix) throws Exception;
	
	public FileInfo getFileInfo(String fileId) throws Exception;
	
	public FileInfo queryFileInfo(String fileId) throws Exception;
	
	public void close();

}

```

>���ʹ��

```java
FastdfsClient client = FastdfsClientFactory.getFastdfsClient("FastdfsClient.properties");
client.upload(file);//Ĭ���ϴ�
client.upload(file, f, "group1");//�ϴ�Fileָ����
String upload = client.upload(bs, file.getName(),"group2");//�ϴ�bytesָ����
```

