/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract consists of methods that extract information from a list of tweets.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
	public static Timespan getTimespan(List<Tweet> tweets) {
	    if (tweets == null) {
	        throw new IllegalArgumentException("Tweet list must not be null");
	    }

	    if (tweets.isEmpty()) {
	        // Return a valid Timespan with same start and end if no tweets
	        Instant now = Instant.now();
	        return new Timespan(now, now);
	    }

	    // Find earliest and latest timestamp
	    Instant start = tweets.get(0).getTimestamp();
	    Instant end = start;

	    for (Tweet tweet : tweets) {
	        Instant time = tweet.getTimestamp();
	        if (time.isBefore(start)) start = time;
	        if (time.isAfter(end)) end = time;
	    }

	    return new Timespan(start, end);
	}

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec). The username-mention cannot
     *         be immediately preceded or followed by any character valid in a
     *         Twitter username. For this reason, an email address like
     *         bitdiddle@mit.edu does NOT contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> mentionedUsers = new HashSet<>();
        if (tweets == null) return mentionedUsers;

        // Pattern: @ followed by letters, digits, or underscores,
        // not immediately preceded or followed by those characters.
        Pattern mentionPattern = Pattern.compile("(?i)(?<![A-Za-z0-9_])@([A-Za-z0-9_]+)(?![A-Za-z0-9_])");

        for (Tweet tweet : tweets) {
            String text = tweet.getText();
            Matcher matcher = mentionPattern.matcher(text);

            while (matcher.find()) {
                String username = matcher.group(1).toLowerCase(Locale.ROOT);
                mentionedUsers.add(username);
            }
        }

        return mentionedUsers;
    }
}