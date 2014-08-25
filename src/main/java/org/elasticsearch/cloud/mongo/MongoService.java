package org.elasticsearch.cloud.mongo;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.discovery.DiscoveryException;
import org.elasticsearch.ElasticsearchException;

/**
 * This service establishes the connection to MongoDB and finds other nodes in the cluster.
 */
public class MongoService extends AbstractLifecycleComponent<MongoService> {
    @Inject
    public MongoService(Settings settings, SettingsFilter settingsFilter) {
        super(settings);
        settingsFilter.addFilter(new MongoSettingsFilter());
    }

    public synchronized DBCollection getCollection() {
        // TODO(dw): Implement authentication
        // TODO(dw): Accept collection name as an arg?
        String connectString = componentSettings.get("connect_string", "mongodb://localhost/es");
        String collectionName = componentSettings.get("collection", "clusters");
        DBCollection coll = null;

        MongoClientURI uri = new MongoClientURI(connectString);

        try {
            logger.debug("Connecting to MongoDB at {}", connectString);
            MongoClient mongo = new MongoClient(uri);
            coll = mongo.getDB(uri.getDatabase()).getCollection(collectionName);
        } catch (Exception e) {
            logger.warn("Error connecting to MongoDB: {} : {}", e.getClass().getName(), e.getMessage());
            throw new DiscoveryException("Unable to start MongoDB discovery service", e);
        }

        return coll;
    }

    @Override
    protected void doStart() throws ElasticsearchException {
    }

    @Override
    protected void doStop() throws ElasticsearchException {
    }

    @Override
    protected void doClose() throws ElasticsearchException {
    }
}
