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
public class ProdutoServiceTest {

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
                .id(2L)
                .nome("Arroz")
                .preco(5.0)
                .quantidadeEmEstoque(10)
                .estoqueId(1L)
                .build();
    }

    @Test
    void salvar_sucesso() {
        when(estoqueRepository.findById(estoque.getId())).thenReturn(Optional.of(estoque));
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto)); // mock necessário
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(estoque);

        ProdutoDTO salvo = produtoService.salvar(produtoDTO);

        assertNotNull(salvo);
        assertEquals(produtoDTO.getNome(), salvo.getNome());
        assertEquals(produtoDTO.getPreco(), salvo.getPreco());
        assertEquals(produtoDTO.getQuantidadeEmEstoque(), salvo.getQuantidadeEmEstoque());
        assertEquals(produtoDTO.getEstoqueId(), salvo.getEstoqueId());
    }

    @Test
    void salvar_estoqueNaoEncontrado() {
        when(estoqueRepository.findById(estoque.getId())).thenReturn(Optional.empty());
        assertThrows(EstoqueNaoEncontradoException.class, () -> produtoService.salvar(produtoDTO));
    }

    @Test
    void salvar_produtoExistenteNaoEncontrado() {
        when(estoqueRepository.findById(estoque.getId())).thenReturn(Optional.of(estoque));
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.empty());
        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.salvar(produtoDTO));
    }

    @Test
    void listarTodos_deveRetornarLista() {
        when(produtoRepository.findAll()).thenReturn(List.of(produto));
        List<ProdutoDTO> lista = produtoService.listarTodos();
        assertEquals(1, lista.size());
        assertEquals(produto.getNome(), lista.get(0).getNome());
    }

    @Test
    void buscarPorId_existente() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        ProdutoDTO dto = produtoService.buscarPorId(produto.getId());
        assertNotNull(dto);
        assertEquals(produto.getNome(), dto.getNome());
    }

    @Test
    void buscarPorId_naoExistente() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.buscarPorId(99L));
    }

    @Test
    void excluir_existente() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        produtoService.excluir(produto.getId());
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