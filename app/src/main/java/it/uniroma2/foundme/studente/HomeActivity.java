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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends Activity {

    private String name = null;
    private TextView studName;
    private String studSid;
    private Button aggiungi;
    private Button rimuovi;
    private Button visualizza;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        studName = (TextView) findViewById(R.id.tvName);
        aggiungi = (Button) findViewById(R.id.btnAdd);
        rimuovi = (Button) findViewById(R.id.btnCanc);
        visualizza = (Button) findViewById(R.id.btnVisualizza);


        Bundle passed = getIntent().getExtras();
        name = passed.getString(Variables_it.NAME);
        studSid = passed.getString(Variables_it.ID);
        studName.setText(" "+name);

        aggiungi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent i = new Intent(HomeActivity.this, FollowCourseActivity.class);
                i.putExtra(Variables_it.ID, studSid);
                startActivity(i);
            }
        });

        rimuovi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent i = new Intent(HomeActivity.this, UnFollowCourseActivity.class);
                i.putExtra(Variables_it.ID, studSid);
                startActivity(i);
            }
        });

        visualizza.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent i = new Intent(HomeActivity.this, ShowCourseActivity.class);
                i.putExtra(Variables_it.ID, studSid);
                startActivity(i);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);
        getMenuInflater().inflate(R.menu.home, menu);
        MenuInflater Inflater = getMenuInflater();
        Inflater.inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.logout:
                pref = SPEditor.init(HomeActivity.this.getApplicationContext());
                SPEditor.delete(pref);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_settings:
                Intent intent2 = new Intent(this, SettingsActivity.class);
                startActivity(intent2);
                return true;
            case R.id.info:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}