import 'package:flutter/services.dart';

import 'viva_platform_interface.dart';

class Viva {
  Future<String?> getPlatformVersion() {
    return VivaPlatform.instance.getPlatformVersion();
  }

  Future<bool?> iScanning() {
    return VivaPlatform.instance.isScanning();
  }

  Future<String?> init() {
    return VivaPlatform.instance.init();
  }

  Future<String?> setUserdata(int height, int gender, String bDay, int user) {
    return VivaPlatform.instance.setUserData(height, gender, bDay, user);
  }

  Future<String?> setAPIKey() {
    return VivaPlatform.instance.setAPIKey();
  }

  Future<String?> scan() {
    return VivaPlatform.instance.scan();
  }

  Future<String?> settings() {
    return VivaPlatform.instance.settings();
  }

  Future<String?> connect() {
    return VivaPlatform.instance.connect();
  }

  Future<String?> transfer() {
    return VivaPlatform.instance.transfer();
  }

  Future<String?> setDevicePosition(int devicePosition) {
    return VivaPlatform.instance.setDevicePosition(devicePosition);
  }

  EventChannel streamIsScan = const EventChannel('isscan');
  EventChannel streamScan = const EventChannel('scan');

  EventChannel streamConn = const EventChannel('conn');
  EventChannel streamTransfer = const EventChannel('transfer');
}
