package com.br.prova.supermercado.repository;

import com.br.prova.supermercado.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    Optional<Estoque> findByListaDeProdutos_Id(Long produtoId);
}
