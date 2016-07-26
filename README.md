Playing with kafka
==================

This implies that you are using `0.10.0.0`. Otherwise, some things will be different.

Reference
---------

[kafka](http://kafka.apache.org)
[kafka docs](http://kafka.apache.org/documentation.html)
[kafka javadoc](http://kafka.apache.org/0100/javadoc)

Setup
-----

1. Install Kafka

    `brew install kafka`
    
    This will install zookeeper as well
    
2. Start zookeeper server

    `zkServer start`
    
    The config for zookeeper is at `/usr/local/etc/zookeeper/zoo.cfg`
    
3. Start the kafka brokers

    `kafka-server-start ./resources/config/server-{0,1,2}.properties`
    
    This will be run for each of the 3 servers.

4. Create some topics

    `kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test`
    
    This will create the topic `test`. Read to documentation for implications of replication factor and partitions.

    You can view the active topics with this command:

    `kafka-topics --list --zookeeper localhost:2181`
