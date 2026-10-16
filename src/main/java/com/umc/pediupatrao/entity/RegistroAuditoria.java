package com.umc.pediupatrao.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// Registro imutável de auditoria (RF05). Não existe endpoint de alteração
// ou exclusão para esta coleção: só é possível criar (via AuditoriaService)
// e consultar (ADMIN/GERENTE).
@Document(collection = "auditoria")
public class RegistroAuditoria {

    @Id
    private String id;

    private String usuarioId;
    private String usuarioUsername;
    private String perfil;
    private LocalDateTime dataHora;

    private TipoOperacaoAuditoria tipoOperacao;
    private String entidade;
    private String entidadeId;

    private List<AlteracaoCampo> alteracoes = new ArrayList<>();
    private String justificativa;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioUsername() {
        return usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public TipoOperacaoAuditoria getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacaoAuditoria tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public String getEntidade() {
        return entidade;
    }

    public void setEntidade(String entidade) {
        this.entidade = entidade;
    }

    public String getEntidadeId() {
        return entidadeId;
    }

    public void setEntidadeId(String entidadeId) {
        this.entidadeId = entidadeId;
    }

    public List<AlteracaoCampo> getAlteracoes() {
        return alteracoes;
    }

    public void setAlteracoes(List<AlteracaoCampo> alteracoes) {
        this.alteracoes = alteracoes;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}
