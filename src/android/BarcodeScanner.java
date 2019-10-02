package com.pebuu.scanner;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.StringWriter;


import android.app.Activity;
import android.os.AsyncTask;

//import com.zebra.adc.decoder.BarCodeReader;

public class BarcodeScanner extends CordovaPlugin implements BarCodeReader.DecodeCallback {
        // states
    static final int STATE_IDLE = 0;
    static final int STATE_DECODE = 1;
    static final int STATE_HANDSFREE = 2;
    static final int STATE_PREVIEW = 3; // snapshot preview mode
    static final int STATE_SNAPSHOT = 4;
    static final int STATE_VIDEO = 5;
    static final int STATE_GEN_PREVIEW = 6; // generic (non-SDL) preview mode
    static final int STATE_GEN_SURFACE = 7; // Surface Created

        // system
    private ToneGenerator tg = null;

    // BarCodeReader specifics
    private BarCodeReader bcr = null;

    private boolean beepMode = true; // decode beep enable
    private boolean snapPreview = false; // snapshot preview mode enabled - true
    // - calls viewfinder which gets
    // handled by
    private int trigMode = BarCodeReader.ParamVal.LEVEL;
    private boolean atMain = false;
    private int state = STATE_IDLE;
    private int decodes = 0;

    private int motionEvents = 0;
    private int modechgEvents = 0;

    private String decodeDataString;
    private String decodeStatString;
    private static int decCount = 0;
    
    

    static {
        System.loadLibrary("IAL");
        
        System.loadLibrary("SDL");
        
        if (android.os.Build.VERSION.SDK_INT >= 26)
            System.loadLibrary("barcodereader80"); // Android 8.0
        /*if (android.os.Build.VERSION.SDK_INT >= 24)
            System.loadLibrary("barcodereader70"); // Android 7.0
        else if (android.os.Build.VERSION.SDK_INT >= 19)
            System.loadLibrary("barcodereader44"); // Android 4.4
        else if (android.os.Build.VERSION.SDK_INT >= 18)
            System.loadLibrary("barcodereader43"); // Android 4.3
        else
            System.loadLibrary("barcodereader"); // Android 2.3 - Android 4.2
        */
        // System.loadLibrary("barcodereader"); // Android 2.3 - Android 4.2

        
        
    }  

    
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView)
    {
        super.initialize(cordova, webView);
        //wedge = new DataWedgeIntentHandler(cordova.getActivity().getBaseContext());
        
        // sound
        tg = new ToneGenerator(AudioManager.STREAM_MUSIC,
                ToneGenerator.MAX_VOLUME);
        showToast("Zebra-SDLgui mainScreen ");
    } 

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            //listBT(callbackContext);
            return true;
        } else if (action.equals("performAdd")) {
            int arg1 = args.getInt(0);
            int arg2 = args.getInt(1);
            /* Indicating success is failure is done by calling the appropriate method on the 
            callbackContext.*/
            int result = arg1 + arg2;
            callbackContext.success(android.os.Build.VERSION.SDK_INT + " result calculated in Java: " + result);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
    // ----------------------------------------
    @Override
    public void onEvent(int event, int info, byte[] data, BarCodeReader reader) {
        switch (event) {
            case BarCodeReader.BCRDR_EVENT_SCAN_MODE_CHANGED:
                ++modechgEvents;
                showToast("Scan Mode Changed Event (#" + modechgEvents + ")");
                break;

            case BarCodeReader.BCRDR_EVENT_MOTION_DETECTED:
                ++motionEvents;
                showToast("Motion Detect Event (#" + motionEvents + ")");
                break;

            case BarCodeReader.BCRDR_EVENT_SCANNER_RESET:
                // dspStat("Reset Event"); // No need to display this event
                break;

            default:
                // process any other events here
                break;
        }
    }
        // ----------------------------------------
    // BarCodeReader.DecodeCallback override
    @Override
    public void onDecodeComplete(int symbology, int length, byte[] data,
            BarCodeReader reader) 
    {
    
    }

    public void showToast(String msg){
        Toast.makeText(cordova.getActivity().getWindow().getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}