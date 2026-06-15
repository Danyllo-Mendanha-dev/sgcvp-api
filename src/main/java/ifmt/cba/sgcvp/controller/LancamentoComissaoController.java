package ifmt.cba.sgcvp.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ifmt.cba.sgcvp.dto.LancamentoComissaoDTO;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.TransicaoEstadoInvalidaException;
import ifmt.cba.sgcvp.negocio.LancamentoComissaoNegocio;
import io.swagger.v3.oas.annotations.Parameter;

@RestController()
@RequestMapping("/lancamento-comissao")
public class LancamentoComissaoController {

    private static final Logger logger = LoggerFactory.getLogger(LancamentoComissaoController.class);

    @Autowired
    private LancamentoComissaoNegocio lancamentoComissaoNegocio;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CollectionModel<EntityModel<LancamentoComissaoDTO>> buscarTodos() throws NotFoundException {
        logger.info("Requisicao para buscar todos os lancamentos de comissao");
        List<EntityModel<LancamentoComissaoDTO>> listaLancamentoComissaoTempDTO = lancamentoComissaoNegocio
                .pesquisaTodos().stream()
                .map(this::adicionarLinks)
                .collect(Collectors.toList());

        return CollectionModel.of(listaLancamentoComissaoTempDTO,
                linkTo(LancamentoComissaoController.class).withSelfRel());
    }

    @GetMapping(value = "/codigo/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<LancamentoComissaoDTO> buscarPorID(@PathVariable("codigo") int codigo)
            throws NotFoundException {
        logger.info("Requisicao para buscar lancamento de comissao por codigo {}", codigo);
        LancamentoComissaoDTO lancamentoComissaoTempDTO = lancamentoComissaoNegocio.pesquisaCodigo(codigo);
        return this.adicionarLinks(lancamentoComissaoTempDTO);
    }

    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CollectionModel<EntityModel<LancamentoComissaoDTO>> buscarPorStatus(@PathVariable("status") String status)
            throws NotFoundException {
        logger.info("Requisicao para buscar lancamentos de comissao por status {}", status);
        List<EntityModel<LancamentoComissaoDTO>> listaLancamentoComissaoTempDTO = lancamentoComissaoNegocio
                .pesquisaPorStatus(status).stream()
                .map(this::adicionarLinks)
                .collect(Collectors.toList());

        return CollectionModel.of(listaLancamentoComissaoTempDTO,
                linkTo(LancamentoComissaoController.class).slash("status").slash(status).withSelfRel(),
                linkTo(LancamentoComissaoController.class).withRel("lancamentos-comissao"));
    }

    @GetMapping(value = "/lancadas/promotor/{codigoPromotor}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CollectionModel<EntityModel<LancamentoComissaoDTO>> buscarLancadasPorPeriodoPromotor(
            @Parameter(description = "Codigo do promotor de venda", required = true)
            @PathVariable("codigoPromotor") int codigoPromotor,
            @Parameter(description = "Data inicial do periodo no formato yyyy-MM-dd", required = true)
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @Parameter(description = "Data final do periodo no formato yyyy-MM-dd", required = true)
            @RequestParam("dataFinal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal)
            throws NotFoundException {
        logger.info("Requisicao para buscar comissoes lancadas do promotor {} entre {} e {}", codigoPromotor,
                dataInicial, dataFinal);
        List<EntityModel<LancamentoComissaoDTO>> listaLancamentoComissaoTempDTO = lancamentoComissaoNegocio
                .pesquisaLancadasPeriodoPromotor(dataInicial, dataFinal, codigoPromotor)
                .stream()
                .map(this::adicionarLinks)
                .collect(Collectors.toList());

        return CollectionModel.of(listaLancamentoComissaoTempDTO,
                linkTo(LancamentoComissaoController.class).slash("lancadas").slash("promotor").slash(codigoPromotor)
                        .withSelfRel(),
                linkTo(LancamentoComissaoController.class).withRel("lancamentos-comissao"));
    }

    @GetMapping(value = "/quitadas/promotor/{codigoPromotor}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CollectionModel<EntityModel<LancamentoComissaoDTO>> buscarQuitadasPorPeriodoPromotor(
            @Parameter(description = "Codigo do promotor de venda", required = true)
            @PathVariable("codigoPromotor") int codigoPromotor,
            @Parameter(description = "Data inicial do periodo no formato yyyy-MM-dd", required = true)
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @Parameter(description = "Data final do periodo no formato yyyy-MM-dd", required = true)
            @RequestParam("dataFinal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal)
            throws NotFoundException {
        logger.info("Requisicao para buscar comissoes quitadas do promotor {} entre {} e {}", codigoPromotor,
                dataInicial, dataFinal);
        List<EntityModel<LancamentoComissaoDTO>> listaLancamentoComissaoTempDTO = lancamentoComissaoNegocio
                .pesquisaQuitadasPeriodoPromotor(dataInicial, dataFinal, codigoPromotor)
                .stream()
                .map(this::adicionarLinks)
                .collect(Collectors.toList());

        return CollectionModel.of(listaLancamentoComissaoTempDTO,
                linkTo(LancamentoComissaoController.class).slash("quitadas").slash("promotor").slash(codigoPromotor)
                        .withSelfRel(),
                linkTo(LancamentoComissaoController.class).withRel("lancamentos-comissao"));
    }

    @PatchMapping(value = "/{codigo}/quitar", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<LancamentoComissaoDTO> quitarLancamentoComissao(@PathVariable("codigo") int codigo)
            throws NotFoundException, TransicaoEstadoInvalidaException {
        logger.info("Requisicao para quitar lancamento de comissao codigo {}", codigo);
        LancamentoComissaoDTO lancamentoComissaoTempDTO = lancamentoComissaoNegocio.quitar(codigo);
        return this.adicionarLinks(lancamentoComissaoTempDTO);
    }

    private EntityModel<LancamentoComissaoDTO> adicionarLinks(LancamentoComissaoDTO lancamentoComissaoDTO) {
        return EntityModel.of(lancamentoComissaoDTO,
                linkTo(LancamentoComissaoController.class).slash("codigo").slash(lancamentoComissaoDTO.getCodigo())
                        .withSelfRel(),
                linkTo(LancamentoComissaoController.class).withRel("lancamentos-comissao"));
    }
}
