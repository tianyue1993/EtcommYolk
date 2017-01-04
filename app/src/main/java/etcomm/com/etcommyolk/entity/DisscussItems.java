package etcomm.com.etcommyolk.entity;


import java.io.Serializable;
import java.util.List;

/**
 * Created by ${tianyue} on 2016/12/24.
 */
public class DisscussItems extends Entity {

    public String comment_number;

    public String discussion_id;

    public String content;

    public String created_at;

    public String like;

    public String user_id;

    public String nick_name;

    public String avatar;

    public String structure;

    public String is_like;

    public String share_type; // 状态 1：分享 0：帖子

    public String share_id;

    public String title;

    public String detail_url;

    public String share_url;
    public List<DisscussPhotosItems> photos;


    public class DisscussPhotosItems implements Serializable {
        public String discussion_image_id;
        public String image;
        public String thumb_image;
    }
}
