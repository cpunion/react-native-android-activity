package cn.applean.reactnative.activity;

import com.facebook.react.ReactPackage;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactActivity;

import android.os.Bundle;
import android.content.Intent;
import android.app.Activity;

import java.util.List;

/**
 * This is the simplest implementation extends from ReactActivity, but in
 * ReactActivity.onDestroy, it calls ReactInstanceManager.onHostDestroy,
 * so we must copy ReactActivity's code and modify.
 */
public class RootActivity1 extends ReactActivity {
    private ReactInstanceManager mReactInstanceManager;
    private String mMainComponentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        mMainComponentName = intent.getStringExtra(ActivityModule.INTENT_COMPONENT_NAME_KEY);

        mReactInstanceManager = ActivityModule.getReactInstanceManager();

        super.onCreate(savedInstanceState);
    }

    @Override
    protected ReactInstanceManager createReactInstanceManager() {
        return mReactInstanceManager;
    }

    @Override
    protected String getMainComponentName() {
        return mMainComponentName;
    }

    @Override
    protected boolean getUseDeveloperSupport() {
        return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
        // Not used
        return null;
    }
}
