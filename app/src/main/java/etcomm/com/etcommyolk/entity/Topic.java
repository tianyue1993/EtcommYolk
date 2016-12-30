package etcomm.com.etcommyolk.entity;

import java.util.ArrayList;

/**
 * Created by ${tianyue} on 2016/12/24.
 */
public class Topic extends Entity {

    public String avatar;
    public String desc;
    public String user_number;
    public ArrayList<TopicUser> user;
    public String user_id;
    public String is_followed;
    public String is_activity;
    public String activity_rank;
    public String is_rank;
    public String detail_url;


    public class TopicUser {
        public String nick_name;
        public String avatar;
        public String structure;
    }
}
