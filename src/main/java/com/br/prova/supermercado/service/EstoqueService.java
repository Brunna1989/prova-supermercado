package com.br.prova.supermercado.service;

import com.br.prova.supermercado.exception.EstoqueNaoEncontradoException;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.repository.EstoqueRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public EstoqueService(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    public Estoque buscarEstoquePorId(Long id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new EstoqueNaoEncontradoException(id));
    }

    public Produto encontraProdutoPorNome(Estoque estoque, String nomeProduto) {
        return estoque.getListaDeProdutos().stream()
                .filter(p -> p.getNome().equalsIgnoreCase(nomeProduto))
                .findFirst()
                .orElse(null);
    }

    public Produto encontraProdutoPorId(Estoque estoque, Long produtoId) {
        return estoque.getListaDeProdutos().stream()
                .filter(p -> p.getId().equals(produtoId))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public boolean darBaixaEmProduto(Long estoqueId, String nomeProduto, int quantidade) {
        Estoque estoque = buscarEstoquePorId(estoqueId);
        Produto produto = encontraProdutoPorNome(estoque, nomeProduto);

        if (produto != null && produto.getQuantidadeEmEstoque() >= quantidade) {
            produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() - quantidade);
            estoqueRepository.save(estoque);
            return true;
        }
        return false;
    }

    public int getQuantidadeAtual(Estoque estoque, Produto produto) {
        return produto.getQuantidadeEmEstoque();
    }

    public void imprimirEstoque(Long estoqueId) {
        Estoque estoque = buscarEstoquePorId(estoqueId);
        List<Produto> produtos = estoque.getListaDeProdutos();

        System.out.println("=== Estoque ID: " + estoqueId + " ===");
        produtos.forEach(produto -> {
            System.out.println("ID: " + produto.getId()
                    + " | Nome: " + produto.getNome()
                    + " | Quantidade: " + produto.getQuantidadeEmEstoque());
        });
    }

    @Transactional
    public Estoque criarEstoque() {
        Estoque novoEstoque = new Estoque();
        return estoqueRepository.save(novoEstoque);
    }

    @Transactional
    public void excluirEstoque(Long id) {
        Estoque estoque = buscarEstoquePorId(id);
        estoqueRepository.delete(estoque);
    }
}
