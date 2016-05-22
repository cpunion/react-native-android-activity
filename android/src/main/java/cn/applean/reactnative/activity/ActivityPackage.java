package cn.applean.reactnative.activity;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.ReactInstanceManager;

import java.util.ArrayList;
import java.util.List;

public class ActivityPackage implements ReactPackage {
  private ReactInstanceManager mReactInstanceManager;

  public void setReactInstanceManager(ReactInstanceManager reactInstanceManager) {
    mReactInstanceManager = reactInstanceManager;
  }

  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactApplicationContext) {
    assert(mReactInstanceManager != null);

    List<NativeModule> modules = new ArrayList<>();
    modules.add(new ActivityModule(reactApplicationContext, mReactInstanceManager));
    return modules;
  }

  @Override
  public List<Class<? extends JavaScriptModule>> createJSModules() {
    return new ArrayList<>();
  }

  @Override
  public List<ViewManager> createViewManagers(ReactApplicationContext reactApplicationContext) {
    return new ArrayList<>();
  }
}
