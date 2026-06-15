package ifmt.cba.sgcvp.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import ifmt.cba.sgcvp.dto.CategoriaProdutoDTO;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.NotValidDataException;
import ifmt.cba.sgcvp.negocio.CategoriaProdutoNegocio;
import io.swagger.v3.oas.annotations.Parameter;

@RestController()
@RequestMapping("/categoria-produto")
public class CategoriaProdutoController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaProdutoController.class);

    @Autowired
    private CategoriaProdutoNegocio categoriaProdutoNegocio;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CategoriaProdutoDTO> buscarTodos() throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar todas as categorias de produto");
        List<CategoriaProdutoDTO> listaCategoriaProdutoTempDTO = categoriaProdutoNegocio.pesquisaTodos();
        return listaCategoriaProdutoTempDTO;
    }

    @GetMapping(value = "/ordenadas-nome", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CategoriaProdutoDTO> buscarTodasOrdenadasPorNome() throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar categorias de produto ordenadas por nome");
        List<CategoriaProdutoDTO> listaCategoriaProdutoTempDTO = categoriaProdutoNegocio.pesquisaTodosOrdenadoPorNome();
        return listaCategoriaProdutoTempDTO;
    }

    @GetMapping(value = "/codigo/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CategoriaProdutoDTO buscarPorID(
            @Parameter(description = "Codigo da categoria de produto", required = true)
            @PathVariable("codigo") int codigo)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar categoria de produto por codigo {}", codigo);
        CategoriaProdutoDTO categoriaProdutoTempDTO = categoriaProdutoNegocio.pesquisaCodigo(codigo);
        return categoriaProdutoTempDTO;
    }

    @GetMapping(value = "/nome/{nome}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CategoriaProdutoDTO buscarPorNome(
            @Parameter(description = "Nome ou parte inicial do nome da categoria de produto", required = true)
            @PathVariable("nome") String nome)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar categoria de produto por nome {}", nome);
        CategoriaProdutoDTO categoriaProdutoTempDTO = categoriaProdutoNegocio.pesquisaPorNome(nome);
        return categoriaProdutoTempDTO;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CategoriaProdutoDTO inserirCategoriaProduto(@RequestBody CategoriaProdutoDTO categoriaProdutoDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para inserir categoria de produto");
        CategoriaProdutoDTO categoriaProdutoTempDTO = categoriaProdutoNegocio.inserir(categoriaProdutoDTO);
        return categoriaProdutoTempDTO;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CategoriaProdutoDTO alterarCategoriaProduto(@RequestBody CategoriaProdutoDTO categoriaProdutoDTO)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para alterar categoria de produto codigo {}", categoriaProdutoDTO.getCodigo());
        CategoriaProdutoDTO categoriaProdutoTempDTO = categoriaProdutoNegocio.alterar(categoriaProdutoDTO);
        return categoriaProdutoTempDTO;
    }

    @DeleteMapping(value = "/{codigo}")
    public ResponseEntity<?> excluirCategoriaProduto(
            @Parameter(description = "Codigo da categoria de produto", required = true)
            @PathVariable("codigo") int codigo)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para excluir categoria de produto codigo {}", codigo);
        categoriaProdutoNegocio.excluir(codigo);
        return ResponseEntity.noContent().build();
    }
}
