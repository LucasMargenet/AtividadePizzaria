package com.umc.pediupatrao.repository;

import com.umc.pediupatrao.entity.RegistroAuditoria;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

// Sem métodos de update/delete expostos: a auditoria é somente-criação (via
// AuditoriaService) e somente-leitura (via AuditoriaController).
@Repository
public interface AuditoriaRepository extends MongoRepository<RegistroAuditoria, String> {
    List<RegistroAuditoria> findAllByOrderByDataHoraDesc();
}
