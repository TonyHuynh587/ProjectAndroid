package com.skylab.donepaper.donepaper.fragment.navigation;

import android.app.FragmentManager;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.fragment.FirstStepFragment;
import com.skylab.donepaper.donepaper.fragment.SecondStepFragment;
import com.skylab.donepaper.donepaper.fragment.ThirdStepFragment;
import com.skylab.donepaper.donepaper.rest.model.FormInfoData;

import java.util.ArrayList;

public class NavigationManager {

    public static final int FIRST_STEP_INDEX = 0;
    public static final int SECOND_STEP_INDEX = 1;
    public static final int THIRD_STEP_INDEX = 2;

    private ArrayList<String> paperTypes;
    private ArrayList<String> paperSubjects;
    private ArrayList<String> citationStyles;

    private ArrayList<Double> highSchoolPriceList;
    private ArrayList<Double> collegePriceList;
    private ArrayList<Double> universityPriceList;

    private ArrayList<String> deadlineList;

    private String defaultPaperType;
    private String defaultPaperSubject;
    private String defaultCitationStyle;

    private String defaultAcademicLevel;
    private int defaultNumberOfPages;
    private int wordsPerPage;
    private String defaultSpacingOptions;
    private String defaultTypeCustomer;
    private String totalPrice;

    private FirstStepFragment firstStepFragment;
    private SecondStepFragment secondStepFragment;
    private ThirdStepFragment thirdStepFragment;

    private int mStack = 0;

    /**
     * Listener interface for navigation events.
     */
    public interface NavigationListener {

        /**
         * Callback on backstack changed.
         */
        void onBackstackChanged();
    }

    private FragmentManager mFragmentManager;
    private NavigationListener mNavigationListener;

