package com.example.jon.mdqr;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {
        private static String algorithm = "AES";
        private static byte[] keyValue =
                new byte[]{'A', 'S', 'e', 'c', 'u', 'r', 'e',
                        'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};

        // Performs Encryption
        public static String encrypt(String plainText) throws Exception {
            Key key = generateKey();
            Cipher chiper = Cipher.getInstance(algorithm);
            chiper.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = chiper.doFinal(plainText.getBytes());
            String encryptedValue = encode(encVal);
            return encryptedValue;
        }

        // Performs decryption
        public static String decrypt(String encryptedText) throws Exception {
    // generate key
            Key key = generateKey();
            Cipher chiper = Cipher.getInstance(algorithm);
            chiper.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = decode(encryptedText);
            byte[] decValue = chiper.doFinal(decordedValue);
            String decryptedValue = new String(decValue);
            return decryptedValue;
        }

        // generateKey() is used to generate a secret key for AES algorithm
        private static Key generateKey() throws Exception {
            Key key = new SecretKeySpec(keyValue, algorithm);
            return key;
        }

    private static String encode(byte[] byteArray) {
        StringBuilder buf = new StringBuilder();
        int intVal = 0;
        String frag = "";
        for (byte b : byteArray) {
            intVal = (int) (0xff & b);
            frag = Integer.toHexString(intVal);
            if (1 == frag.length()) {
                frag = "0" + frag;
            }
            buf.append(frag);
        }
        return buf.toString();
    }

    private static byte[] decode(String textString) {
        byte[] byteArray = new byte[(textString.length() / 2)];
        int intVal = 0;
        String frag = "";
        int c1 = 0;
        for (int i = 0; i < byteArray.length; i++) {
            c1 = (i * 2);
            frag = textString.substring(c1, (c1 + 2));
            intVal = Integer.parseInt(frag, 16);
            byteArray[i] = (byte) (0xff & intVal);
        }
        return byteArray;
    }

    ////////3



}//class