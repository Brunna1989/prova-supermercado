package com.br.prova.supermercado.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDTO {
    private Long id;
    private List<ItemDTO> listaItens;
    private double valorTotalDoPedido;
}
