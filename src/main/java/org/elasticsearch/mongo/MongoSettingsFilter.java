package org.elasticsearch.mongo;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.SettingsFilter;

/**
 * Removes settings from the settings filter so that they are not processed by other plugins.
 */
public class MongoSettingsFilter implements SettingsFilter.Filter {
    @Override
    public void filter(final ImmutableSettings.Builder settings) {
        settings.remove("mongo.connection_string");
        settings.remove("mongo.collection");
    }
}
