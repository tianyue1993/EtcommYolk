package etcomm.com.etcommyolk.entity;

import java.util.List;

public class TopicMember extends Entity {
    public int code;
    public String message;
    public List<TopicUser> content;


    public class TopicUser {
        public String nick_name;
        public String avatar;
        public String structure;
    }

}
