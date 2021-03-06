package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.adapter.ShareGroupListAdapter;
import etcomm.com.etcommyolk.entity.GroupItems;
import etcomm.com.etcommyolk.entity.GroupList;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.GroupListHandler;
import etcomm.com.etcommyolk.utils.GlobalSetting;
import etcomm.com.etcommyolk.widget.DownPullRefreshListView;
import etcomm.com.etcommyolk.widget.ExEditText;
import etcomm.com.etcommyolk.widget.ProgressDialog;

public class SharetoGroupActivity extends Activity {

    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.search_topic_et)
    ExEditText searchHealthnewEt;
    @Bind(R.id.listview)
    DownPullRefreshListView listView;


    private Context mContext;
    public int page_size = 10;
    public int page_number = 1;
    public AbsListView.OnScrollListener loadMoreListener;
    public boolean loadMore;
    public boolean loadStatus = false;
    public View footer;
    public ProgressBar loadingProgressBar;
    public TextView loadingText;
    public ProgressDialog mProgress;
    private ArrayList<GroupItems> adaptList = new ArrayList<>();
    protected ArrayList<GroupItems> list = new ArrayList<GroupItems>();
    private ShareGroupListAdapter mAdapter;

    Intent intent;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals("finish")) {
                    SharetoGroupActivity.this.finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shareto_group);
        ButterKnife.bind(this);
        EtcommApplication.addActivity(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("finish");
        registerReceiver(receiver, filter);
        mContext = this;
        intent = getIntent();
        listView = (DownPullRefreshListView) findViewById(R.id.listview);
        back = (ImageView) findViewById(R.id.back);
        searchHealthnewEt = (ExEditText) findViewById(R.id.search_topic_et);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        footer = View.inflate(this, R.layout.loadmore, null);
        loadingProgressBar = (ProgressBar) footer.findViewById(R.id.progressBar);
        loadingText = (TextView) footer.findViewById(R.id.title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();

    }


    public void initData() {
        getList();
        //点击键盘的搜索符号：搜小组
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

        mAdapter = new ShareGroupListAdapter(mContext, adaptList, intent);
        listView.setAdapter(mAdapter);
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


    /**
     * 获取用户下面所有的小组，包括已关注和未关注
     */
    public void getList() {
        RequestParams params = new RequestParams();
        params.put("access_token", GlobalSetting.getInstance(mContext).getAccessToken());
        params.put("page", (page_number++) + "");
        params.put("page_size", page_size + "");
        params.put("remove_id", 0 + "");
        if (searchHealthnewEt.getText().toString() != "" && !searchHealthnewEt.getText().toString().isEmpty()) {
            //获取所有小组下面包含关键字的小组
            params.put("keyword", searchHealthnewEt.getText().toString());
        }
        //获取用户下面的全部小组
        params.put("type", 1 + "");//2为搜索全部，1为已关注
        //如果是小组分享到小组，去除当前小组
        if (intent.getStringExtra("type") != null) {
            if (intent.getStringExtra("type").equals("topic")) {
                params.put("remove_id", intent.getStringExtra("topic_id"));
            }
        }

        Log.d("", "getList: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        ApiClient.getInstance().GetGroupList(mContext, params, new GroupListHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
                listView.onRefreshComplete();
                loadStatus = false;
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(GroupList groupList) {
                super.onSuccess(groupList);
                cancelmDialog();
                list = groupList.content.items;
                if (list.size() > 0) {
                    if (listView.getFooterViewsCount() == 0 && Integer.parseInt(groupList.content.pages) > 1) {
                        listView.addFooterView(footer);
                        listView.setAdapter(mAdapter);
                    }
                    for (Iterator<GroupItems> iterator = list.iterator(); iterator.hasNext(); ) {
                        GroupItems disscussCommentItems = iterator.next();
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

    public static final int MSG_CLOSE_PROGRESS = 1;
    public static final int MSG_SHOW_TOAST = 2;
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


    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchHealthnewEt, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(searchHealthnewEt.getWindowToken(), 0); //退出app时强制隐藏键盘
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
}
