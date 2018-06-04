package com.skylab.donepaper.donepaper.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.skylab.donepaper.donepaper.R;

import static com.skylab.donepaper.donepaper.fragment.navigation.NavigationManager.FIRST_STEP_INDEX;
import static com.skylab.donepaper.donepaper.fragment.navigation.NavigationManager.SECOND_STEP_INDEX;
import static com.skylab.donepaper.donepaper.fragment.navigation.NavigationManager.THIRD_STEP_INDEX;

public class StepsProgressBar extends RelativeLayout {

    private ProgressBar mProgressBar;
    private Context mContext;
    private AppCompatTextView mStepOneText, mStepTwoText, mStepThreeText,
            mStepOneCircle, mStepTwoCircle, mStepThreeCircle;

    public StepsProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.steps_progressbar_layout, this, true);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mStepOneText = (AppCompatTextView) findViewById(R.id.step_one_text);
        mStepTwoText = (AppCompatTextView) findViewById(R.id.step_two_text);
        mStepThreeText = (AppCompatTextView) findViewById(R.id.step_three_text);

        mStepOneCircle = (AppCompatTextView) findViewById(R.id.step_one_circle);
        mStepTwoCircle = (AppCompatTextView) findViewById(R.id.step_two_circle);
        mStepThreeCircle = (AppCompatTextView) findViewById(R.id.step_three_circle);

        mStepOneCircle.setSelected(true);
    }

    public synchronized boolean setCurrentStep(int currentStep){
        switch (currentStep){
            case FIRST_STEP_INDEX:
                ObjectAnimator animation = ObjectAnimator.ofInt(mProgressBar, "progress", 0);
                animation.setDuration(400); // 0.4 second
                animation.setInterpolator(new AccelerateInterpolator());
                animation.start();

                mStepOneText.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                mStepTwoText.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextTint));
                mStepThreeText.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextTint));

                Animator animatorX = ObjectAnimator.ofFloat(mStepOneCircle, "scaleX", 1.5f, 1f);
                Animator animatorY = ObjectAnimator.ofFloat(mStepOneCircle, "scaleY", 1.5f, 1f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animatorX, animatorY);
                animatorSet.start();

                mStepOneCircle.setText("1");
                mStepOneCircle.setActivated(false);
                mStepOneCircle.setSelected(true);
                mStepTwoCircle.setText("2");
                mStepTwoCircle.setActivated(false);
                mStepTwoCircle.setSelected(false);
                mStepThreeCircle.setText("3");
                mStepThreeCircle.setSelected(false);
                break;
            case SECOND_STEP_INDEX:
                animation = ObjectAnimator.ofInt(mProgressBar, "progress", 50);
                animation.setDuration(400); // 0.4 second
                animation.setInterpolator(new AccelerateInterpolator());
                animation.start();

                mStepOneText.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextTint));
                mStepTwoText.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                mStepThreeText.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextTint));

                mStepOneCircle.setText("");
                animatorX = ObjectAnimator.ofFloat(mStepOneCircle, "scaleX", 1.5f, 1f);
                animatorY = ObjectAnimator.ofFloat(mStepOneCircle, "scaleY", 1.5f, 1f);
                animatorSet = new AnimatorSet();
                animatorSet.playTogether(animatorX, animatorY);
                animatorSet.start();
                mStepOneCircle.setActivated(true);

                mStepTwoCircle.setText("2");
                mStepTwoCircle.setActivated(false);
                mStepTwoCircle.setSelected(true);

                mStepThreeCircle.setText("3");
                mStepThreeCircle.setSelected(false);
                break;
            case THIRD_STEP_INDEX:
                animation = ObjectAnimator.ofInt(mProgressBar, "progress", 100);
                animation.setDuration(400); // 0.4 second
                animation.setInterpolator(new AccelerateInterpolator());
                animation.start();

                mStepOneText.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextTint));
                mStepTwoText.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextTint));
                mStepThreeText.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));

                mStepOneCircle.setText("");
                mStepOneCircle.setActivated(true);

                mStepTwoCircle.setText("");
                animatorX = ObjectAnimator.ofFloat(mStepTwoCircle, "scaleX", 1.5f, 1f);
                animatorY = ObjectAnimator.ofFloat(mStepTwoCircle, "scaleY", 1.5f, 1f);
                animatorSet = new AnimatorSet();
                animatorSet.playTogether(animatorX, animatorY);
                animatorSet.start();
                mStepTwoCircle.setActivated(true);

                mStepThreeCircle.setText("3");
                mStepThreeCircle.setSelected(true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener listener) {
        super.setOnClickListener(listener);
        findViewById(R.id.step_one_layout).setOnClickListener(listener);
        findViewById(R.id.step_two_layout).setOnClickListener(listener);
        findViewById(R.id.step_three_layout).setOnClickListener(listener);
    }
}
