package smsgwapp;

import com.github.javafaker.Faker;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import smsgwapp.serde.HealthCheckSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class HealthCheckProducer {
    private final Producer<String, HealthCheck> producer;

    public HealthCheckProducer(String brokers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", HealthCheckSerializer.class);
        producer = new KafkaProducer<>(props);
    }


    @SuppressWarnings("InfiniteLoopStatement")
    public void produce(int ratePerSecond) {
        long waitTimeBetweenIterationsMs = 1000L / (long)ratePerSecond;
        Faker faker = new Faker();
        while(true) {
            HealthCheck fakeHealthCheck =
                    new HealthCheck(
                            "HEALTH_CHECK",
                            faker.address().city(),
                            faker.bothify("??##-??##", true),
                            Constants.machineType.values()[faker.number().numberBetween(0,4)].toString(),
                            Constants.machineStatus.values()[faker.number().numberBetween(0,3)].toString(),
                            faker.date().past(100, TimeUnit.DAYS),
                            faker.number().numberBetween(100L, 0L),
                            faker.internet().ipV4Address()
                    );


            Future<RecordMetadata> futureResult = producer.send( new ProducerRecord<>(
                    Constants.getHealthChecksTopic(), fakeHealthCheck));
            try {
                Thread.sleep(waitTimeBetweenIterationsMs);
                futureResult.get();
            } catch (InterruptedException | ExecutionException e) {
                // deal with the exception
            }
        }
    }
    public static void main(String[] args) {
        new HealthCheckProducer("localhost:9092").produce(2);
    }

}

