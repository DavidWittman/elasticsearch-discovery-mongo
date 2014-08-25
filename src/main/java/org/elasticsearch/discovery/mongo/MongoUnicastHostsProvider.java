package org.elasticsearch.discovery.mongo;

import java.net.InetAddress;
import java.util.List;
import java.util.Map.Entry;
import java.util.Iterator;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.elasticsearch.cloud.mongo.MongoService;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.discovery.zen.ping.unicast.UnicastHostsProvider;
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
        // TODO(dw): Fetch clusterId from the settings
        String clusterId = "deadbeef42";
        logger.info("Building list of dynamic discovery nodes from Mongo");
        List<DiscoveryNode> discoNodes = Lists.newArrayList();
                
        DBCollection coll = mongoService.getCollection();
        DBObject doc = coll.findOne(new BasicDBObject("cluster", clusterId));
        BasicDBList nodes = (BasicDBList) doc.get("nodes");

        // TODO(dw): Filter out current node from list
        for (Iterator<Object> it = nodes.iterator(); it.hasNext(); ) {
            String node = (String) it.next();

            try {
                TransportAddress[] addresses = transportService.addressesFromString(node);
                logger.trace("adding node {}, transport_address {}", node, addresses[0]);
                discoNodes.add(new DiscoveryNode("#cloud-" + node, addresses[0], Version.CURRENT));
            } catch (Exception e) {
                logger.warn("failed to add {}: {}", node, e);
            }
        }

        logger.debug("using dynamic discovery nodes {}", discoNodes);
        return discoNodes;
    }
}
