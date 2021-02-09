package br.com.luisergio.learningkafka.tutorial1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerDemoThread
{

    public static void main(String[] args) {
        new ConsumerDemoThread().run();
    }

    private ConsumerDemoThread(){

    }

    private void run(){
        Logger logger = LoggerFactory.getLogger(ConsumerDemoThread.class);

        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "my-sixth-application";
        String topic = "first_topic";

        //latch for dealing with multiple Threads
        CountDownLatch latch = new CountDownLatch(1);

        //create consumer runnable
        logger.info("Creating the consumer thread");
        Runnable myConsumerRunnable = new ConsumerThread(
                bootstrapServers,
                groupId,
                topic,
                latch
        );

        //start the thread
        Thread myThread = new Thread(myConsumerRunnable);
        myThread.start();

        //Add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    logger.info("Caught shutdown hook");
                    ((ConsumerThread)myConsumerRunnable).shutdown();
                    try {
                        latch.await();
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    logger.info("Application has exited");
                }
        ));

        try {
            latch.await();
        }
        catch (InterruptedException e){
            logger.error("Application got interrupted", e);
        }
        finally {
            logger.info("Application is closing");
        }



    }

    public class ConsumerThread implements Runnable {

        private CountDownLatch latch;
        private KafkaConsumer<String,String> consumer;
        private Logger logger = LoggerFactory.getLogger(ConsumerThread.class);

        public ConsumerThread(String bootstrapServers,
                              String groupId,
                              String topic,
                              CountDownLatch latch){
            this.latch = latch;



            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            //earliest / latest / none
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");


            // Create Consumer
            this.consumer = new KafkaConsumer<String, String>(properties);

            // Subscribe Consumer to our topic(s)
            //consumer.subscribe(Collections.singleton(topic));
            this.consumer.subscribe(Arrays.asList(topic));
        }

        @Override
        public void run(){
            // Poll for new data
            try {
                while(true)
                {
                    ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(100));

                    for(ConsumerRecord<String,String> record : records)
                    {
                        logger.info("Key: " + record.key() + " Value: " + record.value());
                        logger.info("Partition: " + record.partition() + " Offset: " + record.offset());
                    }
                }
            }
            catch (WakeupException e){
                this.logger.info("Received shutdown signal!");
            }finally {
                this.consumer.close();
                //Tell our main code we're done with the consumer
                this.latch.countDown();
            }

        }

        public void shutdown(){
            //The wakeup method is a special method to interrup consumer.poll()
            //It will throw the exception WakeUpException
            this.consumer.wakeup();
        }
    }
}
