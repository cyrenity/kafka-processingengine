package smsgwapp.extractors;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class GeoIPService {
  private static final String MAXMINDDB = "/home/mustafa/Dev/smsgwapp-java/GeoLite2-City.mmdb";
  File database = new File(MAXMINDDB);
  DatabaseReader reader = new DatabaseReader.Builder(database).build();

  public GeoIPService() throws IOException {
  }

  public CityResponse getLocation(String ipAddress) {
    try {
      InetAddress ipAddress1 = InetAddress.getByName(ipAddress);
      CityResponse response = reader.city(ipAddress1);

      return response;

    } catch (IOException | GeoIp2Exception ex) {
      Logger.getLogger(GeoIPService.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }
}