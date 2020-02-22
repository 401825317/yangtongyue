package com.yangtongyue.yangtongyue.utils;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.http.util.TextUtils;

public class ImageUtils {
	public static boolean isImg(String fileName) {
		 Path path = Paths.get(fileName);  
         String contentType = null;  
         try {  
             contentType = Files.probeContentType(path);  
             if (!TextUtils.isEmpty(fileName)&&contentType.contains("image/")){
                 return true;
             }
         } catch (IOException e) {  
             e.printStackTrace();  
         }
		return false;
	}
	
    public static boolean isVedio(String fileName){
    	 Path path = Paths.get(fileName);  
         String contentType = null;  
         try {  
             contentType = Files.probeContentType(path);  
             if (!TextUtils.isEmpty(fileName)&&contentType.contains("video/")){
                 return true;
             }
         } catch (IOException e) {  
             e.printStackTrace();  
         }
      
        return false;
    }
    
	
    public static boolean isAudio(String fileName){
    	 Path path = Paths.get(fileName);  
         String contentType = null;  
         try {  
             contentType = Files.probeContentType(path);  
             if (!TextUtils.isEmpty(fileName)&&contentType.contains("video/")){
                 return true;
             }
         } catch (IOException e) {  
             e.printStackTrace();  
         }
      
        return false;
    }
    
    
    public static void main(String[] args) {
//    	System.out.println(isVedioFile("C:\\Users\\Administrator\\Documents\\WeChat Files\\yjf-18600533346\\FileStorage\\Video\\2020-02\\802181315a6b7344ab801da3871cd221.mp4"));;
    	 Path path = Paths.get(".png");  
         String contentType = null;  
         try {  
             contentType = Files.probeContentType(path);  
         } catch (IOException e) {  
             e.printStackTrace();  
         }
         System.out.println("File content type is : " + contentType);
	}

	public static String getFileType(String suffixName) {
		Path path = Paths.get(suffixName);  
		String contentType = null;  
        try {  
            contentType = Files.probeContentType(path);  
            return contentType;
        } catch (IOException e) {  
            e.printStackTrace();  
        }
		return "";
	}
}
