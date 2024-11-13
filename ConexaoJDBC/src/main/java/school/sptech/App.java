package school.sptech;

import org.json.JSONObject;

public class App {


    public static void main(String[] args) throws Exception {
        Slack slack = new Slack();

        JSONObject message = new JSONObject();
        message.put("text", "Olá Matheus Cantegelo");

        slack.sendMessage(message);
    }
}
