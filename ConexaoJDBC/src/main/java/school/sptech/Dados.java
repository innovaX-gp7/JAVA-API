package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

public class Dados {

    // Cria uma conexão com o banco de dados e obtém um objeto `JdbcTemplate` para interagir com ele.
    Conexao conexao = new Conexao();
    JdbcTemplate con = conexao.getConexaoDoBanco();

    // Criando atributos dos Dados
    private Integer idDados;
    private String unidadeFederativa;
    private Byte mes;
    private Integer ano;
    private BigDecimal areaDesmatada;
    private BigDecimal temperaturaMensal;
    private String cidade;

    // Criando construtor vazio
    public Dados() {
    }


    // Criando construtor com parâmetros
    public Dados(Conexao conexao, JdbcTemplate con, Integer id, String cidade, String unidadeFederativa, Byte mes, Integer ano, BigDecimal areaDesmatada, BigDecimal temperaturaMensal) {
        this.conexao = conexao;
        this.con = con;
        this.idDados = id;
        this.unidadeFederativa = unidadeFederativa;
        this.cidade = cidade;
        this.mes = mes;
        this.ano = ano;
        this.areaDesmatada = areaDesmatada;
        this.temperaturaMensal = temperaturaMensal;
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

    public Integer getId() {
        return idDados;
    }

    public void setId(Integer id) {
        this.idDados = id;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }


    public String getUnidadeFederativa() {
        return unidadeFederativa;
    }

    public void setUnidadeFederativa(String unidadeFederativa) {
        this.unidadeFederativa = unidadeFederativa;
    }

    public Byte getMes() {
        return mes;
    }

    public void setMes(Byte mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public BigDecimal getAreaDesmatada() {
        return areaDesmatada;
    }

    public void setAreaDesmatada(BigDecimal areaDesmatada) {
        this.areaDesmatada = areaDesmatada;
    }

    public BigDecimal getTemperaturaMensal() {
        return temperaturaMensal;
    }

    public void setTemperaturaMensal(BigDecimal temperaturaMensal) {
        this.temperaturaMensal = temperaturaMensal;
    }




    // Criando metódo para criar a tabela no Banco
    public String criarTabelaDados() {
        String sql = "CREATE TABLE IF NOT EXISTS dados (\n" +
                "    idDados INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "    areaDesmatada DECIMAL(4,2) NOT NULL,\n" +
                "    temperaturaMensal DECIMAL(4,2) NOT NULL,\n" +
                "    precipitacaoMensal DECIMAL(4,2) NOT NULL,\n" +
                "    cidade VARCHAR(45), \n" +
                "    unidadeFederativa VARCHAR(50) NOT NULL,\n" +
                "    mes TINYINT,\n" +
                "    ano YEAR,\n" +
                ") AUTO_INCREMENT = 100;";
        return sql;
    }
}
