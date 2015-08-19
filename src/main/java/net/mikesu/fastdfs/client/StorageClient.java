package net.mikesu.fastdfs.client;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import net.mikesu.fastdfs.data.DownloadResult;
import net.mikesu.fastdfs.data.FileInfo;
import net.mikesu.fastdfs.data.Result;

public interface StorageClient {
	
	public Result<DownloadResult> download(String group,String remoteFileName) throws IOException;
	
	public Result<FileInfo> queryFileInfo(String group,String remoteFileName) throws IOException;
	
	public Result<String> upload(File file,String fileName,byte storePathIndex) throws IOException;
	
	public Result<String> upload(byte[] bytes,String fileName,byte storePathIndex) throws IOException;
	
	public Result<Boolean> delete(String group,String fileName) throws IOException;
	
	public Result<Boolean> setMeta(String group,String fileName,Map<String,String> meta) throws IOException;
	
	public Result<Map<String,String>> getMeta(String group,String fileName) throws IOException;
	
	public void close() throws IOException;
	
	public Result<String> uploadSlave(byte[] buf, String masterfileId, String prefix, String ext) throws IOException;
	
	public Result<String> uploadSlave(File file, String masterfileId, String prefix, String ext) throws IOException;
}
