package etcomm.com.etcommyolk.entity;

/**
 * Created by ${tianyue} on 2016/12/15.
 */
public class RecommendItems extends Entity {
    public String topic_id;
    public String news_id;
    public String id;
    public String title;
    public String image;
    public String start_at;
    public String end_at;
    public String user_num;
    public String type;
    public String status;
    public String share_url;
    public String detail_url;
    public String pv;//浏览量
    public String desc;
    public String activity_id;
    public String customer_id;
    public String deadline;
    public String number;


    @Override
    public String toString() {
        return "RecommendItems{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", start_at='" + start_at + '\'' +
                ", end_at='" + end_at + '\'' +
                ", user_num='" + user_num + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", share_url='" + share_url + '\'' +
                ", detail_url='" + detail_url + '\'' +
                ", pv='" + pv + '\'' +
                '}';
    }
}
