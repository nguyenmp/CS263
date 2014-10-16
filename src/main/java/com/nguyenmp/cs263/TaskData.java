package com.nguyenmp.cs263;

import com.google.appengine.api.datastore.*;

import java.io.Serializable;
import java.util.Date;

public class TaskData implements Serializable {
    public static final long serialVersionUID = 0L;

    public static final String TYPE_NAME = "TaskData";
    private static final String KEY_DATE = "date";
    private static final String KEY_VALUE = "value";

    public String name, value;
    public long date;

    public static TaskData fromDataStore(DatastoreService dataStore, String keyName) {
        if (keyName == null) return null;

        Key key = KeyFactory.createKey(TYPE_NAME, keyName);
        TaskData data = null;
        try {
            data = TaskData.fromEntity(dataStore.get(key));
        } catch (EntityNotFoundException e) {
            // Do nothing.  The resulting entity will be set to null
        }

        return data;
    }

    public static TaskData fromEntity(Entity entity) {
        if (entity == null) return null;

        TaskData data = new TaskData();
        data.date = (long) entity.getProperty(KEY_DATE);
        data.name = entity.getKey().getName();
        data.value = (String) entity.getProperty(KEY_VALUE);

        return data;
    }

    public static Key putValues(DatastoreService dataStore, String name, String value) {
        Key taskKey = KeyFactory.createKey(TYPE_NAME, name);
        Entity task = new Entity(taskKey);
        task.setProperty(KEY_VALUE, value);
        task.setProperty(KEY_DATE, new Date().getTime());
        return dataStore.put(task);
    }
}
