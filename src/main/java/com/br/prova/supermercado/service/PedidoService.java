package com.br.prova.supermercado.service;

import com.br.prova.supermercado.dto.PedidoDTO;
import com.br.prova.supermercado.dto.ItemDTO;
import com.br.prova.supermercado.dto.ProdutoDTO;
import com.br.prova.supermercado.model.Item;
import com.br.prova.supermercado.model.Pedido;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.repository.PedidoRepository;
import com.br.prova.supermercado.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public PedidoDTO salvar(PedidoDTO pedidoDTO) {
        Pedido pedido = new Pedido();

        pedido.setListaItens(
                pedidoDTO.getListaItens().stream().map(itemDTO -> {
                    Produto produto = produtoRepository.findById(itemDTO.getProduto().getId())
                            .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemDTO.getProduto().getId()));

                    if (produto.getQuantidadeEmEstoque() < itemDTO.getQuantidade()) {
                        throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
                    }

                    produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() - itemDTO.getQuantidade());
                    produtoRepository.save(produto);

                    Item item = new Item();
                    item.setProduto(produto);
                    item.setQuantidade(itemDTO.getQuantidade());
                    item.setPreco(produto.getPreco() * itemDTO.getQuantidade());
                    item.setPedido(pedido);
                    return item;
                }).collect(Collectors.toList())
        );

        pedido.calcularValorTotal();
        pedidoRepository.save(pedido);

        return mapearParaDTO(pedido);
    }

    public List<ItemDTO> listarItensPorPedidoId(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));
        return pedido.getListaItens().stream()
                .map(item -> ItemDTO.builder()
                        .id(item.getId())
                        .quantidade(item.getQuantidade())
                        .preco(item.getPreco())
                        .produto(ProdutoDTO.builder()
                                .id(item.getProduto().getId())
                                .nome(item.getProduto().getNome())
                                .preco(item.getProduto().getPreco())
                                .quantidadeEmEstoque(item.getProduto().getQuantidadeEmEstoque())
                                .estoqueId(item.getProduto().getEstoque() != null ? item.getProduto().getEstoque().getId() : null)
                                .build())
                        .build())
                .collect(Collectors.toList());
    }

    private PedidoDTO mapearParaDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setValorTotalDoPedido(pedido.getValorTotalDoPedido());
        dto.setListaItens(
                pedido.getListaItens().stream().map(item -> {
                    Produto produto = item.getProduto();
                    return ItemDTO.builder()
                            .id(item.getId())
                            .quantidade(item.getQuantidade())
                            .preco(item.getPreco())
                            .produto(ProdutoDTO.builder()
                                    .id(produto.getId())
                                    .nome(produto.getNome())
                                    .preco(produto.getPreco())
                                    .quantidadeEmEstoque(produto.getQuantidadeEmEstoque())
                                    .estoqueId(produto.getEstoque() != null ? produto.getEstoque().getId() : null)
                                    .build())
                            .build();
                }).collect(Collectors.toList())
        );
        return dto;
    }
}