package net.slashie.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class DESEncrypter {
	private Cipher ecipher;
	private Cipher dcipher;
	private final static BASE64Encoder encoder = new BASE64Encoder();
	private final static BASE64Decoder decoder = new BASE64Decoder();

	private final static byte[] salt = {
		(byte) 0xA9, (byte)0x9b, (byte)0xc8, (byte)0x32,
		(byte) 0x56, (byte)0x35, (byte)0xe3, (byte)0x03
	};
	
	private final static int iterationCount = 19;
	public DESEncrypter(SecretKey key){
		init (key);
	}
	
	private void init(SecretKey key){
		try {
			ecipher = Cipher.getInstance(key.getAlgorithm());
			dcipher = Cipher.getInstance(key.getAlgorithm());
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e){
			e.printStackTrace();
		}
	}
	
	
	public DESEncrypter(String key){
		SecretKey skey;
		try {
			skey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(key.toCharArray(), salt, iterationCount));
			init(skey);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public String encrypt(String str){
		try {
			byte[] utf8 = str.getBytes("UTF8");
			byte[] enc = ecipher.doFinal(utf8);
			return encoder.encode(enc);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String decrypt(String str){

		try {
			byte[] dec = decoder.decodeBuffer(str);
			byte[] utf8 = dcipher.doFinal(dec);
			return new String(utf8, "UTF8");
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static void main(String[] args){
		try {
			System.out.println("Generated DES Key \""+KeyGenerator.getInstance("DES").generateKey()+"\"");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
