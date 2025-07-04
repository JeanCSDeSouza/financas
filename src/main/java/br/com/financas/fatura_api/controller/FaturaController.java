package br.com.financas.fatura_api.controller;

import br.com.financas.fatura_api.model.TransacaoFatura;
import br.com.financas.fatura_api.service.FaturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/fatura")
public class FaturaController {

    @Autowired
    private FaturaService faturaService;

    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String importar(@RequestParam("file") MultipartFile file) {
        try {
            faturaService.processarFatura(file);
            return "Fatura importada com sucesso.";
        } catch (Exception e) {
            return "Erro ao importar fatura: " + e.getMessage();
        }
    }

    @GetMapping("/listar")
    public List<TransacaoFatura> listar() {
        return faturaService.listarTransacoes();
    }

    @GetMapping("/total-fatura")
    public Double totalFatura(){
        return faturaService.listarTransacoes( faturaService.listarTransacoes() );
    }
}