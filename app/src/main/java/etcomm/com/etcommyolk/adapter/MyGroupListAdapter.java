package etcomm.com.etcommyolk.adapter;

//public class MyGroupListAdapter extends YolkBaseAdapter<GroupItems> {
//    private static final String TAG = "AroundListAdapter";
//    private mAttentioned mListener;
//    ViewHolder viewHolder = null;
//    private int currentType;//当前item类型
//
//    private static final int TYPE_1 = 0;//消费类型
//    private static final int TYPE_2 = 1;//充值类型
//
//    public MyGroupListAdapter(Context context, ArrayList<GroupItems> mList, mAttentioned listener) {
//        super(context);
//        this.mList = mList;
//        this.mListener = listener;
//    }
//
//    public interface mAttentioned {
//        void onAttentioned(GroupItems item);
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (mList.get(position).topic_id.equals(pres.getUserId())){
//            return TYPE_2;//刪除類型
//        }else {
//            return TYPE_1;//取關类型
//        }
//
//    }
//
//
//
//    @Override
//    public int getViewTypeCount() {
//        return 2;//所有的menu数量
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        currentType = getItemViewType(position);
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_around_group, null);
//            viewHolder.attention_count = (TextView) convertView.findViewById(R.id.attention_count);
//            viewHolder.attention_topic = (TextView) convertView.findViewById(R.id.attention_topic);
//            viewHolder.topic_image = (SimpleDraweeView) convertView.findViewById(R.id.topic_image);
//            viewHolder.follow_count = (TextView) convertView.findViewById(R.id.follow_count);
//            viewHolder.topic_discuss = (TextView) convertView.findViewById(R.id.topic_discuss);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        final GroupItems mInfo = getItem(position);
//        if (null != mInfo) {
//            viewHolder.attention_count.setText(mInfo.follows + "人关注");
//            viewHolder.attention_topic.setText(mInfo.name + "");
//            viewHolder.follow_count.setText(mInfo.discussion_number + "个帖子");
//            if (mInfo.desc != null) {
//                viewHolder.topic_discuss.setText(mInfo.desc);
//            }
//            if (!mInfo.avatar.isEmpty()) {
//                viewHolder.topic_image.setImageURI(mInfo.avatar);
//            } else {
//                viewHolder.topic_image.setImageResource(R.mipmap.ic_header);
//
//            }
//
//        }
//        return convertView;
//    }
//
//
//
//    private static class ViewHolder {
//        TextView attention_count, follow_count;
//        TextView attention_topic, topic_discuss;
//        SimpleDraweeView topic_image;
//    }
//
//
//    protected ProgressDialog mProgress;
//
//    public void showProgress(int resId, boolean cancel) {
//        mProgress = new ProgressDialog(mContext);
//        if (resId <= 0) {
//            mProgress.setMessage(R.string.loading_data, cancel);
//        } else {
//            mProgress.setMessage(resId, cancel);
//        }
//        mProgress.show();
//    }
//
//    public void cancelmDialog() {
//        if (mProgress != null) {
//            mProgress.dismiss();
//        }
//
//    }
//}
