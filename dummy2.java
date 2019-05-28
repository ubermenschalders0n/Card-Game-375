package dummy;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class dummy2 {

	public static String printBytes(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(String.format("%02X:", b));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		String msg = "This is SE375 SYSTEM PROGRAMMING.";
		byte[] plain_text;
		byte[] encrypted_text;
		byte[] decrypted_text;
		Cipher cipher;

		try {
			plain_text = msg.getBytes("UTF-8");
			System.out.println("Original data:" + msg);
			System.out.println(printBytes(plain_text));

			// STEP 1. Generate the Keys
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = new SecureRandom();
			keyGen.init(256, secureRandom);
			SecretKey secretKey = keyGen.generateKey();

			// length of the key must be 128 , 192 or 256 bits (16, 24, 32 byte)
//			byte[] key = "!'^+Pass@4meword".getBytes("UTF-8");
	//		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

			// STEP 2. Get a Cipher for the desired transformation
			cipher = Cipher.getInstance("AES");

			// Step 3. Choose a method
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			// STEP 4. Perform the operation
			encrypted_text = cipher.doFinal(plain_text);
			System.out.println("Encrypted data:" + printBytes(encrypted_text));

			// STEP 5. Undo the operation
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			decrypted_text = cipher.doFinal(encrypted_text);
			System.out.println("Decrypted data:" + printBytes(decrypted_text));

			if (java.util.Arrays.equals(decrypted_text, plain_text)) {
				System.out.println("Obtained the original text: " + new String(decrypted_text));
			}
		} catch (UnsupportedEncodingException ex) {
			System.err.println("Couldn't create key: " + ex.getMessage());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.err.println(e.getMessage());
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			System.err.println(e.getMessage());
		}
	}
}
