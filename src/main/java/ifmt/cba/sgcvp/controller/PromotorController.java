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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ifmt.cba.sgcvp.dto.PromotorDTO;
import ifmt.cba.sgcvp.dto.MunicipioDTO;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.NotValidDataException;
import ifmt.cba.sgcvp.negocio.PromotorNegocio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("/promotor")
@Tag(name = "Promotores", description = "Operacoes relacionadas ao gerenciamento de promotores de venda.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operacao realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos enviados na requisicao"),
        @ApiResponse(responseCode = "404", description = "Recurso nao encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
})
public class PromotorController {

    private static final Logger logger = LoggerFactory.getLogger(PromotorController.class);

    @Autowired
    private PromotorNegocio promotorNegocio;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar promotores", description = "Retorna todos os promotores cadastrados.")
    public CollectionModel<EntityModel<PromotorDTO>> buscarTodos() throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar todos os promotores");
        List<EntityModel<PromotorDTO>> listaPromotorTempDTO = promotorNegocio.pesquisaTodos().stream()
                .map(this::adicionarLinks)
                .collect(Collectors.toList());

        return CollectionModel.of(listaPromotorTempDTO,
                linkTo(PromotorController.class).withSelfRel());
    }

    @GetMapping(value = "/codigo/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar promotor por codigo", description = "Retorna um promotor a partir do seu codigo identificador.")
    public EntityModel<PromotorDTO> buscarPorID(
            @Parameter(description = "Codigo do promotor", example = "1", required = true)
            @PathVariable("codigo") int codigo)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar promotor por codigo {}", codigo);
        PromotorDTO promotorTempDTO = promotorNegocio.pesquisaCodigo(codigo);
        return this.adicionarLinks(promotorTempDTO);
    }

    @GetMapping(value = "/nome/{nome}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar promotor por nome", description = "Retorna um promotor pelo nome informado.")
    public EntityModel<PromotorDTO> buscarPorNome(
            @Parameter(description = "Nome ou parte inicial do nome do promotor", example = "Maria", required = true)
            @PathVariable("nome") String nome)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar promotor por nome {}", nome);
        PromotorDTO promotorTempDTO = promotorNegocio.pesquisaPorNome(nome);
        return this.adicionarLinks(promotorTempDTO);
    }

    @GetMapping(value = "/{codigo}/municipios", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar municipios do promotor", description = "Retorna os municipios atendidos por um promotor.")
    public CollectionModel<EntityModel<MunicipioDTO>> buscarMunicipiosAtendidos(
            @Parameter(description = "Codigo do promotor de venda", required = true)
            @PathVariable("codigo") int codigo)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar municipios atendidos pelo promotor {}", codigo);
        List<EntityModel<MunicipioDTO>> listaMunicipioTempDTO = promotorNegocio.pesquisaMunicipiosAtendidos(codigo)
                .stream()
                .map(municipioDTO -> EntityModel.of(municipioDTO))
                .collect(Collectors.toList());

        return CollectionModel.of(listaMunicipioTempDTO,
                linkTo(PromotorController.class).slash(codigo).slash("municipios").withSelfRel());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cadastrar promotor", description = "Cadastra um novo promotor de venda.")
    public EntityModel<PromotorDTO> inserirPromotor(@Valid @RequestBody PromotorDTO promotorDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para inserir promotor");
        PromotorDTO promotorTempDTO = promotorNegocio.inserir(promotorDTO);
        return this.adicionarLinks(promotorTempDTO);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Alterar promotor", description = "Atualiza os dados de um promotor existente.")
    public EntityModel<PromotorDTO> alterarPromotor(@Valid @RequestBody PromotorDTO promotorDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para alterar promotor codigo {}", promotorDTO.getCodigo());
        PromotorDTO promotorTempDTO = promotorNegocio.alterar(promotorDTO);
        return this.adicionarLinks(promotorTempDTO);
    }

    @DeleteMapping(value = "/{codigo}")
    @Operation(summary = "Excluir promotor", description = "Remove um promotor pelo codigo informado.")
    public ResponseEntity<?> excluirPromotor(
            @Parameter(description = "Codigo do promotor", example = "1", required = true)
            @PathVariable("codigo") int codigo)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para excluir promotor codigo {}", codigo);
        promotorNegocio.excluir(codigo);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<PromotorDTO> adicionarLinks(PromotorDTO promotorDTO) {
        return EntityModel.of(promotorDTO,
                linkTo(PromotorController.class).slash("codigo").slash(promotorDTO.getCodigo()).withSelfRel(),
                linkTo(PromotorController.class).withRel("promotores"));
    }
}
