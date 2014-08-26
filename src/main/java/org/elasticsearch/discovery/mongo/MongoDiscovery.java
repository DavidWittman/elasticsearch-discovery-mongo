package org.elasticsearch.discovery.mongo;

import org.elasticsearch.Version;
import org.elasticsearch.mongo.MongoService;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.node.DiscoveryNodeService;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.network.NetworkService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.discovery.DiscoverySettings;
import org.elasticsearch.discovery.zen.ZenDiscovery;
import org.elasticsearch.discovery.zen.ping.ZenPing;
import org.elasticsearch.discovery.zen.ping.ZenPingService;
import org.elasticsearch.discovery.zen.ping.unicast.UnicastZenPing;
import org.elasticsearch.node.settings.NodeSettingsService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

/**
 *
 */
public class MongoDiscovery extends ZenDiscovery {

    @Inject
    public MongoDiscovery(Settings settings, ClusterName clusterName, ThreadPool threadPool, TransportService transportService,
                        ClusterService clusterService, NodeSettingsService nodeSettingsService, ZenPingService pingService,
                        DiscoveryNodeService discoveryNodeService, MongoService mongoService,
                        DiscoverySettings discoverySettings) {
        super(settings, clusterName, threadPool, transportService, clusterService, nodeSettingsService,
                discoveryNodeService, pingService, Version.CURRENT, discoverySettings);
        if (settings.getAsBoolean("discovery.mongo.enabled", true)) {
            ImmutableList<? extends ZenPing> zenPings = pingService.zenPings();
            UnicastZenPing unicastZenPing = null;
            for (ZenPing zenPing : zenPings) {
                if (zenPing instanceof UnicastZenPing) {
                    unicastZenPing = (UnicastZenPing) zenPing;
                    break;
                }
            }

            if (unicastZenPing != null) {
                logger.info("Adding MongoUnicastHostsProvider to zen pings");
                // update the unicast zen ping to add Mongo provider
                // and, while we are at it, use only it and not the multicast for example
                unicastZenPing.addHostsProvider(new MongoUnicastHostsProvider(settings, transportService, mongoService));
                pingService.zenPings(ImmutableList.of(unicastZenPing));
            } else {
                logger.warn("failed to apply mongo unicast discovery, no unicast ping found");
            }
        }
    }
}
