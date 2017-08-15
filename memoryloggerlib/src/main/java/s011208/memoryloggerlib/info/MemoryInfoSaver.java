package s011208.memoryloggerlib.info;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * in charge of save memory information
 * Created by s011208 on 2017/8/11.
 */

public class MemoryInfoSaver {
    private static final String TAG = "MemoryInfoSaver";
    private static final boolean DEBUG = true;

    public MemoryInfoSaver() {

    }

    public void save(final Context context, final Logger logger) {
        new SaveMemoryInfoTask(context, logger).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class SaveMemoryInfoTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final WeakReference<Logger> mLogger;

        private SaveMemoryInfoTask(final Context context, final Logger logger) {
            mContext = new WeakReference<>(context);
            mLogger = new WeakReference<>(logger);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Context context = mContext.get();
            if (context == null) return null;
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            Debug.MemoryInfo[] memoryInformation = activityManager.getProcessMemoryInfo(new int[]{android.os.Process.myPid()});
            if (memoryInformation == null || memoryInformation.length == 0) return null;
            MemoryInfoAdapter memoryInfoAdapter = new MemoryInfoAdapter(memoryInformation[0]);
            if (DEBUG) Log.v(TAG, memoryInfoAdapter.toString());
            Logger logger = mLogger.get();
            if (logger == null) return null;
            logger.log(Level.FINEST, memoryInfoAdapter.toString());
            return null;
        }
    }
}
