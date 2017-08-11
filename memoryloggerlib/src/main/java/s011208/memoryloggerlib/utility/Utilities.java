package s011208.memoryloggerlib.utility;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Utilities
 * Created by s011208 on 2017/8/11.
 */

public class Utilities {
    @Nullable
    public static String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo rapi : am.getRunningAppProcesses()) {
            if (rapi.pid == android.os.Process.myPid()) {
                return rapi.processName;
            }
        }
        return null;
    }

    @Nullable
    public static File getLogDirectory(Context context) {
        if (context == null) return null;
        final File contextFileDir = context.getFilesDir();
        if (contextFileDir == null) return null;
        final File logDir = new File(contextFileDir.getAbsolutePath() + File.separator + "logs");
        if (!logDir.exists()) {
            final boolean result = logDir.mkdir();
            if (!result) return null;
        }
        return logDir;
    }
}
