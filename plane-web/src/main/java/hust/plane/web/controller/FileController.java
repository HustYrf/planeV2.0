package hust.plane.web.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import hust.plane.mapper.pojo.Route;
import hust.plane.service.interFace.FileService;
import hust.plane.utils.ExcelUtil;

@Controller
public class FileController {
	
	@Autowired
	private FileService FileServiceImpl;

	
	// 导入路由功能
	@RequestMapping("/oneFileImport")
	public String importOneFile(@RequestParam("routePathExcel") MultipartFile file, Route route) {

		if (file == null)                   //判断上传失败或者空文件
			return "文件上传失败";
		long size = file.getSize();
		String filename = file.getOriginalFilename();
		if (filename == null || ("").equals(filename) && size == 0)
			return "文件为空";

		File f = null;                        //把MultipartFile转化成File
		if (file.equals("") || file.getSize() <= 0) {
			file = null;
		} else {
			InputStream ins;
			try {
				ins = file.getInputStream();
				f = new File(file.getOriginalFilename());
				ExcelUtil.inputStreamToFile(ins, f);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		FileServiceImpl.insertRoute(f, route);
		
  		 File del = new File(f.toURI());      
  		 del.delete();  //删除临时文件

		return "success";
	}
	
	

}
