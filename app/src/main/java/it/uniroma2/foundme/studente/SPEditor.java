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
import android.content.SharedPreferences;

/**
 * Created by simone on 25/08/2014.
 */
public class SPEditor extends Activity {

    private static final String PREF = "StudentPref";
    private static final String USER = "UserMail";
    private static final String PASS = "CryptedPassword";
    private static final String GCM = "GcmKey";
    private static final String ID = "UserID";
    private static final String RID = "RegistrationID";

    public static SharedPreferences init(Context ctx) {
        return ctx.getSharedPreferences(PREF, MODE_PRIVATE);
    }

    public static void delete(SharedPreferences p) {
        p.edit().clear().apply();
    }

    public static String getUser(SharedPreferences p) {
        return p.getString(USER, null);
    }

    public static String getPass(SharedPreferences p) {
        return p.getString(PASS, null);
    }

    public static String getID(SharedPreferences p) {
        return p.getString(ID, null);
    }

    public static void setUser(SharedPreferences p, String s) {
        p.edit().putString(USER, s).apply();
    }

    public static void setPass(SharedPreferences p, String s) {
        p.edit().putString(PASS, s).apply();
    }

    public static void setID(SharedPreferences p, String s) {
        p.edit().putString(ID, s).apply();
    }

    public static void setGCM(SharedPreferences p, String s) {
        p.edit().putString(GCM, s).apply();
    }

}
