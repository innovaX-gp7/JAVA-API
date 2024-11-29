package school.sptech;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import school.sptech.s3.BucketController;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        var formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        var horaDataAtual = LocalDateTime.now();
        var horaDataAtualFormatada = formatador.format(horaDataAtual);

        var caminhoDoLog = "log.txt";
        Slack slack = new Slack();
        String bucketName = System.getenv("BUCKET_NAME");
        StringBuilder logText = new StringBuilder();

        String sqlText = ("insert into logJAR(descricao, created_at) values ('%s','%s')");

        try {
            // Inicializando a conexão com o banco
            Conexao conexao = new Conexao();
            JdbcTemplate con = conexao.getConexaoDoBanco();

            // Criando objetos para serem usados posteriormente
            CriacaoDeTabelas tabelas = new CriacaoDeTabelas();
            App mensagem = new App();

            // Validador de conexão
            Boolean conexaoOk = true;

            String derrubarDados = "drop table if exists dados;";

            try {
                // Aqui estamos a tentar executar os métodos para criação de tabelas
                con.execute(derrubarDados);

                tabelas.criarTabelaEmpresa();
                tabelas.criarTabelaUserRole();
                tabelas.criarTabelaUsuario();
                tabelas.criarTabelaPromptIA();
                tabelas.criarTabelaRecomendacaoIA();
                tabelas.criarTabelaTipoParametro();
                tabelas.criarTabelaParametroRecomendacao();
                tabelas.criarTabelaLogJAR();
                tabelas.criarTabelaLeitura();

                horaDataAtualFormatada = formatador.format(LocalDateTime.now());
                registrarLog(logText, "Tabelas Criadas", horaDataAtualFormatada);
            } catch (DataAccessException e) {
                // Esse bloco de código só será executado caso a tentativa tenha alguma exceção
                conexaoOk = false;
                String errorMensage = "Erro ao criar as tabelas: " + e.getMessage();
                horaDataAtualFormatada = formatador.format(LocalDateTime.now());
                registrarLog(logText, errorMensage, horaDataAtualFormatada);
            }

            // View S3
            BucketController bucketController;
            try{
                bucketController = new BucketController(); //CRIAR CONTROLE PRO S3
            }catch (Exception e){
                System.out.println("Erro ao conectar com o s3: " + e.getMessage());
                return;
            }

            List<Bucket> buckets = new ArrayList<>();
            try {
                buckets = bucketController.listarBuckets(); //Listar buckets (um, nesse caso)
                horaDataAtualFormatada = formatador.format(LocalDateTime.now());
                if(conexaoOk){con.execute(String.format(sqlText, "Bucket S3 foi detectado", horaDataAtualFormatada));}
                registrarLog(logText, "Bucket S3 foi detectado", horaDataAtualFormatada);
            } catch (Exception e) {
                horaDataAtualFormatada = formatador.format(LocalDateTime.now());
                String errorMensage = "Erro ao localizar bucket: " + e.getMessage();
                if(conexaoOk){con.execute(String.format(sqlText, errorMensage, horaDataAtualFormatada));}
                registrarLog(logText, errorMensage, horaDataAtualFormatada);
            }

            for (Bucket bucket : buckets) {
                List<S3Object> objects = bucketController.listarObjetos(bucket.name()); //LISTAR ARQUIVOS DO BUCKET

                try {
                    horaDataAtualFormatada = formatador.format(LocalDateTime.now());
                    if(conexaoOk){con.execute(String.format(sqlText, "Arquivos do bucket foram detectados", horaDataAtualFormatada));}
                    registrarLog(logText, "Arquivos do bucket foram detectados", horaDataAtualFormatada);
                } catch (Exception e) {
                    String errorMensage = "Erro ao localizar arquivos do buckets: " + e.getMessage();
                    registrarLog(logText, errorMensage, horaDataAtualFormatada);
                    if(conexaoOk){con.execute(String.format(sqlText, errorMensage, horaDataAtualFormatada));}
                }

                if (objects != null) {
                    bucketController.baixarObjetos(objects, bucket.name());
                    try {
                        registrarLog(logText, "Arquivos do bucket foram baixados", horaDataAtualFormatada);
                        if(conexaoOk){con.execute(String.format(sqlText, "Arquivos do bucket foram baixados", horaDataAtualFormatada));}
                    } catch (Exception e) {
                        String errorMensage = "Erro ao baixar arquivos do bucket" + e.getMessage();
                        registrarLog(logText, errorMensage, horaDataAtualFormatada);
                        if(conexaoOk){con.execute(String.format(sqlText, errorMensage, horaDataAtualFormatada));}
                    }
                }
            }

            if(!conexaoOk) {
                horaDataAtualFormatada = formatador.format(LocalDateTime.now());
                String mensage = "A conexao com o banco de dados falhou. Por isso, o tratamento dos arquivos nao sera realizado.";
                registrarLog(logText, mensage, horaDataAtualFormatada);
            } else {
                // Tratamento de dados
                // Map para armazenar os dados agrupados por UF
                Map<String, List<String>> ufCidadesMap = new HashMap<>();
                // Map para armazenar a soma das áreas desmatadas por UF e data
                Map<String, Double> desmatamentoMap = new HashMap<>();

                File diretorio = new File("data"); // Atualize o caminho do diretório
                if (diretorio.listFiles() == null || diretorio.length() == 0) {
                    horaDataAtualFormatada = formatador.format(LocalDateTime.now());
                    registrarLog(logText, "Nenhum arquivo foi encontrado", horaDataAtualFormatada);
                } else {
                    for (File arquivo : diretorio.listFiles()) {
                        try {
                            InputStream streamArquivo = Files.newInputStream(arquivo.toPath());
                            Workbook workbook = new XSSFWorkbook(streamArquivo);
                            Sheet sheet = workbook.getSheetAt(0);

                            horaDataAtualFormatada = formatador.format(LocalDateTime.now());
                            String mensage = "Processando arquivo " + arquivo.getName();
                            registrarLog(logText, mensage, horaDataAtualFormatada);
                            con.execute(String.format(sqlText, mensage, horaDataAtualFormatada));
                            // Verifica se o arquivo é de temperatura pela célula "Nome"
                            if (getCellValue(sheet.getRow(0).getCell(0)).equalsIgnoreCase("Nome")) {

                                // Pega o nome da cidade da célula B1 (linha 0, coluna 1)
                                String cidade = getCellValue(sheet.getRow(0).getCell(1)).trim();

                                // Chama o método getUF para pegar a UF da cidade
                                String uf = CidadeToUF.getUF(cidade); // Implementação anterior do método CidadeToUF

                                // Verifica se a UF foi encontrada
                                if (uf != null) {
//                            System.out.println("Cidade: " + cidade + ", UF: " + uf);

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

                                        // Inserindo dados no banco
                                        con.update(InsercaoTabelas.inserirDados(temperaturaMediaMensal, precipitacaoMensal, cidade, uf, ano, mes));
                                    }
                                }
                                horaDataAtualFormatada = formatador.format(LocalDateTime.now());
                                mensage = "O arquivo " + arquivo.getName() + " foi registrado";
                                registrarLog(logText, mensage, horaDataAtualFormatada);
                                con.execute(String.format(sqlText, mensage, horaDataAtualFormatada));
                            } else {
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

                                        con.update(InsercaoTabelas.inserirDadosDesmatamentos(uf, anoFinal, mesInt, area));

                                    }
                                }
                                horaDataAtualFormatada = formatador.format(LocalDateTime.now());
                                mensage = "O arquivo " + arquivo.getName() + " foi registrado";
                                registrarLog(logText, mensage, horaDataAtualFormatada);
                                con.execute(String.format(sqlText, mensage, horaDataAtualFormatada));
                            }
                            workbook.close();

                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
                try {
                    // Log após o processamento de todos os arquivos
                    horaDataAtualFormatada = formatador.format(LocalDateTime.now());
                    registrarLog(logText, "Todos os arquivos foram registrados", horaDataAtualFormatada);
                    con.execute(String.format(sqlText, "Todos os arquivos foram registrados", horaDataAtualFormatada));
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }

            // Upload do log para o S3 após todo o processamento
            try {
                // Gerar nome do novo arquivo
                String horaAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss"));
                String s3Key = String.format("logs/%s.log", horaAtual);

                try (FileWriter writer = new FileWriter(caminhoDoLog)){
                    writer.write(logText + System.lineSeparator());
                } catch (IOException e) {
                    System.err.println("Erro ao escrever no arquivo de log: " + e.getMessage());
                }

                // Upload do novo arquivo
                bucketController.enviarArquivo(bucketName, s3Key, caminhoDoLog);
                System.out.println("Log enviado com sucesso para o S3: " + s3Key);

            } catch (Exception e) {
                System.err.println("Erro ao fazer o upload do log para o s3: " + e.getMessage());
            }

            con.execute(mensagem.enviarMensagemSlack());
            con.execute(mensagem.enviarRelatorioMes());
            con.execute(mensagem.enviarAvisoNovaRecomendacao());


        } catch (Exception e) {
            e.printStackTrace();
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

    private static void registrarLog(StringBuilder logText, String descricao, String horaDataAtualFormatada) {
        String entradaLog = String.format("[%s] %s\n", horaDataAtualFormatada, descricao);
        System.out.print(entradaLog);
        logText.append(entradaLog);
    }
}
