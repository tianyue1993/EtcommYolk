package etcomm.com.etcommyolk.handler;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONTokener;

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
            Log.d("Reqeust:"+getRequestURI().toString(), jsonString);
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
}
