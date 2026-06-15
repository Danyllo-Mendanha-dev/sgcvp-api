package ifmt.cba.sgcvp.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ifmt.cba.sgcvp.dto.PedidoVendaDTO;
import ifmt.cba.sgcvp.dto.ProcessarPedidoVendaDTO;
import ifmt.cba.sgcvp.exception.EstoqueInsuficienteException;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.NotValidDataException;
import ifmt.cba.sgcvp.exception.TransicaoEstadoInvalidaException;
import ifmt.cba.sgcvp.negocio.PedidoVendaNegocio;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("/pedido-venda")
public class PedidoVendaController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoVendaController.class);

    @Autowired
    private PedidoVendaNegocio pedidoVendaNegocio;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CollectionModel<EntityModel<PedidoVendaDTO>> buscarTodos() throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar todos os pedidos de venda");
        List<EntityModel<PedidoVendaDTO>> listaPedidoVendaTempDTO = pedidoVendaNegocio.pesquisaTodos().stream()
                .map(this::adicionarLinks)
                .collect(Collectors.toList());

        return CollectionModel.of(listaPedidoVendaTempDTO,
                linkTo(PedidoVendaController.class).withSelfRel());
    }

    @GetMapping(value = "/codigo/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoVendaDTO> buscarPorID(@PathVariable("codigo") int codigo)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar pedido de venda por codigo {}", codigo);
        PedidoVendaDTO pedidoVendaTempDTO = pedidoVendaNegocio.pesquisaCodigo(codigo);
        return this.adicionarLinks(pedidoVendaTempDTO);
    }

    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CollectionModel<EntityModel<PedidoVendaDTO>> buscarPorStatus(
            @Parameter(description = "Status do pedido de venda. Valores esperados: SOLICITADO, APROVADO_ESTOQUE, PENDENTE_ESTOQUE, APROVADO_VENDA, REPROVADO_VENDA, PEDIDO_PROGRAMADO ou PROCESSADO", required = true)
            @PathVariable("status") String status)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar pedidos de venda por status {}", status);
        List<EntityModel<PedidoVendaDTO>> listaPedidoVendaTempDTO = pedidoVendaNegocio.pesquisaPorStatus(status).stream()
                .map(this::adicionarLinks)
                .collect(Collectors.toList());

        return CollectionModel.of(listaPedidoVendaTempDTO,
                linkTo(PedidoVendaController.class).slash("status").slash(status).withSelfRel(),
                linkTo(PedidoVendaController.class).withRel("pedidos-venda"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoVendaDTO> inserirPedidoVenda(@Valid @RequestBody PedidoVendaDTO pedidoVendaDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para inserir pedido de venda");
        PedidoVendaDTO pedidoVendaTempDTO = pedidoVendaNegocio.inserir(pedidoVendaDTO);
        return this.adicionarLinks(pedidoVendaTempDTO);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoVendaDTO> alterarPedidoVenda(@Valid @RequestBody PedidoVendaDTO pedidoVendaDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para alterar pedido de venda codigo {}", pedidoVendaDTO.getCodigo());
        PedidoVendaDTO pedidoVendaTempDTO = pedidoVendaNegocio.alterar(pedidoVendaDTO);
        return this.adicionarLinks(pedidoVendaTempDTO);
    }

    @DeleteMapping(value = "/{codigo}")
    public ResponseEntity<?> excluirPedidoVenda(@PathVariable("codigo") int codigo)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para excluir pedido de venda codigo {}", codigo);
        pedidoVendaNegocio.excluir(codigo);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{codigo}/aprovar-estoque", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoVendaDTO> aprovarEstoque(@PathVariable("codigo") int codigo)
            throws NotFoundException, TransicaoEstadoInvalidaException, EstoqueInsuficienteException {
        logger.info("Requisicao para aprovar estoque do pedido de venda codigo {}", codigo);
        PedidoVendaDTO pedidoVendaTempDTO = pedidoVendaNegocio.aprovarEstoque(codigo);
        return this.adicionarLinks(pedidoVendaTempDTO);
    }

    @PatchMapping(value = "/{codigo}/pendente-estoque", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoVendaDTO> pendenteEstoque(@PathVariable("codigo") int codigo)
            throws NotFoundException, TransicaoEstadoInvalidaException {
        logger.info("Requisicao para deixar pedido de venda pendente de estoque codigo {}", codigo);
        PedidoVendaDTO pedidoVendaTempDTO = pedidoVendaNegocio.pendenteEstoque(codigo);
        return this.adicionarLinks(pedidoVendaTempDTO);
    }

    @PatchMapping(value = "/{codigo}/aprovar-venda", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoVendaDTO> aprovarVenda(@PathVariable("codigo") int codigo)
            throws NotFoundException, TransicaoEstadoInvalidaException {
        logger.info("Requisicao para aprovar venda do pedido codigo {}", codigo);
        PedidoVendaDTO pedidoVendaTempDTO = pedidoVendaNegocio.aprovarVenda(codigo);
        return this.adicionarLinks(pedidoVendaTempDTO);
    }

    @PatchMapping(value = "/{codigo}/reprovar-venda", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoVendaDTO> reprovarVenda(@PathVariable("codigo") int codigo)
            throws NotFoundException, TransicaoEstadoInvalidaException {
        logger.info("Requisicao para reprovar venda do pedido codigo {}", codigo);
        PedidoVendaDTO pedidoVendaTempDTO = pedidoVendaNegocio.reprovarVenda(codigo);
        return this.adicionarLinks(pedidoVendaTempDTO);
    }

    @PatchMapping(value = "/{codigo}/processar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoVendaDTO> processar(@PathVariable("codigo") int codigo,
            @Valid @RequestBody ProcessarPedidoVendaDTO processarPedidoVendaDTO)
            throws NotFoundException, TransicaoEstadoInvalidaException, EstoqueInsuficienteException,
            NotValidDataException {
        logger.info("Requisicao para processar pedido de venda codigo {}", codigo);
        PedidoVendaDTO pedidoVendaTempDTO = pedidoVendaNegocio.processar(codigo,
                processarPedidoVendaDTO.getPercentualComissao());
        return this.adicionarLinks(pedidoVendaTempDTO);
    }

    private EntityModel<PedidoVendaDTO> adicionarLinks(PedidoVendaDTO pedidoVendaDTO) {
        return EntityModel.of(pedidoVendaDTO,
                linkTo(PedidoVendaController.class).slash("codigo").slash(pedidoVendaDTO.getCodigo()).withSelfRel(),
                linkTo(PedidoVendaController.class).withRel("pedidos-venda"));
    }
}
