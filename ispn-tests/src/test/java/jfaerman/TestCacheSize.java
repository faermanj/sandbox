package jfaerman;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestCacheSize {
	int sizeBefore = -1;
	int sizeAfter = -1;
	
	@Listener
	public class CacheSizeListener {
		@CacheEntryCreated
		public void onEntryCreated(CacheEntryCreatedEvent<Object, Object> event){
			if(event.isPre())
				sizeBefore = event.getCache().size();
			else
				sizeAfter = event.getCache().size();
		}
	}
	
	@Test
	public void testOnEntryCreated() {
		DefaultCacheManager manager = new DefaultCacheManager();
		Cache<Object, Object> cache = manager.getCache();
		cache.addListener(new CacheSizeListener());
		cache.put(new Object(), new Object());
		assertEquals(sizeBefore + 1, sizeAfter);

	}

}
