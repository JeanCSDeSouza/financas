package br.com.financas.extrato_api.service;

import br.com.financas.extrato_api.model.Transacao;
import br.com.financas.extrato_api.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class ExtratoService {

    @Autowired
    private TransacaoRepository transacaoRepository;
    private static final String RECONHECE_VALOR_MONETARIO = "^[+-]?\\d+(\\.\\d+)?$";
    private static final String CSV_SEPARADOR_COLUNAS = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void processarArquivo(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String linha;
            boolean primeiraLinha = true;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            List<Transacao> transacoes = reader.lines()
                    .skip(1) // pula o cabeçalho
                    .parallel()
                    .map(this::linesToTransactionBancoDoBrasil)
                    .flatMap(Optional::stream)
                    .toList();

            transacaoRepository.saveAll(transacoes);
        }
    catch (IOException e) {
            throw new RuntimeException("Erro ao processar o arquivo: " + e.getMessage(), e);
        }
    }

    /**
     * lê uma linha do csv, separa por campos realizando checagem de validade da linha e entao gera uma transacao
     * Os campos na linha sao:
     * 1 - Data (DD/MM/YYYY)
     * 2 - Lançamento
     * 3 - Detalhes
     * 4 - Numero do documento
     * 5 - Valor (pode ser negativo)
     * 6 - Tipo do lançamento
     *
     * @param linha
     * @return
     */
    private Optional<Transacao> linesToTransactionBancoDoBrasil(String linha) {
        //Splitar os campos da String que vêm do CSV
        String[] campos = linha.split(CSV_SEPARADOR_COLUNAS, -1);

        Map<String, Predicate<String[]>> validacoes = Map.of(
                "tamanho", campo -> campo.length >= 6,
                "tipoLancamentoPresente", campo -> !campos[5].trim().isEmpty(),
                "dataValida", campo -> {
                    String dataStr = campos[0].replace("\"", "").trim();
                    return !(dataStr.isEmpty() || dataStr.equals("00/00/0000"));
                },
                "valorValido", campo -> {
                    String valorStr = campo[4].replace("\"", "").replace(",", ".").trim();
                    return valorStr.matches(RECONHECE_VALOR_MONETARIO);
                }
        );
        return Optional.of(campos)
                .filter(cs -> validacoes.values().stream().allMatch(v -> v.test(cs)))
                .map(cs -> {
                    LocalDate data = LocalDate.parse(cs[0].replace("\"", "").trim(), formatter);
                    double valor = Double.parseDouble(cs[4].replace("\"", "").replace(",", ".").trim());
                    return Transacao.builder()
                            .id(null)
                            .data(data)
                            .lancamento(cs[1].replace("\"", "").trim())
                            .detalhes(cs[2].replace("\"", "").trim())
                            .numeroDocumento(cs[3].replace("\"", "").trim())
                            .valor(valor)
                            .tipoLancamento(cs[5].replace("\"", "").trim())
                            .build();

                });
    }
    public List<Transacao> getExtrato() {
        return transacaoRepository.findAll();
    }
}

