package com.jokin.demo.aidl.sdk.actions;

import android.os.Parcel;

public class CloseAction extends Action {
    /**
     * 关闭的提示
     */
    public String mTip;

    @Override
    void readFromParcel(Parcel in) {
        mTip = in.readString();
    }

    @Override
    void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTip);
    }

    CloseAction() {
        mType = Type.CLOSE;
    }

    public CloseAction(String tip) {
        this();
        mTip = tip;
    }
}
