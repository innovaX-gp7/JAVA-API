package school.sptech;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import school.sptech.s3.BucketController;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //        INFORMAÇÕES PRA TER NO LOG:
        //        DESCRIÇÃO, DATA_HORA, (fkempresa=null(dados-geral)), (fkempresa(especifica(dados-nao-geral)))
        var formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        var horaDataAtual = LocalDateTime.now();
        var horaDataAtualFormatada = formatador.format(horaDataAtual);
        System.out.println(horaDataAtualFormatada);
        var caminhoDoLog = "log.txt";
        String bucketName = "innovaxs3";
        try {


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


            String derrubarDados = "drop table if exists dados;";


            try {
                // Aqui estamos tentando executar os métodos para criação de tabelas
                con.execute(derrubarDados);
                System.out.println("Dropou");
                registrarLog(caminhoDoLog, "Dropou", horaDataAtualFormatada);

                con.execute(empresa.criarTabelaEmpresa());
                con.execute(funcionario.criarTabelaFuncionario());
                con.execute(dados.criarTabelaDados());
                con.execute(recomendacoesIA.criarTabelaRecomendacoesIA());
                con.execute(parametrosRecomendacoes.criarTabelaParametrosRecomendacoes());
                con.execute(logsJAR.criarTabelaLogsJAR());
                con.execute(promptIA.criarTabelaPromptIA());

                System.out.println("Tabelas criadas com sucesso!");
                registrarLog(caminhoDoLog, "Tabelas criadas com sucesso!", horaDataAtualFormatada);
            } catch (DataAccessException e) {
                // Esse bloco de código só será executado caso a tentativa tenha alguma exceção
                System.err.println("Erro ao criar as tabelas: " + e.getMessage());
                registrarLog(caminhoDoLog, "Erro ao criar as tabelas", horaDataAtualFormatada);
            }


            String sqlText = ("insert into logsJAR(descricao, dataHora) values ('%s','%s')");
            //criou bucket, baixar arquivos bucket, dados inseridos

//        View S3
            BucketController bucketController = new BucketController(); //CRIAR CONTROLE PRO S3
            List<Bucket> buckets = bucketController.listarBuckets(); //Listar buckets (um, nesse caso)

            try {
                registrarLog(caminhoDoLog, "Buckets listados", horaDataAtualFormatada);
                System.out.println(String.format(sqlText, "Buckets listados", horaDataAtualFormatada));
                con.execute(String.format(sqlText, "Buckets listados", horaDataAtualFormatada));
            } catch (Exception e) {
                System.err.println("Erro ao listar buckets: " + e.getMessage());
                registrarLog(caminhoDoLog, "Erro ao listar buckets", horaDataAtualFormatada);
            }


            if (buckets == null || buckets.isEmpty()) {
                bucketController.createBucket("innovaxs3");

                try {
                    registrarLog(caminhoDoLog, "Bucket criado", horaDataAtualFormatada);
                    System.out.println(String.format(sqlText, "Bucket criado", horaDataAtualFormatada));
                    con.execute(String.format(sqlText, "Bucket criado", horaDataAtualFormatada));
                } catch (Exception e) {
                    System.err.println("Erro ao criar buckets: " + e.getMessage());
                    registrarLog(caminhoDoLog, "Erro ao criar bucket: ", horaDataAtualFormatada);
                }


            }
            for (Bucket bucket : buckets) {
                List<S3Object> objects = bucketController.listarObjetos(bucket.name()); //LISTAR ARQUIVOS DO BUCKET

                try {
                    con.execute(String.format(sqlText, "Arquivos do bucket listados", horaDataAtualFormatada));
                    registrarLog(caminhoDoLog, "Arquivos do bucket listados", horaDataAtualFormatada);
                    System.out.println(String.format(sqlText, "Arquivos do bucket listados", horaDataAtualFormatada));
                } catch (Exception e) {
                    System.err.println("Erro ao listar arquivos do buckets: " + e.getMessage());
                    registrarLog(caminhoDoLog, "Erro ao listar arquivos do bucket", horaDataAtualFormatada);
                }

                if (objects != null) {
                    bucketController.baixarObjetos(objects, bucket.name());
                    try {
                        registrarLog(caminhoDoLog, "Arquivos do bucket baixados", horaDataAtualFormatada);
                        System.out.println(String.format(sqlText, "Arquivos do bucket baixados", horaDataAtualFormatada));
                        con.execute(String.format(sqlText, "Arquivos do bucket baixados", horaDataAtualFormatada));
                    } catch (Exception e) {
                        System.err.println("Erro ao baixar arquivos do bucket: " + e.getMessage());
                        registrarLog(caminhoDoLog, "Erro ao baixar arquivos do bucket", horaDataAtualFormatada);
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
                                String precipitacaoMensal = valores[1].replace(",", ".");
                                String temperaturaMediaMensal = valores[2].replace(",", "."); // Temperatura média mensal

                                if (valores[1].isEmpty() || valores[1] == null) {
                                    precipitacaoMensal = null;
                                }
                                if (valores[2].isEmpty() || valores[2] == null) {
                                    temperaturaMediaMensal = null;
                                }

                                LocalDate dataFormatada = LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                int ano = dataFormatada.getYear();
                                int mes = dataFormatada.getMonthValue();
                                // Corrige o formato de precipitação
                                if (precipitacaoMensal.startsWith(",")) {
                                    precipitacaoMensal = "0" + precipitacaoMensal;  // Adiciona o zero antes da vírgula
                                }

                                // Exemplo de saída dos dados
//                                System.out.println("Data: " + data + ", Precipitação: " + precipitacaoMensal + ", Temperatura Média: " + temperaturaMediaMensal);


                                // Inserindo dados no banco
                                con.update(dados.inserirDados(temperaturaMediaMensal, precipitacaoMensal, cidade, uf, ano, mes));
                                System.out.println("Insert de precipitação deu certo!");
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

                                con.update(dados.inserirDadosDesmatamentos(uf, anoFinal, mesInt, area));

                            }
                        }
                        System.out.println("Insert de desmatamento deu certo!");
                    }
                    workbook.close();

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
            try {
            // Log após o processamento de todos os arquivos
                registrarLog(caminhoDoLog, "Arquivos manipulados", horaDataAtualFormatada);
                System.out.println(String.format(sqlText, "Arquivos manipulados", horaDataAtualFormatada));
                con.execute(String.format(sqlText, "Arquivos manipulados", horaDataAtualFormatada));
            }catch (Exception e){
                System.err.println(e.getMessage());
            }


            // Exibe as cidades agrupadas por UF
//            System.out.println("\nCidades agrupadas por UF:");
//            for (Map.Entry<String, List<String>> entry : ufCidadesMap.entrySet()) {
//                String uf = entry.getKey();
//                List<String> cidades = entry.getValue();
//                System.out.println("UF: " + uf + " - Cidades: " + cidades);
//            }
            // Exibe os resultados de desmatamento por UF e data
//            System.out.println("\nÁrea total desmatada por UF e data:");
//            for (Map.Entry<String, Double> entry : desmatamentoMap.entrySet()) {
//                String chave = entry.getKey();
//                double areaTotal = entry.getValue();
//                System.out.printf("UF/Data: %s, Área Total Desmatada: %.2f km²\n", chave, areaTotal);
//            }

            // Upload do log para o S3 após todo o processamento
            try {
                // Gerar nome do novo arquivo
                String horaAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
                String s3Key = String.format("logs/%s-log.txt", horaAtual);

                // Listar arquivos existentes no bucket/pasta logs
                ListObjectsV2Response listResponse = bucketController.listarObjetosPorPath(bucketName, "logs/");

                // Excluir arquivos antigos
                for(S3Object s3Object : listResponse.contents()) {
                    if(!s3Object.key().equals(s3Key)) {
                        bucketController.deletarArquivoPorNome(bucketName,s3Object.key());
                        System.out.println("Log antigo excluído: " + s3Object.key());
                    }
                }

                // Upload do novo arquivo
                bucketController.enviarArquivo(bucketName, s3Key, caminhoDoLog);
                System.out.println("Log enviado com sucesso para o S3: " + s3Key);
            } catch (Exception e) {
                System.err.println("Erro ao fazer o upload do log para o s3: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Erro durante o processamento: " + e.getMessage());
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

    private static void registrarLog(String caminhoDoLog, String descricao, String horaDataAtualFormatada) {
        String entradaLog = String.format("[%s] %s", horaDataAtualFormatada, descricao);
        System.out.println(entradaLog);

        try (FileWriter writer = new FileWriter(caminhoDoLog, true)){
            writer.write(entradaLog + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo de log: " + e.getMessage());
        }

    }
}
