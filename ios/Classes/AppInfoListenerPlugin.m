#import "AppInfoListenerPlugin.h"
#if __has_include(<app_info_listener/app_info_listener-Swift.h>)
#import <app_info_listener/app_info_listener-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "app_info_listener-Swift.h"
#endif

@implementation AppInfoListenerPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAppInfoListenerPlugin registerWithRegistrar:registrar];
}
@end
