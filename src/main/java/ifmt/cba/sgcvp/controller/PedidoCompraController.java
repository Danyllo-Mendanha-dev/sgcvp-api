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

import ifmt.cba.sgcvp.dto.PedidoCompraDTO;
import ifmt.cba.sgcvp.exception.EstoqueInsuficienteException;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.NotValidDataException;
import ifmt.cba.sgcvp.exception.TransicaoEstadoInvalidaException;
import ifmt.cba.sgcvp.negocio.PedidoCompraNegocio;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("/pedido-compra")
public class PedidoCompraController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoCompraController.class);

    @Autowired
    private PedidoCompraNegocio pedidoCompraNegocio;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CollectionModel<EntityModel<PedidoCompraDTO>> buscarTodos() throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar todos os pedidos de compra");
        List<EntityModel<PedidoCompraDTO>> listaPedidoCompraTempDTO = pedidoCompraNegocio.pesquisaTodos().stream()
                .map(this::adicionarLinks)
                .collect(Collectors.toList());

        return CollectionModel.of(listaPedidoCompraTempDTO,
                linkTo(PedidoCompraController.class).withSelfRel());
    }

    @GetMapping(value = "/codigo/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoCompraDTO> buscarPorID(@PathVariable("codigo") int codigo)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar pedido de compra por codigo {}", codigo);
        PedidoCompraDTO pedidoCompraTempDTO = pedidoCompraNegocio.pesquisaCodigo(codigo);
        return this.adicionarLinks(pedidoCompraTempDTO);
    }

    @GetMapping(value = "/nota-fiscal/{numNotaFiscal}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoCompraDTO> buscarPorNotaFiscal(@PathVariable("numNotaFiscal") String numNotaFiscal)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar pedido de compra por nota fiscal {}", numNotaFiscal);
        PedidoCompraDTO pedidoCompraTempDTO = pedidoCompraNegocio.pesquisaPorNotaFiscal(numNotaFiscal);
        return this.adicionarLinks(pedidoCompraTempDTO);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoCompraDTO> inserirPedidoCompra(@Valid @RequestBody PedidoCompraDTO pedidoCompraDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para inserir pedido de compra");
        PedidoCompraDTO pedidoCompraTempDTO = pedidoCompraNegocio.inserir(pedidoCompraDTO);
        return this.adicionarLinks(pedidoCompraTempDTO);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoCompraDTO> alterarPedidoCompra(@Valid @RequestBody PedidoCompraDTO pedidoCompraDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para alterar pedido de compra codigo {}", pedidoCompraDTO.getCodigo());
        PedidoCompraDTO pedidoCompraTempDTO = pedidoCompraNegocio.alterar(pedidoCompraDTO);
        return this.adicionarLinks(pedidoCompraTempDTO);
    }

    @DeleteMapping(value = "/{codigo}")
    public ResponseEntity<?> excluirPedidoCompra(@PathVariable("codigo") int codigo)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para excluir pedido de compra codigo {}", codigo);
        pedidoCompraNegocio.excluir(codigo);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{codigo}/conferir", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoCompraDTO> conferir(@PathVariable("codigo") int codigo)
            throws NotFoundException, TransicaoEstadoInvalidaException {
        logger.info("Requisicao para conferir pedido de compra codigo {}", codigo);
        PedidoCompraDTO pedidoCompraTempDTO = pedidoCompraNegocio.conferir(codigo);
        return this.adicionarLinks(pedidoCompraTempDTO);
    }

    @PatchMapping(value = "/{codigo}/processar", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PedidoCompraDTO> processar(@PathVariable("codigo") int codigo)
            throws NotFoundException, TransicaoEstadoInvalidaException, EstoqueInsuficienteException {
        logger.info("Requisicao para processar pedido de compra codigo {}", codigo);
        PedidoCompraDTO pedidoCompraTempDTO = pedidoCompraNegocio.processar(codigo);
        return this.adicionarLinks(pedidoCompraTempDTO);
    }

    private EntityModel<PedidoCompraDTO> adicionarLinks(PedidoCompraDTO pedidoCompraDTO) {
        return EntityModel.of(pedidoCompraDTO,
                linkTo(PedidoCompraController.class).slash("codigo").slash(pedidoCompraDTO.getCodigo()).withSelfRel(),
                linkTo(PedidoCompraController.class).withRel("pedidos-compra"));
    }
}
