package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

public class InsercaoTabelas extends Conexao{

    public InsercaoTabelas() {
        super();
    }

    public static String inserirDados(String temperaturaMensal, String precipitacaoMensal, String cidade, String unidadeFederativa, Integer ano, Integer mes){
        String sql = "INSERT INTO leitura (temperaturaMensal, precipitacaoMensal, cidade, unidadeFederativa, ano, mes) " +
                "VALUES (" + temperaturaMensal + ", " + precipitacaoMensal + ", '" + cidade + "', '" + unidadeFederativa + "', " + ano + ", " + mes + ")";
        getConexaoDoBanco().update(sql, temperaturaMensal, precipitacaoMensal, cidade, unidadeFederativa, ano, mes);
        System.out.println("Dados inseridos com sucesso na tabela `leitura`.");
        return sql;
    }

    public static String inserirDadosDesmatamentos(String unidadeFederativa, String anoFinal, Integer mesInt, Double area) {
        String sql = "INSERT INTO leitura (unidadeFederativa, ano, mes, areaDesmatada) " +
                "VALUES ('" + unidadeFederativa + "', " + anoFinal + ", " + mesInt + ", " + area + ")";
        getConexaoDoBanco().update(sql, unidadeFederativa, anoFinal, mesInt, area);
        System.out.println("Dados de desmatamento inseridos com sucesso na tabela `leitura`.");
        return sql;
    }
}
