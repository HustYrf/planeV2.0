package hust.plane.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import hust.plane.mapper.pojo.Route;
import hust.plane.utils.pojo.RouteExcel;

public class ExcelUtil {

	private static final String EXCEL_XLS = "xls";
	private static final String EXCEL_XLSX = "xlsx";

	public static Workbook getWorkbok(File file) throws IOException {
		Workbook wb = null;
		if (file.getName().endsWith(EXCEL_XLS)) { // Excel&nbsp;2003
			wb = new HSSFWorkbook();
		} else if (file.getName().endsWith(EXCEL_XLSX)) { // Excel 2007/2010
			wb = new XSSFWorkbook();
		} else {
			System.out.println("格式有错误");
		}
		return wb;

	}

	// 创建excel样式
	private static CellStyle createCellStyle(Workbook workbook, short fontsize) {
		// TODO Auto-generated method stub
		CellStyle style = workbook.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);// 水平居中
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直居中
		// 创建字体
		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints(fontsize);
		// 加载字体
		style.setFont(font);
		return style;
	}

	// 写入数据
	public static void writeExcel(List<RouteExcel> list, String path) {

		try {
			// 创建文件
			File file = new File(path);
			// 创建工作
			Workbook workbok = getWorkbok(file);
			// 创建头标题样式
			CellStyle headStyle = createCellStyle(workbok, (short) 16);
			// 创建列标题样式
			CellStyle colStyle = createCellStyle(workbok, (short) 13);
			// 创建工作表
			Sheet sheet = workbok.createSheet("路由列表");
			// 创建行
			Row row = sheet.createRow(0);
			Cell cell = row.createCell(0);
			// 加载单元格样式
			cell.setCellStyle(headStyle);
			cell.setCellValue("路由");
			// 创建列标题
			Row row2 = sheet.createRow(1);
			String[] tities = { "编号", "经度", "纬度" };

			for (int i = 0; i < tities.length; i++) {
				Cell cell2 = row2.createCell(i);
				// 加载单元格样式
				cell2.setCellStyle(colStyle);
				cell2.setCellValue(tities[i]);

			}
			// 将数据写入excel
			if (list != null) {
				for (int j = 0; j < list.size(); j++) {
					// 创建数据行
					Row row3 = sheet.createRow(j + 2);
					Cell cell1 = row3.createCell(0);
					cell1.setCellValue(list.get(j).getId());
					Cell cell2 = row3.createCell(1);
					cell2.setCellValue(list.get(j).getLongitude());
					Cell cell3 = row3.createCell(2);
					cell3.setCellValue(list.get(j).getLatitude());

				}
				// 输出
				FileOutputStream out = new FileOutputStream(file);
				workbok.write(out);
				workbok.close();
				out.close();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 读取内容
	public static boolean readExcel(File file, Route route) {

		try {
			FileInputStream fis = new FileInputStream(file);
			Workbook workbook = null;
			if (file.getName().endsWith(EXCEL_XLS)) { // Excel&nbsp;2003
				workbook = new HSSFWorkbook(fis);
			} else if (file.getName().endsWith(EXCEL_XLSX)) { // Excel 2007/2010
				workbook = new XSSFWorkbook(fis);
			} else {
				System.out.println("格式有错误");
			}
			// 2.读取工作表
			Sheet sheet = workbook.getSheetAt(0);
			// 3.读取行
			// 判断行数大于4,是因为路由点数据从第4行开始插入

			if (sheet.getPhysicalNumberOfRows() >= 4) {

				RouteExcel routeExcel = null;
				// 读取第0行0列作为路由名称
				Row row0 = sheet.getRow(0);
				Object name = row0.getCell(0);
				if(name==null) {
					return false;
				}
				route.setName(name.toString());
				// 读取第1行0列作为路由描述
				Row row1 = sheet.getRow(1);
				Object description = row1.getCell(0);
				if(description == null) {
					return false;
				}
				route.setDescription(description.toString());
				
				// 读取第3行6列作为路由类型
				Row row3 = sheet.getRow(3);
				Object type = row3.getCell(6);
				String typestr;
				if (type == null) {
					return false;
				} else {
					typestr = type.toString();
				}
				switch (typestr) {
					case "一干":
						route.setType(1);
						break;
					case "二干":
						route.setType(2);
						break;
					case "混合":
						route.setType(0);
						break;
					default:
						return false;
				}
				// 读取路由点数据及标桩数据
				List<RouteExcel> list = new ArrayList<RouteExcel>();
				List<String> flagdata = new ArrayList<String>();

				for (int k = 3; k < sheet.getPhysicalNumberOfRows(); k++) {
					// 读取单元格
					Row row = sheet.getRow(k);

					routeExcel = new RouteExcel();

					String flag = row.getCell(0).toString();
					flagdata.add(flag);
					// 得到经度
					Cell cell1 = row.getCell(1);
					try {
						Double Longitude = Double.parseDouble(cell1.getStringCellValue());
						routeExcel.setLongitude(Longitude);
					} catch (Exception e) {
						return false;
					}
					// 得到维度
					Cell cell2 = row.getCell(2);
					try {
						Double Latitude = Double.parseDouble(cell2.getStringCellValue());
						routeExcel.setLatitude(Latitude);
					} catch (Exception e) {
						return false;
					}

					list.add(routeExcel);
				}
				route.setRoutepathdata(LineUtil.ListToString(list));
				route.setFlagdata(flagdata.toString().replace("[", "").replace("]", ""));
				// 构成经纬度序列
				// String s = LineUtil.ListToString(readExcel);
			} else {
				return false;
			}

			workbook.close();
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/*
	 * public static void main(String[] args) { String path="D:\\test2.xlsx";
	 * List<RouteExcel> readExcellist = new ArrayList<RouteExcel>(); RouteExcel
	 * routeExcel=new RouteExcel(); routeExcel.setId(1.0);
	 * routeExcel.setLongitude(20.99); routeExcel.setLatitude(30.99);
	 * readExcellist.add(routeExcel); RouteExcel routeExcel2=new RouteExcel();
	 * routeExcel2.setId(2.0); routeExcel2.setLongitude(10.999999);
	 * routeExcel2.setLatitude(30.99); readExcellist.add(routeExcel2);
	 * ExcelUtil.writeExcel(readExcellist, path);
	 * 
	 * List<RouteExcel> readExcellist2 = ExcelUtil.readExcel(path); for(RouteExcel
	 * r:readExcellist2) { System.out.println(r.getId()); } }
	 */
	public static void inputStreamToFile(InputStream ins, File file) {
		try {
			OutputStream os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			ins.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		String string = "123.4";
		String string2 = "ggg";

		Double d1 = Double.parseDouble(string);
		System.out.println(d1);
		try {
			Double d2 = Double.parseDouble(string2);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
