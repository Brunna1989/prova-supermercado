package com.br.prova.supermercado.controller;

import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.service.EstoqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoques")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estoque> buscarEstoquePorId(@PathVariable Long id) {
        Estoque estoque = estoqueService.buscarEstoquePorId(id);
        return ResponseEntity.ok(estoque);
    }

    @GetMapping("/{id}/produtos")
    public ResponseEntity<List<Produto>> listarProdutos(@PathVariable Long id) {
        Estoque estoque = estoqueService.buscarEstoquePorId(id);
        return ResponseEntity.ok(estoque.getListaDeProdutos());
    }

    @GetMapping("/{idEstoque}/produtos/{idProduto}")
    public ResponseEntity<Produto> buscarProdutoNoEstoque(
            @PathVariable Long idEstoque,
            @PathVariable Long idProduto) {

        Estoque estoque = estoqueService.buscarEstoquePorId(idEstoque);
        Produto produto = estoqueService.encontraProdutoPorId(estoque, idProduto);

        if (produto != null) {
            return ResponseEntity.ok(produto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{id}/dar-baixa")
    public ResponseEntity<String> darBaixa(
            @PathVariable Long id,
            @RequestParam String nomeProduto,
            @RequestParam int quantidade
    ) {
        boolean sucesso = estoqueService.darBaixaEmProduto(id, nomeProduto, quantidade);
        if (sucesso) {
            return ResponseEntity.ok("Baixa realizada com sucesso.");
        } else {
            return ResponseEntity.badRequest().body("Erro ao dar baixa: produto inexistente ou quantidade insuficiente.");
        }
    }

    @GetMapping("/{id}/imprimir")
    public ResponseEntity<String> imprimirEstoque(@PathVariable Long id) {
        estoqueService.imprimirEstoque(id);
        return ResponseEntity.ok("Estoque impresso no console com sucesso.");
    }

    @PostMapping
    public ResponseEntity<Estoque> criarEstoque() {
        Estoque novoEstoque = estoqueService.criarEstoque();
        return ResponseEntity.ok(novoEstoque);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluirEstoque(@PathVariable Long id) {
        estoqueService.excluirEstoque(id);
        return ResponseEntity.ok("Estoque com ID " + id + " excluído com sucesso.");
    }
}
