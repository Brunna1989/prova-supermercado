package com.br.prova.supermercado.repository;

import com.br.prova.supermercado.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
