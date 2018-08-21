package com.ald.fanbei.web.test.api.dsed;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BufferStream {
    public static void main(String[] args) {
        try {
            getBufferStream();
            getStream();
            getBufferWrite();
            readByChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void getBufferStream() throws IOException {
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream  bufferedOutputStream = null;
        try {
            long startTime = System.currentTimeMillis();
//            System.out.println("getBufferStream start = "+startTime);
            bufferedInputStream = new BufferedInputStream(new FileInputStream(new File("E:/HTTP.pdf")));
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("E:/HTTP2.pdf")));
            int length = 0;
            byte[] data = new byte[1024];
            while ((length = bufferedInputStream.read(data)) != -1){
                bufferedOutputStream.write(data,0,length);
            }
            bufferedOutputStream.flush();
            long endTime = System.currentTimeMillis();
            System.out.println("getBufferStream need time = "+(endTime-startTime));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (bufferedOutputStream != null){
                bufferedOutputStream.close();
            }
            e.printStackTrace();
        }
    }

    public static void getStream() throws IOException {
        FileInputStream inputStream = null;
        FileOutputStream  outputStream = null;
        try {
            long startTime = System.currentTimeMillis();
//            System.out.println("getStream start = "+startTime);
            inputStream = new FileInputStream(new File("E:/HTTP.pdf"));
            outputStream = new FileOutputStream(new File("E:/HTTP3.pdf"));
            int length = 0;
            byte[] data = new byte[1024];
            while ((length = inputStream.read(data)) != -1){
                outputStream.write(data,0,length);
            }
            outputStream.flush();
            long endTime = System.currentTimeMillis();
            System.out.println("getStream need time = "+(endTime-startTime));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (outputStream != null){
                outputStream.close();
            }
            e.printStackTrace();
        }
    }


    public static  void  getBufferWrite(){
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        try {
            long startTime = System.currentTimeMillis();
//            System.out.println("getBufferWrite start = "+startTime);
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("E:/HTTP.pdf"),"UTF-8"));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:/HTTP4.pdf"),"UTF-8"));
            String data = null;
            while ((data = bufferedReader.readLine()) != null ){
                bufferedWriter.write(data);
            }
            bufferedWriter.close();
            long endTime = System.currentTimeMillis();
            System.out.println("getBufferWrite need time = "+(endTime-startTime));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readByChannel(){
        FileChannel fileInChannel = null;
        FileChannel fileOutChannel = null;
        try {
            long startTime = System.currentTimeMillis();
//            System.out.println("readByChannel start = "+startTime);
            fileInChannel = new FileInputStream(new File("E:/HTTP.pdf")).getChannel();
            fileOutChannel = new FileOutputStream(new File("E:/HTTP5.pdf")).getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
            int offset = 0;
            while ((offset = fileInChannel.read(byteBuffer)) != -1){
                byteBuffer.flip();
                fileOutChannel.write(byteBuffer);
                byteBuffer.clear();
            }
            fileOutChannel.close();
            long endTime = System.currentTimeMillis();
            System.out.println("readByChannel need time = "+(endTime-startTime));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (fileOutChannel != null){
                    fileOutChannel.close();
                }
                if (fileInChannel != null){
                    fileInChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
