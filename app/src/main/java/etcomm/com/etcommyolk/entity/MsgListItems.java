package etcomm.com.etcommyolk.entity;

import java.util.List;

public class MsgListItems extends Entity {

    public int code;
    public String message;
    public List<MsgList> content;

    public class MsgList {
        public String news_id;

        public String time;

        public String detail;

        public String title;
        public String topic_name;
        public String topic_list_type;

        public String detail_id;

        public List<String> picNamesArray;

        public String is_like;

        public String user_id;

        public String avatar;

        public String type;
        public String topic_id;

        public String created_at;
    }
}