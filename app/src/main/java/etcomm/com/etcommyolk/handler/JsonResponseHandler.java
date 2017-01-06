package etcomm.com.etcommyolk.handler;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONTokener;

import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.activity.LoginActivity;
import etcomm.com.etcommyolk.utils.GlobalSetting;

public class JsonResponseHandler extends JsonHttpResponseHandler {

    public JsonResponseHandler() {
        super();
    }

    public JsonResponseHandler(String encoding) {
        super(encoding);
    }

    @Override
    protected Object parseResponse(byte[] responseBody) throws JSONException {
        if (null == responseBody)
            return null;
        Object result = null;
        String jsonString = getResponseString(responseBody, getCharset());

        if (getRequestURI() != null) {
            Log.d("Reqeust:" + getRequestURI().toString(), jsonString);
        }

        if (jsonString != null) {
            jsonString = jsonString.trim();
            if (jsonString.startsWith(UTF8_BOM)) {
                jsonString = jsonString.substring(1);
            }
            if (jsonString.startsWith("{") || jsonString.startsWith("[")) {
                result = new JSONTokener(jsonString).nextValue();
            }
        }

        if (result == null) {
            result = jsonString;
        }
        return result;
    }

    /**
     * 当code=10000时，表示当前账号被异地登录，返回登录页面
     */
    protected synchronized void exceptionCode() {
        Toast.makeText(EtcommApplication.getContext(), R.string.token_error, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EtcommApplication.getContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        EtcommApplication.getContext().startActivity(intent);
        GlobalSetting.getInstance(EtcommApplication.getContext()).clear();
    }
}
