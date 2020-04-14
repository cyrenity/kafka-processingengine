package smsgwapp.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import smsgwapp.Constants;
import java.util.Map;
import org.apache.kafka.common.serialization.Serializer;
import smsgwapp.HealthCheck;


public final class HealthCheckSerializer implements Serializer<HealthCheck> {
    @Override
    public byte[] serialize(String topic, HealthCheck data) {
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