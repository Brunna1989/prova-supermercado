package com.br.prova.supermercado.repository;

import com.br.prova.supermercado.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
