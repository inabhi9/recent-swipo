package in.abhi9.recentswipo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class SwipeRightActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        finish();

        if (AppLogService.getServiceObject() == null) {
            Intent intent = new Intent(this, AppLogService.class);
            startService(intent);
            Toast bread = Toast.makeText(
                    getApplicationContext(), R.string.starting_service, Toast.LENGTH_LONG
            );
            bread.show();
        } else {
            AppLogService.getServiceObject().swipeRight();
        }
    }
}
