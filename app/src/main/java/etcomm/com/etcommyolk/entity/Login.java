package etcomm.com.etcommyolk.entity;

/**
 * Created by ${tianyue} on 2016/12/10.
 */
public class Login extends Entity {
    public int code;
    public String message;
    public Content content;

    public class Content {
        public String id; //用户ID
        public String department_id; //组织结构ID
        public String serial_number_id;//激活码ID
        public String avatar; //用户头像
        public String mobile; //手机
        public String nick_name; //用户昵称
        public String real_name; //真实姓名
        public String access_token; //token
        public String structure_id; // 组织ID
        public String structure; //组织名称
        public String customer_id; //客户ID
        public String gender; //性别 1男
        public String job_number; //工号
        public String email; //邮箱
        public String height; //身高
        public String weight; //体重
        public String score; // 积分
        public String total_score; //累积积分
        public String pedometer_target; //运动目标
        public String pedometer_distance; //运动积累距离
        public String pedometer_time;//运动积累时间
        public String pedometer_consume;//运动积累消耗
        public String is_leader; //是否领导
        public String created_at; //注册时间
        public String birthday; //生日
        public String birth_year; //出生年份
        public String is_sign; ////签到状态 1:已签到 0：未签到
        public String customer_image; //app启动页
        public String info_status; //用户信息是否完整 1：完整 0：不完整
        public String is_like;//消息推送赞开关 1：开启 0：关闭
        public String is_comment; //消息推送评论/回复开关 1：开启 0:关闭
        public String islevel; //1 :属于分级权限 0：没有分级权限

    }

    @Override
    public String toString() {
        return "Login{" +
                "content=" + content +
                '}';
    }
}
