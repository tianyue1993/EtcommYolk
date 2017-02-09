package etcomm.com.etcommyolk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.RequestParams;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.activity.AddNewTopicActivity;
import etcomm.com.etcommyolk.activity.MineActivity;
import etcomm.com.etcommyolk.activity.MsgListActivity;
import etcomm.com.etcommyolk.activity.SearchGroupActivity;
import etcomm.com.etcommyolk.activity.TopicDisscussListActivity;
import etcomm.com.etcommyolk.adapter.GoodGroupAdapter;
import etcomm.com.etcommyolk.adapter.YolkBaseAdapter;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.entity.GoodGroup;
import etcomm.com.etcommyolk.entity.GroupItems;
import etcomm.com.etcommyolk.entity.GroupList;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.handler.GroupListHandler;
import etcomm.com.etcommyolk.handler.QuitGroupHandler;
import etcomm.com.etcommyolk.utils.GlobalSetting;
import etcomm.com.etcommyolk.widget.HorizontalListView;

public class AroundFragment extends BaseFragment implements
        SlideAndDragListView.OnMenuItemClickListener {
    /**
     * 控制OnResume()执行次数
     */
    public static boolean limitOnresumSide;
    public static String TAG = "AroundFragment";
    @Bind(R.id.base_left)
    ImageView baseLeft;
    @Bind(R.id.add_topic)
    ImageView addTopic;
    @Bind(R.id.topic_search)
    ImageView topicSearch;
    @Bind(R.id.base_right)
    ImageView baseRight;
    @Bind(R.id.listview)
    SlideAndDragListView<GroupItems> listView;
    @Bind(R.id.base_title)
    TextView baseTitle;
    @Bind(R.id.title)
    RelativeLayout title;
    @Bind(R.id.empty)
    RelativeLayout empty;
    Context mContext;
    @Bind(R.id.to_see)
    TextView to_see;
    @Bind(R.id.good_group)
    HorizontalListView goodGroup;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.text)
    TextView text;
    private ArrayList<GroupItems> adaptList = new ArrayList<>();
    protected ArrayList<GroupItems> list = new ArrayList<GroupItems>();
    private MyGroupListAdapter mAdapter;
    private List<Menu> mMenuList;
    GoodGroupAdapter adapter;
    private ArrayList<GoodGroup.QuitGroup> arrayList = new ArrayList<>();
    //关注成功，刷新身边页面————添加关注小组到我的小组列表
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("add")) {
                adaptList.clear();
                page_number = 1;
                getList();
            }
        }
    };


    /**
     * onCreateView
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    protected void initView(View view, Bundle savedInstanceState) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_side;
    }

    @Override
    public void receive_msg_data() {
        baseRight.setImageResource(R.mipmap.icon_msg_unread);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        IntentFilter filter = new IntentFilter();
        filter.addAction("add");
        mContext.registerReceiver(receiver, filter);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        limitOnresumSide = true;
        if (limitOnresumSide) {
            adaptList.clear();
            list.clear();
            arrayList.clear();
            page_number = 1;
            getList();
            getGoodgroup();
        }


        if (prefs.getHaveReceiveUnReadData()) {
            baseRight.setImageResource(R.mipmap.icon_msg_unread);
        } else {
            baseRight.setImageResource(R.mipmap.ic_messege);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        adapter = new GoodGroupAdapter(mContext, arrayList);
        goodGroup.setAdapter(adapter);
        //设置侧滑菜单按钮
        initMenu();
        initUiAndListener();
        listView.setOnListItemClickListener(new SlideAndDragListView.OnListItemClickListener() {
            @Override
            public void onListItemClick(View view, int i) {
                GroupItems m = mAdapter.getItem(i);
                Intent intent = new Intent(mContext, TopicDisscussListActivity.class);
                intent.putExtra("topic_id", m.topic_id);
                intent.putExtra("topic_name", m.name);
                startActivity(intent);
            }
        });
        return rootView;
    }


    public void initMenu() {
        mMenuList = new ArrayList<>(2);
        Menu menu0 = new Menu(new ColorDrawable(Color.LTGRAY), true, 0);
        menu0.addItem(new MenuItem.Builder().setWidth(200)// set Width
                //设置背景
                .setBackground(new ColorDrawable(Color.LTGRAY))// set background
                .setText("取消关注")// set text string
                .setDirection(MenuItem.DIRECTION_RIGHT).setTextColor(Color.WHITE)// set
                .setTextSize(10)// set text size
                .build());

        Menu menu1 = new Menu(new ColorDrawable(Color.LTGRAY), false, 1);// the
        menu1.addItem(new MenuItem.Builder().setWidth(200)// set Width
                //设置背景
                .setBackground(new ColorDrawable(Color.LTGRAY))// set background
                .setText("关闭小组")// set text string
                .setDirection(MenuItem.DIRECTION_RIGHT).setTextColor(Color.WHITE)// set
                .setTextSize(10)// set text size
                .build());
        mMenuList.add(menu0);
        mMenuList.add(menu1);
    }


    public void initUiAndListener() {
        listView.setMenu(mMenuList);
        mAdapter = new MyGroupListAdapter(mContext, adaptList);
        listView.setAdapter(mAdapter);
        listView.setOnMenuItemClickListener(this);
        listView.setDividerHeight(1);
    }


    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        Log.i(TAG, "onMenuItemClick   " + itemPosition + "   " + buttonPosition + "   " + direction);
        int viewType = mAdapter.getItemViewType(itemPosition);
        switch (viewType) {
            case 0:
                unAttention(mAdapter.getItem(itemPosition));
                return clickMenuBtn0(buttonPosition, direction);
            case 1:
                deleteTopic(mAdapter.getItem(itemPosition));
                return clickMenuBtn1(buttonPosition, direction);
            default:
                return Menu.ITEM_NOTHING;
        }
    }

    private int clickMenuBtn0(int buttonPosition, int direction) {
        switch (direction) {

            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_SCROLL_BACK;
                }
        }
        return Menu.ITEM_NOTHING;
    }

    private int clickMenuBtn1(int buttonPosition, int direction) {
        switch (direction) {

            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_SCROLL_BACK;
                }
        }
        return Menu.ITEM_NOTHING;
    }


    @OnClick({R.id.base_left, R.id.add_topic, R.id.topic_search, R.id.base_right, R.id.to_see})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.base_left:
                startActivity(new Intent(mContext, MineActivity.class));
                break;
            case R.id.add_topic:
                startActivity(new Intent(mContext, AddNewTopicActivity.class));
                break;
            case R.id.topic_search:
                startActivity(new Intent(mContext, SearchGroupActivity.class));
                break;
            case R.id.base_right:
                startActivity(new Intent(mContext, MsgListActivity.class));
                break;
            case R.id.to_see:
                startActivity(new Intent(mContext, SearchGroupActivity.class));
                break;
        }
    }

    /**
     * 获取优质小组列表
     */
    public void getGoodgroup() {
        RequestParams params = new RequestParams();
        params.put("access_token", GlobalSetting.getInstance(mContext).getAccessToken());
        cancelmDialog();
        showProgress(0, true);
        ApiClient.getInstance().goodGroup(mContext, params, new QuitGroupHandler() {
            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(GoodGroup commen) {
                super.onSuccess(commen);
                cancelmDialog();
                if (commen.content.size() > 0) {
                    for (Iterator<GoodGroup.QuitGroup> iterator = commen.content.iterator(); iterator.hasNext(); ) {
                        GoodGroup.QuitGroup disscussCommentItems = iterator.next();
                        arrayList.add(disscussCommentItems);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    /**
     * 关闭小组
     * */
    public void deleteTopic(final GroupItems mInfo) {
        RequestParams params = new RequestParams();
        params.put("topic_id", mInfo.topic_id);
        params.put("access_token", prefs.getAccessToken());
        cancelmDialog();
        showProgress(0, true);
        client.topicDelete(mContext, params, new CommenHandler() {
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
                cancelmDialog();
                Toast.makeText(mContext, commen.message, Toast.LENGTH_SHORT).show();
                adaptList.remove(mInfo);
                mAdapter.notifyDataSetChanged();
                if (adaptList.size() < 1) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                }
            }
        });

    }

    /**
     * 获取用户下面所有的小组，包括已关注和未关注
     */
    public void getList() {
        RequestParams params = new RequestParams();
        params.put("access_token", GlobalSetting.getInstance(mContext).getAccessToken());
        params.put("page", (page_number) + "");
        params.put("page_size", 1000 + "");
        params.put("type", 1 + "");//2为搜索全部，1为已关注
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
            }

            @Override
            public void onSuccess(GroupList groupList) {
                super.onSuccess(groupList);
                cancelmDialog();
                list = groupList.content.items;
                if (list.size() > 0) {
                    for (Iterator<GroupItems> iterator = list.iterator(); iterator.hasNext(); ) {
                        GroupItems disscussCommentItems = iterator.next();
                        adaptList.add(disscussCommentItems);
                    }
                } else {
                    showToast("已无更多内容");
                    if (listView.getFooterViewsCount() > 0) {
                        listView.removeFooterView(footer);
                    }

                }
                if (adaptList.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * 取消关注
     *
     * @param mInfo
     * @return
     */
    protected int unAttention(final GroupItems mInfo) {
        RequestParams params = new RequestParams();
        params.put("access_token", GlobalSetting.getInstance(mContext).getAccessToken());
        params.put("topic_id", mInfo.topic_id);
        cancelmDialog();
        showProgress(0, true);
        ApiClient.getInstance().UnAttention(mContext, params, new CommenHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                Toast.makeText(mContext, "取消关注", Toast.LENGTH_SHORT).show();
                adaptList.remove(mInfo);
                mAdapter.notifyDataSetChanged();
                if (adaptList.size() < 1) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
        return 0;
    }


    public class MyGroupListAdapter extends YolkBaseAdapter<GroupItems> {
        ViewHolder viewHolder = null;

        public MyGroupListAdapter(Context context, ArrayList<GroupItems> mList) {
            super(context);
            this.mList = mList;
        }


        @Override
        public int getItemViewType(int position) {
            if (mList.get(position).user_id.equals(pres.getUserId())) {
                return 1;//刪除類型
            } else {
                return 0;//取關类型
            }

        }


        @Override
        public int getViewTypeCount() {
            return 2;//所有的menu数量
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_around_group, null);
                viewHolder.attention_count = (TextView) convertView.findViewById(R.id.attention_count);
                viewHolder.attention_topic = (TextView) convertView.findViewById(R.id.attention_topic);
                viewHolder.topic_image = (SimpleDraweeView) convertView.findViewById(R.id.topic_image);
                viewHolder.follow_count = (TextView) convertView.findViewById(R.id.follow_count);
                viewHolder.topic_discuss = (TextView) convertView.findViewById(R.id.topic_discuss);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final GroupItems mInfo = getItem(position);
            if (null != mInfo) {
                viewHolder.attention_count.setText(mInfo.follows + "人关注");
                viewHolder.attention_topic.setText(mInfo.name + "");
                viewHolder.follow_count.setText(mInfo.discussion_number + "个帖子");
                if (mInfo.desc != null) {
                    viewHolder.topic_discuss.setText(mInfo.desc);
                }
                if (!mInfo.avatar.isEmpty()) {
                    viewHolder.topic_image.setImageURI(mInfo.avatar);
                } else {
                    viewHolder.topic_image.setImageResource(R.mipmap.ic_header);

                }

            }
            return convertView;
        }


        private class ViewHolder {
            TextView attention_count, follow_count;
            TextView attention_topic, topic_discuss;
            SimpleDraweeView topic_image;
        }


    }


}



