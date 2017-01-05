package etcomm.com.etcommyolk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.iwown.android_iwown_ble.utils.LogUtil;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;

import etcomm.com.etcommyolk.service.data.DataDao;
import etcomm.com.etcommyolk.service.data.DataPerDay;
import etcomm.com.etcommyolk.service.data.DataPerDayFromWrist;
import etcomm.com.etcommyolk.service.data.DataPerHour;
import etcomm.com.etcommyolk.service.data.DeviceDailyData;
import etcomm.com.etcommyolk.service.data.DataSync;
import etcomm.com.etcommyolk.service.data.UserDao;
import etcomm.com.etcommyolk.service.db.DatabaseHelper;
import etcomm.com.etcommyolk.utils.GlobalSetting;
import etcomm.com.etcommyolk.utils.Preferences;
import etcomm.com.etcommyolk.utils.StringUtils;
import etcomm.com.etcommyolk.service.data.Data;

/**
 * 数据上传服务
 *
 * @author iexpressbox
 */
public class StepDataUploadService extends IntentService {

    private static final String tag = "StepDataUploadService";
    private static final String dateformat = "yyyyMMdd";
    private boolean isusersubmit;
    private boolean isexit;

    public StepDataUploadService() {
        super("");
    }

    private Context mContext;
    private UserDao userDao;
    private DataDao dataDao;
    private SharedPreferences sp;
    private DataSync datasync;
    private Dao daoDeviceDailyData;
    private GlobalSetting prefs;

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mContext = this;
        prefs = etcomm.com.etcommyolk.utils.GlobalSetting.getInstance(mContext);
        Log.i(tag, "onCreate");
    }

    /**
     * 获取手机中的数据
     *
     * @param daily_date
     * @return
     */
    private DataPerDayFromWrist getLocalDataFromPhone(String daily_date) {
        // TODO Auto-generated method stub
        DataPerDayFromWrist localData = new DataPerDayFromWrist(daily_date, 0, 0, 0, 0, Integer.parseInt(prefs.getPedometerTarget()));
        sp = getApplicationContext().getSharedPreferences(Preferences.DayData, Context.MODE_PRIVATE);
        String daily_date_type = DateTime.parse(daily_date, DateTimeFormat.forPattern("yyyy-MM-dd")).toString(dateformat);
        float steps = prefs.getTmpStep(mContext, daily_date_type);
        float bluesteps = sp.getFloat(daily_date_type + "-" + prefs.getUserId() + Preferences.BLUEDaySteps, 0);
        if (steps > 0 || bluesteps > 0) {
            if (steps >= bluesteps) {

                // TODO 临时方式
                float miles = prefs.getTmpMiles(mContext, daily_date_type);
                float caliries = prefs.getTmpCaliries(mContext, daily_date_type);
                float seconds = prefs.getTmpSeconds(mContext, daily_date_type);
                localData.setC(caliries);
                localData.setD(miles);
                localData.setS((int) steps);
                localData.setT(seconds);
            } else {
                float miles = sp.getFloat(daily_date_type + "-" + prefs.getUserId() + Preferences.BLUEDayMile, 0);
                float caliries = sp.getFloat(daily_date_type + "-" + prefs.getUserId() + Preferences.BLUEDayCalories, 0);
                float seconds = sp.getFloat(daily_date_type + "-" + prefs.getUserId() + Preferences.BLUEDaySeconds, 0);
                float step = sp.getFloat(daily_date_type + "-" + prefs.getUserId() + Preferences.BLUEDaySteps, 0);
                localData.setC(caliries);
                localData.setD(miles);
                localData.setS((int) step);
                localData.setT(seconds);
            }
            Log.i(tag, "read LocalData Date " + daily_date + " from Sp : " + localData.toParseJsonString());
            return localData;
        } else {
            Log.i(tag, "read LocalData Date " + daily_date + " from Sp : steps = 0");
        }
        DateTime datetime = DateTime.parse(daily_date, DateTimeFormat.forPattern("yyyy-MM-dd"));
        DataPerDay data = getDataPerDay(datetime);
        if (data == null) {
            return null;
        } else {

        }
        List<DataPerHour> list = data.getList();
        for (int i = 0; i < list.size(); i++) {
            DataPerHour dataperHour = list.get(i);
            localData.setC(localData.getC() + dataperHour.getC());
            localData.setS(localData.getS() + dataperHour.getS());
            localData.setT(localData.getT() + dataperHour.getT());
            localData.setD(localData.getD() + dataperHour.getD());
        }
        Log.i(tag, "getLocalDataFromPhone " + daily_date + " " + localData.toParseJsonString());
        return localData;
    }

    /**
     * 数据同步
     *
     * @param dataMap
     */
    private void syncWristData(HashMap<String, DataPerDayFromWrist> dataMap) {
        StringBuffer sb = new StringBuffer();
        sb.append("[").append("\"").append(prefs.getAccessToken()).append("\"").append(",").append("[");
        Set<String> set = dataMap.keySet();
        Log.i(tag, "Map  set: " + set.size());
        for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
            String string = (String) iterator.next();
            DataPerDayFromWrist cur = dataMap.get(string);
            Log.i(tag, "DataPerDayFromWrist : " + cur.toParseJsonString());
            sb.append(cur.toParseJsonString()).append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("]");
        sb.append("]");
        String datastr = sb.toString();
        Log.i(tag, datastr);
        LogUtil.e("ddUP", ">>>>" + datastr);
        uploaddataWristPerDay(datastr);
    }

    /**
     * 数据同步
     *
     * @param datastr
     */
    private void uploaddataWristPerDay(String datastr) {
        RequestParams params = new RequestParams();
        params.put("data", datastr);
        ApiClient.getInstance().toDateSync(mContext, params, new CommenHandler(){

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);

                Log.i(tag, "上传成功，退出service");
                if (isusersubmit) {
                    Toast.makeText(mContext, "数据同步成功", Toast.LENGTH_SHORT).show();
                }
                if (isexit) {
                    mContext.sendBroadcast(new Intent("isexit"));
                }

            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                Toast.makeText(mContext, R.string.network_conn_error, Toast.LENGTH_SHORT).show();
                save_datasync_to_sp();
            }
        });
    }


    protected void save_datasync_to_sp() {
        // TODO Auto-generated method stub
        // sp保存数据
        if (datasync != null) {
            List<DataPerDay> list = datasync.getList();
            for (DataPerDay dataPerDay : list) {
                String date = dataPerDay.getDate();
                sp.edit().putString(date, JSON.toJSONString(dataPerDay, true)).commit();
            }
        }
        stopSelf();
    }

    /**
     * 获取一日 的数据
     *
     * @param datetime
     * @return
     */
    DataPerDay getDataPerDay(DateTime datetime) {
        DataPerDay day = new DataPerDay();
        Log.i(tag, "getDataPerDay: " + datetime.toString("YYYY-MM-dd"));
        day.setDate(datetime.toString("YYYY-MM-dd"));
        DateTime curtime = datetime.millisOfDay().withMinimumValue();
        for (int i = 0; i <= 23; i++) {
            long curstarttime = curtime.getMillis();
            curtime = curtime.plusHours(1);
            long curendtime = curtime.getMillis();
            if (curendtime > System.currentTimeMillis()) {
                curendtime = System.currentTimeMillis();
                day.getList().add(getDataPerHour(i, curstarttime, curendtime));
                break;
            }
            day.getList().add(getDataPerHour(i, curstarttime, curendtime));
        }

        return day;
    }

    /**
     * 获取一小时的的数据
     */
    DataPerHour getDataPerHour(int h, long startime, long endtime) {
        DataPerHour hour = new DataPerHour(h);
        Data data = null;
        List<Data> datalist = dataDao.getDataFromTimeMillisToTimeMillis(startime, endtime, prefs.getUserId());
        if (datalist != null && datalist.size() > 0) {
            data = datalist.get(0);
            for (int i = 1; i < datalist.size(); i++) {
                Data curdata = datalist.get(i);
                data.setCalaries(data.getCalaries() + curdata.getCalaries());
                data.setMiles(data.getMiles() + curdata.getMiles());
                data.setSeconds(data.getSeconds() + curdata.getSeconds());
                data.setSteps(data.getSteps() + curdata.getSteps());
            }
        }
        if (data != null) {

            hour.setS(data.getSteps());
            hour.setC(data.getCalaries());
            hour.setD(data.getMiles());
            hour.setT(data.getSeconds());
            Log.i(tag, "Hour: " + hour.toString());
        }
        return hour;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Log.i(tag, "onStart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.i(tag, "onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Log.i(tag, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        Log.i(tag, "DateTimeZone.getDefault: " + DateTimeZone.getDefault().toString());
        sp = getSharedPreferences(Preferences.DataUpload, MODE_PRIVATE);
        isusersubmit = intent.getBooleanExtra("usersubmit", false);
        isexit = intent.getBooleanExtra("isexit", false);
        Log.i(tag, "isusersubmit: " + isusersubmit);
        DateTime datetime = new DateTime(DateTimeZone.getDefault());
        long todaystarttime = datetime.millisOfDay().withMinimumValue().getMillis();
        long todaysendtime = datetime.millisOfDay().withMaximumValue().getMillis();
        long lastdaystarttime = datetime.plusDays(1).millisOfDay().withMinimumValue().getMillis();
        if (StringUtils.isEmpty(prefs.getUserId())) {
            Log.e(tag, "EMpty Userid  exit");
            stopSelf();
            return;
        }
        Log.i(tag, "datetime: " + datetime.getMillis() + " todaystarttime " + todaystarttime + "  todaysendtime: " + todaysendtime);
        userDao = new UserDao(mContext);
        dataDao = new DataDao(mContext);
        try {
            daoDeviceDailyData = DatabaseHelper.getHelper(mContext).getDao(DeviceDailyData.class);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (daoDeviceDailyData == null) {
            Log.e(tag, "daoDeviceDailyData null   ");
        } else {

        }
        long high = datetime.getMillis() / 1000;
        long low = datetime.minusDays(8).getMillis() / 1000;
        Log.i(tag, "high: " + high + "  low: " + low);
        ArrayList<DeviceDailyData> list = null;
        HashMap<String, DataPerDayFromWrist> dataMap = new HashMap<>();
        HashMap<String, DataPerDayFromWrist> localdataMap = new HashMap<>();
        // ////////////////////////// 手环数据没有做多账号处理
        try {
            list = (ArrayList<DeviceDailyData>) daoDeviceDailyData.queryBuilder().orderBy("id", true).where().between("timestamp", low, high).query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (list != null) {
            Log.i(tag, "list.size: " + list.size());
            for (int i = 0; i < list.size(); i++) {
                DeviceDailyData obj = list.get(i);
                DataPerDayFromWrist localphone = null;
                if (localdataMap.containsKey(obj.getDaily_date())) {
                    localphone = localdataMap.get(obj.getDaily_date());
                } else {
                    localphone = getLocalDataFromPhone(obj.getDaily_date());
                    localdataMap.put(obj.getDaily_date(), localphone);
                }
                Log.i(tag, "localphone: " + localphone.toParseJsonString());
                if (dataMap.containsKey(obj.getDaily_date())) {
                    DataPerDayFromWrist curwrist = dataMap.get(obj.getDaily_date());
                    if (curwrist.getS() < obj.getSteps()) {
                        dataMap.remove(obj.getDaily_date());
                        curwrist = new DataPerDayFromWrist(obj.getDaily_date(), obj.getSteps(), (float) obj.getCalorie(), 0, (float) obj.getDistance(), Integer.parseInt(prefs.getPedometerTarget()));
                        Log.i(tag, "DataPerDayFromWrist : " + curwrist.toParseJsonString());
                        if (localphone.getS() > curwrist.getS()) {
                            dataMap.put(obj.getDaily_date(), localphone);
                        } else {
                            dataMap.put(obj.getDaily_date(), curwrist);
                        }
                    }
                } else {// String dt,int s,float c,float t,float d,int tg)
                    // 数据比较，本地数据与手环数据比较 ?????????????????????????????????????
                    DataPerDayFromWrist datawrist = new DataPerDayFromWrist(obj.getDaily_date(), obj.getSteps(), (float) obj.getCalorie(), 0, (float) obj.getDistance(), Integer.parseInt(prefs.getPedometerTarget()));
                    Log.i(tag, "DataPerDayFromWrist : " + datawrist.toParseJsonString());
                    if (localphone.getS() > datawrist.getS()) {
                        dataMap.put(obj.getDaily_date(), localphone);
                    } else {
                        dataMap.put(obj.getDaily_date(), datawrist);
                    }
                }
            }

        } else {
            Log.e(tag, "error empty data");
        }
        DateTime datatime = new DateTime(low * 1000);
        for (int i = 0; i <= 8; i++) {
            String date = datatime.toString("yyyy-MM-dd");
            if (dataMap.containsKey(date)) {
                Log.i(tag, "date: " + date + "有数据");
                Log.i(tag, "" + dataMap.get(date).toString());
            } else {
                DataPerDayFromWrist localphone;
                if (localdataMap.containsKey(date)) {
                    localphone = localdataMap.get(date);
                } else {
                    localphone = getLocalDataFromPhone(date);
                }
                Log.i(tag, "date: " + date + "没有数据，从本地读取");
                // = getLocalDataFromPhone(date);
                Log.i(tag, "" + localphone.toString());
                dataMap.put(date, localphone);
            }
            datatime = datatime.plusDays(1);
        }
        syncWristData(dataMap);
    }

}
