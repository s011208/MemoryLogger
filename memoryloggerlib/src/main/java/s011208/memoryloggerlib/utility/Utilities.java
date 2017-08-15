package s011208.memoryloggerlib.utility;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utilities
 * Created by s011208 on 2017/8/11.
 */

public class Utilities {
    private static final String TAG = "Utilities";
    private static final boolean DEBUG = true;

    public static final String FILE_SPLITTER = "-";
    public static final String FILE_EXTENSION_NAME = ".txt";

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

    @Nullable
    public static String readFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            if (DEBUG) {
                Log.w(TAG, "file not found", e);
            }
        } catch (IOException e) {
            if (DEBUG) {
                Log.w(TAG, "io exception", e);
            }
        }
        return null;
    }
}
