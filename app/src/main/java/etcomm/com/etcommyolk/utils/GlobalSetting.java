package etcomm.com.etcommyolk.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class GlobalSetting {


    private Context mContext;
    private SharedPreferences mPrefs;
    private static GlobalSetting mSpInstance;

    private static final String PREFERENCE_NAME = "etcommyolk";
    private static final String PREFERENCE_USER_ID = "user_id";
    private static final String PREFERENCE_SCORE = "preference_score";
    public static final String ACCESS_TOKEN = "access_token"; //保存token
    public static final String TOTALSCORE = "totalscore";


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
    public SharedPreferences getSharedPreferences() {
        if (mPrefs == null) {
            mPrefs = mContext.getSharedPreferences(PREFERENCE_NAME,
                    Context.MODE_PRIVATE);
        }
        return mPrefs;
    }


    private void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void saveInt(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    // 保存uid
    public void saveUid(String uid) {
        saveString(PREFERENCE_USER_ID, uid);
    }

    // 获取uid
    public String getUid() {
        return getSharedPreferences().getString(PREFERENCE_USER_ID, "");
    }

    /**
     * 设置积分
     *
     * @return
     */
    public void setScore(String score) {
        saveString(PREFERENCE_SCORE, score);
    }

    /**
     * 获取积分
     *
     * @return
     */
    public String getScore() {
        return getSharedPreferences().getString(PREFERENCE_SCORE, "0");
    }

    /**
     * 存储ACCESS_TOKEN
     *
     * @return
     */
    public void setAccessToken(String name) {
        saveString(ACCESS_TOKEN, name);
    }

    public String getAccessToken() {
        return getSharedPreferences().getString(ACCESS_TOKEN, "ee6d116c0e7dab6190cc0e246b421cfc");
    }

    /**
     * 设总积分
     *
     * @return
     */
    public void setTotalScore(String score) {
        saveString(TOTALSCORE, score);
    }

    /**
     * 获取总积分
     *
     * @return
     */
    public String getTotalScore() {
        return getSharedPreferences().getString(TOTALSCORE, "");
    }
}
