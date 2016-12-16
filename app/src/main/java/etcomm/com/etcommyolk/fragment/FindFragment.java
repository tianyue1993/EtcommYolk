package etcomm.com.etcommyolk.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.activity.AdWebActivity;
import etcomm.com.etcommyolk.activity.PointsExchangeActivity;
import etcomm.com.etcommyolk.adapter.ActivityAdapter;
import etcomm.com.etcommyolk.entity.FindHome;
import etcomm.com.etcommyolk.entity.FindList;
import etcomm.com.etcommyolk.entity.RecommendItems;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.FindHomeHandler;
import etcomm.com.etcommyolk.handler.FindListHandler;
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


    private Context mContext;
    private int AppMudle = 0;//爱康显示标识
    private ArrayList<RecommendItems> adaptList = new ArrayList<>();
    protected ArrayList<RecommendItems> list = new ArrayList<RecommendItems>();
    private ActivityAdapter mAdapter;
    private View header;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
        mAdapter = new ActivityAdapter(mContext, adaptList);
        listView.setAdapter(mAdapter);
        exchange.setOnClickListener(this);
        group.setOnClickListener(this);
        tvMoreActivity.setOnClickListener(this);
        tvMoreHealth.setOnClickListener(this);
        tvMoreWalfe.setOnClickListener(this);
        baseLeft.setOnClickListener(this);
        baseRight.setOnClickListener(this);
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
                page_number = 2;
                adaptList.clear();
                getFindHome();
            }
        });

        getFindHome();
        return rootView;
    }

    @OnClick({R.id.exchange, R.id.group, R.id.tv_more_activity, R.id.tv_more_health, R.id.tv_more_walfe, R.id.base_left, R.id.base_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exchange:
                startActivity(new Intent(getBaseActivity(), PointsExchangeActivity.class));
                break;
            case R.id.group:
                if (AppMudle == 0) {
                    showToast("小组");
                } else {
                    showToast("爱康");
                }
                break;
            case R.id.tv_more_activity:
                break;
            case R.id.tv_more_health:
                break;
            case R.id.tv_more_walfe:
                break;
            case R.id.base_left:
                break;
            case R.id.base_right:
                break;
        }
    }

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
                RecommendItems recommendactivity = findHome.content.recommend.get(0);
                RecommendItems recommendhealth = findHome.content.recommend.get(1);
                RecommendItems recommendwelfare = findHome.content.recommend.get(2);
                //活动相关信息
                activityImage.setImageURI(recommendactivity.image);
                activityStatus.setText(recommendactivity.status);
                participation.setText(recommendactivity.user_num + "人参与");
                daterange.setText(recommendactivity.start_at + "——" + recommendactivity.end_at);
                suggestActivityTopic.setText(recommendactivity.title);
                //资讯阅读相关信息填充
                healthImage.setImageURI(recommendhealth.image);
                healthtopic.setText(recommendhealth.title);
                readingamount.setText(recommendhealth.pv);
                //福利相关信息
                welfareImage.setImageURI(recommendwelfare.image);
                welfaretopic.setText(recommendwelfare.title);
                tvTime.setText(recommendwelfare.start_at + "——" + recommendwelfare.end_at);
                //是否显示爱康
                if (findHome.content.app_module.get(0).name.equals("爱康")) {
                    AppMudle = 1;
                    group.setImageResource(R.mipmap.ic_aikang);
                }
                //广告轮播相关代码
                ArrayList<RecommendItems> carousel = findHome.content.carousel;
                //计算广告图高度
                WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                int hight = display.getWidth() * 355 / 960;
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) slideADView.getLayoutParams();
                lp.height = hight;
                slideADView.setLayoutParams(lp);
                slideADView.setImageResources(carousel, mSlideADViewListener);

                //三个推荐下面的列表
                list = findHome.content.activity;

                if (list.size() > 0) {
                    if (listView.getFooterViewsCount() == 0) {
                        listView.addFooterView(footer);
                        listView.setAdapter(mAdapter);
                    }
                    if (listView.getHeaderViewsCount() < 2) {
                        listView.addHeaderView(header);
                    }
                    adaptList.addAll(list);
                    listView.setAdapter(mAdapter);
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


    public void getList() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("page_number", (page_number++) + "");
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
                    if (listView.getHeaderViewsCount() < 2) {
                        listView.addHeaderView(header);
                    }
                    if (listView.getFooterViewsCount() == 0 && Integer.parseInt(findList.content.pages) > 0) {
                        listView.addFooterView(footer);
                        listView.setAdapter(mAdapter);
                    }
                    adaptList.addAll(list);
                    listView.setAdapter(mAdapter);
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
                Intent intent = new Intent(mContext, AdWebActivity.class);
                mContext.startActivity(intent);
            }
        }

        @Override
        public void displayImage(RecommendItems ad, SimpleDraweeView imageView) {
            imageView.setImageURI(ad.image);
        }
    };
}
