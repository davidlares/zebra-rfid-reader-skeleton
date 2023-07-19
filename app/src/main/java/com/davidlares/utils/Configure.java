package com.davidlares.utils;

import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.START_TRIGGER_TYPE;
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE;
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE;
import com.zebra.rfid.api3.TriggerInfo;
import com.davidlares.utils.Constants;

public class Configure {

    // this is the handler
    private static Handler handler;

    // configuring reader
    public static void configureReader() {
        if (Constants.reader.isConnected()) {
            TriggerInfo triggerInfo = new TriggerInfo();
            triggerInfo.StartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE);
            triggerInfo.StopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE);
            try {
                // receive events from reader
                if (handler == null)
                    handler = new Handler();
                Constants.reader.Events.addEventsListener(handler);
                // HH event
                Constants.reader.Events.setHandheldEvent(true);
                // tag event with tag data
                Constants.reader.Events.setTagReadEvent(true);
                // application will collect tag using getReadTags API
                Constants.reader.Events.setAttachTagDataWithReadEvent(false);
                // set trigger mode as rfid so scanner beam will not come
                Constants.reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true);
                // set start and stop triggers
                Constants.reader.Config.setStartTrigger(triggerInfo.StartTrigger);
                Constants.reader.Config.setStopTrigger(triggerInfo.StopTrigger);
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            } catch (OperationFailureException e) {
                e.printStackTrace();
            }
        }
    }
}
