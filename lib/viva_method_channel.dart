import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'viva_platform_interface.dart';

/// An implementation of [VivaPlatform] that uses method channels.
class MethodChannelViva extends VivaPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('viva');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> init() async {
    log('\x1B[36m init \x1B[0m');
    final version = await methodChannel.invokeMethod<String>('init');
    return version;
  }

  @override
  Future<String?> setUserData(
      int height, int gender, String bDay, int user) async {
    log('\x1B[36m setuserdata \x1B[0m');
    final version = await methodChannel
        .invokeMethod<String>('setUserData', <String, dynamic>{
      'height': height,
      'gender': gender,
      'bDay': bDay,
      'user': user,
    });
    return version;
  }

  @override
  Future<String?> setDevicePosition(int position) async {
    log('\x1B[36m setDevicePosition \x1B[0m');
    final version =
        await methodChannel.invokeMethod<String>('position', <String, dynamic>{
      'position': position,
    });
    return version;
  }

  @override
  Future<bool?> isScanning() async {
    final version = await methodChannel.invokeMethod<bool>('isScan');
    return version;
  }

  @override
  Future<String?> setAPIKey() async {
    final version = await methodChannel.invokeMethod<String>('setApiKey');
    return version;
  }

  @override
  Future<String?> scan() async {
    final version = await methodChannel.invokeMethod<String>('scan');
    return version;
  }

  @override
  Future<String?> settings() async {
    final version = await methodChannel.invokeMethod<String>('settings');
    return version;
  }

  @override
  Future<String?> connect() async {
    final version = await methodChannel.invokeMethod<String>('connect');
    return version;
  }

  @override
  Future<String?> transfer() async {
    final version = await methodChannel.invokeMethod<String>('transfer');
    return version;
  }

  @override
  Future<int> errorCode() async {
    String? code = await methodChannel.invokeMethod<String>('code');
    return int.tryParse(code ?? '-1') ?? -1;
  }
}
