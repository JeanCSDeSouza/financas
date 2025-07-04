package br.com.financas.fatura_api.repository;

import br.com.financas.fatura_api.model.TransacaoFatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoFaturaRepository extends JpaRepository<TransacaoFatura, String> {}
