package com.br.prova.supermercado.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> listaItens = new ArrayList<>();

    private double valorTotalDoPedido;

    private Double valorPago;
    private Double troco;

    public void calcularValorTotal() {
        this.valorTotalDoPedido = listaItens.stream()
                .mapToDouble(Item::getPreco)
                .sum();
    }

    public void registrarPagamento(double valorPago) {
        this.valorPago = valorPago;
        this.troco = valorPago - this.valorTotalDoPedido;
    }
}