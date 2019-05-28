package se375;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class encryption {
	public static byte[] encryptKey(String password) throws Exception {

		KeyGenerator keyGen=KeyGenerator.getInstance("AES");
		keyGen.init(256);
		SecretKey secretKey=keyGen.generateKey();
		Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte [] encrypted=password.getBytes("UTF-8");
		return cipher.doFinal(encrypted);
		
		/*PrivateKey secretKey = get("/home/thael/Desktop/SecretKey");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encrypted = password.getBytes("UTF-8");
		return cipher.doFinal(encrypted); */
	}

	public static PrivateKey get(String filename) throws Exception {
		byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("AES");
		return kf.generatePrivate(spec);
	}

	public static String decryptedKey(byte[] encrypted) throws Exception {
		PrivateKey secretKey = get("/home/thael/Desktop/SecretKey");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decrypted_text = cipher.doFinal(encrypted);
		return new String(decrypted_text);
	}
}
