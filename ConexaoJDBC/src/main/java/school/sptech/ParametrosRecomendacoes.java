package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalTime;

public class ParametrosRecomendacoes {

    // Criando atributos dos Parametros(aqui deve ser o mesmo nome que as colunas do Banco)
    private Integer idParametros;
    private BigDecimal limiteArea;
    private Integer fk_empresa_recom;

    // Cria uma conexão com o banco de dados e obtém um objeto `JdbcTemplate` para interagir com ele.
    Conexao conexao = new Conexao();
    JdbcTemplate con = conexao.getConexaoDoBanco();

    // Criando construtor vazio
    public ParametrosRecomendacoes(){}

    // Criando construtor com os parâmetros
    public ParametrosRecomendacoes(Integer idParametros, BigDecimal limiteArea, Integer fk_empresa_recom, Conexao conexao, JdbcTemplate con) {
        this.idParametros = idParametros;
        this.limiteArea = limiteArea;
        this.fk_empresa_recom = fk_empresa_recom;
        this.conexao = conexao;
        this.con = con;
    }

    public Integer getIdParametros() {
        return idParametros;
    }

    public void setIdParametros(Integer idParametros) {
        this.idParametros = idParametros;
    }

    public BigDecimal getLimiteArea() {
        return limiteArea;
    }

    public void setLimiteArea(BigDecimal limiteArea) {
        this.limiteArea = limiteArea;
    }

    public Integer getFk_empresa_recom() {
        return fk_empresa_recom;
    }

    public void setFk_empresa_recom(Integer fk_empresa_recom) {
        this.fk_empresa_recom = fk_empresa_recom;
    }

    public Conexao getConexao() {
        return conexao;
    }

    public void setConexao(Conexao conexao) {
        this.conexao = conexao;
    }

    public JdbcTemplate getCon() {
        return con;
    }

    public void setCon(JdbcTemplate con) {
        this.con = con;
    }

    public String criarTabelaParametrosRecomendacoes(){
        String sql = "CREATE TABLE IF NOT EXISTS parametrosRecomendacoes (\n" +
                "idParametros INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "limiteArea DECIMAL (4,2),\n" +
                "fkEmpresaRecomendacoes INT,\n" +
                "CONSTRAINT fk_empresa_recom FOREIGN KEY (fkEmpresaRecomendacoes) REFERENCES empresa(idEmpresa)\n" +
                ")auto_increment=10000;";
        return sql;
    }
}
