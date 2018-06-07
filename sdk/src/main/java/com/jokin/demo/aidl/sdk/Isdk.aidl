// IActionListener.aidl.aidl
package com.jokin.demo.aidl.sdk;

// Declare any non-default types here with import statements
// 自定义类型，必须显式 import
import com.jokin.demo.aidl.sdk.IAction;
import com.jokin.demo.aidl.sdk.IActionListener;

interface Isdk {
    boolean doAction(String key, in IAction action);
    void addActionListener(String key, IActionListener listener);
    void removeActionListener(String key);
}
