package school.sptech;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequisicaoIA {
    public static void main(String[] args) {
        String chaveAPI = "AIzaSyBjyREW97sbYAG-eWdNWFB2CAqePREmFqw";
        String endpoint = "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent";


        String requestBody = """
            {
                "contents": [{
                    "parts": [{
                        "text": "Escreva um texto aleatório sobre árvores"
                    }]
                }],
                "generationConfig": {
                    "temperature": 0.7,
                    "maxOutputTokens": 100
                }
            }
            """;

        try {
            HttpClient client = HttpClient.newHttpClient();

            // Criar a requisição HTTP POST
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint + "?key=" + chaveAPI))  // A chave API vai como parâmetro na URL
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Enviar a requisição e receber a resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Imprimir o corpo da resposta, não o objeto response inteiro
            System.out.println("Resposta da IA: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
