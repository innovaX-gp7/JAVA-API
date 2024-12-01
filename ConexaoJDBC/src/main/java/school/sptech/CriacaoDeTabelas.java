package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

public class CriacaoDeTabelas extends Conexao{

    // Cria uma conexão com o banco de dados e obtém um objeto 'JdbcTemplate' para interagir com ele.
    public CriacaoDeTabelas() {
        super();
    }

    public void criarTabelaEmpresa(){
        String sql = "CREATE TABLE IF NOT EXISTS empresa (\n" +
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "razaoSocial VARCHAR(80) NOT NULL,\n" +
                "nomeFantasia VARCHAR(50) NOT NULL,\n" +
                "cnpj VARCHAR(14) NOT NULL UNIQUE KEY,\n" +
                "created_at DATETIME NOT NULL DEFAULT now(),\n" +
                "modified_at DATETIME NOT NULL DEFAULT now()\n" +
                ");";
        getConexaoDoBanco().execute(sql);
    }

    public void criarTabelaUserRole(){
        String sql = "CREATE TABLE IF NOT EXISTS userRole (\n" +
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "nome VARCHAR(45) NOT NULL,\n" +
                "created_at DATETIME NOT NULL DEFAULT now(),\n" +
                "modified_at DATETIME NOT NULL DEFAULT now()\n" +
                ");";
        getConexaoDoBanco().execute(sql);
    }

    public void criarTabelaUsuario(){
        String sql = "CREATE TABLE IF NOT EXISTS usuario (\n" +
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "nome VARCHAR(50) NOT NULL,\n" +
                "cpf VARCHAR(11) NOT NULL UNIQUE KEY,\n" +
                "email VARCHAR(45) UNIQUE KEY NOT NULL, \n" +
                "senha VARCHAR(45) NOT NULL,\n" +
                "created_at DATETIME NOT NULL DEFAULT now(),\n" +
                "modified_at DATETIME NOT NULL DEFAULT now(),\n" +
                "fkEmpresa INT NOT NULL,\n" +
                "fkUserRole INT,\n" +
                "CONSTRAINT fk_usuario_empresa FOREIGN KEY(fkEmpresa) REFERENCES empresa(id) ON DELETE CASCADE,\n" +
                "CONSTRAINT fk_usuario_userRole FOREIGN KEY(fkUserRole) REFERENCES userRole(id) ON DELETE SET NULL\n" +
                ")AUTO_INCREMENT=1000;";
        getConexaoDoBanco().execute(sql);
    }

    public void criarTabelaPromptIA(){

        String sql = "CREATE TABLE IF NOT EXISTS promptIA (\n" +
                "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "descricao VARCHAR(3000) NOT NULL,\n" +
                "created_at DATETIME NOT NULL DEFAULT now(),\n" +
                "modified_at DATETIME NOT NULL DEFAULT now()" +
                ");";
        getConexaoDoBanco().execute(sql);
    }

    public void criarTabelaRecomendacaoIA() {
        String sql = "CREATE TABLE IF NOT EXISTS recomendacaoIA (\n" +
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "recomendacao VARCHAR(5000) NOT NULL,\n" +
                "created_at DATETIME NOT NULL DEFAULT now(), \n" +
                "fkEmpresa INT NOT NULL,\n" +
                "fkPromptIA INT,\n" +
                "CONSTRAINT fk_recomendacao_empresa FOREIGN KEY(fkEmpresa) REFERENCES empresa(id) ON DELETE CASCADE,\n" +
                "CONSTRAINT fk_recomendacao_promptIA FOREIGN KEY(fkPromptIA) REFERENCES promptIA(id) ON DELETE SET NULL\n" +
                ")AUTO_INCREMENT=1000;";
        getConexaoDoBanco().execute(sql);
    }

    public void criarTabelaTipoParametro(){
        String sql = "CREATE TABLE IF NOT EXISTS tipoParametro (\n" +
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "nome VARCHAR(45) NOT NULL,\n" +
                "created_at DATETIME NOT NULL DEFAULT now(),\n" +
                "modified_at DATETIME NOT NULL DEFAULT now()" +
                ");";
        getConexaoDoBanco().execute(sql);
    }

    public void criarTabelaParametroRecomendacao(){
        String sql = "CREATE TABLE IF NOT EXISTS parametroRecomendacao (\n" +
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "parametro DECIMAL(6,2) NOT NULL,\n" +
                "created_at DATETIME NOT NULL DEFAULT now(),\n" +
                "modified_at DATETIME NOT NULL DEFAULT now(),\n" +
                "fkEmpresa INT NOT NULL,\n" +
                "fkTipoParametro INT NOT NULL,\n" +
                "CONSTRAINT fk_parametro_tipo_param FOREIGN KEY (fkTipoParametro) REFERENCES tipoParametro(id) ON DELETE CASCADE, \n" +
                "CONSTRAINT fk_parametro_empresa FOREIGN KEY (fkEmpresa) REFERENCES empresa(id) ON DELETE CASCADE\n" +
                ");";
        getConexaoDoBanco().execute(sql);
    }

    public void criarTabelaLogJAR() {
        String sql = "CREATE TABLE IF NOT EXISTS logJAR (\n" +
                "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "descricao VARCHAR(1000) NOT NULL,\n" +
                "created_at DATETIME NOT NULL DEFAULT now()\n" +
                ");";
        getConexaoDoBanco().execute(sql);
    }

    public void criarTabelaLeitura() {
        String sql = "CREATE TABLE IF NOT EXISTS leitura (\n" +
                "id INT PRIMARY KEY  AUTO_INCREMENT,\n" +
                "areaDesmatada DECIMAL(6,2),\n" +
                "temperaturaMensal DECIMAL(4,2),\n" +
                "precipitacaoMensal DECIMAL(5,2),\n" +
                "cidade VARCHAR(45), \n" +
                "unidadeFederativa VARCHAR(2) NOT NULL,\n" +
                "mes TINYINT NOT NULL,\n" +
                "ano YEAR NOT NULL, \n" +
                "fkEmpresa INT, \n" +
                "CONSTRAINT fk_leitura_empresa FOREIGN KEY (fkEmpresa) REFERENCES empresa(id) ON DELETE CASCADE\n" +
                ") AUTO_INCREMENT = 100;";
        getConexaoDoBanco().execute(sql);
    }
}
