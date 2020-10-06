package smsgwapp.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.common.serialization.Serializer;
import smsgwapp.Constants;
import smsgwapp.OriginatingMessage;

import java.util.Map;


public final class OriginatingMessageSerializer implements Serializer<OriginatingMessage> {
    @Override
    public byte[] serialize(String topic, OriginatingMessage data) {
        if (data == null) {
            return null;
        }
        try {
            return Constants.getJsonMapper().writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public void close() {}
    @Override
    public void configure(Map configs, boolean isKey) {}
}