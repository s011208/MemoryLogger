package s011208.memoryloggerlib.log;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import s011208.memoryloggerlib.info.MemoryInfoAdapter;
import s011208.memoryloggerlib.utility.Utilities;

/**
 * Created by s011208 on 2017/8/15.
 */

public class LogRetriever {
    private static final String TAG = "LogRetriever";
    private static final boolean DEBUG = true;

    public interface Callback {
        void onRetrieve(Map<String, List<MemoryInfoAdapter>> infoAdapters);

        void onFail(String reason);
    }

    private final WeakReference<Context> mContext;

    public LogRetriever(Context context) {
        mContext = new WeakReference<>(context);

    }

    public Map<String, List<MemoryInfoAdapter>> retrieveLog() {
        return retrieve();
    }

    public void retrieveLogAsync(final Callback cb) {
        new RetrieveLog(this, cb).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Nullable
    private Map<String, List<MemoryInfoAdapter>> retrieve() {
        final Context context = mContext.get();
        if (context == null) return null;

        final Map<String, List<MemoryInfoAdapter>> rtn = new HashMap<>();
        File logDir = Utilities.getLogDirectory(context);
        for (File logFile : logDir.listFiles()) {
            final String logFileName = logFile.getName();
            if (DEBUG) {
                Log.v(TAG, "logFileName: " + logFileName);
            }
            if (!logFileName.endsWith(Utilities.FILE_EXTENSION_NAME)) {
                if (DEBUG) {
                    Log.v(TAG, "logFile extension not match, logFileName: " + logFileName);
                }
                continue;
            }

            final int lastIndexOfFileSplitter = logFileName.lastIndexOf(Utilities.FILE_SPLITTER);
            if (lastIndexOfFileSplitter < 0) {
                if (DEBUG) {
                    Log.w(TAG, "missing file splitter, file name: " + logFileName);
                }
                continue;
            }

            List<MemoryInfoAdapter> memoryInfoAdapterList = new ArrayList<>();
            final String processName = logFileName.substring(0, lastIndexOfFileSplitter);

            String[] logData = Utilities.readFile(logFile.getAbsolutePath()).split(System.lineSeparator());
            for (String data : logData) {
                int indexOfJSONObject = data.indexOf("{");
                if (indexOfJSONObject < 0) continue;
                memoryInfoAdapterList.add(new MemoryInfoAdapter(data.substring(indexOfJSONObject)));
            }
            Collections.sort(memoryInfoAdapterList, new Comparator<MemoryInfoAdapter>() {
                @Override
                public int compare(MemoryInfoAdapter t1, MemoryInfoAdapter t2) {
                    try {
                        return Long.compare(t1.getTime(), t2.getTime());
                    } catch (JSONException e) {
                        return 0;
                    }
                }
            });
            rtn.put(processName, memoryInfoAdapterList);
        }
        return rtn;
    }

    private static class RetrieveLog extends AsyncTask<Void, Void, Map<String, List<MemoryInfoAdapter>>> {
        private final WeakReference<Callback> mCallback;
        private final WeakReference<LogRetriever> mLogRetriever;

        public RetrieveLog(LogRetriever logRetriever, Callback cb) {
            mLogRetriever = new WeakReference<>(logRetriever);
            mCallback = new WeakReference<>(cb);
        }

        @Override
        protected Map<String, List<MemoryInfoAdapter>> doInBackground(Void... voids) {
            if (mLogRetriever.get() == null || mCallback.get() == null) return null;
            return mLogRetriever.get().retrieveLog();
        }


        @Override
        protected void onPostExecute(Map<String, List<MemoryInfoAdapter>> logData) {
            final Callback cb = mCallback.get();
            if (cb == null) return;
            if (logData == null) cb.onFail(null);
            else cb.onRetrieve(logData);
        }
    }
}
