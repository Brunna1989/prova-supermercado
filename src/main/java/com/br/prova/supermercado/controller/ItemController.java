package com.br.prova.supermercado.controller;

import com.br.prova.supermercado.dto.ItemDTO;
import com.br.prova.supermercado.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itens")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemDTO>> listarTodos() {
        List<ItemDTO> itens = itemService.listarTodos();
        return ResponseEntity.ok(itens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> buscarPorId(@PathVariable Long id) {
        ItemDTO itemDTO = itemService.buscarPorId(id);
        return ResponseEntity.ok(itemDTO);
    }

    @PostMapping
    public ResponseEntity<ItemDTO> salvar(@RequestBody ItemDTO itemDTO) {
        ItemDTO salvo = itemService.salvar(itemDTO);
        return new ResponseEntity<>(salvo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> atualizar(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        itemDTO.setId(id);
        ItemDTO atualizado = itemService.salvar(itemDTO);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        itemService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
