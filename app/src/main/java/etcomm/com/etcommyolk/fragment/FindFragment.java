package etcomm.com.etcommyolk.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.OnClick;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.activity.AboutUsActivity;
import etcomm.com.etcommyolk.activity.ExamineReportActivity;
import etcomm.com.etcommyolk.activity.MainActivity;
import etcomm.com.etcommyolk.activity.MineActivity;
import etcomm.com.etcommyolk.activity.MoreHealthActivity;
import etcomm.com.etcommyolk.activity.MoreSportsActivity;
import etcomm.com.etcommyolk.activity.MoreWealfeActivity;
import etcomm.com.etcommyolk.activity.MsgListActivity;
import etcomm.com.etcommyolk.activity.PointsExchangeActivity;
import etcomm.com.etcommyolk.activity.WebviewDetailActivity;
import etcomm.com.etcommyolk.adapter.SportsAdapter;
import etcomm.com.etcommyolk.entity.DaySignUp;
import etcomm.com.etcommyolk.entity.FindHome;
import etcomm.com.etcommyolk.entity.FindList;
import etcomm.com.etcommyolk.entity.FirstEvent;
import etcomm.com.etcommyolk.entity.RecommendItems;
import etcomm.com.etcommyolk.entity.UpdateObj;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.DaySignUpHandler;
import etcomm.com.etcommyolk.handler.FindHomeHandler;
import etcomm.com.etcommyolk.handler.FindListHandler;
import etcomm.com.etcommyolk.handler.UpdateObjHandler;
import etcomm.com.etcommyolk.utils.StringUtils;
import etcomm.com.etcommyolk.utils.UpdateCheckUtils;
import etcomm.com.etcommyolk.widget.DialogFactory;
import etcomm.com.etcommyolk.widget.DownPullRefreshListView;
import etcomm.com.etcommyolk.widget.SlideADView;

public class FindFragment extends BaseFragment implements View.OnClickListener {
    /**
     * 控制OnResume()执行次数
     */
    public static boolean limitResumeFind;
    ImageView baseLeft;
    ImageView baseRight;
    TextView baseTitle;
    RelativeLayout title;

    //以下为头布局内容
    DownPullRefreshListView listView;
    SlideADView slideADView;
    ImageView exchange;
    ImageView group;
    TextView tvMoreActivity;
    RelativeLayout header1;
    SimpleDraweeView activityImage;
    TextView suggestActivityTopic;
    TextView participation;
    TextView daterange;
    TextView activityStatus;
    TextView tvMoreHealth;
    RelativeLayout header2;
    SimpleDraweeView healthImage;
    TextView healthtopic;
    TextView readingamount;
    TextView tvMoreWalfe;
    RelativeLayout header3;
    SimpleDraweeView welfareImage;
    TextView welfaretopic;
    TextView tvTime;
    RelativeLayout activity, health, walfe;


    private Context mContext;
    private int AppMudle = 0;//爱康显示标识
    private ArrayList<RecommendItems> adaptList = new ArrayList<>();
    protected ArrayList<RecommendItems> list = new ArrayList<RecommendItems>();
    private SportsAdapter mAdapter;
    private View header;
    RecommendItems recommendactivity;
    RecommendItems recommendhealth;
    RecommendItems recommendwelfare;
    int page = 2;

