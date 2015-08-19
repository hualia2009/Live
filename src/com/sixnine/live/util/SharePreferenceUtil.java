package com.sixnine.live.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {

    private static SharePreferenceUtil util;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static SharePreferenceUtil getInstance(Context context) {
        if (null == util) {
            util = new SharePreferenceUtil(context);
        }
        return util;
    }

    private SharePreferenceUtil(Context context) {
        super();
        sharedPreferences = context.getSharedPreferences("gamevideo",
            Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setTotalPoint(int totalPoint) {
        editor.putInt("totalPoint", totalPoint);
        editor.commit();
    }
    
    public int getTotalPoint() {
        return sharedPreferences.getInt("totalPoint", 0);
    }

}
