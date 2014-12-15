package com.nguyenmp.cs263_real.dao;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nguyenmp.cs263_real.model.UsageModel;
import com.nguyenmp.cs263_real.servlet.DatastoreToBlobstoreConverter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.google.appengine.api.datastore.Query.FilterOperator.EQUAL;
import static com.google.appengine.api.datastore.Query.FilterOperator.GREATER_THAN_OR_EQUAL;
import static com.google.appengine.api.datastore.Query.FilterOperator.LESS_THAN_OR_EQUAL;
import static com.google.appengine.api.datastore.Query.FilterPredicate;
import static com.nguyenmp.cs263_real.servlet.DatastoreToBlobstoreConverter.Interval;

/**
 * <p>The Data Access Object for all Usages in our database.</p>
 *
 * <p>All public static facing methods in this class are self containing
 * and do not require any state information.</p>
 *
 * <p>To access the database, simply call the public static methods
 * in this class and we will handle all serialization and deserialization for you.</p>
 */
public class UsageDao {

    /** The KIND for the {@link UsageModel} datatype to be
     * stored into the database under */
    private static final String KIND = "usage";

    /** the key for the timestamp.  the timestamp is stored as
     * time since epoche in UTC/GMT (+00:00) */
    private static final String KEY_TIMESTAMP = "date_time";

    /** the username that corresponds with this entry. e.g., "mpnguyen" */
    private static final String KEY_USER = "username";

    /** the string hostname for this entry. e.g., "csil.cs.ucsb.edu" */
    private static final String KEY_HOSTNAME = "hostname";

    /** the key that corresponds with the boolean that represents whether
     *  the data point is a remote login (true or false) */
    private static final String KEY_IS_REMOTE = "is_remote";

