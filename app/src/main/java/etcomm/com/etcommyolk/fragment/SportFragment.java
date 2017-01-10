package etcomm.com.etcommyolk.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.excheer.library.BluetoothConnectListener;
import com.excheer.library.BluetoothLeManager;
import com.excheer.library.DaySportsData;
import com.iwown.android_iwown_ble.bluetooth.WristBandDevice;
import com.iwown.android_iwown_ble.model.WristBand;
import com.iwown.android_iwown_ble.utils.LogUtil;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.activity.MineActivity;
import etcomm.com.etcommyolk.activity.MsgListActivity;
import etcomm.com.etcommyolk.activity.RankActivity;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.entity.PedometerItem;
import etcomm.com.etcommyolk.entity.Weather;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.handler.PedometerItemHandler;
import etcomm.com.etcommyolk.handler.WeatherHandler;
import etcomm.com.etcommyolk.service.StepDataUploadService;
import etcomm.com.etcommyolk.service.data.DataPerDayFromWrist;
import etcomm.com.etcommyolk.service.data.Device5MinData;
import etcomm.com.etcommyolk.service.data.DeviceDailyData;
import etcomm.com.etcommyolk.service.db.DatabaseHelper;
import etcomm.com.etcommyolk.utils.ACache;
import etcomm.com.etcommyolk.utils.BluetoothUtils;
import etcomm.com.etcommyolk.utils.Preferences;
import etcomm.com.etcommyolk.utils.StringUtils;
import etcomm.com.etcommyolk.widget.AutoTextView;
import etcomm.com.etcommyolk.widget.StepPageDataView;
import me.chunyu.pedometerservice.IntentConsts;
import me.chunyu.pedometerservice.PedometerCounterService;

public class SportFragment extends BaseFragment implements BluetoothConnectListener {
    /**
     * 控制OnResume()执行次数
     */
    public static boolean limitOnresumeSports;

