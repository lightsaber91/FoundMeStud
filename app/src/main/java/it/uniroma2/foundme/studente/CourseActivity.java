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
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CourseActivity extends Activity {

    private static TextView course, prof, ass1, ass2;
    private static Button msg;
    private static Switch notifica;
    private static ImageButton imgUniroma2;
    private static String Data;
    private static String Title;
    private static String Prof;
    private static String Sid;
    private static Context context;

    private static SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        context = this;

        course = (TextView) findViewById(R.id.tvNameCoures);
        prof = (TextView) findViewById(R.id.tvNameProf);
        ass1 = (TextView) findViewById(R.id.tvNameAss1);
        ass2 = (TextView) findViewById(R.id.tvNameAss2);
        msg = (Button) findViewById(R.id.btnMsg);

        notifica = (Switch) findViewById(R.id.switch_notif);

        imgUniroma2 = (ImageButton) findViewById(R.id.imageButton);

        Bundle passed = getIntent().getExtras();
        Data = passed.getString(Variables_it.COURSE);
        extractData(Data);

        pref = SPEditor.init(CourseActivity.this.getApplicationContext());
        Sid = SPEditor.getID(pref);

        //new getCourseInfo().execute(Title, Prof, Sid);
        try {
            manageInfo(Title, Prof, Sid);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        imgUniroma2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Variables_it.SITE_TV));
                startActivity(browserIntent);
            }
        });
    }

    public void extractData(String data) {
        String[] items = data.split(",");
        Title = items[0];
        Prof = items[1].trim();
    }

    private static void manageInfo(String Title, String Prof, String Sid) throws ExecutionException, InterruptedException {
        new Connection(context, true, Variables_it.INFO, Variables_it.GET_INFO_OK, Variables_it.INFOC)
                .execute(Variables_it.GET_INFO, Variables_it.COURSE, Title, Variables_it.PROF, Prof, Variables_it.ID, Sid);
    }

    private static void manageNotification(String en, String Sid, String Title, String Prof) throws ExecutionException, InterruptedException {
        new Connection(context, true, Variables_it.MODIFYING, Variables_it.NOTIFY_OK, "NULL")
                .execute(Variables_it.NOTIFY, Variables_it.NOTIFICATION, en, Variables_it.ID, Sid, Variables_it.COURSE, Title, Variables_it.PROF, Prof);
    }

    public static void setFields(String cfu, String prof2, String prof3, int n) {
        course.setText(Title + " " + cfu + " CFU");
        prof.setText(Prof);
        ass1.setText(prof2);
        ass2.setText(prof3);

        if (n != 0) notifica.setChecked(true);
        else notifica.setChecked(false);
        notifica.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        manageNotification(Variables_it.ENABLE, Sid, Title, Prof);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        manageNotification(Variables_it.DISABLE, Sid, Title, Prof);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
