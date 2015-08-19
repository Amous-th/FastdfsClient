package net.mikesu.fastdfs.command;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import net.mikesu.fastdfs.data.FileInfo;
import net.mikesu.fastdfs.data.Result;

public class QueryFileInfoCmd extends AbstractCmd<FileInfo> {

	public QueryFileInfoCmd(String group, String remoteFilename) {
		byte[] groupBytes;
		byte[] filenameBytes;
		byte[] bs;
		int groupLen;
		
		filenameBytes = remoteFilename.getBytes(charset);			
		groupBytes = new byte[FDFS_GROUP_NAME_MAX_LEN];
		bs = group.getBytes(charset);
		Arrays.fill(groupBytes, (byte)0);
		if (bs.length <= groupBytes.length) {
			groupLen = bs.length;
		} else {
			groupLen = groupBytes.length;
		}
		System.arraycopy(bs, 0, groupBytes, 0, groupLen);
		
		this.requestCmd = STORAGE_PROTO_CMD_QUERY_FILE_INFO;
		this.responseCmd = STORAGE_PROTO_CMD_RESP;
		this.responseSize = 3 * FDFS_PROTO_PKG_LEN_SIZE + FDFS_IPADDR_SIZE;
		
		body1 = new byte[groupBytes.length+ filenameBytes.length];
		System.arraycopy(groupBytes, 0, body1, 0,groupBytes.length);
		System.arraycopy(filenameBytes, 0, body1,groupBytes.length, filenameBytes.length);
	}

	@Override
	public Result<FileInfo> exec(Socket socket) throws IOException {
		this.request(socket.getOutputStream());
		Response response = response(socket.getInputStream());
		if(response.isSuccess()){
			byte[] data = response.getData();
			long fileSize = buff2long(data, 0);
			int createTime = (int) buff2long(data,FDFS_PROTO_PKG_LEN_SIZE);
			int crc32 = (int) buff2long(data,2 * FDFS_PROTO_PKG_LEN_SIZE);
			String sourceStorageIp = (new String(data,3 * FDFS_PROTO_PKG_LEN_SIZE,FDFS_IPADDR_SIZE)).trim();
			FileInfo info = new FileInfo(fileSize, createTime, crc32, sourceStorageIp);
			return new Result<FileInfo>(response.getCode(),info);
		}
		else{
			Result<FileInfo> result = new Result<FileInfo>(response.getCode());
			result.setMessage("Error");
			return result;
		}
	}

}
