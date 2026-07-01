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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController()
@RequestMapping("/produto")
@Tag(name = "Produtos", description = "Operacoes relacionadas ao cadastro, consulta e estoque de produtos.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operacao realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos enviados na requisicao"),
        @ApiResponse(responseCode = "404", description = "Recurso nao encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
})
public class ProdutoController {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    @Autowired
    private ProdutoNegocio produtoNegocio;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar produtos", description = "Retorna todos os produtos cadastrados.")
    public List<ProdutoDTO> buscarTodos() throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar todos os produtos");
        List<ProdutoDTO> listaProdutoTempDTO = produtoNegocio.pesquisaTodos();
        return listaProdutoTempDTO;
    }

    @GetMapping(value = "/codigo/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar produto por codigo", description = "Retorna um produto a partir do seu codigo identificador.")
    public ProdutoDTO buscarPorID(
            @Parameter(description = "Codigo do produto", example = "1", required = true)
            @PathVariable("codigo") int codigo) throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar produto por codigo {}", codigo);
        ProdutoDTO produtoTempDTO = produtoNegocio.pesquisaCodigo(codigo);
        return produtoTempDTO;
    }

    @GetMapping(value = "/nome/{nome}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar produto por nome", description = "Retorna um produto pelo nome informado.")
    public ProdutoDTO buscarPorNome(
            @Parameter(description = "Nome ou parte inicial do nome do produto", example = "Cafe", required = true)
            @PathVariable("nome") String nome) throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar produto por nome {}", nome);
        ProdutoDTO produtoTempDTO = produtoNegocio.pesquisaPorNome(nome);
        return produtoTempDTO;
    }

    @GetMapping(value = "/categoria/{codigoCategoria}/ordenados-nome", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar produtos por categoria e nome", description = "Retorna produtos de uma categoria ordenados pelo nome.")
    public List<ProdutoDTO> buscarPorCategoriaOrdenadoPorNome(
            @Parameter(description = "Codigo da categoria de produto", required = true)
            @PathVariable("codigoCategoria") int codigoCategoria)
            throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar produtos da categoria {} ordenados por nome", codigoCategoria);
        List<ProdutoDTO> listaProdutoTempDTO = produtoNegocio.pesquisaPorCategoriaOrdenadoPorNome(codigoCategoria);
        return listaProdutoTempDTO;
    }

    @GetMapping(value = "/categoria/{codigoCategoria}/estoque-decrescente", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar produtos por categoria e estoque", description = "Retorna produtos de uma categoria ordenados por estoque decrescente.")
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
    @Operation(summary = "Listar produtos abaixo do estoque minimo", description = "Retorna os produtos cuja quantidade em estoque esta abaixo do minimo cadastrado.")
    public List<ProdutoDTO> buscarProdutosAbaixoEstoqueMinimo() throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para buscar produtos abaixo do estoque minimo");
        List<ProdutoDTO> listaProdutoTempDTO = produtoNegocio.pesquisaProdutosAbaixoEstoqueMinimo();
        return listaProdutoTempDTO;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cadastrar produto", description = "Cadastra um novo produto.")
    public ProdutoDTO inserirProduto(@RequestBody ProdutoDTO produtoDTO) throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para inserir produto");
        ProdutoDTO produtoTempDTO = produtoNegocio.inserir(produtoDTO);
        return produtoTempDTO;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Alterar produto", description = "Atualiza os dados de um produto existente.")
    public ProdutoDTO alterarProduto(@RequestBody ProdutoDTO produtoDTO) throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para alterar produto codigo {}", produtoDTO.getCodigo());
        ProdutoDTO produtoTempDTO = produtoNegocio.alterar(produtoDTO);
        return produtoTempDTO;
    }

    @DeleteMapping(value = "/{codigo}")
    @Operation(summary = "Excluir produto", description = "Remove um produto pelo codigo informado.")
    public ResponseEntity<?> excluirProduto(
            @Parameter(description = "Codigo do produto", example = "1", required = true)
            @PathVariable("codigo") int codigo) throws NotFoundException, NotValidDataException {
        logger.info("Requisicao para excluir produto codigo {}", codigo);
        produtoNegocio.excluir(codigo);
        return ResponseEntity.noContent().build();
    }
}
