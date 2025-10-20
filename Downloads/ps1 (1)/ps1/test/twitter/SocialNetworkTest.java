/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphNoMentions() {
        Tweet tweet = new Tweet(1, "alice", "Hello world", Instant.now());
        List<Tweet> tweets = List.of(tweet);

        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("expected empty graph for no mentions", graph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphSingleMention() {
        Tweet tweet = new Tweet(1, "bob", "Hey @alice!", Instant.now());
        List<Tweet> tweets = List.of(tweet);

        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("bob should follow alice",
            graph.containsKey("bob") && graph.get("bob").contains("alice"));
    }

    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        Tweet tweet = new Tweet(1, "bob", "Hi @alice @charlie", Instant.now());
        List<Tweet> tweets = List.of(tweet);

        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);

        assertEquals(Set.of("alice", "charlie"), graph.get("bob"));
    }

    @Test
    public void testGuessFollowsGraphMultipleTweetsSameUser() {
        Tweet t1 = new Tweet(1, "bob", "Hi @alice", Instant.now());
        Tweet t2 = new Tweet(2, "bob", "Hello again @charlie", Instant.now());
        List<Tweet> tweets = List.of(t1, t2);

        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("bob should follow both alice and charlie",
            graph.get("bob").containsAll(Set.of("alice", "charlie")));
    }

    @Test
    public void testGuessFollowsGraphIgnoresSelfMention() {
        Tweet tweet = new Tweet(1, "alice", "I love myself @alice", Instant.now());
        List<Tweet> tweets = List.of(tweet);

        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);

        assertFalse("alice should not follow herself",
            graph.getOrDefault("alice", Set.of()).contains("alice"));
    }

    @Test
    public void testInfluencersEmptyGraph() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertTrue("expected empty influencer list", influencers.isEmpty());
    }

    @Test
    public void testInfluencersSingleUserNoFollowers() {
        Map<String, Set<String>> followsGraph = Map.of("alice", Set.of());
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertTrue("no one follows anyone, so influencers list should be empty", influencers.isEmpty());
    }

    @Test
    public void testInfluencersSingleFollower() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("bob", Set.of("alice"));

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("alice should be most influential", "alice", influencers.get(0));
    }

    @Test
    public void testInfluencersMultipleFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("bob", Set.of("alice"));
        followsGraph.put("charlie", Set.of("alice"));
        followsGraph.put("david", Set.of("bob"));

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("alice should have the most followers", "alice", influencers.get(0));
        assertTrue("bob should also appear in list", influencers.contains("bob"));
    }

    @Test
    public void testInfluencersTiedCounts() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("bob", Set.of("alice"));
        followsGraph.put("charlie", Set.of("david"));

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        // both alice and david have one follower each
        assertTrue("list should contain both alice and david",
            influencers.containsAll(Set.of("alice", "david")));
    }

    
    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

}