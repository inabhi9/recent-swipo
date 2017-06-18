package in.abhi9.recentswipo;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AppLogService extends Service {
    private static AppLogService self = null;
    final String TAG = "AppLogService";
    private String lastLaunchedBySwipe = "";
    private ActivityManager mActivityManager;
    private List<ActivityManager.RecentTaskInfo> recents = new ArrayList<>();
    private Integer next = 0;
    private ActivityManager.RecentTaskInfo launcherTask;

    public static AppLogService getServiceObject() {
        return self;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Log.d(TAG, "Service started");

        self = this;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "FirstService destroyed");
        super.onDestroy();
    }

    public void swipeLeft() {
        ActivityManager.RecentTaskInfo nextApp;

        this.initList();

        if (Objects.equals(getCurrentActivity(), getHomeLauncher())) {
            next = 0;
            Log.d(TAG, "Currently on Home. Launching 1st app");
        }

        if ((nextApp = getNextActiveApp("forward")) == null) {
            Log.d(TAG, "SL - No next active app found");
            return;
        }

        Log.d(TAG, "App to Launch:" + nextApp.baseIntent.getComponent().getPackageName());

        mActivityManager.moveTaskToFront(
                nextApp.id,
                ActivityManager.MOVE_TASK_NO_USER_ACTION,
                getStartActivityOption("left").toBundle()
        );
        lastLaunchedBySwipe = nextApp.baseIntent.getComponent().getPackageName();
        next++;
        Log.d(TAG, "SL - Next: " + next);
    }

    public void swipeRight() {
        ActivityManager.RecentTaskInfo nextApp;

        this.initList();

        if (Objects.equals(getCurrentActivity(), getHomeLauncher())) {
            next = 0;
            Log.d(TAG, "SR - Currently on Home");
            return;
        }

        if ((nextApp = getNextActiveApp("backward")) == null) {
            Log.d(TAG, "SL - No previous active app found. Going home.");
            mActivityManager.moveTaskToFront(
                    launcherTask.id,
                    ActivityManager.MOVE_TASK_NO_USER_ACTION,
                    getStartActivityOption("right").toBundle()
            );
            next = 0;
            return;
        }

        Log.d(TAG, "App to Launch:" + nextApp.baseIntent.getComponent().getPackageName());

        mActivityManager.moveTaskToFront(
                nextApp.id,
                ActivityManager.MOVE_TASK_NO_USER_ACTION,
                getStartActivityOption("right").toBundle()
        );
        lastLaunchedBySwipe = nextApp.baseIntent.getComponent().getPackageName();
        next--;
        Log.d(TAG, "SL - Next: " + next);
    }

    private void initList() {
        if (this.recents.size() == 0
                || !Objects.equals(getCurrentActivity(), lastLaunchedBySwipe)) {
            this.recents = getRecents();
            this.recents.add(0, launcherTask);
            next = 1;
            Log.d(TAG, "App launch sequence changed externally");
        }

    }

    private ActivityManager.RecentTaskInfo getNextActiveApp(String direction) {
        List<ActivityManager.RecentTaskInfo> recentTasks = getRecents();
        List<Integer> ids = new ArrayList<>();

        for (ActivityManager.RecentTaskInfo task : recentTasks) {
            ids.add(task.id);
        }

        if (Objects.equals(direction, "forward")) {
            for (Integer i = this.next + 1; i < this.recents.size(); i++) {
                if (ids.contains(this.recents.get(i).id)) return this.recents.get(i);
            }
        } else {
            for (Integer i = this.next - 1; i > -1; i--) {
                if (ids.contains(this.recents.get(i).id)) return this.recents.get(i);
            }
        }

        return null;
    }

    private List<ActivityManager.RecentTaskInfo> getRecents() {

        ArrayList<ActivityManager.RecentTaskInfo> packages = new ArrayList<>();
        final String homeLauncherPackage = getHomeLauncher();

        final List<ActivityManager.RecentTaskInfo> recentTasks = mActivityManager
                .getRecentTasks(Integer.MAX_VALUE,
                        ActivityManager.RECENT_IGNORE_UNAVAILABLE | 4 |
                                ActivityManager.RECENT_WITH_EXCLUDED);

        Log.d(TAG, "---------------------------------------------");
        for (ActivityManager.RecentTaskInfo task : recentTasks) {
            if (task.id < 0) continue;
            String packageName = task.baseIntent.getComponent().getPackageName();

            // Ignoring this app
            if (Objects.equals(packageName, getPackageName())) continue;
            // Ignoring home package
            if (Objects.equals(packageName, homeLauncherPackage)) {
                launcherTask = task;
                continue;
            }
            // Ignoring any system ui activity
            if (Objects.equals(packageName, "com.android.systemui")) continue;

            packages.add(task);
        }

        Log.d(TAG, "Recents: " + packages.toString());
        return packages;
    }

    private String getCurrentActivity() {
        final ActivityManager mActivityManager = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);

        final List<ActivityManager.RecentTaskInfo> recentTasks = mActivityManager
                .getRecentTasks(2,
                        ActivityManager.RECENT_IGNORE_UNAVAILABLE | 4 |
                                ActivityManager.RECENT_WITH_EXCLUDED);

        String currentActivity = recentTasks.get(1).baseIntent.getComponent().getPackageName();

        Log.d(TAG, "Current activity: " + currentActivity);

        return currentActivity;
    }

    public String getHomeLauncher() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo defaultLauncher = getPackageManager().resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY
        );

        return defaultLauncher.activityInfo.packageName;
    }

    private ActivityOptions getStartActivityOption(String animDirection) {
        // Animation options
        ArrayList<Integer> anim = new ArrayList<>();
        if (Objects.equals(animDirection, "right")) {
            anim.add(android.R.anim.slide_in_left);
            anim.add(android.R.anim.slide_out_right);
        } else {
            anim.add(R.anim.slide_in_right);
            anim.add(R.anim.slide_out_left);
        }

        return ActivityOptions.makeCustomAnimation(this,
                anim.get(0),
                anim.get(1)
        );
    }
}
