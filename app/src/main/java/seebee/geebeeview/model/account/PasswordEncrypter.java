package seebee.geebeeview.model.account;

import android.util.Base64;
import android.util.Log;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import static android.content.ContentValues.TAG;

/**
 * Created by Joy on 6/28/2017.
 */

public class PasswordEncrypter {

    private SecretKeySpec sks;

    public PasswordEncrypter() {

        // Set up secret key spec for 128-bit AES encryption and decryption
        sks = null;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
            sr.setSeed("any data used as random seed".getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Log.e(TAG, "AES secret key spec error");
        }

    }

    public byte[] encryptPassword(String password) {
        // Encode the original data with AES
        byte[] encodedBytes = null;

        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(password.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "AES encryption error");
        }

        String encryptedText = Base64.encodeToString(encodedBytes, Base64.DEFAULT);

        Log.v("Encrypted Text", encryptedText);

        return encodedBytes;
    }

    public String decodePassword(byte[] encodedBytes) {
        // Decode the encoded data with AES
        byte[] decodedBytes = null;
        //byte[] encodedBytes = Base64.decode(encryptedText, Base64.);

        Log.v("Decode", Base64.encodeToString(encodedBytes, Base64.DEFAULT));

        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, sks);
            decodedBytes = c.doFinal(encodedBytes);
        } catch (Exception e) {
            Log.e(TAG, "AES decryption error");
            e.printStackTrace();
        }

        if(decodedBytes == null) {
            Log.v(TAG, "Decoded bytes are null");
        } else {
            Log.v(TAG, "Decoded bytes are not null");
        }

        String decodedString = new String(decodedBytes);
        Log.v("Decoded String", decodedString);
        //Log.v("Temp String", new String(tempBytes));
        return decodedString;
    }

}
