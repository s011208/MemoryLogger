package s011208.memorylogger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import s011208.memoryloggerlib.log.LogHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogHelper l = LogHelper.getInstance(this);
        l.setLogInterval(2000);
    }
}
