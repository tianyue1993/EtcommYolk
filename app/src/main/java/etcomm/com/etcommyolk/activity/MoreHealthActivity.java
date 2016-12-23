package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.adapter.HealthAdapter;
import etcomm.com.etcommyolk.entity.NewsList;
import etcomm.com.etcommyolk.entity.RecommendItems;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.NewsListHandler;
import etcomm.com.etcommyolk.widget.DownPullRefreshListView;

public class MoreHealthActivity extends BaseActivity {

    @Bind(R.id.more_sports)
    DownPullRefreshListView listView;
    private ArrayList<RecommendItems> adaptList = new ArrayList<>();
    protected ArrayList<RecommendItems> list = new ArrayList<RecommendItems>();
    private HealthAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_health);
        ButterKnife.bind(this);
        EtcommApplication.addActivity(this);
        setTitleTextView("资讯阅读", null);
        setRightImage(R.mipmap.ic_search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SearchHealthNewsActivity.class));
            }
        });
        initData();
    }

    public void initData() {
        getList();
        mAdapter = new HealthAdapter(mContext, adaptList);
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
        //条目点击进入webview详情
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecommendItems recommendItems = mAdapter.getItem(position - 1);
                Intent intent = new Intent(mContext, WebviewDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("RecommendItems", recommendItems);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

    }


    public void getList() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("page", (page_number++) + "");
        params.put("page_size", page_size + "");
        Log.d("", "getFindHome: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        client.GetNewsList(mContext, params, new NewsListHandler() {
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
                listView.onRefreshComplete();
                loadStatus = false;
                loadingProgressBar.setVisibility(View.GONE);
            }
        });

    }


}
