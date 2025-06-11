package com.br.prova.supermercado;

import com.br.prova.supermercado.dto.ItemDTO;
import com.br.prova.supermercado.dto.PedidoDTO;
import com.br.prova.supermercado.dto.ProdutoDTO;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.repository.EstoqueRepository;
import com.br.prova.supermercado.repository.ProdutoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PedidoControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private Produto produto;
    private Estoque estoque;

    @BeforeEach
    public void setup() {
        estoqueRepository.deleteAll();
        produtoRepository.deleteAll();

        estoque = new Estoque();
        estoque.setQuantidade(100);
        estoque = estoqueRepository.save(estoque);

        produto = new Produto();
        produto.setNome("Arroz Integral");
        produto.setPreco(7.50);
        produto.setQuantidadeEmEstoque(100);
        produto.setEstoque(estoque);
        produto = produtoRepository.save(produto);

        estoque.setProduto(produto);
        estoqueRepository.save(estoque);
    }

    @Test
    public void deveCriarPedidoComSucesso() throws Exception {
        ProdutoDTO produtoDTO = ProdutoDTO.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .preco(produto.getPreco())
                .quantidadeEmEstoque(produto.getQuantidadeEmEstoque())
                .estoqueId(estoque.getId()) // Corrigido aqui!
                .build();

        ItemDTO itemDTO = ItemDTO.builder()
                .produto(produtoDTO)
                .quantidade(3)
                .preco(produto.getPreco())
                .build();

        PedidoDTO pedidoDTO = PedidoDTO.builder()
                .listaItens(Collections.singletonList(itemDTO))
                .valorTotalDoPedido(produto.getPreco() * itemDTO.getQuantidade())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(
                new ObjectMapper().writeValueAsString(pedidoDTO),
                headers
        );

        ResponseEntity<PedidoDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/pedidos",
                HttpMethod.POST,
                request,
                PedidoDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(1, response.getBody().getListaItens().size());
        assertEquals(produto.getId(), response.getBody().getListaItens().get(0).getProduto().getId());
    }
}
