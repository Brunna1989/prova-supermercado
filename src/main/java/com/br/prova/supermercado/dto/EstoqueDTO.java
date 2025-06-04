package com.br.prova.supermercado.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstoqueDTO {
    private Long id;
    private List<ProdutoDTO> listaDeProdutos;
}
