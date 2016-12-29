package etcomm.com.etcommyolk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.TopicMember;

public class MemberListAdapter extends YolkBaseAdapter<TopicMember.TopicUser> {


    public MemberListAdapter(Context context, List<TopicMember.TopicUser> List) {
        super(context);
        mList = List;
        mContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_topic_member, null);
            viewHolder.member_image = (SimpleDraweeView) convertView.findViewById(R.id.member_image);
            viewHolder.attention_member = (TextView) convertView.findViewById(R.id.attention_member);
            viewHolder.structure_name = (TextView) convertView.findViewById(R.id.structure_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TopicMember.TopicUser mInfo = mList.get(position);
        if (mInfo != null) {
            viewHolder.attention_member.setText(mInfo.nick_name);
            viewHolder.structure_name.setText(mInfo.structure);
            viewHolder.member_image.setImageURI(mInfo.avatar);
        }

        return convertView;
    }

    private static class ViewHolder {
        SimpleDraweeView member_image;
        TextView attention_member;
        TextView structure_name;
    }


}