    /**
     * onCreateView
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    protected void initView(View view, Bundle savedInstanceState) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_find;
    }

    @Override
    public void receive_msg_data() {
        baseRight.setImageResource(R.mipmap.icon_msg_unread);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        if (!prefs.getsignin().equals("1")) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    signin();
                }
            }, 1000);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        limitResumeFind = true;
        if (limitResumeFind) {
            page = 2;
            adaptList.clear();
            list.clear();
            getFindHome();
        }
        if (prefs.getHaveReceiveUnReadData()) {
            baseRight.setImageResource(R.mipmap.icon_msg_unread);
        } else {
            baseRight.setImageResource(R.mipmap.ic_messege);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        baseLeft = (ImageView) rootView.findViewById(R.id.base_left);
        baseRight = (ImageView) rootView.findViewById(R.id.base_right);
        baseTitle = (TextView) rootView.findViewById(R.id.base_title);
        listView = (DownPullRefreshListView) rootView.findViewById(R.id.listview);
        //以下为头布局内容(涉及两部分layout，不能使用butterknife)
        header = View.inflate(getActivity(), R.layout.find_header, null);
        health = (RelativeLayout) header.findViewById(R.id.health);
        activity = (RelativeLayout) header.findViewById(R.id.activity);
        walfe = (RelativeLayout) header.findViewById(R.id.welfare);
        slideADView = (SlideADView) header.findViewById(R.id.slideADView);
        exchange = (ImageView) header.findViewById(R.id.exchange);
        group = (ImageView) header.findViewById(R.id.group);
        tvMoreActivity = (TextView) header.findViewById(R.id.tv_more_activity);
        header1 = (RelativeLayout) header.findViewById(R.id.header1);
        activityImage = (SimpleDraweeView) header.findViewById(R.id.activity_image);
        suggestActivityTopic = (TextView) header.findViewById(R.id.suggest_activity_topic);
        participation = (TextView) header.findViewById(R.id.participation);
        daterange = (TextView) header.findViewById(R.id.daterange);
        activityStatus = (TextView) header.findViewById(R.id.activity_status);
        tvMoreHealth = (TextView) header.findViewById(R.id.tv_more_health);
        header2 = (RelativeLayout) header.findViewById(R.id.header2);
        healthImage = (SimpleDraweeView) header.findViewById(R.id.health_image);
        healthtopic = (TextView) header.findViewById(R.id.healthtopic);
        readingamount = (TextView) header.findViewById(R.id.readingamount);
        tvMoreWalfe = (TextView) header.findViewById(R.id.tv_more_walfe);
        header3 = (RelativeLayout) header.findViewById(R.id.header3);
        welfareImage = (SimpleDraweeView) header.findViewById(R.id.welfare_image);
        welfaretopic = (TextView) header.findViewById(R.id.welfaretopic);
        tvTime = (TextView) header.findViewById(R.id.tv_time);
        mAdapter = new SportsAdapter(mContext, adaptList, 0);
        listView.setAdapter(mAdapter);
        exchange.setOnClickListener(this);
        group.setOnClickListener(this);
        tvMoreActivity.setOnClickListener(this);
        tvMoreHealth.setOnClickListener(this);
        tvMoreWalfe.setOnClickListener(this);
        baseLeft.setOnClickListener(this);
        baseRight.setOnClickListener(this);
        activityImage.setOnClickListener(this);
        healthImage.setOnClickListener(this);
        welfareImage.setOnClickListener(this);
        //点击角布局加载更多
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loadStatus && list.size() != 0) {
                    getList();
                }
            }
        });

        //上拉listview加载更多监听
        loadMoreListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (loadMore && !loadStatus) {
                        getList();
                    }
                    if (list.size() == 0) {
                        listView.removeFooterView(footer);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                listView.setFirstItemIndex(firstVisibleItem);
                if (firstVisibleItem != 1 && list.size() != 0) {
                    loadMore = firstVisibleItem + visibleItemCount == totalItemCount;
                } else {
                    loadMore = false;
                }
            }
        };
        listView.setOnScrollListener(loadMoreListener);
        //listviw下拉刷新
        listView.setOnRefreshListener(new DownPullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (listView.getFooterViewsCount() > 0) {
                    listView.removeFooterView(footer);
                }
                //下拉时候刷新整个页面接口，并将列表的页面置为2
                page = 2;
                adaptList.clear();
                getFindHome();
            }
        });

        //条目点击进入webview详情
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecommendItems recommendItems = mAdapter.getItem(position - 2);
                Intent intent = new Intent(mContext, WebviewDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("RecommendItems", recommendItems);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        checkUpdate(getVersion());
        return rootView;
    }

    @OnClick({R.id.exchange, R.id.group, R.id.tv_more_activity, R.id.tv_more_health, R.id.tv_more_walfe, R.id.base_left, R.id.base_right, R.id.activity_image, R.id.health_image, R.id.welfare_image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exchange:
                startActivity(new Intent(getBaseActivity(), PointsExchangeActivity.class));
                break;
            case R.id.group:
                if (AppMudle == 0) {
                    EventBus.getDefault().post(
                            new FirstEvent("start"));
                } else {
                    startActivity(new Intent(mContext, ExamineReportActivity.class));
                }
                break;
            case R.id.tv_more_activity:
                startActivity(new Intent(mContext, MoreSportsActivity.class));
                break;
            case R.id.tv_more_health:
                startActivity(new Intent(mContext, MoreHealthActivity.class));
                break;
            case R.id.tv_more_walfe:
                startActivity(new Intent(mContext, MoreWealfeActivity.class));
                break;
            case R.id.base_left:
                startActivity(new Intent(mContext, MineActivity.class));
                break;
            case R.id.base_right:
                startActivity(new Intent(mContext, MsgListActivity.class));
                break;
            case R.id.activity_image:
                if (!recommendactivity.detail_url.isEmpty()) {
                    Intent intent = new Intent(mContext, WebviewDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("RecommendItems", recommendactivity);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
                break;
            case R.id.health_image:

                if (!recommendhealth.detail_url.isEmpty()) {
                    Intent intent = new Intent(mContext, WebviewDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("RecommendItems", recommendhealth);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
                break;
            case R.id.welfare_image:
                if (!recommendwelfare.detail_url.isEmpty()) {
                    Intent intent = new Intent(mContext, WebviewDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("RecommendItems", recommendwelfare);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
                break;

        }
    }

    /**
     * 获取首页数据：轮播+企业module+推荐条目+list部分条目
     */
    public void getFindHome() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        Log.d("", "getFindHome: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        client.GetFindHome(mContext, params, new FindHomeHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(FindHome findHome) {
                super.onSuccess(findHome);
                cancelmDialog();
                for (RecommendItems items : findHome.content.recommend) {
                    if (items.type.equals("activity")) {
                        //活动相关信息
                        recommendactivity = items;
                        activityImage.setImageURI(items.image);
                        activityStatus.setText(items.status);
                        participation.setText(items.user_num + "人参与");
                        daterange.setText(items.start_at + "——" + items.end_at);
                        suggestActivityTopic.setText(items.title);
                    }
                    if (items.type.equals("health")) {
                        //资讯阅读相关信息填充
                        recommendhealth = items;
                        healthImage.setImageURI(items.image);
                        healthtopic.setText(items.title);
                        readingamount.setText(items.pv);
                    }
                    if (items.type.equals("welfare")) {
                        //福利相关信息
                        recommendwelfare = items;
                        welfareImage.setImageURI(items.image);
                        welfaretopic.setText(items.title);
                        tvTime.setText(items.start_at + "——" + items.end_at);
                    }
                }
                //判断条目是否有数据
                if (recommendhealth == null) {
                    health.setVisibility(View.GONE);
                } else {
                    health.setVisibility(View.VISIBLE);
                }
                if (recommendactivity == null) {
                    activity.setVisibility(View.GONE);
                } else {
                    activity.setVisibility(View.VISIBLE);
                }
                if (recommendwelfare == null) {
                    walfe.setVisibility(View.GONE);
                } else {
                    walfe.setVisibility(View.VISIBLE);
                }
                //是否显示爱康
                if (findHome.content.app_module.get(0).name.equals("爱康")) {
                    AppMudle = 1;
                    group.setImageResource(R.mipmap.ic_aikang);
                }
                //广告轮播相关代码
                ArrayList<RecommendItems> carousel = findHome.content.carousel;
                //计算广告图高度
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                int hight = display.getWidth() * 355 / 960;
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) slideADView.getLayoutParams();
                lp.height = hight;
                slideADView.setLayoutParams(lp);
                slideADView.setImageResources(carousel, mSlideADViewListener);

