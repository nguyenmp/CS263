package com.nguyenmp.cs263_real.dao;

import com.google.appengine.api.datastore.*;
import com.nguyenmp.cs263_real.model.UsageModel;

import java.io.Serializable;
import java.util.*;

public class UsageDao implements Serializable {
    private static final String KIND = "usage";
    private static final String KEY_TIMESTAMP = "date_time";
    private static final String KEY_USER = "username";
    private static final String KEY_HOSTNAME = "hostname";
    private static final String KEY_IS_REMOTE = "is_remote";

    public static boolean prune() {
        return false;
    }

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

    public static String[] getComputers() {
        Query query = new Query(KIND)
                .addProjection(new PropertyProjection(KEY_HOSTNAME, String.class))
                .setDistinct(true);

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Iterator<Entity> entityIterator = datastoreService.prepare(query).asIterator();
        List<String> computers = new ArrayList<>();
        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            computers.add((String) entity.getProperty(KEY_HOSTNAME));
        }

        return computers.toArray(new String[computers.size()]);
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
        Query query = new Query(KIND)
                .addSort(KEY_TIMESTAMP)
                .setFilter(new Query.FilterPredicate(KEY_HOSTNAME, Query.FilterOperator.EQUAL, hostname));

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Iterator<Entity> entityIterator = datastoreService.prepare(query).asIterator();

        List<UsageModel> usage = new ArrayList<>();
        while (entityIterator.hasNext()) {
            usage.add(fromEntity(entityIterator.next()));
        }

        return usage.toArray(new UsageModel[usage.size()]);
    }

    @Deprecated
    public static UsageModel[] getByComputerInWeek(String hostname, int weeksAgo) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.add(Calendar.WEEK_OF_YEAR, -weeksAgo);

        return getByComputerInWeek(hostname, calendar.getTimeInMillis());
    }

    public static UsageModel[] getByComputerInDay(String hostname, long date) {
        long[] timeIntervalDay = getTimeIntervalDay(date);
        return getByComputerInRange(hostname, timeIntervalDay[0], timeIntervalDay[1]);
    }

    public static UsageModel[] getByComputerInWeek(String hostname, long date_in_week) {
        long[] interval = getTimeIntervalWeek(date_in_week);
        return getByComputerInRange(hostname, interval[0], interval[1]);
    }

    public static long[] getTimeIntervalWeek(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(new Date(time));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // "calculate" the start date of the week
        Calendar first = (Calendar) calendar.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));

        // and add six days to the end date
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, 7);
        last.add(Calendar.MILLISECOND, -1);

        return new long[] {first.getTimeInMillis(), last.getTimeInMillis()};
    }

    public static long[] getTimeIntervalDay(long time) {
        // "calculate" the start date of the day
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(new Date(time));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar first = (Calendar) calendar.clone();

        // and add 1 days to the end date
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, 1);
        last.add(Calendar.MILLISECOND, -1);

        return new long[] {first.getTimeInMillis(), last.getTimeInMillis()};
    }

    public static UsageModel[] getByComputerInRange(String hostname, long start, long end) throws UnsupportedOperationException {
        Query.FilterPredicate filter1 = new Query.FilterPredicate(KEY_HOSTNAME, Query.FilterOperator.EQUAL, hostname);
        Query.FilterPredicate filter2 = new Query.FilterPredicate(KEY_TIMESTAMP, Query.FilterOperator.LESS_THAN_OR_EQUAL, end);
        Query.FilterPredicate filter3 = new Query.FilterPredicate(KEY_TIMESTAMP, Query.FilterOperator.GREATER_THAN_OR_EQUAL, start);
        Set<Query.Filter> filters = new HashSet<>();
        filters.add(filter1);
        filters.add(filter2);
        filters.add(filter3);

        Query query = new Query(KIND)
                .addSort(KEY_TIMESTAMP)
                .setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.AND, filters));

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Iterator<Entity> entityIterator = datastoreService.prepare(query).asIterator();

        List<UsageModel> usage = new ArrayList<>();
        while (entityIterator.hasNext()) {
            usage.add(fromEntity(entityIterator.next()));
        }

        return usage.toArray(new UsageModel[usage.size()]);
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

    public static void delete(UsageModel[] usages) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        for (UsageModel usage : usages) {
            Key key = KeyFactory.createKey(KIND, usage.id);
            datastoreService.delete(key);
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

        for (UsageModel usage : usages) {
            usage.timestamp = System.currentTimeMillis();
            Key key = datastoreService.put(toEntity(usage));
            usage.id = key.getId();
        }

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

    private static final String PARENT_KIND = "usage_group";
    @Deprecated
    private static Entity newParent() {
        return new Entity(PARENT_KIND);
    }
}
