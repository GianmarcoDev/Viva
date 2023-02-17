package com.viva.viva;
import com.viva.viva.MainActivity;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import android.content.Context;
/** VivaPlugin */
public class VivaPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context mContext;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "viva");
    channel.setMethodCallHandler(this);
    mContext = flutterPluginBinding.getApplicationContext();
  }

  private int height=0;
  private int gender=0;
  private int user=1;
  private  String bday="";

    void setData(int userGender,int userHeight,int userNumber,String userbDay){
height=userHeight;
gender=userGender;
user=userNumber;
bday=userbDay;
    }

 private final MainActivity vivaM = new MainActivity();
 



  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    System.out.println( "Chiamata : "+call.method);
  
   // mContext = flutterPluginBinding.getApplicationContext();
    switch(call.method){
      case "setUserData":   
     
      try{       
        System.out.println("Dati"+ call.argument("height") +" "+ call.argument("gender") +" "+ call.argument("user")+" "+ call.argument("bDay"));
      setData(call.argument("height"), call.argument("gender"), call.argument("user"), call.argument("bDay"));
      }catch(Exception e){
        e.printStackTrace();    
      }
      result.success("done");
      break;
      case "getPlatformVersion":
      result.success("Android " + android.os.Build.VERSION.RELEASE);
      break;
      case "isScan":   
      Boolean b =null;    
      try{       
      b=  vivaM.isScan;
      }catch(Exception e){
        e.printStackTrace();    
      }
      result.success(b);
      break;
      case "init":   
      String s ="init";    
      try{       
        vivaM.loadConfiguration(mContext);
      }catch(Exception e){
        e.printStackTrace();
        s="ERROR";
      }
      result.success(s);
      break;
        case "scan":   
       s ="scan"; 
      try{       
        vivaM.scan(mContext);
      }catch(Exception e){
        e.printStackTrace();
        s="ERROR";
      }
      result.success(s);
        break;
        case "settings":   
        s ="settings"; 
       try{       
         vivaM.init(mContext, height, bday, gender, user);
       }catch(Exception e){
         e.printStackTrace();
         s="ERROR";
       }
       result.success(s);
         break;
         case "connect":   
         s ="connect"; 
        try{       
          vivaM.connectPeripheral(vivaM.mPeripheralList.get(vivaM.devicePosition),mContext);
        }catch(Exception e){
          e.printStackTrace();
          s="ERROR";
        }
        result.success(s);
          break;
          case "transfer":   
          s ="transfer"; 
         try{       
           vivaM.transferData();
         }catch(Exception e){
           e.printStackTrace();
           s="ERROR";
         }
         result.success(s);
           break;
           case "position":   
           s ="ok"; 
          try{       
            System.out.println("Dati"+ call.argument("position"));
            int p =call.argument("position");
            vivaM.devicePosition=p;
            s=s+" "+p;
          }catch(Exception e){
            e.printStackTrace();
            s="ERROR";
          }
          result.success(s);
            break;
      default:
       result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
