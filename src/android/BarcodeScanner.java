package com.pebuu.scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.StringWriter;
import java.io.PrintWriter;

import android.app.Activity;
import android.os.AsyncTask;

import com.zebra.adc.decoder.Barcode2DWithSoft;

public class BarcodeScanner extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            //listBT(callbackContext);
            return true;
        }
    }

        private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

}