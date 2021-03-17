package pe.eclipse.neon.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 엔터로 구분된 라인수를 기반으로 파일을 쪼개는 유틸.
 * 
 * @author coreawin
 * @date 2020. 10. 13.
 */
public class SplitRowContents {

	static final int DIVIDE_ROW = 5_000;

	public static void main(String[] args) {

		String path = "f:\\Users\\coreawin\\Documents\\OneDrive - hansung.ac.kr\\21.KISTI\\20.이창환\\20200814";
		String readFile = path + File.separator + "scopus_title.txt";

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(readFile))));) {
			String line = null;

			StringBuffer sb = new StringBuffer();
			int row = 1;
			while ((line = br.readLine()) != null) {

				if (row <= 5000) {
					sb.append(line);
					sb.append("\r\n");
				} else {
					// 초기화
					row = 1;
					writeFile(path + File.separator + "divide" + File.separator, sb.toString());
					sb.setLength(0);
					sb.append(line);
					sb.append("\r\n");
				}
				row += 1;
			}
			writeFile(path + File.separator + "divide" + File.separator, sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public static void writeFile(String path, String contents) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		String writeFile = path + System.nanoTime()+".txt";
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(writeFile))));) {
			bw.write(contents);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
