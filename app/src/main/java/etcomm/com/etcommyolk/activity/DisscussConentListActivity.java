package etcomm.com.etcommyolk.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.adapter.DisscussCommentListAdapter;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.entity.DiscussionComment;
import etcomm.com.etcommyolk.entity.DisscussCommentItems;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.handler.DiscussCommentHandler;
import etcomm.com.etcommyolk.utils.StringUtils;
import etcomm.com.etcommyolk.widget.ContainsEmojiEditText;
import etcomm.com.etcommyolk.widget.DialogFactory;
import etcomm.com.etcommyolk.widget.DownPullRefreshListView;

public class DisscussConentListActivity extends BaseActivity {


    @Bind(R.id.comment_et)
    ContainsEmojiEditText comment_et;
    @Bind(R.id.comment_send)
    Button comment_send;
    @Bind(R.id.bottom_bar)
    LinearLayout bottom_bar;
    @Bind(R.id.commmentpulllist)
    DownPullRefreshListView listView;
    @Bind(R.id.emptyview)
    View emptyview;


    private String disscuss_id;
    protected List<DisscussCommentItems> adaptList = new ArrayList<DisscussCommentItems>();
    List<DisscussCommentItems> list = new ArrayList<DisscussCommentItems>();
    private DisscussCommentListAdapter adapter;
    private String response_comment_id = "";
    private String topic_id;
    protected Dialog deletecommentDialog;
    private Dialog commonDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_disscuss_conent_list);
        setTitleTextView("评论", null);
        ButterKnife.bind(this);
        initDatas();
    }

    private void deleteShowDialog(final DisscussCommentItems item) {
        // TODO Auto-generated method stub
        commonDialog = DialogFactory.getDialogFactory().showCommonDialog(mContext, "确定删除该评论吗？", "取消", "确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dismiss();
            }
        }, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                commonDialog.dismiss();
                deleteComment(item);

            }
        }, Color.BLACK, Color.BLACK);

    }


    public void initDatas() {
        Intent intent = getIntent();
        disscuss_id = intent.getStringExtra("disscuss_id");
        topic_id = intent.getStringExtra("topic_id");
        adapter = new DisscussCommentListAdapter(mContext, adaptList, new DisscussCommentListAdapter.DisscussCommentResponseClickListener() {
            @Override
            public void onClick(DisscussCommentItems mInfo) {
                // TODO Auto-generated method stub
                String comment = comment_et.getText().toString();
                if (!StringUtils.isEmpty(comment) && comment.startsWith("回复")) {
                    String[] strs = comment.split(":");
                    if (!StringUtils.isEmpty(strs[1])) {
                        comment_et.setHint("回复" + mInfo.nick_name + ":" + strs[1]);
                        response_comment_id = mInfo.comment_id;
                    } else {
                        comment_et.setHint("回复" + mInfo.nick_name + ":");
                        response_comment_id = mInfo.comment_id;
                    }
                } else {
                    comment_et.setHint("回复" + mInfo.nick_name + ":");
                    response_comment_id = mInfo.comment_id;
                }
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DisscussCommentItems item = adapter.getItem(position - 1);
                View.OnClickListener upBtnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (deletecommentDialog != null && deletecommentDialog.isShowing()) {
                            deletecommentDialog.dismiss();
                        }
                        String comment = comment_et.getText().toString();
                        if (!StringUtils.isEmpty(comment) && comment.startsWith("回复")) {
                            String[] strs = comment.split(":");
                            if (strs.length >= 2 && !StringUtils.isEmpty(strs[1])) {
                                comment_et.setHint("回复" + item.nick_name + ":" + strs[1]);
                                response_comment_id = item.comment_id;
                            } else {
                                comment_et.setHint("回复" + item.nick_name + ":");
                                response_comment_id = item.comment_id;
                            }
                        } else {
                            comment_et.setHint("回复" + item.nick_name + ":");
                            response_comment_id = item.comment_id;
                        }
                    }
                };
                View.OnClickListener bottomBtnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (deletecommentDialog != null && deletecommentDialog.isShowing()) {
                            deletecommentDialog.dismiss();
                        }
                        deleteShowDialog(item);
                    }

                };
                if (item.user_id.equals(prefs.getUserId())) {
                    deletecommentDialog = DialogFactory.getDialogFactory().showCommentOptDialog(mContext, "删除", null, bottomBtnClickListener, null);
                } else {
                    deletecommentDialog = DialogFactory.getDialogFactory().showCommentOptDialog(mContext, "回复", null, upBtnClickListener, null);

                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DisscussCommentItems item = adapter.getItem(position - 1);
                Intent intent = new Intent(mContext, TopicReportPopActivity.class);
                intent.putExtra("comment_id", item.comment_id);
                intent.putExtra("type", "conment");
                startActivity(intent);
                return true;
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

    @OnClick(R.id.comment_send)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.comment_send:
                String comment = comment_et.getText().toString();
                if (!StringUtils.isEmpty(comment) && comment.startsWith("回复") && comment.contains(":")) {
                    String[] strs = comment.split(":");
                    if (strs.length > 1 && !StringUtils.isEmpty(strs[1])) {
                        comment = strs[1];
                    } else {
                        comment = "";
                    }
                } else {
                }
                if (StringUtils.isEmpty(comment) || comment.length() < 1) {
                    showToast(R.string.disscuss_null);
                    return;
                }
                RequestParams params = new RequestParams();
                params.put("discussion_id", disscuss_id);
                params.put("topic_id", topic_id);
                params.put("access_token", prefs.getAccessToken());
                params.put("comment", comment);
                if (!StringUtils.isEmpty(response_comment_id)) {
                    params.put("comment_id", response_comment_id);// 只有回复 某个人时才有
                }
                cancelmDialog();
                showProgress(0, true);
                Log.i(tag, "params: " + params.toString());
                client.commentCreate(mContext, params, new CommenHandler() {
                    @Override
                    public void onCancel() {
                        super.onCancel();
                        cancelmDialog();
                    }

                    @Override
                    public void onFailure(BaseException exception) {
                        super.onFailure(exception);
                        cancelmDialog();
                    }

                    @Override
                    public void onSuccess(Commen commen) {
                        super.onSuccess(commen);
                        comment_et.setText("");
                        // if()考虑一下是不是要显示刚才评论的问题，如果刚好在下一页面，怎么考虑
                        // 评论列表避免重复显示
                        page_number = 1;
                        adaptList.clear();
                        getList();
                    }
                });
                break;
        }
    }

    void deleteComment(final DisscussCommentItems item) {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("comment_id", item.comment_id);// 只有回复 某个人时才有
        cancelmDialog();
        showProgress(0, true);
        Log.i(tag, "params: " + params.toString());
        client.commentDelete(mContext, params, new CommenHandler() {
            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                comment_et.setText("");
                adaptList.remove(item);
                adapter.notifyDataSetChanged();
                // if()考虑一下是不是要显示刚才评论的问题，如果刚好在下一页面，怎么考虑
                // 评论列表避免重复显示
                showToast(commen.message);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }
        });
    }

    public void getList() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("page", (page_number++) + "");
        params.put("page_size", page_size + "");
        params.put("discussion_id", disscuss_id);
        cancelmDialog();
        showProgress(0, true);
        Log.i(tag, "params: " + params.toString());
        client.discussionComment(mContext, params, new DiscussCommentHandler() {
            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
                listView.onRefreshComplete();
                loadStatus = false;
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(DiscussionComment discussionComment) {
                super.onSuccess(discussionComment);
                cancelmDialog();
                list = discussionComment.content.items;
                if (list.size() > 0) {
                    if (listView.getFooterViewsCount() == 0 && Integer.parseInt(discussionComment.content.pages) > 1) {
                        listView.addFooterView(footer);
                        listView.setAdapter(adapter);
                    }
                    for (Iterator<DisscussCommentItems> iterator = list.iterator(); iterator.hasNext(); ) {
                        DisscussCommentItems disscussCommentItems = (DisscussCommentItems) iterator.next();
                        adaptList.add(disscussCommentItems);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    showToast("已无更多内容");
                    if (listView.getFooterViewsCount() > 0) {
                        listView.removeFooterView(footer);
                    }
                }
                if (adaptList.size() == 0) {
                    emptyview.setVisibility(View.VISIBLE);
                } else {
                    emptyview.setVisibility(View.INVISIBLE);
                }
                loadStatus = false;
                listView.onRefreshComplete();
                loadingProgressBar.setVisibility(View.GONE);
                loadingText.setText(getResources().getString(R.string.loadmore));
            }
        });

    }


    public void onResume() {
        super.onResume();
        page_number = 1;
        adaptList.clear();
        getList();
        hideSoftKeyBoard();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(tag);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(tag);
    }
}
