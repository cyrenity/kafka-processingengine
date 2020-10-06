package smsgwapp;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import smsgwapp.serde.OriginatingMessageDeserializer;
import smsgwapp.serde.OriginatingMessageSerializer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public final class MessageStreamProcessor {
    private final String brokers;
    public MessageStreamProcessor(String brokers) {
        super();
        this.brokers = brokers;
    }
    public final void process() {

        // Custom SerDe [Serializer + Deserializer]
        OriginatingMessageSerializer serializer = new OriginatingMessageSerializer();
        OriginatingMessageDeserializer deserializer = new OriginatingMessageDeserializer();
        Serde<OriginatingMessage> omSerde = Serdes.serdeFrom(serializer, deserializer);

        // Stream DSL
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, OriginatingMessage> initialStream = streamsBuilder.stream(Constants.getOriginatingMessageTopic(), Consumed.with(Serdes.String(), omSerde));

        KStream<String, OriginatingMessage> test1 = initialStream.flatMapValues(
                new ValueMapper<OriginatingMessage, Iterable<? extends OriginatingMessage>>() {
                    @Override
                    public Iterable<? extends OriginatingMessage> apply(OriginatingMessage value) {
                        List<OriginatingMessage> list1 = new ArrayList<>();
                        if (value.getSmscId().equals(Constants.esmes.MOBILINKECP.toString())) {
                            System.out.println("Discarding this message!");
                        } else {
                            list1.add(value);
                        }

                        return list1;
                        //return Arrays.asList(value.getMessageContent().split("\\s+"));
                    }
                }
        );
        test1.foreach((k, v) -> System.out.println(k + " -> " + v));

        // KStream -> KStream[] branch example
        // First we define predicates for branching
        Predicate <String, OriginatingMessage> ecpPredicate = new Predicate<String, OriginatingMessage>() {
            @Override
            public boolean test(String key, OriginatingMessage originatingMessageObject) {
                boolean result = false;
                if (originatingMessageObject.getShortCode().equals("8300")) {
                    result = true;
                }
                return result;
            }
        };

        Predicate <String, OriginatingMessage> cnicTrackPredicate = new Predicate<String, OriginatingMessage>() {
            @Override
            public boolean test(String key, OriginatingMessage originatingMessageObject) {
                boolean result = false;
                if (originatingMessageObject.getShortCode().equals("8400")) {
                    result = true;
                }
                return result;
            }
        };

        KStream<String, OriginatingMessage>[] branches = initialStream.branch(ecpPredicate, cnicTrackPredicate, (key, value) -> true);
        branches[0].foreach((k, v) -> System.out.println(k + " -> " + v.getMessageContent() +  " "+ v.getMessageTo() + " "+ v.getSmscId()));

        // Build stream topology and start the stream processing engine!
        Topology topology = streamsBuilder.build();
        Properties props = new Properties();
        props.put("bootstrap.servers", this.brokers);
        props.put("application.id", "smsgwapp");
        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
    }
    public static void main(String[] args) {
        (new MessageStreamProcessor("localhost:9092")).process();
    }
}