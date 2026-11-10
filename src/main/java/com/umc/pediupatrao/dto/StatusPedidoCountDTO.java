package com.umc.pediupatrao.dto;

import com.umc.pediupatrao.entity.StatusPedido;

public class StatusPedidoCountDTO {

    private StatusPedido status;
    private long quantidade;

    public StatusPedidoCountDTO() {
    }

    public StatusPedidoCountDTO(StatusPedido status, long quantidade) {
        this.status = status;
        this.quantidade = quantidade;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(long quantidade) {
        this.quantidade = quantidade;
    }
}
