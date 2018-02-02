package rx.rxjavasearch;

import android.app.Application;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.util.Log;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import dagger.internal.DaggerCollections;
import rx.rxjavasearch.dagger.AppModule;
import rx.rxjavasearch.dagger.DaggerPersistentComponent;
import rx.rxjavasearch.dagger.PersistentComponent;
import rx.rxjavasearch.dagger.PersistentModule;
import rx.rxjavasearch.utils.SharedStatesMap;
import rx.rxjavasearch.utils.Utilities;


/**
 * Created by Aleksei Jegorov on 01/02/2018.
 */
public class RxJavaSearchApplication extends Application {

    private static final String TAG = "BEERAPI";
    private static RxJavaSearchApplication sApp;
    public static Thread.UncaughtExceptionHandler androidDefaultUEH;

    private PersistentComponent mPersistentComponent;

    public PersistentComponent getPersistentComponent() {
        return mPersistentComponent;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        FlowManager.init(new FlowConfig.Builder(this).build());

        mPersistentComponent = DaggerPersistentComponent.builder()
                .appModule(new AppModule(this))
                .persistentModule(new PersistentModule())
                .build();

        sApp = this;


        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                String report = "";
                StackTraceElement[] arr = paramThrowable.getStackTrace();
                report = paramThrowable.toString() + "\r\n";
                report += "--------- Stack trace ---------\r\n" + paramThread.toString();
                for (int i = 0; i < arr.length; i++) {
                    report += "    " + arr[i].toString() + "\r\n";
                }

                Throwable cause = paramThrowable.getCause();
                if (cause != null) {
                    report += "\n------------ Cause ------------\r\n";
                    report += cause.toString() + "\r\n";
                    arr = cause.getStackTrace();
                    for (int i = 0; i < arr.length; i++) {
                        report += "    " + arr[i].toString() + "\r\n";
                    }
                }

                String rep = "";
                rep += "Time: " + Utilities.GetTimeNow() + "\r\n";
                rep += "Message: " + paramThrowable.getMessage() + "\r\n";

                Utilities.writeFile("CrashReport.txt", rep + report + "\r\n", false, getApplicationContext(), "");

                androidDefaultUEH.uncaughtException(paramThread, paramThrowable);
            }
        });
    }

    public static RxJavaSearchApplication getApp() {
        return sApp;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        onTrimMemory(TRIM_MEMORY_COMPLETE);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Log.d(TAG, "Application not visible anymore");
        } else if (level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
            Log.d(TAG, "Application is going to be killed");
        }
    }

/*
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
*/
}