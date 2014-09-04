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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

import it.uniroma2.foundme.studente.R;

public class ShowMsgActivity extends Activity {

    private static String Course;
    private static String Sid;
    private static String msgId;
    private static String num;
    private static String message;
    private static TextView tvView;
    private static TextView tvNum;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_msg);
        context = this;

        Bundle passed = getIntent().getExtras();
        Course =  passed.getString(Variables_it.COURSE);
        Sid = passed.getString(Variables_it.ID);
        msgId = passed.getString(Variables_it.MID);
        this.setTitle(passed.getString(Variables_it.TITLE));

        tvView = (TextView) findViewById(R.id.tvViews);
        tvNum = (TextView) findViewById(R.id.tvNum);

        try {
            getMsg(Course, Sid, msgId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void getMsg(String Course, String Sid, String msgId) throws ExecutionException, InterruptedException {
        new Connection(context, true, Variables_it.READING, Variables_it.MID, Variables_it.GET)
                .execute(Variables_it.READ_MSG, Variables_it.COURSE, Course, Variables_it.ID, Sid, Variables_it.MID, msgId);
    }

    public static void populateView(String[] result) {
        tvView.setText(result[0]);
        tvNum.setText("Numero visualizzazioni: "+result[1]);
    }
}
