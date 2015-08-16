#fastdfs客户端

* 增加连接池支持
* 上传bytes支持
* 增加上传group支持
* 修复tracker list storage协议问题


>api列表

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
	
	public void close();

}

```

>如何使用

```java
FastdfsClient client = FastdfsClientFactory.getFastdfsClient("FastdfsClient.properties");
client.upload(file);//默认上传
client.upload(file, f, "group1");//上传File指定组
String upload = client.upload(bs, file.getName(),"group2");//上传bytes指定组
```

