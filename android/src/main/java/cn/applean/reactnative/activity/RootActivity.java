package cn.applean.reactnative.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.common.logging.FLog;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.LifecycleState;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.LifecycleEventListener;

import java.util.List;

import javax.annotation.Nullable;

public class RootActivity extends Activity implements DefaultHardwareBackBtnHandler {

  private static final String REDBOX_PERMISSION_MESSAGE =
      "Overlay permissions needs to be granted in order for react native apps to run in dev mode";

  private @Nullable ReactInstanceManager mReactInstanceManager;
  private String mMainComponentName;

  private LifecycleState mLifecycleState = LifecycleState.BEFORE_RESUME;
  private boolean mDoRefresh = false;

  private LifecycleEventListener mLifecycleEventListener;

  private Intent resultIntent;
  private boolean isFinishByReload = false;

  /**
   * Returns the launchOptions which will be passed to the {@link ReactInstanceManager}
   * when the application is started. By default, this will return null and an empty
   * object will be passed to your top level component as its initial props.
   * If your React Native application requires props set outside of JS, override
   * this method to return the Android.os.Bundle of your desired initial props.
   */
  protected @Nullable Bundle getLaunchOptions() {
    return null;
  }

  /**
   * Returns the name of the main component registered from JavaScript.
   * This is used to schedule rendering of the component.
   * e.g. "MoviesApp"
   */
  protected String getMainComponentName() {
    return mMainComponentName;
  }

  /**
   * Returns whether dev mode should be enabled. This enables e.g. the dev menu.
   */
  protected boolean getUseDeveloperSupport() {
    return BuildConfig.DEBUG;
  }

  /**
   * A subclass may override this method if it needs to use a custom instance.
   */
  protected ReactInstanceManager createReactInstanceManager() {
    return mReactInstanceManager;
  }

  /**
   * A subclass may override this method if it needs to use a custom {@link ReactRootView}.
   */
  protected ReactRootView createRootView() {
    return new ReactRootView(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    final int requestId = intent.getIntExtra(ActivityModule.INTENT_REQUEST_ID_KEY, -1);
    mMainComponentName = intent.getStringExtra(ActivityModule.INTENT_COMPONENT_NAME_KEY);
    mReactInstanceManager = ActivityModule.getReactInstanceManager();

    if (getUseDeveloperSupport() && Build.VERSION.SDK_INT >= 23) {
      // Get permission to show redbox in dev builds.
      if (!Settings.canDrawOverlays(this)) {
        Intent serviceIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        startActivity(serviceIntent);
        FLog.w(ReactConstants.TAG, REDBOX_PERMISSION_MESSAGE);
        Toast.makeText(this, REDBOX_PERMISSION_MESSAGE, Toast.LENGTH_LONG).show();
      }
    }

    mReactInstanceManager = createReactInstanceManager();
    final ReactRootView reactRootView = createRootView();
    reactRootView.startReactApplication(mReactInstanceManager, getMainComponentName(), getLaunchOptions());
    setContentView(reactRootView);

    resultIntent = new Intent();
    resultIntent.putExtra(ActivityModule.INTENT_REQUEST_ID_KEY, requestId);
    setResult(RESULT_OK, resultIntent);

    final RootActivity activity = this;

    final ReactContext reactContext = mReactInstanceManager.getCurrentReactContext();
    mLifecycleEventListener = new LifecycleEventListener() {
      public void onHostPause() {
        if (mLifecycleState != LifecycleState.RESUMED) {
          // Ignore non-reload operation
          return;
        }
        mReactInstanceManager.detachRootView(reactRootView);
        reactContext.removeLifecycleEventListener(mLifecycleEventListener);
        activity.mLifecycleEventListener = null;

        activity.isFinishByReload = true;
        activity.finish();
      }
      public void onHostResume() {}
      public void onHostDestroy() {}
    };
    
    reactContext.addLifecycleEventListener(mLifecycleEventListener);
  }

  @Override
  protected void onPause() {
    super.onPause();

    mLifecycleState = LifecycleState.BEFORE_RESUME;

    if (mReactInstanceManager != null) {
      mReactInstanceManager.onHostPause();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    mLifecycleState = LifecycleState.RESUMED;

    if (mReactInstanceManager != null) {
      mReactInstanceManager.onHostResume(this, this);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (mLifecycleState != null) {
      mReactInstanceManager.getCurrentReactContext().removeLifecycleEventListener(mLifecycleEventListener);
      mLifecycleEventListener = null;
    }

    // if (mReactInstanceManager != null) {
    //   mReactInstanceManager.destroy();
    // }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (mReactInstanceManager != null) {
      mReactInstanceManager.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (mReactInstanceManager != null &&
        mReactInstanceManager.getDevSupportManager().getDevSupportEnabled()) {
      if (keyCode == KeyEvent.KEYCODE_MENU) {
        mReactInstanceManager.showDevOptionsDialog();
        return true;
      }
      if (keyCode == KeyEvent.KEYCODE_R && !(getCurrentFocus() instanceof EditText)) {
        // Enable double-tap-R-to-reload
        if (mDoRefresh) {
          mReactInstanceManager.getDevSupportManager().handleReloadJS();
          mDoRefresh = false;
        } else {
          mDoRefresh = true;
          new Handler().postDelayed(
              new Runnable() {
                @Override
                public void run() {
                  mDoRefresh = false;
                }
              },
              200);
        }
      }
    }
    return super.onKeyUp(keyCode, event);
  }

  @Override
  public void onBackPressed() {
    if (mReactInstanceManager != null) {
      mReactInstanceManager.onBackPressed();
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public void invokeDefaultOnBackPressed() {
    super.onBackPressed();
  }
}
