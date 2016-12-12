package etcomm.com.etcommyolk.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Login;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.LoginHandler;
import etcomm.com.etcommyolk.utils.GlobalSetting;

public class AroundFragment extends Fragment {
    /**
     * 控制OnResume()执行次数
     */
    public static boolean limitOnresumSide;
    public static GlobalSetting pres;
    public static Context mContext;
    protected ApiClient client;
    public static String TAG = "AroundFragment";
    @Bind(R.id.save)
    Button save;
    @Bind(R.id.get)
    Button get;
    @Bind(R.id.login)
    Button login;


    @OnClick({R.id.get, R.id.save, R.id.login})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get:
                Toast.makeText(mContext, pres.getUid(), Toast.LENGTH_LONG).show();
                break;
            case R.id.save:
                pres.saveUid("123456");
                break;
            case R.id.login:
                visitorLogin();
                break;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        pres = GlobalSetting.getInstance(mContext);
        client = ApiClient.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_side, container, false);
        ButterKnife.bind(this, view);
        SimpleDraweeView myImageView = (SimpleDraweeView) view.findViewById(R.id.my_image_view);
        myImageView.setImageURI(Uri.parse("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1481514246&di=b84ff611e64fafe69fd2b9bcad7e7ca5&src=http://pic.58pic.com/58pic/12/40/40/21C58PICc7e.jpg"));
        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 接口调试，模拟登录
     */
    public void visitorLogin() {
        JSONObject object = new JSONObject();
        object.put("username", "23652356@qq.com");
        object.put("password", "123456");
        object.put("client_id", "23245538a6de9b824741556172c2d8da");
        object.put("device_id", "ad2c5508b66d5835");
        try {
            StringEntity entity = new StringEntity(object.toJSONString(), "utf-8");
            client.invokeLogin(mContext, entity, new LoginHandler() {
                @Override
                public void onSuccess(Login login) {
                    super.onSuccess(login);
                    Toast.makeText(mContext, login.message, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(BaseException exception) {
                    super.onFailure(exception);
                    Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
