package net.mikesu.fastdfs.command;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import net.mikesu.fastdfs.data.DownloadResult;
import net.mikesu.fastdfs.data.Result;

public class SimpleDownloadCmd extends AbstractCmd<DownloadResult>{
	
	public SimpleDownloadCmd(String group,String remoteFileName){
		byte[] bsOffset;
		byte[] bsDownBytes;
		byte[] groupBytes;
		byte[] filenameBytes;
		byte[] bs;
		int groupLen;
		long fileOffset = 0;
		long downloadBytes = 0;
		bsOffset = long2buff(fileOffset);
		bsDownBytes = long2buff(downloadBytes);
		groupBytes = new byte[FDFS_GROUP_NAME_MAX_LEN];
		bs = group.getBytes(charset);
		filenameBytes = remoteFileName.getBytes(charset);
		Arrays.fill(groupBytes, (byte) 0);
		if (bs.length <= groupBytes.length) {
			groupLen = bs.length;
		} else {
			groupLen = groupBytes.length;
		}
		System.arraycopy(bs, 0, groupBytes, 0, groupLen);
		
		this.responseCmd = STORAGE_PROTO_CMD_RESP;
		this.responseSize = -1;
		this.requestCmd = STORAGE_PROTO_CMD_DOWNLOAD_FILE;
		this.body1 = new byte[bsOffset.length + bsDownBytes.length + groupBytes.length + filenameBytes.length];
		//接着写fileOffset long型
		System.arraycopy(bsOffset, 0, body1, 0, bsOffset.length);
		//接着写 downloadBytes long型
		System.arraycopy(bsDownBytes, 0, body1, bsOffset.length, bsDownBytes.length);
		//接着写group的bytes
		System.arraycopy(groupBytes, 0, body1, bsOffset.length+bsDownBytes.length, groupBytes.length);
		//接着写filename的bytes
		System.arraycopy(filenameBytes, 0, body1, bsOffset.length+bsDownBytes.length+groupBytes.length, filenameBytes.length);
	}
	
	@Override
	public Result<DownloadResult> exec(Socket socket) throws IOException {
		this.request(socket.getOutputStream());
		Response response = response(socket.getInputStream());
		if(response.isSuccess()){
			byte[] data = response.getData();
			DownloadResult result = new DownloadResult(data);
			return new Result<DownloadResult>(response.getCode(),result);
		}
		else{
			Result<DownloadResult> result = new Result<DownloadResult>(response.getCode());
			result.setMessage("Error");
			return result;
		}
	}

}
