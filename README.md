# Projeto Innovax 🌳
## Bem vindo ao nosso JAVA-API! ☕
Somos a Innovax, uma equipe de desenvolvedores com o intuito de ajudar a corrigir o desmatamento da floresta amazônica, utilizando tecnologias modernas como **Apache POI** e **AWS** para o desenvolvimento do nosso projeto. 

Para fazer um projeto rodar, precisa de um back-end de peso, não é mesmo? Venha conferir!

## Primeiro passo
O primeiro passo é clonar o repositório. Para isso, abriremos uma pasta local para colocar o nosso projeto.
Depois, abriremos o **Git Bash** dentro da pasta e clonamos o repositório `https://github.com/innovaX-gp7/JAVA-API.git`

## Como foi o processo?

Nós solicitamos a base de dados ao nosso cliente, e colocamos essa base de dados no *S3* da *AWS*, como _.xlsx_ e a tratamos com a biblioteca do Java, utilizando **Apache POI**.

Assim, nós conseguimos inserir os dados no banco, para mostrar na Dashboard, posteriormente.

## Configurações necessárias ⚙
> O back-end todo do projeto está na pasta ConexãoJDBC
### Configuração do S3 
Primeiramente, temos que ir na **AWS**, e iniciar o laboratório. Assim que iniciamos, podemos ir em **AWS Details** e visualizar as nossas variáveis de ambiente. 

Para utilizá-las, precisamos ir para a classe `src/main/java/school/sptech/s3/S3Provider.java` e mudar as nossas variáveis de ambiente.

```
System.getenv("AWS_ACCESS_KEY_ID"),
System.getenv("AWS_SECRET_ACCESS_KEY"),
System.getenv("AWS_SESSION_TOKEN")
```

Além disso, precisamos ir na barra de pesquisar do _Windows_, e digitar **Variáveis de Ambiente**. Agora, podemos clicar em:

`Editar as variáveis de ambiente do sistema > Avançado >  Variáveis de Ambiente > Variáveis do Sistema > Novo`

**Agora, dessa forma, as variáveis de ambiente estão certamente configuradas no sistema.**

### Configuração do Banco de Dados

Precisamos ir a classe `src/main/java/school/sptech/Conexao.java` e mudar as configurações de acordo com o seu dispositivo local:
```
dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
dataSource.setUrl("jdbc:mysql://mysql-container:3306/InnovaxDB"); 
dataSource.setUsername("root"); 
dataSource.setPassword("Urubu100"); 
```

## Últimos passos
Com a base de dados tratada no `Main.java`, podemos executar o ***Run*** e ver a mágica acontecer!
> Sim, agora os dados estão sendo inseridos diretamente ao banco de dados! 🎲

Boa! Agora é só aproveitar tudo que a Innovax tem para oferecer! 🤗🌱
