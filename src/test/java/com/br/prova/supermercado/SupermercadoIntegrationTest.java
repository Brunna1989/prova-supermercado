package com.br.prova.supermercado;


import com.br.prova.supermercado.dto.ProdutoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SupermercadoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long estoqueId;
    private static Long produtoId;
    private static Long pedidoId;
    private static Long itemId;

    @Test
    @Order(1)
    void criarEstoque() throws Exception {
        mockMvc.perform(post("/api/estoques"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    estoqueId = objectMapper.readTree(json).get("id").asLong();
                });
    }

    @Test
    @Order(2)
    void criarProduto() throws Exception {
        ProdutoDTO dto = ProdutoDTO.builder()
                .nome("Arroz")
                .preco(10.0)
                .quantidadeEmEstoque(20)
                .estoqueId(estoqueId)
                .build();

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    produtoId = objectMapper.readTree(json).get("id").asLong();
                });
    }

    @Test
    @Order(3)
    void listarProdutos() throws Exception {
        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Arroz")));
    }

    @Test
    @Order(4)
    void buscarProdutoPorId() throws Exception {
        mockMvc.perform(get("/api/produtos/" + produtoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Arroz")));
    }

    @Test
    @Order(5)
    void listarProdutosDoEstoque() throws Exception {
        mockMvc.perform(get("/api/estoques/" + estoqueId + "/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Arroz")));
    }

    @Test
    @Order(6)
    void darBaixaNoProduto() throws Exception {
        mockMvc.perform(post("/api/estoques/" + estoqueId + "/dar-baixa")
                        .param("nomeProduto", "Arroz")
                        .param("quantidade", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("sucesso")));
    }

    @Test
    @Order(7)
    void imprimirEstoque() throws Exception {
        mockMvc.perform(get("/api/estoques/" + estoqueId + "/imprimir"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    void criarPedido() throws Exception {
        String pedidoJson = "{ \"listaItens\": [ { \"produto\": { \"id\": " + produtoId + " }, \"quantidade\": 2, \"preco\": 20.0 } ] }";
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    pedidoId = objectMapper.readTree(json).get("id").asLong();
                });
    }

    @Test
    @Order(9)
    void listarItensDoPedido() throws Exception {
        mockMvc.perform(get("/api/pedidos/" + pedidoId + "/itens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantidade", is(2)));
    }

    @Test
    @Order(10)
    void salvarItemDiretamente() throws Exception {
        String itemJson = "{ \"produto\": { \"id\": " + produtoId + " }, \"quantidade\": 1, \"preco\": 10.0 }";
        mockMvc.perform(post("/api/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    itemId = objectMapper.readTree(json).get("id").asLong();
                });
    }

    @Test
    @Order(11)
    void listarTodosItens() throws Exception {
        mockMvc.perform(get("/api/itens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantidade").exists());
    }

    @Test
    @Order(12)
    void buscarItemPorId() throws Exception {
        mockMvc.perform(get("/api/itens/" + itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId.intValue())));
    }

    @Test
    @Order(13)
    void atualizarItem() throws Exception {
        String itemJson = "{ \"produto\": { \"id\": " + produtoId + " }, \"quantidade\": 3, \"preco\": 30.0 }";
        mockMvc.perform(put("/api/itens/" + itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade", is(3)));
    }

    @Test
    @Order(14)
    void deletarItem() throws Exception {
        mockMvc.perform(delete("/api/itens/" + itemId))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(15)
    void excluirProduto() throws Exception {
        mockMvc.perform(delete("/api/produtos/" + produtoId))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(16)
    void excluirEstoque() throws Exception {
        mockMvc.perform(delete("/api/estoques/" + estoqueId))
                .andExpect(status().isOk());
    }
}