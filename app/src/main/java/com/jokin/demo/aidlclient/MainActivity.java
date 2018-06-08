package com.jokin.demo.aidlclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initClient();
        initMessenger();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
        unbindService(mMessengerServiceConnection);
    }

    //////////// Messenger /////////////

    private static final String MESSENGER_PACKAGE_NAME = "com.jokin.demo.aidl.server";
    private static final String MESSENGER_ACTION_SERVICE = MESSENGER_PACKAGE_NAME+".MessengerService";

    public static final int OPEN_ACTION = 1000;
    public static final int CLOSE_ACTION = 1001;

    public static final int OPEN_RESULT = 2000;
    public static final int CLOSE_RESULT = 2001;

    public static final String KEY_OF_COLOR = "key.color";
    public static final String KEY_OF_SIZE = "key.size";
    public static final String KEY_OF_TIP = "key.tip";

    private void initMessenger() {
        Intent intentService = new Intent();
        intentService.setComponent(new ComponentName(MESSENGER_PACKAGE_NAME, MESSENGER_ACTION_SERVICE));
        bindService(intentService, mMessengerServiceConnection, BIND_AUTO_CREATE);

        findViewById(R.id.openActionMessenger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Message message = Message.obtain();
                    message.what = OPEN_ACTION;
                    Bundle bundle = new Bundle();
                    bundle.putInt(KEY_OF_COLOR, Color.BLACK);
                    bundle.putInt(KEY_OF_SIZE, 10);
                    message.setData(bundle);
                    message.replyTo = mClientMessenger;

                    mServerMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.closeActionMessenger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Message message = Message.obtain();
                    message.what = CLOSE_ACTION;
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_OF_TIP, "Messenger要close拉！");
                    message.setData(bundle);
                    message.replyTo = mClientMessenger;

                    mServerMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Messenger mServerMessenger;
    private ServiceConnection mMessengerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServerMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServerMessenger = null;
        }
    };

    /**
     * 注意内存泄漏，这里不修复了，参考Server。
     */
    Messenger mClientMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case OPEN_RESULT:
                    Log.e(TAG, "open with result:"+msg.toString());
                    break;
                case CLOSE_RESULT:
                    Log.e(TAG, "close with result:"+msg.toString());
                    break;
                default:
                    break;
            }
        }
    });

    //////////// AIDL ///////////////////

    private static final String PACKAGE_NAME = "com.jokin.demo.aidl.server";
    private static final String ACTION_SERVICE = PACKAGE_NAME+".ActionService";

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
