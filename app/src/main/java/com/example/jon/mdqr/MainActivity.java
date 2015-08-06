package com.example.jon.mdqr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sun.crypto.provider.SunJCE;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


public class MainActivity extends Activity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void start (View view){
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(MainActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                try {
                    contents = Decrypt(contents);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast toast = Toast.makeText(this, contents, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
    ////
    private static String Encrypt(String raw) throws Exception {
        Cipher c = getCipher(Cipher.ENCRYPT_MODE);

        byte[] encryptedVal = c.doFinal(raw.getBytes("UTF-8"));
        return new BASE64Encoder().encode(encryptedVal);
    }

    private static Cipher getCipher(int mode) throws Exception {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding", new SunJCE());

        //a random Init. Vector. just for testing
        byte[] iv = "e675f725e675f725".getBytes("UTF-8");

        c.init(mode, generateKey(), new IvParameterSpec(iv));
        return c;
    }

    private static String Decrypt(String encrypted) throws Exception {

        byte[] decodedValue = new BASE64Decoder().decodeBuffer(encrypted);

        Cipher c = getCipher(Cipher.DECRYPT_MODE);
        byte[] decValue = c.doFinal(decodedValue);

        return new String(decValue);
    }

    private static Key generateKey() throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        char[] password = "Pass@word1".toCharArray();
        byte[] salt = "S@1tS@lt".getBytes("UTF-8");

        KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
        SecretKey tmp = factory.generateSecret(spec);
        byte[] encoded = tmp.getEncoded();
        return new SecretKeySpec(encoded, "AES");

    }

    //rot
    public static String rot13(String value) {

        char[] values = value.toCharArray();
        for (int i = 0; i < values.length; i++) {
            char letter = values[i];

            if (letter >= 'a' && letter <= 'z') {
                // Rotate lowercase letters.

                if (letter > 'm') {
                    letter -= 17;
                } else {
                    letter += 17;
                }
            } else if (letter >= 'A' && letter <= 'Z') {
                // Rotate uppercase letters.

                if (letter > 'M') {
                    letter -= 17;
                } else {
                    letter += 17;
                }
            }
            values[i] = letter;
        }
        // Convert array to a new String.
        return new String(values);
    }

    public static String rot14(String value) {

        char[] values = value.toCharArray();
        for (int i = 0; i < values.length; i++) {
            char letter = values[i];

            if (letter >= 'a' && letter <= 'z') {
                // Rotate lowercase letters.

                if (letter > 'm') {
                    letter -= 18;
                } else {
                    letter += 18;
                }
            } else if (letter >= 'A' && letter <= 'Z') {
                // Rotate uppercase letters.

                if (letter > 'M') {
                    letter -= 18;
                } else {
                    letter += 18;
                }
            }
            values[i] = letter;
        }
        // Convert array to a new String.
        return new String(values);
    }
    public void test(View view){
        try {
            String data = "KL0iJPvOkDFswTAgkS9zVg==";

            //String encrypt = Encrypt(data);
            //System.out.println(encrypt);

            String decrypt = Decrypt(data);
            System.out.println(decrypt);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


}
