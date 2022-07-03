package com.example.httpurlconnectiontest;

//这里我们主要针对GET和POST请求写两个不同的使用示例，我们可以conn.getInputStream() 获取到的是一个流，所以我们需要写一个类将流转化为二进制数组！

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamTool {
    //从流中读取数据
    public static byte[] read(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len = inStream.read(buffer)) != -1)
        {
            outStream.write(buffer,0,len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
}
