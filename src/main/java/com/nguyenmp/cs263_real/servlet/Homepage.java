package com.nguyenmp.cs263_real.servlet;

import com.nguyenmp.cs263_real.model.UsageModel;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.*;

/**
 * Utility class for the homepage.jsp class
 */
public class Homepage {

    /** for each hostname, creates a set of usernames logged into each computer */
    public static Map<String, Set<String>> countUsagesByComputer(UsageModel[] usages) {
        Map<String, Set<String>> currentUsage = new HashMap<>();
        for (UsageModel usage : usages) {
            String hostname = usage.hostname;

            // Either get the set of users or initialize a new set
            Set<String> users;
            if (!currentUsage.containsKey(hostname)) {
                users = new HashSet<>();
                currentUsage.put(hostname, users);
            } else {
                users = currentUsage.get(hostname);
            }

            // Add this user to the set
            users.add(usage.username);
        }

        return currentUsage;
    }

    /** Counts the number of people logged into each computer */
    public static Count[] count(Map<String, Set<String>> currentUsage) {
        List<Count> counts = new ArrayList<>(currentUsage.keySet().size());
        for (String hostname : currentUsage.keySet()) {
            Count count = new Count();
            count.hostname = hostname;
            count.count = currentUsage.get(hostname).size();
            counts.add(count);
        }

        return counts.toArray(new Count[counts.size()]);
    }

    public static class Count implements Serializable, Comparable<Count> {
        public String hostname;
        public int count;

        @Override
        public int compareTo(@Nonnull Count o) {
            return this.count - o.count;
        }
    }
}
