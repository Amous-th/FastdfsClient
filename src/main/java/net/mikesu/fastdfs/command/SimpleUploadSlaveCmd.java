package net.mikesu.fastdfs.command;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

import net.mikesu.fastdfs.data.Result;

public class SimpleUploadSlaveCmd extends AbstractCmd<String> {

	private byte[] buf;
	
	private File file;
	
	private SimpleUploadSlaveCmd(long len, String masterfileId, String prefix, String ext){
        super();
        this.requestCmd = STORAGE_PROTO_CMD_UPLOAD_SLAVE_FILE;
        this.body2Len = len;
        this.responseCmd = STORAGE_PROTO_CMD_RESP;
        this.responseSize = -1;

        byte[] masterfileNameLenByte = long2buff(masterfileId.length());
        byte[] fileSizeLenByte = long2buff(len);
        byte[] prefixByte = prefix.getBytes(charset);
        byte[] fileExtNameByte = getFileExtNameByte(ext);
        int fileExtNameByteLen = fileExtNameByte.length;
        if(fileExtNameByteLen>FDFS_FILE_EXT_NAME_MAX_LEN){
            fileExtNameByteLen = FDFS_FILE_EXT_NAME_MAX_LEN;
        }

        byte[] masterfilenameBytes = masterfileId.getBytes(charset);

        this.body1 = new byte[2 * FDFS_PROTO_PKG_LEN_SIZE + FDFS_FILE_PREFIX_MAX_LEN + FDFS_FILE_EXT_NAME_MAX_LEN
                + masterfilenameBytes.length ];

        Arrays.fill(body1, (byte) 0);

        System.arraycopy(masterfileNameLenByte, 0, body1, 0, masterfileNameLenByte.length);
        System.arraycopy(fileSizeLenByte, 0, body1, FDFS_PROTO_PKG_LEN_SIZE , fileSizeLenByte.length);
        System.arraycopy(prefixByte, 0, body1, 2*FDFS_PROTO_PKG_LEN_SIZE , prefixByte.length);
        System.arraycopy(fileExtNameByte, 0, body1,2 * FDFS_PROTO_PKG_LEN_SIZE + FDFS_FILE_PREFIX_MAX_LEN , fileExtNameByteLen);
        System.arraycopy(masterfilenameBytes, 0, body1,2 * FDFS_PROTO_PKG_LEN_SIZE + FDFS_FILE_PREFIX_MAX_LEN + FDFS_FILE_EXT_NAME_MAX_LEN ,
                masterfilenameBytes.length);
	}

	public SimpleUploadSlaveCmd(byte[] buf, String masterfileId, String prefix, String ext){
		this(buf.length,masterfileId,prefix,ext);
        this.buf = buf;
    }
	
	public SimpleUploadSlaveCmd(File file, String masterfileId, String prefix, String ext){
		this(file.length(),masterfileId,prefix,ext);
        this.file = file;
    }

	@Override
	public Result<String> exec(Socket socket) throws IOException {
		InputStream is = null;
		if(file!=null){
			is = new FileInputStream(file);
		}else{
			is = new ByteArrayInputStream(buf);
		}
		request(socket.getOutputStream(), is);
		Response response = response(socket.getInputStream());
		if(response.isSuccess()){
			byte[] data = response.getData();
			String group = new String(data, 0,	FDFS_GROUP_NAME_MAX_LEN).trim();
			String remoteFileName = new String(data,FDFS_GROUP_NAME_MAX_LEN, data.length - FDFS_GROUP_NAME_MAX_LEN);
			Result<String> result = new Result<String>(response.getCode());
			result.setData(group + "/" + remoteFileName);
			return result;
		}else{
			Result<String> result = new Result<String>(response.getCode());
			result.setMessage("Error");
			return result;
		}
	}

	private byte[] getFileExtNameByte(String extName) {

		if (extName != null && extName.length() > 0) {
			return extName.getBytes(charset);
		}else{
			return new byte[0];
		}
	}
}
