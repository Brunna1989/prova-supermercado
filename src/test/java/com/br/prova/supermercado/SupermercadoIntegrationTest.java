package com.br.prova.supermercado;

import com.br.prova.supermercado.dto.ProdutoDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SupermercadoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        entityManager.createQuery("DELETE FROM Item").executeUpdate();
        entityManager.createQuery("DELETE FROM Pedido").executeUpdate();
        entityManager.createQuery("DELETE FROM Produto").executeUpdate();
        entityManager.createQuery("DELETE FROM Estoque").executeUpdate();
    }

    @Test
    void criarEstoqueComCorpo() throws Exception {
        String estoqueJson = "{ \"nome\": \"Estoque Novo\" }";

        mockMvc.perform(post("/api/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estoqueJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome", is("Estoque Novo")));
    }

    @Test
    void criarProdutoComEstoque() throws Exception {
        String estoqueJson = "{ \"nome\": \"Estoque Teste\" }";
        MvcResult estoqueResult = mockMvc.perform(post("/api/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estoqueJson))
                .andExpect(status().isOk())
                .andReturn();
        Long estoqueId = objectMapper.readTree(estoqueResult.getResponse().getContentAsString()).get("id").asLong();

        ProdutoDTO produtoDTO = ProdutoDTO.builder()
                .nome("Arroz")
                .preco(10.0)
                .quantidadeEmEstoque(20)
                .estoqueId(estoqueId)
                .build();

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome", is("Arroz")));
    }

    @Test
    void listarProdutos() throws Exception {
        String estoqueJson = "{ \"nome\": \"Estoque Listar\" }";
        MvcResult estoqueResult = mockMvc.perform(post("/api/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estoqueJson))
                .andExpect(status().isOk())
                .andReturn();
        Long estoqueId = objectMapper.readTree(estoqueResult.getResponse().getContentAsString()).get("id").asLong();

        ProdutoDTO dto = ProdutoDTO.builder()
                .nome("Feijão")
                .preco(8.0)
                .quantidadeEmEstoque(30)
                .estoqueId(estoqueId)
                .build();

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Feijão")));
    }

    @Test
    void buscarProdutoPorId() throws Exception {
        String estoqueJson = "{ \"nome\": \"Estoque Buscar\" }";
        MvcResult estoqueResult = mockMvc.perform(post("/api/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estoqueJson))
                .andExpect(status().isOk())
                .andReturn();
        Long estoqueId = objectMapper.readTree(estoqueResult.getResponse().getContentAsString()).get("id").asLong();

        ProdutoDTO dto = ProdutoDTO.builder()
                .nome("Macarrão")
                .preco(5.5)
                .quantidadeEmEstoque(15)
                .estoqueId(estoqueId)
                .build();

        MvcResult produtoResult = mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode produtoJson = objectMapper.readTree(produtoResult.getResponse().getContentAsString());
        Long produtoId = produtoJson.get("id").asLong();

        mockMvc.perform(get("/api/produtos/" + produtoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Macarrão")));
    }

    @Test
    void listarProdutosDoEstoque() throws Exception {
        // Criar estoque e produto
        String estoqueJson = "{ \"nome\": \"Estoque Produtos\" }";
        MvcResult estoqueResult = mockMvc.perform(post("/api/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estoqueJson))
                .andExpect(status().isOk())
                .andReturn();
        Long estoqueId = objectMapper.readTree(estoqueResult.getResponse().getContentAsString()).get("id").asLong();

        ProdutoDTO dto = ProdutoDTO.builder()
                .nome("Café")
                .preco(12.0)
                .quantidadeEmEstoque(10)
                .estoqueId(estoqueId)
                .build();

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/estoques/" + estoqueId + "/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Café")));
    }
}
