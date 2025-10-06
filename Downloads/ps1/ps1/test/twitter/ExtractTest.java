/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * Testing strategy
     * 
     * Partition the input as follows for getTimespan():
     *  - number of tweets: 0, 1, >1
     *  - tweets in chronological order, reverse order, same timestamp
     * 
     * For getMentionedUsers():
     *  - no mentions
     *  - one mention
     *  - multiple mentions
     *  - mentions with uppercase/lowercase (case insensitivity)
     *  - mention inside email address (should NOT count)
     *  - multiple tweets with overlapping mentions
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "charlie", "hey @Alyssa, check this out!", d3);
    private static final Tweet tweet4 = new Tweet(4, "david", "email me at test@mit.edu, not a mention", d1);
    private static final Tweet tweet5 = new Tweet(5, "emma", "Multiple mentions: @Bob @bob @CAROL", d2);

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // ---------- getTimespan() tests ----------

    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));

        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }

    @Test
    public void testGetTimespanOneTweet() {
        Timespan timespan = Extract.getTimespan(Collections.singletonList(tweet1));
        assertEquals("expected same start and end", d1, timespan.getStart());
        assertEquals("expected same start and end", d1, timespan.getEnd());
    }

    @Test
    public void testGetTimespanMultipleOutOfOrder() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet3, tweet1, tweet2));
        assertEquals("expected earliest start", d1, timespan.getStart());
        assertEquals("expected latest end", d3, timespan.getEnd());
    }

    @Test
    public void testGetTimespanEmptyList() {
        Timespan timespan = Extract.getTimespan(Collections.emptyList());
        assertNotNull("start should not be null", timespan.getStart());
        assertNotNull("end should not be null", timespan.getEnd());
    }

    // ---------- getMentionedUsers() tests ----------

    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2));
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersSingleMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Collections.singletonList(tweet3));
        assertTrue("expected to contain alyssa", mentionedUsers.contains("alyssa"));
        assertEquals("expected one user", 1, mentionedUsers.size());
    }

    @Test
    public void testGetMentionedUsersIgnoresEmail() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Collections.singletonList(tweet4));
        assertTrue("expected no mentions", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersCaseInsensitive() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Collections.singletonList(tweet5));
        assertTrue("expected bob", mentionedUsers.contains("bob"));
        assertTrue("expected carol", mentionedUsers.contains("carol"));
        assertEquals("expected two mentions", 2, mentionedUsers.size());
    }

    @Test
    public void testGetMentionedUsersMultipleTweetsCombined() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3, tweet5));
        assertTrue("expected alyssa", mentionedUsers.contains("alyssa"));
        assertTrue("expected bob", mentionedUsers.contains("bob"));
        assertTrue("expected carol", mentionedUsers.contains("carol"));
        assertEquals("expected three mentions", 3, mentionedUsers.size());
    }
}