    /**
     * @return an array of all usernames stored in the Datastore (since the last database wipe)
     * e.g., {"mpnguyen", "dcoffill", "cs263"}
     */
    @Nonnull public static String[] getUsers() {
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

    /**
     * @return an array of all computer urls stored in the Datastore (since the last database wipe)
     * e.g., {"csil.cs.ucsb.edu", "linus.cs.ucsb.edu"}
     */
    @Nonnull public static String[] getComputers() {
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

    /**
     * <p>Queries the datastore for all checkins from the given user.</p>
     * @param username the username ("mpnguyen") of the user
     * @return all data entries belonging to that user ordered in ascending order by timestamp
     */
    @Nonnull private static UsageModel[] getByUser(@Nonnull String username) {
        Query query = new Query(KIND)
                .addSort(KEY_TIMESTAMP)
                .setFilter(new FilterPredicate(KEY_USER, EQUAL, username));

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Iterator<Entity> entityIterator = datastoreService.prepare(query).asIterator();

        List<UsageModel> usage = new ArrayList<>();
        while (entityIterator.hasNext()) {
            usage.add(fromEntity(entityIterator.next()));
        }

        return usage.toArray(new UsageModel[usage.size()]);
    }

    /**
     * <p>Queries the memcache and falls back to the datastore
     * for all checkins from the given user.</p>
     * @param username the username ("mpnguyen") of the user
     * @return all data entries belonging to that user ordered in ascending order by timestamp
     */
    @Nonnull public static UsageModel[] getByUserCached(String username) {
        MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        String key = "user_" + username;
        UsageModel[] usages = (UsageModel[]) memcacheService.get(key);
        if (usages == null) {
            usages = getByUser(username);
            memcacheService.put(key, usages, Expiration.byDeltaSeconds(60*15)); // 15 minutes
        }

        return usages;
    }

    /**
     * <p>Queries the datastore for all checkins from the given hostname.</p>
     * @param hostname the hostname ("linus.cs.ucsb.edu") of the computer
     * @return all data entries belonging to that computer ordered in ascending order by timestamp
     */
    @Nonnull private static UsageModel[] getByComputer(String hostname) throws UnsupportedOperationException {
        Query query = new Query(KIND)
                .addSort(KEY_TIMESTAMP)
                .setFilter(new FilterPredicate(KEY_HOSTNAME, EQUAL, hostname));

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Iterator<Entity> entityIterator = datastoreService.prepare(query).asIterator();

        List<UsageModel> usage = new ArrayList<>();
        while (entityIterator.hasNext()) {
            usage.add(fromEntity(entityIterator.next()));
        }

        return usage.toArray(new UsageModel[usage.size()]);
    }

    /**
     * <p>Queries the memcache and falls back to the datastore
     * for all checkins from the given computer.</p>
     * @param hostname the hostname ("linus.cs.ucsb.edu") of the computer
     * @return all data entries belonging to that user ordered in ascending order by timestamp
     */
    @Nonnull public static Map<String, LinkedList<Interval>> getByComputerCached(String hostname) {
        MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        String key = "computer_" + hostname;
        Map<String, LinkedList<Interval>> map = (Map<String, LinkedList<Interval>>) memcacheService.get(key);
        if (map == null) {
            UsageModel[] data = getByComputer(hostname);
            map = DatastoreToBlobstoreConverter.convertToIntervalsByUser(data);
            memcacheService.put(key, map, Expiration.byDeltaSeconds(60*15)); // 15 minutes
        }

        return map;
    }

    /**
     * <p>Queries the database for all data entries pertaining to the given computer
     * and puts that data into the mem cache.  This call should be used to avoid having
     * expired data in the mem cache.  Alternatively, it can be used to make sure this
     * data is always in the cache by persisting it whenever the timeout elapses.</p>
     * @param hostname the hostname ("linus.cs.ucsb.edu") of the computer
     * @return the mapping of users to time intervals of login periods on this computer
     */
    public static Map<String, LinkedList<Interval>> cacheComputer(String hostname) {
        MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        String key = "computer_" + hostname;
        UsageModel[] data = getByComputer(hostname);
        Map<String, LinkedList<Interval>> map = DatastoreToBlobstoreConverter.convertToIntervalsByUser(data);
        memcacheService.put(key, map, Expiration.byDeltaSeconds(60*15)); // 15 minutes
        return map;
    }

    @Nonnull public static UsageModel[] getByComputerInDay(String hostname, long date) {
        long[] timeIntervalDay = getTimeIntervalDay(date);
        return getByComputerInRange(hostname, timeIntervalDay[0], timeIntervalDay[1]);
    }

    /**
     * Calculates the start and end time for the given date.
     * @param time the time since epoch in UTC/GMT for the
     *             date to calculate the start and end times
     * @return an array of two longs, the first element being the
     * start of the date and the second element being the end of the date
     */
    @Nonnull public static long[] getTimeIntervalDay(long time) {
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

    /**
     * Returns all usage models belonging to a particular computer in a given range of time
     * @param hostname the hostname for the computer to filter by
     * @param start the start time (time since epoch in milliseconds) inclusive
     * @param end the end time (time since epoch in milliseconds) inclusive
     * @return the set of data entries that satisfy the given constraints
     */
    @Nonnull public static UsageModel[] getByComputerInRange(String hostname, long start, long end) {
        FilterPredicate filter1 = new FilterPredicate(KEY_HOSTNAME, EQUAL, hostname);
        FilterPredicate filter2 = new FilterPredicate(KEY_TIMESTAMP, LESS_THAN_OR_EQUAL, end);
        FilterPredicate filter3 = new FilterPredicate(KEY_TIMESTAMP, GREATER_THAN_OR_EQUAL, start);
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
     * Deletes the given usage models one at a time.  This doesn't
     * do the deletion as a transaction because that would
     * be too much for Google App Engine
     * @param usages the UsageModels to delete.  Realistically,
     *               only the ID's are needed to be filled
     */
    public static void delete(@Nonnull UsageModel... usages) {
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
    @Nullable public static UsageModel[] put(@Nullable UsageModel[] usages) {
        if (usages == null) return null;

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        for (UsageModel usage : usages) {
            usage.timestamp = System.currentTimeMillis();
            Key key = datastoreService.put(toEntity(usage));
            usage.id = key.getId();
        }

        return usages;
    }

    /**
     * Converts a database entity into a {@link UsageModel}, filling in all the important bits.
     * @param usage the database compatible representation
     * @return the data point as a simple POJO
     */
    @Nonnull private static UsageModel fromEntity(@Nonnull Entity entity) {
        UsageModel model = new UsageModel();
        model.timestamp = (long) entity.getProperty(KEY_TIMESTAMP);
        model.username = (String) entity.getProperty(KEY_USER);
        model.hostname = (String) entity.getProperty(KEY_HOSTNAME);
        model.isRemote = (boolean) entity.getProperty(KEY_IS_REMOTE);

        Key key = entity.getKey();
        model.id = key == null ? null : key.getId();
        return model;
    }

    /**
     * Converts a {@link UsageModel} into a database entity, filling in all the important bits.
     * @param usage the data point to convert into a Database compatible representation
     * @return the database compatible representation
     */
    @Nonnull private static Entity toEntity(@Nonnull UsageModel usage) {
        Entity task = usage.id == null ? new Entity(KIND) : new Entity(KIND, usage.id);
        task.setProperty(KEY_TIMESTAMP, usage.timestamp);
        task.setProperty(KEY_USER, usage.username);
        task.setProperty(KEY_HOSTNAME, usage.hostname);
        task.setProperty(KEY_IS_REMOTE, usage.isRemote);
        return task;
    }
}
