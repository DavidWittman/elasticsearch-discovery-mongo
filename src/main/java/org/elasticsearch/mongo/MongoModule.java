package org.elasticsearch.mongo;

import org.elasticsearch.common.inject.AbstractModule;

/**
 * Binds the Mongo service as a module that is available to other modules, like the MongoDiscovery.
 */
public class MongoModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MongoService.class).asEagerSingleton();
    }
}
