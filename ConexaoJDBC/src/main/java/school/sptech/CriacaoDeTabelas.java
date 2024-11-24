package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

public class CriacaoDeTabelas extends Conexao{

    // Cria uma conexão com o banco de dados e obtém um objeto 'JdbcTemplate' para interagir com ele.
    public CriacaoDeTabelas() {
        super();
    }

    public void criarTabelaEmpresa(){
        String sql = """
                CREATE TABLE IF NOT EXISTS empresa (
                id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
                razaoSocial VARCHAR(80) NOT NULL,
                nomeFantasia VARCHAR(50) NOT NULL,
                cnpj VARCHAR(14) UNIQUE KEY DEFAULT NULL
                );""";
        getConexaoDoBanco().execute(sql);
        System.out.println("Tabela empresa criada com sucesso");
    }

    public void criarTabelaUserRole(){
        String sql = """
                CREATE TABLE IF NOT EXISTS userRole (
                id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
                nome VARCHAR(45) NOT NULL,
                created_at DATETIME NOT NULL DEFAULT now(),
                modified_at DATETIME
                );""";
        getConexaoDoBanco().execute(sql);
        System.out.println("Tabela userRole criada com sucesso");
    }


    public void criarTabelaUsuario(){
        String sql = """
                CREATE TABLE IF NOT EXISTS usuario (
                id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
                nome VARCHAR(50) NOT NULL,
                cpf VARCHAR(11) UNIQUE KEY DEFAULT NULL,
                email VARCHAR(45) UNIQUE KEY NOT NULL,\s
                senha VARCHAR(45) NOT NULL,
                created_at DATETIME NOT NULL,
                modified_at DATETIME,
                fkEmpresa INT,
                fkUserRole INT,
                CONSTRAINT fk_usuario_empresa FOREIGN KEY(fkEmpresa) REFERENCES empresa(id),
                CONSTRAINT fk_usuario_userRole FOREIGN KEY(fkUserRole) REFERENCES userRole(id)
                )AUTO_INCREMENT=1000;""";
        getConexaoDoBanco().execute(sql);
        System.out.println("Tabela usuario criada com sucesso");
    }

    public void criarTabelaPromptIA(){

        String sql = """
                CREATE TABLE IF NOT EXISTS promptIA (
                id INT PRIMARY KEY AUTO_INCREMENT,
                descricao VARCHAR(1000),
                dataHora DATETIME
                );""";
        getConexaoDoBanco().execute(sql);
        System.out.println("Tabela promptIA criada com sucesso");
    }

    public void criarTabelaRecomendacaoIA() {
        String sql = """
                CREATE TABLE IF NOT EXISTS recomendacaoIA (
                id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
                unidadeFederativa VARCHAR(50) NOT NULL,
                recomendacao VARCHAR(100) NOT NULL,
                created_at DATETIME NOT NULL,\s
                fkEmpresa INT,
                fkPromptIA INT,
                CONSTRAINT fk_recomendacao_empresa FOREIGN KEY(fkEmpresa) REFERENCES empresa(id),
                CONSTRAINT fk_recomendacao_promptIA FOREIGN KEY(fkPromptIA) REFERENCES promptIA(id)
                )AUTO_INCREMENT=1000;""";
        getConexaoDoBanco().execute(sql);
        System.out.println("Tabela recomendacaoIA criada com sucesso");
    }

    public void criarTabelaTipoParametro(){
        String sql = """
                CREATE TABLE IF NOT EXISTS tipoParametro (
                id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
                nome VARCHAR(45) NOT NULL
                );""";
        getConexaoDoBanco().execute(sql);
        System.out.println("Tabela tipoParametro criada com sucesso");
    }

    public void criarTabelaParametroRecomendacao(){
        String sql = """
                CREATE TABLE IF NOT EXISTS parametroRecomendacao (
                id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
                parametro DECIMAL(6,2),
                created_at DATETIME NOT NULL,
                modified_at DATETIME,
                fkEmpresa INT,
                fkTipoParametro INT,
                CONSTRAINT fk_parametro_tipo_param FOREIGN KEY (fkTipoParametro) REFERENCES tipoParametro(id),\s
                CONSTRAINT fk_parametro_empresa FOREIGN KEY (fkEmpresa) REFERENCES empresa(id)
                );""";
        getConexaoDoBanco().execute(sql);
        System.out.println("Tabela parametroRecomendacao criada com sucesso");
    }

    public void criarTabelaLogJAR() {
        String sql = """
                CREATE TABLE IF NOT EXISTS logJAR (
                id INT PRIMARY KEY AUTO_INCREMENT,
                descricao VARCHAR(1000),
                created_at DATETIME NOT NULL,
                fkEmpresa INT,
                CONSTRAINT fk_log_empresa FOREIGN KEY (fkEmpresa) REFERENCES empresa(id)
                );""";
        getConexaoDoBanco().execute(sql);
        System.out.println("Tabela logJAR criada com sucesso");
    }

    public void criarTabelaLeitura() {
        String sql = """
                CREATE TABLE IF NOT EXISTS leitura (
                id INT PRIMARY KEY  AUTO_INCREMENT,
                areaDesmatada DECIMAL(6,2),
                temperaturaMensal DECIMAL(4,2),
                precipitacaoMensal DECIMAL(5,2),
                cidade VARCHAR(45),\s
                unidadeFederativa VARCHAR(50) NOT NULL,
                mes TINYINT NOT NULL,
                ano YEAR NOT NULL,\s
                fkEmpresa INT,\s
                CONSTRAINT fk_leitura_empresa FOREIGN KEY (fkEmpresa) REFERENCES empresa(id)
                ) AUTO_INCREMENT = 100;""";
        getConexaoDoBanco().execute(sql);
        System.out.println("Tabela leitura criada com sucesso");
    }



}
