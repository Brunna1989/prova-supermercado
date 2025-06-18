// src/main/java/com/br/prova/supermercado/mapper/PedidoMapper.java
package com.br.prova.supermercado.mapper;

import com.br.prova.supermercado.dto.PedidoDTO;
import com.br.prova.supermercado.exception.EstoqueNaoEncontradoException;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Item;
import com.br.prova.supermercado.model.Pedido;
import com.br.prova.supermercado.repository.EstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PedidoMapper {

    private final EstoqueRepository estoqueRepository;

    public PedidoDTO toDTO(Pedido pedido) {
        if (pedido == null) return null;

        return PedidoDTO.builder()
                .id(pedido.getId())
                .listaItens(pedido.getListaItens()
                        .stream()
                        .map(ItemMapper::toDTO)
                        .collect(Collectors.toList()))
                .valorTotalDoPedido(pedido.getValorTotalDoPedido())
                .valorPago(pedido.getValorPago())
                .troco(pedido.getTroco())
                .build();
    }

    public Pedido toEntity(PedidoDTO dto) {
        if (dto == null) return null;

        Pedido pedido = Pedido.builder()
                .id(dto.getId())
                .valorTotalDoPedido(dto.getValorTotalDoPedido())
                .valorPago(dto.getValorPago())
                .troco(dto.getTroco())
                .build();

        List<Item> itens = dto.getListaItens() != null
                ? dto.getListaItens()
                .stream()
                .map(itemDTO -> {
                    Estoque estoque = estoqueRepository.findByListaDeProdutos_Id(itemDTO.getProduto().getId())
                            .orElseThrow(() -> new EstoqueNaoEncontradoException(itemDTO.getProduto().getId()));

                    Item item = ItemMapper.toEntity(itemDTO, estoque);
                    item.setPedido(pedido);
                    return item;
                })
                .collect(Collectors.toList())
                : Collections.emptyList();

        pedido.setListaItens(itens);
        return pedido;
    }
}