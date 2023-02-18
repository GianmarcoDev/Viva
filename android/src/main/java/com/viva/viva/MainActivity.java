package com.viva.viva;

import android.content.Context;
import android.os.Build;
import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.LibraryManager.OmronPeripheralManager;
import android.os.Bundle;
import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.OmronUtility.OmronConstants;
import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.Model.OmronPeripheral;
import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.DeviceConfiguration.OmronPeripheralManagerConfig;
import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.Model.OmronErrorInfo;
import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.Interface.OmronPeripheralManagerConnectListener;
import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.Interface.OmronPeripheralManagerConnectStateListener;
import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.Interface.OmronPeripheralManagerDataTransferListener;
import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.Interface.OmronPeripheralManagerDisconnectListener;
import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.Interface.OmronPeripheralManagerScanListener;
import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.Interface.OmronPeripheralManagerStopScanListener;
import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.Interface.OmronPeripheralManagerUpdateListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Bundle;
import com.viva.viva.adapter.ScannedDevicesAdapter;
import com.viva.viva.utility.Constants;
import com.viva.viva.models.PreferencesManager;
import com.omronhealthcare.OmronConnectivityLibrary.OmronLibrary.Model.OmronErrorInfo;
import java.util.*;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import androidx.annotation.Nullable;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import io.flutter.plugin.common.EventChannel;
import java.util.stream.*;

public class MainActivity extends FlutterActivity {

  // apikey 51DFC54A-0F99-4FF0-8AEA-71F5987FFF06
  private String apiKey = "51DFC54A-0F99-4FF0-8AEA-71F5987FFF06";
  private static Context mContext;
  static Boolean isScan = false;
  final String TAG = "DeviceList";
  private Bundle weightBundle;
  static ArrayList<OmronPeripheral> mPeripheralList;
  static ScannedDevicesAdapter mScannedDevicesAdapter;
  // ConnectedDeviceAdapter mConnectedDeviceAdapter;
  List<HashMap<String, String>> fullDeviceList;
  HashMap<String, String> device;
  private int mSelectedUser = 1;
  private ArrayList<Integer> selectedUsers = new ArrayList<>();
  private final int TIME_INTERVAL = 1000;
  private static OmronPeripheral mSelectedPeripheral;
   Handler mHandler;
   Bundle mBundle = null;
   Runnable mRunnable;
  private PreferencesManager preferencesManager = null;
  private HashMap<String, String> profileSettings = null;
  private Intent mIntent = null;

  public  final String STREAMISSCAN = "isscan";
  private static EventChannel.EventSink attachIsScanEvent;
  private static Handler isScanHandler;

    public  final String STREAMSCAN = "scan";
    private static EventChannel.EventSink attachScanEvent;
    final String TAG_NAME = "From_Native";
    private static Handler scanHandler;

    public  final String STREAMCONNESSION = "conn";
    private static EventChannel.EventSink attachConnessionEvent;
    private static Handler connessionHandler;
    static int devicePosition = 0;

