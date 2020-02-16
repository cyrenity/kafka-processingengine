package smsgwapp;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maxmind.geoip2.model.CityResponse;
import smsgwapp.extractors.GeoIPService;
import smsgwapp.extractors.OpenExchangeService;
import org.apache.kafka.clients.producer.KafkaProducer;
import java.io.IOException;

public final class Enricher implements Producer {

  private final KafkaProducer<String, String> producer;
  private final String validMessages;
  private final String invalidMessages;
  private static final ObjectMapper MAPPER = new ObjectMapper();

  public Enricher(String servers, String validMessages, String invalidMessages) {
    this.producer = new KafkaProducer<>(Producer.createConfig(servers));
    this.validMessages = validMessages;
    this.invalidMessages = invalidMessages;
  }

  @Override
  public void process(String message) {
    try {
        final JsonNode root = MAPPER.readTree(message);
        final JsonNode ipAddressNode = root.path("customer").path("ipAddress");
        final JsonNode currencyNode = root.path("currency");

        if (ipAddressNode.isMissingNode()) {
            Producer.write(this.producer, this.invalidMessages, "{\"error\": \"customer.ipAddress is missing\"}");
        } else if (currencyNode.isMissingNode()) {
            Producer.write(this.producer, this.invalidMessages, "{\"error\": \"currency data is missing\"}");
        } else {
            final String ipAddress = ipAddressNode.textValue();
            final CityResponse location = new GeoIPService().getLocation(ipAddress);

            ((ObjectNode) root).with("customer").put("country", location.getCountry().getName());
            ((ObjectNode) root).with("customer").put("city", location.getCity().getName());

            final OpenExchangeService oes = new OpenExchangeService();

            ((ObjectNode) root).with("currency").put("rate", oes.getPrice("BTC"));

            Producer.write(this.producer, this.validMessages, MAPPER.writeValueAsString(root));
        }
    } catch (IOException e) {
        Producer.write(this.producer, this.invalidMessages, "{\"error\": \""
          + e.getClass().getSimpleName() + ": " + e.getMessage() + "\"}");
    }
  }
}