package smsgwapp;

import com.github.javafaker.Faker;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import smsgwapp.serde.OriginatingMessageSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class OriginatingMessageProducer {
    private final Producer<String, OriginatingMessage> producer;

    public OriginatingMessageProducer(String brokers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", OriginatingMessageSerializer.class);
        producer = new KafkaProducer<>(props);
    }


    @SuppressWarnings("InfiniteLoopStatement")
    public void produce(int ratePerSecond) {
        long waitTimeBetweenIterationsMs = 1000L / (long) ratePerSecond;
        Faker faker = new Faker();
        while(true) {
            OriginatingMessage fakeOriginatingMessage = new OriginatingMessage(
                    "8300",
                    faker.phoneNumber().cellPhone(),
                    faker.harryPotter().quote(),
                    faker.date().past(100, TimeUnit.DAYS),
                    Constants.esmes.values()[faker.number().numberBetween(0,3)].toString(),
                    faker.internet().ipV4Address()

            );
            Future<RecordMetadata> futureResult = producer.send( new ProducerRecord<>(
                    Constants.getOriginatingMessageTopic(), fakeOriginatingMessage));
            try {
                Thread.sleep(waitTimeBetweenIterationsMs);
                futureResult.get();
            } catch (InterruptedException | ExecutionException e) {
                // deal with the exception
            }
        }
    }
    public static void main(String[] args) {
        new OriginatingMessageProducer("localhost:9092").produce(2);
    }

}

