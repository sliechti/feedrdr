package feedreader.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class SimpleEncryption {

    public static byte[] encrypt(String key, boolean asBase64, String text) 
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, 
                   IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "Blowfish");
        Cipher ic = Cipher.getInstance("Blowfish");
        ic.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] enc = ic.doFinal(text.getBytes());
        if (asBase64) {
            return new Base64(true).encode(enc);
        }

        return enc;
    }

    public static byte[] decrypt(String key, boolean fromBase64, byte[] text) 
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
                   IllegalBlockSizeException, BadPaddingException {
        byte[] in = (fromBase64) ? new Base64().decode(text) : text;

        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "Blowfish");
        Cipher oc = Cipher.getInstance("Blowfish");
        oc.init(Cipher.DECRYPT_MODE, keySpec);

        return oc.doFinal(in);
    }

}
