# MongoDB Discovery Plugin for Elasticsearch

The MongoDB discovery plugin uses MongoDB for unicast discovery in Elasticsearch.

The configuration value `discovery.mongo.cluster_id` is used for looking up a set of nodes (an array identifed by the 'nodes' key) in a MongoDB collection. Here's an example schema:

``` javascript
> db.clusters.ensureIndex({cluster: 1}, {unique: true})
> db.clusters.findOne()
{
    "_id" : ObjectId("53fbb280da215060eb6b9134"),
    "cluster" : "lolwut",
    "nodes" : [
        "one.example.com",
        "two.example.com",
        "three.example.com"
    ]
}
```

In this case, a cluster identifed by the key 'lolwut' will discover three nodes from MongoDB: one.example.com, two.example.com, and three.example.com.

## Build

```
$ mvn clean package
```

## Installation

```
$ /usr/share/elasticsearch/bin/plugin -url file:/path/to/elasticsearch-discovery-mongo-0.2.zip -install elasticsearch-discovery-mongo
```

## Configuration

In elasticsearch.yml

```
plugin.mandatory: discovery-mongo
discovery.type: mongo
discovery.mongo.cluster_id: ${cluster.name}
mongo:
  connection_string: 'mongodb://user:pass@mongo.example.com/database'
  collection: clusters
```
