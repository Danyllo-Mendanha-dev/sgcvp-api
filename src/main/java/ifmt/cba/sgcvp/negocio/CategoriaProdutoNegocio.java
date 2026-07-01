package ifmt.cba.sgcvp.negocio;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ifmt.cba.sgcvp.dto.CategoriaProdutoDTO;
import ifmt.cba.sgcvp.entity.CategoriaProduto;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.NotValidDataException;
import ifmt.cba.sgcvp.repository.CategoriaProdutoRepository;
import ifmt.cba.sgcvp.repository.ProdutoRepository;

@Service
// Centraliza as regras de cadastro e consulta de categorias de produto.
public class CategoriaProdutoNegocio {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaProdutoNegocio.class);

    private ModelMapper modelMapper;

    @Autowired
    private CategoriaProdutoRepository categoriaProdutoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public CategoriaProdutoNegocio() {
        this.modelMapper = new ModelMapper();
    }

    // Valida e cadastra uma nova categoria de produto.
    public CategoriaProdutoDTO inserir(CategoriaProdutoDTO categoriaProdutoDTO) throws NotValidDataException {

        CategoriaProduto categoriaProduto = this.toEntity(categoriaProdutoDTO);
        String mensagemErros = categoriaProduto.validar();

        if (!mensagemErros.isEmpty()) {
            logger.warn("Dados invalidos para incluir categoria de produto: {}", mensagemErros);
            throw new NotValidDataException(mensagemErros);
        }

        try {
            if (categoriaProdutoRepository.findByNomeIgnoreCaseStartingWith(categoriaProduto.getNome()) != null) {
                logger.warn("Tentativa de incluir categoria de produto duplicada: {}", categoriaProduto.getNome());
                throw new NotValidDataException("Ja existe essa categoria de produto");
            }
            categoriaProduto = categoriaProdutoRepository.save(categoriaProduto);
            logger.info("Categoria de produto incluida com codigo {}", categoriaProduto.getCodigo());
        } catch (Exception ex) {
            logger.error("Erro ao incluir categoria de produto", ex);
            throw new NotValidDataException("Erro ao incluir a categoria de produto - " + ex.getMessage());
        }
        return this.toDTO(categoriaProduto);
    }

    // Valida e atualiza uma categoria de produto existente.
    public CategoriaProdutoDTO alterar(CategoriaProdutoDTO categoriaProdutoDTO)
            throws NotValidDataException, NotFoundException {

        CategoriaProduto categoriaProduto = this.toEntity(categoriaProdutoDTO);
        String mensagemErros = categoriaProduto.validar();
        if (!mensagemErros.isEmpty()) {
            logger.warn("Dados invalidos para alterar categoria de produto: {}", mensagemErros);
            throw new NotValidDataException(mensagemErros);
        }
        try {
            if (categoriaProdutoRepository.findById(categoriaProduto.getCodigo()).isEmpty()) {
                logger.warn("Tentativa de alterar categoria de produto inexistente: {}", categoriaProduto.getCodigo());
                throw new NotFoundException("Nao existe essa categoria de produto");
            }
            categoriaProduto = categoriaProdutoRepository.save(categoriaProduto);
            logger.info("Categoria de produto alterada com codigo {}", categoriaProduto.getCodigo());
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao alterar categoria de produto", ex);
            throw new NotValidDataException("Erro ao alterar a categoria de produto - " + ex.getMessage());
        }
        return this.toDTO(categoriaProduto);
    }

    // Exclui uma categoria se ela nao estiver vinculada a produtos.
    public void excluir(int codigo) throws NotValidDataException, NotFoundException {

        try {
            CategoriaProduto categoriaProduto = categoriaProdutoRepository.findById(codigo)
                    .orElseThrow(() -> new NotFoundException("Essa categoria de produto nao existe"));

            if (produtoRepository.findByCategoriaProduto(categoriaProduto).size() > 0) {
                logger.warn("Tentativa de excluir categoria de produto relacionada a produtos: {}", codigo);
                throw new NotValidDataException("Categoria de produto esta relacionada a produtos");
            }
            categoriaProdutoRepository.delete(categoriaProduto);
            logger.info("Categoria de produto excluida com codigo {}", codigo);
        } catch (NotFoundException | NotValidDataException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao excluir categoria de produto", ex);
            throw new NotValidDataException("Erro ao excluir a categoria de produto - " + ex.getMessage());
        }
    }

    // Retorna todas as categorias cadastradas.
    public List<CategoriaProdutoDTO> pesquisaTodos() throws NotFoundException {
        try {
            logger.info("Pesquisando todas as categorias de produto");
            return this.toDTOAll(categoriaProdutoRepository.findAll());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar categorias de produto", ex);
            throw new NotFoundException("Erro ao pesquisar categorias de produto - " + ex.getMessage());
        }
    }

    // Retorna categorias ordenadas alfabeticamente por nome.
    public List<CategoriaProdutoDTO> pesquisaTodosOrdenadoPorNome() throws NotFoundException {
        try {
            logger.info("Pesquisando todas as categorias de produto ordenadas por nome");
            return this.toDTOAll(categoriaProdutoRepository.findAllByOrderByNomeAsc());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar categorias de produto ordenadas por nome", ex);
            throw new NotFoundException("Erro ao pesquisar categorias de produto ordenadas por nome - " + ex.getMessage());
        }
    }

    // Busca uma categoria pelo inicio do nome.
    public CategoriaProdutoDTO pesquisaPorNome(String parteNome) throws NotFoundException {
        try {
            logger.info("Pesquisando categoria de produto pelo nome {}", parteNome);
            return this.toDTO(categoriaProdutoRepository.findByNomeIgnoreCaseStartingWith(parteNome));
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar categoria de produto pelo nome", ex);
            throw new NotFoundException("Erro ao pesquisar categoria de produto pelo nome - " + ex.getMessage());
        }
    }

    // Busca uma categoria pelo codigo informado.
    public CategoriaProdutoDTO pesquisaCodigo(int codigo) throws NotFoundException {
        try {
            logger.info("Pesquisando categoria de produto pelo codigo {}", codigo);
            return this.toDTO(categoriaProdutoRepository.findById(codigo).get());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar categoria de produto pelo codigo", ex);
            throw new NotFoundException("Erro ao pesquisar categoria de produto pelo codigo - " + ex.getMessage());
        }
    }

    public List<CategoriaProdutoDTO> toDTOAll(List<CategoriaProduto> listaCategoriaProduto) {
        List<CategoriaProdutoDTO> listaDTO = new ArrayList<CategoriaProdutoDTO>();

        for (CategoriaProduto categoriaProduto : listaCategoriaProduto) {
            listaDTO.add(this.toDTO(categoriaProduto));
        }
        return listaDTO;
    }

    public CategoriaProdutoDTO toDTO(CategoriaProduto categoriaProduto) {
        return this.modelMapper.map(categoriaProduto, CategoriaProdutoDTO.class);
    }

    public CategoriaProduto toEntity(CategoriaProdutoDTO categoriaProdutoDTO) {
        return this.modelMapper.map(categoriaProdutoDTO, CategoriaProduto.class);
    }
}
