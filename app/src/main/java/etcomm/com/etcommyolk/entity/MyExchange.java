package etcomm.com.etcommyolk.entity;

import java.util.ArrayList;

/**
 * Created by ${tianyue} on 2016/12/13.
 */
public class MyExchange extends Entity {

    public int code;
    public String message;
    public Content content;

    public class Content {
        public String pages;

        public ArrayList<PointsExchangeItems> items;

    }

}
