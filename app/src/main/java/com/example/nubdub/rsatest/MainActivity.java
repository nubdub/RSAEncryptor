package com.example.nubdub.rsatest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;

import org.w3c.dom.Text;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;



import javax.crypto.Cipher;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Button send;
    private Button clear;
    private Button decrypt;
    private Button ok;
    private Button sendKey;
    private EditText inputMessage;
    private EditText outputEncryption;
    private EditText phoneNum;
    private EditText privKeyText;
    private EditText pubKeyText;
    private TextView outputDecryption;
    private String input;
    private String encryptedMessage;
    private String phone;
    private byte[] encrypted;
    private byte[] decrypted;
    private PublicKey pubKey;
    private static final int REQUEST_PERMISSION = 9000;
    private String encodedPubKey;
    private String encodedPrivKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send = findViewById(R.id.send);
        clear = findViewById(R.id.clear);
        decrypt = findViewById(R.id.buttonDecrypt);
        ok = findViewById(R.id.ok);
        sendKey = findViewById(R.id.sendKey);

        inputMessage = findViewById(R.id.editText2);
        outputEncryption = findViewById(R.id.editText);
        outputDecryption = findViewById(R.id.decryptout);
        phoneNum = findViewById(R.id.phoneNum);
        privKeyText = findViewById(R.id.privKey);
        pubKeyText = findViewById(R.id.pubKey);

        inputMessage.setFocusableInTouchMode(true);
        inputMessage.requestFocus();

        outputDecryption.setMovementMethod(new ScrollingMovementMethod());

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                send.setEnabled(false);
                input = inputMessage.getText().toString();
                inputMessage.setEnabled(false);
                try {
                    KeyPair keyPair = RSA.buildKeyPair();
                    pubKey = keyPair.getPublic();
                    PrivateKey privateKey = keyPair.getPrivate();

                    // Encrypt the message
                    encrypted = RSA.encrypt(privateKey, input);
                    encryptedMessage = new String(encrypted);
                    encodedPubKey = Base64.getEncoder().encodeToString(pubKey.getEncoded());
                    outputEncryption.setText(encryptedMessage + encodedPubKey);
                    runCode(v);
                } catch (Exception e) {
                    // Popup message
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                send.setEnabled(true);
                ok.setEnabled(true);
                phoneNum.setEnabled(true);
                inputMessage.setEnabled(true);
                inputMessage.setText("");
                outputEncryption.setText("");
                input = "";
                outputDecryption.setText("");
            }
        });
        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    decrypted = RSA.decrypt(pubKey, encrypted);
                    // outputDecryption.setText(new String(decrypted));
                    outputDecryption.setText(new String(decrypted));
                    //outputDecryption.setText(pubKey.toString());
                } catch (Exception e) {
                    // Popup message
                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ok.setEnabled(false);
                phone = phoneNum.getText().toString();
                while (phone.contains("-")) {
                    phone = phone.replace("-", "");
                }
                phoneNum.setEnabled(false);
            }
        });
        sendKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    KeyPair keyPair = RSA.buildKeyPair();
                    pubKey = keyPair.getPublic();
                    PrivateKey privateKey = keyPair.getPrivate();

                    encodedPubKey = Base64.getEncoder().encodeToString(pubKey.getEncoded());
                    encodedPrivKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

                    pubKeyText.setText(encodedPubKey);
                    privKeyText.setText(encodedPrivKey);

                    sendPubKey(view);

                }
                catch (Exception e) {

                }
            }
        });
        checkPermissions();
    }

    /**
     * Asks the user for SMS permission
     *
     * @return true if the user hits "yes"
     */
    private boolean checkPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // Will figure this out later :)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_PERMISSION);
            return false;
        }
    }


    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                grantResults[0] = PackageManager.PERMISSION_GRANTED;
            }
        } else {
            //
        }
    }

    public void runCode(View view) {
        if (checkPermissions()) {
            encodedPubKey = Base64.getEncoder().encodeToString(pubKey.getEncoded());

            SmsManager manager = SmsManager.getDefault();
            ArrayList<String> list = manager.divideMessage(pubKey.toString()/*+"HELLOWORLD"+encodedKey*/);
            manager.sendMultipartTextMessage(phone, null, list, null, null);
//            manager.sendTextMessage("7572142613", null, encryptedMessage, null, null);
        }
    }

    public void sendPubKey(View view) {
        if (checkPermissions()) {

            SmsManager manager = SmsManager.getDefault();
            ArrayList<String> list = manager.divideMessage(pubKey.toString());
            manager.sendMultipartTextMessage(phone, null, list, null, null);
//            manager.sendTextMessage("7572142613", null, encryptedMessage, null, null);
        }
    }


}
