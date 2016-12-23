package etcomm.com.etcommyolk.entity;

import java.util.ArrayList;

/**
 * Created by ${tianyue} on 2016/12/21.
 */
public class GroupList extends Entity {

    public int code;
    public String message;
    public GroupContent content;

    public class GroupContent {
        public ArrayList<GroupItems> items;
        public String pages;
    }


    @Override
    public String toString() {
        return "GroupList{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", content=" + content +
                '}';
    }
}
