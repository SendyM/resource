package com.meilitech.zhongyi.resource;

import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RedisTest {
	public static byte [] getBucketId(byte [] key, Integer bit) throws NoSuchAlgorithmException {

		MessageDigest mdInst = MessageDigest.getInstance("MD5");

		mdInst.update(key);

		byte [] md = mdInst.digest();

		byte [] r = new byte[(bit-1)/7 + 1];// 因为一个字节中只有7位能够表示成单字符

		int a = (int) Math.pow(2, bit%7)-2;

		md[r.length-1] = (byte) (md[r.length-1] & a);

		System.arraycopy(md, 0, r, 0, r.length);

		for(int i=0;i<r.length;i++) {

			if(r[i]<0) r[i] &= 127;

		}

		return r;

	}

	@Test
	public void connect(){
		String  str = "123";
		byte[] sb = str.getBytes();
		try {
		sb =	getBucketId(sb,33);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		System.out.println( new String( sb ));
	}
}