package com.br.prova.supermercado.mapper;

import com.br.prova.supermercado.dto.ProdutoDTO;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Produto;

public class ProdutoMapper {

    public static Produto toEntity(ProdutoDTO dto, Estoque estoque) {
        Produto produto = new Produto();
        produto.setId(dto.getId());
        produto.setNome(dto.getNome());
        produto.setPreco(dto.getPreco());
        produto.setQuantidadeEmEstoque(dto.getQuantidadeEmEstoque());
        produto.setEstoque(estoque);
        return produto;
    }

    public static ProdutoDTO toDTO(Produto entity) {
        return ProdutoDTO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .preco(entity.getPreco())
                .quantidadeEmEstoque(entity.getQuantidadeEmEstoque())
                .estoqueId(entity.getEstoque() != null ? entity.getEstoque().getId() : null)
                .build();
    }
}
