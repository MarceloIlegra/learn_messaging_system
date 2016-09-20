#!/bin/bash
# My first script

echo "Starting ZK..."
./kafka_2.11-0.9.0.0/bin/zookeeper-server-start.sh -daemon ./kafka_2.11-0.9.0.0/config/zookeeper.properties &

echo "Starting instance 1 of broker..."
./kafka_2.11-0.9.0.0/bin/kafka-server-start.sh -daemon ./kafka_2.11-0.9.0.0/config/server-1.properties &

echo "Starting instance 2 of broker..."
./kafka_2.11-0.9.0.0/bin/kafka-server-start.sh -daemon ./kafka_2.11-0.9.0.0/config/server-2.properties &

echo "Starting instance 3 of broker..."
./kafka_2.11-0.9.0.0/bin/kafka-server-start.sh -daemon ./kafka_2.11-0.9.0.0/config/server-3.properties &


