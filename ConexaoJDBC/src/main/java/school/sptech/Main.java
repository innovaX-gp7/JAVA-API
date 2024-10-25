package school.sptech;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import school.sptech.s3.BucketController;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
        // Map para armazenar os dados agrupados por UF
        Map<String, List<String>> ufCidadesMap = new HashMap<>();
        // Map para armazenar a soma das áreas desmatadas por UF e data
        Map<String, Double> desmatamentoMap = new HashMap<>();

        File diretorio = new File("data"); // Atualize o caminho do diretório

        for (File arquivo : diretorio.listFiles()) {
            try {
                InputStream streamArquivo = Files.newInputStream(arquivo.toPath());
                Workbook workbook = new XSSFWorkbook(streamArquivo);
                Sheet sheet = workbook.getSheetAt(0);

                // Verifica se o arquivo é de temperatura pela célula "Nome"
                if (getCellValue(sheet.getRow(0).getCell(0)).equalsIgnoreCase("Nome")) {
                    System.out.println("Arquivo é de temperatura");

                    // Pega o nome da cidade da célula B1 (linha 0, coluna 1)
                    String cidade = getCellValue(sheet.getRow(0).getCell(1)).trim();

                    // Chama o método getUF para pegar a UF da cidade
                    String uf = CidadeToUF.getUF(cidade); // Implementação anterior do método CidadeToUF

                    // Verifica se a UF foi encontrada
                    if (uf != null) {
                        System.out.println("Cidade: " + cidade + ", UF: " + uf);

                        // Adiciona a cidade ao mapa de sua respectiva UF
                        ufCidadesMap.computeIfAbsent(uf, k -> new ArrayList<>()).add(cidade);
                    } else {
                        System.out.println("Cidade não encontrada.");
                    }

                    // Processa os dados a partir da linha 11
                    for (Row row : sheet) {
                        if (row.getRowNum() >= 11) {  // Processa apenas as linhas a partir da 11
                            String[] valores = getCellValue(row.getCell(0)).split(";");
                            String data = valores[0];  // Data de medição
                            String precipitacaoMensal = valores[1]; // Precipitação total mensal
                            String temperaturaMediaMensal = valores[2]; // Temperatura média mensal

                            // Corrige o formato de precipitação
                            if (precipitacaoMensal.startsWith(",")) {
                                precipitacaoMensal = "0" + precipitacaoMensal;  // Adiciona o zero antes da vírgula
                            }

                            // Exemplo de saída dos dados
                            System.out.println("Data: " + data + ", Precipitação: " + precipitacaoMensal + ", Temperatura Média: " + temperaturaMediaMensal);

                        }
                    }
                } else {
                    System.out.println("Arquivo é de desmatamento");

                    // Itera pelas linhas da planilha
                    for (Row row : sheet) {
                        if (row.getRowNum() >= 1) {  // Ignora o cabeçalho

                            // Pega os valores das colunas
                            String year = getCellValue(row.getCell(0)); // "2020/2021"
                            String month = getCellValue(row.getCell(1));
                            String areaStr = getCellValue(row.getCell(2));
                            double area = Double.parseDouble(areaStr);  // Converte para double
                            String uf = getCellValue(row.getCell(3));  // Obtém a UF da coluna D (índice 3)

                            // Remove o ".0" do mês convertendo para int
                            int mesInt = (int) Double.parseDouble(month);

                            // Separar o range de anos
                            String[] anos = year.split("/");  // ["2020", "2021"]

                            // Verifica se o mês é de 1 a 6 (usar o segundo ano) ou 7 a 12 (usar o primeiro ano)
                            String anoFinal;
                            if (mesInt >= 1 && mesInt <= 6) {
                                anoFinal = anos[1];  // Segundo ano
                            } else {
                                anoFinal = anos[0];  // Primeiro ano
                            }

                            // Armazena o objeto Desmatamento
                            String chaveComposta = uf + ":" + anoFinal + "/" + mesInt;  // "AM:2020-1"

                            // Soma a área desmatada para a chave correspondente
                            desmatamentoMap.put(chaveComposta, desmatamentoMap.getOrDefault(chaveComposta, 0.0) + area);
                        }
                    }
                }
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        // Exibe as cidades agrupadas por UF
        System.out.println("\nCidades agrupadas por UF:");
        for (Map.Entry<String, List<String>> entry : ufCidadesMap.entrySet()) {
            String uf = entry.getKey();
            List<String> cidades = entry.getValue();
            System.out.println("UF: " + uf + " - Cidades: " + cidades);
        }
        // Exibe os resultados de desmatamento por UF e data
        System.out.println("\nÁrea total desmatada por UF e data:");
        for (Map.Entry<String, Double> entry : desmatamentoMap.entrySet()) {
            String chave = entry.getKey();
            double areaTotal = entry.getValue();
            System.out.printf("UF/Data: %s, Área Total Desmatada: %.2f km²\n", chave, areaTotal);
        }
    }

    // Função auxiliar para pegar o valor da célula como String
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";  // Se a célula estiver vazia
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();  // Se for uma data, formata corretamente
                } else {
                    return String.valueOf(cell.getNumericCellValue());  // Converte para string numérica
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }

    }
}