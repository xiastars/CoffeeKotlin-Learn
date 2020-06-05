package com.summer.helper.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {
	
	/**
	 * 序列化对象
	 * 
	 * @throws IOException
	 */

	public static byte[] serializeObject(Object object){
		ByteArrayOutputStream saos = null;
		ObjectOutputStream oos = null;
		try {
			saos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(saos);
			oos.writeObject(object);
			oos.flush();
			return saos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(saos != null)
					saos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 反序列化对象
	 * 
	 * @throws IOException
	 * 
	 * @throws ClassNotFoundException
	 */

	public static Object deserializeObject(byte[] serialize){
		try {
			Object object = null;
			ByteArrayInputStream sais = new ByteArrayInputStream(serialize);
			ObjectInputStream ois = new ObjectInputStream(sais);
			object = ois.readObject();
			return object;
		}  catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
