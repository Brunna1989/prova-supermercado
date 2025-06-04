package com.br.prova.supermercado.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {
    private Long id;
    private ProdutoDTO produto;
    private int quantidade;
    private double preco;
}