package etcomm.com.etcommyolk.entity;

import java.util.ArrayList;

/**
 * Created by ${tianyue} on 2016/12/13.
 */
public class PointsExchange extends Entity {

    public int code;
    public String message;
    public Content content;

    public class Content {
        public int page_count;

        public ArrayList<PointsExchangeItems> model;

        public int my_score;


    }

}
