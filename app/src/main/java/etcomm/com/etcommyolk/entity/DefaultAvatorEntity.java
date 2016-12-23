package etcomm.com.etcommyolk.entity;

import java.util.ArrayList;

/**
 * Created by zuoh on 2016/12/21.
 */
public class DefaultAvatorEntity extends Entity {

    public int code;
    public String message;
    public ArrayList<DefaultAvator> content;

    public class DefaultAvator {

        public int id;

        public String avatar;

        public int gender;

    }

}
