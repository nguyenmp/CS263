package com.nguyenmp.cs263;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.util.Date;

public class TaskData {
    private static final String TYPE_NAME = "TaskData";
    private static final String KEY_DATE = "date";
    private static final String KEY_VALUE = "value";

    public String name, value;
    public int date;

    public static TaskData fromEntity(Entity entity) {
        if (entity == null) return null;

        TaskData data = new TaskData();
        data.date = (int) entity.getProperty(KEY_DATE);
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
