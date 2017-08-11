package s011208.memoryloggerlib.info;

import android.os.Debug;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Adapter for MemoryInfoSaver data
 * Created by s011208 on 2017/8/11.
 */

public class MemoryInfoAdapter {
    private static final String TAG = "MemoryInfoAdapter";
    private static final boolean DEBUG = true;

    public static final String TOTAL_PSS = "TotalPss";
    public static final String TIME = "time";
    public static final String PID = "pid";

    private Debug.MemoryInfo mMemoryInfo;
    private JSONObject mJsonObject;

    public MemoryInfoAdapter(Debug.MemoryInfo info) {
        mMemoryInfo = info;
        mJsonObject = toJSON();
    }

    public MemoryInfoAdapter(String jsonString) {
        try {
            mJsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            if (DEBUG) {
                Log.w(TAG, "failed to init MemoryInfoAdapter with json string", e);
            }
        }
    }

    @NonNull
    private JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Map<String, String> map = mMemoryInfo.getMemoryStats();
                Iterator<String> mapKeyIterator = map.keySet().iterator();
                while (mapKeyIterator.hasNext()) {
                    final String mapKey = mapKeyIterator.next();
                    jsonObject.put(mapKey, Integer.valueOf(map.get(mapKey)));
                }
            } else {
                jsonObject.put(TOTAL_PSS, mMemoryInfo.getTotalPss());
            }
            jsonObject.put(TIME, System.currentTimeMillis());
            jsonObject.put(PID, android.os.Process.myPid());
        } catch (JSONException e) {
            if (DEBUG)
                Log.w(TAG, "failed to put into json object, sdk version: " +
                        android.os.Build.VERSION.SDK_INT, e);
        }
        return jsonObject;
    }

    public JSONObject getJsonObject() {
        return mJsonObject;
    }

    @Override
    public String toString() {
        return mJsonObject.toString();
    }

    public int getValue(String key) throws JSONException {
        if (mJsonObject.has(key)) {
            return mJsonObject.getInt(key);
        } else {
            throw new JSONException("cannot find key: " + key);
        }
    }

    @NonNull
    public String[] getKeys() {
        String[] rtn = new String[mJsonObject.length()];
        Iterator<String> keyIterator = mJsonObject.keys();
        int counter = 0;
        while (keyIterator.hasNext()) {
            rtn[counter++] = keyIterator.next();
        }
        return rtn;
    }
}