                //三个推荐下面的列表
                list = findHome.content.activity;
                if (listView.getHeaderViewsCount() < 2) {
                    listView.addHeaderView(header);
                }
                if (list.size() > 0) {
                    if (listView.getFooterViewsCount() == 0) {
                        listView.addFooterView(footer);
                        listView.setAdapter(mAdapter);
                    }
                    if (listView.getHeaderViewsCount() < 2) {
                        listView.addHeaderView(header);
                    }
                    for (Iterator<RecommendItems> iterator = list.iterator(); iterator.hasNext(); ) {
                        RecommendItems disscussCommentItems = iterator.next();
                        adaptList.add(disscussCommentItems);
                    }
                    mAdapter.notifyDataSetChanged();

                } else {
                    showToast("已无更多内容");
                    if (listView.getFooterViewsCount() > 0) {
                        listView.removeFooterView(footer);
                    }
                }
                loadStatus = false;
                listView.onRefreshComplete();
                loadingProgressBar.setVisibility(View.GONE);
                loadingText.setText(getResources().getString(R.string.loadmore));
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
                listView.onRefreshComplete();
                loadStatus = false;
                loadingProgressBar.setVisibility(View.GONE);
            }
        });


    }

    /**
     * list条目加载更多调用
     */
    public void getList() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("page", (page++) + "");
        Log.d("", "getFindHome: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        client.GetFindList(mContext, params, new FindListHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(FindList findList) {
                super.onSuccess(findList);
                cancelmDialog();
                list = findList.content.activity;
                if (list.size() > 0) {
                    if (listView.getFooterViewsCount() == 0 && Integer.parseInt(findList.content.pages) > 0) {
                        listView.addFooterView(footer);
                        listView.setAdapter(mAdapter);
                    }
                    for (Iterator<RecommendItems> iterator = list.iterator(); iterator.hasNext(); ) {
                        RecommendItems disscussCommentItems = iterator.next();
                        adaptList.add(disscussCommentItems);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    showToast("已无更多内容");
                    if (listView.getFooterViewsCount() > 0) {
                        listView.removeFooterView(footer);
                    }

                }
                loadStatus = false;
                listView.onRefreshComplete();
                loadingProgressBar.setVisibility(View.GONE);
                loadingText.setText(getResources().getString(R.string.loadmore));
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                listView.onRefreshComplete();
                loadStatus = false;
                loadingProgressBar.setVisibility(View.GONE);
            }
        });

    }

    private SlideADView.SlideADViewListener mSlideADViewListener = new SlideADView.SlideADViewListener() {
        @Override
        public void onImageClick(final RecommendItems ad, View imageView) {
            if (TextUtils.isEmpty(ad.detail_url)) {
                Toast.makeText(mContext, "链接地址无效：" + ad.detail_url, Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(mContext, WebviewDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("RecommendItems", ad);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        }

        @Override
        public void displayImage(RecommendItems ad, SimpleDraweeView imageView) {
            imageView.setImageURI(ad.image);
        }
    };


    //每日签到
    protected void signin() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        client.toSignIn(getActivity(), params, new DaySignUpHandler() {

            @Override
            public void onSuccess(DaySignUp daySignUp) {
                super.onSuccess(daySignUp);
                prefs.setScore(daySignUp.content.score);
                showSignedDialoag(daySignUp.content.day, daySignUp.content.score);
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
            }

        });
        prefs.savesignin("1");
    }


    //版本更新
    private void checkUpdate(String version) {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("version", version);
        cancelmDialog();
        showProgress(0, true);
        client.toUploadversion(getActivity(), params, new UpdateObjHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }


            @Override
            public void onSuccess(UpdateObj commen) {
                super.onSuccess(commen);
                cancelmDialog();
                int code = commen.code;
                if (code == 45000) {
                    UpdateCheckUtils.getInstanse().lookVersion(getActivity(), true, commen);
                }
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
    }

    /**
     * 根据版本号返回new long[3]。用于比较版本新旧。
     *
     * @param version
     * @return
     */
    public Long[] stringVersionToLong(String version) {
        Long[] longVersion = new Long[2];
        try {
            if (!StringUtils.isEmpty(version)) {
                String[] temp = version.split("\\.");
                if (temp != null && temp.length == 2) {
                    longVersion[0] = Long.parseLong(temp[0]);
                    longVersion[1] = Long.parseLong(temp[1]);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return longVersion;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static final long SigninDialogDismissTime = 1500;

    protected void showSignedDialoag(String days, String score) {
        final Dialog signinDialog = DialogFactory.getDialogFactory().showSignedDialog(mContext, days, score);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                signinDialog.dismiss();
            }
        }, SigninDialogDismissTime);
    }

    static final int IS_TODAY = 0x09;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case IS_TODAY:
//                    signin();
                default:
                    break;
            }
        }
    };
}
