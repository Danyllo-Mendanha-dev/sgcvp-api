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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ifmt.cba.sgcvp.dto.ClienteDTO;
import ifmt.cba.sgcvp.dto.ClienteValorVendidoDTO;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.NotValidDataException;
import ifmt.cba.sgcvp.negocio.ClienteNegocio;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("/cliente")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    @Autowired
    private ClienteNegocio clienteNegocio;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CollectionModel<EntityModel<ClienteDTO>> buscarTodos() throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar todos os clientes");
        List<EntityModel<ClienteDTO>> listaClienteTempDTO = clienteNegocio.pesquisaTodos().stream()
                .map(this::adicionarLinks)
                .collect(Collectors.toList());

        return CollectionModel.of(listaClienteTempDTO,
                linkTo(ClienteController.class).withSelfRel());
    }

    @GetMapping(value = "/codigo/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<ClienteDTO> buscarPorID(@PathVariable("codigo") int codigo)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar cliente por codigo {}", codigo);
        ClienteDTO clienteTempDTO = clienteNegocio.pesquisaCodigo(codigo);
        return this.adicionarLinks(clienteTempDTO);
    }

    @GetMapping(value = "/razao-social/{razaoSocial}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<ClienteDTO> buscarPorRazaoSocial(@PathVariable("razaoSocial") String razaoSocial)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar cliente por razao social {}", razaoSocial);
        ClienteDTO clienteTempDTO = clienteNegocio.pesquisaPorRazaoSocial(razaoSocial);
        return this.adicionarLinks(clienteTempDTO);
    }

    @GetMapping(value = "/cnpj/{CNPJ}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<ClienteDTO> buscarPorCNPJ(@PathVariable("CNPJ") String CNPJ)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar cliente por CNPJ {}", CNPJ);
        ClienteDTO clienteTempDTO = clienteNegocio.pesquisaPorCNPJ(CNPJ);
        return this.adicionarLinks(clienteTempDTO);
    }

    @GetMapping(value = "/promotor/{codigoPromotor}/valor-vendido", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteValorVendidoDTO> buscarClientesPorPromotorValorVendido(
            @Parameter(description = "Codigo do promotor de venda", required = true)
            @PathVariable("codigoPromotor") int codigoPromotor,
            @Parameter(description = "Data inicial do periodo no formato yyyy-MM-dd", required = true)
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @Parameter(description = "Data final do periodo no formato yyyy-MM-dd", required = true)
            @RequestParam("dataFinal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar clientes do promotor {} por valor vendido entre {} e {}",
                codigoPromotor, dataInicial, dataFinal);
        List<ClienteValorVendidoDTO> listaClienteValorVendidoTempDTO = clienteNegocio
                .pesquisaClientesPorPromotorValorVendido(codigoPromotor, dataInicial, dataFinal);
        return listaClienteValorVendidoTempDTO;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<ClienteDTO> inserirCliente(@Valid @RequestBody ClienteDTO clienteDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para inserir cliente");
        ClienteDTO clienteTempDTO = clienteNegocio.inserir(clienteDTO);
        return this.adicionarLinks(clienteTempDTO);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<ClienteDTO> alterarCliente(@Valid @RequestBody ClienteDTO clienteDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para alterar cliente codigo {}", clienteDTO.getCodigo());
        ClienteDTO clienteTempDTO = clienteNegocio.alterar(clienteDTO);
        return this.adicionarLinks(clienteTempDTO);
    }

    @DeleteMapping(value = "/{codigo}")
    public ResponseEntity<?> excluirCliente(@PathVariable("codigo") int codigo)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para excluir cliente codigo {}", codigo);
        clienteNegocio.excluir(codigo);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<ClienteDTO> adicionarLinks(ClienteDTO clienteDTO) {
        return EntityModel.of(clienteDTO,
                linkTo(ClienteController.class).slash("codigo").slash(clienteDTO.getCodigo()).withSelfRel(),
                linkTo(ClienteController.class).withRel("clientes"));
    }
}
