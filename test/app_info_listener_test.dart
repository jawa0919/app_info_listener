import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:app_info_listener/app_info_listener.dart';

void main() {
  const MethodChannel channel = MethodChannel('app_info_listener');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await AppInfoListener().platformVersion, '42');
  });
}
