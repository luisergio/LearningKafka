package br.com.luisergio.learningkafka.tutorial1;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.SneakyThrows;

import java.util.Properties;

public class ProducerDemoWithKeys {

    @SneakyThrows
    public static void main(String[] args) {

        final Logger logger = LoggerFactory.getLogger(ProducerDemoWithKeys.class);

        //Create Producer Properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //Create Producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        for(int i=0; i<10; i++)
        {
            //Create Producer Record
            String topic = "first_topic";
            String value = "hello world" + i;
            String key = "id_" + i;

            final ProducerRecord<String, String> record =
                    new ProducerRecord<String, String>(topic, key, value);

            logger.info("Key Information\n" +
                    "Key: " + key); //log de key

            //Send Data - asynchronous
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e)
                {
                    //executes every times a record is successfully sent or an exception is throw
                    if(e == null)
                    {
                        logger.info(
                                "Partition Information\n" +
                                "Partition:" + recordMetadata.partition() + "\n");
                    }
                    else
                    {
                        logger.error("Error while producing", e);
                    }
                }
            }).get(); //Block the send to make in synchronous -- Don't do it in Production
        }

        producer.flush();

        producer.close();

    }

}
