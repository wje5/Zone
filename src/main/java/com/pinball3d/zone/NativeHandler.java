package com.pinball3d.zone;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class NativeHandler {
	public static void loadNative(String name) {
		name += "_64.dll";
		String nativeTempDir = System.getProperty("java.io.tmpdir");
		InputStream in = null;
		BufferedInputStream reader = null;
		FileOutputStream writer = null;

		File extractedLibFile = new File(nativeTempDir, name);
		try {
			in = NativeHandler.class.getResourceAsStream(name);
			NativeHandler.class.getResource(name);
			reader = new BufferedInputStream(in);
			writer = new FileOutputStream(extractedLibFile);

			byte[] buffer = new byte[1024];

			while (reader.read(buffer) > 0) {
				writer.write(buffer);
				buffer = new byte[1024];
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		System.load(extractedLibFile.toString());
	}
}
