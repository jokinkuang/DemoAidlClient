package com.jokin.demo.aidlclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.jokin.demo.aidl.sdk.IAction;
import com.jokin.demo.aidl.sdk.Isdk;
import com.jokin.demo.aidl.sdk.actions.CloseAction;
import com.jokin.demo.aidl.sdk.actions.OpenAction;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String PACKAGE_NAME = "com.jokin.demo.aidl.server";
    private static final String ACTION_SERVICE = PACKAGE_NAME+".ActionService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initClient();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
    }

    private void initClient() {
        Intent intentService = new Intent();
        intentService.setComponent(new ComponentName(PACKAGE_NAME, ACTION_SERVICE));
        bindService(intentService, mServiceConnection, BIND_AUTO_CREATE);

        findViewById(R.id.openAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mIsdk.doAction("open", new IAction(new OpenAction(Color.BLACK, 10)));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.closeAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 20; ++i) {
                    Thread thread = new Thread(closeRunnable, "thread-"+i);
                    thread.start();
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        });
    }

    private Object lock = new Object();
    private Runnable closeRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Log.e(TAG, Thread.currentThread().getName()+" call close action!!!");
                mIsdk.doAction("close", new IAction(new CloseAction("要关闭拉！")));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private Isdk mIsdk;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected() called with: name = [" + name + "]");
            mIsdk = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() called with: name = [" + name + "], service = [" + service + "]");
            //通过服务端onBind方法返回的binder对象得到IMyService的实例，得到实例就可以调用它的方法了
            mIsdk = Isdk.Stub.asInterface(service);
        }
    };
}
