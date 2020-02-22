package com.yangtongyue.yangtongyue.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.qcloud.cos.model.ListObjectsRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.yangtongyue.yangtongyue.common.qcolud.YTYCOSClient;
import com.yangtongyue.yangtongyue.entity.Image;
import com.yangtongyue.yangtongyue.intefaces.ImageRepository;

@Controller
public class IndexController {
	private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("")
    public String index(HttpServletRequest request){
    	List<Image> r=getImageList();
    	request.setAttribute("list", r);
        return "index";
    }
	@Autowired
	public YTYCOSClient  ytycosClient;
    @Autowired
    private ImageRepository imageRepository;
    
    
    @RequestMapping("fileupload")
    @ResponseBody
    public String fileupload(@RequestParam MultipartFile file, Model model, HttpServletRequest request) throws MalformedURLException {
    	 if (file.isEmpty()) {
             System.out.println("文件为空空");
         }
         String fileName = file.getOriginalFilename();  // 文件名
         String suffixName = fileName.substring(fileName.lastIndexOf("."));  // 后缀名
//         String realPath=ClassUtils.getDefaultClassLoader().getResource("static").getPath()+File.separator+"upload"+File.separator;
         //获取文件名
         String realPath=request.getServletContext().getRealPath("/upload")+File.separator;
         System.out.println("上传文件名：" + fileName);
         String uuid=UUID.randomUUID().toString().replace("-", "");
         String uploadFileName = uuid+"_"+ fileName;
        		 System.out.println("获取上传路径：" + realPath + ", 上传的真实文件名：" + uploadFileName);
         File dest = new File(realPath+uploadFileName);
         if (!dest.getParentFile().exists()) {
             dest.getParentFile().mkdirs();
         }
         try {
             file.transferTo(dest);
             // 指定要上传的文件
     		File localFile = dest;
     		// 指定要上传到的存储桶
     		String bucketName = "yangtongyue-1252240258";
     		// 指定要上传到 COS 上对象键
     		String key = uploadFileName;
     		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
     		PutObjectResult putObjectResult = ytycosClient.cosClient.putObject(putObjectRequest);
     		LOG.info("putObjectResult="+JSON.toJSONString(putObjectResult));
     		ytycosClient.cosClient.shutdown();
     		LOG.info(file.getContentType());
             Image i=new Image();
             i.setImageid(uuid);
             i.setName(fileName);
             i.setUploadpath("https://"+bucketName+".cos.ap-shanghai.myqcloud.com/"+key);
             i.setUploadtime(System.currentTimeMillis());
             i.setSuffixName(suffixName);
//             String contentType=ImageUtils.getFileType(suffixName);
             if(file.getContentType().contains("image/")) {
            	 i.setType(0);
             }else if(file.getContentType().contains("video/")) {
            	 i.setType(1);
             } else if(file.getContentType().contains("audio/")) {
            	 i.setType(2);
             }else {
            	 i.setType(-1);

             }
             imageRepository.save(i);
         } catch (IOException e) {
             e.printStackTrace();
         }
         return "sucess";
    }
    
    
    @RequestMapping("getImageList")
    @ResponseBody
    public List<Image> getImageList(){
		List<Order> orders=Lists.newArrayList();
		Order order=new Sort.Order(Sort.Direction.DESC,"uploadtime");
		orders.add(order);
		Sort sort=Sort.by(orders);
		List<Image> result=imageRepository.findAll(sort);
		return result;
    	
    }
    
}