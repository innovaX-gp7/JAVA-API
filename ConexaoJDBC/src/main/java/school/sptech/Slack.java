package school.sptech;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Slack {

    private String url = System.getenv("WEBHOOK");

    public void sendMessage(JSONObject message) throws Exception {
        URL obj = new URL(this.url);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = message.toString().getBytes("UTF-8");
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("Failed to send message. HTTP error code: " + responseCode);
        }

    }
}
