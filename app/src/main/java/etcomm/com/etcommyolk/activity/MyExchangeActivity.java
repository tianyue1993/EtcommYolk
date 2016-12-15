package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.adapter.MyExchangeListAdapter;
import etcomm.com.etcommyolk.entity.MyExchange;
import etcomm.com.etcommyolk.entity.PointsExchangeItems;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.MyExchangeHandler;
import etcomm.com.etcommyolk.widget.DownPullRefreshListView;

public class MyExchangeActivity extends BaseActivity {

    @Bind(R.id.myexchangelist)
    DownPullRefreshListView listView;
    private ArrayList<PointsExchangeItems> list = new ArrayList<>();
    private ArrayList<PointsExchangeItems> adaptList = new ArrayList<>();
    private MyExchangeListAdapter mAdapter;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_exchange);
        ButterKnife.bind(this);
        setTitleTextView("我的兑换", null);
        initData();

    }

    public void initData() {
        getList();
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loadStatus && list.size() != 0) {
                    getList();
                }
            }
        });
        mAdapter = new MyExchangeListAdapter(mContext, adaptList);
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
        emptyView = getLayoutInflater().inflate(R.layout.empty_myexchange, null);
        TextView emptydec = (TextView) emptyView.findViewById(R.id.empty_dec);
        emptydec.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(mContext, PointsExchangeActivity.class);
                                            mContext.startActivity(intent);
                                            finish();
                                        }
                                    }

        );
        String html = "暂时没有兑换任何商品\n <br/>&#10;赶紧去【<font color=\"#f37f32\">兑换</font>】吧！";
        emptydec.setText(Html.fromHtml(html));
    }

    private void getList() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("page_size", String.valueOf(page_size));
        params.put("page", String.valueOf(page_number++));
        cancelmDialog();
        showProgress(0, true);
        Log.i(tag, "params: " + params.toString());
        client.GetMyExchange(mContext, params, new MyExchangeHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(MyExchange exchange) {
                super.onSuccess(exchange);
                cancelmDialog();
                list = exchange.content.model;
                if (list != null && list.size() > 0) {
                    for (Iterator<PointsExchangeItems> iterator = list.iterator(); iterator.hasNext(); ) {
                        PointsExchangeItems disscussCommentItems = (PointsExchangeItems) iterator.next();
                        adaptList.add(disscussCommentItems);
                    }
                    mAdapter.notifyDataSetChanged();
                    listView.setEmptyView(emptyView);
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


}
