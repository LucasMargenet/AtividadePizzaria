package com.umc.pediupatrao.dto;

public class ProdutoMaisVendidoDTO {

    private String produtoId;
    private String nomeProduto;
    private int quantidadeVendida;
    private double faturamento;

    public ProdutoMaisVendidoDTO() {
    }

    public ProdutoMaisVendidoDTO(String produtoId, String nomeProduto, int quantidadeVendida, double faturamento) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidadeVendida = quantidadeVendida;
        this.faturamento = faturamento;
    }

    public String getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(String produtoId) {
        this.produtoId = produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public int getQuantidadeVendida() {
        return quantidadeVendida;
    }

    public void setQuantidadeVendida(int quantidadeVendida) {
        this.quantidadeVendida = quantidadeVendida;
    }

    public double getFaturamento() {
        return faturamento;
    }

    public void setFaturamento(double faturamento) {
        this.faturamento = faturamento;
    }
}
