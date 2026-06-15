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

import ifmt.cba.sgcvp.dto.ProdutoDTO;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.NotValidDataException;
import ifmt.cba.sgcvp.negocio.ProdutoNegocio;
import io.swagger.v3.oas.annotations.Parameter;

@RestController()
@RequestMapping("/produto")
public class ProdutoController {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    @Autowired
    private ProdutoNegocio produtoNegocio;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProdutoDTO> buscarTodos() throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar todos os produtos");
        List<ProdutoDTO> listaProdutoTempDTO = produtoNegocio.pesquisaTodos();
        return listaProdutoTempDTO;
    }

    @GetMapping(value = "/codigo/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProdutoDTO buscarPorID(@PathVariable("codigo") int codigo) throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar produto por codigo {}", codigo);
        ProdutoDTO produtoTempDTO = produtoNegocio.pesquisaCodigo(codigo);
        return produtoTempDTO;
    }

    @GetMapping(value = "/nome/{nome}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProdutoDTO buscarPorNome(@PathVariable("nome") String nome) throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar produto por nome {}", nome);
        ProdutoDTO produtoTempDTO = produtoNegocio.pesquisaPorNome(nome);
        return produtoTempDTO;
    }

    @GetMapping(value = "/categoria/{codigoCategoria}/ordenados-nome", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProdutoDTO> buscarPorCategoriaOrdenadoPorNome(
            @Parameter(description = "Codigo da categoria de produto", required = true)
            @PathVariable("codigoCategoria") int codigoCategoria)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar produtos da categoria {} ordenados por nome", codigoCategoria);
        List<ProdutoDTO> listaProdutoTempDTO = produtoNegocio.pesquisaPorCategoriaOrdenadoPorNome(codigoCategoria);
        return listaProdutoTempDTO;
    }

    @GetMapping(value = "/categoria/{codigoCategoria}/estoque-decrescente", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProdutoDTO> buscarPorCategoriaOrdenadoPorEstoqueDecrescente(
            @Parameter(description = "Codigo da categoria de produto", required = true)
            @PathVariable("codigoCategoria") int codigoCategoria)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar produtos da categoria {} por estoque decrescente", codigoCategoria);
        List<ProdutoDTO> listaProdutoTempDTO = produtoNegocio
                .pesquisaPorCategoriaOrdenadoPorEstoqueDecrescente(codigoCategoria);
        return listaProdutoTempDTO;
    }

    @GetMapping(value = "/estoque-abaixo-minimo", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProdutoDTO> buscarProdutosAbaixoEstoqueMinimo() throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar produtos abaixo do estoque minimo");
        List<ProdutoDTO> listaProdutoTempDTO = produtoNegocio.pesquisaProdutosAbaixoEstoqueMinimo();
        return listaProdutoTempDTO;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProdutoDTO inserirProduto(@RequestBody ProdutoDTO produtoDTO) throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para inserir produto");
        ProdutoDTO produtoTempDTO = produtoNegocio.inserir(produtoDTO);
        return produtoTempDTO;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProdutoDTO alterarProduto(@RequestBody ProdutoDTO produtoDTO) throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para alterar produto codigo {}", produtoDTO.getCodigo());
        ProdutoDTO produtoTempDTO = produtoNegocio.alterar(produtoDTO);
        return produtoTempDTO;
    }

    @DeleteMapping(value = "/{codigo}")
    public ResponseEntity<?> excluirProduto(@PathVariable("codigo") int codigo) throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para excluir produto codigo {}", codigo);
        produtoNegocio.excluir(codigo);
        return ResponseEntity.noContent().build();
    }
}
