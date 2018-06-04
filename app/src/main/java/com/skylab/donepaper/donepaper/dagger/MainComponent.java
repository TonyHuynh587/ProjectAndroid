package com.skylab.donepaper.donepaper.dagger;

import android.content.SharedPreferences;

import com.skylab.donepaper.donepaper.DonePaperApplication;
import com.skylab.donepaper.donepaper.activities.ChatActivity;
import com.skylab.donepaper.donepaper.activities.LoginActivity;
import com.skylab.donepaper.donepaper.activities.MainActivity;
import com.skylab.donepaper.donepaper.activities.OrderDetailActivity;
import com.skylab.donepaper.donepaper.activities.OrderProcessActivity;
import com.skylab.donepaper.donepaper.activities.ProfileActivity;
import com.skylab.donepaper.donepaper.activities.SignUpActivity;
import com.skylab.donepaper.donepaper.dagger.modules.AppModule;
import com.skylab.donepaper.donepaper.dagger.modules.NavigationModule;
import com.skylab.donepaper.donepaper.dagger.modules.OrderModule;
import com.skylab.donepaper.donepaper.dagger.modules.UserModule;
import com.skylab.donepaper.donepaper.fragment.FirstStepFragment;
import com.skylab.donepaper.donepaper.fragment.SecondStepFragment;
import com.skylab.donepaper.donepaper.fragment.ThirdStepFragment;
import com.skylab.donepaper.donepaper.material.PaperDetailsDialog;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NavigationModule.class, UserModule.class, OrderModule.class})
public interface MainComponent {

    void inject(DonePaperApplication app);

    void inject(OrderProcessActivity orderProcessActivity);

    void inject(FirstStepFragment firstStepFragment);

    void inject(SecondStepFragment secondStepFragment);

    void inject(ThirdStepFragment thirdStepFragment);

    void inject(PaperDetailsDialog paperDetailsDialog);

    void inject(LoginActivity loginActivity);

    void inject(SignUpActivity signUpActivity);

    void inject(MainActivity mainActivity);

    void inject(OrderDetailActivity orderDetailActivity);

    void inject(ChatActivity chatActivity);

    void inject(ProfileActivity profileActivity);

    SharedPreferences sharedPreferences();

}
