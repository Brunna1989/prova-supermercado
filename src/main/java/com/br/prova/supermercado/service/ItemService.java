package com.br.prova.supermercado.service;

import com.br.prova.supermercado.dto.ItemDTO;
import com.br.prova.supermercado.exception.EstoqueNaoEncontradoException;
import com.br.prova.supermercado.exception.ProdutoNaoEncontradoException;
import com.br.prova.supermercado.mapper.ItemMapper;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Item;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.repository.EstoqueRepository;
import com.br.prova.supermercado.repository.ItemRepository;
import com.br.prova.supermercado.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;

    public List<ItemDTO> listarTodos() {
        return itemRepository.findAll()
                .stream()
                .map(ItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ItemDTO buscarPorId(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
        return ItemMapper.toDTO(item);
    }

    public ItemDTO salvar(ItemDTO dto) {
        Long produtoId = dto.getProduto().getId();

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(produtoId));

        Estoque estoque = produto.getEstoque();
        if (estoque == null) {
            throw new EstoqueNaoEncontradoException(produtoId);
        }

        Item item = ItemMapper.toEntity(dto, estoque);
        Item salvo = itemRepository.save(item);
        return ItemMapper.toDTO(salvo);
    }

    public void deletar(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ProdutoNaoEncontradoException(id);
        }
        itemRepository.deleteById(id);
    }
}
