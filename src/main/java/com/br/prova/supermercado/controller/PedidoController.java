package com.br.prova.supermercado.controller;

import com.br.prova.supermercado.dto.PedidoDTO;
import com.br.prova.supermercado.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> criarPedido(@RequestBody PedidoDTO pedidoDTO) {
        PedidoDTO salvo = pedidoService.salvar(pedidoDTO);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<PedidoDTO> pagarPedido(@PathVariable("id") Long id, @RequestBody Map<String, Double> body) {
        double valorPago = body.get("valorPago");
        PedidoDTO dto = pedidoService.registrarPagamento(id, valorPago);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable("id") Long id) {
        return pedidoService.listarTodos().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}