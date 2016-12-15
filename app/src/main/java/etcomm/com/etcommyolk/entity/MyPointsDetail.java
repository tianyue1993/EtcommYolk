package etcomm.com.etcommyolk.entity;

import java.util.ArrayList;

/**
 * Created by ${tianyue} on 2016/12/14.
 */
public class MyPointsDetail extends Entity {
    public int code;
    public String message;
    public Content content;

    public class Content {

        public String page_count;
        public ArrayList<Score> score;

        public class Score {
            public String type;

            public String score;

            public String description;

            public String income_and_expenditure;

            public String created_at;
        }


        @Override
        public String toString() {
            return "Content{" +
                    "page_count='" + page_count + '\'' +
                    ", score=" + score +
                    '}';
        }
    }
}
