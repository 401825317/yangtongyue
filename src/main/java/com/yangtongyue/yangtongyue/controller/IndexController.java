package com.yangtongyue.yangtongyue.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.yangtongyue.yangtongyue.entity.Image;
import com.yangtongyue.yangtongyue.intefaces.ImageRepository;

@Controller
public class IndexController {
    @RequestMapping("")
    public String index(HttpServletRequest request){
    	List<Image> r=getImageList();
    	request.setAttribute("list", r);
        return "index";
    }
    
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
             Image i=new Image();
             i.setImageid(uuid);
             i.setName(fileName);
             i.setUploadpath("upload");
             i.setUploadtime(System.currentTimeMillis());
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