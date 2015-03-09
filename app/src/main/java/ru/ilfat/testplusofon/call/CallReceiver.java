package ru.ilfat.testplusofon.call;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.util.Date;
import java.util.List;

import ru.ilfat.testplusofon.CallInfoActivity;

/**
 * Created by userocker on 08.03.2015.
 */
public class CallReceiver extends PhonecallReceiver {

    int MSG_ID_CHECK_TOP_ACTIVITY = 0;
    boolean mDismissed;
    int DELAY_INTERVAL = 700;
    ActivityManager mActivityManager;
    Context ctx;
    String number;

    @Override
    protected void onIncomingCallStarted(final Context ctx, String number, Date start) {
        mActivityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        this.ctx = ctx;
        this.number = number.replace("+", "");
        mHandler.sendEmptyMessageDelayed(MSG_ID_CHECK_TOP_ACTIVITY,
                DELAY_INTERVAL);
    }

    void dismissHandler() {
        mDismissed = true;
        ctx = null;
    }

    /*
     * From http://stackoverflow.com/a/16221978/3085512
     */
    void pickupPhone() {
        Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
        ctx.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
    }

    void startCallInfoActivity() {
        Intent i = new Intent(ctx, CallInfoActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        i.putExtra(CallInfoActivity.EXTRA_PHONE_NUMBER, Long.parseLong(number));
        ctx.startActivity(i);
    }

    /**
     * From http://stackoverflow.com/a/14457786/3085512
     */
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_ID_CHECK_TOP_ACTIVITY && !mDismissed) {
                List<ActivityManager.RunningTaskInfo> tasks = mActivityManager
                        .getRunningTasks(1);
                String topActivityName = tasks.get(0).topActivity
                        .getClassName();
                if (!topActivityName.equals(CallInfoActivity.class.getName())) {
                    // Try to show on top until user dismiss this activity
                    pickupPhone();

                    startCallInfoActivity();

                    dismissHandler();
                }
                sendEmptyMessageDelayed(MSG_ID_CHECK_TOP_ACTIVITY,
                        DELAY_INTERVAL);
            }
        }
    };
}