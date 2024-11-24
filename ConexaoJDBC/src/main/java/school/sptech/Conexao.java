package school.sptech;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Conexao {

    private static JdbcTemplate conexaoDoBanco;


    public Conexao() {
        // Configurando o DataSource
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/InnovaxDB"); // Aqui fica a URL do nosso banco de dados
        dataSource.setUsername("root"); // Aqui fica o usuário do Banco de Dados
        dataSource.setPassword("ipurpleyou1437"); // Aqui fica a senha do usuário

        // Inicializando o JdbcTemplate com o DataSource configurado
        conexaoDoBanco = new JdbcTemplate(dataSource);
        System.out.println("Conexão com o banco de dados configurada com sucesso.");
    }

    public static JdbcTemplate getConexaoDoBanco() {
        return conexaoDoBanco;
    }


}
