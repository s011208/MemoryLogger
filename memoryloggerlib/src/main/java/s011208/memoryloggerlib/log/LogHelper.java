package s011208.memoryloggerlib.log;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import s011208.memoryloggerlib.info.MemoryInfoSaver;
import s011208.memoryloggerlib.utility.Utilities;

/**
 * Access and store memory logs
 * Created by s011208 on 2017/8/11.
 */

public class LogHelper {

    private static final String TAG = "LogHelper";
    private static final boolean DEBUG = true;

    private static final String HANDLER_THREAD_TAG = "LogHelper Handler";

    /**
     * log every minutes
     */
    private static final long DEFAULT_LOG_INTERVAL = 60 * 1000;

    private static final int DEFAULT_LOG_FILE_COUNT = 5;

    private static final int DEFAULT_LOG_FILE_SIZE = 5 * 1024;

    private static LogHelper sLogHelper;

    public synchronized static LogHelper getInstance(Context context) {
        if (sLogHelper == null) {
            sLogHelper = new LogHelper(context);
        }
        return sLogHelper;
    }

    /**
     * save application context in constructor
     */
    private Context mContext;
    private final HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_TAG);
    private final Handler mHandler;

    {
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    private final Logger mLogger = Logger.getLogger("LogHelper");

    private long mLogInterval = DEFAULT_LOG_INTERVAL;
    private int mLogFileSize = DEFAULT_LOG_FILE_SIZE;
    private int mMaximumLogFileCount = DEFAULT_LOG_FILE_COUNT;

    private LogHelper(Context context) {
        mContext = context.getApplicationContext();
        String processName = Utilities.getProcessName(mContext);

        initLogger(processName);
        run();

        if (DEBUG) {
            Log.i(TAG, "start LogHelper in process " + processName);
        }
    }

    private void initLogger(final String processName) {
        final File logDir = Utilities.getLogDirectory(mContext);
        if (logDir != null) {
            try {
                FileHandler mFileHandler = new FileHandler(logDir.getPath() + File.separator + processName + Utilities.FILE_SPLITTER + "%g." + Utilities.FILE_EXTENSION_NAME,
                        mLogFileSize, mMaximumLogFileCount, true);
                mFileHandler.setFormatter(new Formatter() {
                    @Override
                    public String format(LogRecord logRecord) {
                        return logRecord.getLevel() + ": " + logRecord.getMessage() + "\n";
                    }
                });
                mLogger.addHandler(mFileHandler);
                mLogger.setLevel(Level.ALL);
            } catch (IOException e) {
                if (DEBUG) Log.w(TAG, "failed to create logger", e);
            }
        }
    }

    /**
     * set log interval
     *
     * @param interval a positive number. Unit is millisecond
     * @return LogHelper instance
     */
    public LogHelper setLogInterval(long interval) {
        if (interval <= 0) {
            mLogInterval = DEFAULT_LOG_INTERVAL;
        } else {
            mLogInterval = interval;
        }
        return this;
    }

    /**
     * set maximum log file count.
     *
     * @param fileCount maximum log file count
     * @return LogHelper instance
     */
    public LogHelper setMaximumLogFileCount(int fileCount) {
        if (fileCount <= 0) {
            mMaximumLogFileCount = DEFAULT_LOG_FILE_COUNT;
        } else {
            mMaximumLogFileCount = fileCount;
        }
        return this;
    }

    /**
     * set log file size
     *
     * @param size in byte
     * @return LogHelper instance
     */
    public LogHelper setLogFileSize(int size) {
        if (size <= 1024) {
            mLogFileSize = DEFAULT_LOG_FILE_SIZE;
        } else {
            mLogFileSize = size;
        }
        return this;
    }

    private final Runnable mLogRunnable = new Runnable() {
        @Override
        public void run() {
            new MemoryInfoSaver().save(mContext, mLogger);
            mHandler.removeCallbacks(null);
            mHandler.postDelayed(mLogRunnable, mLogInterval);
        }
    };

    /**
     * start to log
     */
    private void run() {
        mHandler.removeCallbacks(null);
        mHandler.post(mLogRunnable);
    }
}
