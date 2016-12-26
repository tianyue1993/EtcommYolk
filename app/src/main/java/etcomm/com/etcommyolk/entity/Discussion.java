package etcomm.com.etcommyolk.entity;

import java.util.ArrayList;

/**
 * Created by ${tianyue} on 2016/12/24.
 */
public class Discussion extends Entity {
    public int code;
    public String message;
    public DicussContent content;


    public class DicussContent {

        public ArrayList<DisscussItems> items;

        public int pages;

        public Topic topic;
    }
}
