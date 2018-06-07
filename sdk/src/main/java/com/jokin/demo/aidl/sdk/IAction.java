package com.jokin.demo.aidl.sdk;

import android.os.Parcel;
import android.os.Parcelable;

import com.jokin.demo.aidl.sdk.actions.Action;

/**
 * Created by jokin on 2018/6/5 16:36.
 */

public class IAction implements Parcelable {
    protected IAction(Parcel in) {
        mAction = Action.fromParcel(in);
    }

    public static final Creator<IAction> CREATOR = new Creator<IAction>() {
        @Override
        public IAction createFromParcel(Parcel in) {
            return new IAction(in);
        }

        @Override
        public IAction[] newArray(int size) {
            return new IAction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mAction != null) {
            Action.toParcel(dest, flags, mAction);
        }
    }

    //////// Main /////////

    private Action mAction;

    public IAction(Action action) {
        mAction = action;
    }

    public Action getAction() {
        return mAction;
    }
}
