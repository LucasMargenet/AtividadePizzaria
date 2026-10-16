package com.umc.pediupatrao.entity;

// Snapshot do produto no momento do pedido: nome e preço ficam congelados
// aqui para que uma alteração futura no cadastro de Produto não mude o
// valor de pedidos já registrados.
public class ItemPedido {

    private String produtoId;
    private String nomeProduto;
    private double precoUnitario;
    private int quantidade;

    public ItemPedido() {
    }

    public ItemPedido(String produtoId, String nomeProduto, double precoUnitario, int quantidade) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
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

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getSubtotal() {
        return precoUnitario * quantidade;
    }
}
