package etcomm.com.etcommyolk.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class GlobalSetting {


    private Context mContext;
    private SharedPreferences mPrefs;
    private static GlobalSetting mSpInstance;

    private static final String PREFERENCE_NAME = "etcommyolk";
    private static final String PREFERENCE_USER_ID = "user_id";


    private GlobalSetting(Context context) {
        mContext = context;
    }

    // 单例
    public static synchronized GlobalSetting getInstance(Context context) {
        if (mSpInstance == null) {
            mSpInstance = new GlobalSetting(context);
        }
        return mSpInstance;
    }


    // 初始化sp对象
    public SharedPreferences getmPrefs() {
        if (mPrefs == null) {
            mPrefs = mContext.getSharedPreferences(PREFERENCE_NAME,
                    Context.MODE_PRIVATE);
        }
        return mPrefs;
    }


    private void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getmPrefs().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private void saveString(String key, String value) {
        SharedPreferences.Editor editor = getmPrefs().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void saveInt(String key, int value) {
        SharedPreferences.Editor editor = getmPrefs().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    // 保存uid
    public void saveUid(String uid) {
        saveString(PREFERENCE_USER_ID, uid);
    }

    // 获取uid
    public String getUid() {
        return getmPrefs().getString(PREFERENCE_USER_ID, "");
    }

}
