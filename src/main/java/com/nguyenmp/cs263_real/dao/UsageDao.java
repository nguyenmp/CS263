package com.nguyenmp.cs263_real.dao;

import com.google.appengine.api.datastore.*;
import com.nguyenmp.cs263_real.model.UsageModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UsageDao implements Serializable {
    private static final String KIND = "usage";
    private static final String KEY_TIMESTAMP = "date_time";
    private static final String KEY_USER = "username";
    private static final String KEY_HOSTNAME = "hostname";
    private static final String KEY_IS_REMOTE = "is_remote";

    public static String[] getUsers() {
        Query query = new Query(KIND)
                .addProjection(new PropertyProjection(KEY_USER, String.class))
                .setDistinct(true);

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Iterator<Entity> entityIterator = datastoreService.prepare(query).asIterator();
        List<String> users = new ArrayList<>();
        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            users.add((String) entity.getProperty(KEY_USER));
        }

        return users.toArray(new String[users.size()]);
    }

    public static UsageModel[] getByUser(String username) {
        Query query = new Query(KIND)
                .addSort(KEY_TIMESTAMP)
                .setFilter(new Query.FilterPredicate(KEY_USER, Query.FilterOperator.EQUAL, username));

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Iterator<Entity> entityIterator = datastoreService.prepare(query).asIterator();

        List<UsageModel> usage = new ArrayList<>();
        while (entityIterator.hasNext()) {
            usage.add(fromEntity(entityIterator.next()));
        }

        return usage.toArray(new UsageModel[usage.size()]);
    }

    public static UsageModel[] getByComputer(String hostname) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Method not implemented yet.");
    }

    /**
     * @param id the ID of the Usage to get
     * @return if the ID is found in our database, then this function returns null.
     * Otherwise, if this id exists in our database, then we return a fully defined UsageModel
     */
    public static UsageModel get(long id) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key key = KeyFactory.createKey(KIND, id);
        try {
            Entity entity = datastoreService.get(key);
            return fromEntity(entity);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    /**
     * Puts the given array of usages into the database as a single transaction.
     * @param usages this function mutates this parameter by filling in the timestamp and id field
     * @return the same usage models with populated timestamps and id fields
     */
    public static UsageModel[] put(UsageModel[] usages) {
        if (usages == null) return null;

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        Entity parent = null;
        if (usages.length > 0) {
            parent = newParent();
            datastoreService.put(parent);
        }

        Transaction transaction = datastoreService.beginTransaction();
        for (UsageModel usage : usages) {
            usage.timestamp = System.currentTimeMillis();
            Key key = datastoreService.put(transaction, toEntity(usage, parent.getKey()));
            usage.id = key.getId();
        }
        transaction.commitAsync();

        return usages;
    }

    public static UsageModel put(String username, String hostname, boolean isRemote) {
        UsageModel usage = new UsageModel();
        usage.username = username;
        usage.isRemote = isRemote;
        usage.hostname = hostname;
        usage.timestamp = System.currentTimeMillis();

        // Allow GAE's data store to automatically generate numeric key for us
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        usage.id = datastoreService.put(toEntity(usage, null)).getId();

        return usage;
    }

    private static UsageModel fromEntity(Entity entity) {
        UsageModel model = new UsageModel();
        model.timestamp = (long) entity.getProperty(KEY_TIMESTAMP);
        model.username = (String) entity.getProperty(KEY_USER);
        model.hostname = (String) entity.getProperty(KEY_HOSTNAME);
        model.isRemote = (boolean) entity.getProperty(KEY_IS_REMOTE);

        Key key = entity.getKey();
        model.id = key == null ? null : key.getId();
        return model;
    }

    private static Entity toEntity(UsageModel usage, Key parent) {
        Entity task = usage.id == null ? new Entity(KIND, parent) : new Entity(KIND, usage.id, parent);
        task.setProperty(KEY_TIMESTAMP, usage.timestamp);
        task.setProperty(KEY_USER, usage.username);
        task.setProperty(KEY_HOSTNAME, usage.hostname);
        task.setProperty(KEY_IS_REMOTE, usage.isRemote);
        return task;
    }

    private static final String PARENT_KIND = "usage_group";
    private static Entity newParent() {
        return new Entity(PARENT_KIND);
    }
}
