package com.tanuj.nowplayinghistory.fragments;

import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntroBaseFragment;
import com.github.paolorotolo.appintro.ISlidePolicy;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

public class CustomPolicySlide extends AppIntroBaseFragment implements ISlidePolicy {

    private ISlidePolicy slidePolicy;

    public static CustomPolicySlide newInstance(CharSequence title, CharSequence description, @DrawableRes int imageDrawable, @ColorInt int bgColor, ISlidePolicy slidePolicy) {
        CustomPolicySlide slide = new CustomPolicySlide();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title.toString());
        args.putString(ARG_TITLE_TYPEFACE, null);
        args.putString(ARG_DESC, description.toString());
        args.putString(ARG_DESC_TYPEFACE, null);
        args.putInt(ARG_DRAWABLE, imageDrawable);
        args.putInt(ARG_BG_COLOR, bgColor);
        args.putInt(ARG_TITLE_COLOR, 0);
        args.putInt(ARG_DESC_COLOR, 0);
        slide.setArguments(args);
        slide.setSlidePolicy(slidePolicy);

        return slide;
    }

    protected void setSlidePolicy(ISlidePolicy slidePolicy) {
        this.slidePolicy = slidePolicy;
    }

    @Override
    protected int getLayoutId() {
        return com.github.paolorotolo.appintro.R.layout.appintro_fragment_intro2;
    }

    @Override
    public boolean isPolicyRespected() {
        if (slidePolicy != null) {
            return slidePolicy.isPolicyRespected();
        }
        return false;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        if (slidePolicy != null) {
            slidePolicy.onUserIllegallyRequestedNextPage();
        }
    }
}
