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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import it.uniroma2.foundme.studente.R;

public class ShowMsgActivity extends Activity {

    private WebView wVmsg;

    private static String Url;
    private static String Title;
    private static String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_show_msg);

        setProgressBarVisibility(true);
        setProgressBarIndeterminateVisibility(true);

        wVmsg = (WebView) findViewById(R.id.wVmsg);

        final Bundle passed = getIntent().getExtras();
        Url =  passed.getString(Variables_it.URL);
        Title = passed.getString(Variables_it.COURSE);

        uri = Variables_it.MSG_ROOT + Title + "/" + Url;

        wVmsg.getSettings().setJavaScriptEnabled(true);
        final Activity activity = this;
        wVmsg.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 100);
                if(progress == 100) {
                    setProgressBarIndeterminateVisibility(false);
                    setProgressBarVisibility(false);
                    activity.setTitle(passed.getString(Variables_it.MSG));
                }
            }
        });
        wVmsg.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, Variables_it.ERROR, Toast.LENGTH_SHORT).show();
            }
        });

        wVmsg.loadUrl(uri);
    }

}
