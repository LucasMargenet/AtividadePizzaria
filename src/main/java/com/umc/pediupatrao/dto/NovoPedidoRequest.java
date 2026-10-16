package com.umc.pediupatrao.dto;

import java.util.List;

// DTO de entrada para criação de pedido: contém apenas o que o cliente da
// requisição pode informar (cliente e itens). Preço, total, status,
// responsável e data/hora são sempre calculados/atribuídos no servidor.
public class NovoPedidoRequest {

    private String clienteId;
    private List<ItemPedidoRequest> itens;

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public List<ItemPedidoRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequest> itens) {
        this.itens = itens;
    }
}
