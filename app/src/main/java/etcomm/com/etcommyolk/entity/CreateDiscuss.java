package etcomm.com.etcommyolk.entity;

/**
 * Created by ${tianyue} on 2016/12/15.
 */
public class CreateDiscuss extends Entity {
    public int code;
    public String message;
    public DicussId content;

    public class DicussId {
        public String discussion_id;
    }
}
