package br.com.financas.extrato_api.repository;

import br.com.financas.extrato_api.model.Transacao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoRepository extends MongoRepository<Transacao, String> {}