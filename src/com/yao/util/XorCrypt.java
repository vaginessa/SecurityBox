package com.yao.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class XorCrypt {
	 public static final byte XOR_CONST = 0X12;
	 
	 /**
	     * 异或的一个特点： a^b = c c^b = a
	     * 所以简单点，这里的加解密都用一个函数就行了
	     * @param src
	     * @param dest
	     * @throws Exception
	     */
	    public static void xorEn(File src, File dest) throws Exception {
	        // 文件不存在或为文件夹就不判断了
	        FileInputStream fis = new FileInputStream(src);
	        FileOutputStream fos = new FileOutputStream(dest);
	        byte[] bs = new byte[1024];
	        int len = 0;
	        while ((len = fis.read(bs)) != -1) {
	            for (int i = 0; i < len; i++) {
	                bs[i] ^= XOR_CONST;
	            }
	            fos.write(bs, 0, len);
	        }
	        fos.close();
	        fis.close();
	    }
}
