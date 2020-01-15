package com.italia.municipality.lakesebu.security;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import com.italia.municipality.lakesebu.enm.AppConf;
/**
 * 
 * @author mark italia
 * @since 11/16/2016
 * @version 1.0
 * this class is use for encoding and decoding of character
 */
public class SecureChar {

	public static String encode(String val){
		
		try{
		// encode with padding
		String encoded = Base64.getEncoder().encodeToString(val.getBytes(AppConf.SECURITY_ENCRYPTION_FORMAT.getValue()));
		// encode without padding
		//String encoded = Base64.getEncoder().withoutPadding().encodeToString(val.getBytes(Ipos.SECURITY_ENCRYPTION_FORMAT.getName()));
		
		return encoded;
		}catch(Exception e){}
		return null;
	}
	public static String decode(String val){
		try{
			byte [] barr = Base64.getDecoder().decode(val);
			return new String(barr,AppConf.SECURITY_ENCRYPTION_FORMAT.getValue());
			}catch(Exception e){}
			return null;
	}
	
	
	public static void main(String[] args) {
		String val = SecureChar.encode("taxation");
		System.out.println("Encode: " + val);
		System.out.println("Decode: " + SecureChar.decode("02r03i-00m01a-02r00m01a08i"));
	}
	
}

