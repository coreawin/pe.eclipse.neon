package yeo.y201712;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public abstract class FileRW {

	public BufferedReader createReader(File path, String encode) {
		BufferedReader br = null;
		if (encode == null) {
			encode = "UTF-8";
		}
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), encode));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return br;
	}

	public void close(Reader r) {
		if (r != null) {
			try {
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close(Writer r) {
		if (r != null) {
			try {
				r.flush();
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public abstract void readline(BufferedReader br);

	public abstract void writerline(BufferedWriter bw);

	public BufferedWriter createWriter(File path, String encode) {
		BufferedWriter bw = null;
		if (encode == null) {
			encode = "UTF-8";
		}
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), encode));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bw;
	}

}
