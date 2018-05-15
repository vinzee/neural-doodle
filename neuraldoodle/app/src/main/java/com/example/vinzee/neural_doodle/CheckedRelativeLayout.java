package com.example.vinzee.neural_doodle;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckedRelativeLayout extends RelativeLayout implements Checkable {

    boolean mFlgChecked = false;

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };

    public CheckedRelativeLayout(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {

        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public boolean isChecked() {

        return mFlgChecked;
    }

    @Override
    public void setChecked(boolean checked) {

        mFlgChecked = checked;
        refreshDrawableState();
    }

    @Override
    public void toggle() {

        mFlgChecked = !mFlgChecked;
    }
}