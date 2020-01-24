package pe.eclipse.neon.yeo._2017.y201712;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Year;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.poi.ss.formula.functions.Index;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReport {

	Workbook xlsWb = new XSSFWorkbook(); // Excel 2007 �̻�
	boolean isSCOPUS = false;

	public ExcelReport(boolean isSCOPUS) {
		this.isSCOPUS = isSCOPUS;
	}

	/**
	 * �󵵼� ������ �ִ´�.<br>
	 * ������ ��Ʈ��� ù��° Į������ �����̴�.<br>
	 * 
	 * @param data
	 * 
	 */
	public void createExcelSheetForCount(Map<Integer, Integer> data) {
		// Sheet ����
		String title = "������ ����";
		if (!isSCOPUS) {
			title = "������ Ư���";
		}
		Sheet sheet1 = xlsWb.createSheet(title);
		CellStyle cellStyle = xlsWb.createCellStyle();
		cellStyle.setWrapText(true);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		Set<Integer> set = data.keySet();
		int rowIDX = 0;
		int cellIDX = 0;

		Row row = null;
		Cell cell = null;
		row = sheet1.createRow(rowIDX++);
		cell = row.createCell(cellIDX++);
		cell.setCellValue("YEAR");
		cell.setCellStyle(cellStyle);
		cell = row.createCell(cellIDX++);
		cell.setCellValue("Count");
		cell.setCellStyle(cellStyle);
		cellIDX = 0;
		for (Integer i : set) {
			int value = data.get(i);
			cellIDX = 0;
			row = sheet1.createRow(rowIDX++);
			cell = row.createCell(cellIDX++);
			cell.setCellValue(i);
			cell.setCellStyle(cellStyle);
			cell = row.createCell(cellIDX++);
			cell.setCellValue(value);
			cell.setCellStyle(cellStyle);
		}

	}

	/**
	 * ���� ��Ʈ�� �����Ͽ� �󵵼��� �Է��Ѵ�.
	 * 
	 * @param sheetName
	 *            ��Ʈ��
	 * @param data
	 *            ��� ������
	 * @param excelFirstColumnNames
	 *            ���� ù��° Į�� ����
	 */
	public void createExcelSheetForCountCustom(String sheetName, Map<String, String> data,
			String[] excelFirstColumnNames) {
		// Sheet ����
		Sheet sheet1 = xlsWb.createSheet(sheetName);
		CellStyle cellStyle = xlsWb.createCellStyle();
		cellStyle.setWrapText(true);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		Set<String> set = data.keySet();
		int rowIDX = 0;
		int cellIDX = 0;

		Row row = null;
		Cell cell = null;
		row = sheet1.createRow(rowIDX++);

		for (String _ti : excelFirstColumnNames) {
			cell = row.createCell(cellIDX++);
			cell.setCellValue(_ti);
			cell.setCellStyle(cellStyle);
		}
		cellIDX = 0;
		for (String i : set) {
			String value = data.get(i);
			cellIDX = 0;
			row = sheet1.createRow(rowIDX++);
			cell = row.createCell(cellIDX++);
			cell.setCellValue(i);
			cell.setCellStyle(cellStyle);
			cell = row.createCell(cellIDX++);
			cell.setCellValue(Integer.parseInt(value));
			cell.setCellStyle(cellStyle);
		}

	}

	/**
	 * ���� ��Ʈ�� �����Ͽ� �󵵼��� �Է��Ѵ�.
	 * 
	 * @param sheetName
	 *            ��Ʈ��
	 * @param data
	 *            ��� ������
	 * @param excelFirstColumnNames
	 *            ���� ù��° Į�� ����
	 */
	public void createExcelSheetForCountCustom3(String sheetName, Map<String, String[]> data,
			String[] excelFirstColumnNames) {
		// Sheet ����
		Sheet sheet1 = xlsWb.createSheet(sheetName);
		CellStyle cellStyle = xlsWb.createCellStyle();
		cellStyle.setWrapText(true);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		Set<String> set = data.keySet();
		int rowIDX = 0;
		int cellIDX = 0;

		Row row = null;
		Cell cell = null;
		row = sheet1.createRow(rowIDX++);

		for (String _ti : excelFirstColumnNames) {
			cell = row.createCell(cellIDX++);
			cell.setCellValue(_ti);
			cell.setCellStyle(cellStyle);
		}
		cellIDX = 0;
		for (String i : set) {
			String[] value = data.get(i);
			cellIDX = 0;
			row = sheet1.createRow(rowIDX++);
			cell = row.createCell(cellIDX++);
			cell.setCellValue(i);
			cell.setCellStyle(cellStyle);
			for (String _value : value) {
				cell = row.createCell(cellIDX++);
				cell.setCellValue(_value);
				cell.setCellStyle(cellStyle);
			}
		}

	}

	/**
	 * ���� ��Ʈ�� �����Ͽ� �Ҽ��� Ȥ�� ����� �����͸� �Է��Ѵ�.
	 * 
	 * @param sheetName
	 *            ��Ʈ��
	 * @param data
	 *            ��� ������
	 * @param excelFirstColumnNames
	 *            ���� ù��° Į�� ����
	 */
	public void createExcelSheetForRateCustom(String sheetName, Map<String, String> data,
			String[] excelFirstColumnNames) {
		createExcelSheetForRateCustom(sheetName, data, excelFirstColumnNames, "");
	}

	/**
	 * ���� ��Ʈ�� �����Ͽ� �Ҽ��� Ȥ�� ����� �����͸� �Է��Ѵ�.
	 * 
	 * @param sheetName
	 *            ��Ʈ��
	 * @param data
	 *            ��� ������
	 * @param excelFirstColumnNames
	 *            ���� ù��° Į�� ����
	 * @param type
	 *            % �Է½� %������ ������ �ƹ��͵� �Է¾��ϸ� �Ҽ��� ǥ��.
	 */
	public void createExcelSheetForRateCustom(String sheetName, Map<String, String> data,
			String[] excelFirstColumnNames, String type) {
		// Sheet ����
		Sheet sheet1 = xlsWb.createSheet(sheetName);
		CellStyle cellStyle = xlsWb.createCellStyle();
		cellStyle.setWrapText(true);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);

		DataFormat format = xlsWb.createDataFormat();

		// cellStyle.set
		Set<String> set = data.keySet();
		int rowIDX = 0;
		int cellIDX = 0;

		Row row = null;
		Cell cell = null;
		row = sheet1.createRow(rowIDX++);
		for (String _title : excelFirstColumnNames) {
			cell = row.createCell(cellIDX++);
			cell.setCellValue(_title);
			cell.setCellStyle(cellStyle);
		}
		cellIDX = 0;
		for (String i : set) {
			String value = data.get(i);
			cellIDX = 0;
			row = sheet1.createRow(rowIDX++);
			cell = row.createCell(cellIDX++);
			cell.setCellValue(i);
			cell.setCellStyle(cellStyle);

			cellStyle = xlsWb.createCellStyle();
			cellStyle.setWrapText(true);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);

			cell = row.createCell(cellIDX++);
			if ("1.0".trim().equals(value)) {
				cellStyle.setDataFormat(format.getFormat("##" + type));
			} else {
				cellStyle.setDataFormat(format.getFormat("0#.##0" + type));
			}
			cell.setCellValue(Double.parseDouble(value));
			cell.setCellStyle(cellStyle);
		}

	}

	/**
	 * �Է��� �������� ������� ������ �����͸� ����. <br>
	 * ù��° ������ Į������ �����Ǿ� �ִ�.<br>
	 * 
	 * @param title
	 *            ��Ʈ��
	 * @param data
	 *            list ���ڿ��� tab ������.
	 */
	public void createExcelSheetForDocumentList(String title, SortedMap<String, LinkedList<String>> data) {
		// Sheet ����
		Sheet sheet1 = xlsWb.createSheet(title);
		CellStyle cellStyle = xlsWb.createCellStyle();
		cellStyle.setWrapText(true);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		Set<String> set = data.keySet();
		int rowIDX = 0;
		int cellIDX = 0;

		Row row = null;
		Cell cell = null;
		row = sheet1.createRow(rowIDX++);
		if (isSCOPUS) {
			String[] clm = new String[] { "EID", "Title", "Publication Year", "Author Keyword","Index Keyword", 
					"Number of Citation", "Country", "Affiliation Name", "Source Title" };
			for (String _c : clm) {
				cell = row.createCell(cellIDX++);
				cell.setCellValue(_c);
				cell.setCellStyle(cellStyle);
			}
		} else {
			String[] clm = new String[] { "����", "�����ȣ", "����⵵", "�������(Ư��û X)" };
			for (String _c : clm) {
				cell = row.createCell(cellIDX++);
				cell.setCellValue(_c);
				cell.setCellStyle(cellStyle);
			}
		}
		cellIDX = 0;
		for (String key : set) {
			LinkedList<String> list = data.get(key);
//			System.out.println(key +"\t" + list.size());
			for (String column : list) {
				row = sheet1.createRow(rowIDX++);
				String[] cls = column.split("\t");
				cellIDX = 0;
				for (String ds : cls) {
					cell = row.createCell(cellIDX++);
					cell.setCellValue(ds.trim());
//					System.out.println(ds.trim());
					// cell.setCellStyle(cellStyle);
				}
//				System.out.println(rowIDX);
			}
		}

	}

	public void writeExcel(String path) {
		// excel ���� ����
		FileOutputStream fileOut = null;
		try {
			File xlsFile = new File(path);
			fileOut = new FileOutputStream(xlsFile);
			xlsWb.write(fileOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) {

	}
}
