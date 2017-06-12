package in.abhi9.apphistory;

import android.app.Activity;
import android.os.Bundle;

public class SwipeLeftActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        finish();

        AppLogService.getServiceObject().swipeLeft();
    }

}
