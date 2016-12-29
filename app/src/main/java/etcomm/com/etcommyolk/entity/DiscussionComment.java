package etcomm.com.etcommyolk.entity;

import java.util.ArrayList;

/**
 * Created by ${tianyue} on 2016/12/24.
 */
public class DiscussionComment extends Entity {
    public int code;
    public String message;
    public CommentContent content;


    public class CommentContent {

        public ArrayList<DisscussCommentItems> items;
        public String pages;

    }
}
