package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RequisicaoIA {

    private static List<List<String>> buscarDadosBanco() {
        Conexao conexao = new Conexao();
        JdbcTemplate jdbcTemplate = conexao.getConexaoDoBanco();
        List<String> dadosParametro = new ArrayList<>();
        List<String> dadosPromptIA = new ArrayList<>();
        List<String> dadosLeitura = new ArrayList<>();
        List<String> idsEmpresas = new ArrayList<>();


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

        String sqlLeitura = "SELECT areaDesmatada, temperaturaMensal, precipitacaoMensal, cidade, unidadeFederativa, mes, ano, fkEmpresa FROM leitura";

        dadosLeitura = jdbcTemplate.query(sqlLeitura, (rs, rowNum) -> {
            return String.format(
                    "areaDesmatada:%f, temperaturaMensal:%f, precipitacaoMensal:%f, cidade:%s, unidadeFederativa:%s, mes:%d, ano:%d, %d",
                    rs.getFloat("areaDesmatada"),
                    rs.getFloat("temperaturaMensal"),
                    rs.getFloat("precipitacaoMensal"),
                    rs.getString("cidade"),
                    rs.getString("unidadeFederativa"),
                    rs.getInt("mes"),
                    rs.getInt("ano"),
                    rs.getInt("fkEmpresa")
            );
        });

        String sqlIdEmpresas = "SELECT distinct fkEmpresa FROM leitura";

        idsEmpresas = jdbcTemplate.query(sqlIdEmpresas, (rs, rowNum) -> {
            return String.format(
                    "%d",
                    rs.getInt("fkEmpresa")
            );
        });

        List<List<String>> dados = new ArrayList<>();
        dados.add(dadosParametro);
        dados.add(dadosPromptIA);
        dados.add(dadosLeitura);
        dados.add(idsEmpresas);
        return dados;
    }


    public static void main(String[] args) {
        Conexao conexao = new Conexao();
        JdbcTemplate jdbcTemplate = conexao.getConexaoDoBanco();

        String chaveAPI = "AIzaSyBjyREW97sbYAG-eWdNWFB2CAqePREmFqw";
        String endpoint = "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent";

        List<List<String>> dadosBanco = buscarDadosBanco();

        for (String idEmpresa : dadosBanco.get(3)) {
            if(idEmpresa.equals("0")) {
                continue;
            }
            System.out.println(idEmpresa);
            Integer idEmpresaNumber = Integer.parseInt(idEmpresa);
            //Montar o prompt com os dados do banco
            StringBuilder promptBuilder = new StringBuilder();

            promptBuilder.append("Com base nos seguintes parâmetros de recomendação: \n");
            //Adiciona os dados dos parâmetros
            for (int i = 0; i < dadosBanco.get(0).size(); i++) {
                String leitura = dadosBanco.get(0).get(i);
                String[] valores = leitura.split(", ");
                for (String valor : valores) {
                    if(valor.equals(idEmpresa) || valor.equals("0")) {
                        promptBuilder.append(leitura).append("\n");
                    }
                }
            }

            //Adiciona o prompt da IA do banco (a descrição da tabela promptIA)
            for (String prompt : dadosBanco.get(1)) {
                if (prompt == dadosBanco.get(1).get(5)) {
                    promptBuilder.append(dadosBanco.get(1).get(5)).append("\n");
                }
            }

            //Adiciona os dados das leituras
            for (int i = 0; i < dadosBanco.get(2).size(); i++) {
                String leitura = dadosBanco.get(2).get(i);
                String[] valores = leitura.split(", ");
                for (String valor : valores) {
                    if(valor.equals(idEmpresa) || valor.equals("0")) {
                        promptBuilder.append(leitura).append("\n");
                    }
                }
            }


            promptBuilder.append("\nFaça uma análise e recomendação personalizada");
            System.out.println(promptBuilder.toString());

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

                Integer tentativas = 0;
                Boolean respostaCorreta = false;
                String respostaIA = "";
                do {
                    // Enviar a requisição e receber a resposta
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    Integer posicaoInicio = response.body().indexOf("cc*D");
                    Integer posicaoFinal = response.body().lastIndexOf("cc*D");


                    if ((posicaoFinal != -1) && (!posicaoInicio.equals(posicaoFinal))) {
                        respostaIA = response.body().substring(posicaoInicio + 4, posicaoFinal);
                        System.out.println(respostaIA);
                        respostaCorreta = true;
                    } else if (posicaoInicio.equals(posicaoFinal) && posicaoInicio != -1) {
                        posicaoInicio = response.body().indexOf("\"text\": ");
                        respostaIA = response.body().substring(posicaoInicio + 9, posicaoFinal);
                        System.out.println(respostaIA);
                        respostaCorreta = true;
                    } else {
                        tentativas++;
                        System.out.println("Erro na execução!");
                    }
                } while (!respostaCorreta && tentativas < 3);
                if (respostaCorreta) {
                    String respostaTratada = tratarResposta(respostaIA);
                    jdbcTemplate.execute(InsercaoTabelas.inserirRecomendacao(respostaTratada,idEmpresaNumber, 6));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String tratarResposta(String aiResponse){
        String respostaTratada = aiResponse
                .replace("*","")
                .replace("\\n\\n"," ")
                .replace("\\n", "");
        System.out.println(respostaTratada);
        return respostaTratada;
    }
}