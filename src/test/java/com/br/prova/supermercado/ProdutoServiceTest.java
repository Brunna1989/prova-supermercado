package com.br.prova.supermercado;


import com.br.prova.supermercado.dto.ProdutoDTO;
import com.br.prova.supermercado.exception.EstoqueNaoEncontradoException;
import com.br.prova.supermercado.exception.ProdutoNaoEncontradoException;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.repository.EstoqueRepository;
import com.br.prova.supermercado.repository.ProdutoRepository;
import com.br.prova.supermercado.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private EstoqueRepository estoqueRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Estoque estoque;
    private Produto produto;
    private ProdutoDTO produtoDTO;

    @BeforeEach
    void setUp() {
        estoque = new Estoque();
        estoque.setId(1L);

        produto = new Produto();
        produto.setId(2L);
        produto.setNome("Arroz");
        produto.setPreco(5.0);
        produto.setQuantidadeEmEstoque(10);
        produto.setEstoque(estoque);

        produtoDTO = ProdutoDTO.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .preco(produto.getPreco())
                .quantidadeEmEstoque(produto.getQuantidadeEmEstoque())
                .estoqueId(estoque.getId())
                .build();
    }

    @Test
    void salvar_sucesso() {
        when(estoqueRepository.findById(estoque.getId())).thenReturn(Optional.of(estoque));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(estoque);

        ProdutoDTO salvo = produtoService.salvar(produtoDTO);

        assertNotNull(salvo);
        assertEquals(produto.getNome(), salvo.getNome());
        verify(estoqueRepository).findById(estoque.getId());
        verify(produtoRepository).save(any(Produto.class));
        verify(estoqueRepository).save(any(Estoque.class));
    }

    @Test
    void salvar_estoqueNaoEncontrado() {
        when(estoqueRepository.findById(estoque.getId())).thenReturn(Optional.empty());
        assertThrows(EstoqueNaoEncontradoException.class, () -> produtoService.salvar(produtoDTO));
        verify(estoqueRepository).findById(estoque.getId());
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void salvar_produtoExistenteNaoEncontrado() {
        produtoDTO.setId(99L);
        when(estoqueRepository.findById(estoque.getId())).thenReturn(Optional.of(estoque));
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.salvar(produtoDTO));
        verify(produtoRepository).findById(99L);
    }

    @Test
    void listarTodos_deveRetornarLista() {
        when(produtoRepository.findAll()).thenReturn(Collections.singletonList(produto));
        List<ProdutoDTO> lista = produtoService.listarTodos();
        assertEquals(1, lista.size());
        assertEquals(produto.getNome(), lista.get(0).getNome());
        verify(produtoRepository).findAll();
    }

    @Test
    void buscarPorId_existente() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        ProdutoDTO dto = produtoService.buscarPorId(produto.getId());
        assertEquals(produto.getNome(), dto.getNome());
        verify(produtoRepository).findById(produto.getId());
    }

    @Test
    void buscarPorId_naoExistente() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.buscarPorId(99L));
        verify(produtoRepository).findById(99L);
    }

    @Test
    void excluir_existente() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        doNothing().when(produtoRepository).delete(produto);
        assertDoesNotThrow(() -> produtoService.excluir(produto.getId()));
        verify(produtoRepository).findById(produto.getId());
        verify(produtoRepository).delete(produto);
    }

    @Test
    void excluir_naoExistente() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.excluir(99L));
        verify(produtoRepository).findById(99L);
        verify(produtoRepository, never()).delete(any());
    }
}