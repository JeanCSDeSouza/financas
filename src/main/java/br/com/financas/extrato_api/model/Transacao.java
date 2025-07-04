package br.com.financas.extrato_api.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "transacoes")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private String lancamento;

    private String detalhes;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    @Column(nullable = false)
    private double valor;

    @Column(name = "tipo_lancamento", nullable = false)
    private String tipoLancamento;

    @Column(nullable = false)
    private String categoria;

    public Transacao() {
    }

    public Transacao(Long id, LocalDate data, String lancamento, String detalhes,
                     String numeroDocumento, double valor, String tipoLancamento, String categoria) {
        this.id = id;
        this.data = data;
        this.lancamento = lancamento;
        this.detalhes = detalhes;
        this.numeroDocumento = numeroDocumento;
        this.valor = valor;
        this.tipoLancamento = tipoLancamento;
        this.categoria = categoria;
    }

    // Getters
    public Long getId() {
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

    public String getCategoria() {
        return categoria;
    }

    // Setters
    public void setId(Long id) {
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

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private LocalDate data;
        private String lancamento;
        private String detalhes;
        private String numeroDocumento;
        private double valor;
        private String tipoLancamento;
        private String categoria;

        public Builder id(Long id) {
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

        public Builder categoria(String categoria) {
            this.categoria = categoria;
            return this;
        }

        public Transacao build() {
            return new Transacao(id, data, lancamento, detalhes, numeroDocumento, valor, tipoLancamento, categoria);
        }
    }
}