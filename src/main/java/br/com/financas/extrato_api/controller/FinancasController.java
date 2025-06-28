package br.com.financas.extrato_api.controller;

import br.com.financas.extrato_api.model.Transacao;
import br.com.financas.extrato_api.model.dto.TransacaoDTO;
import br.com.financas.extrato_api.service.ExtratoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/financas")
class FinancasController {

    @Autowired
    private ExtratoService extratoService;

    @PostMapping("/carregar-extrato")
    public String carregarExtrato(@RequestParam("file") MultipartFile file) {
        try {
            extratoService.processarArquivo(file);
            return "Extrato carregado com sucesso!";
        } catch (Exception e) {
            return "Erro ao processar o arquivo: " + e.getMessage();
        }
    }

    @GetMapping("/visualisar-extrato")
    public List<TransacaoDTO> visualizarExtrato() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return extratoService.getExtrato()
                .stream()
                //.sorted(Comparator.comparing(Transacao::getData))
                .map(TransacaoDTO::from)
                .collect(Collectors.toList());
    }
}