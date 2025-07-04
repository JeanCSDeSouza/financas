package br.com.financas.fatura_api.service;

import br.com.financas.fatura_api.model.TransacaoFatura;
import br.com.financas.fatura_api.repository.TransacaoFaturaRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class FaturaService {

    @Autowired
    private TransacaoFaturaRepository repository;
    private final Pattern RECONHECE_LANCAMENTO_FINANCEIRO_FATURA = Pattern.compile("(\\d{2}/\\d{2})\\s+(.+?)\\s+R\\$\\s*(-?[\\d\\.\\u00A0,]+)");
    private final DateTimeFormatter FORMATAR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public void processarFatura(MultipartFile file) throws Exception {
        try (InputStream input = file.getInputStream(); RandomAccessRead rar = new RandomAccessReadBuffer(input);
             PDDocument doc = Loader.loadPDF(rar)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String texto = stripper.getText(doc);
            int anoAtual = LocalDate.now().getYear();

            List<TransacaoFatura> transacoes = RECONHECE_LANCAMENTO_FINANCEIRO_FATURA.matcher(texto)
                    .results()
                    .map(matcher -> {
                        String dataParcial = matcher.group(1);
                        String descricao = matcher.group(2).trim();
                        String valorStr = matcher.group(3)
                                .replace("R$", "")
                                .replace("\u00A0", "")
                                .replaceAll("[^\\d,.-]", "")
                                .replace(".", "")
                                .replace(",", ".")
                                .trim();
                        LocalDate data = LocalDate.parse(dataParcial + "/" + anoAtual, FORMATAR_DATA);
                        double valor = Double.parseDouble(valorStr);

                        String tipo = Stream.of(
                                        Map.entry("encargo", List.of("iof", "encargos")),
                                        Map.entry("parcelado", List.of("parcelado")),
                                        Map.entry("internacional", List.of("internacional", "exterior")),
                                        Map.entry("credito", List.of("pagamento", "ajuste"))
                                )
                                .filter(entry -> entry.getValue().stream().anyMatch(descricao.toLowerCase()::contains))
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElse("compra");

                        return new TransacaoFatura(null, data, descricao, valor, tipo);
                    })
                    .toList();

            repository.saveAll(transacoes);
        }
    }

    public List<TransacaoFatura> listarTransacoes() {
        return repository.findAll();
    }
    public Double listarTransacoes(List<TransacaoFatura> fatura) {
        double soma = fatura.stream()
                .filter(t -> !t.getDescricao().contains("PGTO"))
                .mapToDouble(TransacaoFatura::getValor)
                .sum();

        return BigDecimal.valueOf(soma)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

    }
}