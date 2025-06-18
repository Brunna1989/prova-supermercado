# Supermercado API

Sistema de gerenciamento de supermercado, com controle de estoques, produtos, itens e pedidos. Desenvolvido em Java com Spring Boot, persistência em banco H2 e arquitetura orientada a testes.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.5**
    - Spring Web
    - Spring Data JPA
- **Hibernate 6.4.4**
- **Banco de Dados H2 (em memória)**
- **Lombok**
- **MapStruct**
- **Maven**
- **JUnit 5** (testes unitários e de integração)
- **Mockito** (mock de dependências nos testes)

## Como Funciona

O sistema permite:

- **Gerenciar Estoques:** criar, buscar, listar produtos, dar baixa e excluir estoques.
- **Gerenciar Produtos:** adicionar, buscar, listar e excluir produtos vinculados a estoques.
- **Gerenciar Itens:** itens são produtos associados a pedidos.
- **Gerenciar Pedidos:** criar pedidos com múltiplos itens, calcular valor total e listar itens de um pedido.
- **Console App:** menu simples para visualizar estoques e produtos via terminal. (Inclusive ao gerar um pedido no Postman e consultar novamente o saldo do produto incluso, percebemos a baixa no estoque de forma automática.)

As entidades principais são: `Estoque`, `Produto`, `Item` e `Pedido`. O relacionamento entre elas é feito via JPA.

## Testes

### Testes Unitários

- Cobrem as regras de negócio dos serviços (`EstoqueService`, `ProdutoService`, `ItemService`, `PedidoService`).
- Utilizam Mockito para simular repositórios e dependências.
- Testam cenários de sucesso, falha, exceções e validações.

### Testes de Integração

- Utilizam Spring Boot Test e TestRestTemplate.
- Testam a integração dos endpoints REST, persistência real no banco H2 e fluxo completo de criação de pedidos, produtos e estoques.
- Exemplo: `PedidoControllerIntegrationTest` valida a criação de pedidos via API.

## Como Executar

1. **Clonar o repositório**
2. **Compilar o projeto:** mvn clean install

3. **Executar a aplicação:**  mvn spring-boot:run

4. **Acessar o H2 Console (opcional):**
    - URL: `http://localhost:8080/h2-console`
    - JDBC URL: `jdbc:h2:mem:mercado`
    - Usuário: `sa` (sem senha)

5. **Executar testes:**  mvn test

## Endpoints Principais

- `/api/estoques` - Gerenciamento de estoques
- `/api/produtos` - Gerenciamento de produtos
- `/api/itens` - Gerenciamento de itens
- `/api/pedidos` - Gerenciamento de pedidos

## 🔁 Requisições via Postman

### 🏬 Criar Estoque

**Endpoint:**  
`POST http://localhost:8080/api/estoques`

**Corpo da Requisição:**  
_Não é necessário corpo na requisição_

---

### 📦 Criar Produto

**Endpoint:**  
`POST http://localhost:8080/api/produtos`

**Corpo da Requisição (JSON):**
```json
{
  "nome": "Café Torrado",
  "preco": 15.90,
  "quantidadeEmEstoque": 100,
  "estoqueId": 1
}

````

### 🧾 Criar Pedido de Venda

**Endpoint:**
`POST http://localhost:8080/api/pedidos`

**Corpo da Requisição (JSON):**
```json
{
  "listaItens": [
    {
      "produto": {
        "id": 1
      },
      "quantidade": 2
    }
  ]
}
````
### 💵 Pagar Pedido e Calcular Troco

**Endpoint:**  
`PUT http://localhost:8080/api/pedidos/{id}/pagar`

**Corpo da Requisição (JSON):**
```json
{
  "valorPago": 50.00
}

````

## Resposta de Exemplo: ##

```json
{
  "id": 1,
  "listaItens": [
    {
      "id": 10,
      "produto": {
        "id": 1,
        "nome": "Café Torrado",
        "preco": 15.90,
        "quantidadeEmEstoque": 98,
        "estoqueId": 1
      },
      "quantidade": 2,
      "preco": 31.80
    }
  ],
  "valorTotalDoPedido": 31.80,
  "valorPago": 50.00,
  "troco": 18.20
}
```

## Observações

- O banco de dados é resetado a cada execução (H2 em memória).
- Todos os testes são independentes e autocontidos.
- O menu console pode ser executado pela classe `SupermercadoConsoleApp`.

## 🎥 Vídeo demonstrativo do funcionamento do projeto

[👉 Assista no YouTube](https://www.youtube.com/watch?v=bu9yPV5MgZ0)


---
Desenvolvido para fins acadêmicos e de demonstração.