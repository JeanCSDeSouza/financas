package br.com.financas.extrato_api.service;

import br.com.financas.extrato_api.model.Transacao;
import br.com.financas.extrato_api.repository.TransacaoRepository;
import br.com.financas.extrato_api.util.CsvColumn;
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

    private final TransacaoRepository transacaoRepository;
    private static final String RECONHECE_VALOR_MONETARIO = "^[+-]?\\d+(\\.\\d+)?$";
    private static final String CSV_SEPARADOR_COLUNAS = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    public ExtratoService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    public void processarArquivo(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.ISO_8859_1))) {
            String linha;
            boolean primeiraLinha = true;

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

        return Optional.of(campos)
                .filter(cs -> getPredicateMap(campos).values().stream().allMatch(v -> v.test(cs)))
                .map(cs -> {
                    LocalDate data = LocalDate.parse(cleanAndTrimCsvField(cs[0]), formatter);
                    double valor = Double.parseDouble(replaceCommaWithPeriod(cs[4]));
                    return Transacao.builder()
                            .id(null)
                            .data(data)
                            .lancamento(cleanAndTrimCsvField(cs[CsvColumn.LANCAMENTO.getIndex()]))
                            .detalhes(cleanAndTrimCsvField(cs[CsvColumn.DETALHES.getIndex()]))
                            .numeroDocumento(cleanAndTrimCsvField(cs[CsvColumn.NUMERO_DOCUMENTO.getIndex()]))
                            .valor(valor)
                            .tipoLancamento(cleanAndTrimCsvField(cs[CsvColumn.TIPO_LANCAMENTO.getIndex()]))
                            .build();

                });
    }

    private static Map<String, Predicate<String[]>> getPredicateMap(String[] campos) {
        return Map.of(
                "tamanho", campo -> campo.length >= 6,
                "tipoLancamentoPresente", campo -> !campos[CsvColumn.TIPO_LANCAMENTO.getIndex()].trim().isEmpty(),
                "dataValida", campo -> {
                    String dataStr = campos[CsvColumn.DATA.getIndex()].replace("\"", "").trim();
                    return !(dataStr.isEmpty() || dataStr.equals("00/00/0000"));
                },
                "valorValido", campo -> {
                    String valorStr = campo[CsvColumn.VALOR.getIndex() ].replace("\"", "").replace(",", ".").trim();
                    return valorStr.matches(RECONHECE_VALOR_MONETARIO);
                }
        );
    }
    private static String cleanAndTrimCsvField(String campo){
        return campo.replace("\"", "").trim();
    }
    private static String replaceCommaWithPeriod(String campo){
        return campo.replace("\"", "").replace(",", ".").trim();
    }

    public List<Transacao> getExtrato() {
        return transacaoRepository.findAll();
    }
}

