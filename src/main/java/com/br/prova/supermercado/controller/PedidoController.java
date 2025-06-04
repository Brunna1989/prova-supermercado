package com.br.prova.supermercado.controller;

import com.br.prova.supermercado.dto.PedidoDTO;
import com.br.prova.supermercado.dto.ItemDTO;
import com.br.prova.supermercado.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> criarPedido(@RequestBody PedidoDTO pedidoDTO) {
        PedidoDTO pedidoSalvo = pedidoService.salvar(pedidoDTO);
        return new ResponseEntity<>(pedidoSalvo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/itens")
    public ResponseEntity<List<ItemDTO>> listarItensDoPedido(@PathVariable("id") Long id) {
        List<ItemDTO> itens = pedidoService.listarItensPorPedidoId(id);
        return ResponseEntity.ok(itens);
    }
}