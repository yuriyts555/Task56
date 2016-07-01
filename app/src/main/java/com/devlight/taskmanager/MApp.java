package com.devlight.taskmanager;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by yura on 30.06.2016.
 */
public class MApp extends Application {


    public RealmConfiguration realmConfiguration;
    public Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();


        if (realmConfiguration == null)
            realmConfiguration = new RealmConfiguration
                    .Builder(this)
                    .name("mtaskmanger.realm")
                    //.deleteRealmIfMigrationNeeded()
                    .schemaVersion(8)
                    .migration(new Migration())
                    .build();

        if (realm == null) realm = Realm.getInstance(realmConfiguration);

    }




    @Override
    public void onTerminate() {

        realm.close();

        super.onTerminate();
    }

    public static RealmConfiguration getThreadConfiguration(Context context) {

        return       new RealmConfiguration
                .Builder(context)
                .name("mtaskmanger.realm")
                //.deleteRealmIfMigrationNeeded()
                .schemaVersion(8)
                .migration(new Migration())
                .build();
    }
}
