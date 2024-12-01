package school.sptech;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RequisicaoIA {

    private static List<List<String>> buscarDadosBanco() throws Exception {
        Conexao conexao = new Conexao();
        JdbcTemplate jdbcTemplate = conexao.getConexaoDoBanco();
        List<String> dadosParametro = new ArrayList<>();
        List<String> dadosPromptIA = new ArrayList<>();
        List<String> dadosLeitura = new ArrayList<>();
        List<String> idsEmpresas = new ArrayList<>();

        try {
            String sqlParametro = "SELECT parametro, fkEmpresa, nome tipoParametro FROM parametroRecomendacao pr join tipoParametro tp on tp.id = pr.fkTipoParametro";

            dadosParametro = jdbcTemplate.query(sqlParametro, (rs, rowNum) -> {
                return String.format(
                        "valor parametro:%s, %d, Tipo do parametro:%s",
                        rs.getString("parametro"),
                        rs.getInt("fkEmpresa"),
                        rs.getString("tipoParametro")
                );
            });

            String sqlPromptIA = "SELECT id, descricao FROM promptIA";

            dadosPromptIA = jdbcTemplate.query(sqlPromptIA, (rs, rowNum) -> {
                return String.format(
                        "%s",
                        rs.getString("descricao")
                );
            });

            String sqlLeitura = "SELECT round(sum(areaDesmatada),2) areaDesmatadakm2, round(avg(temperaturaMensal),2) temperaturaMensal, round(avg(precipitacaoMensal),2) precipitacaoMensal, unidadeFederativa, mes, ano, fkEmpresa FROM leitura WHERE 1=1 AND ano >= year(curdate()) - 2 AND ano <> 2024 AND mes = month(curdate()) GROUP BY 4, 5, 6, 7;";

            dadosLeitura = jdbcTemplate.query(sqlLeitura, (rs, rowNum) -> {
                return String.format(
                        "areaDesmatadakm2:%.2f, temperaturaMensal:%.2f, precipitacaoMensal:%.2f, unidadeFederativa:%s, mes:%d, ano:%d, %d",
                        rs.getFloat("areaDesmatadakm2"),
                        rs.getFloat("temperaturaMensal"),
                        rs.getFloat("precipitacaoMensal"),
                        rs.getString("unidadeFederativa"),
                        rs.getInt("mes"),
                        rs.getInt("ano"),
                        rs.getInt("fkEmpresa")
                );
            });

            String sqlIdEmpresas = "SELECT distinct id FROM empresa";

            idsEmpresas = jdbcTemplate.query(sqlIdEmpresas, (rs, rowNum) -> {
                return String.format(
                        "%d",
                        rs.getInt("id")
                );
            });

            List<List<String>> dados = new ArrayList<>();
            dados.add(dadosParametro);
            dados.add(dadosPromptIA);
            dados.add(dadosLeitura);
            dados.add(idsEmpresas);
            return dados;
        } catch (Exception e) {
            String mensage = "Erro ao buscar dado do banco: " + e.getMessage();
            throw new Exception(mensage);
        }
    }

    public static void gerarRecomendacao() throws Exception {
        Conexao conexao = new Conexao();
        JdbcTemplate jdbcTemplate = conexao.getConexaoDoBanco();

        String chaveAPI = "AIzaSyBdFGC3KqHLNET_7JtkPm6mNPHBk87_fy4";
        String endpoint = "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent";

        List<List<String>> dadosBanco = buscarDadosBanco();

        for (String idEmpresa : dadosBanco.get(3)) {
            if (idEmpresa.equals("0")) {
                continue;
            }
//            System.out.println(idEmpresa);
            Integer idEmpresaNumber = Integer.parseInt(idEmpresa);
            //Montar o prompt com os dados do banco
            StringBuilder promptBuilder = new StringBuilder();

            //Adiciona o prompt da IA do banco (a descrição da tabela promptIA)
            int idPromptSorteado = ThreadLocalRandom.current().nextInt(1, dadosBanco.get(1).size() + 1);
            for (String prompt : dadosBanco.get(1)) {
                if (prompt == dadosBanco.get(1).get(idPromptSorteado - 1)) {
                    promptBuilder.append(dadosBanco.get(1).get(idPromptSorteado - 1)).append("\n");
                }
            }

            promptBuilder.append("Caso os valores ultrapassaram os parametros a seguir, dê foco a eles em sua resposta: \n");
            //Adiciona os dados dos parâmetros
            for (int i = 0; i < dadosBanco.get(0).size(); i++) {
                String leitura = dadosBanco.get(0).get(i);
                String[] valores = leitura.split(", ");
                for (String valor : valores) {
                    if (valor.equals(idEmpresa) || valor.equals("0")) {
                        promptBuilder.append(leitura).append("\n");
                    }
                }
            }

            //Adiciona os dados das leituras
            promptBuilder.append("As leituras e dados que foram capitadas do mes e ano atual (2023) e do mesmo mes do ano anterior (2022) são essas:");
            for (int i = 0; i < dadosBanco.get(2).size(); i++) {
                String leitura = dadosBanco.get(2).get(i);
                String[] valores = leitura.split(", ");
                for (String valor : valores) {
                    if (valor.equals(idEmpresa) || valor.equals("0")) {
                        promptBuilder.append(leitura).append("\n");
                    }
                }
            }


            promptBuilder.append("\nFaça uma análise e recomendação personalizada");
//            System.out.println(promptBuilder.toString());

            String requestBody = String.format("""
                    {
                        "contents": [{
                            "parts": [{
                                "text": "%s"
                            }]
                        }],
                        "generationConfig": {
                            "temperature": 0.7,
                            "maxOutputTokens": 1500
                        }
                    }
                    """, promptBuilder.toString().replace("\"", "\\\""));


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

                // Transforma resposta em JSON
                JSONObject jsonResponseBody = new JSONObject(response.body());

                // Navega até o campo "text"
                String text = jsonResponseBody
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");

                String respostaIA = text;
                String respostaTratada = tratarResposta(respostaIA);
                jdbcTemplate.execute(InsercaoTabelas.inserirRecomendacao(respostaTratada, idEmpresaNumber, idPromptSorteado));

            } catch (Exception e) {
                String mensage = "Erro ao gerar recomendação: " + e.getMessage();
                throw new RuntimeException(mensage);
            }
        }
    }

    public static String tratarResposta(String aiResponse) {
        String respostaTratada = aiResponse
                .replace("*", "")
                .replace("\\n\\n", " ")
                .replace("\\n", "")
                .replace("\n","");
//        System.out.println(respostaTratada);
        return respostaTratada;
    }
}
