/**
 * Property of Sam Mathias Weggersen
 * sam.mathias.weggersen@gmail.com
 *
 * Copyright 2014 Sam Mathias Weggersen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package sintef.android.controller.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PreferencesHelper {

    private static Context context;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static final int INVALID_INT = -1;
    public static final long INVALID_LONG = -1L;
    public static final String INVALID_STRING = "";

    public enum DataTypes {
        INT, STRING, BOOLEAN, LONG, FLOAT, SET_STRING;
    }

    public static final String FALL_DETECTION_ENABLED = "fall_detection_enabled";
    public static final String RECORDING_ENABLED = "recording_enabled";

    public static boolean isFallDetectionEnabled() {
        return PreferencesHelper.getBoolean(FALL_DETECTION_ENABLED, true);
    }

    @SuppressLint("CommitPrefEdits")
    public static void initializePreferences(Context c) {
        if (context == null) {
            context = c;
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            editor = preferences.edit();
        }
    }

    private static Context getContext() {
        return context;
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static SharedPreferences.Editor getEdit() {
        return editor;
    }

    public static Map<String, ?> getAllPreferences() {
        return preferences.getAll();
    }

    public static boolean contanis(String key) {
        return preferences.contains(key);
    }

    public static DataTypes datatype(String key) {
        Map<String, ?> allPreferences = getAllPreferences();
        if (allPreferences != null)
            for (Map.Entry<String, ?> entry : allPreferences.entrySet()) {
                if (key.equals(entry.getKey())) {
                    Object o = entry.getValue();
                    if (o instanceof Integer)
                        return DataTypes.INT;
                    else if (o instanceof String)
                        return DataTypes.STRING;
                    else if (o instanceof Boolean)
                        return DataTypes.BOOLEAN;
                    else if (o instanceof Long)
                        return DataTypes.LONG;
                    else if (o instanceof Float)
                        return DataTypes.FLOAT;
                    else if (o instanceof Set)
                        return DataTypes.SET_STRING;
                }
            }
        return null;
    }

    public static void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public static void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public static void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public static void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void putStringSet(String key, Set<String> value) {
        editor.putStringSet(key, value);
        editor.commit();
    }

    public static int getInt(String key) {
        return getInt(key, INVALID_INT);
    }

    public static int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public static String getString(String key) {
        return preferences.getString(key, INVALID_STRING);
    }

    public static String getString(String key, String def_value) {
        return preferences.getString(key, def_value);
    }

    public static boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    public static long getLong(String key) {
        return preferences.getLong(key, INVALID_LONG);
    }

    public static long getLong(String key, long defValue) {
        return preferences.getLong(key, defValue);
    }

    public static float getFloat(String key) {
        return preferences.getFloat(key, INVALID_INT);
    }

    public static float getFloat(String key, float defValue) {
        return preferences.getFloat(key, defValue);
    }

    public static Set<String> getStringSet(String key) {
        return preferences.getStringSet(key, null);
    }

    public static void setStringArrayPref(final String key, final List<String> values) {
        final JSONArray a = new JSONArray();
        for (String value : values)
            a.put(value);
        editor.putString(key, !values.isEmpty() ? a.toString() : null);
        editor.commit();
    }

    public static List<String> getStringArrayPref(final String key) {
        if (preferences == null) return new ArrayList<>();
        final String json = preferences.getString(key, null);
        final ArrayList<String> stored_array = new ArrayList<String>();
        if (json != null) {
            try {
                final JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++)
                    stored_array.add(a.optString(i));
            } catch (final JSONException e) {
                e.printStackTrace();
            }
        }
        return stored_array;
    }

    public static void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener l) {
        preferences.registerOnSharedPreferenceChangeListener(l);
    }

    public static void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener l) {
        preferences.unregisterOnSharedPreferenceChangeListener(l);
    }

    public static boolean isRecording() {
        return getBoolean(RECORDING_ENABLED, false);
    }
}
