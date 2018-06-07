// IActionListener.aidl.aidl
package com.jokin.demo.aidl.sdk;

// Declare any non-default types here with import statements
// 自定义类型，必须显式 import
import com.jokin.demo.aidl.sdk.IAction;

interface IActionListener {
	// 基本类型，不需 import，默认是 in
    void onDone(String action);
}
