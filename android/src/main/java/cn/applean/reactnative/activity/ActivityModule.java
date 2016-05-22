package cn.applean.reactnative.activity;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.os.Bundle;
import android.content.Intent;
import java.util.Map;
import java.util.HashMap;

public class ActivityModule extends ReactContextBaseJavaModule implements ActivityEventListener {
  public static final String ACTIVITY_RESULT_EVENT_NAME = "ACTIVITY_RESULT";
  public static final String INTENT_COMPONENT_NAME_KEY = "componentName";
  public static final String INTENT_REQUEST_ID_KEY = "requestId";
  public static final String INTENT_FINISH_REASON_KEY = "reason";
  public static final int ACTIVITY_REQUEST = 9799317; // Just a number

  private static ReactInstanceManager sReactInstanceManager;

  public static ReactInstanceManager getReactInstanceManager() {
    return sReactInstanceManager;
  }

  public ActivityModule(ReactApplicationContext reactContext, ReactInstanceManager reactInstanceManager) {
    super(reactContext);

    assert(sReactInstanceManager == null);
    assert(reactInstanceManager != null);
    sReactInstanceManager = reactInstanceManager;

    reactContext.addActivityEventListener(this);
  }

  @Override
  public String getName() {
    return "Activity";
  }

  @Override
  public Map<String, Object> getConstants() {
    Map<String, Object> constants = new HashMap<String, Object>();
    return constants;
  }

  @ReactMethod
  public void startActivity(String componentName, int requestId) {
    Intent intent = new Intent(getReactApplicationContext(), RootActivity.class);
    intent.putExtra(INTENT_COMPONENT_NAME_KEY, componentName);
    intent.putExtra(INTENT_REQUEST_ID_KEY, requestId);

    getReactApplicationContext().startActivityForResult(intent, ACTIVITY_REQUEST, (Bundle)null);
  }

  @Override
  public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
    if (requestCode != ACTIVITY_REQUEST) {
      return;
    }

    WritableMap result = Arguments.createMap();
    result.putInt("result", resultCode);
    if (intent != null) {
      int requestId = intent.getIntExtra(INTENT_REQUEST_ID_KEY, -1);
      String reason = intent.getStringExtra(INTENT_FINISH_REASON_KEY);
      result.putInt(INTENT_REQUEST_ID_KEY, requestId);
      result.putString(INTENT_FINISH_REASON_KEY, reason);
    } else {
      result.putNull(INTENT_REQUEST_ID_KEY);
    }

    getReactApplicationContext()
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(ACTIVITY_RESULT_EVENT_NAME, result);
  }
}
