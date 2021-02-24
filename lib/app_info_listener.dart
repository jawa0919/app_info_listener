import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

class AppInfoListener {
  static const MethodChannel _channel =
      MethodChannel('top.jawa0919.app_info_listener/method');
  static const EventChannel _event =
      EventChannel("top.jawa0919.app_info_listener/event");

  Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// 搜索app包信息 返回值参考
  /// ```
  /// import 'package:package_info/package_info.dart';
  /// PackageInfo({
  ///   this.appName,
  ///   this.packageName,
  ///   this.version,
  ///   this.buildNumber,
  ///  })
  /// ```
  Future<Map<String, dynamic>> searchPackageInfo(String packageName) async {
    if (Platform.isAndroid) {
      Map<String, dynamic> params = {"packageName": packageName};
      final res = await _channel.invokeMethod('searchPackageInfo', params);
      return Map<String, dynamic>.from(res);
    } else {
      throw UnsupportedError("only use android");
    }
  }

  /// 启动apk
  Future<void> launchPackageInfo(String packageName) async {
    if (Platform.isAndroid) {
      Map<String, dynamic> params = {"packageName": packageName};
      await _channel.invokeMethod<String>('launchPackageInfo', params);
    } else {
      throw UnsupportedError("only use android");
    }
  }

  /// 监听流
  static Stream<Map<String, dynamic>> _onChanged;

  Stream get onChanged {
    if (_onChanged == null)
      _onChanged = _event
          .receiveBroadcastStream()
          .map((res) => Map<String, dynamic>.from(res));
    return _onChanged;
  }

  Stream<String> get onApkAdded {
    if (_onChanged == null)
      _onChanged = _event
          .receiveBroadcastStream()
          .map((res) => Map<String, dynamic>.from(res));
    return _onChanged
        .where((event) => event["action"] == ChangedAction.ADDED)
        .map<String>((event) => event["packageName"]);
  }

  Stream<String> get onApkRemoved {
    if (_onChanged == null)
      _onChanged = _event
          .receiveBroadcastStream()
          .map((res) => Map<String, dynamic>.from(res));
    return _onChanged
        .where((event) => event["action"] == ChangedAction.REMOVED)
        .map<String>((event) => event["packageName"]);
  }

  Stream<String> get onApkReplaced {
    if (_onChanged == null)
      _onChanged = _event
          .receiveBroadcastStream()
          .map((res) => Map<String, dynamic>.from(res));
    return _onChanged
        .where((event) => event["action"] == ChangedAction.REPLACED)
        .map<String>((event) => event["packageName"]);
  }
}

class ChangedAction {
  static const ADDED = "ADDED";
  static const REMOVED = "REMOVED";
  static const REPLACED = "REPLACED";
}
