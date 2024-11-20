package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

public class CriacaoDeTabelas {

    // Cria uma conexão com o banco de dados e obtém um objeto `JdbcTemplate` para interagir com ele.
    Conexao conexao = new Conexao();
    JdbcTemplate con = conexao.getConexaoDoBanco();

    public CriacaoDeTabelas() {
    }

    public String criarTabelaEmpresa(){
        String sql = "CREATE TABLE IF NOT EXISTS empresa (\n" +
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "razaoSocial VARCHAR(80) NOT NULL,\n" +
                "nomeFantasia VARCHAR(50) NOT NULL,\n" +
                "cnpj VARCHAR(14) UNIQUE KEY DEFAULT NULL,\n" +
                "created_at DATETIME NOT NULL DEFAULT now(),\n" +
                "modified_at DATETIME\n" +
                ");";
        return sql;
    }

    public String criarTabelaUserRole(){
        String sql = "CREATE TABLE IF NOT EXISTS userRole (\n" +
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "nome VARCHAR(45) NOT NULL,\n" +
                "created_at DATETIME NOT NULL DEFAULT now(),\n" +
                "modified_at DATETIME\n" +
                ");";
        return sql;
    }

    public String criarTabelaUsuario(){
        String sql = "CREATE TABLE IF NOT EXISTS usuario (\n" +
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "nome VARCHAR(50) NOT NULL,\n" +
                "cpf VARCHAR(11) UNIQUE KEY DEFAULT NULL,\n" +
                "email VARCHAR(45) UNIQUE KEY NOT NULL, \n" +
                "senha VARCHAR(45) NOT NULL,\n" +
                "created_at DATETIME NOT NULL DEFAULT now(),\n" +
                "modified_at DATETIME,\n" +
                "fkEmpresa INT,\n" +
                "fkUserRole INT,\n" +
                "CONSTRAINT fk_usuario_empresa FOREIGN KEY(fkEmpresa) REFERENCES empresa(id),\n" +
                "CONSTRAINT fk_usuario_userRole FOREIGN KEY(fkUserRole) REFERENCES userRole(id)\n" +
                ")AUTO_INCREMENT=1000;";
        return sql;
    }

    public String criarTabelaPromptIA(){

        String sql = "CREATE TABLE IF NOT EXISTS promptIA (\n" +
                "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "descricao VARCHAR(1000),\n" +
                "dataHora DATETIME,\n" +
                "created_at DATETIME NOT NULL DEFAULT now(),\n" +
                "modified_at DATETIME" +
                ");";
        return sql;
    }

    public String criarTabelaRecomendacaoIA() {
        String sql = "CREATE TABLE IF NOT EXISTS recomendacaoIA (\n" +
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "unidadeFederativa VARCHAR(50) NOT NULL,\n" +
                "recomendacao VARCHAR(100) NOT NULL,\n" +
                "created_at DATETIME NOT NULL, \n" +
                "fkEmpresa INT,\n" +
                "fkPromptIA INT,\n" +
                "CONSTRAINT fk_recomendacao_empresa FOREIGN KEY(fkEmpresa) REFERENCES empresa(id),\n" +
                "CONSTRAINT fk_recomendacao_promptIA FOREIGN KEY(fkPromptIA) REFERENCES promptIA(id)\n" +
                ")AUTO_INCREMENT=1000;";
        return sql;
    }

    public String criarTabelaTipoParametro(){
        String sql = "CREATE TABLE IF NOT EXISTS tipoParametro (\n" +
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "nome VARCHAR(45) NOT NULL\n" +
                ");";
        return sql;
    }

    public String criarTabelaParametroRecomendacao(){
        String sql = "CREATE TABLE IF NOT EXISTS parametroRecomendacao (\n" +
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "parametro DECIMAL(6,2),\n" +
                "created_at DATETIME NOT NULL,\n" +
                "modified_at DATETIME,\n" +
                "fkEmpresa INT,\n" +
                "fkTipoParametro INT,\n" +
                "CONSTRAINT fk_parametro_tipo_param FOREIGN KEY (fkTipoParametro) REFERENCES tipoParametro(id), \n" +
                "CONSTRAINT fk_parametro_empresa FOREIGN KEY (fkEmpresa) REFERENCES empresa(id)\n" +
                ");";
        return sql;
    }

    public String criarTabelaLogJAR() {
        String sql = "CREATE TABLE IF NOT EXISTS logJAR (\n" +
                "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "descricao VARCHAR(1000),\n" +
                "created_at DATETIME NOT NULL,\n" +
                "fkEmpresa INT,\n" +
                "CONSTRAINT fk_log_empresa FOREIGN KEY (fkEmpresa) REFERENCES empresa(id)\n" +
                ");";
        return sql;
    }

    public String criarTabelaLeitura() {
        String sql = "CREATE TABLE IF NOT EXISTS leitura (\n" +
                "id INT PRIMARY KEY  AUTO_INCREMENT,\n" +
                "areaDesmatada DECIMAL(6,2),\n" +
                "temperaturaMensal DECIMAL(4,2),\n" +
                "precipitacaoMensal DECIMAL(5,2),\n" +
                "cidade VARCHAR(45), \n" +
                "unidadeFederativa VARCHAR(50) NOT NULL,\n" +
                "mes TINYINT NOT NULL,\n" +
                "ano YEAR NOT NULL, \n" +
                "fkEmpresa INT, \n" +
                "CONSTRAINT fk_leitura_empresa FOREIGN KEY (fkEmpresa) REFERENCES empresa(id)\n" +
                ") AUTO_INCREMENT = 100;";
        return sql;
    }



}
