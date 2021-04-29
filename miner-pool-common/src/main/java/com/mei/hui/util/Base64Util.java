package com.mei.hui.util;

import org.apache.commons.codec.binary.Base64;

/**
 * Description: base64 加密解密
 * @author baohongjian
 * @version 1.0
 * <pre>
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2017年7月28日    bao       1.0        1.0 Version 
 * </pre>
 */
public class Base64Util {


	/**
	 * 
	* @Description: TODO(base64 编码) 
	* @param @param plainText 明文
	* @param @return    
	* @return String    返回类型 
	* @throws
	 */
	public static String encode(byte[] data){
		return Base64.encodeBase64String(data);
	}  

	/**
	 * 
	* @Description: TODO(base64解码) 
	* @param @param encryptText 加密数据
	* @param @return    
	* @return String    返回类型 
	* @throws
	 */
	public static byte[] decode(String data){
		return Base64.decodeBase64(data);
	}  

/*	public static void main(String[] args)  
	{  

		String testStr = "{\"name\":\"vicken\",\"age\":20}";  
		System.out.println("加密前：" + testStr);  

		String encodeStr = Base64Util.encode(testStr);  
		System.out.println("加密数据：" + encodeStr);  

		String decodeStr = Base64Util.decode(encodeStr);  
		System.out.println("解密数据：" + decodeStr);  
	}  */


}
