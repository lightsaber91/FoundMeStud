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

/**
 * Created by simone on 02/06/2014.
 */
public class DelAccountActivity extends Activity {

    private EditText etMail;
    private EditText etPsw;
    private Button btConfirm;
    private String mail = null;
    private String pass = null;
    private SharedPreferences pref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del_account);

        etMail = (EditText) findViewById(R.id.etMail);
        etPsw = (EditText) findViewById(R.id.etPsw);
        btConfirm = (Button) findViewById(R.id.btConfirm);

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mail = etMail.getText().toString();
                pass = etPsw.getText().toString();

                if (!checkData(mail, pass)) {
                    Toast.makeText(getApplicationContext(), Variables_it.FILL_FIELD, Toast.LENGTH_LONG).show();
                } else {
                    pass = computeSHAHash.sha1(pass);
                    try {
                        manageCourse(mail, pass);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void manageCourse(String mail, String pass) throws ExecutionException, InterruptedException {
        new Connection(this, true, Variables_it.DELETION, Variables_it.DEL_ACCOUNT_OK, Variables_it.DELACC)
                .execute(Variables_it.DEL_ACCOUNT, Variables_it.MAIL, mail, Variables_it.PASS, pass);
    }


    private boolean checkData(String mail, String pass) {
        return !(mail == null || pass == null || mail.isEmpty() || mail.equalsIgnoreCase(SPEditor.getUser(SPEditor.init(this))) || pass.isEmpty() || pass.length() < 8);
    }

}
