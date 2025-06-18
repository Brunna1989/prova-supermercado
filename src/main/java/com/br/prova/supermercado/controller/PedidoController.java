package com.br.prova.supermercado.controller;

import com.br.prova.supermercado.dto.PedidoDTO;
import com.br.prova.supermercado.service.PedidoService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> criarPedido(@RequestBody PedidoDTO pedidoDTO) {
        PedidoDTO criado = pedidoService.salvar(pedidoDTO);
        return ResponseEntity.ok(criado);
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<PedidoDTO> pagarPedido(@PathVariable("id") Long id, @RequestBody PagamentoRequest pagamento) {
        PedidoDTO atualizado = pedidoService.registrarPagamento(id, pagamento.getValorPago());
        return ResponseEntity.ok(atualizado);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @Getter
    @Setter
    public static class PagamentoRequest {
        private double valorPago;
    }
}