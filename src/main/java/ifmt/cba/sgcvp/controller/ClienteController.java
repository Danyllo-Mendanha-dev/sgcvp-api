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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("/cliente")
@Tag(name = "Clientes", description = "Operacoes relacionadas ao gerenciamento de clientes.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operacao realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos enviados na requisicao"),
        @ApiResponse(responseCode = "404", description = "Recurso nao encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
})
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    @Autowired
    private ClienteNegocio clienteNegocio;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar clientes", description = "Retorna todos os clientes cadastrados.")
    public CollectionModel<EntityModel<ClienteDTO>> buscarTodos() throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar todos os clientes");
        List<EntityModel<ClienteDTO>> listaClienteTempDTO = clienteNegocio.pesquisaTodos().stream()
                .map(this::adicionarLinks)
                .collect(Collectors.toList());

        return CollectionModel.of(listaClienteTempDTO,
                linkTo(ClienteController.class).withSelfRel());
    }

    @GetMapping(value = "/codigo/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar cliente por codigo", description = "Retorna um cliente a partir do seu codigo identificador.")
    public EntityModel<ClienteDTO> buscarPorID(
            @Parameter(description = "Codigo do cliente", example = "1", required = true)
            @PathVariable("codigo") int codigo)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar cliente por codigo {}", codigo);
        ClienteDTO clienteTempDTO = clienteNegocio.pesquisaCodigo(codigo);
        return this.adicionarLinks(clienteTempDTO);
    }

    @GetMapping(value = "/razao-social/{razaoSocial}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar cliente por razao social", description = "Retorna um cliente pela razao social informada.")
    public EntityModel<ClienteDTO> buscarPorRazaoSocial(
            @Parameter(description = "Razao social do cliente", example = "Mercado Central Ltda", required = true)
            @PathVariable("razaoSocial") String razaoSocial)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar cliente por razao social {}", razaoSocial);
        ClienteDTO clienteTempDTO = clienteNegocio.pesquisaPorRazaoSocial(razaoSocial);
        return this.adicionarLinks(clienteTempDTO);
    }

    @GetMapping(value = "/cnpj/{CNPJ}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar cliente por CNPJ", description = "Retorna um cliente pelo CNPJ informado.")
    public EntityModel<ClienteDTO> buscarPorCNPJ(
            @Parameter(description = "CNPJ do cliente com 14 digitos, sem pontuacao", example = "12345678000199", required = true)
            @PathVariable("CNPJ") String CNPJ)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar cliente por CNPJ {}", CNPJ);
        ClienteDTO clienteTempDTO = clienteNegocio.pesquisaPorCNPJ(CNPJ);
        return this.adicionarLinks(clienteTempDTO);
    }

    @GetMapping(value = "/promotor/{codigoPromotor}/valor-vendido", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar clientes por valor vendido", description = "Retorna clientes de um promotor com o valor vendido dentro do periodo informado.")
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
    @Operation(summary = "Cadastrar cliente", description = "Cadastra um novo cliente.")
    public EntityModel<ClienteDTO> inserirCliente(@Valid @RequestBody ClienteDTO clienteDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para inserir cliente");
        ClienteDTO clienteTempDTO = clienteNegocio.inserir(clienteDTO);
        return this.adicionarLinks(clienteTempDTO);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Alterar cliente", description = "Atualiza os dados de um cliente existente.")
    public EntityModel<ClienteDTO> alterarCliente(@Valid @RequestBody ClienteDTO clienteDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para alterar cliente codigo {}", clienteDTO.getCodigo());
        ClienteDTO clienteTempDTO = clienteNegocio.alterar(clienteDTO);
        return this.adicionarLinks(clienteTempDTO);
    }

    @DeleteMapping(value = "/{codigo}")
    @Operation(summary = "Excluir cliente", description = "Remove um cliente pelo codigo informado.")
    public ResponseEntity<?> excluirCliente(
            @Parameter(description = "Codigo do cliente", example = "1", required = true)
            @PathVariable("codigo") int codigo)
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
