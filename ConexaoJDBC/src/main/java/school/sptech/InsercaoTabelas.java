package school.sptech;

public class InsercaoTabelas {

    public static String inserirDados(String temperaturaMensal, String precipitacaoMensal, String cidade, String unidadeFederativa, Integer ano, Integer mes ){
        String sql = "INSERT INTO leitura (temperaturaMensal, precipitacaoMensal, cidade, unidadeFederativa, ano, mes) VALUES (" + temperaturaMensal + ", " + precipitacaoMensal + ", '" + cidade + "', '" + unidadeFederativa + "', " + ano + ", " + mes + ")";
        return sql;
    }

    public static String inserirDadosDesmatamentos(String uf, String anoFinal, Integer mesInt, Double area) {
        String sql = "INSERT INTO leitura (unidadeFederativa, ano, mes, areaDesmatada) " +
                "VALUES ('" + uf + "', " + anoFinal + ", " + mesInt + ", " + area + ")";
        return sql;
    }

    public static String inserirRecomendacao(String recomendacao, Integer fkEmpresa, Integer fkPromptIA){
        String sql = "INSERT INTO recomendacaoIA (recomendacao, fkEmpresa, fkPromptIA)" +
                "VALUES ('" + recomendacao + "', " + fkEmpresa + ", " + fkPromptIA + ")";
        return sql;
    }
}
