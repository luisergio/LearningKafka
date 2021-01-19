# Learning Kafka

A project used to study the concepts of Kafka and be able to apply they using Java.<br/>
All the content present here as made using the course made by <a href="https://www.linkedin.com/in/stephanemaarek/">Stephane Maarek</a>.

## The Course
<a href="https://www.udemy.com/course/apache-kafka/">https://www.udemy.com/course/apache-kafka/</a>

## Kafka CLI

Below I will list the most important commands to manage Kafka broker, topics and partitions:

1. Start Zookeeper:
```bash
zookeeper-server-start.bat config/zookeeper.properties
```
	
2. Start Kafka:
```bash
kafka-server-start.bat config/server.properties
```
	
3. Create topic:
```bash
kafka-topics.bat --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 1
```

4. List topics:
```bash
kafka-topics.bat --zookeeper 127.0.0.1:2181 --list
```

5. Describe a topic:
```bash
kafka-topics.bat --zookeeper 127.0.0.1:2181 --topic first_topic --describe
```

6. Produce content for a topic:
```bash
kafka-console-producer.bat --broker-list 127.0.0.1:9092 --topic first_topic
```

7. Produce content for a topic with properties:
```bash
kafka-console-producer.bat --broker-list 127.0.0.1:9092 --topic first_topic --producer-property acks=all
```

8. Consume content from topic:
```bash
kafka-console-consumer.bat --bootstrap-server 127.0.0.1:9092 --topic first_topic
```

9. Consume content from a group on the topic:
```bash
kafka-console-consumer.bat --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-first-application
```

10. List consumer group:
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --list
```

11. Consumer group details (Latency and IP):
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group my-first-application
```

12. Reset Offsets form a consumer group:
```bash
Kafka-consumer-groups --bootstrap-server localhost:9092 --group my-first-application --reset-offsets --to-earliest --execute --topic first_topic
```
