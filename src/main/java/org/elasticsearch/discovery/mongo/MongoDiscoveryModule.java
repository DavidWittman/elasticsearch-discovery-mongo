package org.elasticsearch.discovery.mongo;

import org.elasticsearch.discovery.Discovery;
import org.elasticsearch.discovery.zen.ZenDiscoveryModule;

/**
 * A wrapper to bind the Mongo discovery as a singleton to the available discovery modules.
 */
public class MongoDiscoveryModule extends ZenDiscoveryModule {
    @Override
    protected void bindDiscovery() {
        bind(Discovery.class).to(MongoDiscovery.class).asEagerSingleton();
    }
}
