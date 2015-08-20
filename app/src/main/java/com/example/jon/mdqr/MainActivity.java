package com.example.jon.mdqr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
//import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sun.crypto.provider.SunJCE;


import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Arrays;

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


    String[] camArray = { "p1357camera", "p3367vecamera","q6044ecamera","p3034f"};
    String[] swArray = { "n2024pswitch","n3024fswitch","n3024pswitch","s4810switch"};
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

    public void start(View view) {

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
                    //contents = Decrypt(contents);
                    new MyTask().execute(contents);
                } catch (Exception e) {
                    e.printStackTrace();
                }


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



    //backgroud assynctask
    class MyTask extends AsyncTask<String, Void, String> {

        ProgressDialog myPd_ring = null;

        @Override
        protected void onPreExecute() {


            myPd_ring = new ProgressDialog(MainActivity.this);
            myPd_ring.setMessage("Decrypting...");
            myPd_ring.setTitle("Please Wait..");
            myPd_ring.setIcon(R.drawable.guru);
            myPd_ring.setCancelable(false);
            myPd_ring.setCanceledOnTouchOutside(false);
            myPd_ring.show();

        }

        @Override
        protected String doInBackground(String... params) {

            String page = null;
            try {
                page = Decrypt(params[0]);
                //parse out the message

            } catch (Exception e) {
                e.printStackTrace();
            }
            return page;

        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            myPd_ring.dismiss();

            String[] parts = result.split("~");
            String string1 = parts[0];
            String string2 = parts[1];
            Log.d("spilt", string1 + " " + string2);




        if (Arrays.asList(camArray).contains(string2)) {
                Toast.makeText(getApplicationContext(),"Camera Qr Scanned",Toast.LENGTH_SHORT).show();
                String url = "http://maindev.ddns.net:8888/x1/index.php?login=good&cam=" + string1;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

        if (Arrays.asList(swArray).contains(string2)) {
                Toast.makeText(getApplicationContext(),"Switch QR Scanned",Toast.LENGTH_SHORT).show();
                String url = "http://maindev.ddns.net:8888/x1/switch.php?login=good&cam=" + string1+"&partype="+string2;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }




        }

    }

}
