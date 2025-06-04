package com.br.prova.supermercado.mapper;

import com.br.prova.supermercado.dto.EstoqueDTO;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Produto;

import java.util.List;
import java.util.stream.Collectors;

public class EstoqueMapper {

    public static EstoqueDTO toDTO(Estoque estoque) {
        if (estoque == null) return null;

        return EstoqueDTO.builder()
                .id(estoque.getId())
                .listaDeProdutos(
                        estoque.getListaDeProdutos() != null
                                ? estoque.getListaDeProdutos()
                                .stream()
                                .map(ProdutoMapper::toDTO)
                                .collect(Collectors.toList())
                                : null
                )
                .build();
    }

    public static Estoque toEntity(EstoqueDTO dto) {
        if (dto == null) return null;

        Estoque estoque = new Estoque();
        estoque.setId(dto.getId());

        if (dto.getListaDeProdutos() != null) {
            List<Produto> produtos = dto.getListaDeProdutos()
                    .stream()
                    .map(produtoDTO -> ProdutoMapper.toEntity(produtoDTO, estoque))
                    .collect(Collectors.toList());

            estoque.setListaDeProdutos(produtos);
        }

        return estoque;
    }
}
