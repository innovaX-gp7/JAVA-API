package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

public class RecomendacoesIA {

    private Integer idRecomedacoes;
    private String unidadeFederativa;
    private String recomendacao;
    private Integer fkEmpresa;

    // Cria uma conexão com o banco de dados e obtém um objeto `JdbcTemplate` para interagir com ele.
    Conexao conexao = new Conexao();
    JdbcTemplate con = conexao.getConexaoDoBanco();

    // Criando construtor vazio
    public RecomendacoesIA() {
    }

    ;

    // Criando construtor com parâmetros
    public RecomendacoesIA(Integer idRecomedacoes, String unidadeFederativa, String recomendacao, Integer fkEmpresa, Conexao conexao, JdbcTemplate con) {
        this.idRecomedacoes = idRecomedacoes;
        this.unidadeFederativa = unidadeFederativa;
        this.recomendacao = recomendacao;
        this.fkEmpresa = fkEmpresa;
        this.conexao = conexao;
        this.con = con;
    }

    public Integer getIdRecomedacoes() {
        return idRecomedacoes;
    }

    public void setIdRecomedacoes(Integer idRecomedacoes) {
        this.idRecomedacoes = idRecomedacoes;
    }

    public String getUnidadeFederativa() {
        return unidadeFederativa;
    }

    public void setUnidadeFederativa(String unidadeFederativa) {
        this.unidadeFederativa = unidadeFederativa;
    }

    public String getRecomendacao() {
        return recomendacao;
    }

    public void setRecomendacao(String recomendacao) {
        this.recomendacao = recomendacao;
    }

    public Integer getFkEmpresa() {
        return fkEmpresa;
    }

    public void setFkEmpresa(Integer fkEmpresa) {
        this.fkEmpresa = fkEmpresa;
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

    public String criarTabelaRecomendacoesIA() {
        String sql = "CREATE TABLE IF NOT EXISTS recomendacoesIA (\n" +
                "idRecomendacoes INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "unidadeFederativa VARCHAR(50) NOT NULL,\n" +
                "recomendacao VARCHAR(100) NOT NULL,\n" +
                "fkEmpresa INT,\n" +
                "CONSTRAINT fk_empresa FOREIGN KEY(fkEmpresa) REFERENCES empresa(idEmpresa)\n" +
                ")AUTO_INCREMENT=1000;";
        return sql;
    }

}
