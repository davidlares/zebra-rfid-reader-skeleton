package com.davidlares.utils;

import static android.content.ContentValues.TAG;


import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.ACCESS_OPERATION_STATUS;
import com.zebra.rfid.api3.ACCESS_OPERATION_CODE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.STATUS_EVENT_TYPE;
import com.zebra.rfid.api3.RfidStatusEvents;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.TagData;
import android.os.AsyncTask;
import android.util.Log;

public class Handler implements RfidEventsListener {

    @Override
    public void eventReadNotify(RfidReadEvents rfidReadEvents) {
        TagData[] myTags = Constants.reader.Actions.getReadTags(100);
        if (myTags != null) {
            for (int index = 0; index < myTags.length; index++) {
                Log.d(TAG, "Tag ID " + myTags[index].getTagID());
                if (myTags[index].getOpCode() == ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ &&
                        myTags[index].getOpStatus() == ACCESS_OPERATION_STATUS.ACCESS_SUCCESS) {
                    if (myTags[index].getMemoryBankData().length() > 0) {
                        Log.d(TAG, " Mem Bank Data " + myTags[index].getMemoryBankData());
                    }
                }
            }
        }
    }

    @Override
    public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
        Log.d(TAG, "Status Notification: " + rfidStatusEvents.StatusEventData.getStatusEventType());
        if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {
            if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED) {
                Log.d("RFIDApp", "Trigger pressed");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Constants.reader.Actions.Inventory.perform();
                        } catch (InvalidUsageException e) {
                            e.printStackTrace();
                        } catch (OperationFailureException e) {
                            e.printStackTrace();
                        } 
                        return null;
                    }
                }.execute();
            }

            if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED) {
                Log.d("RFIDApp", "Trigger released");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void ...voids) {
                        try {
                            Constants.reader.Actions.Inventory.stop();
                        } catch (InvalidUsageException e) {
                            e.printStackTrace();
                        } catch (OperationFailureException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            }
        }
    }
}