    public  final String STREAMTRANSFER = "transfer";
    private static EventChannel.EventSink attachTransferEvent;
    private static Handler transferHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void configureFlutterEngine​(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new EventChannel(Objects.requireNonNull(getFlutterEngine()).getDartExecutor(), STREAMISSCAN).setStreamHandler(
                new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object args, final EventChannel.EventSink events) {
                        Log.w(TAG_NAME, "Adding listener");

                        attachIsScanEvent = events;

                        isScanHandler = new Handler();
                        

                    }

                    @Override
                    public void onCancel(Object args) {

                        isScanHandler = null;

                        attachIsScanEvent = null;
                        System.out.println("StreamHandler - onCanceled: ");

                    }
                });

        new EventChannel(Objects.requireNonNull(getFlutterEngine()).getDartExecutor(), STREAMSCAN).setStreamHandler(
                new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object args, final EventChannel.EventSink events) {
                        Log.w(TAG_NAME, "Adding listener");

                        attachScanEvent = events;

                        scanHandler = new Handler();
                        

                    }

                    @Override
                    public void onCancel(Object args) {

                        scanHandler = null;

                        attachScanEvent = null;
                        System.out.println("StreamHandler - onCanceled: ");

                    }
                });
        new EventChannel(Objects.requireNonNull(getFlutterEngine()).getDartExecutor(), STREAMCONNESSION)
                .setStreamHandler(
                        new EventChannel.StreamHandler() {
                            @Override
                            public void onListen(Object args, final EventChannel.EventSink events) {
                                Log.w(TAG_NAME, "Adding Connession listener");
                                
                                attachConnessionEvent = events;

                                connessionHandler = new Handler();


                            }

                            @Override
                            public void onCancel(Object args) {
                                attachConnessionEvent.endOfStream();
                                connessionHandler = null;

                                 attachConnessionEvent = null;

                            }
                        });
                        new EventChannel(Objects.requireNonNull(getFlutterEngine()).getDartExecutor(), STREAMTRANSFER)
                        .setStreamHandler(
                                new EventChannel.StreamHandler() {
                                    @Override
                                    public void onListen(Object args, final EventChannel.EventSink events) {
                                        Log.w(TAG_NAME, "Adding transfer listener");
                                        
                                        attachTransferEvent = events;
        
                                        transferHandler = new Handler();
        
        
                                    }
        
                                    @Override
                                    public void onCancel(Object args) {
                                        attachTransferEvent.endOfStream();
                                        transferHandler = null;
        
                                        attachTransferEvent = null;
        
                                    }
                                });
    }  

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // scanHandler.removeCallbacks(runnable);
        // scanHandler = null;
        // attachScanEvent = null;
        // connessionHandler = null;
        // attachConnessionEvent = null;
        // transferHandler=null;
        // attachTransferEvent=null;
        // isScanHandler=null;
        // attachIsScanEvent=null;
    }

    //// fine stream

    
    private void showErrorLoadingDevices() {
        if (fullDeviceList.size() == 0) {
            String information = "Invalid Library API key configured OR no devices supported for API Key. Please try again. ";
            System.out.println(information);
        } else {
            System.out.println("lunghezza lista" + fullDeviceList.size());
            for (HashMap<String, String> hashMap : fullDeviceList) {
                if (hashMap.get("modelName").equals("VIVA")) {
                    device = hashMap;
                    System.out.println(device);
                }
            }
        }
    }

    public void init(Context context, int height, String bday, int gender, int user) {
        String g = "Male";
        if (gender == 0) {
            g = "Female";
        }

        System.out.println("                     " + height + " " + bday + " " + gender + " " + user);
        mContext = context;

        HashMap<String, String> settingsModel = new HashMap<String, String>();
        settingsModel.put("personalHeight", String.valueOf(height));
        settingsModel.put("personalWeight", "90");
        settingsModel.put("personalStride", "1");

        Intent toMain = new Intent();
        Log.d(TAG, toMain.toString());
        Bundle bundle = new Bundle();
        toMain.putExtra(Constants.extraKeys.KEY_SELECTED_DEVICE, device);
        toMain.putExtra(Constants.extraKeys.KEY_PERSONAL_SETTINGS, settingsModel);
        toMain.putExtra(Constants.extraKeys.KEY_SELECTED_USER, String.valueOf(user));
        bundle.putString(Constants.bundleKeys.KEY_BUNDLE_WEIGHT_UNIT, "Kg");

        bundle.putString(Constants.bundleKeys.KEY_BUNDLE_DOB, bday);
        bundle.putString(Constants.bundleKeys.KEY_BUNDLE_GENDER, g);
        bundle.putString(Constants.bundleKeys.KEY_BUNDLE_HEIGHT_CM, String.valueOf(height));
        mBundle = bundle;
        toMain.putExtra(Constants.extraKeys.KEY_WEIGHT_SETTINGS, mBundle);

        mIntent = toMain;
        Log.d(TAG, settingsModel.toString());

        if (preferencesManager == null)
            preferencesManager = new PreferencesManager(mContext);

        selectedUsers.add(1);

        device = (HashMap<String, String>) mIntent.getSerializableExtra(Constants.extraKeys.KEY_SELECTED_DEVICE);

        weightBundle = mBundle;

        profileSettings = (HashMap<String, String>) mIntent
                .getSerializableExtra(Constants.extraKeys.KEY_PERSONAL_SETTINGS);
        System.out.println("                                                        done");

        initLists();

        // Start OmronPeripheralManager
        startOmronPeripheralManager(false);
    }

    private void initLists() {
        mPeripheralList = new ArrayList<OmronPeripheral>();
        mScannedDevicesAdapter = new ScannedDevicesAdapter(mContext, mPeripheralList);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (mMessageReceiver != null)
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);

            // Get extra data included in the Intent
            final int status = intent.getIntExtra(OmronConstants.OMRONConfigurationStatusKey, 0);

            if (status == OmronConstants.OMRONConfigurationStatus.OMRONConfigurationFileSuccess) {

                System.out.println("Config File Extract Success");
                System.out.println("Controllo intento" + intent);
                System.out.println("Controllo context" + mContext);
                loadDeviceList();

            } else if (status == OmronConstants.OMRONConfigurationStatus.OMRONConfigurationFileError) {
                System.out.println("Config File Extract Failure");
            } else if (status == OmronConstants.OMRONConfigurationStatus.OMRONConfigurationFileUpdateError) {
                System.out.println("Config File Update Failure");
            }

        }
    };

    private void loadDeviceList() {

        fullDeviceList = new ArrayList<HashMap<String, String>>();

        if (OmronPeripheralManager.sharedManager(mContext).retrieveManagerConfiguration(mContext) != null) {
            fullDeviceList = (List<HashMap<String, String>>) OmronPeripheralManager.sharedManager(mContext)
                    .retrieveManagerConfiguration(mContext).get(OmronConstants.OMRONBLEConfigDeviceKey);
            showErrorLoadingDevices();
        }
    }

    public void loadConfiguration(Context context) {
        mContext = context;
        OmronPeripheralManager.sharedManager(mContext).setAPIKey(apiKey, null);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                new IntentFilter(OmronConstants.OMRONBLEConfigDeviceAvailabilityNotification));
    }









    public void scan(Context context) {
        int bluetoothState = OmronPeripheralManager.sharedManager(mContext).getBluetoothState();

        if (bluetoothState == OmronConstants.OMRONBLEBluetoothState.OMRONBLEBluetoothStateUnknown) {

            Log.d(TAG, "Bluetooth is in unknown state");

        } else if (bluetoothState == OmronConstants.OMRONBLEBluetoothState.OMRONBLEBluetoothStateOff) {

            Log.d(TAG, "Bluetooth is currently powered off");

        } else if (bluetoothState == OmronConstants.OMRONBLEBluetoothState.OMRONBLEBluetoothStateOn) {

            Log.d(TAG, "Bluetooth is currently powered on");
            startScanning();
        }

    }

    private void startOmronPeripheralManager(boolean isHistoricDataRead) {
        OmronPeripheralManagerConfig peripheralConfig = OmronPeripheralManager.sharedManager(mContext)
                .getConfiguration();
        Log.d(TAG, "Library Identifier : " + peripheralConfig.getLibraryIdentifier());

        // Filter device to scan and connect (optional)
        if (device != null && device.get(OmronConstants.OMRONBLEConfigDevice.GroupID) != null
                && device.get(OmronConstants.OMRONBLEConfigDevice.GroupIncludedGroupID) != null) {

            // Add item
            List<HashMap<String, String>> filterDevices = new ArrayList<>();
            filterDevices.add(device);
            peripheralConfig.deviceFilters = filterDevices;
        }
        Log.d(TAG, "Aggiunto filtro device");
        ArrayList<HashMap> deviceSettings = new ArrayList<>();
        Log.d(TAG, "Imposto settings e timeout");
        // BCM device settings (optional)
        // deviceSettings = getBCMSettings(deviceSettings);
        Log.d(TAG, "-prendo settings BCM");
        peripheralConfig.deviceSettings = deviceSettings;
        // Set Scan timeout interval (optional)
        peripheralConfig.timeoutInterval = 15;
        Log.d(TAG, "-Settom bluetooth timeout");
        // Set User Hash Id (mandatory)
        peripheralConfig.userHashId = "email@gmail.com"; // Set logged in user email
        System.out.println("-Imposto email " + peripheralConfig.userHashId);

        Log.d(TAG, "Settings impostate");
        // Disclaimer: Read definition before usage
        if (Integer.parseInt(device
                .get(OmronConstants.OMRONBLEConfigDevice.Category)) != OmronConstants.OMRONBLEDeviceCategory.ACTIVITY) {
            // Reads all data from device.
            peripheralConfig.enableAllDataRead = isHistoricDataRead;
        }
        System.out.println("isHistoricDataRead " + isHistoricDataRead);

    }

    private ArrayList<HashMap> getBCMSettings(ArrayList<HashMap> deviceSettings) {

        // body composition
        if (Integer.parseInt(device.get(
                OmronConstants.OMRONBLEConfigDevice.Category)) == OmronConstants.OMRONBLEDeviceCategory.BODYCOMPOSITION) {

            // Weight settings
            HashMap<String, Object> weightPersonalSettings = new HashMap<>();
            weightPersonalSettings.put(OmronConstants.OMRONDevicePersonalSettings.WeightDCIKey,
                    OmronConstants.OMRONDevicePersonalSettings.WeightDCINotAvailable);

            HashMap<String, Object> settings = new HashMap<>();
            settings.put(OmronConstants.OMRONDevicePersonalSettings.WeightKey, weightPersonalSettings);
            if (Integer.parseInt(device.get(OmronConstants.OMRONBLEConfigDevice.Users)) > 1) {
                // BCM configuration

                String gender = weightBundle.getString(Constants.bundleKeys.KEY_BUNDLE_GENDER);
                int genderValue = OmronConstants.OMRONDevicePersonalSettingsUserGenderType.Male;
                if (gender.equals("Female")) {
                    genderValue = OmronConstants.OMRONDevicePersonalSettingsUserGenderType.Female;
                }

                settings.put(OmronConstants.OMRONDevicePersonalSettings.UserHeightKey,
                        weightBundle.getString(Constants.bundleKeys.KEY_BUNDLE_HEIGHT_CM));
                settings.put(OmronConstants.OMRONDevicePersonalSettings.UserGenderKey, genderValue);
                settings.put(OmronConstants.OMRONDevicePersonalSettings.UserDateOfBirthKey,
                        weightBundle.getString(Constants.bundleKeys.KEY_BUNDLE_DOB, "19000101"));
            }

            HashMap<String, HashMap> personalSettings = new HashMap<>();
            personalSettings.put(OmronConstants.OMRONDevicePersonalSettingsKey, settings);

            // Weight Settings
            // Add other weight common settings if any
            String unit = weightBundle.getString(Constants.bundleKeys.KEY_BUNDLE_WEIGHT_UNIT);
            int unitValue;
            if (unit.equals("Kg")) {
                unitValue = OmronConstants.OMRONDeviceWeightUnit.Kg;
            } else if (unit.equals("Lbs")) {
                unitValue = OmronConstants.OMRONDeviceWeightUnit.Lbs;
            } else {
                unitValue = OmronConstants.OMRONDeviceWeightUnit.St;

            }
            HashMap<String, Object> weightCommonSettings = new HashMap<>();
            weightCommonSettings.put(OmronConstants.OMRONDeviceWeightSettings.UnitKey, unitValue);
            HashMap<String, Object> weightSettings = new HashMap<>();
            weightSettings.put(OmronConstants.OMRONDeviceWeightSettingsKey, weightCommonSettings);

            deviceSettings.add(personalSettings);
            deviceSettings.add(weightSettings);
        }

        return deviceSettings;
    }

    private void startScanning() {

        setStateChanges();

        if (isScan) {
               
            // Stop Scanning for Devices using OmronPeripheralManager
            OmronPeripheralManager.sharedManager(mContext)
                    .stopScanPeripherals(new OmronPeripheralManagerStopScanListener() {

                        public void onStopScanCompleted(final OmronErrorInfo resultInfo) {

                            Log.d(TAG, "\u001B[32mStop scan");
                            if (resultInfo.getResultCode() == 0) {
                                if (mPeripheralList.size() > 0) {
                                    System.out.println(
                                            "\u001B[36m device" + mPeripheralList.get(mPeripheralList.size() - 1));

                                }
                                //attachScanEvent.endOfStream(); 
                                // scanHandler = null;

                                // attachScanEvent = null;
                               
                            } else {
                                System.out.println("\u001B[32m" + resultInfo.getResultCode());
                                System.out.println("\u001B[32m" + resultInfo.getDetailInfo());
                            }
                            scanHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (attachScanEvent != null) {
                                        attachScanEvent.success(new ArrayList<>());
                                    }

                                }
                            }, 500);
                            isScan=false;
                            isScanHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (attachIsScanEvent != null) {
                                        attachIsScanEvent.success(isScan);
                                    }

                                }
                            });
                        }
                    });

        } else {
            isScan = true;
            isScanHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (attachIsScanEvent != null) {
                        attachIsScanEvent.success(isScan);
                    }

                }
            });
            Log.d(TAG, "\u001B[32mInizio scan");

            mPeripheralList = new ArrayList<OmronPeripheral>();
            List<HashMap<String, String>> videoList = new ArrayList();
            OmronPeripheralManager.sharedManager(mContext)
                    .startScanPeripherals(new OmronPeripheralManagerScanListener() {

                        public void onScanCompleted(final ArrayList<OmronPeripheral> peripheralList,
                                final OmronErrorInfo resultInfo) {
                                    videoList.clear();
                            if (resultInfo.getResultCode() == 0) {

                                mPeripheralList = peripheralList;

                                if (mScannedDevicesAdapter != null) {
                                    if (peripheralList.size() > 0) {
                                        System.out.println("\u001B[35m device " + mPeripheralList
                                                .get(mPeripheralList.size() - 1).getDeviceInformation());
                                                for (OmronPeripheral omronPeripheral : mPeripheralList) {
                                                    videoList.add(omronPeripheral.getDeviceInformation());
                                                }
                                    }
                                    mScannedDevicesAdapter.setPeripheralList(mPeripheralList);
                                }

                            } else {
                                isScan = false;
                                isScanHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
    
                                        if (attachIsScanEvent != null) {
                                            attachIsScanEvent.success(isScan);
                                        }
    
                                    }
                                });
                                System.out.println(resultInfo.getResultCode() + " / " + resultInfo.getDetailInfo());
                                System.out.println(resultInfo.getMessageInfo());
                            }
                            scanHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (attachScanEvent != null) {
                                        attachScanEvent.success(videoList);
                                    }

                                }
                            }, 500);
                        }
                    });
        }
      //  isScan = !isScan;
        
    }

    private void setStateChanges() {

        // Listen to Device state changes using OmronPeripheralManager
        OmronPeripheralManager.sharedManager(mContext)
                .onConnectStateChange(new OmronPeripheralManagerConnectStateListener() {
                    @Override
                    public void onConnectStateChange(final int state) {

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        int status = 0;

                                        if (state == OmronConstants.OMRONBLEConnectionState.CONNECTING) {
                                            status = 1;
                                        } else if (state == OmronConstants.OMRONBLEConnectionState.CONNECTED) {
                                            status = 2;
                                        } else if (state == OmronConstants.OMRONBLEConnectionState.DISCONNECTING) {
                                            status = 3;
                                        } else if (state == OmronConstants.OMRONBLEConnectionState.DISCONNECTED) {
                                            status = 4;
        
                                        }
                                        System.out.println("\u001B[33m Stato " + status);
                                        try {
                                              if (attachConnessionEvent != null ) {
                                             
                                               attachConnessionEvent.success(status);
                                           }   
                                        } catch (Exception e) {
                                           System.out.println("\u001B[33m Errore Stato " + e);
                                        }
                                    }
                                });

                               

                               


                    }
                });
    }

    public void connectPeripheral(final OmronPeripheral omronPeripheral, Context context) {

        initLists();

        setStateChanges();
        mSelectedPeripheral = omronPeripheral;
        System.out.println("\u001B[32mInizio connessione con " + mSelectedPeripheral.getUuid()
                + mScannedDevicesAdapter.getCount());

        updatePeripheralForSelectedUser();
        // Pair to Device using OmronPeripheralManager
        OmronPeripheralManager.sharedManager(mContext).connectPeripheral(mSelectedPeripheral,
                new OmronPeripheralManagerConnectListener() {

                    public void onConnectCompleted(final OmronPeripheral peripheral, final OmronErrorInfo resultInfo) {
                        System.out.println("\u001B[33mAvvio libreria connessione " + resultInfo.getResultCode()+" "+ resultInfo.getDetailInfo());
                        System.out.println(profileSettings);
                        
                       connectionUpdateWithPeripheral(mSelectedPeripheral, resultInfo, false);
                            
                          
                        
                    }
                });
    }

    private void connectionUpdateWithPeripheral(final OmronPeripheral peripheral, final OmronErrorInfo resultInfo,
            final boolean wait) {

        weightBundle = mIntent.getBundleExtra(Constants.extraKeys.KEY_WEIGHT_SETTINGS);

        Log.d(TAG, "\u001B[32mPeripheral " + peripheral);

        String string = "Bundle{";
        for (String key : mBundle.keySet()) {
            string += " " + key + " => " + mBundle.get(key) + ";";
        }
        string += " }Bundle";
        Log.d(TAG, "\u001B[32mKEY_SELECTED_DEVICE "
                + mIntent.getSerializableExtra(Constants.extraKeys.KEY_SELECTED_DEVICE));
        Log.d(TAG, "\u001B[32mKEY_WEIGHT_SETTINGS " + weightBundle);
        Log.d(TAG, "\u001B[32mKEY_PERSONAL_SETTINGS "
                + mIntent.getSerializableExtra(Constants.extraKeys.KEY_PERSONAL_SETTINGS));
                setStateChanges();
        if (resultInfo.getResultCode() == 0 && peripheral != null) {

            mSelectedPeripheral = peripheral;

            if (peripheral.getLocalName() != null) {

                HashMap<String, String> deviceInformation = peripheral.getDeviceInformation();
                Log.d(TAG, "\u001B[36mDevice Information : " + deviceInformation);

                ArrayList<HashMap> deviceSettings = mSelectedPeripheral.getDeviceSettings();
                if (deviceSettings != null) {
                    Log.d(TAG, "\u001B[36mDevice Settings:" + deviceSettings.toString());
                }

                OmronPeripheralManagerConfig peripheralConfig = OmronPeripheralManager.sharedManager(mContext)
                        .getConfiguration();
                Log.d(TAG, "\u001B[36mDevice Config :  " + peripheralConfig.getDeviceConfigGroupIdAndGroupIncludedId(
                        peripheral.getDeviceGroupIDKey(), peripheral.getDeviceGroupIncludedGroupIDKey()));

                if (wait) {
                    mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resumeConnection(peripheral);
                        }
                    }, 3000);
                } else {
                    System.out.println("\u001B[32mConnesso");
                }
                Log.d(TAG, "\u001B[32mConnesso");
               
            }
        } else {
            System.out.println("\u001B[32mErrore----- " + resultInfo.getDetailInfo() + resultInfo.getMessageInfo());
        }
            
    }

    private void resumeConnection(final OmronPeripheral omronPeripheral) {
        setStateChanges();
        if (selectedUsers.size() > 1) {
            OmronPeripheralManager.sharedManager(mContext).resumeConnectPeripheral(
                    omronPeripheral, selectedUsers, new OmronPeripheralManagerConnectListener() {
                        @Override
                        public void onConnectCompleted(final OmronPeripheral peripheral,
                                final OmronErrorInfo resultInfo) {

                            connectionUpdateWithPeripheral(peripheral, resultInfo, false);
                        }

                    });
        } else {

            OmronPeripheralManager.sharedManager(mContext).resumeConnectPeripheral(
                    omronPeripheral, new ArrayList<>(Arrays.asList(selectedUsers.get(0))),
                    new OmronPeripheralManagerConnectListener() {
                        @Override
                        public void onConnectCompleted(final OmronPeripheral peripheral,
                                final OmronErrorInfo resultInfo) {

                            connectionUpdateWithPeripheral(peripheral, resultInfo, false);
                        }

                    });
        }
    }

    private void disconnectDevice() {

        // Disconnect device using OmronPeripheralManager
        OmronPeripheralManager.sharedManager(mContext).disconnectPeripheral(mSelectedPeripheral,
                new OmronPeripheralManagerDisconnectListener() {
                    @Override
                    public void onDisconnectCompleted(OmronPeripheral peripheral, OmronErrorInfo resultInfo) {
                        System.out.println(
                                "\u001B[32mErrore " + resultInfo.getDetailInfo() + resultInfo.getMessageInfo());
                    }
                });
    }

    public void transferData() {
        if (mSelectedPeripheral == null) {
            System.out.println("\u001B[32mDevice Not Paired");
            return;
        }
        // Disclaimer: Read definition before usage
        if (Integer.parseInt(device
                .get(OmronConstants.OMRONBLEConfigDevice.Category)) == OmronConstants.OMRONBLEDeviceCategory.ACTIVITY) {
            System.out.println("\u001B[32m transfer");
            startOmronPeripheralManager(false);// false per tutte le misurazioni
            performDataTransfer();
        } else {
            System.out.println("\u001B[32m transfer all");
            startOmronPeripheralManager(false);
            performDataTransfer();
        }
    }

    private void performDataTransfer() {
        setStateChanges();
        OmronPeripheral peripheralLocal = new OmronPeripheral(mSelectedPeripheral.getLocalName(),
                mSelectedPeripheral.getUuid());
        transferUserDataWithPeripheral(peripheralLocal);
    }

    // Single User data transfer
    private void transferUserDataWithPeripheral(OmronPeripheral peripheral) {

        // Data Transfer from Device using OmronPeripheralManager
        OmronPeripheralManager.sharedManager(mContext).startDataTransferFromPeripheral(peripheral, selectedUsers.get(0),
                true, OmronConstants.OMRONVitalDataTransferCategory.BloodPressure,
                new OmronPeripheralManagerDataTransferListener() {
                    @Override
                    public void onDataTransferCompleted(OmronPeripheral peripheral, final OmronErrorInfo resultInfo) {

                        if (resultInfo.getResultCode() == 0 && peripheral != null) {

                            HashMap<String, String> deviceInformation = peripheral.getDeviceInformation();
                            Log.d(TAG, "Device Information : " + deviceInformation);

                            ArrayList<HashMap> allSettings = (ArrayList<HashMap>) peripheral.getDeviceSettings();
                            Log.i(TAG, "Device settings : " + allSettings.toString());

                            mSelectedPeripheral = peripheral; // Saving for Transfer Function

                            // Save Device to List
                            // To change based on data available
                            preferencesManager.addDataStoredDeviceList(peripheral.getLocalName(),
                                    Integer.parseInt(device.get(OmronConstants.OMRONBLEConfigDevice.Category)),
                                    peripheral.getModelName());

                            // Get vital data for previously selected user using OmronPeripheral
                            Object output = peripheral.getVitalData();

                            if (output instanceof OmronErrorInfo) {

                                final OmronErrorInfo errorInfo = (OmronErrorInfo) output;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("\u001B[32m" + errorInfo.getResultCode() + " / "
                                                + errorInfo.getDetailInfo());
                                        System.out.println("\u001B[32m" + errorInfo.getMessageInfo());
                                    }
                                });

                                disconnectDevice();

                            } else {

                                HashMap<String, Object> vitalData = (HashMap<String, Object>) output;

                                if (vitalData != null) {
                                    uploadData(vitalData, peripheral, true);
                                }
                            }

                        } else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    System.out.println("\u001B[32m" + resultInfo.getResultCode() + " / "
                                            + resultInfo.getDetailInfo());
                                    System.out.println("\u001B[32m" + resultInfo.getMessageInfo());

                                    if (mHandler != null) {
                                        mHandler.removeCallbacks(mRunnable);
                                    }

                                }
                            });
                        }
                    }
                });
    }

    private void uploadData(HashMap<String, Object> vitalData, OmronPeripheral peripheral, boolean isWait) {

        HashMap<String, String> deviceInfo = peripheral.getDeviceInformation();

        // Weight Data
        ArrayList<HashMap<String, Object>> weightData = (ArrayList<HashMap<String, Object>>) vitalData
                .get(OmronConstants.OMRONVitalDataWeightKey);
        if (weightData != null) {

            for (HashMap<String, Object> weightItem : weightData) {

                Log.d("Weight - ", weightItem.toString());

            }
        }

        if (isWait) {
            mHandler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    continueDataTransfer();
                }
            };

            mHandler.postDelayed(mRunnable, TIME_INTERVAL);

        } else {
            if (mHandler != null)
                mHandler.removeCallbacks(mRunnable);
            continueDataTransfer();
        }
    }

    private void continueDataTransfer() {
        OmronPeripheralManager.sharedManager(mContext)
                .endDataTransferFromPeripheral(new OmronPeripheralManagerDataTransferListener() {
                    @Override
                    public void onDataTransferCompleted(final OmronPeripheral peripheral,
                            final OmronErrorInfo errorInfo) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (errorInfo.getResultCode() == 0 && peripheral != null) {

                                    // Get vital data for previously selected user using OmronPeripheral
                                    Object output = peripheral.getVitalDataWithUser(mSelectedUser);

                                    if (output instanceof OmronErrorInfo) {

                                        final OmronErrorInfo errorInfo = (OmronErrorInfo) output;

                                        System.out.println("\u001B[32m" + errorInfo.getResultCode() + " / "
                                                + errorInfo.getDetailInfo());
                                        System.out.println("\u001B[32m" + errorInfo.getMessageInfo());

                                    } else {

                                        HashMap<String, Object> vitalData = (HashMap<String, Object>) output;

                                        if (vitalData != null) {

                                            // Weightdata
                                            final ArrayList<HashMap<String, Object>> weightItemList = (ArrayList<HashMap<String, Object>>) vitalData
                                                    .get(OmronConstants.OMRONVitalDataWeightKey);
                                            if (weightItemList != null) {

                                                for (HashMap<String, Object> w : weightItemList) {
                                                    System.out.println("\u001B[32m" + w);
  
                                                }
                                                transferHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                    
                                                        if (attachTransferEvent != null) {
                                                            if(weightItemList.size()>0){
                                                                attachTransferEvent.success(weightItemList.get(weightItemList.size()-1));
                                                            }else{
                                                                attachTransferEvent.success(new ArrayList());   
                                                            }
                                                         
                                                        }
                    
                                                    }
                                                    
                                                });
                                             //   attachTransferEvent.endOfStream();
                                            }else{
                                                transferHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                    
                                                        if (attachTransferEvent != null) {
                                                            attachTransferEvent.success("");
                                                        }
                    
                                                    }
                                                    
                                                });
                                            }
                                        }
                                    }

                                } else {

                                    System.out.println("\u001B[32m Error" + errorInfo.getResultCode() + " / "
                                            + errorInfo.getDetailInfo());
                                    System.out.println("\u001B[32m Error" + errorInfo.getMessageInfo());

                                }
                            }
                        });
                    }
                });
    }

    private void updateSettings() {

        if (mSelectedPeripheral == null) {
            System.out.println("\u001B[32m" + "Device Not Paired");
            return;
        }

        System.out.println("\u001B[32m" + "Update...");

        OmronPeripheralManagerConfig peripheralConfig = OmronPeripheralManager.sharedManager(mContext)
                .getConfiguration();

        // Filter device to scan and connect (optional)
        if (device != null && device.get(OmronConstants.OMRONBLEConfigDevice.GroupID) != null
                && device.get(OmronConstants.OMRONBLEConfigDevice.GroupIncludedGroupID) != null) {

            // Add item
            List<HashMap<String, String>> filterDevices = new ArrayList<>();
            filterDevices.add(device);
            peripheralConfig.deviceFilters = filterDevices;
        }

        // Category body composition

        HashMap<String, Object> settingsModel = new HashMap<>();
        HashMap<String, HashMap> userSettings = new HashMap<>();
        HashMap<String, Object> personalWeightSettings = new HashMap<>();

        String gender = weightBundle.getString(Constants.bundleKeys.KEY_BUNDLE_GENDER);
        String unit = weightBundle.getString(Constants.bundleKeys.KEY_BUNDLE_WEIGHT_UNIT);
        int unitValue;
        if (unit.equals("Kg")) {
            unitValue = OmronConstants.OMRONDeviceWeightUnit.Kg;
        } else if (unit.equals("Lbs")) {
            unitValue = OmronConstants.OMRONDeviceWeightUnit.Lbs;
        } else {
            unitValue = OmronConstants.OMRONDeviceWeightUnit.St;

        }
        int genderValue = OmronConstants.OMRONDevicePersonalSettingsUserGenderType.Male;
        if (gender.equals("Female")) {
            genderValue = OmronConstants.OMRONDevicePersonalSettingsUserGenderType.Female;
        }

        settingsModel.put(OmronConstants.OMRONDevicePersonalSettings.UserHeightKey,
                weightBundle.getString(Constants.bundleKeys.KEY_BUNDLE_HEIGHT_CM));
        settingsModel.put(OmronConstants.OMRONDevicePersonalSettings.UserGenderKey, genderValue);
        settingsModel.put(OmronConstants.OMRONDevicePersonalSettings.UserDateOfBirthKey,
                weightBundle.getString(Constants.bundleKeys.KEY_BUNDLE_DOB, "19000101"));

        // Weight settings

        personalWeightSettings.put(OmronConstants.OMRONDevicePersonalSettings.WeightDCIKey,
                OmronConstants.OMRONDevicePersonalSettings.WeightDCINotAvailable);
        settingsModel.put(OmronConstants.OMRONDevicePersonalSettings.WeightKey, personalWeightSettings);
        userSettings.put(OmronConstants.OMRONDevicePersonalSettingsKey, settingsModel);
        // Weight Settings
        // Add other weight common settings if any
        HashMap<String, Object> weightCommonSettings = new HashMap<>();
        weightCommonSettings.put(OmronConstants.OMRONDeviceWeightSettings.UnitKey, unitValue);
        HashMap<String, Object> weightSettings = new HashMap<>();
        weightSettings.put(OmronConstants.OMRONDeviceWeightSettingsKey, weightCommonSettings);
        settingsModel.put(OmronConstants.OMRONDevicePersonalSettings.WeightKey, weightSettings);
        userSettings.put(OmronConstants.OMRONDevicePersonalSettingsKey, settingsModel);

        ArrayList<HashMap> deviceSettings = new ArrayList<>();
        deviceSettings.add(userSettings);

        peripheralConfig.deviceSettings = deviceSettings;

        // Set Scan timeout interval (optional)
        peripheralConfig.timeoutInterval = Constants.CONNECTION_TIMEOUT;

        // Set User Hash Id (mandatory)
        peripheralConfig.userHashId = "<email@gmail.com>"; // Set logged in user email

        // Set configuration for OmronPeripheralManager
        OmronPeripheralManager.sharedManager(mContext).setConfiguration(peripheralConfig);

        // Create peripheral object with localname and UUID
        OmronPeripheral peripheral = new OmronPeripheral(mSelectedPeripheral.getLocalName(),
                mSelectedPeripheral.getUuid());

        // Call to update the settings
        OmronPeripheralManager.sharedManager(mContext).updatePeripheral(peripheral,
                new OmronPeripheralManagerUpdateListener() {
                    @Override
                    public void onUpdateCompleted(final OmronPeripheral peripheral, final OmronErrorInfo resultInfo) {

                       

                                if (resultInfo.getResultCode() == 0 && peripheral != null) {

                                    mSelectedPeripheral = peripheral;

                                    if (null != peripheral.getLocalName()) {

                                        System.out.println("\u001B[32m" + peripheral.getLocalName());
                                        System.out.println("\u001B[32m" + peripheral.getUuid());
                                        // showMessage(getString(R.string.device_connected),
                                        // getString(R.string.update_success));

                                        HashMap<String, String> deviceInformation = peripheral.getDeviceInformation();
                                        Log.d(TAG, "Device Information : " + deviceInformation);

                                        ArrayList<HashMap> deviceSettings = mSelectedPeripheral.getDeviceSettings();
                                        if (deviceSettings != null) {
                                            Log.d(TAG, "Device Settings:" + deviceSettings.toString());
                                        }
                                        Object personalSettingsForUser1 = mSelectedPeripheral
                                                .getDeviceSettingsWithUser(1);
                                        if (personalSettingsForUser1 != null) {
                                            Log.d(TAG, "Personal Settings for User 1:"
                                                    + personalSettingsForUser1.toString());
                                        }
                                        Object personalSettingsForUser2 = mSelectedPeripheral
                                                .getDeviceSettingsWithUser(2);
                                        if (personalSettingsForUser2 != null) {
                                            Log.d(TAG, "Personal Settings for User 2:"
                                                    + personalSettingsForUser2.toString());
                                        }

                                        try {
                                            Object personalSettingsForSelectedUser = mSelectedPeripheral
                                                    .getDeviceSettingsWithUser(mSelectedUser);
                                            if (personalSettingsForSelectedUser != null) {
                                                HashMap<String, Object> settings = (HashMap<String, Object>) personalSettingsForSelectedUser;
                                                if (settings.containsKey(
                                                        OmronConstants.OMRONDevicePersonalSettings.WeightKey)) {
                                                    HashMap<String, Object> weightSettings = (HashMap<String, Object>) settings
                                                            .get(OmronConstants.OMRONDevicePersonalSettings.WeightKey);
                                                    if (weightSettings != null) {
                                                        if (weightSettings.containsKey(
                                                                OmronConstants.OMRONDevicePersonalSettings.WeightDCIKey)) {
                                                            long dciValue = (long) weightSettings.get(
                                                                    OmronConstants.OMRONDevicePersonalSettings.WeightDCIKey);
                                                            preferencesManager.saveDCIValue(dciValue);
                                                            System.out.println("\u001B[32m" + String.valueOf(dciValue));
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            System.out.println("\u001B[32m" + "Eccezzione" + e);
                                        }

                                        OmronPeripheralManagerConfig peripheralConfig = OmronPeripheralManager
                                                .sharedManager(mContext).getConfiguration();
                                        Log.d(TAG,
                                                "Device Config :  "
                                                        + peripheralConfig.getDeviceConfigGroupIdAndGroupIncludedId(
                                                                peripheral.getDeviceGroupIDKey(),
                                                                peripheral.getDeviceGroupIncludedGroupIDKey()));
                                    } else {

                                        System.out.println("\u001B[32m" + resultInfo.getDetailInfo());
                                        System.out.println("\u001B[32m" + resultInfo.getMessageInfo());
                                    }
                                }

                            
                       

                    }
                });
    }

    private void updatePeripheralForSelectedUser() {

        if (mSelectedPeripheral == null) {
            System.out.println("\u001B[32mDevice Not Paired");
            return;
        }

        // Set State Change Listener
        setStateChanges();

        System.out.println("\u001B[32mConnecting... ");

        OmronPeripheralManagerConfig peripheralConfig = OmronPeripheralManager.sharedManager(mContext)
                .getConfiguration();

        // Filter device to scan and connect (optional)
        if (device != null && device.get(OmronConstants.OMRONBLEConfigDevice.GroupID) != null
                && device.get(OmronConstants.OMRONBLEConfigDevice.GroupIncludedGroupID) != null) {

            // Add item
            List<HashMap<String, String>> filterDevices = new ArrayList<>();
            filterDevices.add(device);
            peripheralConfig.deviceFilters = filterDevices;
        }

        // Category body composition

        HashMap<String, Object> settingsModel = new HashMap<>();

        String gender = weightBundle.getString(Constants.bundleKeys.KEY_BUNDLE_GENDER);
        String unit = weightBundle.getString(Constants.bundleKeys.KEY_BUNDLE_WEIGHT_UNIT);
        int unitValue;
        if (unit.equals("Kg")) {
            unitValue = OmronConstants.OMRONDeviceWeightUnit.Kg;
        } else if (unit.equals("Lbs")) {
            unitValue = OmronConstants.OMRONDeviceWeightUnit.Lbs;
        } else {
            unitValue = OmronConstants.OMRONDeviceWeightUnit.St;

        }
        int genderValue = OmronConstants.OMRONDevicePersonalSettingsUserGenderType.Male;
        if (gender.equals("Female")) {
            genderValue = OmronConstants.OMRONDevicePersonalSettingsUserGenderType.Female;
        }

        double height = Double.parseDouble(weightBundle.getString(Constants.bundleKeys.KEY_BUNDLE_HEIGHT_CM));
        settingsModel.put(OmronConstants.OMRONDevicePersonalSettings.UserHeightKey,
                weightBundle.getString(Constants.bundleKeys.KEY_BUNDLE_HEIGHT_CM));
        settingsModel.put(OmronConstants.OMRONDevicePersonalSettings.UserGenderKey, genderValue);
        settingsModel.put(OmronConstants.OMRONDevicePersonalSettings.UserDateOfBirthKey,
                weightBundle.getString(Constants.bundleKeys.KEY_BUNDLE_DOB, "19000101"));

        // Weight settings
        HashMap<String, Object> personalWeightSettings = new HashMap<>();
        personalWeightSettings.put(OmronConstants.OMRONDevicePersonalSettings.WeightDCIKey,
                OmronConstants.OMRONDevicePersonalSettings.WeightDCINotAvailable);
        settingsModel.put(OmronConstants.OMRONDevicePersonalSettings.WeightKey, personalWeightSettings);

        // Weight Settings
        // Add other weight common settings if any
        HashMap<String, Object> weightCommonSettings = new HashMap<>();
        weightCommonSettings.put(OmronConstants.OMRONDeviceWeightSettings.UnitKey, unitValue);
        HashMap<String, Object> weightSettings = new HashMap<>();
        weightSettings.put(OmronConstants.OMRONDeviceWeightSettingsKey, weightCommonSettings);

        HashMap<String, HashMap> userSettings = new HashMap<>();
        userSettings.put(OmronConstants.OMRONDevicePersonalSettingsKey, settingsModel);

        ArrayList<HashMap> deviceSettings = new ArrayList<>();
        deviceSettings.add(userSettings);
        deviceSettings.add(weightSettings);

        peripheralConfig.deviceSettings = deviceSettings;

        // Set Scan timeout interval (optional)
        peripheralConfig.timeoutInterval = Constants.CONNECTION_TIMEOUT;

        // Set User Hash Id (mandatory)
        peripheralConfig.userHashId = "<email@gmail.com>"; // Set logged in user email

        // Set configuration for OmronPeripheralManager
        OmronPeripheralManager.sharedManager(mContext).setConfiguration(peripheralConfig);

        // Initialize the connection process.
        // OmronPeripheralManager.sharedManager(mContext).startManager();

        // Create peripheral object with localname and UUID
        OmronPeripheral peripheral = new OmronPeripheral(mSelectedPeripheral.getLocalName(),
                mSelectedPeripheral.getUuid());

        // Call to update the settings
        OmronPeripheralManager.sharedManager(mContext).updatePeripheral(peripheral, mSelectedUser,
                new OmronPeripheralManagerUpdateListener() {
                    @Override
                    public void onUpdateCompleted(final OmronPeripheral peripheral, final OmronErrorInfo resultInfo) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (resultInfo.getResultCode() == 0 && peripheral != null) {

                                    mSelectedPeripheral = peripheral;

                                    if (null != peripheral.getLocalName()) {

                                        System.out.println("\u001B[32m" + peripheral.getLocalName());
                                        System.out.println("\u001B[32m" + peripheral.getUuid());

                                        HashMap<String, String> deviceInformation = peripheral.getDeviceInformation();
                                        System.out.println("\u001BDevice Information : " + deviceInformation);

                                        ArrayList<HashMap> deviceSettings = mSelectedPeripheral.getDeviceSettings();
                                        if (deviceSettings != null) {
                                            System.out.println("\u001BDevice Settings:" + deviceSettings.toString());
                                        }
                                        Object personalSettingsForUser1 = mSelectedPeripheral
                                                .getDeviceSettingsWithUser(1);
                                        if (personalSettingsForUser1 != null) {
                                            System.out.println("\u001BPersonal Settings for User 1:"
                                                    + personalSettingsForUser1.toString());
                                        }
                                        Object personalSettingsForUser2 = mSelectedPeripheral
                                                .getDeviceSettingsWithUser(2);
                                        if (personalSettingsForUser2 != null) {
                                            System.out.println("\u001BPersonal Settings for User 2:"
                                                    + personalSettingsForUser2.toString());
                                        }

                                        try {
                                            Object personalSettingsForSelectedUser = mSelectedPeripheral
                                                    .getDeviceSettingsWithUser(mSelectedUser);
                                            if (personalSettingsForSelectedUser != null) {
                                                HashMap<String, Object> settings = (HashMap<String, Object>) personalSettingsForSelectedUser;
                                                if (settings.containsKey(
                                                        OmronConstants.OMRONDevicePersonalSettings.WeightKey)) {
                                                    HashMap<String, Object> weightSettings = (HashMap<String, Object>) settings
                                                            .get(OmronConstants.OMRONDevicePersonalSettings.WeightKey);
                                                    if (weightSettings != null) {
                                                        if (weightSettings.containsKey(
                                                                OmronConstants.OMRONDevicePersonalSettings.WeightDCIKey)) {
                                                            long dciValue = (long) weightSettings.get(
                                                                    OmronConstants.OMRONDevicePersonalSettings.WeightDCIKey);
                                                            preferencesManager.saveDCIValue(dciValue);
                                                            System.out.println("\u001B[32m" + String.valueOf(dciValue));
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            System.out.println("\u001B[32mEccezzione " + e);
                                        }
                                        OmronPeripheralManagerConfig peripheralConfig = OmronPeripheralManager
                                                .sharedManager(mContext).getConfiguration();
                                        Log.d(TAG,
                                                "Device Config :  "
                                                        + peripheralConfig.getDeviceConfigGroupIdAndGroupIncludedId(
                                                                peripheral.getDeviceGroupIDKey(),
                                                                peripheral.getDeviceGroupIncludedGroupIDKey()));
                                    }
                                } else {
                                    System.out.println("\u001B[32mError " + resultInfo.getDetailInfo());
                                    System.out.println("\u001B[32mError... " + resultInfo.getMessageInfo());
                                }

                            }
                        });

                    }
                });
    }
}
