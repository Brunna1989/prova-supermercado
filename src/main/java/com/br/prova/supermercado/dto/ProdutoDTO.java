package com.br.prova.supermercado.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoDTO {
    private Long id;
    private String nome;
    private double preco;
    private int quantidadeEmEstoque;
    private Long estoqueId;
}
