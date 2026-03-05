#!/bin/bash
echo "Creating Kafka topics..."
docker exec kafka /opt/kafka/bin/kafka-topics.sh --create --topic meeting-events --partitions 5 --replication-factor 1 --bootstrap-server localhost:9092
docker exec kafka /opt/kafka/bin/kafka-topics.sh --create --topic meeting-events-dlq --partitions 5 --replication-factor 1 --bootstrap-server localhost:9092
echo "Executing MySQL script..."
docker exec -i mysql mysql -uroot -proot meeting_db < "$(dirname "$0")/src/main/resources/db/migration/0000_init.sql"

echo "Local setup complete."