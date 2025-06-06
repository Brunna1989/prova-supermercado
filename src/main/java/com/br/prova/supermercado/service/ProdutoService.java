package com.br.prova.supermercado.service;

import com.br.prova.supermercado.dto.ProdutoDTO;
import com.br.prova.supermercado.exception.EstoqueNaoEncontradoException;
import com.br.prova.supermercado.exception.ProdutoNaoEncontradoException;
import com.br.prova.supermercado.mapper.ProdutoMapper;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.repository.EstoqueRepository;
import com.br.prova.supermercado.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Transactional
    public ProdutoDTO salvar(ProdutoDTO dto) {
        Estoque estoque = estoqueRepository.findById(dto.getEstoqueId())
                .orElseThrow(() -> new EstoqueNaoEncontradoException(dto.getEstoqueId()));

        Produto produto = ProdutoMapper.toEntity(dto, estoque);

        if (produto.getId() != null) {
            produtoRepository.findById(produto.getId())
                    .orElseThrow(() -> new ProdutoNaoEncontradoException(produto.getId()));
        }

        if (!estoque.getListaDeProdutos().contains(produto)) {
            estoque.getListaDeProdutos().add(produto);
        }

        Produto salvo = produtoRepository.save(produto);
        estoqueRepository.save(estoque);

        return ProdutoMapper.toDTO(salvo);
    }

    public List<ProdutoDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(ProdutoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProdutoDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
        return ProdutoMapper.toDTO(produto);
    }

    public void excluir(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
        produtoRepository.delete(produto);
    }
}