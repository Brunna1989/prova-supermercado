package com.br.prova.supermercado.repository;

import com.br.prova.supermercado.model.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @EntityGraph(attributePaths = "listaItens.produto")
    List<Pedido> findAll();
}