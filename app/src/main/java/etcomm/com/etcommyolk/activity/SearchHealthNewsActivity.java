package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.adapter.HealthAdapter;
import etcomm.com.etcommyolk.entity.NewsList;
import etcomm.com.etcommyolk.entity.RecommendItems;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.NewsListHandler;
import etcomm.com.etcommyolk.utils.GlobalSetting;
import etcomm.com.etcommyolk.widget.DownPullRefreshListView;
import etcomm.com.etcommyolk.widget.ExEditText;
import etcomm.com.etcommyolk.widget.ProgressDialog;

public class SearchHealthNewsActivity extends Activity {


    @Bind(R.id.search_healthnew_et)
    ExEditText searchHealthnewEt;
    @Bind(R.id.cancel)
    TextView cancel;
    @Bind(R.id.pulllistview)
    DownPullRefreshListView listView;
    public int page_size = 10;
    public int page_number = 1;
    public AbsListView.OnScrollListener loadMoreListener;
    public boolean loadMore;
    public boolean loadStatus = false;
    public View footer;
    public ProgressBar loadingProgressBar;
    public TextView loadingText;
    public ProgressDialog mProgress;
    private ArrayList<RecommendItems> adaptList = new ArrayList<>();
    protected ArrayList<RecommendItems> list = new ArrayList<RecommendItems>();
    private HealthAdapter mAdapter;
    private Context mContext;
    public static final int MSG_CLOSE_PROGRESS = 1;
    public static final int MSG_SHOW_TOAST = 2;
    String searchType = "0";//0为搜索全部，1为搜索收藏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_health_news);
        ButterKnife.bind(this);
        EtcommApplication.addActivity(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mContext = this;
        footer = View.inflate(this, R.layout.loadmore, null);
        loadingProgressBar = (ProgressBar) footer.findViewById(R.id.progressBar);
        loadingText = (TextView) footer.findViewById(R.id.title);
        initData();

    }

    public void initData() {
        Intent intent = getIntent();
        if (intent.getStringExtra("type") != "") {
            searchType = intent.getStringExtra("type");
        }
        searchType = intent.getStringExtra("type");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchHealthnewEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    page_number = 1;
                    adaptList.clear();
                    getList();

                }
                return false;
            }
        });
        mAdapter = new HealthAdapter(mContext, adaptList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecommendItems m = mAdapter.getItem(position - 1);
                Bundle extras = new Bundle();
                extras.putSerializable("RecommendItems", m);
                Intent intent = new Intent(mContext, WebviewDetailActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
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
        listView.setOnRefreshListener(new DownPullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (listView.getFooterViewsCount() > 0) {
                    listView.removeFooterView(footer);
                }
                page_number = 1;
                adaptList.clear();
                getList();
            }
        });


    }

    @OnClick({R.id.search_healthnew_et, R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_healthnew_et:
                break;
            case R.id.cancel:
                break;
        }
    }

    public void getList() {
        RequestParams params = new RequestParams();
        params.put("access_token", GlobalSetting.getInstance(mContext).getAccessToken());
        params.put("page", (page_number++) + "");
        params.put("page_size", page_size + "");
        params.put("keyword", searchHealthnewEt.getText().toString());
        params.put("type", searchType);
        Log.d("", "getFindHome: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        ApiClient.getInstance().GetNewsSearch(mContext, params, new NewsListHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(NewsList findList) {
                super.onSuccess(findList);
                cancelmDialog();
                list = findList.content.items;
                if (list.size() > 0) {
                    if (listView.getFooterViewsCount() == 0 && Integer.parseInt(findList.content.pages) > 1) {
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
                cancelmDialog();
                listView.onRefreshComplete();
                loadStatus = false;
                loadingProgressBar.setVisibility(View.GONE);
            }
        });

    }

    public void showProgress(int resId, boolean cancel) {
        mProgress = new ProgressDialog(this);
        if (resId <= 0) {
            mProgress.setMessage(R.string.loading_data, cancel);
        } else {
            mProgress.setMessage(resId, cancel);
        }
        mProgress.show();
    }

    public void cancelmDialog() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    public Handler baseHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_CLOSE_PROGRESS:
                    // cancelProgress(); //加载进度条
                    break;
                case MSG_SHOW_TOAST:
                    int resid = msg.arg1;
                    if (resid > 0) {
                        showToast(resid);
                    } else if (msg.obj != null) {
                        String mes = (String) msg.obj;
                        if (!TextUtils.isEmpty(mes)) {
                            showToast(mes);
                        }
                    }
                    break;
            }
        }

        ;
    };
    Toast toast = null;

    protected void showToast(String message) {
        if (toast != null) {
            return;
        }
        toast = Toast.makeText(mContext, (!TextUtils.isEmpty(message)) ? message
                : this.getString(R.string.network_error), Toast.LENGTH_SHORT);
        toast.show();
        baseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
            }
        }, 2000);
    }

    protected void showToast(int resid) {
        if (toast != null) {
            return;
        }
        toast = Toast.makeText(mContext, resid, Toast.LENGTH_SHORT);
        toast.show();
        baseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
            }
        }, 2000);
    }
}
