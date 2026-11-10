package com.umc.pediupatrao.dto;

public class IndicadoresGeraisDTO {

    private long totalPedidos;
    private long pedidosFinalizados;
    private long pedidosCancelados;
    private double taxaCancelamento;
    private double faturamentoTotal;
    private double ticketMedio;

    public long getTotalPedidos() {
        return totalPedidos;
    }

    public void setTotalPedidos(long totalPedidos) {
        this.totalPedidos = totalPedidos;
    }

    public long getPedidosFinalizados() {
        return pedidosFinalizados;
    }

    public void setPedidosFinalizados(long pedidosFinalizados) {
        this.pedidosFinalizados = pedidosFinalizados;
    }

    public long getPedidosCancelados() {
        return pedidosCancelados;
    }

    public void setPedidosCancelados(long pedidosCancelados) {
        this.pedidosCancelados = pedidosCancelados;
    }

    public double getTaxaCancelamento() {
        return taxaCancelamento;
    }

    public void setTaxaCancelamento(double taxaCancelamento) {
        this.taxaCancelamento = taxaCancelamento;
    }

    public double getFaturamentoTotal() {
        return faturamentoTotal;
    }

    public void setFaturamentoTotal(double faturamentoTotal) {
        this.faturamentoTotal = faturamentoTotal;
    }

    public double getTicketMedio() {
        return ticketMedio;
    }

    public void setTicketMedio(double ticketMedio) {
        this.ticketMedio = ticketMedio;
    }
}
