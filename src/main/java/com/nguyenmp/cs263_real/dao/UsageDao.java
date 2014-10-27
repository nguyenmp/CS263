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
     * Puts this datagram into the database
     * @param usage A pre-initialized usage object that contains the data to put into the server.
     *              {@link UsageModel#id} is ignored and will be replaced by the ID of the new entry.
     * @return the same usage item with the {@link UsageModel#id} set from the put.
     */
    public static UsageModel put(UsageModel usage) {
        // Allow GAE's data store to automatically generate numeric key for us
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        usage.id = datastoreService.put(toEntity(usage)).getId();

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

    private static Entity toEntity(UsageModel usage) {
        Entity task = usage.id == null ? new Entity(KIND) : new Entity(KIND, usage.id);
        task.setProperty(KEY_TIMESTAMP, usage.timestamp);
        task.setProperty(KEY_USER, usage.username);
        task.setProperty(KEY_HOSTNAME, usage.hostname);
        task.setProperty(KEY_IS_REMOTE, usage.isRemote);
        return task;
    }
}
