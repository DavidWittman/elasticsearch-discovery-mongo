package org.elasticsearch.cloud.mongo;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.SettingsFilter;

/**
 * Removes settings from the settings filter so that they are not processed by other plugins.
 */
public class MongoSettingsFilter implements SettingsFilter.Filter {
    @Override
    public void filter(final ImmutableSettings.Builder settings) {
        settings.remove("cloud.mongo.connect_string");
        settings.remove("cloud.mongo.collection");
    }
}
