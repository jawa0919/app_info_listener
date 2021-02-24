import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:app_info_listener/app_info_listener.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
    AppInfoListener()
        .searchPackageInfo("top.jawa0919.app_info_listener_example")
        .then((value) => debugPrint("searchPackageInfo $value"));
    initListener();
  }

  initListener() {
    AppInfoListener().onChanged.listen((event) {
      debugPrint("Flutter Listener onChanged $event");
    });
    AppInfoListener().onApkAdded.listen((event) {
      debugPrint("Flutter Listener onApkAdded $event");
    });
    AppInfoListener().onApkRemoved.listen((event) {
      debugPrint("Flutter Listener onApkRemoved $event");
    });
    AppInfoListener().onApkReplaced.listen((event) {
      debugPrint("Flutter Listener onApkReplaced $event");
    });
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion = await AppInfoListener().platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }
    if (!mounted) return;
    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: $_platformVersion\n'),
        ),
      ),
    );
  }
}
