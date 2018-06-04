package com.skylab.donepaper.donepaper.dagger.modules;

import com.skylab.donepaper.donepaper.data.OrderManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class OrderModule {

    @Provides
    @Singleton
    protected OrderManager provideOrderManager(){
        return new OrderManager();
    }
}