    /**
     * Initialize the NavigationManager with a FragmentManager, which will be used at the
     * fragment transactions.
     *
     * @param fragmentManager: fragment manager
     */
    public void init(FragmentManager fragmentManager) {

        mFragmentManager = fragmentManager;

        firstStepFragment = FirstStepFragment.newInstance();
        secondStepFragment = SecondStepFragment.newInstance();
        thirdStepFragment = ThirdStepFragment.newInstance();

        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (mNavigationListener != null) {
                    mNavigationListener.onBackstackChanged();
                }
            }
        });

        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_content, firstStepFragment, "first")
                .commit();
    }

    public void nextFragment() {
        if (mFragmentManager != null) {
            switch (mStack) {
                case FIRST_STEP_INDEX:
                    mFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                    R.animator.card_flip_right_in,
                                    R.animator.card_flip_right_out,
                                    R.animator.card_flip_left_in,
                                    R.animator.card_flip_left_out)
                            .replace(R.id.fragment_content, secondStepFragment)
                            .addToBackStack("second")
                            .commit();
                    break;
                case SECOND_STEP_INDEX:
                    mFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                    R.animator.card_flip_right_in,
                                    R.animator.card_flip_right_out,
                                    R.animator.card_flip_left_in,
                                    R.animator.card_flip_left_out)
                            .replace(R.id.fragment_content, thirdStepFragment)
                            .addToBackStack("third")
                            .commit();
                    break;
                case THIRD_STEP_INDEX:
                default:
                    break;
            }
        }
        mStack++;
    }

    public void previousFragment() {
        if (mFragmentManager != null) {
            switch (getCurrentStep()) {
                case FIRST_STEP_INDEX:
                    break;
                case SECOND_STEP_INDEX:
                    mFragmentManager.popBackStack();
                    break;
                case THIRD_STEP_INDEX:
                    mFragmentManager.popBackStack();
                    break;
                default:
                    break;
            }
        }
        mStack--;
    }

    public void switchFragment(int index) {
        if (mFragmentManager != null && getCurrentStep() != index) {
            switch (index) {
                case FIRST_STEP_INDEX:
                    mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    mStack = FIRST_STEP_INDEX;
                    break;
                case SECOND_STEP_INDEX:
                    if (getCurrentStep() == THIRD_STEP_INDEX) {
                        previousFragment();
                    } else {
                        nextFragment();
                    }
                    break;
                case THIRD_STEP_INDEX:
                    nextFragment();
                    if (mStack == SECOND_STEP_INDEX) {
                        nextFragment();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void setNavigationListener(NavigationListener navigationListener) {
        mNavigationListener = navigationListener;
    }

    public ArrayList<String> getPaperTypes() {
        return paperTypes;
    }

    public ArrayList<String> getPaperSubjects() {
        return paperSubjects;
    }

    public ArrayList<String> getCitationStyles() {
        return citationStyles;
    }

    public ArrayList<Double> getHighSchoolPriceList() {
        return highSchoolPriceList;
    }

    public ArrayList<Double> getCollegePriceList() {
        return collegePriceList;
    }

    public ArrayList<Double> getUniversityPriceList() {
        return universityPriceList;
    }

    public ArrayList<String> getDeadlineList() {
        return deadlineList;
    }

    public int getCurrentStep() {
        return mStack;
    }

    public String getDefaultPaperType() {
        return defaultPaperType;
    }

    public String getDefaultPaperSubject() {
        return defaultPaperSubject;
    }

    public String getDefaultCitationStyle() {
        return defaultCitationStyle;
    }

    public String getDefaultAcademicLevel() {
        return defaultAcademicLevel;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setDefaultAcademicLevel(String defaultAcademiclevel) {
        this.defaultAcademicLevel = defaultAcademiclevel;
    }

    public int getDefaultNumberOfPages() {
        return defaultNumberOfPages;
    }

    public void setDefaultNumberOfPages(int defaultNumberOfPages) {
        this.defaultNumberOfPages = defaultNumberOfPages;
    }

    public int getWordsPerPage() {
        return wordsPerPage;
    }

    public void setWordsPerPage(int wordsPerPage) {
        this.wordsPerPage = wordsPerPage;
    }

    public String getDefaultSpacingOptions() {
        return defaultSpacingOptions;
    }

    public void setDefaultSpacingOptions(String defaultSpacingOptions) {
        this.defaultSpacingOptions = defaultSpacingOptions;
    }

    public String getDefaultTypeCustomer() {
        return defaultTypeCustomer;
    }

    public void setDefaultTypeCustomer(String defaultTypeCustomer) {
        this.defaultTypeCustomer = defaultTypeCustomer;
    }

    public boolean isFirstStep() {
        return mStack == FIRST_STEP_INDEX;
    }

    public void reset() {
        mStack = 0;
        mNavigationListener = null;
        mFragmentManager = null;
        firstStepFragment = null;
        secondStepFragment = null;
        thirdStepFragment = null;
    }

    public void pourData(FormInfoData formInfoData) {
        this.paperTypes = (ArrayList<String>) formInfoData.getPaperTypes();
        this.paperSubjects = (ArrayList<String>) formInfoData.getPaperSubjects();
        this.citationStyles = (ArrayList<String>) formInfoData.getCitationStyles();

        this.highSchoolPriceList = formInfoData.getPricing().getHighschoolPriceList();
        this.collegePriceList = formInfoData.getPricing().getCollegePriceList();
        this.universityPriceList = formInfoData.getPricing().getUniversityPriceList();

        this.deadlineList = formInfoData.getDeadlines().toArrayList();

        this.defaultPaperType = paperTypes.get(0);
        this.defaultPaperSubject = paperSubjects.get(0);
        this.defaultCitationStyle = formInfoData.getDefaultCitationStyle();

        this.defaultAcademicLevel = formInfoData.getDefaultAcademicLevel();
        this.defaultNumberOfPages = formInfoData.getDefaultNumberOfPages();
        this.wordsPerPage = formInfoData.getWordsPerPage();
        this.defaultSpacingOptions = formInfoData.getDefaultSpacingOption();

    }
}
