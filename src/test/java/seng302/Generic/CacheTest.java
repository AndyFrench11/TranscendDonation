package seng302.Generic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CacheTest {

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }


    @Test
    public void purgeEntriesOlderThan_oldEntiresCached_oldEntriesPurged() throws InterruptedException{
        Cache cache = new Cache("");
        cache.put("key","value");
        sleep(20);
        cache.purgeEntriesOlderThan(Duration.ofMillis(20));
        assertFalse(cache.contains("key"));
    }

    @Test
    public void purgeEntriesOlderThan_zeroOldEntriesCached_entriesUnchanged() throws InterruptedException{
        Cache cache = new Cache("");
        cache.put("key","value1");
        cache.purgeEntriesOlderThan(Duration.ofMillis(20));
        assertTrue(cache.contains("key"));
    }

    @Test
    public void testSavingDataToCacheThenImportingAgain() {
        Cache cache = IO.importCache("./doc/examples/testCache.json");
        cache.put("test", "test data");
        cache.save();
        Cache testCache = IO.importCache("./doc/examples/testCache.json");
        assertEquals("test data", testCache.get("test"));
        testCache.clear();
        testCache.save();
    }

    @Test
    public void testAddingDataToCache() {
        Cache cache = new Cache("");
        cache.put("test", "test data");
        assertEquals("test data", cache.get("test"));

    }
}