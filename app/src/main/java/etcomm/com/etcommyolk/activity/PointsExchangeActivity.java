package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.PointsExchange;
import etcomm.com.etcommyolk.entity.PointsExchangeItems;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.PointExchangeHandler;
import etcomm.com.etcommyolk.widget.DownPullRefreshListView;

/**
 * 积分兑换
 * 可以兑换物品，显示当前自己的积分，可进入铁 兑换中
 */
public class PointsExchangeActivity extends BaseActivity {

    @Bind(R.id.mypoints)
    TextView mypoints;
    @Bind(R.id.myexchange)
    TextView myexchange;
    @Bind(R.id.refreshlist)
    DownPullRefreshListView refreshlist;
    //    private PointsExchangeListAdapter mAdapter;
    protected ArrayList<PointsExchangeItems> list = new ArrayList<PointsExchangeItems>();
    protected ArrayList<PointsExchangeItems> adaptList = new ArrayList<PointsExchangeItems>();
    protected int page_size = 10;
    protected int page_number = 1;
    private AbsListView.OnScrollListener loadMoreListener;
    private boolean loadMore;
    private boolean loadStatus = false;
    private View footer;
    private ProgressBar loadingProgressBar;
    private TextView loadingText;
    private int totalpoints;

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(tag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointsexchange);
        setTitleTextView("积分兑换", null);
        ButterKnife.bind(this);
        getList();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(tag);
    }

    /**
     * 接口调试，获取兑换列表
     */
    public void getList() {
        JSONObject params = new JSONObject();
        params.put("page_size", page_size + "");
        params.put("page", String.valueOf(page_number++));
        params.put("access_token", "23245538a6de9b824741556172c2d8da");
        try {
            StringEntity entity = new StringEntity(params.toJSONString(), "utf-8");
            client.invokePointExch(mContext, entity, new PointExchangeHandler() {
                @Override
                public void onSuccess(PointsExchange exchange) {
                    super.onSuccess(exchange);
                }

                @Override
                public void onFailure(BaseException exception) {
                    super.onFailure(exception);
                    showToast(exception.getMessage());
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

//    private void getList() {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("access_token", SharePreferencesUtil.getToken(mContext));
//        params.put("page_size", String.valueOf(page_size));
//        params.put("page", String.valueOf(page_number++));
//        cancelmDialog();
//        showProgress(DIALOG_DEFAULT, true);
//        Log.i(tag, "params: " + params.toString());
//        DcareRestClient.volleyGet(Constants.PointsExchangeList, params, new FastJsonHttpResponseHandler() {
//            @Override
//            public void onCancel() {
//                cancelmDialog();
//                super.onCancel();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.e(tag, "OnFailure:" + this.getRequestURI() + " statusCode" + statusCode);
//                cancelmDialog();
//                Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
//                listView.onRefreshComplete();
//                loadStatus = false;
//                loadingProgressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.e(tag, "onSuccess:" + this.getRequestURI() + " statusCode" + statusCode + " response " + response.toString());
//                cancelmDialog();
//                try {
//                    int code = response.getInteger("code");
//                    String message = response.getString("message");
//                    Log.i(tag, "onSuccess  code: " + code + " message: " + message + "content: ");
//                    if (code == 0) {
//                        PointsExchangeContent disscomment = JSON.parseObject(response.getString("content"), PointsExchangeContent.class);
//                        list = disscomment.getModel();
//                        totalpoints = disscomment.getMy_score();
//                        if (!StringUtils.isEmpty(AppSharedPreferencesHelper.getScore()) && Integer.valueOf(AppSharedPreferencesHelper.getScore()) == totalpoints) {
//
//                        } else {
//                            AppSharedPreferencesHelper.setScore(String.valueOf(totalpoints));
//                        }
//                        if (list.size() > 0) {
//                            if (listView.getFooterViewsCount() == 0 && disscomment.getPage_count() > 1) {
//                                listView.addFooterView(footer);
//                                listView.setAdapter(mAdapter);
//                            }
//                            for (Iterator<PointsExchangeItems> iterator = list.iterator(); iterator.hasNext(); ) {
//                                PointsExchangeItems disscussCommentItems = (PointsExchangeItems) iterator.next();
//                                adaptList.add(disscussCommentItems);
//                            }
//                            mAdapter.notifyDataSetChanged();
//                        } else {
//                            showToast("已无更多内容");
//                            if (listView.getFooterViewsCount() > 0) {
//                                listView.removeFooterView(footer);
//                            }
//                        }
//                    } else {// code不为0 发生异常
//                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
//                    }
//                    loadStatus = false;
//                    listView.onRefreshComplete();
//                    loadingProgressBar.setVisibility(View.GONE);
//                    loadingText.setText(getResources().getString(R.string.loadmore));
//                } catch (JSONException e) {
//                    Log.e(tag, "JSONException:");
//                    e.printStackTrace();
//                }
//
//            }
//        });
//    }
//
//    @Override
//    protected Activity atvBind() {
//        return this;
//    }

    protected void initDatas() {
//        getList();
//        footer = View.inflate(mContext, R.layout.loadmore, null);
//        loadingProgressBar = (ProgressBar) footer.findViewById(R.id.progressBar);
//        loadingText = (TextView) footer.findViewById(R.id.title);
//        footer.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!loadStatus && list.size() != 0) {
//                    getList();
//                }
//            }
//        });
//        titlebar.setTitle("积分兑换");
//        titlebar.setLeftLl(backClickListener);
//        mypoints.setText("可用积分：" + AppSharedPreferencesHelper.getScore());
//
//        mAdapter = new PointsExchangeListAdapter(mContext, adaptList);
//        mAdapter.setmPointExchanged(new ExchangeGift() {
//            @Override
//            public void exchangegift(int score) {
//                mypoints.setText("我的积分：" + (int) (Integer.valueOf(AppSharedPreferencesHelper.getScore()) - score));
//                AppSharedPreferencesHelper.setScore((int) (Integer.valueOf(AppSharedPreferencesHelper.getScore()) - score) + "");
//            }
//        });
//        listView.setAdapter(mAdapter);
//        listView.setFooterDividersEnabled(false);
//        listView.setHeaderDividersEnabled(false);
//        listView.setDividerHeight(4);
//        listView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                PointsExchangeItems m = mAdapter.getItem(position - 1);
//                Log.i(tag, "mInfo  itemClick  : " + m.toString());
//            }
//        });
//        View emptyView = getLayoutInflater().inflate(
//                R.layout.empty_pointsexchange_view, null);
//        listView.setEmptyView(emptyView);
//        loadMoreListener = new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    if (loadMore && !loadStatus) {
//                        getList();
//                    }
//                    if (list.size() == 0) {
//                        listView.removeFooterView(footer);
//                    }
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                listView.setFirstItemIndex(firstVisibleItem);
//                if (firstVisibleItem != 1 && list.size() != 0) {
//                    loadMore = firstVisibleItem + visibleItemCount == totalItemCount;
//                } else {
//                    loadMore = false;
//                }
//            }
//        };
//        listView.setOnScrollListener(loadMoreListener);
//        listView.setOnRefreshListener(new DownPullRefreshListView.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (listView.getFooterViewsCount() > 0) {
//                    listView.removeFooterView(footer);
//                }
//                page_number = 1;
//                adaptList.clear();
//                getList();
//            }
//        });

    }

    @OnClick({R.id.mypoints, R.id.myexchange})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mypoints:
                Intent intent1 = new Intent(mContext, MyPointsDetailActivity.class);
                startActivity(intent1);
                break;
            case R.id.myexchange:
                Intent intent2 = new Intent(mContext, MyExchangeActivity.class);
                startActivity(intent2);
                break;
        }
    }

}
