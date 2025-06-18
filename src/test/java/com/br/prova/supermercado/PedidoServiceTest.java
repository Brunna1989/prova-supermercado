package com.br.prova.supermercado;

import com.br.prova.supermercado.dto.ItemDTO;
import com.br.prova.supermercado.dto.PedidoDTO;
import com.br.prova.supermercado.dto.ProdutoDTO;
import com.br.prova.supermercado.model.Pedido;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.repository.PedidoRepository;
import com.br.prova.supermercado.repository.ProdutoRepository;
import com.br.prova.supermercado.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    private PedidoRepository pedidoRepository;
    private ProdutoRepository produtoRepository;
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        pedidoRepository = mock(PedidoRepository.class);
        produtoRepository = mock(ProdutoRepository.class);
        pedidoService = new PedidoService(pedidoRepository, produtoRepository);
    }

    @Test
    void salvarPedidoComSucesso() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Teste");
        produto.setPreco(10.0);
        produto.setQuantidadeEmEstoque(10);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any())).thenReturn(produto);
        when(pedidoRepository.save(any())).thenAnswer(i -> {
            Pedido p = i.getArgument(0);
            p.setId(1L);
            return p;
        });

        ItemDTO itemDTO = ItemDTO.builder()
                .produto(ProdutoDTO.builder().id(1L).build())
                .quantidade(2)
                .build();

        PedidoDTO pedidoDTO = PedidoDTO.builder()
                .listaItens(List.of(itemDTO))
                .build();

        PedidoDTO salvo = pedidoService.salvar(pedidoDTO);

        assertThat(salvo.getId()).isEqualTo(1L);
        assertThat(salvo.getListaItens()).hasSize(1);
        assertThat(salvo.getValorTotalDoPedido()).isEqualTo(20.0);
        verify(produtoRepository).save(any());
        verify(pedidoRepository).save(any());
    }

    @Test
    void salvarPedidoEstoqueInsuficiente() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Teste");
        produto.setPreco(10.0);
        produto.setQuantidadeEmEstoque(1);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ItemDTO itemDTO = ItemDTO.builder()
                .produto(ProdutoDTO.builder().id(1L).build())
                .quantidade(2)
                .build();

        PedidoDTO pedidoDTO = PedidoDTO.builder()
                .listaItens(List.of(itemDTO))
                .build();

        assertThatThrownBy(() -> pedidoService.salvar(pedidoDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Estoque insuficiente");
    }

    @Test
    void registrarPagamentoComSucesso() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setValorTotalDoPedido(30.0);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any())).thenReturn(pedido);

        PedidoDTO dto = pedidoService.registrarPagamento(1L, 50.0);

        assertThat(dto.getValorPago()).isEqualTo(50.0);
        assertThat(dto.getTroco()).isEqualTo(20.0);
    }

    @Test
    void registrarPagamentoInsuficiente() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setValorTotalDoPedido(30.0);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.registrarPagamento(1L, 10.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("insuficiente");
    }

    @Test
    void listarTodosRetornaPedidos() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setValorTotalDoPedido(10.0);

        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        List<PedidoDTO> pedidos = pedidoService.listarTodos();
        assertThat(pedidos).hasSize(1);
        assertThat(pedidos.get(0).getId()).isEqualTo(1L);
    }
}