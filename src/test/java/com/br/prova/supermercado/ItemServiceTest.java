package com.br.prova.supermercado;

import com.br.prova.supermercado.dto.ItemDTO;
import com.br.prova.supermercado.dto.ProdutoDTO;
import com.br.prova.supermercado.exception.EstoqueNaoEncontradoException;
import com.br.prova.supermercado.exception.ProdutoNaoEncontradoException;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Item;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.repository.EstoqueRepository;
import com.br.prova.supermercado.repository.ItemRepository;
import com.br.prova.supermercado.repository.ProdutoRepository;
import com.br.prova.supermercado.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private EstoqueRepository estoqueRepository;
    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ItemService itemService;

    private Produto produto;
    private Estoque estoque;
    private Item item;
    private ItemDTO itemDTO;

    @BeforeEach
    void setUp() {
        estoque = new Estoque();
        estoque.setId(1L);

        produto = new Produto();
        produto.setId(2L);
        produto.setNome("Produto Teste");
        produto.setEstoque(estoque);

        // Adiciona o produto à lista do estoque para o mapper funcionar corretamente
        estoque.getListaDeProdutos().add(produto);

        item = new Item();
        item.setId(3L);
        item.setProduto(produto);
        item.setQuantidade(5);
        item.setPreco(10.0);

        ProdutoDTO produtoDTO = ProdutoDTO.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .estoqueId(estoque.getId())
                .build();

        itemDTO = ItemDTO.builder()
                .id(item.getId())
                .produto(produtoDTO)
                .quantidade(item.getQuantidade())
                .preco(item.getPreco())
                .build();
    }

    @Test
    void listarTodos_deveRetornarListaDeItensDTO() {
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
        List<ItemDTO> result = itemService.listarTodos();
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
        verify(itemRepository).findAll();
    }

    @Test
    void buscarPorId_existente_deveRetornarItemDTO() {
        when(itemRepository.findById(3L)).thenReturn(Optional.of(item));
        ItemDTO result = itemService.buscarPorId(3L);
        assertEquals(item.getId(), result.getId());
        verify(itemRepository).findById(3L);
    }

    @Test
    void buscarPorId_naoExistente_deveLancarProdutoNaoEncontradoException() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProdutoNaoEncontradoException.class, () -> itemService.buscarPorId(99L));
        verify(itemRepository).findById(99L);
    }

    @Test
    void salvar_sucesso_deveRetornarItemDTO() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDTO result = itemService.salvar(itemDTO);

        assertNotNull(result);
        assertEquals(itemDTO.getQuantidade(), result.getQuantidade());
        verify(produtoRepository).findById(produto.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void salvar_produtoNaoEncontrado_deveLancarProdutoNaoEncontradoException() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.empty());
        assertThrows(ProdutoNaoEncontradoException.class, () -> itemService.salvar(itemDTO));
        verify(produtoRepository).findById(produto.getId());
    }

    @Test
    void salvar_estoqueNaoEncontrado_deveLancarEstoqueNaoEncontradoException() {
        produto.setEstoque(null);
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        assertThrows(EstoqueNaoEncontradoException.class, () -> itemService.salvar(itemDTO));
        verify(produtoRepository).findById(produto.getId());
    }

    @Test
    void deletar_existente_deveDeletar() {
        when(itemRepository.existsById(3L)).thenReturn(true);
        doNothing().when(itemRepository).deleteById(3L);
        assertDoesNotThrow(() -> itemService.deletar(3L));
        verify(itemRepository).existsById(3L);
        verify(itemRepository).deleteById(3L);
    }

    @Test
    void deletar_naoExistente_deveLancarProdutoNaoEncontradoException() {
        when(itemRepository.existsById(99L)).thenReturn(false);
        assertThrows(ProdutoNaoEncontradoException.class, () -> itemService.deletar(99L));
        verify(itemRepository).existsById(99L);
        verify(itemRepository, never()).deleteById(anyLong());
    }
}