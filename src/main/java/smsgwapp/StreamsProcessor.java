package smsgwapp;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import smsgwapp.serde.HealthCheckDeserializer;
import smsgwapp.serde.HealthCheckSerializer;

import java.util.Properties;

public final class StreamsProcessor {
    private final String brokers;
    public StreamsProcessor(String brokers) {
        super();
        this.brokers = brokers;
    }
    public final void process() {

        HealthCheckSerializer serializer = new HealthCheckSerializer();
        HealthCheckDeserializer deserializer = new HealthCheckDeserializer();


        Serde<HealthCheck> customSerde = Serdes.serdeFrom(serializer, deserializer);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, HealthCheck> initialStream = streamsBuilder.stream(Constants.getHealthChecksTopic(), Consumed.with(Serdes.String(), customSerde));

        initialStream.foreach((k, v) -> System.out.println(v.getIpAddress()));

        Topology topology = streamsBuilder.build();
        Properties props = new Properties();
        props.put("bootstrap.servers", this.brokers);
        props.put("application.id", "kioto");
        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
    }
    public static void main(String[] args) {
        (new StreamsProcessor("localhost:9092")).process();
    }
}