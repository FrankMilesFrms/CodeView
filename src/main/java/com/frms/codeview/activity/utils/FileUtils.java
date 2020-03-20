/*
 * Copyright Frms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.frms.codeview.activity.utils;

/**
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/3/16 20:23(ydt)
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtils {
    
    static final int BUFFER_SIZE = 0x300000;// 缓冲区大小为3M
    /**
     * 从输入流中读取string
     * @param inputStream
     * @return
     */
    public static String getString(InputStream inputStream){
        String returnStr = "";
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuffer sb = new StringBuffer();
            String str = "";
            while ((str = reader.readLine()) != null)
            {
                sb.append(str).append("\n");
            }
            returnStr = sb.toString();
        }catch(Exception e){
        
        }
        return returnStr;
    }
    
    
    /**
     * 新建一个文件并写入内容
     *
     * @param path    文件全路径
     * @param fileName 文件名
     * @param content  内容
     * @param bufLen      设置缓冲区大小
     * @param isWrite 是否追加写入文件
     * @return boolean
     * @throws IOException
     */
    public  static boolean newStaticFile(String path, String fileName, String content,
                                         int bufLen, boolean isWrite)  {
        
        if (path == null || path.equals("") || content == null
            || content.equals(""))
            return false;
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fos = null;
        Writer out = null;
        try {
            fos = new FileOutputStream(path + File.separator + fileName,
                isWrite);
            out = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"),
                bufLen);
            out.write(content);
            out.flush();
            flag = true;
            //log.info(content);
        } catch (IOException e) {
            System.out.println("写入文件出错");
            flag = false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }
    
    
    /**
     * 复制一个目录及其子目录、文件到另外一个目录
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void copyFolder(File src, File dest){
        try{
            if (src.isDirectory()) {
                if (!dest.exists()) {
                    dest.mkdirs();
                }
                String files[] = src.list();
                for (String file : files) {
                    File srcFile = new File(src, file);
                    File destFile = new File(dest, file);
                    // 递归复制
                    copyFolder(srcFile, destFile);
                }
            } else {
                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dest);
                
                byte[] buffer = new byte[1024];
                
                int length;
                
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
    
    /**
     * 复制单个文件
     *
     * @param oldPath
     *            String 原文件路径 如：c:/fqf.txt
     * @param newPath
     *            String 复制后路径 如：f:/
     * @return boolean
     */
    public static boolean copyFile(String oldPath, String newPath,
                                   String newFileName) throws IOException {
        
        boolean flag = false;
        if (oldPath == null || newPath == null || newPath.equals("")
            || oldPath.equals("")) {
            return flag;
        }
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int bytesum = 0;
            int byteread = 0;
            File file = null;
            file = new File(newPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(oldPath);
            if (file.exists()) { // 文件存在时
                inStream = new FileInputStream(oldPath); // 读入原文件
                if (newFileName == null || newFileName.equals("")) {
                    newFileName = file.getName();
                }
                fs = new FileOutputStream(newPath + File.separator
                                          + newFileName);
                byte[] buffer = new byte[1024 * 8];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
//					System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                flag = true;
            }
        } catch (IOException e) {
            throw e;
            
        } finally {
            try {
                if (fs != null) {
                    fs.close();
                }
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                throw e;
            }
        }
        return flag;
    }
    
    /**
     * 读取文本文件内容，以行的形式读取
     *
     * @param filePathAndName
     *            filePathAndName 带有完整绝对路径的文件名
     * @return String 返回文本文件的内容
     */
    public static String readFileContent(String filePathAndName)
        throws IOException {
        return readFileContent(filePathAndName, "UTF-8", null, 1024);
    }
    
    /**
     * 读取文本文件内容，以行的形式读取
     *
     * @param filePathAndName
     *            filePathAndName 带有完整绝对路径的文件名
     * @param encoding 文本文件打开的编码方式 例如 GBK,UTF-8
     * @param sep 分隔符 例如：#，默认为/n;
     * @param bufLen 设置缓冲区大小
     * @return String 返回文本文件的内容
     */
    public static String readFileContent(String filePathAndName,
                                         String encoding, String sep, int bufLen) throws IOException {
        if (filePathAndName == null || filePathAndName.equals("")) {
            return "";
        }
        if (sep == null || sep.equals("")) {
            sep = System.getProperty("line.separator");
        }
        if (!new File(filePathAndName).exists()) {
            return "";
        }
        StringBuffer str = new StringBuffer("");
        FileInputStream fs = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fs = new FileInputStream(filePathAndName);
            if (encoding == null || encoding.trim().equals("")) {
                isr = new InputStreamReader(fs);
            } else {
                isr = new InputStreamReader(fs, encoding.trim());
            }
            br = new BufferedReader(isr, bufLen);
            
            String data = "";
            while ((data = br.readLine()) != null) {
                if(!str.toString().isEmpty()){
                    str.append(sep);
                }
                str.append(data);
            }
            
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (br != null)
                    br.close();
                if (isr != null)
                    isr.close();
                if (fs != null)
                    fs.close();
            } catch (IOException e) {
                throw e;
            }
            
        }
        return str.toString();
    }
    /**
     * 新建一个文件并写入内容
     *
     * @param path 文件全路径
     * @param content 内容
     * @return boolean
     * @throws IOException
     */
    public static boolean newFile(String path, String content)
        throws IOException
    {
        new FileOutputStream(path).write(content.getBytes());
        return true;
    }
    /**
     * 新建一个文件并写入内容
     *
     * @param path 文件全路径
     * @param fileName 文件名
     * @param content 内容
     * @param bufLen 设置缓冲区大小
     * @param isWrite 是否追加写入文件
     * @return boolean
     * @throws IOException
     */
    public static boolean newFile(String path, String fileName, String content,
                                  int bufLen, boolean isWrite) throws IOException {
        
        if (path == null || path.equals("") || content == null
            || content.equals(""))
            return false;
        boolean flag = false;
        FileWriter fw = null;
        BufferedWriter bw = null;
        
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            
        }
        try {
            fw = new FileWriter(path + File.separator + fileName, isWrite);
            bw = new BufferedWriter(fw, bufLen);
            bw.write(content);
            flag = true;
        } catch (IOException e) {
            System.out.println("写入文件出错");
            flag = false;
            throw e;
        } finally {
            if (bw != null) {
                bw.flush();
                bw.close();
            }
            if (fw != null)
                fw.close();
        }
        
        return flag;
    }
    
    /**
     * 通过url下载文件
     * @param urlStr
     * @return
     * @throws Exception
     */
    public static InputStream downLoadFromUrl(String urlStr/*,String fileName,String savePath*/) throws Exception{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        
        //得到输入流
        InputStream inputStream = conn.getInputStream();
		/*//获取自己数组
		byte[] getData = readInputStream(inputStream);
		//文件保存位置
		File saveDir = new File(savePath);
		if(!saveDir.exists()){
			saveDir.mkdir();
		}
		File file = new File(saveDir+File.separator+fileName);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(getData);
		if(fos!=null){
			fos.close();
		}
		if(inputStream!=null){
			inputStream.close();
		}
		System.out.println("info:"+url+" download success");*/
        return inputStream;
    }
    
    /**
     * 读取大文本文件
     * @param file
     * @return
     * @throws Exception
     */
    public static String readBigFile(String file) throws Exception
    {
        StringBuffer sb = new StringBuffer();
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(file)));
        BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), BUFFER_SIZE);
        
        while (in.ready()) {
            String line = in.readLine();
            sb.append(line);
            sb.append("\n");
        }
        in.close();
        
        if(sb.length() < 1)
        {
            return sb.toString();
        }
        
        return sb.substring(0, sb.length() - 1);
    }
}

