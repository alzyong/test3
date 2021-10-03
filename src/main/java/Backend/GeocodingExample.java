package Backend;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeocodingExample {
    private static final String GEOCODING_RESOURCE = "https://geocode.search.hereapi.com/v1/geocode";
    private static final String API_KEY = "oraWq-tyP-_o-aU4kTwJ4lRMGQYHrDzNgCpzcsrJ_Tc";
    public static String Geocode(String a) throws IOException, InterruptedException {

        ObjectMapper mapper = new ObjectMapper();
        GeocodingExample geocoder = new GeocodingExample();
        int counter = 0;
        String coord = "";
        String response = geocoder.GeocodeSync(a);
        JsonNode responseJsonNode = mapper.readTree(response);

        JsonNode items = responseJsonNode.get("items");
        System.out.println("test: "+a);
        for (JsonNode item : items) {
            JsonNode address = item.get("address");
            String label = address.get("label").asText();
            JsonNode position = item.get("position");
            String lat = position.get("lat").asText();
            String lng = position.get("lng").asText();
            if (counter == 0)
            {
                coord = lat+","+lng;
                counter++;
            }
            System.out.println(label + " is located at " + lat + "," + lng + ".");
        }
        return coord;
    }
    public String GeocodeSync(String query) throws IOException, InterruptedException {

        HttpClient httpClient = HttpClient.newHttpClient();

        String encodedQuery = URLEncoder.encode(query,"UTF-8");
        String requestUri = GEOCODING_RESOURCE + "?apiKey=" + API_KEY + "&q=" + encodedQuery;

        HttpRequest geocodingRequest = HttpRequest.newBuilder().GET().uri(URI.create(requestUri))
                .timeout(Duration.ofMillis(2000)).build();

        HttpResponse geocodingResponse = httpClient.send(geocodingRequest,
                HttpResponse.BodyHandlers.ofString());

        return (String) geocodingResponse.body();
    }
}

