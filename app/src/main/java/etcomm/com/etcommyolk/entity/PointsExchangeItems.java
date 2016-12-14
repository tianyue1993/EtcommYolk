package etcomm.com.etcommyolk.entity;

public class PointsExchangeItems extends Entity {
    public String list_id;

    public String gift_id;

    public String name;

    public String detail;

    public String inventory;

    public String score;

    public String image;

    public String status;
    public String show_money;


    @Override
    public String toString() {
        return "PointsExchangeItems{" +
                "list_id='" + list_id + '\'' +
                ", gift_id=" + gift_id +
                ", name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", inventory=" + inventory +
                ", score=" + score +
                ", image='" + image + '\'' +
                ", status='" + status + '\'' +
                ", show_money='" + show_money + '\'' +
                '}';
    }
}
