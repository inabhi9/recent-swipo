package in.abhi9.recentswipo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Starting service
        if (AppLogService.getServiceObject() == null) {
            Intent intent = new Intent(this, AppLogService.class);
            startService(intent);
        }
    }
}
