package hust.plane.web.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mysql.fabric.xmlrpc.base.Array;

import hust.plane.mapper.pojo.Route;
import hust.plane.service.interFace.FileService;
import hust.plane.utils.ExcelUtil;
import hust.plane.utils.pojo.JsonView;

@Controller
public class FileController {

	@Autowired
	private FileService FileServiceImpl;

	// 导入路由功能
	@RequestMapping("/oneFileImport")
	public String importOneFile(@RequestParam("routePathExcel") MultipartFile file, Route route) {

		if (file == null) // 判断上传失败或者空文件
			return "文件上传失败";
		long size = file.getSize();
		String filename = file.getOriginalFilename();
		if (filename == null || ("").equals(filename) && size == 0)
			return "文件为空";

		File f = null; // 把MultipartFile转化成File
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
		del.delete(); // 删除临时文件

		return JsonView.render(0, "导入成功!");
	}

	// 上传路由文件，多文件上传
	@RequestMapping(value = "routesImport", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@ResponseBody
	public String routesImport(@RequestParam(value = "routePathExcel", required = true)MultipartFile[] routeFiles, HttpServletRequest request) {

		List<String> errorFile = new ArrayList<String>();
		
		if (routeFiles != null && routeFiles.length > 0) {
			//遍历已上传的文件，并存入数据库中
			for (int i = 0; i < routeFiles.length; i++) {
				MultipartFile file = routeFiles[i];
				//对上传的文件进行处理
				
				/*
				 * long size = file.getSize(); String filename = file.getOriginalFilename();
				 * File f = null; Route route = new Route(); if (filename == null ||
				 * ("").equals(filename) && size == 0) { return JsonView.render(0, "文件为空!"); }
				 * else { InputStream ins; try { ins = file.getInputStream(); f = new
				 * File(file.getOriginalFilename()); ExcelUtil.inputStreamToFile(ins, f); }
				 * catch (IOException e) { e.printStackTrace(); } FileServiceImpl.insertRoute(f,
				 * route); File del = new File(f.toURI()); del.delete(); // 删除临时文件 }
				 */
			}
		}

		return JsonView.render(0, "导入成功!");
	}

	// 测试文件上传
	@RequestMapping("file")
	public String fileList(Model model) {
		return "file";
	}

	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
	@ResponseBody
	public String upload(@RequestParam(value = "files", required = true)MultipartFile[] files, HttpServletRequest request) throws IOException {

		// String path = request.getSession().getServletContext().getRealPath("upload");
		String path = "D:\\upload\\";
		if(files.length>0) {
			for(int i=0;i<files.length;i++) {
				String fileName = files[i].getOriginalFilename();// 获取到上传文件的名字
				File dir = new File(path, fileName);
				System.out.println(files[i].getSize());
				if (!dir.exists()) {
					dir.mkdirs();
				}
				//或者处理
				
				//保存
				files[i].transferTo(dir); // MultipartFile自带的解析方法
			}
			return JsonView.render(0, "导入成功!");
			
		}else {
			return JsonView.render(0, "导入失败，无文件!");
		}
	}	
}
