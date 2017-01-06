package etcomm.com.etcommyolk.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.adapter.MsgListAdapter;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.entity.MsgListItems;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.handler.MsgListHandler;
import etcomm.com.etcommyolk.widget.DialogFactory;

/**
 * 推送消息
 */
public class MsgListActivity extends BaseActivity {

    @Bind(R.id.sdlistview)
    SlideAndDragListView sdlistview;
    @Bind(R.id.emptyview)
    RelativeLayout emptyview;
    private MsgListAdapter adapter;
    protected List<MsgListItems.MsgList> mMsgList = new ArrayList<>();
    private int mScreenWidth;
    private Dialog commonDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_list);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        prefs.setHaveReceiveUnReadData(false);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        getMSGList();
        super.onStart();
    }

    private void getMSGList() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        client.toNews(this, params, new MsgListHandler() {

            @Override
            public void onSuccess(MsgListItems msgListItems) {
                super.onSuccess(msgListItems);
                if (msgListItems.content.size() > 0) {
                    mMsgList.clear();
                }
                mMsgList = msgListItems.content;


                if (mMsgList.size() < 1) {
                    emptyview.setVisibility(View.VISIBLE);
                    sdlistview.setVisibility(View.GONE);
                } else {
                    emptyview.setVisibility(View.GONE);
                    sdlistview.setVisibility(View.VISIBLE);
                }
                adapter = new MsgListAdapter(mContext, mMsgList, mScreenWidth);
                sdlistview.setAdapter(adapter);
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
            }
        });
    }

    protected void deleteAllMsg() {
        // TODO Auto-generated method stub
        commonDialog = DialogFactory.getDialogFactory().showCommonDialog(mContext, "确定删除该消息", "取消", "确定", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                commonDialog.dismiss();
            }
        }, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                commonDialog.dismiss();
                deleteAllMsgFromNet(null);
            }
        }, Color.BLACK, Color.BLACK);
    }

    protected void deleteAllMsgFromNet(final MsgListItems.MsgList item) {
        // TODO Auto-generated method stub
        RequestParams params = new RequestParams();
        //不为空 删除单个消息
        if (item != null) {
            params.put("news_id", prefs.getUserId());
        }
        params.put("access_token", prefs.getAccessToken());
        client.toNewsDelete(this, params, new CommenHandler() {

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                Toast.makeText(mContext, commen.message, Toast.LENGTH_SHORT).show();
                if (item != null) {
                    mMsgList.remove(item);
                    adapter.notifyDataSetChanged();
                    if (mMsgList.size() < 1) {
                        emptyview.setVisibility(View.VISIBLE);
                        sdlistview.setVisibility(View.GONE);
                    } else {
                        emptyview.setVisibility(View.GONE);
                        sdlistview.setVisibility(View.VISIBLE);
                    }
                } else {
                    mMsgList.clear();
                    emptyview.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
            }

            // 处理非0返回值
            @Override
            public void onAllow(Commen commen) {
                super.onAllow(commen);
                if (mMsgList.size() < 1) {
                    emptyview.setVisibility(View.VISIBLE);
                } else {
                    emptyview.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
                Toast.makeText(mContext, commen.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void deleteThisMSG(final MsgListItems.MsgList item) {
        // TODO Auto-generated method stub
        commonDialog = DialogFactory.getDialogFactory().showCommonDialog(mContext, "确定删除该消息", "取消", "确定", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                commonDialog.dismiss();
                adapter.notifyDataSetChanged();
            }
        }, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                commonDialog.dismiss();
                deleteAllMsgFromNet(item);
            }
        }, Color.BLACK, Color.BLACK);
    }


    private void initView() {
        WindowManager wm = getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        mScreenWidth = width;
        setTitleTextView("消息", null);
        setRightTextView("清空消息", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mMsgList.size() > 0) {

                    deleteAllMsg();
                } else {
                    showToast("当前没有消息");
                    // showToast(mContext, "当前没有消息", Toast.LENGTH_SHORT);
                }
            }
        });
        Menu menu = new Menu(new ColorDrawable(Color.WHITE), true, 0);// the
        menu.addItem(new MenuItem.Builder().setWidth(200)// set Width
                .setBackground(new ColorDrawable(Color.RED))// set background
                .setText("删除")// set text string
                .setDirection(MenuItem.DIRECTION_RIGHT).setTextColor(Color.WHITE)// set
                .setTextSize(20)// set text size
                .build());
        sdlistview.setMenu(menu);
        sdlistview.setDividerHeight(1);
        sdlistview.setOnSlideListener(new SlideAndDragListView.OnSlideListener() {
            @Override
            public void onSlideOpen(View view, View parentView, int position, int direction) {
                Log.i(tag, "onSlideOpen position:" + position + "  direction: " + direction);
            }

            @Override
            public void onSlideClose(View view, View parentView, int position, int direction) {
                Log.i(tag, "onSlideClose position:" + position + "  direction: " + direction);
            }
        });
        sdlistview.setOnMenuItemClickListener(new SlideAndDragListView.OnMenuItemClickListener() {
            @Override
            public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
                Log.i(tag, "onMenuItemClick itemPosition:" + itemPosition + " buttonPosition: " + buttonPosition + " direction: " + direction);
                switch (direction) {
                    case MenuItem.DIRECTION_LEFT:
                        switch (buttonPosition) {
                            case 0:// One
                                return Menu.ITEM_SCROLL_BACK;
                        }
                        break;
                    case MenuItem.DIRECTION_RIGHT:
                        switch (buttonPosition) {
                            case 0:// icon
                                MsgListItems.MsgList item = adapter.getItem(itemPosition);
                                deleteThisMSG(item);
                                // sdlistview
                                return Menu.ITEM_NOTHING;
                            // return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                        }
                        break;
                    default:
                        return Menu.ITEM_NOTHING;
                }
                return Menu.ITEM_NOTHING;
            }
        });
        sdlistview.setOnListItemClickListener(new SlideAndDragListView.OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {
                Log.i(tag, "onListItemClick position:" + position);
                MsgListItems.MsgList obj = adapter.getItem(position);
                if (obj.type.equals("2")) {
                    if (obj.topic_list_type.equals("1")) {// 点赞
                        Intent notifyintent = new Intent(MsgListActivity.this, TopicDisscussListActivity.class);
                        notifyintent.putExtra("topic_id", String.valueOf(obj.detail_id));
                        notifyintent.putExtra("user_id", String.valueOf(obj.user_id));
                        notifyintent.putExtra("isAttentioned", obj.is_like);
                        notifyintent.putExtra("topic_name", obj.detail);// /
                        notifyintent.putExtra("isFromMSG", true);
                        startActivity(notifyintent);
                    } else if (obj.topic_list_type.equals("2")) {
                        Intent notifyintent = new Intent(MsgListActivity.this, DisscussConentListActivity.class);
                        notifyintent.putExtra("topic_id", String.valueOf(obj.topic_id));
                        notifyintent.putExtra("disscuss_id", obj.detail_id);
                        notifyintent.putExtra("isAttentioned", obj.is_like);
                        notifyintent.putExtra("topic_name", obj.topic_name);
                        notifyintent.putExtra("isFromMSG", true);
                        startActivity(notifyintent);

                    }

                }

            }
        });

    }


}
