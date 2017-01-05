package etcomm.com.etcommyolk.entity;

/**
 * Created by zuoh on 2016/12/28.
 */
public class DaySignUp extends Entity {
    public int code;
    public String message;
    public SignIn content;

    public class SignIn{

        public String day;
        public String score;

    }

}

//{
//        "code": 0,
//        "message": "签到成功",
//        "content": {
//        "day": "4", //连续天数
//        "score": "4990002" //获得积分
//        }
//        }