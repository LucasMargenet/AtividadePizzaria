package com.umc.pediupatrao.dto;

import java.time.LocalDate;

public class VendaPeriodoDTO {

    private LocalDate data;
    private long quantidadePedidos;
    private double faturamento;

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public long getQuantidadePedidos() {
        return quantidadePedidos;
    }

    public void setQuantidadePedidos(long quantidadePedidos) {
        this.quantidadePedidos = quantidadePedidos;
    }

    public double getFaturamento() {
        return faturamento;
    }

    public void setFaturamento(double faturamento) {
        this.faturamento = faturamento;
    }
}
