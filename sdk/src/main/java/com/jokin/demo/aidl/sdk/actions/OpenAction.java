package com.jokin.demo.aidl.sdk.actions;

import android.os.Parcel;
import android.support.annotation.NonNull;

public class OpenAction extends Action {
    /**
     * 打开的参数
     */
    public int mPenColor;
    public int mPenSize;

    OpenAction() {
        mType = Type.OPEN;
    }

    public OpenAction(@NonNull int color, @NonNull int size) {
        this();
        mPenColor = color;
        mPenSize = size;
    }

    @Override
    void readFromParcel(Parcel in) {
        mPenColor = in.readInt();
        mPenSize =in.readInt();
    }

    @Override
    void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPenColor);
        dest.writeInt(mPenSize);
    }
}
