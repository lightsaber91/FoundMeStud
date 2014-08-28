/*
 * Copyright (C) 2014 - Simone Martucci <martucci.simone.91@gmail.com>
 * Copyright (C) 2014 - Mattia Mancini <mattia.mancini.1991@gmail.com>
 *
 * This file is part of Foundme Studente.
 *
 * Foundme Studente is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foundme Studente is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foundme Studente.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.uniroma2.foundme.studente;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RegistrationActivity extends Activity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String TAG = "GCMRelated";
    private static GoogleCloudMessaging gcm;
    private static String regid;
    private static String SENDER_ID = "1006614533598";

    private static Button btSignUp;

    private static EditText etNewUser;
    private static EditText etNewPass1;
    private static EditText etNewPass2;
    private static EditText etNewMail;
    private static EditText etNewMail2;
    private static EditText etNewId;

    private static String NewUser = null;
    private static String NewPass1 = null;
    private static String NewPass2 = null;
    private static String NewMail = null;
    private static String NewMail2 = null;
    private static String NewId = null;
    private static Context context;

    private static SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        context = this;
        btSignUp = (Button) findViewById(R.id.btSignUp);

        etNewUser = (EditText) findViewById(R.id.etNewUser);
        etNewPass1 = (EditText) findViewById(R.id.etNewPass);
        etNewPass2 = (EditText) findViewById(R.id.etNewPass2);
        etNewMail = (EditText) findViewById(R.id.etNewMail);
        etNewMail2 = (EditText) findViewById(R.id.etNewMail2);
        etNewId = (EditText) findViewById(R.id.etNewId);

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                NewUser = etNewUser.getText().toString();
                NewPass1 = etNewPass1.getText().toString();
                NewPass2 = etNewPass2.getText().toString();
                NewMail = etNewMail.getText().toString();
                NewMail2 = etNewMail2.getText().toString();
                NewId = etNewId.getText().toString();

                if (checkPlayServices()) {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    if (!checkLoginData(NewPass1, NewPass2, NewMail, NewMail2)) {
                        Toast.makeText(getApplicationContext(), Variables_it.FILL_FIELD, Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            manageRegistration(NewUser, NewPass1, NewMail, NewId);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public static boolean GoogleCloudRegistration() {
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            regid = gcm.register(SENDER_ID);
            pref = SPEditor.init(context);
            SPEditor.setRegID(pref, regid);
            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.

            // For this demo: we don't need to send it because the device
            // will send upstream messages to a server that echo back the
            // message using the 'from' address in the message.

            // Persist the regID - no need to register again.
        } catch (IOException ex) {
            return false;
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return true;
    }

    private void manageRegistration(String name, String pass, String mail, String id) throws ExecutionException, InterruptedException {
        pass = computeSHAHash.sha1(pass);
        new Connection(this, true, Variables_it.SIGN_UP, Variables_it.SIGN_UP_OK, Variables_it.REGIS)
                .execute(Variables_it.REGISTRATION, Variables_it.NAME, name, Variables_it.PASS, pass, Variables_it.MAIL, mail, Variables_it.ID, id, Variables_it.GCMKEY, regid);
    }

    boolean checkLoginData(String pass1, String pass2, String mail1, String mail2) {
        if (pass1.equals(pass2) && mail1.equals(mail2)) {
            int l = pass1.length();
            return l >= 8;
        }
        return false;
    }
    public static String getmail() {
        return NewMail;
    }

    public static String getpass() {
        return NewPass1;
    }
}
