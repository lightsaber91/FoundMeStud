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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ShowCourseActivity extends Activity {

    private static ListView lvCorsiSeguiti;
    private static String Sid = null;
    private static String[] courses = null;
    private static SwipeRefreshLayout swipeShow;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_courses);
        context = this;
        swipeShow = (SwipeRefreshLayout) findViewById(R.id.swipe_show);
        swipeShow.setEnabled(false);

        lvCorsiSeguiti = (ListView) findViewById(R.id.lvCorsiSeguiti);

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

    private static void getCourse(boolean en) throws ExecutionException, InterruptedException {
        new Connection(context, en, Variables_it.LOADING, Variables_it.SHOW, Variables_it.GET)
                .execute(Variables_it.SHOW_COURSE, Variables_it.ID, Sid);
    }

    public static void populateView(String[] result) {
        courses = new String[result.length];
        System.arraycopy(result, 0, courses, 0, result.length);
        ArrayList<String> listp = new ArrayList<String>();

        Collections.addAll(listp, courses);
        //creo l'adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listp);
        //inserisco i dati
        lvCorsiSeguiti.setAdapter(adapter);

        swipeShow.setColorSchemeColors(0xff429874, 0xffffffff, 0xff429874, 0xffffffff);
        swipeShow.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeShow.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeShow.setRefreshing(false);
                        try {
                            getCourse(false);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, 3000);
            }
        });

        lvCorsiSeguiti.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!courses[position].equalsIgnoreCase(Variables_it.NO_COURSE)) {
                    Intent i = new Intent(context, CourseActivity.class);
                    i.putExtra(Variables_it.COURSE, courses[position]);
                    ((Activity) context).startActivity(i);
                }
            }
        });

        lvCorsiSeguiti.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeShow.setEnabled(true);
                else
                    swipeShow.setEnabled(false);
            }
        });
    }
}
