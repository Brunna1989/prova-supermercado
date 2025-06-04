package com.br.prova.supermercado.mapper;

import com.br.prova.supermercado.dto.ItemDTO;
import com.br.prova.supermercado.dto.ProdutoDTO;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Item;
import com.br.prova.supermercado.model.Produto;

public class ItemMapper {

    public static ItemDTO toDTO(Item item) {
        if (item == null) return null;

        Produto produto = item.getProduto();
        return ItemDTO.builder()
                .id(item.getId())
                .quantidade(item.getQuantidade())
                .preco(item.getPreco())
                .produto(ProdutoDTO.builder()
                        .id(produto.getId())
                        .nome(produto.getNome())
                        .preco(produto.getPreco())
                        .quantidadeEmEstoque(produto.getQuantidadeEmEstoque())
                        .estoqueId(produto.getEstoque() != null ? produto.getEstoque().getId() : null)
                        .build())
                .build();
    }

    public static Item toEntity(ItemDTO dto, Estoque estoque) {
        if (dto == null || dto.getProduto() == null) return null;

        Produto produto = estoque.getListaDeProdutos().stream()
                .filter(p -> p.getId().equals(dto.getProduto().getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Produto não encontrado no estoque."));

        return Item.builder()
                .id(dto.getId())
                .produto(produto)
                .quantidade(dto.getQuantidade())
                .preco(dto.getPreco())
                // O pedido deve ser setado na camada de serviço
                .build();
    }
}
