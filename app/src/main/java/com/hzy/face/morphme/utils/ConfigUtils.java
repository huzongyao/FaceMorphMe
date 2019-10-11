package com.hzy.face.morphme.utils;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;

import com.blankj.utilcode.util.Utils;
import com.hzy.face.morphme.R;

public class ConfigUtils {

    private static SharedPreferences mPref;

    private static SharedPreferences getPrefrence() {
        if (mPref == null) {
            mPref = PreferenceManager.getDefaultSharedPreferences(Utils.getApp());
        }
        return mPref;
    }

    /**
     * get the image resolution config
     *
     * @return a point with x and y means with and height
     */
    public static Point getConfigResolution() {
        try {
            SharedPreferences pref = getPrefrence();
            String key = Utils.getApp().getString(R.string.pref_resolution_key);
            String[] resolutions = Utils.getApp().getResources().getStringArray(R.array.pref_resolution_array);
            int index = Integer.parseInt(pref.getString(key, "0"));
            String[] result = resolutions[index].split("x");
            return new Point(Integer.parseInt(result[0]), Integer.parseInt(result[1]));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * get the transition frame count
     *
     * @return int value
     */
    public static int getConfigFrameCount() {
        try {
            SharedPreferences pref = getPrefrence();
            String key = Utils.getApp().getString(R.string.pref_frames_key);
            String[] resolutions = Utils.getApp().getResources().getStringArray(R.array.pref_frames_array);
            int index = Integer.parseInt(pref.getString(key, "0"));
            return Integer.parseInt(resolutions[index]);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * get transformation duration
     *
     * @return duration
     */
    public static int getConfigDuration() {
        try {
            SharedPreferences pref = getPrefrence();
            String key = Utils.getApp().getString(R.string.pref_delay_key);
            return Integer.parseInt(pref.getString(key, "800"));
        } catch (Exception e) {
            return 800;
        }
    }
}
