package com.example.calllogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver {

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static boolean isIncoming;
    private static String savedNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!"android.intent.action.PHONE_STATE".equals(intent.getAction())) {
            return;
        }

        String stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        int state = 0;
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(stateStr)) {
            state = TelephonyManager.CALL_STATE_IDLE;
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(stateStr)) {
            state = TelephonyManager.CALL_STATE_OFFHOOK;
        } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(stateStr)) {
            state = TelephonyManager.CALL_STATE_RINGING;
        }

        onCallStateChanged(context, state, number);
    }

    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            return;
        }

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                if (number != null) savedNumber = number;
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    if (number != null) savedNumber = number;
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                // Call ended
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    // Missed
                    openLogActivity(context, savedNumber, "Missed");
                } else if (isIncoming) {
                    openLogActivity(context, savedNumber, "Incoming");
                } else {
                    openLogActivity(context, savedNumber, "Outgoing");
                }
                break;
        }
        lastState = state;
    }

    private void openLogActivity(Context context, String number, String type) {
        if (number == null || number.isEmpty()) number = "Unknown";
        
        Intent intent = new Intent(context, LogActivity.class);
        intent.putExtra("phone_number", number);
        intent.putExtra("call_type", type);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace(); // On modern Android, starting activity from bg may be blocked without display-over-other-apps permission
        }
    }
}
