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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UnFollowCourseActivity extends Activity {

    private static ListView lvCorsiSeguiti;
    private static String Sid = null;
    private static String title = null, prof = null;
    private static String[] courses = null;
    private static SwipeRefreshLayout swipeUnfollow;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unfollow_course);
        context = this;
        lvCorsiSeguiti = (ListView) findViewById(R.id.lvCorsiSeguiti);

        swipeUnfollow = (SwipeRefreshLayout) findViewById(R.id.swipe_unfollow);
        swipeUnfollow.setEnabled(false);

        Bundle passed = getIntent().getExtras();
        Sid = passed.getString(Variables_it.ID);

        try {
            getCourse(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void getCourse(boolean en) throws ExecutionException, InterruptedException {
        new Connection(context, en, Variables_it.LOADING, Variables_it.UNFOLLOW, Variables_it.GET)
                .execute(Variables_it.SHOW_COURSE, Variables_it.ID, Sid);
    }

    public static void populateView(String[] result) {
        courses = new String[result.length];
        System.arraycopy(result, 0, courses, 0, result.length);
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        for(int i=0; i<result.length; i++) {
            Map<String, String> datum = new HashMap<String, String>(2);
            if (i == 0 && result[0].equalsIgnoreCase(Variables_it.NO_COURSE)) {
                datum.put(Variables_it.COURSE, result[0]);
                datum.put(Variables_it.PROF, "");
                data.add(datum);
                break;
            }
            else {
                String[] items = result[i].split(",");
                datum.put(Variables_it.COURSE, items[0]);
                datum.put(Variables_it.PROF, items[1]);
                data.add(datum);
            }
        }
        //creo l'adapter
        SimpleAdapter adapter = new SimpleAdapter(context, data, android.R.layout.simple_list_item_2, new String[] {Variables_it.COURSE,Variables_it.PROF}, new int[] {android.R.id.text1,
                android.R.id.text2});
        lvCorsiSeguiti.setAdapter(adapter);

        swipeUnfollow.setColorSchemeColors(0xff429874, 0xffffffff, 0xff429874, 0xffffffff);
        swipeUnfollow.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeUnfollow.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getCourse(false);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        swipeUnfollow.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        lvCorsiSeguiti.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!courses[position].equalsIgnoreCase(Variables_it.NO_COURSE))
                    alertMessage(courses[position]);
            }
        });

        lvCorsiSeguiti.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeUnfollow.setEnabled(true);
                else
                    swipeUnfollow.setEnabled(false);
            }
        });
    }

    public static void extractData(String data) {
        String[] items = data.split(",");
        title = items[0];
        prof = items[1];
    }

    public static void alertMessage(final String info) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        extractData(info);
                        //new deleteCourse().execute(title, prof, Sid)
                        try {
                            managefollowing(title, prof, Sid);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(Variables_it.UNFOLLOW_REQUEST)
                .setNegativeButton(Variables_it.NO, dialogClickListener)
                .setPositiveButton(Variables_it.YES, dialogClickListener)
                .show();
    }

    private static void  managefollowing(String title, String prof, String sid) throws ExecutionException, InterruptedException {
        new Connection(context, true, Variables_it.DELETION, Variables_it.DEL_COURSE_OK, Variables_it.UNFOLLOW)
                .execute(Variables_it.DEL_COURSE, Variables_it.COURSE, title, Variables_it.PROF, prof, Variables_it.ID, sid);
    }
}