    /**
     * onCreateView
     * @param view
     * @param savedInstanceState
     */
    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sports;
    }


    final String TAG = "WalkPageFragment";
    protected Dialog dialog;

    /**
     * 敏狐手环连接相关对象
     */
    private BluetoothLeManager bluetoothLeManager;
    /**
     * 记步传感器的步数
     */
    private int mCYStepCounterSensorValue;
    /**
     * 计步传感器的时间
     */
    private long mSCSMotionTime;
    /**
     * 加速度传感器的步数
     */
    private int mCYAccelerateSensorValue;
    /**
     * 加速度传感器的时间
     */
    private long mASMotionTime;
    /**
     * 保留小数点后两位
     */
    private DecimalFormat decimalFormat;
    /**
     * 推送消息
     */
    private ImageView msg_iv;
    /**
     * 进入排行页面
     */
    private ImageView in_rank;
    /**
     * 进入我的页面
     */
    private ImageView in_mine;

    static final int IS_TODAY = 0x09;
    private static final String tag = SportFragment.class.getSimpleName();
    private static final String tagPage = SportFragment.class.getSimpleName() + "Page";
    private static final String tagBlue = SportFragment.class.getSimpleName() + "Blue";
    protected static final int SendDevice5Mins = 11;
    protected static final int SendDeviceDaily = 12;
    protected static final int GetLocalSport255 = 21;
    protected static final int SendDevicePower = 13;
    protected static final int SendDiviceBlueDataUpdateView = 22;
    protected static final int SyncBlueDeviceData = 15;
    protected static final int SendDeviceTimeSync = 16;
    private static final String dateformat = "yyyyMMdd";

    private TextView userrank_tv;
    private TextView userrank;
    private TextView totalrankcount;
    private ViewPager viewpager;


    private ImageView walk_page_leftcircle;
    private ImageView walk_page_rightcircle;

    private RelativeLayout wristband;
    private TextView wrist_status;
    private TextView tv_motiontimes;
    private TextView tv_mileage;
    private TextView tv_caliries;
    private TextView tv_motiontimes_unit;
    private SeekBar curprogress;
    private TextView caliriesinfo;
    private TextView distanceinfo;
    /**
     * app内部计步的数据
     */
    protected int curStep = 0;
    protected float Miles = 0f;
    protected float Seconds = 0f;
    protected float Calories = 0f;
    /**
     * 蓝牙手环计步的数据
     */
    protected int blueStep = 0; // 步数
    protected float blueMiles = 0f;
    protected float blueSeconds = 0f;
    protected float blueCalories = 0f;// 卡路里

    /**
     * 回传蓝牙计步数据
     */
    DataPerDayFromWrist wristData = new DataPerDayFromWrist(getActivity());

    private void saveWristDataToSp() {
        if (wristData.getDt().equals(new DateTime().toString("yyyy-MM-dd"))) {// 首先判断日期是否相同，如果不同，就已经过了一天了，需要清空数据

            sp.edit().putString("blue_" + new DateTime().toString("yyyy-MM-dd") + prefs.getUserId() + "_data", wristData.toParseJsonString()).commit();
        } else {
            sp.edit().putString("blue_" + wristData.getDt() + prefs.getUserId() + "_data", wristData.toParseJsonString()).commit();
            readWristDataFromSp();
        }
    }

    private void readWristDataFromSp() {
        wristData = new DataPerDayFromWrist(getActivity());
        String data = sp.getString("blue_" + new DateTime().toString("yyyy-MM-dd") + prefs.getUserId() + "_data", wristData.toParseJsonString());
        wristData = JSON.parseObject(data, DataPerDayFromWrist.class);

    }

    private Timer mBlueTimer;

    protected int curp = 0;
    protected WristBand mDevice;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SendDiviceBlueDataUpdateView:
                    updateViewByBlueData();
                    break;
                case SyncBlueDeviceData:
                    wrist_status.setText("正在同步手环数据...");
                    break;
                case GetLocalSport255:
                    wrist_status.setText("已连接");
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private Context mContext;
    private LinearLayout currank_li;
    protected ArrayList<PedometerItem.Pedometer.PedometerData> pedometerlist = new ArrayList<PedometerItem.Pedometer.PedometerData>();
    protected int mSuggestSteps;
    private PagerAdapter pageAdapter;
    protected int curPageIndex;
    /**
     * 本地数据缓存
     */
    private SharedPreferences localsp;
    private BluetoothUtils mBluetoothUtils;
    /**
     * 寻问是否请求开启蓝牙
     */
    private boolean isNotAskToOpenBlue = false;
    private TimerTask mBlueTask;
    private SharedPreferences sp;
    private boolean LooperPrepared = false;

    private Dao daoDevice5MinData;
    private Dao daoDeviceDailyData;
    private ACache acache;
    private AutoTextView switcher;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        sp = activity.getPreferences(Context.MODE_PRIVATE);
    }

    /**
     * 是否已经被绑定
     */
    private boolean hasAskEnableBlueTooth = false;


    @Override
    public void receive_msg_data() {
            msg_iv.setImageResource(R.mipmap.icon_msg_unread);
    }

    @Override
    public void onResume() {
        super.onResume();

        LogUtil.e(TAG, TAG + "onResume>>>>");

        /**
         * 当前计步为第三方手环计步 模仿我的手环列表页面的自动连接功能，但是必须通过事件轮询实现，后期需要优化
         */
        if (prefs.getMacAddress().length() > 5 && org.apache.commons.lang3.StringUtils.startsWithIgnoreCase(prefs.getBlueDeviceName(), "Braceli5")) {
            // 更新艾薇手环蓝牙数据显示
            final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
            final boolean mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();

            if (!hasAskEnableBlueTooth) {
                mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
                hasAskEnableBlueTooth = true;
            }
            if (mIsBluetoothOn && mIsBluetoothLePresent) {
                if (!StringUtils.isEmpty(prefs.getMacAddress())) {
                    if (!WristBandDevice.getInstance(mContext).isConnected()) {
                        if (mDevice == null) {
                            mDevice = new WristBand();
                            mDevice.setAddress(prefs.getMacAddress());
                            mDevice.setName(prefs.getBlueDeviceName());
                            mDevice.setRssi(prefs.getBlueDeviceRssi());
                            LogUtil.i(TAG, "Create mDevice");
                        }
                        WristBandDevice.getInstance(mContext).connect(mDevice);
                    }
                }
            }
        }
        if (prefs.getHaveReceiveUnReadData()) {
            msg_iv.setImageResource(R.mipmap.icon_msg_unread);
        } else {
            msg_iv.setImageResource(R.mipmap.ic_messege);
        }
        if (!StringUtils.isEmpty(prefs.getPedometerTarget())) {
            mSuggestSteps = Integer.parseInt(prefs.getPedometerTarget());
        }
        // bindSer();
        if (!StringUtils.isEmpty(prefs.getMacAddress())) {
            if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                if (!isNotAskToOpenBlue) {
                    isNotAskToOpenBlue = true;
                    mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
                }
            }

            wristband.setVisibility(View.VISIBLE);
            if (mBlueTimer == null) {
                mBlueTimer = new Timer();
                mBlueTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (!LooperPrepared) {
                            Looper.prepare();
                            LooperPrepared = true;
                        }
                        Log.i(tag, "mBlueTimer mBlueTask");
                        Log.i(tag, "SendDeviceDaily");
                        mHandler.sendEmptyMessage(SendDeviceDaily);
                    }
                };
                mBlueTimer.schedule(mBlueTask, 45 * 1000, 15 * 1000);
            } else {
                mBlueTask.cancel();
                mBlueTimer.cancel();
                if (mBlueTask != null) {
                    mBlueTimer.schedule(mBlueTask, 45 * 1000, 15 * 1000);
                }
            }
        } else {
            wristband.setVisibility(View.INVISIBLE);
        }
        /**
         * 进行服务的重新启动，进行除第一次进入首页外的数据更新，可能会浪费内存，后期需要优化
         */
        Intent serviceIntent = new Intent(getActivity(), PedometerCounterService.class);
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onPause() {
        Log.i(tag, "onPause mBlueTask cancel");
        if (mBlueTimer != null) {
            mBlueTask.cancel();
            mBlueTimer.cancel();
            mBlueTimer.purge();
            mBlueTask = null;
            mBlueTimer = null;
        }
        super.onPause();
    }



    @Override
    public void onStart() {
        Log.i(tag, "onStart");
        super.onStart();
        getTodayRank();
        getWeather();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(tag, "onViewCreated");

    }

    private void getTodayRank() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        Log.i(tag, "params: " + params.toString());
        client.toShowRank(getActivity(), params, new CommenHandler() {
            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);

                localsp.edit().putString("LastRank", commen.content).commit();

                String[] strs = commen.content.split("/");
                if (strs.length > 1) {
                    String user = strs[0];
                    String total = strs[1];
                    Log.e(tag, "user: " + user + "total: " + total);
                    userrank.setText(user);
                    userrank_tv.setVisibility(View.VISIBLE);
                    totalrankcount.setText(total);
                }

            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);

                String lastRank = localsp.getString("LastRank", "");
                if (!StringUtils.isEmpty(lastRank)) {
                    String[] strs = lastRank.split("/");
                    Log.e(tag, "lastRank: " + lastRank);
                    if (strs.length > 1) {
                        String user = strs[0];
                        String total = strs[1];
                        Log.e(tag, "user: " + user + "total: " + total);
                        userrank.setText(user);
                        userrank_tv.setVisibility(View.VISIBLE);
                        totalrankcount.setText(total);
                    }
                }

            }
        });
    }
    //获取天气
    private void getWeather() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        client.toWeather(getActivity(), params, new WeatherHandler(){

            @Override
            public void onSuccess(Weather weather) {
                super.onSuccess(weather);
                switcher.setUpText(weather.content.weather_status + "\n" + weather.content.temp);
                switcher.setDownText("空气质量" + "\n" + weather.content.qlty);
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
            }
        });
    }


    @Override
    public void onDestroy() {
        Log.i(tag, "onDestroy");
        unregisterReceiver();
        unregisterDeviceReceiver();
        Intent service = new Intent(getActivity(), StepDataUploadService.class);
        service.putExtra("isexit", true);
        getActivity().startService(service);
        //敏狐手环连接 结束当前生命时断开连接
        if (prefs.getMacAddress().length() > 5 && org.apache.commons.lang3.StringUtils.startsWithIgnoreCase(prefs.getMacAddress(), "F4:06:A5")) {
            //判断为敏狐手环
            if (bluetoothLeManager.isConnected()) {
                bluetoothLeManager.disconnect();
                Log.d("sampleBLE", "onDestroy");
            }
        }
        super.onDestroy();
    }

    SparseArray<StepPageDataView> sparseArray = new SparseArray<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = super.onCreateView(inflater, container, savedInstanceState);
        switcher = (AutoTextView) layout.findViewById(R.id.switcher);
        msg_iv = (ImageView) layout.findViewById(R.id.base_right);
        in_rank = (ImageView) layout.findViewById(R.id.in_rank);
        in_mine = (ImageView) layout.findViewById(R.id.base_left);
        currank_li = (LinearLayout) layout.findViewById(R.id.currank_li);
        userrank = (TextView) layout.findViewById(R.id.userrank);
        userrank_tv = (TextView) layout.findViewById(R.id.userrank_tv);
        totalrankcount = (TextView) layout.findViewById(R.id.totalrankcount);
        viewpager = (ViewPager) layout.findViewById(R.id.viewpager);
        in_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RankActivity.class);
                intent.putExtra("islevel", prefs.getIslevel());
                intent.putExtra("token", prefs.getAccessToken());
                startActivity(intent);
            }
        });
        msg_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,MsgListActivity.class));
            }
        });
        walk_page_leftcircle = (ImageView) layout.findViewById(R.id.walk_page_leftcircle);
        walk_page_rightcircle = (ImageView) layout.findViewById(R.id.walk_page_rightcircle);

        wristband = (RelativeLayout) layout.findViewById(R.id.wristband);
        wrist_status = (TextView) layout.findViewById(R.id.wrist_status);
        tv_caliries = (TextView) layout.findViewById(R.id.iv_caliries);
        tv_mileage = (TextView) layout.findViewById(R.id.tv_mileage);
        tv_motiontimes = (TextView) layout.findViewById(R.id.tv_motiontimes);
        tv_motiontimes_unit = (TextView) layout.findViewById(R.id.tv_motiontimes_unit);
        tv_motiontimes_unit.setText("小时");
        tv_motiontimes_unit.setVisibility(View.VISIBLE);
        curprogress = (SeekBar) layout.findViewById(R.id.curprogress);
        curprogress.setClickable(false);
        curprogress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        caliriesinfo = (TextView) layout.findViewById(R.id.caliriesinfo);
        distanceinfo = (TextView) layout.findViewById(R.id.distanceinfo);
        initData();
        pageAdapter = new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                StepPageDataView view = new StepPageDataView(mContext);
                if (position == pedometerlist.size() - 1) {
                    view.setData(pedometerlist.get(position), mSuggestSteps, true);
                } else {
                    view.setData(pedometerlist.get(position), mSuggestSteps, false);
                }
                container.addView(view);
                Log.e(tagPage, "sparseArray put : " + position);
                sparseArray.put(position, view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public int getCount() {

                return pedometerlist.size();
            }

        };

        viewpager.setAdapter(pageAdapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onPageSelected(int arg0) {
                curPageIndex = arg0;
                PedometerItem.Pedometer.PedometerData object = pedometerlist.get(arg0);
                tv_caliries.setText(object.calorie);
                tv_mileage.setText(object.distance);
                tv_motiontimes.setText(decimalFormat.format(Float.valueOf(object.total_time)));
                if (Float.valueOf(object.total_time) == 0 && Float.valueOf(object.calorie) > 0) {
                    tv_motiontimes.setText(decimalFormat.format(Float.valueOf(object.total_time)));
                    tv_motiontimes_unit.setVisibility(View.VISIBLE);
                } else {
                    tv_motiontimes.setText(decimalFormat.format(Float.valueOf(object.total_time)));
                    tv_motiontimes_unit.setVisibility(View.VISIBLE);
                }
                int curpro = (int) (100 * Float.parseFloat(object.step.equals("-") ? "0" : object.step) / Float.valueOf((object.target)));
                if (curpro > 100) {
                    curpro = 100;
                }
                Log.i(tag, "setProgress: 3" + curpro);
                curprogress.setProgress(curpro);
                caliriesinfo.setText(object.calorie_text);
                distanceinfo.setText(object.distance_text);
                if (curpro > 50) {
                    caliriesinfo.setGravity(Gravity.LEFT);
                    distanceinfo.setGravity(Gravity.RIGHT);
                } else {
                    caliriesinfo.setGravity(Gravity.RIGHT);
                    distanceinfo.setGravity(Gravity.LEFT);
                }
                if (pedometerlist.size() == 1) {
                    walk_page_leftcircle.setVisibility(View.INVISIBLE);
                    walk_page_rightcircle.setVisibility(View.INVISIBLE);
                } else if (arg0 == 0 && arg0 == pedometerlist.size() - 1) {
                    walk_page_leftcircle.setVisibility(View.INVISIBLE);
                    walk_page_rightcircle.setVisibility(View.INVISIBLE);
                } else if (arg0 == 0) {
                    walk_page_leftcircle.setVisibility(View.INVISIBLE);
                    walk_page_rightcircle.setVisibility(View.VISIBLE);
                } else if (arg0 == pedometerlist.size() - 1) {
                    walk_page_leftcircle.setVisibility(View.VISIBLE);
                    walk_page_rightcircle.setVisibility(View.INVISIBLE);
                } else {
                    walk_page_leftcircle.setVisibility(View.VISIBLE);
                    walk_page_rightcircle.setVisibility(View.VISIBLE);
                }
                // cancelmDialog();
                StepPageDataView cur = sparseArray.get(arg0);
                if (cur != null) {
                    Log.e(tagPage, " sparseArray get " + arg0);
                    cur.startProcessAnimation((int) (Float.valueOf(pedometerlist.get(arg0).step) * 100 / Float.valueOf(pedometerlist.get(arg0).target)));
                } else {
                    Log.e(tagPage, "sparseArray get NULL");
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (arg1 != 0 || arg2 != 0) {
                    walk_page_leftcircle.setVisibility(View.INVISIBLE);
                    walk_page_rightcircle.setVisibility(View.INVISIBLE);
                } else {
                    walk_page_leftcircle.setVisibility(View.VISIBLE);
                    walk_page_rightcircle.setVisibility(View.VISIBLE);
                    if (arg0 == 0) {
                        walk_page_leftcircle.setVisibility(View.INVISIBLE);
                    } else if (arg0 == pedometerlist.size() - 1) {
                        walk_page_rightcircle.setVisibility(View.INVISIBLE);
                    }
                    if (pedometerlist.size() == 1) {
                        walk_page_leftcircle.setVisibility(View.INVISIBLE);
                        walk_page_rightcircle.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            // arg0 ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做
            public void onPageScrollStateChanged(int arg0) {
                Log.i(tagPage, "onPageScrollStateChanged: " + arg0);
            }
        });
        initFirstPageWithLocalData();
        return layout;
    }

    /**
     * 防止开始时进入 的卡顿，部分手机（华为）进入viewPager，不执行OnPageSelected
     */
    private void initFirstPageWithLocalData() {
        PedometerItem.Pedometer.PedometerData object = pedometerlist.get(0);
        tv_caliries.setText(object.calorie);
        tv_mileage.setText(object.distance);
        tv_motiontimes.setText(decimalFormat.format(Float.valueOf(object.total_time)));
        tv_motiontimes_unit.setVisibility(View.VISIBLE);
        // }
        int curpro = (int) (100 * Float.parseFloat(object.step) / Float.valueOf(object.target));
        if (curpro > 100) {
            curpro = 100;
        }
        Log.i(tag, "setProgress: 2" + curpro);
        curprogress.setProgress(curpro);
        caliriesinfo.setText(object.calorie_text);
        distanceinfo.setText(object.distance_text);
        if (curpro > 50) {
            caliriesinfo.setGravity(Gravity.LEFT);
            distanceinfo.setGravity(Gravity.RIGHT);
        } else {
            caliriesinfo.setGravity(Gravity.RIGHT);
            distanceinfo.setGravity(Gravity.LEFT);
        }
        Log.i(tagPage, "updataTodayView: " + (curStep > blueStep ? curStep : blueStep));
        if (prefs.isSoftPedometerOn()) {
            updataTodayView(curStep);
        } else {
            updataTodayView(blueStep);
        }

    }

    private void readTodayFromdataSp() {
        dailydate = new DateTime().toString(dateformat);
        SharedPreferences datasp = mContext.getSharedPreferences(Preferences.DayData, Context.MODE_PRIVATE);
        curStep = (int) prefs.getTmpStep(mContext, dailydate);
        Miles = prefs.getTmpMiles(mContext, dailydate);
        Calories = (int) prefs.getTmpCaliries(mContext, dailydate);
        Seconds = prefs.getTmpSeconds(mContext, dailydate);
        Log.i(TAG, "TodayStep sp : " + "代表用户每次存入数据库的数据" + curStep);
        blueStep = (int) datasp.getFloat(dailydate + "-" + prefs.getUserId() + Preferences.BLUEDaySteps, 0);
        blueMiles = datasp.getFloat(dailydate + "-" + prefs.getUserId() + Preferences.BLUEDayMile, 0);
        blueCalories = datasp.getFloat(dailydate + "-" + prefs.getUserId() + Preferences.BLUEDayCalories, 0);
        blueSeconds = datasp.getFloat(dailydate + "-" + prefs.getUserId() + Preferences.BLUEDaySeconds, 0);
        Log.i(TAG, "ReadTodayFrom sp : " + "curStep" + curStep + " Calories " + Calories + " blueStep" + blueStep + " blueCalories" + blueCalories);
    }

    String dailydate = "";

    private void saveTodayToDataSp(float bStep, float bMiles, float bCalor, float bSecondes) {
        dailydate = new DateTime().toString(dateformat);
        SharedPreferences datasp = mContext.getSharedPreferences(Preferences.DayData, Context.MODE_PRIVATE);
        SharedPreferences.Editor dataspEditor = datasp.edit();
        dataspEditor.putFloat(dailydate + "-" + prefs.getUserId() + Preferences.BLUEDaySteps, bStep);
        dataspEditor.putFloat(dailydate + "-" + prefs.getUserId() + Preferences.BLUEDayMile, bMiles);
        dataspEditor.putFloat(dailydate + "-" + prefs.getUserId() + Preferences.BLUEDayCalories, bCalor);
        dataspEditor.putFloat(dailydate + "-" + prefs.getUserId() + Preferences.BLUEDaySeconds, bSecondes);
        dataspEditor.commit();
    }

    String strMile = "";// UI 攀登公里数显示

    private void initData() {
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // 判断系统是否支持蓝牙连接
            mBluetoothUtils = new BluetoothUtils(getActivity());
        }
        acache = ACache.get(mContext);
        registerReceiver();
        registerDeviceReceiver();
        localsp = getActivity().getPreferences(Context.MODE_PRIVATE);
        mSuggestSteps = Integer.parseInt(prefs.getPedometerTarget());
        try {
            daoDevice5MinData = DatabaseHelper.getHelper(mContext).getDao(Device5MinData.class);
            daoDeviceDailyData = DatabaseHelper.getHelper(mContext).getDao(DeviceDailyData.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (daoDeviceDailyData == null || daoDevice5MinData == null) {
            Log.e(tag, "daoDeviceDailyData null    or   daoDevice5MinData null");
        }

        /**
         *  暂时存储计步 方便后续使用 固定时间的值
         *  第一时间清除老数据
         */
        decimalFormat = new DecimalFormat("0.0");
        dailydate = new DateTime().toString(dateformat);
        Log.i(tagPage, "0 .--> getFrom SP curStep : " + curStep);
        PedometerItem.Pedometer.PedometerData object = new PedometerItem().new Pedometer().new PedometerData();

        object.date = "今天";
        if (!prefs.isSoftPedometerOn()) {
            if (prefs.getBlueConn()) {
                wrist_status.setText("已连接");
            } else {
                wrist_status.setText("未连接");
            }
            /**
             * 蓝牙获取到的设备号判断当前设备
             */
            if (prefs.getMacAddress().length() > 5 && org.apache.commons.lang3.StringUtils.startsWithIgnoreCase(prefs.getBlueDeviceName(), "Braceli5")) {
                String blueData = prefs.getBlueData();
                // 更新艾薇手环蓝牙数据显示
                syncConnSyncData(blueData);
                /**
                 * 开启记步动态监听服务器 实现动态更新首页记步数据
                 */
            } else if (prefs.getMacAddress().length() > 5 && org.apache.commons.lang3.StringUtils.startsWithIgnoreCase(prefs.getMacAddress(), "F4:06:A5")) {
                //判断为敏狐手环
                /**
                 * 创建敏狐连接对象
                 */
                bluetoothLeManager = BluetoothLeManager.getInstance(getActivity(), SportFragment.this, true);
                /**
                 * 处理耗时操作
                 */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * 设置同步数据
                         */
                        bluetoothLeManager.setSyncFlag(true);
                        /**
                         * 连接设备
                         */
                        bluetoothLeManager.connect(prefs.getMacAddress());
                        Log.d("sampleBLE", "start");
                    }
                }).start();

            }
            object.calorie = decimalFormat.format(blueCalories);
            object.distance = (decimalFormat.format(blueMiles));
            object.step = (String.valueOf(blueStep));
            object.total_time = ("0");

            object.target = (prefs.getPedometerTarget());
            String strCalories = StringUtils.calorie2Text(blueCalories);
            object.calorie_text = (strCalories);
            strMile = StringUtils.distance2Text(blueMiles);
            object.distance_text = (strMile);
        } else {
            object.calorie = (decimalFormat.format(Calories));
            object.distance = (decimalFormat.format(Miles));
            object.step = (String.valueOf(curStep));
            object.total_time = (decimalFormat.format(Seconds));
            object.target = (prefs.getPedometerTarget());
            String strCalories = StringUtils.calorie2Text(Calories);
            object.calorie_text = (strCalories);
            strMile = StringUtils.distance2Text(Miles);
            object.distance_text = (strMile);
        }

        in_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MineActivity.class));
            }
        });

        Log.i(tag, "add " + object.toString());
        pedometerlist.add(object);
        /**
         * 网络请求
         */
        getTwoMonthDataFromNet();
    }

    /**
     * 需要保留的网络请求方法
     */
    private void getTwoMonthDataFromNet() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        client.toPedometerWeek(getActivity(), params, new PedometerItemHandler(){


            @Override
            public void onSuccess(PedometerItem pedometerItem) {
                super.onSuccess(pedometerItem);

                /**
                 * 需要第一次加载时存储的步数
                 */
                curStep = Integer.valueOf(pedometerItem.content.data.get(pedometerItem.content.data.size() - 1).step);

                // 返回八天数据，今天在这个当日当回数据的基础上计步

                List<PedometerItem.Pedometer.PedometerData> li = pedometerItem.content.data;
                if (li != null && li.size() > 0) {
                    PedometerItem.Pedometer.PedometerData PedometerData = null;
                    for (int i = 0; i < li.size(); i++) {
                        if ((li.size() - 2 - i) > -1) {
                            PedometerData = (PedometerItem.Pedometer.PedometerData) li.get(li.size() - 2 - i);
                        }

                        if (!pedometerlist.contains(PedometerData) && pedometerlist.size() > 0 && PedometerData != null) {
                            pedometerlist.add(0, PedometerData);
                        }
                    }
                    sparseArray.clear();
                    pageAdapter.notifyDataSetChanged();
                    viewpager.setCurrentItem(pedometerlist.size() - 1, false);
                }


            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
            }
        });
    }

    /**
     * 春雨计步算法
     */
    private BroadcastReceiver stepReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (prefs.isSoftPedometerOn()) {

                switch (intent.getAction()) {
                    // 计步传感器的步数增加广播
                    case IntentConsts.STEP_COUNTER_SENSOR_VALUE_FILTER:
                        // 计步传感器的步数
                        mCYStepCounterSensorValue = intent.getIntExtra(IntentConsts.CY_STEP_SENSOR_VALUE_EXTRA, 0);
                        // 计步传感器的时间
                        mSCSMotionTime = intent.getLongExtra(IntentConsts.MOTION_TIME_STEP_SENSOR_EXTRA, 0L);
                        calculateActiveMile(mCYStepCounterSensorValue);
                        calculateActiveCalory(mCYStepCounterSensorValue);
                        curStep = mCYStepCounterSensorValue;
                        prefs.saveTmpStep(mContext, (float) mCYStepCounterSensorValue, (float) mDistance, (float) mSCSMotionTime / 3600000, (float) mCalories);

                        break;

                    // 加速度传感器的步数增加广播
                    case IntentConsts.ACCELERATE_SENSOR_VALUE_FILTER:
                        // 加速度传感器的步数
                        mCYAccelerateSensorValue = intent.getIntExtra(IntentConsts.CY_ACCELERATE_SENSOR_VALUE_EXTRA, 0);
                        // 加速度传感器的时间
                        mASMotionTime = intent.getLongExtra(IntentConsts.MOTION_TIME_ACCELERATE_EXTRA, 0L);
                        calculateActiveMile(mCYAccelerateSensorValue);
                        calculateActiveCalory(mCYAccelerateSensorValue);
                        curStep = mCYAccelerateSensorValue;
                        prefs.saveTmpStep(mContext, (float) mCYAccelerateSensorValue, (float) mDistance, (float) mASMotionTime / 3600000, (float) mCalories);
                        break;
                }

                if (curPageIndex == pedometerlist.size() - 1) {
                    readTodayFromdataSp();
                    updateViewFromView();
                }
            } else {
                if (intent.getAction().equals(Preferences.HOME_STEP_UPDATE)) {
                    if (!prefs.getBlueData().equals("")) {
                        CurrData_0x29_Convert_DailyData(prefs.getBlueData());
                    }
                    readTodayFromdataSp();
                    updateViewByBlueData();

                }
                updateViewByBlueData();
            }
        }
    };


    void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(IntentConsts.STEP_COUNTER_SENSOR_VALUE_FILTER);
        filter.addAction(IntentConsts.ACCELERATE_SENSOR_VALUE_FILTER);
        filter.addAction(IntentConsts.TIME_CHANGE_FILTER);
        filter.addAction(Preferences.HOME_STEP_UPDATE);
        getActivity().registerReceiver(stepReceiver, filter);
    }

    /**
     * 更新蓝牙连接数据
     */
    @SuppressLint("NewApi")
    protected void updateViewByBlueData() {
        PedometerItem.Pedometer.PedometerData object = pedometerlist.get(pedometerlist.size() - 1);
        if (object.date.equals("今天")) {
            LogUtil.i(TAG, "3 .--> updateViewByBlueData");
            DecimalFormat decimalFormat = new DecimalFormat("0.0");
            object.calorie = (decimalFormat.format(blueCalories));
            object.distance = (decimalFormat.format(blueMiles));
            object.step = (blueStep + "");
            object.total_time = (decimalFormat.format(blueSeconds));
            String strCalories = StringUtils.calorie2Text(blueCalories);
            object.calorie_text = (strCalories);
            strMile = StringUtils.distance2Text(blueMiles);
            object.distance_text = (strMile);
            wrist_status.setText("已连接");
            if (curPageIndex == pedometerlist.size() - 1) {
                pageAdapter.notifyDataSetChanged();
                updataTodayView(blueStep);
                tv_caliries.setText(decimalFormat.format(blueCalories));
                tv_mileage.setText(decimalFormat.format(blueMiles));
                tv_motiontimes.setText(decimalFormat.format(blueSeconds));
                tv_motiontimes_unit.setVisibility(View.VISIBLE);
                caliriesinfo.setText(strCalories);
                distanceinfo.setText(strMile);
                int curpro = (int) (100 * blueStep / Float.valueOf(prefs.getPedometerTarget()));
                if (curpro > 100) {
                    curpro = 100;
                }
                Log.i(tag, "setProgress:1 " + curpro);
                curprogress.setProgress(curpro);
                if (curpro > 50) {
                    caliriesinfo.setGravity(Gravity.LEFT);
                    distanceinfo.setGravity(Gravity.RIGHT);
                } else {
                    caliriesinfo.setGravity(Gravity.RIGHT);
                    distanceinfo.setGravity(Gravity.LEFT);
                }
            }
        } else {
            Log.e(tag, "最后一个不是今天 ");
        }
    }

    /**
     * 圆形进度条监听
     *
     * @param blueStep2
     */
    private void updataTodayView(int blueStep2) {
        // TODO Auto-generated method stub
        StepPageDataView cur = sparseArray.get(pedometerlist.size() - 1);
        if (cur != null) {
            Log.e(tag, " sparseArray get " + (int) (pedometerlist.size() - 1));
            cur.setCurStep(blueStep2);// 当日步数更新
            cur.postInvalidate();// 刷新view
        } else {
            Log.e(tag, "sparseArray get NULL");
        }
        saveTodayToDataSp(blueStep, (float) calculateActiveMile(blueStep), blueCalories, Float.valueOf(decimalFormat.format(blueStep * 500 / 3600000f)));
    }

    /**
     * 更新非蓝牙数据
     */
    @SuppressLint("NewApi")
    protected void updateViewFromView() {
        PedometerItem.Pedometer.PedometerData object = pedometerlist.get(pedometerlist.size() - 1);
        if (object.date.equals("今天")) {
            object.calorie = (decimalFormat.format(Calories));
            object.distance = (decimalFormat.format(Miles));
            object.step = (curStep + "");
            object.total_time = ((Seconds) + "");
            String strCalories = StringUtils.calorie2Text(Calories);
            object.calorie_text = (strCalories);
            strMile = StringUtils.distance2Text(Miles);
            object.distance_text = (strMile);
            if (curPageIndex == pedometerlist.size() - 1) {
                Log.i(TAG, "TodayStep sp : " + "代表用户摇晃手机" + curStep);
                pageAdapter.notifyDataSetChanged();
                updataTodayView(curStep);
                tv_caliries.setText(decimalFormat.format((float) Calories));
                tv_mileage.setText(decimalFormat.format(Miles));
                tv_motiontimes.setText(decimalFormat.format(Seconds));
                tv_motiontimes_unit.setVisibility(View.VISIBLE);
                caliriesinfo.setText(strCalories);
                distanceinfo.setText(strMile);
                int curpro = (int) (100 * curStep / Float.valueOf(prefs.getPedometerTarget()));
                if (curpro > 100) {
                    curpro = 100;
                }
                Log.i(tag, "setProgress: 4" + curpro);
                curprogress.setProgress(curpro);
                if (curpro > 50) {
                    caliriesinfo.setGravity(Gravity.LEFT);
                    distanceinfo.setGravity(Gravity.RIGHT);
                } else {
                    caliriesinfo.setGravity(Gravity.RIGHT);
                    distanceinfo.setGravity(Gravity.LEFT);
                }
            }
        } else {
            Log.e(tag, "最后一个不是今天 ");
        }

    }


    /**
     * 卡路里
     */
    private double mCalories = 0;
    private static double METRIC_RUNNING_FACTOR = 1.02784823;
    private static double METRIC_WALKING_FACTOR = 0.708;

    /**
     * 监听运动卡路里
     */
    public double calculateActiveCalory(int step) {
        mCalories = +step * (Float.parseFloat(prefs.getWeight()) * (false ? METRIC_RUNNING_FACTOR : METRIC_WALKING_FACTOR)) * Float.parseFloat(prefs.getUserStepLeight()) / 100000.0;
        return mCalories;
    }

    /**
     * 运动距离
     */
    double mDistance = 0;

    /**
     * 监听运动距离
     */
    public double calculateActiveMile(int step) {

        mDistance = +step * Float.parseFloat(prefs.getUserStepLeight()) / 100000.0;
        return mDistance;
    }


    void unregisterReceiver() {
        getActivity().unregisterReceiver(stepReceiver);
    }

    private void registerDeviceReceiver() {
        // TODO Auto-generated method stub
        IntentFilter filter = new IntentFilter();
        // 自拍广播
        filter.addAction("com.ACTION_SELFIE_DATA");
        // 连接广播
        filter.addAction("com.WRISTBAND_CONNECT");
        // 断开广播
        filter.addAction("com.WRISTBAND_DISCONNECT");
        // 找手机
        filter.addAction("com.ACTION_FIND_PHONE");
        // 简单的后台扫描广播，用来检测手环和手机蓝牙的连接状态，15秒钟发一次广播状态。广播在timeSerivce.java种发出的
        filter.addAction("COM.ACTION.TIMESERVICE");
        mContext.registerReceiver(mBlueDeviceReceiver, filter);
    }

    private void unregisterDeviceReceiver() {
        // TODO Auto-generated method stub
        mContext.unregisterReceiver(mBlueDeviceReceiver);
    }

    private BroadcastReceiver mBlueDeviceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(tag, "onReceive: " + intent.getAction());
            if ("com.ACTION_SELFIE_DATA".equalsIgnoreCase(intent.getAction())) {
                showToast("自拍开启");
            } else if ("com.WRISTBAND_CONNECT".equals(intent.getAction())) {
                Log.i(tagBlue, "WRISTBAND_CONNECT");
                if (prefs.getBlueConn()) {
                    wrist_status.setText("已连接");
                } else {
                    wrist_status.setText("未连接");
                }
                mHandler.sendEmptyMessage(SendDeviceTimeSync);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(SendDeviceDaily);
                    }
                }, 10 * 1000);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(SendDevice5Mins);
                    }
                }, 40 * 1000);
            } else if ("com.WRISTBAND_DISCONNECT".equals(intent.getAction())) {
                wrist_status.setText("未连接");
                Log.i(tagBlue, "WRISTBAND_DISCONNECT");
            } else if ("com.ACTION_FIND_PHONE".equals(intent.getAction())) {
            } else if ("COM.ACTION.TIMESERVICE".equals(intent.getAction())) {
            }
        }
    };

    // 广播同步手环数据 0x29
    // void syncConnSyncData(String Year, String updataTodayView) {
    void syncConnSyncData(String data29) {
        if (TextUtils.isEmpty(data29)) {
            showToast("请等待....");
            mContext.sendBroadcast(new Intent(Preferences.ActionBlueSysn)); // 同步蓝牙数据请求
            return;
        }
        DeviceDailyData dailySport = CurrData_0x29_Convert_DailyData(data29);
        try {
            int Year = dailySport.getYear();
            if (Year == 255) {
                blueStep = dailySport.getSteps();
                updataTodayView(dailySport.getSteps());
                blueCalories = (float) dailySport.getCalorie();
                blueMiles = (float) calculateActiveMile(blueStep);
                blueSeconds = Float.valueOf(decimalFormat.format(blueStep * 500 / 3600000f));
                if (prefs.getBlueConn()) {
                    wrist_status.setText("已连接");
                } else {
                    wrist_status.setText("未连接");
                }
                LogUtil.e(TAG, "2 .--> Year>" + "blueStep" + blueStep + "blueMiles" + blueMiles + "blueCalories" + blueCalories + "blueSeconds" + blueSeconds);
                if (blueStep != wristData.getS()) {
                    wristData.setC(blueCalories);
                    wristData.setD((float) calculateActiveMile(blueStep));
                    wristData.setS(blueStep);
                    wristData.setT(blueSeconds);
                    saveWristDataToSp();
                }
                saveTodayToDataSp(blueStep, (float) calculateActiveMile(blueStep), blueCalories, blueSeconds);
                // float bStep,float bMiles,float bCalor,float bSecondes
            } else {
                showToast("请等待....");
            }
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            LogUtil.e(TAG, "syncConnSyncData>>NullPointerException");
            // e.printStackTrace();
        }

    }

    // TODO 同步903
    void sync903Data(String bStep, String bCalories, String bDistance, String bSeconds) {
        blueStep = Integer.valueOf(bStep);
        updataTodayView(blueStep);
        blueCalories = Float.valueOf(bCalories);
        blueMiles = (float) calculateActiveMile(blueStep);
        blueSeconds = Float.valueOf(decimalFormat.format(blueStep * 500 / 3600000f));
        LogUtil.e(TAG, "2 .--> Year>" + "blueStep" + blueStep + "blueMiles" + blueMiles + "blueCalories" + blueCalories + "blueSeconds" + blueSeconds);
        if (blueStep != wristData.getS()) {
            wristData.setC(blueCalories);
            wristData.setD(blueMiles);
            wristData.setS(blueStep);
            wristData.setT(blueSeconds);
            saveWristDataToSp();
        }
        /**
         * 存储蓝牙903数据
         */
        saveTodayToDataSp(blueStep, (float) calculateActiveMile(blueStep), blueCalories, blueSeconds);
        updateViewByBlueData();
    }

    /**
     * 是否已经发送同步
     */
    public boolean isSendSync = false;


    public DeviceDailyData CurrData_0x29_Convert_DailyData(String json) {
        DeviceDailyData dailyData = new DeviceDailyData();
        JSONObject jobject;
        try {
            jobject = JSON.parseObject(json);
        } catch (Exception e) {
            return null;
        }
        dailyData.setYear(jobject.getIntValue("year"));
        dailyData.setMonth(jobject.getIntValue("month"));
        dailyData.setDay(jobject.getIntValue("day"));
        dailyData.setSteps(jobject.getIntValue("sportSteps"));
        dailyData.setDistance(jobject.getFloatValue("sportDistances/1000"));
        dailyData.setCalorie(jobject.getFloatValue("sportCalories"));
        dailyData.setBcc(jobject.getIntValue("uid"));
        dailyData.set_converted(jobject.getIntValue("sportType"));
        dailyData.set_uploaded(jobject.getIntValue("count"));
        saveTodayToDataSp(dailyData.getSteps(), (float) calculateActiveMile(dailyData.getSteps()), (float) dailyData.getCalorie(), Float.valueOf(decimalFormat.format(dailyData.getSteps() * 500 / 3600000f)));
        return dailyData;
    }

    /**
     * 蓝牙连接回调
     *
     * @param b 是否连接
     * @param i 1 连接过程中断开，2 连接设备超时， 3 连接状态下断开连接；4 蓝牙开关关闭；6 主动断开连接
     */
    @Override
    public void isConnected(boolean b, int i) {


    }

    /**
     * 蓝牙扫描结果回调
     *
     * @param s  MAC地址 产品序列号
     * @param s1 SSID 设备广播名 F4T
     */
    @Override
    public void onScanResult(String s, String s1) {
        Log.d("sampleBLE", "code" + s1);
    }

    /**
     * 数据同步过程数据流
     *
     * @param i  各个状态的步数
     * @param i1 9 静止；10 走路；11 跑步
     * @param l  开始时间戳
     * @param l1 结束时间戳
     * @param i2 同步过程百分比
     */
    @Override
    public void onSyncStepData(int i, int i1, long l, long l1, int i2) {
        showToast("请等待....");
        /**
         * 代码走到这步证明已经连接成功 设置标示为true
         */
        prefs.saveBlueConn(true);
        Log.d("sampleBLE", "onSyncStepData");
    }

    /**
     * 同步完成
     *
     * @param i  硬件设备版本号
     * @param i1 当前电池电量
     */
    @Override
    public void onSyncFinished(int i, int i1) {
        /**
         * getDaySportsData
         * 0 当天数据 1 昨天 2 前天 一次类推
         * 设备MAC地址
         * 身高
         * 体重
         */
        Log.d("sampleBLE", "sync finished, version " + i + ", battery " + i1);
        DaySportsData data = bluetoothLeManager.getDaySportsData(0, prefs.getMacAddress(), 170, 65);
        if (data == null) {
            Log.d("sampleBLE", "no sports data!");
        } else {
            Log.d("sampleBLE", "steps: " + data.totalSteps + ", distance " + data.totalDistance + ", time " + data.totalTime + ", calories " + data.totalCalories);
            saveTodayToDataSp(data.totalSteps, (data.totalDistance / 1000f), data.totalCalories, (data.totalTime / 3600f));
            mContext.sendBroadcast(new Intent(Preferences.HOME_STEP_UPDATE));
        }
        mContext.sendBroadcast(new Intent("com.WRISTBAND_CONNECT"));
    }

    /**
     * 硬件升级回调
     *
     * @param l  当前升级的文件位置
     * @param l1 总文件大小
     */
    @Override
    public void onOTAProgress(long l, long l1) {


    }

    /**
     * 硬件升级完成
     */
    @Override
    public void onOTAFinished() {

    }

    /**
     * 硬件升级失败
     *
     * @param i 一般为0的错误码
     */
    @Override
    public void onOTAError(int i) {

    }

}
