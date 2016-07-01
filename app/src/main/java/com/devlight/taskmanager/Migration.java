package com.devlight.taskmanager;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;


public class Migration implements RealmMigration {


    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0 && newVersion ==8) {

            // Create a new class
            RealmObjectSchema taskSchema = schema.create("Task")
                    .addField("mPeriodicHours", Long.class, FieldAttribute.REQUIRED)
                    .addField("mIsPeriodic", Boolean.class, FieldAttribute.REQUIRED)
                    .addField("mIsPaused", Boolean.class, FieldAttribute.REQUIRED)
                    .addField("mAvatarPath", String.class, FieldAttribute.REQUIRED);


            oldVersion++;
        }


    }
}
