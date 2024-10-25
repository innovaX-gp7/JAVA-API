package school.sptech;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import school.sptech.s3.BucketController;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //        INFORMAÇÕES PRA TER NO LOG:
        //        DESCRIÇÃO, DATA_HORA, (fkempresa=null(dados-geral)), (fkempresa(especifica(dados-nao-geral)))
        var formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        var horaDataAtual = LocalDateTime.now();
        var horaDataAtualFormatada = formatador.format(horaDataAtual);
        System.out.println(horaDataAtualFormatada);



        // Inicializando a conexão com o banco
        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();

        // Criando objetos para serem usados posteriormente
        Empresa empresa = new Empresa();
        Funcionario funcionario = new Funcionario();
        Dados dados = new Dados();
        ParametrosRecomendacoes parametrosRecomendacoes = new ParametrosRecomendacoes();
        RecomendacoesIA recomendacoesIA = new RecomendacoesIA();
        LogsJAR logsJAR = new LogsJAR();
        PromptIA promptIA = new PromptIA();

        try {
            // Aqui estamos tentando executar os métodos para criação de tabelas
            con.execute(empresa.criarTabelaEmpresa());
            con.execute(funcionario.criarTabelaFuncionario());
            con.execute(dados.criarTabelaDados());
            con.execute(recomendacoesIA.criarTabelaRecomendacoesIA());
            con.execute(parametrosRecomendacoes.criarTabelaParametrosRecomendacoes());
            con.execute(logsJAR.criarTabelaLogsJAR());
            con.execute(promptIA.criarTabelaPromptIA());

            System.out.println("Tabelas criadas com sucesso!");
        } catch (DataAccessException e) {
            // Esse bloco de código só será executado caso a tentativa tenha alguma exceção
            System.err.println("Erro ao criar as tabelas: " + e.getMessage());
        }


        String sqlText = ("insert into logsJAR(descricao, dataHora) values ('%s','%s')");
        //criou bucket, baixar arquivos bucket, dados inseridos

//        View S3
        BucketController bucketController = new BucketController(); //CRIAR CONTROLE PRO S3
        List<Bucket> buckets = bucketController.listarBuckets(); //Listar buckets (um, nesse caso)

        try {
            System.out.println(String.format(sqlText, "Buckets listados", horaDataAtualFormatada));
            con.execute(String.format(sqlText, "Buckets listados", horaDataAtualFormatada));
        } catch (Exception e) {
            e.printStackTrace();
        }





        if (buckets == null || buckets.isEmpty()) {
            bucketController.createBucket("innovaxs3");

            try {
                System.out.println(String.format(sqlText, "Bucket criado", horaDataAtualFormatada));
                con.execute(String.format(sqlText, "Bucket criado", horaDataAtualFormatada));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }
        for (Bucket bucket : buckets) {
            List<S3Object> objects = bucketController.listarObjetos(bucket.name()); //LISTAR ARQUIVOS DO BUCKET

            try {
                System.out.println(String.format(sqlText, "Arquivos do bucket listados", horaDataAtualFormatada));
                con.execute(String.format(sqlText, "Arquivos do bucket listados", horaDataAtualFormatada));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (objects != null) {
                bucketController.baixarObjetos(objects, bucket.name());
                try {
                    System.out.println(String.format(sqlText, "Arquivos do bucket baixados", horaDataAtualFormatada));
                    con.execute(String.format(sqlText, "Arquivos do bucket baixados", horaDataAtualFormatada));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }








//        Tratamento de dados
//        Está inacabado daqui para baixo
        File diretorio = new File("data");
        for (File arquivo : diretorio.listFiles()) {
            Boolean arquivoTemperatura = false;
            try {
                InputStream streamArquivo = Files.newInputStream(arquivo.toPath());
                Workbook workbook = new XSSFWorkbook(streamArquivo);
                Sheet sheet = workbook.getSheetAt(0);
                if(sheet.getRow(0).getCell(0).getStringCellValue().equalsIgnoreCase("Nome")){
                    arquivoTemperatura = true;
                    System.out.println("Arquivo é de temperatura");
                }
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if(arquivoTemperatura && row.getRowNum() == 0 && cell.getColumnIndex() == 1){
                            System.out.println(cell.getStringCellValue());
                        }
                        if(arquivoTemperatura && row.getRowNum() > 11){
                            System.out.print(cell.getStringCellValue() + ", ");
                        } else {

                        }
                    }
//                    System.out.println();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}