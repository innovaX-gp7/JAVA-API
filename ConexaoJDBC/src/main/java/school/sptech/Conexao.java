package school.sptech;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Conexao {

    private JdbcTemplate conexaoDoBanco;

    public Conexao() {
        // Configurando o DataSource
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://mysql-container:3306/InnovaxDB"); // Aqui fica a URL do nosso banco de dados
        dataSource.setUsername("root"); // Aqui fica o usuário do Banco de Dados
        dataSource.setPassword("Urubu100"); // Aqui fica a senha do usuário

        // Inicializando o JdbcTemplate com o DataSource configurado
        this.conexaoDoBanco = new JdbcTemplate(dataSource);
    }

    public JdbcTemplate getConexaoDoBanco() {
        return conexaoDoBanco;
    }
}
