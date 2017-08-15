package s011208.linechartlib.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s011208 on 2017/8/15.
 */

public class LineChart extends View {
    private static final String TAG = "LineChart";
    private static final boolean DEBUG = true;

    private final int UNIT_COUNT = 200;
    private final List<Point> mPoints = new ArrayList<>();

    private int mMaxValue = Integer.MIN_VALUE;
    private int mMinValue = Integer.MAX_VALUE;

    private final Paint mPaint = new Paint();

    {
        mPaint.setColor(Color.BLUE);
    }

    public LineChart(Context context) {
        this(context, null);
    }

    public LineChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initValues() {
        for (Point point : mPoints) {
            mMaxValue = Math.max(mMaxValue, point.y);
            mMinValue = Math.min(mMinValue, point.y);
        }
    }

    public void setPoints(List<Point> points) {
        mPoints.clear();
        mPoints.addAll(points);
        if (DEBUG) {
            Log.d(TAG, "setPoints size: " + mPoints.size());
        }
        invalidate();
    }

    public void addPoints(Point point) {
        mPoints.add(point);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (DEBUG) {
            Log.d(TAG, "onDraw");
        }
        super.onDraw(canvas);
        if (mPoints.isEmpty()) return;

        initValues();

        final float unit = mMaxValue / UNIT_COUNT;
        for (int i = 1; i < mPoints.size(); ++i) {
            Log.d(TAG, "draw " + mPoints.get(i - 1).x + ", " + mPoints.get(i - 1).y + ", unit: " + unit);
            canvas.drawLine((i - 1) * 30, mPoints.get(i - 1).y / unit, i * 30, mPoints.get(i).y / unit, mPaint);
        }
    }
}
