package smsgwapp.serde;

import org.apache.kafka.common.serialization.Deserializer;
import smsgwapp.Constants;
import smsgwapp.OriginatingMessage;

import java.io.IOException;
import java.util.Map;


public final class OriginatingMessageDeserializer implements Deserializer<OriginatingMessage> {
    @Override
    public OriginatingMessage deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return Constants.getJsonMapper().readValue(data, OriginatingMessage.class);
        } catch (IOException e) {
            return null;
        }
    }
    @Override
    public void close() {}
    @Override
    public void configure(Map configs, boolean isKey) {}
}