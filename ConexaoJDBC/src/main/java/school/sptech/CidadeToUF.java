package school.sptech;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.Normalizer;

public class CidadeToUF {

//    public static void main(String[] args) {
//        String cidade = "ALMENARA"; // Nome da cidade
//        String uf = getUF(cidade); // Chama o método para obter a UF
//        if (uf != null) {
//            System.out.println("Cidade: " + cidade + ", UF: " + uf);
//        } else {
//            System.out.println("Cidade não encontrada.");
//        }
//    }

    // Método para obter a UF a partir do nome da cidade
    public static String getUF(String cidade) {
        try {
            String cidadeCodificada = cidade.replace(" ", "%20");  // Codifica o nome da cidade para a URL
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://servicodados.ibge.gov.br/api/v1/localidades/municipios?nome=" + cidadeCodificada))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            // Normaliza a cidade para comparação
            String cidadeNormalizada = normalizarString(cidade);

            // Itera sobre a resposta da API para encontrar a cidade
            String uf = null;

            // Para comparar a cidade, vamos analisar a resposta da API
            for (String linha : body.split("\\},\\{")) { // Divide as linhas da resposta
                // Verifica se a linha contém o nome do município
                if (linha.contains("\"nome\":\"")) {
                    String nomeMunicipio = linha.split("\"nome\":\"")[1].split("\"")[0];
                    String nomeMunicipioNormalizado = normalizarString(nomeMunicipio);

                    if (nomeMunicipioNormalizado.equals(cidadeNormalizada)) {
                        uf = linha.split("\"sigla\":\"")[1].split("\"")[0]; // Extrai a UF
                        break; // Encerra o loop quando encontra a cidade
                    }
                }
            }
            return uf;

        } catch (Exception e) {
            e.printStackTrace();
            return null; // Retorna null em caso de erro
        }
    }

    // Método para normalizar a string, removendo acentos e transformando em maiúsculas
    private static String normalizarString(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toUpperCase(); // Transforma em maiúsculas para comparação
    }
}


