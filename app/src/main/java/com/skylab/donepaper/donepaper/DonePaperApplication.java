package com.skylab.donepaper.donepaper;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.skylab.donepaper.donepaper.dagger.DaggerMainComponent;
import com.skylab.donepaper.donepaper.dagger.MainComponent;
import com.skylab.donepaper.donepaper.dagger.modules.AppModule;
import com.skylab.donepaper.donepaper.dagger.modules.NavigationModule;
import com.skylab.donepaper.donepaper.dagger.modules.OrderModule;
import com.skylab.donepaper.donepaper.dagger.modules.UserModule;
import com.skylab.donepaper.donepaper.utils.FontsOverride;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

public class DonePaperApplication extends Application {

    private MainComponent mMainComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        mMainComponent = DaggerMainComponent.builder()
                .appModule(new AppModule(this))
                .navigationModule(new NavigationModule())
                .userModule(new UserModule())
                .orderModule(new OrderModule())
                .build();

        FontsOverride.setDefaultFont(this, "DEFAULT", "Avenir-Book.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Avenir-Roman.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "Avenir-Medium.ttf");
    }

    public MainComponent getMainComponent() {
        return mMainComponent;
    }
}
