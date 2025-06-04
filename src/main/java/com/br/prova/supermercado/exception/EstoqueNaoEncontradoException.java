package com.br.prova.supermercado.exception;

public class EstoqueNaoEncontradoException extends RuntimeException {
    public EstoqueNaoEncontradoException(Long id) {
        super("Estoque com ID " + id + " não encontrado.");
    }
}
