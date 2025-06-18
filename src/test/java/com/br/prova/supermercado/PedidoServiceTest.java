package com.br.prova.supermercado;

import com.br.prova.supermercado.dto.ItemDTO;
import com.br.prova.supermercado.dto.PedidoDTO;
import com.br.prova.supermercado.dto.ProdutoDTO;
import com.br.prova.supermercado.model.Item;
import com.br.prova.supermercado.model.Pedido;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.repository.PedidoRepository;
import com.br.prova.supermercado.repository.ProdutoRepository;
import com.br.prova.supermercado.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Produto produto;
    private Pedido pedido;
    private Item item;
    private ProdutoDTO produtoDTO;
    private ItemDTO itemDTO;
    private PedidoDTO pedidoDTO;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setPreco(10.0);
        produto.setQuantidadeEmEstoque(10);

        produtoDTO = ProdutoDTO.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .preco(produto.getPreco())
                .quantidadeEmEstoque(produto.getQuantidadeEmEstoque())
                .estoqueId(1L)
                .build();

        item = new Item();
        item.setId(2L);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setPreco(20.0);

        itemDTO = ItemDTO.builder()
                .id(item.getId())
                .produto(produtoDTO)
                .quantidade(item.getQuantidade())
                .preco(item.getPreco())
                .build();

        pedido = new Pedido();
        pedido.setId(3L);
        pedido.setListaItens(new ArrayList<>(List.of(item)));
        pedido.setValorTotalDoPedido(20.0);

        pedidoDTO = PedidoDTO.builder()
                .id(pedido.getId())
                .listaItens(List.of(itemDTO))
                .valorTotalDoPedido(pedido.getValorTotalDoPedido())
                .build();
    }

    @Test
    void salvar_deveSalvarPedidoComSucesso() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(3L);
            p.getListaItens().get(0).setId(2L);
            return p;
        });

        PedidoDTO result = pedidoService.salvar(pedidoDTO);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(1, result.getListaItens().size());
        assertEquals(20.0, result.getValorTotalDoPedido());
        verify(produtoRepository).findById(produto.getId());
        verify(produtoRepository).save(any(Produto.class));
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void salvar_deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> pedidoService.salvar(pedidoDTO));
        assertTrue(ex.getMessage().contains("Produto não encontrado"));
        verify(produtoRepository).findById(produto.getId());
        verify(produtoRepository, never()).save(any());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void salvar_deveLancarExcecaoQuandoEstoqueInsuficiente() {
        produto.setQuantidadeEmEstoque(1);
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> pedidoService.salvar(pedidoDTO));
        assertTrue(ex.getMessage().contains("Estoque insuficiente"));
        verify(produtoRepository).findById(produto.getId());
        verify(produtoRepository, never()).save(any());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void listarItensPorPedidoId_deveRetornarItensDoPedido() {
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        List<ItemDTO> itens = pedidoService.listarItensPorPedidoId(pedido.getId());
        assertEquals(1, itens.size());
        assertEquals(item.getId(), itens.get(0).getId());
        verify(pedidoRepository).findById(pedido.getId());
    }

    @Test
    void listarItensPorPedidoId_deveLancarExcecaoQuandoPedidoNaoEncontrado() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> pedidoService.listarItensPorPedidoId(99L));
        assertTrue(ex.getMessage().contains("Pedido não encontrado"));
        verify(pedidoRepository).findById(99L);
    }

    @Test
    void deveCalcularTrocoCorretamente() {
        PedidoService service = new PedidoService(null, null);
        double troco = service.calcularTroco(37.50, 50.00);
        assertEquals(12.50, troco, 0.001);
    }

    @Test
    void deveCalcularNotasTroco() {
        PedidoService service = new PedidoService(null, null);
        Map<Double, Integer> notas = service.calcularNotasTroco(18.35);
        assertEquals(1, notas.get(10.0));
        assertEquals(1, notas.get(5.0));
        assertEquals(1, notas.get(2.0));
        assertEquals(1, notas.get(1.0));
        assertEquals(1, notas.get(0.25));
        assertEquals(1, notas.get(0.10));
    }
}