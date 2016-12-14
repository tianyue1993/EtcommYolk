package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.adapter.PointsExchangeListAdapter;
import etcomm.com.etcommyolk.entity.PointsExchange;
import etcomm.com.etcommyolk.entity.PointsExchangeItems;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.PointExchangeHandler;
import etcomm.com.etcommyolk.utils.StringUtils;
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
    DownPullRefreshListView listView;
    private PointsExchangeListAdapter mAdapter;
    protected List<PointsExchangeItems> list = new ArrayList<PointsExchangeItems>();
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
        ButterKnife.bind(this);
        initDatas();
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
        RequestParams params = new RequestParams();
        params.put("page_size", page_size + "");
        params.put("page", String.valueOf(page_number++));
        params.put("access_token", "ee6d116c0e7dab6190cc0e246b421cfc");
        Log.d(tag, "getList: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        client.invokePointExch(mContext, params, new PointExchangeHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(PointsExchange exchange) {
                super.onSuccess(exchange);
                cancelmDialog();
                int code = exchange.code;
                String message = exchange.message;
                if (code == 0) {
                    list = exchange.content.model;
                    totalpoints = Integer.parseInt(exchange.content.my_score);
                    if (!StringUtils.isEmpty(prefs.getScore()) && Integer.valueOf(prefs.getScore()) == totalpoints) {
                    } else {
                        prefs.setScore(totalpoints + "");
                    }

                    if (list.size() > 0) {
                        if (listView.getFooterViewsCount() == 0 && Integer.parseInt(exchange.content.page_count) > 1) {
                            listView.addFooterView(footer);
                            listView.setAdapter(mAdapter);
                        }
                        for (Iterator<PointsExchangeItems> iterator = list.iterator(); iterator.hasNext(); ) {
                            PointsExchangeItems disscussCommentItems = (PointsExchangeItems) iterator.next();
                            adaptList.add(disscussCommentItems);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        showToast("已无更多内容");
                        if (listView.getFooterViewsCount() > 0) {
                            listView.removeFooterView(footer);
                        }
                    }

                } else {
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

                }
                loadStatus = false;
                listView.onRefreshComplete();
                loadingProgressBar.setVisibility(View.GONE);
                loadingText.setText(getResources().getString(R.string.loadmore));
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                showToast(exception.getMessage());
                cancelmDialog();
                listView.onRefreshComplete();
                loadStatus = false;
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }


    protected void initDatas() {
        getList();
        setTitleTextView("积分兑换", null);
        footer = View.inflate(mContext, R.layout.loadmore, null);
        loadingProgressBar = (ProgressBar) footer.findViewById(R.id.progressBar);
        loadingText = (TextView) footer.findViewById(R.id.title);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loadStatus && list.size() != 0) {
                    getList();
                }
            }
        });
        mypoints.setText("可用积分：" + prefs.getScore());
        mAdapter = new PointsExchangeListAdapter(mContext, adaptList);
        mAdapter.setmPointExchanged(new PointsExchangeListAdapter.ExchangeGift() {
            @Override
            public void exchangegift(int score) {
                mypoints.setText("我的积分：" + (int) (Integer.valueOf(prefs.getScore()) - score));
                prefs.setScore((int) (Integer.valueOf(prefs.getScore()) - score) + "");
            }
        });
        listView.setAdapter(mAdapter);
        listView.setFooterDividersEnabled(false);
        listView.setHeaderDividersEnabled(false);
        listView.setDividerHeight(4);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PointsExchangeItems m = mAdapter.getItem(position - 1);
                Log.i(tag, "mInfo  itemClick  : " + m.toString());
            }
        });
        View emptyView = getLayoutInflater().inflate(
                R.layout.empty_pointsexchange_view, null);
        listView.setEmptyView(emptyView);
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
