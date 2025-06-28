package br.com.financas.extrato_api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Document(collection = "transacoes")
public class Transacao {

    @Id
    private String id;

    private LocalDate data;
    private String lancamento;
    private String detalhes;
    private String numeroDocumento;
    private double valor;
    private String tipoLancamento;

    public Transacao() {
    }

    public Transacao(String id, LocalDate data, String lancamento, String detalhes,
                     String numeroDocumento, double valor, String tipoLancamento) {
        this.id = id;
        this.data = data;
        this.lancamento = lancamento;
        this.detalhes = detalhes;
        this.numeroDocumento = numeroDocumento;
        this.valor = valor;
        this.tipoLancamento = tipoLancamento;
    }

    // Getters
    public String getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public String getLancamento() {
        return lancamento;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public double getValor() {
        return valor;
    }

    public String getTipoLancamento() {
        return tipoLancamento;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setLancamento(String lancamento) {
        this.lancamento = lancamento;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setTipoLancamento(String tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private LocalDate data;
        private String lancamento;
        private String detalhes;
        private String numeroDocumento;
        private double valor;
        private String tipoLancamento;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder data(LocalDate data) {
            this.data = data;
            return this;
        }

        public Builder lancamento(String lancamento) {
            this.lancamento = lancamento;
            return this;
        }

        public Builder detalhes(String detalhes) {
            this.detalhes = detalhes;
            return this;
        }

        public Builder numeroDocumento(String numeroDocumento) {
            this.numeroDocumento = numeroDocumento;
            return this;
        }

        public Builder valor(double valor) {
            this.valor = valor;
            return this;
        }

        public Builder tipoLancamento(String tipoLancamento) {
            this.tipoLancamento = tipoLancamento;
            return this;
        }

        public Transacao build() {
            return new Transacao(id, data, lancamento, detalhes, numeroDocumento, valor, tipoLancamento);
        }
    }
}
