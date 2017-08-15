package s011208.memorylogger;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import s011208.linechartlib.views.LineChart;
import s011208.memoryloggerlib.info.MemoryInfoAdapter;
import s011208.memoryloggerlib.log.LogHelper;
import s011208.memoryloggerlib.log.LogRetriever;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = true;

    private LineChart mLineChart;

    private LogRetriever.Callback mLogRetrieverCallback = new LogRetriever.Callback() {
        @Override
        public void onRetrieve(Map<String, List<MemoryInfoAdapter>> infoAdapters) {
            if (DEBUG) {
                Iterator<String> iterator = infoAdapters.keySet().iterator();
                String key = null;
                while (iterator.hasNext()) {
                    key = iterator.next();
                    break;
                }
                if (key == null) return;
                List<MemoryInfoAdapter> adapters = infoAdapters.get(key);
                List<Point> points = new ArrayList<>();
                try {
                    long firstTime = adapters.get(0).getTime();
                    for (MemoryInfoAdapter adapter : adapters) {
                        Point p = new Point();
                        p.y = adapter.getValue("summary.total-pss");
                        p.x = (int) (adapter.getTime() - firstTime);
                        points.add(p);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mLineChart.setPoints(points);
            }
        }

        @Override
        public void onFail(String reason) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogHelper l = LogHelper.getInstance(this);
        l.setLogInterval(2000);

        mLineChart = (LineChart) findViewById(R.id.lineChart);
        LogRetriever retriever = new LogRetriever(this);
        retriever.retrieveLogAsync(mLogRetrieverCallback);
    }
}
