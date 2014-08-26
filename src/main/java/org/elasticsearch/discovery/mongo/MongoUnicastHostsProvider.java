package org.elasticsearch.discovery.mongo;

import java.net.InetAddress;
import java.util.List;
import java.util.Map.Entry;
import java.util.Iterator;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.elasticsearch.mongo.MongoService;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.discovery.zen.ping.unicast.UnicastHostsProvider;
import org.elasticsearch.discovery.DiscoveryException;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.transport.TransportService;

/**
 * Is used to register this node and create a list of available nodes in the cluster.
 */
public class MongoUnicastHostsProvider extends AbstractComponent implements UnicastHostsProvider {
    private final TransportService transportService;
    private final MongoService mongoService;

    @Inject
    public MongoUnicastHostsProvider(final Settings settings, final TransportService transportService, final MongoService mongoService) {
        super(settings);
        this.mongoService = mongoService;
        this.transportService = transportService;
    }

    @Override
    public List<DiscoveryNode> buildDynamicNodes() {
        // TODO(dw): Implement caching?
        List<DiscoveryNode> discoNodes = Lists.newArrayList();
        String clusterId = componentSettings.get("cluster_id");

        if (clusterId == null) {
            throw new ElasticsearchIllegalArgumentException("discovery.mongo.cluster_id must be set in the elasticsearch config");
        }
        logger.info("Building list of dynamic discovery nodes from Mongo using ID {}", clusterId);
                
        DBCollection coll = mongoService.getCollection();
        DBObject doc = coll.findOne(new BasicDBObject("cluster", clusterId));

        if (doc == null) {
            logger.error("Cluster matching ID '{}' not found", clusterId);
            return null;
        }

        BasicDBList nodes = (BasicDBList) doc.get("nodes");

        // TODO(dw): Filter out current node from list
        for (Iterator<Object> it = nodes.iterator(); it.hasNext(); ) {
            String node = (String) it.next();

            try {
                TransportAddress[] addresses = transportService.addressesFromString(node);
                logger.trace("adding node {}, transport_address {}", node, addresses[0]);
                discoNodes.add(new DiscoveryNode("#mongo-" + node, addresses[0], Version.CURRENT));
            } catch (Exception e) {
                logger.warn("failed to add {}: {}", node, e);
            }
        }

        logger.debug("using dynamic discovery nodes {}", discoNodes);
        return discoNodes;
    }
}
