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
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("/promotor")
public class PromotorController {

    private static final Logger logger = LoggerFactory.getLogger(PromotorController.class);

    @Autowired
    private PromotorNegocio promotorNegocio;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CollectionModel<EntityModel<PromotorDTO>> buscarTodos() throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar todos os promotores");
        List<EntityModel<PromotorDTO>> listaPromotorTempDTO = promotorNegocio.pesquisaTodos().stream()
                .map(this::adicionarLinks)
                .collect(Collectors.toList());

        return CollectionModel.of(listaPromotorTempDTO,
                linkTo(PromotorController.class).withSelfRel());
    }

    @GetMapping(value = "/codigo/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PromotorDTO> buscarPorID(@PathVariable("codigo") int codigo)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar promotor por codigo {}", codigo);
        PromotorDTO promotorTempDTO = promotorNegocio.pesquisaCodigo(codigo);
        return this.adicionarLinks(promotorTempDTO);
    }

    @GetMapping(value = "/nome/{nome}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PromotorDTO> buscarPorNome(@PathVariable("nome") String nome)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar promotor por nome {}", nome);
        PromotorDTO promotorTempDTO = promotorNegocio.pesquisaPorNome(nome);
        return this.adicionarLinks(promotorTempDTO);
    }

    @GetMapping(value = "/{codigo}/municipios", produces = MediaType.APPLICATION_JSON_VALUE)
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
    public EntityModel<PromotorDTO> inserirPromotor(@Valid @RequestBody PromotorDTO promotorDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para inserir promotor");
        PromotorDTO promotorTempDTO = promotorNegocio.inserir(promotorDTO);
        return this.adicionarLinks(promotorTempDTO);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PromotorDTO> alterarPromotor(@Valid @RequestBody PromotorDTO promotorDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para alterar promotor codigo {}", promotorDTO.getCodigo());
        PromotorDTO promotorTempDTO = promotorNegocio.alterar(promotorDTO);
        return this.adicionarLinks(promotorTempDTO);
    }

    @DeleteMapping(value = "/{codigo}")
    public ResponseEntity<?> excluirPromotor(@PathVariable("codigo") int codigo)
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
