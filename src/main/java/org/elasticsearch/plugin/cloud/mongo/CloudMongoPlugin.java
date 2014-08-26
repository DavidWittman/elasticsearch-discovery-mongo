package org.elasticsearch.plugin.cloud.mongo;

import org.elasticsearch.cloud.mongo.MongoModule;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.AbstractPlugin;

import java.util.Collection;

/**
 *
 */
public class CloudMongoPlugin extends AbstractPlugin {

    private final Settings settings;

    public CloudMongoPlugin(Settings settings) {
        this.settings = settings;
    }

    @Override
    public String name() {
        return "cloud-mongo";
    }

    @Override
    public String description() {
        return "Cloud MongoDB Plugin";
    }

    @Override
    public Collection<Class<? extends Module>> modules() {
        Collection<Class<? extends Module>> modules = Lists.newArrayList();
        if (settings.getAsBoolean("cloud.enabled", true)) {
            modules.add(MongoModule.class);
        }
        return modules;
    }

    @Override
    public Collection<Class<? extends LifecycleComponent>> services() {
        Collection<Class<? extends LifecycleComponent>> services = Lists.newArrayList();
        if (settings.getAsBoolean("cloud.enabled", true)) {
//            services.add(MongoServiceImpl.class);
        }
        return services;
    }

}
