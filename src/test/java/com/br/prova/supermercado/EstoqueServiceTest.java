package com.br.prova.supermercado;


import com.br.prova.supermercado.exception.EstoqueNaoEncontradoException;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.repository.EstoqueRepository;
import com.br.prova.supermercado.service.EstoqueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstoqueServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @InjectMocks
    private EstoqueService estoqueService;

    private Estoque estoque;
    private Produto produto;

    @BeforeEach
    void setUp() {
        estoque = new Estoque();
        estoque.setId(1L);

        produto = new Produto();
        produto.setId(2L);
        produto.setNome("Arroz");
        produto.setQuantidadeEmEstoque(10);
        produto.setEstoque(estoque);

        estoque.getListaDeProdutos().add(produto);
    }

    @Test
    void buscarEstoquePorId_existente() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        Estoque result = estoqueService.buscarEstoquePorId(1L);
        assertEquals(estoque, result);
        verify(estoqueRepository).findById(1L);
    }

    @Test
    void buscarEstoquePorId_naoExistente() {
        when(estoqueRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EstoqueNaoEncontradoException.class, () -> estoqueService.buscarEstoquePorId(99L));
        verify(estoqueRepository).findById(99L);
    }

    @Test
    void encontraProdutoPorNome_existente() {
        Produto result = estoqueService.encontraProdutoPorNome(estoque, "Arroz");
        assertNotNull(result);
        assertEquals(produto, result);
    }

    @Test
    void encontraProdutoPorNome_naoExistente() {
        Produto result = estoqueService.encontraProdutoPorNome(estoque, "Feijao");
        assertNull(result);
    }

    @Test
    void encontraProdutoPorId_existente() {
        Produto result = estoqueService.encontraProdutoPorId(estoque, 2L);
        assertNotNull(result);
        assertEquals(produto, result);
    }

    @Test
    void encontraProdutoPorId_naoExistente() {
        Produto result = estoqueService.encontraProdutoPorId(estoque, 99L);
        assertNull(result);
    }

    @Test
    void darBaixaEmProduto_sucesso() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(estoque);

        boolean sucesso = estoqueService.darBaixaEmProduto(1L, "Arroz", 5);

        assertTrue(sucesso);
        assertEquals(5, produto.getQuantidadeEmEstoque());
        verify(estoqueRepository).save(estoque);
    }

    @Test
    void darBaixaEmProduto_produtoNaoEncontrado() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        boolean sucesso = estoqueService.darBaixaEmProduto(1L, "Feijao", 2);
        assertFalse(sucesso);
        verify(estoqueRepository, never()).save(any());
    }

    @Test
    void darBaixaEmProduto_quantidadeInsuficiente() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        boolean sucesso = estoqueService.darBaixaEmProduto(1L, "Arroz", 20);
        assertFalse(sucesso);
        verify(estoqueRepository, never()).save(any());
    }

    @Test
    void getQuantidadeAtual() {
        int qtd = estoqueService.getQuantidadeAtual(estoque, produto);
        assertEquals(10, qtd);
    }

    @Test
    void imprimirEstoque() {
        // Apenas para cobertura, não há assert pois imprime no console
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        assertDoesNotThrow(() -> estoqueService.imprimirEstoque(1L));
    }

    @Test
    void criarEstoque() {
        Estoque novo = new Estoque();
        novo.setId(2L);
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(novo);
        Estoque result = estoqueService.criarEstoque();
        assertEquals(2L, result.getId());
        verify(estoqueRepository).save(any(Estoque.class));
    }

    @Test
    void excluirEstoque_existente() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        doNothing().when(estoqueRepository).delete(estoque);
        assertDoesNotThrow(() -> estoqueService.excluirEstoque(1L));
        verify(estoqueRepository).delete(estoque);
    }

    @Test
    void excluirEstoque_naoExistente() {
        when(estoqueRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EstoqueNaoEncontradoException.class, () -> estoqueService.excluirEstoque(99L));
        verify(estoqueRepository, never()).delete(any());
    }
}