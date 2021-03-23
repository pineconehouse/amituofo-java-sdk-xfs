package com.amituofo.xfs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectUtils {

	public static void writeObject(File file, Object obj) throws IOException {
		FileOutputStream o = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(o);
		out.writeObject(obj);
		out.flush();
		out.close();
	}

	/**
	 * Write object to file
	 * 
	 * @author hansong
	 * @param filepath
	 * @param obj
	 * @throws IOException
	 */
	public static void writeObject(String filepath, Object obj) throws IOException {
		writeObject(new File(filepath), obj);
	}

	public static Object readObject(File file) throws IOException, ClassNotFoundException {
		if (!file.exists()) {
			return null;
		}
		FileInputStream i = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(i);
		Object obj = in.readObject();
		in.close();
		return obj;
	}

	/**
	 * 
	 * @author hansong
	 * @param filepath
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object readObject(String filepath) throws IOException, ClassNotFoundException {
		return readObject(new File(filepath));
	}

}
