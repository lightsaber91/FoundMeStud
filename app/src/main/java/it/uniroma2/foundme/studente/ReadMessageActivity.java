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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;


public class ReadMessageActivity extends Activity {

    private static ListView lvMessaggi;
    private static String Title;
    private static String[] messages = null;
    private static Context context;
    private static SwipeRefreshLayout swipeMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_message);
        context = this;

        Bundle passed = getIntent().getExtras();
        Title =  passed.getString(Variables_it.COURSE);

        swipeMsg = (SwipeRefreshLayout) findViewById(R.id.swipe_msg);
        swipeMsg.setEnabled(false);
        lvMessaggi = (ListView) findViewById(R.id.lvmes);

        try {
            getMsg(Title, true);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void getMsg(String Title, boolean en) throws ExecutionException, InterruptedException {
        new Connection(context, en, Variables_it.READING, Variables_it.MSG, Variables_it.GET)
                .execute(Variables_it.SHOW_MSG, Variables_it.COURSE, Title);
    }

    private static String extractData(String msg) {
        String[] items = msg.split("_");
        return "["+items[0]+"] "+items[1];
    }

    public static void populateView(String[] result){
        messages = new String[result.length];
        System.arraycopy(result, 0, messages, 0, result.length);
        final ArrayList<String> listp = new ArrayList<String>();

        for(int i=0; i<result.length; i++) {
            listp.add(extractData(messages[i]));
        }
        //creo l'adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listp);
        //inserisco i dati
        lvMessaggi.setAdapter(adapter);

        swipeMsg.setColorSchemeColors(0xff429874, 0xffffffff, 0xff429874, 0xffffffff);
        swipeMsg.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeMsg.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeMsg.setRefreshing(false);
                        try {
                            getMsg(Title, false);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, 3000);
            }
        });

        lvMessaggi.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!messages[position].equalsIgnoreCase(Variables_it.NO_MSG)) {
                    Intent i = new Intent(context, ShowMsgActivity.class);
                    i.putExtra(Variables_it.COURSE,Title);
                    i.putExtra(Variables_it.URL, messages[position]);
                    i.putExtra(Variables_it.MSG, listp.get(position));
                    context.startActivity(i);
                }
            }
        });

        lvMessaggi.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeMsg.setEnabled(true);
                else
                    swipeMsg.setEnabled(false);
            }
        });
    }
}
