package com.davidlares.rfidapp;

import static android.content.ContentValues.TAG;
import androidx.appcompat.app.AppCompatActivity;
import com.davidlares.utils.Configure;
import com.davidlares.utils.Constants;
import com.davidlares.utils.Handler;
import android.widget.TextView;
import com.zebra.rfid.api3.*;
import android.widget.Toast;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.os.Build;

public class MainActivity extends AppCompatActivity {

    private Handler handler;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initiating the textView
        result = findViewById(R.id.result);
        // checking the OS
        if (Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.contains("Motorola Solutions")) {
            // creating the instance
            if(Constants.readers == null) {
                Constants.readers = new Readers(this, ENUM_TRANSPORT.SERVICE_SERIAL);
            }
            // async
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void ...voids) {
                    try {
                        if (Constants.readers != null) {
                            if (Constants.readers.GetAvailableRFIDReaderList() != null) {
                                Constants.availableRFIDReaderList = Constants.readers.GetAvailableRFIDReaderList();
                                if (Constants.availableRFIDReaderList.size() != 0) {
                                    // get first reader from list
                                    Constants.readerDevice = (ReaderDevice) Constants.availableRFIDReaderList.get(0);
                                    Constants.reader = Constants.readerDevice.getRFIDReader();
                                    if (!Constants.reader.isConnected()) {
                                        // Establish connection to the RFID Reader
                                        Constants.reader.connect();
                                        Configure.configureReader();
                                        return true;
                                    }
                                }
                            }
                        }
                    } catch (InvalidUsageException e) {
                        e.printStackTrace();
                    } catch (OperationFailureException e) {
                        e.printStackTrace();
                        Log.d(TAG, "OperationFailureException " + e.getVendorMessage());
                    }
                    return false;
                }

                // onPostExecute
                @Override
                protected void onPostExecute(Boolean value) {
                    super.onPostExecute(value);
                    if (value) {
                        Toast.makeText(getApplicationContext(), "Reader Connected", Toast.LENGTH_LONG).show();
                        result.setText("Reader connected");
                    }
                }
            }.execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Constants.readers != null) {
            try {
                if (Constants.reader != null) {
                    Constants.reader.Events.removeEventsListener(handler);
                    Constants.reader.disconnect();
                    Toast.makeText(getApplicationContext(), "Disconnecting reader", Toast.LENGTH_LONG).show();
                    Constants.reader = null;
                    Constants.readers.Dispose();
                    Constants.readers = null;
                }
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            } catch (OperationFailureException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}