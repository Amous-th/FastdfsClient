package net.mikesu.fastdfs.client;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import net.mikesu.fastdfs.FastdfsClientConfig;
import net.mikesu.fastdfs.command.CloseCmd;
import net.mikesu.fastdfs.command.Command;
import net.mikesu.fastdfs.command.DeleteCmd;
import net.mikesu.fastdfs.command.FileUploadCmd;
import net.mikesu.fastdfs.command.GetMetaDataCmd;
import net.mikesu.fastdfs.command.QueryFileInfoCmd;
import net.mikesu.fastdfs.command.SetMetaDataCmd;
import net.mikesu.fastdfs.command.SimpleDownloadCmd;
import net.mikesu.fastdfs.command.SimpleUploadCmd;
import net.mikesu.fastdfs.command.SimpleUploadSlaveCmd;
import net.mikesu.fastdfs.data.DownloadResult;
import net.mikesu.fastdfs.data.FileInfo;
import net.mikesu.fastdfs.data.Result;

public class StorageClientImpl implements StorageClient{
	
	private Socket socket;
	private String host;
	private Integer port;
	private Integer connectTimeout = FastdfsClientConfig.DEFAULT_CONNECT_TIMEOUT * 1000;
	private Integer networkTimeout = FastdfsClientConfig.DEFAULT_NETWORK_TIMEOUT * 1000;
	private boolean connected = false;
	
	private Socket getSocket() throws IOException{
		if(socket==null){
			socket = new Socket();
			socket.setSoTimeout(networkTimeout);
			socket.connect(new InetSocketAddress(host, port),connectTimeout);
			connected = true;
		}
		//修复服务器或者网络抖动连接异常
		if(!connected){
			socket.connect(new InetSocketAddress(host, port),connectTimeout);
			connected = true;
		}
		return socket;
	}
	
	public StorageClientImpl(String address){
		super();
		String[] hostport = address.split(":");
		this.host = hostport[0];
		this.port = Integer.valueOf(hostport[1]);
	}
	
	public StorageClientImpl(String address,Integer connectTimeout, Integer networkTimeout){
		this(address);
		this.connectTimeout = connectTimeout;
		this.networkTimeout = networkTimeout;
	}

	public void close() throws IOException{
		Socket socket = getSocket();
		Command<Boolean> command = new CloseCmd();
		command.exec(socket);
		socket.close();
		socket = null;
	}

	public Result<String> upload(File file,String fileName,byte storePathIndex) throws IOException{
		Socket socket = getSocket();
		FileUploadCmd uploadCmd = new FileUploadCmd(file, fileName,storePathIndex);
		return uploadCmd.exec(socket);
	}
	
	public Result<Boolean> delete(String group,String fileName) throws IOException{
		Socket socket = getSocket();
		DeleteCmd deleteCmd = new DeleteCmd(group,fileName);
		return deleteCmd.exec(socket);
	}

	@Override
	public Result<Boolean> setMeta(String group, String fileName,
			Map<String, String> meta) throws IOException {
		Socket socket = getSocket();
		SetMetaDataCmd setMetaDataCmd = new SetMetaDataCmd(group, fileName, meta);
		return setMetaDataCmd.exec(socket);
	}

	@Override
	public Result<Map<String, String>> getMeta(String group, String fileName)
			throws IOException {
		Socket socket = getSocket();
		GetMetaDataCmd getMetaDataCmd = new GetMetaDataCmd(group, fileName);
		return getMetaDataCmd.exec(socket);
	}

	@Override
	public Result<String> upload(byte[] bytes, String fileName,
			byte storePathIndex) throws IOException {
		if(bytes==null||bytes.length<1){
			throw new IOException("upload bytes of "+fileName+" bytes null");
		}
		Socket socket = getSocket();
		SimpleUploadCmd uploadCmd = new SimpleUploadCmd(bytes, fileName,storePathIndex);
		return uploadCmd.exec(socket);
	}

	@Override
	public Result<DownloadResult> download(String group, String remoteFileName)  throws IOException{
		Socket socket = getSocket();
		SimpleDownloadCmd downloadCmd = new SimpleDownloadCmd(group, remoteFileName);
		return downloadCmd.exec(socket);
	}

	@Override
	public Result<String> uploadSlave(byte[] buf, String masterfileId,
			String prefix, String ext) throws IOException {
		SimpleUploadSlaveCmd uploadCmd = new SimpleUploadSlaveCmd(buf, masterfileId,prefix,ext);
		Socket socket = getSocket();
		return uploadCmd.exec(socket);
	}
	
	@Override
	public Result<String> uploadSlave(File file, String masterfileId,
			String prefix, String ext) throws IOException {
		SimpleUploadSlaveCmd uploadCmd = new SimpleUploadSlaveCmd(file, masterfileId,prefix,ext);
		Socket socket = getSocket();
		return uploadCmd.exec(socket);
	}

	@Override
	public Result<FileInfo> queryFileInfo(String group, String remoteFileName)
			throws IOException {
		QueryFileInfoCmd uploadCmd = new QueryFileInfoCmd(group, remoteFileName);
		Socket socket = getSocket();
		return uploadCmd.exec(socket);
	}

}
