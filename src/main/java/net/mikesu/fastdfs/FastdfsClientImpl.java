package net.mikesu.fastdfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.mikesu.fastdfs.client.StorageClient;
import net.mikesu.fastdfs.client.StorageClientFactory;
import net.mikesu.fastdfs.client.TrackerClient;
import net.mikesu.fastdfs.client.TrackerClientFactory;
import net.mikesu.fastdfs.data.DownloadResult;
import net.mikesu.fastdfs.data.GroupInfo;
import net.mikesu.fastdfs.data.Result;
import net.mikesu.fastdfs.data.StorageInfo;
import net.mikesu.fastdfs.data.UploadStorage;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastdfsClientImpl implements FastdfsClient{
	
	private static Logger logger = LoggerFactory.getLogger(FastdfsClientImpl.class);
	private GenericKeyedObjectPool<String, TrackerClient> trackerClientPool;
	private GenericKeyedObjectPool<String, StorageClient> storageClientPool;
	private List<String> trackerAddrs = new ArrayList<String>();
	private Map<String,String> storageIpMap = new ConcurrentHashMap<String, String>();
	
	public FastdfsClientImpl(List<String> trackerAddrs) throws Exception {
		super();
		this.trackerAddrs = trackerAddrs;
		trackerClientPool = new GenericKeyedObjectPool<>(new TrackerClientFactory());
		storageClientPool = new GenericKeyedObjectPool<>(new StorageClientFactory());
		updateStorageIpMap();
	}
	
	public FastdfsClientImpl(List<String> trackerAddrs,
			GenericKeyedObjectPool<String, TrackerClient> trackerClientPool,
			GenericKeyedObjectPool<String, StorageClient> storageClientPool) {
		super();
		this.trackerAddrs = trackerAddrs;
		this.trackerClientPool = trackerClientPool;
		this.storageClientPool = storageClientPool;
	}
	
	

	@Override
	public void close() {
		this.trackerClientPool.close();
		this.storageClientPool.close();
	}

	private void updateStorageIpMap() throws Exception{
		String trackerAddr = getTrackerAddr();
		TrackerClient trackerClient = null;
		try {
			trackerClient = trackerClientPool.borrowObject(trackerAddr);
			Result<List<GroupInfo>> result = trackerClient.getGroupInfos();
			if(result.getCode()==0){
				List<GroupInfo> groupInfos = result.getData();
				for(GroupInfo groupInfo:groupInfos){
					Result<List<StorageInfo>>  result2= trackerClient.getStorageInfos(groupInfo.getGroupName());
					if(result2.getCode()==0){
						List<StorageInfo> storageInfos = result2.getData();
						for(StorageInfo storageInfo:storageInfos){
							String hostPort = storageInfo.getDomainName();
							if(storageInfo.getStorageHttpPort()!=80){
								hostPort = hostPort + ":" + storageInfo.getStorageHttpPort();
							}
							storageIpMap.put(storageInfo.getIpAddr()+":"+storageInfo.getStoragePort(), hostPort);
						}
					}
				}
			}else{
				throw new Exception("Get getGroupInfos Error");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			if(trackerClient!=null){
				trackerClientPool.returnObject(trackerAddr, trackerClient);
			}
		}
	}
	
	private String getDownloadHostPort(String storageAddr) throws Exception{
		String downloadHostPort = storageIpMap.get(storageAddr);
		if(downloadHostPort==null){
			updateStorageIpMap();
			downloadHostPort = storageIpMap.get(storageAddr);
		}
		return downloadHostPort;
	}
	
	@Override
	public Boolean setMeta(String fileId, Map<String, String> meta)
			throws Exception {
		String trackerAddr = getTrackerAddr();
		TrackerClient trackerClient = null;
		StorageClient storageClient = null;
		Boolean result = null;
		String storageAddr=null;
		try{
			FastDfsFile fastDfsFile = new FastDfsFile(fileId);
			trackerClient = trackerClientPool.borrowObject(trackerAddr);
			Result<String> result2 = trackerClient.getUpdateStorageAddr(fastDfsFile.group, fastDfsFile.fileName);
			if(result2.getCode()==0){
				storageAddr = result2.getData();
				storageClient = storageClientPool.borrowObject(storageAddr);
				Result<Boolean> result3 = storageClient.setMeta(fastDfsFile.group, fastDfsFile.fileName,meta);
				if(result3.getCode()==0||result3.getCode()==0){
					result = result3.getData();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}finally{
			if(storageClient!=null){
				storageClientPool.returnObject(storageAddr, storageClient);
			}
			if(trackerClient!=null){
				trackerClientPool.returnObject(trackerAddr, trackerClient);
			}
		}
		return result;
	}

	@Override
	public Map<String, String> getMeta(String fileId) throws Exception {
		String trackerAddr = getTrackerAddr();
		TrackerClient trackerClient = null;
		StorageClient storageClient = null;
		Map<String, String> meta = null;
		String storageAddr=null;
		try{
			FastDfsFile fastDfsFile = new FastDfsFile(fileId);
			trackerClient = trackerClientPool.borrowObject(trackerAddr);
			Result<String> result2 = trackerClient.getUpdateStorageAddr(fastDfsFile.group, fastDfsFile.fileName);
			if(result2.getCode()==0){
				storageAddr = result2.getData();
				storageClient = storageClientPool.borrowObject(storageAddr);
				Result<Map<String,String>> result3 = storageClient.getMeta(fastDfsFile.group, fastDfsFile.fileName);
				if(result3.getCode()==0||result3.getCode()==0){
					meta = result3.getData();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}finally{
			if(storageClient!=null){
				storageClientPool.returnObject(storageAddr, storageClient);
			}
			if(trackerClient!=null){
				trackerClientPool.returnObject(trackerAddr, trackerClient);
			}
		}
		return meta;
	}

	public String getUrl(String fileId) throws Exception{
		String trackerAddr = getTrackerAddr();
		TrackerClient trackerClient = null;
		String url = null;
		try {
			FastDfsFile fastDfsFile = new FastDfsFile(fileId);
			trackerClient = trackerClientPool.borrowObject(trackerAddr);
			Result<String> result = trackerClient.getDownloadStorageAddr(fastDfsFile.group,fastDfsFile.fileName);
			if(result.getCode()==0){
				String hostPort = getDownloadHostPort(result.getData());
				url = "http://"+hostPort+"/"+fastDfsFile.fileName;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			if(trackerClient!=null){
				trackerClientPool.returnObject(trackerAddr, trackerClient);
			}
		}
		return url;
	}
	
	public String upload(File file) throws Exception{
		String fileName = file.getName();
		return upload(file, fileName);
	}
	
	public String upload(byte[] bytes,String fileName) throws Exception{
		String trackerAddr = getTrackerAddr();
		TrackerClient trackerClient = null;
		StorageClient storageClient = null;
		String storageAddr = null;
		String fileId = null;
		try {
			trackerClient = trackerClientPool.borrowObject(trackerAddr);
			Result<UploadStorage> result = trackerClient.getUploadStorage();
			if(result.getCode()==0){
				storageAddr = result.getData().getAddress();
				storageClient = storageClientPool.borrowObject(storageAddr);
				Result<String> result2 = storageClient.upload(bytes, fileName, result.getData().getPathIndex());
				if(result2.getCode()==0){
					fileId = result2.getData();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			if(storageClient!=null){
				storageClientPool.returnObject(storageAddr, storageClient);
			}
			if(trackerClient!=null){
				trackerClientPool.returnObject(trackerAddr, trackerClient);
			}
		}
		return fileId;
	}
	
	@Override
	public String upload(byte[] bytes, String fileName, String group)
			throws Exception {
		String fileId = null;
		StorageClient storageClient = null;
		StoragePath storePath= null;
		try {
			storePath = this.getStoreIp(group);
			if(storePath!=null){
				storageClient = storageClientPool.borrowObject(storePath.store);
				Result<String> result = storageClient.upload(bytes, fileName, (byte)storePath.path);
				if(result.getCode()==0){
					fileId = result.getData();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			if(storageClient!=null){
				storageClientPool.returnObject(storePath.store, storageClient);
			}
		}
		return fileId;
	}
	
	private class StoragePath{
		private String store;
		private int path;
	}
	
	private StoragePath getStoreIp(String group) throws Exception{
		String trackerAddr = getTrackerAddr();
		TrackerClient trackerClient = null;
		try{
			trackerClient = trackerClientPool.borrowObject(trackerAddr);
			Result<List<StorageInfo>> storageInfos = trackerClient.getStorageInfos(group);
			if(storageInfos.getCode()==0){
				List<StorageInfo> list = storageInfos.getData();
				if(list!=null&&list.size()>0){
					Random random = new Random();
					int size = list.size();
					int haha = random.nextInt(size);
					int index = haha%size;
					StorageInfo info = list.get(index);
					
					String ipAddr = info.getIpAddr();
					int port = info.getStoragePort();
					StoragePath storagePath = new StoragePath();
					storagePath.store = ipAddr+":"+port;
					storagePath.path = random.nextInt(info.getStorePathCount());
					return storagePath;
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}finally{
			if(trackerClient!=null){
				trackerClientPool.returnObject(trackerAddr, trackerClient);
			}
		}
		return null;
	}
	
	@Override
	public String upload(File file, String fileName, String group)
			throws Exception {
		String fileId = null;
		StorageClient storageClient = null;
		StoragePath storeIp = null;
		try {
			storeIp = this.getStoreIp(group);
			if(storeIp!=null){
				storageClient = storageClientPool.borrowObject(storeIp.store);
				Result<String> result = storageClient.upload(file, fileName, (byte)storeIp.path);
				if(result.getCode()==0){
					fileId = result.getData();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			if(storageClient!=null){
				storageClientPool.returnObject(storeIp.store, storageClient);
			}
		}
		return fileId;
	}

	public String upload(File file,String fileName) throws Exception{
		String trackerAddr = getTrackerAddr();
		TrackerClient trackerClient = null;
		StorageClient storageClient = null;
		String storageAddr = null;
		String fileId = null;
		try {
			trackerClient = trackerClientPool.borrowObject(trackerAddr);
			Result<UploadStorage> result = trackerClient.getUploadStorage();
			if(result.getCode()==0){
				storageAddr = result.getData().getAddress();
				storageClient = storageClientPool.borrowObject(storageAddr);
				Result<String> result2 = storageClient.upload(file, fileName, result.getData().getPathIndex());
				if(result2.getCode()==0){
					fileId = result2.getData();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			if(storageClient!=null){
				storageClientPool.returnObject(storageAddr, storageClient);
			}
			if(trackerClient!=null){
				trackerClientPool.returnObject(trackerAddr, trackerClient);
			}
		}
		return fileId;
	}
	
	public Boolean delete(String fileId) throws Exception{
		String trackerAddr = getTrackerAddr();
		TrackerClient trackerClient = null;
		StorageClient storageClient = null;
		Boolean result=false;
		String storageAddr=null;
		try{
			FastDfsFile fastDfsFile = new FastDfsFile(fileId);
			trackerClient = trackerClientPool.borrowObject(trackerAddr);
			Result<String> result2 = trackerClient.getUpdateStorageAddr(fastDfsFile.group, fastDfsFile.fileName);
			if(result2.getCode()==0){
				storageAddr = result2.getData();
				storageClient = storageClientPool.borrowObject(storageAddr);
				Result<Boolean> result3 = storageClient.delete(fastDfsFile.group, fastDfsFile.fileName);
				if(result3.getCode()==0){
					result = true;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}finally{
			if(storageClient!=null){
				storageClientPool.returnObject(storageAddr, storageClient);
			}
			if(trackerClient!=null){
				trackerClientPool.returnObject(trackerAddr, trackerClient);
			}
		}
		return result;
	}
	
	public String getTrackerAddr(){
        Random r = new Random();
        int i = r.nextInt(trackerAddrs.size());
		return trackerAddrs.get(i);
	}
	
	private class FastDfsFile{
		private String group;
		private String fileName;
		
		public FastDfsFile(String fileId) {
			super();
			int pos = fileId.indexOf("/");
			group = fileId.substring(0, pos);
			fileName = fileId.substring(pos+1);
		}
	}
	
	public String[] splitFile(String fileId) {
		//修正从第一个开始
		String[] strs = new String[2];
		if(fileId.startsWith("/")){
			int pos = fileId.indexOf("/", 1);
			if(pos>0){
				strs[0] = fileId.substring(1, pos); // group name
				strs[1] = fileId.substring(pos + 1); // file name
			}else{
				return null;
			}
		}else{
			int pos = fileId.indexOf("/");
			if(pos>0){
				strs[0] = fileId.substring(0, pos); // group name
				strs[1] = fileId.substring(pos + 1); // file name
			}else{
				return null;
			}
		}
		return strs;
	}

	@Override
	public byte[] download(String fileId) throws Exception {
		String[] gf = this.splitFile(fileId);
		if(gf!=null){
			String group = gf[0];
			String fileName = gf[1];
			StorageClient storageClient = null;
			StoragePath storeIp = null;
			try {
				storeIp = this.getStoreIp(group);
				if(storeIp!=null){
					storageClient = storageClientPool.borrowObject(storeIp.store);
					Result<DownloadResult> result = storageClient.download(group, fileName);
					if(result.getCode()==0){
						return result.getData().getData();
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw e;
			} finally {
				if(storageClient!=null){
					storageClientPool.returnObject(storeIp.store, storageClient);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("resource")
	@Override
	public boolean transferTo(String fileId, File file) throws Exception {
		File parent = file.getParentFile();
		if(file.exists()){
			logger.error("file already exist:"+file.getAbsolutePath());
			return false;
		}
		if(!parent.exists()){
			parent.mkdirs();
		}
		
		if(file.createNewFile()){
			byte[] download = this.download(fileId);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(download);
			fos.close();
			return true;
		}else{
			logger.error("create file :"+file.getAbsolutePath()+" error");
		}
		return false;
	}

	@Override
	public String uploadSlave(String fileId, File file, String suffix,String ext) throws Exception {
		String[] gf = this.splitFile(fileId);
		if(gf!=null){
			String group = gf[0];
			String fileName = gf[1];
			if(ext==null){
				ext = "bak";
				int idx = fileName.lastIndexOf('.');
				if(idx>0){
					ext = fileName.substring(idx+1);
				}	
			}
			StorageClient storageClient = null;
			StoragePath storeIp = null;
			try {
				storeIp = this.getStoreIp(group);
				if(storeIp!=null){
					storageClient = storageClientPool.borrowObject(storeIp.store);
					Result<String> result = storageClient.uploadSlave(file, fileName, suffix, ext);
					if(result.getCode()==0){
						return result.getData();
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw e;
			} finally {
				if(storageClient!=null){
					storageClientPool.returnObject(storeIp.store, storageClient);
				}
			}
		}
		return null;
	}

	@Override
	public String uploadSlave(String fileId, byte[] bytes, String suffix,String ext) throws Exception {
		String[] gf = this.splitFile(fileId);
		if(gf!=null){
			String group = gf[0];
			String fileName = gf[1];
			if(ext==null){
				ext = "bak";
				int idx = fileName.lastIndexOf('.');
				if(idx>0){
					ext = fileName.substring(idx+1);
				}	
			}
			StorageClient storageClient = null;
			StoragePath storeIp = null;
			try {
				storeIp = this.getStoreIp(group);
				if(storeIp!=null){
					storageClient = storageClientPool.borrowObject(storeIp.store);
					Result<String> result = storageClient.uploadSlave(bytes, fileName, suffix, ext);
					if(result.getCode()==0){
						return result.getData();
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw e;
			} finally {
				if(storageClient!=null){
					storageClientPool.returnObject(storeIp.store, storageClient);
				}
			}
		}
		return null;
	}
	
}
