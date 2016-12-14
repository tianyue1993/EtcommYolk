package etcomm.com.etcommyolk.entity;

/**
 * Created by ${tianyue} on 2016/12/13.
 */
public class Exchange extends Entity {

    public int code;
    public String message;
    public Content content;

    public class Content {
        public String gift_address;

    }

}
