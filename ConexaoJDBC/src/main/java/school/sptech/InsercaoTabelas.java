package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

public class InsercaoTabelas extends Conexao {

    public InsercaoTabelas() {
        super();
    }

    public static String inserirDados(String temperaturaMensal, String precipitacaoMensal, String cidade, String unidadeFederativa, Integer ano, Integer mes){
        String sql = "INSERT INTO leitura (temperaturaMensal, precipitacaoMensal, cidade, unidadeFederativa, ano, mes) " +
                "VALUES (" + temperaturaMensal + ", " + precipitacaoMensal + ", '" + cidade + "', '" + unidadeFederativa + "', " + ano + ", " + mes + ")";
//        System.out.println("Dados inseridos com sucesso na tabela `leitura`.");
        return sql;
    }

    public static String inserirDadosDesmatamentos(String unidadeFederativa, String anoFinal, Integer mesInt, Double area) {
        String sql = "INSERT INTO leitura (unidadeFederativa, ano, mes, areaDesmatada) " +
                "VALUES ('" + unidadeFederativa + "', " + anoFinal + ", " + mesInt + ", " + area + ")";
//        getConexaoDoBanco().update(sql);
//        System.out.println("Dados de desmatamento inseridos com sucesso na tabela `leitura`.");
        return sql;
    }

    public static String inserirRecomendacao(String recomendacao, Integer fkEmpresa, Integer fkPromptIA){
        String sql = "INSERT INTO recomendacaoIA (recomendacao, fkEmpresa, fkPromptIA)" +
                "VALUES ('" + recomendacao + "', " + fkEmpresa + ", " + fkPromptIA + ")";
//        getConexaoDoBanco().update(sql);
        return sql;
    }

    public static void inserirDadosTabelasAuxiliares(){
        String sql = "INSERT IGNORE INTO userRole (id, nome) VALUES \n" +
                "(1, 'Representante Legal'),\n" +
                "(2, 'Gerente'),\n" +
                "(3, 'Analista');\n";
        getConexaoDoBanco().execute(sql);
        sql = "INSERT IGNORE INTO tipoParametro (id, nome) VALUES \n" +
                "(1, 'desmatamento'),\n" +
                "(2, 'temperatura'),\n" +
                "(3, 'precipitação');\n";
        getConexaoDoBanco().execute(sql);
    }

    public static void inserirPromptBase(){
        String sql = "INSERT IGNORE INTO promptIA \n" +
                "(`id`, `descricao`, `created_at`)\n" +
                "VALUES \n" +
                "(1, 'Você é uma Inteligência Artificial especializada em mitigar os impactos do desmatamento na Amazônia, analisando dados sobre temperatura, precipitação, área desmatada e outros fatores ambientais. Sua tarefa é transformar essas informações em recomendações práticas e objetivas. O objetivo é oferecer estratégias claras que auxiliem no planejamento de reflorestamento e na preservação ambiental, considerando a biodiversidade e as condições locais de cada região afetada. Baseando-se nos impactos detectados, como aumento da temperatura, redução da precipitação, degradação do solo e perda de biodiversidade, suas recomendações devem incluir ações eficazes que promovam o reflorestamento com espécies nativas adequadas ao clima e ao tipo de solo. Envolver comunidades locais é essencial, incentivando práticas agroflorestais e sustentáveis que ajudem a restaurar o equilíbrio ecológico. É importante sugerir medidas preventivas contra novos desmatamentos, incluindo aprimoramento na fiscalização, fortalecimento de políticas públicas que desestimulem atividades ilegais e parcerias com organizações especializadas na conservação ambiental. As ações propostas devem ser diretas e acessíveis, priorizando soluções que possam ser implementadas rapidamente e que gerem impacto positivo significativo na preservação da floresta e na qualidade de vida das populações locais.\n" +
                "Seja assertivo e direto na resposta, colocando somente a recomendação do que realmente importa. \n" +
                "Não se preocupe com o contexto, apenas fale o que fazer diante os dados.\n" +
                "Não ultrapasse 2000 caracteres de resposta.\n" +
                "Não termine a resposta bruptamente.\n" +
                "Evite ao maximo usar palavras com acentuação.\n" +
                "Responda de forma direta, sem listar itens ou tópicos, mantendo a resposta como um texto contínuo. Sem quebras de texto.\n" +
                "Não utilize aspas simples ou duplas.\n" +
                "Escreva sem markdown.\n" +
                "Não descreva o que fazer estado por estado, apenas uma recomendação de ações gerais a tomar" +
                "', NOW());\n";
        getConexaoDoBanco().execute(sql);
    }
}
