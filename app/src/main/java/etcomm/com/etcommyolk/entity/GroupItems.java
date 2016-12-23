package etcomm.com.etcommyolk.entity;

/**
 * Created by ${tianyue} on 2016/12/21.
 */
public class GroupItems  extends  Entity{

    public String follows;//关注用户数

    public String discussion_number;//帖子数

    public String topic_id;//小组ID

    public String name;//小组名称

    public String user_id; //创建小组用户ID, 如果为0是后台创建的小组

    public String is_followed;//用户是否已关注

    public String desc; //详情
    public String avatar;
    public String activity_id;//活动id  0代表普通小组 非0代表活动小组
    public String activity_rank;//活动排名地址

}
