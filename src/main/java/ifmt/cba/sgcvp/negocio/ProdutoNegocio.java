package ifmt.cba.sgcvp.negocio;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ifmt.cba.sgcvp.dto.ProdutoDTO;
import ifmt.cba.sgcvp.entity.CategoriaProduto;
import ifmt.cba.sgcvp.entity.Produto;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.NotValidDataException;
import ifmt.cba.sgcvp.repository.CategoriaProdutoRepository;
import ifmt.cba.sgcvp.repository.ProdutoRepository;

@Service
// Centraliza as regras de cadastro, consulta e estoque de produtos.
public class ProdutoNegocio {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoNegocio.class);

    private ModelMapper modelMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaProdutoRepository categoriaProdutoRepository;

    public ProdutoNegocio() {
        this.modelMapper = new ModelMapper();
    }

    // Valida e cadastra um novo produto.
    public ProdutoDTO inserir(ProdutoDTO produtoDTO) throws NotValidDataException, NotFoundException {

        Produto produto = this.toEntity(produtoDTO);
        String mensagemErros = produto.validar();

        if (!mensagemErros.isEmpty()) {
            logger.warn("Dados invalidos para incluir produto: {}", mensagemErros);
            throw new NotValidDataException(mensagemErros);
        }

        try {
            if (produtoRepository.findByNomeIgnoreCaseStartingWith(produto.getNome()) != null) {
                logger.warn("Tentativa de incluir produto duplicado: {}", produto.getNome());
                throw new NotValidDataException("Ja existe esse produto");
            }
            produto.setCategoriaProduto(this.pesquisarCategoriaProduto(produto.getCategoriaProduto().getCodigo()));
            produto = produtoRepository.save(produto);
            logger.info("Produto incluido com codigo {}", produto.getCodigo());
        } catch (NotFoundException | NotValidDataException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao incluir produto", ex);
            throw new NotValidDataException("Erro ao incluir o produto - " + ex.getMessage());
        }

        return this.toDTO(produto);
    }

    // Valida e atualiza um produto existente.
    public ProdutoDTO alterar(ProdutoDTO produtoDTO) throws NotValidDataException, NotFoundException {

        Produto produto = this.toEntity(produtoDTO);
        String mensagemErros = produto.validar();
        if (!mensagemErros.isEmpty()) {
            logger.warn("Dados invalidos para alterar produto: {}", mensagemErros);
            throw new NotValidDataException(mensagemErros);
        }
        try {
            if (produtoRepository.findById(produto.getCodigo()).isEmpty()) {
                logger.warn("Tentativa de alterar produto inexistente: {}", produto.getCodigo());
                throw new NotFoundException("Nao existe esse produto");
            }
            produto.setCategoriaProduto(this.pesquisarCategoriaProduto(produto.getCategoriaProduto().getCodigo()));
            produto = produtoRepository.save(produto);
            logger.info("Produto alterado com codigo {}", produto.getCodigo());
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao alterar produto", ex);
            throw new NotValidDataException("Erro ao alterar o produto - " + ex.getMessage());
        }
        return this.toDTO(produto);
    }

    // Exclui um produto pelo codigo informado.
    public void excluir(int codigo) throws NotValidDataException, NotFoundException {
        try {
            Produto produto = produtoRepository.findById(codigo)
                    .orElseThrow(() -> new NotFoundException("Nao existe esse produto"));
            produtoRepository.delete(produto);
            logger.info("Produto excluido com codigo {}", codigo);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao excluir produto", ex);
            throw new NotValidDataException("Erro ao excluir o produto - " + ex.getMessage());
        }
    }

    // Retorna todos os produtos cadastrados.
    public List<ProdutoDTO> pesquisaTodos() throws NotFoundException {
        try {
            logger.info("Pesquisando todos os produtos");
            return this.toDTOAll(produtoRepository.findAll());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar produtos", ex);
            throw new NotFoundException("Erro ao pesquisar produtos - " + ex.getMessage());
        }
    }

    // Busca um produto pelo inicio do nome.
    public ProdutoDTO pesquisaPorNome(String parteNome) throws NotFoundException {
        try {
            logger.info("Pesquisando produto pelo nome {}", parteNome);
            return this.toDTO(produtoRepository.findByNomeIgnoreCaseStartingWith(parteNome));
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar produto pelo nome", ex);
            throw new NotFoundException("Erro ao pesquisar produto pelo nome - " + ex.getMessage());
        }
    }

    // Busca um produto pelo codigo informado.
    public ProdutoDTO pesquisaCodigo(int codigo) throws NotFoundException {
        try {
            logger.info("Pesquisando produto pelo codigo {}", codigo);
            return this.toDTO(produtoRepository.findById(codigo).get());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar produto pelo codigo", ex);
            throw new NotFoundException("Erro ao pesquisar produto pelo codigo - " + ex.getMessage());
        }
    }

    // Lista produtos de uma categoria ordenados por nome.
    public List<ProdutoDTO> pesquisaPorCategoriaOrdenadoPorNome(int codigoCategoria) throws NotFoundException {
        try {
            CategoriaProduto categoriaProduto = this.pesquisarCategoriaProduto(codigoCategoria);
            logger.info("Pesquisando produtos da categoria {} ordenados por nome", codigoCategoria);
            return this.toDTOAll(produtoRepository.findByCategoriaProdutoOrderByNomeAsc(categoriaProduto));
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar produtos por categoria ordenados por nome", ex);
            throw new NotFoundException("Erro ao pesquisar produtos por categoria ordenados por nome - " + ex.getMessage());
        }
    }

    // Lista produtos de uma categoria por estoque decrescente.
    public List<ProdutoDTO> pesquisaPorCategoriaOrdenadoPorEstoqueDecrescente(int codigoCategoria) throws NotFoundException {
        try {
            CategoriaProduto categoriaProduto = this.pesquisarCategoriaProduto(codigoCategoria);
            logger.info("Pesquisando produtos da categoria {} ordenados por estoque decrescente", codigoCategoria);
            return this.toDTOAll(produtoRepository.findByCategoriaProdutoOrderByQuantidadeEstoqueDesc(categoriaProduto));
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar produtos por categoria ordenados por estoque decrescente", ex);
            throw new NotFoundException(
                    "Erro ao pesquisar produtos por categoria ordenados por estoque decrescente - " + ex.getMessage());
        }
    }

    // Lista produtos com estoque abaixo do minimo cadastrado.
    public List<ProdutoDTO> pesquisaProdutosAbaixoEstoqueMinimo() throws NotFoundException {
        try {
            logger.info("Pesquisando produtos abaixo do estoque minimo");
            return this.toDTOAll(produtoRepository.findByQuantidadeEstoqueAbaixoEstoqueMinimo());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar produtos abaixo do estoque minimo", ex);
            throw new NotFoundException("Erro ao pesquisar produtos abaixo do estoque minimo - " + ex.getMessage());
        }
    }

    private CategoriaProduto pesquisarCategoriaProduto(int codigo) throws NotFoundException {
        return categoriaProdutoRepository.findById(codigo)
                .orElseThrow(() -> new NotFoundException("Nao existe essa categoria de produto"));
    }

    public List<ProdutoDTO> toDTOAll(List<Produto> listaProduto) {
        List<ProdutoDTO> listaDTO = new ArrayList<ProdutoDTO>();

        for (Produto produto : listaProduto) {
            listaDTO.add(this.toDTO(produto));
        }
        return listaDTO;
    }

    public ProdutoDTO toDTO(Produto produto) {
        return this.modelMapper.map(produto, ProdutoDTO.class);
    }

    public Produto toEntity(ProdutoDTO produtoDTO) {
        return this.modelMapper.map(produtoDTO, Produto.class);
    }
}
