package com.br.prova.supermercado.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@Setter
@AllArgsConstructor
@Builder
public class PedidoDTO {
    private Long id;
    private List<ItemDTO> listaItens;
    private double valorTotalDoPedido;
    private Double valorPago; // novo campo
    private Double troco;     // novo campo
}