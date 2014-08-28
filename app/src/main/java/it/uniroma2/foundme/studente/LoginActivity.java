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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends Activity {

    public static SharedPreferences pref;

    public static String user = null;
    public static String pass = null;
    private TextView tvRegistration;
    private EditText etUser;
    private EditText etPpass;
    private Button btAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GetSharedPref();

        setContentView(R.layout.activity_login);

        btAccess = (Button) findViewById(R.id.btAccess);
        etUser = (EditText) findViewById(R.id.etUserName);
        etPpass = (EditText) findViewById(R.id.etPassword);
        tvRegistration = (TextView) findViewById(R.id.tvRegistration);

        tvRegistration.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent goToRegistration = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(goToRegistration);
            }
        });

        btAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                user = etUser.getText().toString();
                pass = etPpass.getText().toString();
                pass = computeSHAHash.sha1(pass);
                //new Login().execute(user, pass);
                try {
                    manageLogin(user, pass);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void GetSharedPref() {
        pref = SPEditor.init(LoginActivity.this.getApplicationContext());
        user = SPEditor.getUser(pref);
        pass = SPEditor.getPass(pref);
        if (user == null || user.isEmpty() || pass == null || pass.isEmpty()) {
            return;
        }
        //new Login().execute(user, pass);
        try {
            manageLogin(user, pass);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void manageLogin(String user, String pass) throws ExecutionException, InterruptedException {
        new Connection(this, true, Variables_it.LOGGING_IN, Variables_it.NAME, Variables_it.LOG)
                .execute(Variables_it.LOGIN, Variables_it.MAIL, user, Variables_it.PASS, pass);
    }

    public static String getuser() {
        return user;
    }

    public static String getpass() {
        return pass;
    }
}
