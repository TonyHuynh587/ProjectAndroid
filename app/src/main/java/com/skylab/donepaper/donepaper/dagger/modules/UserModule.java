package com.skylab.donepaper.donepaper.dagger.modules;

import com.skylab.donepaper.donepaper.data.UserManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UserModule {

    @Provides
    @Singleton
    protected UserManager provideUserManager(){
        return new UserManager();
    }

}
