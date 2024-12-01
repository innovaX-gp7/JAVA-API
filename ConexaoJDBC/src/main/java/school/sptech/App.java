package school.sptech;

import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class App {

        Slack slack = new Slack();

        JSONObject message = new JSONObject();
        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();


        public void enviarMensagemSlack() throws Exception {
            String sql = ("SELECT DISTINCT descricao\n" +
                    "FROM logJAR\n" +
                    "WHERE descricao LIKE \"%registrados%\"\n" +
                    "AND created_at > DATE_SUB(NOW(), INTERVAL 2 HOUR)\n" +
                    "LIMIT 1;");

            String resultadoSelect = con.queryForObject(sql, String.class);

            if (resultadoSelect != null) {
                message.put("text",
                        ":rotating_light: Os dados da dashboard foram atualizados! Verifique a dashboard para visualizar as novas informações ");
            }

            slack.sendMessage(message);
        }

        public void enviarRelatorioMes() throws Exception {

            String relatorioMes = ("SELECT \n" +
                    "    MAX(mes) AS mes,\n" +
                    "    ROUND(SUM(areaDesmatada), 2) AS totalAreaDesmatada,\n" +
                    "    ROUND(AVG(temperaturaMensal), 1) AS mediaTemperatura,\n" +
                    "    ROUND(AVG(precipitacaoMensal), 2) AS mediaPrecipitacao\n" +
                    "FROM \n" +
                    "    leitura\n" +
                    "WHERE \n" +
                    "    ano = YEAR(CURDATE()) - 1;");



            Map<String, Object> resultadoSelect = con.queryForMap(relatorioMes);


            BigDecimal totalAreaDesmatada = (BigDecimal) resultadoSelect.get("totalAreaDesmatada");
            BigDecimal mediaTemperatura = (BigDecimal) resultadoSelect.get("mediaTemperatura");
            BigDecimal mediaPrecipitacao = (BigDecimal) resultadoSelect.get("mediaPrecipitacao");



            String mensagemFormatada = String.format(
                    "Resumo do Mês: Desmatamento total: %.2f km² | Média da temperatura: %.1f°C | Média de precipitação: %.2f mm. Confira com mais detalhes na dashboard!",
                    totalAreaDesmatada, mediaTemperatura, mediaPrecipitacao
            );
            message.put("text", new String(mensagemFormatada.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));

            slack.sendMessage(message);
        }

        public String enviarAvisoNovaRecomendacao() throws Exception {
            String query = ("     SELECT \n" +
                    "                     recomendacao\n" +
                    "                 FROM \n" +
                    "                     recomendacaoIA \n" +
                    "                 WHERE \n" +
                    "                     created_at >= NOW() - INTERVAL 5 MINUTE limit 1;");

            String resultadoQuery = con.queryForObject(query, String.class);

            if (resultadoQuery != null) {
                message.put("text",
                       ":rotating_light: Uma nova recomendação foi enviada! Verifique a dashboard para visualizar");
            }

            slack.sendMessage(message);
            return query;

        }
}

