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


        public String enviarMensagemSlack() throws Exception {
            String sql = ("select distinct descricao\n" +
                    "from logJAR \n" +
                    "where descricao like \"%registrados%\"\n" +
                    "and created_at = (select max(created_at) from logJAR limit 1) limit 1;");


            String resultadoSelect = con.queryForObject(sql, String.class);


            if (resultadoSelect != null) {
                message.put("text",
                        ":rotating_light: Os dados da dashboard foram atualizados! Verifique a dashboard para visualizar as novas informações ");


            }
            slack.sendMessage(message);
            return sql;
        }

            // PARA: nosso cliente -> representante da empresa
            // QUANDO: mensalmente, quando tiver dado atualizado, e for gerado uma nova notificação
            // Atualização Mensal: média de area desmatada da amazonia, de temperatura e umidade
            // quando for atualizado o dado na dashboard
            // quando for gerada uma nova recomendação da IA


        public String enviarRelatorioMes() throws Exception {

            String relatorioMes = ("SELECT \n" +
                    "    round(SUM(areaDesmatada),2) AS totalAreaDesmatada,\n" +
                    "    round(AVG(temperaturaMensal),1) AS mediaTemperatura,\n" +
                    "    round(AVG(precipitacaoMensal),2) AS mediaPrecipitacao\n" +
                    "FROM \n" +
                    "    leitura\n" +
                    "WHERE \n" +
                    " mes = MONTH(CURDATE()) AND ano = YEAR(CURDATE());");



            Map<String, Object> resultadoSelect = con.queryForMap(relatorioMes);


            BigDecimal totalAreaDesmatada = (BigDecimal) resultadoSelect.get("totalAreaDesmatada");
            BigDecimal mediaTemperatura = (BigDecimal) resultadoSelect.get("mediaTemperatura");
            BigDecimal mediaPrecipitacao = (BigDecimal) resultadoSelect.get("mediaPrecipitacao");



            String mensagemFormatada = String.format(
                    "Resumo do Mês: Desmatamento total: %.2f km² | Média da temperatura: %.1f°C | Média de precipitação: %.2f mm. Confira com mais detalhes na dashboard!",
                    totalAreaDesmatada, mediaTemperatura, mediaPrecipitacao
            );


            message.put("text", mensagemFormatada);

            slack.sendMessage(message);

            return relatorioMes;
        }

        public String enviarAvisoNovaRecomendacao() throws Exception {

            String query = ("     SELECT \n" +
                    "                     recomendacao\n" +
                    "                 FROM \n" +
                    "                     recomendacao \n" +
                    "                 WHERE \n" +
                    "                     created_at >= NOW() - INTERVAL 5 MINUTE;");



            String resultadoQuery = con.queryForObject(query, String.class);

           if (resultadoQuery != null) {
               message.put("text",
                       ":rotating_light: Uma nova recomendação foi enviada! Verifique a dashboard para visualizar");

           }

            slack.sendMessage(message);
            return query;

        }



}

