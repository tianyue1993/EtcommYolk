package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.adapter.HealthAdapter;
import etcomm.com.etcommyolk.entity.FindHome;
import etcomm.com.etcommyolk.entity.RecommendItems;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.FindHomeHandler;
import etcomm.com.etcommyolk.widget.DownPullRefreshListView;

/**
 * 健康资讯-获取已收藏列表
 */
public class MyCollectionActivity extends BaseActivity {

    @Bind(R.id.collectpulllist)
    DownPullRefreshListView collectpulllist;
    @Bind(R.id.noCollection)
    RelativeLayout noCollection;
    private ArrayList<RecommendItems> list = new ArrayList<RecommendItems>();
    private HealthAdapter mHealthAdapter;
    protected int page_size = 6;
    protected int page_number = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);
        ButterKnife.bind(this);

        initView();

    }

    protected void initView() {
        EtcommApplication.addActivity(this);
        setTitleTextView("我的收藏", null);
        setRightImage(R.mipmap.arout_search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchHealthNewsActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
            }
        });
        //上拉listview加载更多监听
        loadMoreListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (loadMore && !loadStatus) {
                        getHealthList(false, page_size, ++page_number);
                    }
                    if (list.size() == 0) {
                        collectpulllist.removeFooterView(footer);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                collectpulllist.setFirstItemIndex(firstVisibleItem);
                if (firstVisibleItem != 1 && list.size() != 0) {
                    loadMore = firstVisibleItem + visibleItemCount == totalItemCount;
                } else {
                    loadMore = false;
                }
            }
        };
        collectpulllist.setOnScrollListener(loadMoreListener);
        //listviw下拉刷新
        collectpulllist.setOnRefreshListener(new DownPullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (collectpulllist.getFooterViewsCount() > 0) {
                    collectpulllist.removeFooterView(footer);
                }
                page_number = 1;
                getHealthList(true, page_size, page_number);
            }
        });
        mHealthAdapter = new HealthAdapter(this, list);
        //条目点击进入webview详情
        collectpulllist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecommendItems recommendItems = mHealthAdapter.getItem(position - 1);
                Intent intent = new Intent(mContext, WebviewDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("RecommendItems", recommendItems);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        getHealthList(true, page_size, page_number);
    }

    //获取列表
    private void getHealthList(final boolean isRefresh, int page_size, int page_number) {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("page_size", String.valueOf(page_size));
        params.put("page_number", String.valueOf(page_number));
        cancelmDialog();
        showProgress(0, true);
        client.toFavorite(this, params, new FindHomeHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(FindHome healthNewsItems) {
                super.onSuccess(healthNewsItems);
                cancelmDialog();
                List<RecommendItems> lists = healthNewsItems.content.items;

                if (isRefresh) {
                    if (lists != null && lists.size() > 0) {
                        list.clear();
                        list.addAll(lists);
                        collectpulllist.setAdapter(mHealthAdapter);
                    }else {
                        noCollection.setVisibility(View.VISIBLE);
                        collectpulllist.setVisibility(View.GONE);
                    }
                }else {
                    if (lists != null && lists.size() > 0) {
                        for (Iterator<RecommendItems> iterator = lists.iterator(); iterator.hasNext(); ) {
                            RecommendItems disscussCommentItems = (RecommendItems) iterator.next();
                            if (!list.contains(disscussCommentItems)) {
                                list.add(disscussCommentItems);
                            }
                        }
                    }else {
                        showToast("已无更多内容");
                        if (collectpulllist.getFooterViewsCount() > 0) {
                            collectpulllist.removeFooterView(footer);
                        }
                    }
                }
                loadStatus = false;
                collectpulllist.onRefreshComplete();
                loadingProgressBar.setVisibility(View.GONE);
                loadingText.setText(getResources().getString(R.string.loadmore));
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
                collectpulllist.onRefreshComplete();
                loadStatus = false;
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                getHealthList(false, page_size, page_number);
                break;
            default:
                break;
        }
    }


}
