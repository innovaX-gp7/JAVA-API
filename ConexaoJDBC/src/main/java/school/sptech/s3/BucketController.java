package school.sptech.s3;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class BucketController {
//    Instancia uma conexão com o S3
    S3Client s3Client = new S3Provider().getS3Client();

    public List<Bucket> listarBuckets(){
        List<Bucket> buckets = s3Client.listBuckets().buckets();
        return buckets;
    }

    public void createBucket(String bucketName){
        CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();
        try{
            s3Client.createBucket(createBucketRequest);
            System.out.println("Bucket Criado!");
        } catch (Exception e){
            System.out.println("Ocorreu um erro ao criar um bucket");
            System.out.println(e.getMessage());
        }
    }

    public List<S3Object> listarObjetos(String bucketName) {
//        Cria requisição para listar os objetos dentro do bucket
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(bucketName)
                .build();

//        Lista objetos do bucket usando a requisição
        List<S3Object> objects = s3Client.listObjects(listObjectsRequest).contents();
        System.out.println("Obteve Objetos:");
        return objects;
    }

    public ListObjectsV2Response listarObjetosPorPath(String bucketName, String path){
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(path)
                .build();

        return s3Client.listObjectsV2(listRequest);
    }

    public void baixarObjetos(List<S3Object> objects, String bucketName){
        for (S3Object object : objects) {
            System.out.println(object);
//            Cria requisição para baixar um objeto do bucket
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(object.key())
                    .build();

//            Captura objeto usando a requisição e tenta transformar para formato de arquivo que o java interpreta
            InputStream objectContent = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
            if(!object.key().endsWith("txt")) {
                try {
                    File arquivo = new File("data/" + object.key());
                    if (!arquivo.exists()) {
//                Copia arquivo do s3 para um arquivo local com o nome da key do objeto
                        Files.copy(objectContent, arquivo.toPath());
                        System.out.println("Copiou arquivo");
                    } else {
                        System.out.println("Arquivo já baixado");
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public void enviarArquivo(String bucketName, String fileName, String path){
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.putObject(request, RequestBody.fromFile(new File(path)));
    }

    public void deletarArquivoPorNome(String bucketName, String fileName){
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        s3Client.deleteObject(deleteRequest);
    }
}
