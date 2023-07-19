package com.davidlares.utils;

import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.ReaderDevice;
import com.zebra.rfid.api3.Readers;
import java.util.ArrayList;

public class Constants {
    public static Readers readers;
    public static ArrayList availableRFIDReaderList;
    public static ReaderDevice readerDevice;
    public static RFIDReader reader;
    public static String TAG = "DEMO";
}
