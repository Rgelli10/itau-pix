### Projeto de Cadastro de Chaves PIX

## Descrição do Projeto
Este projeto é uma aplicação Java que implementa o sistema de chaves PIX, seguindo as diretrizes do Banco Central do Brasil. A aplicação permite o cadastro, consulta, alteração, inativação e deleção de chaves PIX para diferentes tipos de contas e correntistas.
## Tecnologias Utilizadas
- **Linguagem**: Java
- **Banco de Dados**: MongoDB
- **Framework**: Spring Boot
- **Arquitetura**: MVC (Model-View-Controller)
- **API**: RESTful
- **Validações**: Classes Validation
- **Gerenciamento de Dependências**: Maven

## Padrões de Design Utilizados
Estrutura do Projeto

- **src/main/java**: Contém o código-fonte da aplicação.
- **com.itau.pix**: Pacote principal da aplicação.
- **controller**: Contém os controladores REST.
- **service**: Contém as classes de serviço que implementam a lógica de negócio.
- **validation**: Contém as classes de validação que garantem a integridade dos dados.
- **repository**: Contém os repositórios que interagem com o banco de dados MongoDB.
- **model**: Contém as classes que representam os modelos de dados.
- **dto**: Contém as classes Data Transfer Objects (DTOs) utilizadas para transferir dados entre as camadas.
- **exception**: Contém as classes de exceção personalizadas.
- **config**: Contém as classes de configuração da aplicação.
- **src/main/resources**: Contém arquivos de configuração da aplicação.
- **application.properties**: Arquivo de configuração do Spring Boot.
- **logback-spring.xml**: Arquivo de configuração do Logback para gerenciamento de logs.
- **src/test/java**: Contém os testes unitários da aplicação.

## Funcionalidades Implementadas
### Inclusão de Chaves PIX
- Permite a inclusão de diferentes tipos de chaves: celular, e-mail, CPF, CNPJ e chave aleatória.
- Limite de 5 chaves por conta para pessoas físicas e 20 chaves para pessoas jurídicas.
- Geração de um código de registro único (UUID) para cada chave registrada.
- Validação de duplicidade e formato das chaves conforme regras específicas.

### Alteração de Chaves PIX
- Permite a alteração dos dados associados a uma chave registrada, exceto o próprio valor da chave.
- Validação de campos obrigatórios e regras de negócio.

### Deleção de Chaves PIX
- Inativa uma chave registrada, impedindo sua alteração ou consulta futura.
- Registra a data e hora da inativação.

### Consulta de Chaves PIX
- Disponibiliza consulta por ID, tipo de chave, agência e conta, nome do correntista, data de inclusão e data de inativação.
- Implementação de filtros combinados para consultas mais precisas.

## Como Executar o Projeto
1. Clone o repositório: `git clone https://github.com/seu-usuario/seu-repositorio.git`
2. Navegue até o diretório do projeto: `cd seu-repositorio`
3. Compile o projeto: `mvn clean install`
4. Execute a aplicação: `mvn spring-boot:run`
5. Acesse a API através do Postman ou qualquer cliente HTTP: `http://localhost:8080//api/v1/pix`

## Testes
- Os testes unitários estão localizados no diretório `src/test/java/br/com/itau/pix`.
- A cobertura de testes é de 97%.

## Contato
- Nome: Ricardo Gelli
- E-mail: ricardo.gelli@hotmail.com