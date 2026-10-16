package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.AlteracaoCampo;
import com.umc.pediupatrao.entity.RegistroAuditoria;
import com.umc.pediupatrao.entity.TipoOperacaoAuditoria;
import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.AuditoriaRepository;
import com.umc.pediupatrao.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Serviço de auditoria (RF05). Propositalmente não expõe nenhum método de
// atualização ou remoção: uma vez criado, um registro é permanente.
@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void registrar(String usernameAtor, TipoOperacaoAuditoria tipoOperacao, String entidade,
            String entidadeId, List<AlteracaoCampo> alteracoes, String justificativa) {
        if (alteracoes == null || alteracoes.isEmpty()) {
            return; // nada mudou de fato, não polui a auditoria
        }

        RegistroAuditoria registro = new RegistroAuditoria();
        usuarioRepository.findByUsername(usernameAtor).ifPresent(usuario -> {
            registro.setUsuarioId(usuario.getId());
            registro.setPerfil(usuario.getRole() != null ? usuario.getRole().name() : null);
        });
        registro.setUsuarioUsername(usernameAtor);
        registro.setDataHora(LocalDateTime.now());
        registro.setTipoOperacao(tipoOperacao);
        registro.setEntidade(entidade);
        registro.setEntidadeId(entidadeId);
        registro.setAlteracoes(alteracoes);
        registro.setJustificativa(justificativa);

        auditoriaRepository.save(registro);
    }

    public List<RegistroAuditoria> listarTodos() {
        return auditoriaRepository.findAllByOrderByDataHoraDesc();
    }
}
