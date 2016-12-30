package etcomm.com.etcommyolk.entity;

import java.util.ArrayList;

public class GoodGroup extends Entity {
    public int code;
    public String message;
    public ArrayList<QuitGroup> content;


    public class QuitGroup {
        public String id;
        public String name;
    }

}
