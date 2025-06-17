package com.br.prova.supermercado;

import com.br.prova.supermercado.dto.PedidoDTO;
import com.br.prova.supermercado.dto.ProdutoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PedidoControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void criarPedidoEAbaterEstoque() {
        ResponseEntity<Map> estoqueResp = restTemplate.postForEntity("/api/estoques", null, Map.class);
        Long estoqueId = Long.valueOf(estoqueResp.getBody().get("id").toString());

        ProdutoDTO produto = ProdutoDTO.builder()
                .nome("Arroz")
                .preco(10.0)
                .quantidadeEmEstoque(50)
                .estoqueId(estoqueId)
                .build();
        ResponseEntity<ProdutoDTO> prodResp = restTemplate.postForEntity("/api/produtos", produto, ProdutoDTO.class);
        Long produtoId = prodResp.getBody().getId();

        String pedidoJson = """
        {
          "listaItens": [
            {
              "produto": { "id": %d },
              "quantidade": 5
            }
          ]
        }
        """.formatted(produtoId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> pedidoEntity = new HttpEntity<>(pedidoJson, headers);

        ResponseEntity<PedidoDTO> pedidoResp = restTemplate.postForEntity("/api/pedidos", pedidoEntity, PedidoDTO.class);
        assertThat(pedidoResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pedidoResp.getBody().getListaItens().get(0).getQuantidade()).isEqualTo(5);

        ResponseEntity<ProdutoDTO> prodAtualizado = restTemplate.getForEntity("/api/produtos/" + produtoId, ProdutoDTO.class);
        assertThat(prodAtualizado.getBody().getQuantidadeEmEstoque()).isEqualTo(45);
    }
}