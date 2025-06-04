package com.br.prova.supermercado.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private double preco;
    private int quantidadeEmEstoque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estoque_id", nullable = false)
    private Estoque estoque;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> itens = new ArrayList<>();
}