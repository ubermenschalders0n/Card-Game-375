package se375;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class createKey {
	public static void writeToFile(String path, byte[] key) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();

		FileOutputStream fos = new FileOutputStream(f);
		fos.write(key);
		fos.flush();
		fos.close();
	}

	public static void main(String[] args) {

		try {
			KeyGenerator keyGen=KeyGenerator.getInstance("AES");
			SecureRandom secureRandom=new SecureRandom();
			keyGen.init(256,secureRandom);
			SecretKey secretKey=keyGen.generateKey();
			writeToFile("/home/thael/Desktop/SecretKey", secretKey.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static PrivateKey get(String filename)
			  throws Exception {

			    byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

			    PKCS8EncodedKeySpec spec =
			      new PKCS8EncodedKeySpec(keyBytes);
			    KeyFactory kf = KeyFactory.getInstance("AES");
			    return kf.generatePrivate(spec);
			  }
}
