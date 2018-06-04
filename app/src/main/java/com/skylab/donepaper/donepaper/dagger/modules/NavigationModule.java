package com.skylab.donepaper.donepaper.dagger.modules;

import com.skylab.donepaper.donepaper.fragment.navigation.NavigationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NavigationModule {

    @Provides
    @Singleton
    protected NavigationManager provideNavigationManager() {
        return new NavigationManager();
    }

}
