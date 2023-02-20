import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'viva_method_channel.dart';

abstract class VivaPlatform extends PlatformInterface {
  /// Constructs a VivaPlatform.
  VivaPlatform() : super(token: _token);

  static final Object _token = Object();

  static VivaPlatform _instance = MethodChannelViva();

  /// The default instance of [VivaPlatform] to use.
  ///
  /// Defaults to [MethodChannelViva].
  static VivaPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [VivaPlatform] when
  /// they register themselves.
  static set instance(VivaPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool?> isScanning() {
    throw UnimplementedError('isScanning() has not been implemented.');
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> setUserData(int height, int gender, String bDay, int user) {
    throw UnimplementedError('init has not been implemented.');
  }

  Future<String?> init() {
    throw UnimplementedError('init has not been implemented.');
  }

  Future<String?> setAPIKey() {
    throw UnimplementedError('setAPIKey has not been implemented.');
  }

  Future<String?> scan() {
    throw UnimplementedError('scan has not been implemented.');
  }

  Future<String?> settings() {
    throw UnimplementedError('scan has not been implemented.');
  }

  Future<String?> connect() {
    throw UnimplementedError('scan has not been implemented.');
  }

  Future<String?> transfer() {
    throw UnimplementedError('scan has not been implemented.');
  }

  Future<String?> setDevicePosition(int position) {
    throw UnimplementedError('scan has not been implemented.');
  }

  Future<int> errorCode() {
    throw UnimplementedError('scan has not been implemented.');
  }
}
