package in.abhi9.recentswipo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class OmniSwitchToggleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent("org.omnirom.omniswitch.ACTION_TOGGLE_OVERLAY");
        sendBroadcast(intent);
        finish();
    }
}
