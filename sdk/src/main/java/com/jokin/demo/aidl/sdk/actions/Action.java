package com.jokin.demo.aidl.sdk.actions;

import android.os.Parcel;

public abstract class Action {
    protected Type mType;

    public enum Type {
        OPEN,
        CLOSE
    }

    /**
     * Action 反序列化
     */
    public static Action fromParcel(Parcel in) {
        String name = in.readString();
        Action action = create(name);
        action.readFromParcel(in);
        return action;
    }

    /**
     * Action 序列化
     */
    public static Parcel toParcel(Parcel dest, int flags, Action action) {
        dest.writeString(action.getType().name());
        action.writeToParcel(dest, flags);
        return dest;
    }

    private static Action create(String name) {
        Type type = Type.valueOf(name);
        Action action;
        switch (type) {
            case OPEN:
                action = new OpenAction();
                break;
            case CLOSE:
                action = new CloseAction();
                break;
            default:
                throw new NullPointerException("wrong action name:" + name);
        }
        return action;
    }

    public final Type getType() {
        return mType;
    }

    abstract void readFromParcel(Parcel in);

    abstract void writeToParcel(Parcel dest, int flags);

}
