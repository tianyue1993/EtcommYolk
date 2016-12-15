package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.adapter.MyPointsDetailListAdapter;
import etcomm.com.etcommyolk.entity.MyPointsDetail;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.MyPointHandler;
import etcomm.com.etcommyolk.widget.DownPullRefreshListView;

public class MyPointsDetailActivity extends BaseActivity {

    @Bind(R.id.mytotalpoints)
    TextView mytotalpoints;
    @Bind(R.id.howtoearnpoints)
    TextView howtoearnpoints;
    @Bind(R.id.refreshlist)
    DownPullRefreshListView listView;


    private ArrayList<MyPointsDetail.Content.Score> list = new ArrayList<>();
    private ArrayList<MyPointsDetail.Content.Score> adaptList = new ArrayList<>();
    private MyPointsDetailListAdapter mAdapter;
    private int page_size = 12;
    private int page_number = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_points_detail);
        ButterKnife.bind(this);
        setTitleTextView("我的积分", null);
        setRightTextView("积分规则", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PointsColleketDetailActivity.class);
                intent.putExtra("Token", prefs.getAccessToken());
                startActivity(intent);
            }
        });
        initDatas();

    }


    protected void initDatas() {
        getList();
        mytotalpoints.setText("累计积分:" + prefs.getTotalScore());//
        howtoearnpoints.setText("可用积分:" + prefs.getScore());
        mAdapter = new MyPointsDetailListAdapter(mContext, adaptList);
        listView.setAdapter(mAdapter);
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
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loadStatus && list.size() != 0) {
                    getList();
                }
            }
        });

    }

    public void getList() {
        RequestParams params = new RequestParams();
        params.put("page_size", page_size + "");
        params.put("page", String.valueOf(page_number++));
        params.put("access_token", prefs.getAccessToken());
        Log.d(tag, "getList: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        client.GetMyPointsDetail(mContext, params, new MyPointHandler() {
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
            public void onSuccess(MyPointsDetail exchange) {
                super.onSuccess(exchange);
                cancelmDialog();
                list = exchange.content.score;
                if (list.size() > 0) {
                    if (listView.getFooterViewsCount() == 0 && Integer.parseInt(exchange.content.page_count) > 1) {
                        listView.addFooterView(footer);
                        listView.setAdapter(mAdapter);
                    }
                    for (Iterator<MyPointsDetail.Content.Score> iterator = list.iterator(); iterator.hasNext(); ) {
                        MyPointsDetail.Content.Score disscussCommentItems = iterator.next();
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
}
