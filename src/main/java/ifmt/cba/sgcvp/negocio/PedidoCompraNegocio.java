package ifmt.cba.sgcvp.negocio;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ifmt.cba.sgcvp.dto.ItemPedidoCompraDTO;
import ifmt.cba.sgcvp.dto.PedidoCompraDTO;
import ifmt.cba.sgcvp.entity.Fornecedor;
import ifmt.cba.sgcvp.entity.ItemPedidoCompra;
import ifmt.cba.sgcvp.entity.PedidoCompra;
import ifmt.cba.sgcvp.entity.Produto;
import ifmt.cba.sgcvp.exception.EstoqueInsuficienteException;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.NotValidDataException;
import ifmt.cba.sgcvp.exception.TransicaoEstadoInvalidaException;
import ifmt.cba.sgcvp.repository.FornecedorRepository;
import ifmt.cba.sgcvp.repository.PedidoCompraRepository;
import ifmt.cba.sgcvp.repository.ProdutoRepository;

@Service
public class PedidoCompraNegocio {

    private static final Logger logger = LoggerFactory.getLogger(PedidoCompraNegocio.class);
    private static final String DIGITADA = "DIGITADA";
    private static final String CONFERIDA = "CONFERIDA";
    private static final String PROCESSADA = "PROCESSADA";

    private ModelMapper modelMapper;

    @Autowired
    private PedidoCompraRepository pedidoCompraRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public PedidoCompraNegocio() {
        this.modelMapper = new ModelMapper();
    }

    public PedidoCompraDTO inserir(PedidoCompraDTO pedidoCompraDTO) throws NotValidDataException, NotFoundException {

        PedidoCompra pedidoCompra = this.toEntity(pedidoCompraDTO);
        String mensagemErros = this.validarPedido(pedidoCompra);

        if (!mensagemErros.isEmpty()) {
            logger.warn("Dados invalidos para incluir pedido de compra: {}", mensagemErros);
            throw new NotValidDataException(mensagemErros);
        }

        try {
            if (pedidoCompraRepository.findByNumNotaFiscal(pedidoCompra.getNumNotaFiscal()) != null) {
                logger.warn("Tentativa de incluir pedido de compra com nota fiscal duplicada: {}", pedidoCompra.getNumNotaFiscal());
                throw new NotValidDataException("Ja existe esse pedido de compra");
            }
            this.prepararRelacionamentos(pedidoCompra);
            pedidoCompra = pedidoCompraRepository.save(pedidoCompra);
            logger.info("Pedido de compra incluido com codigo {}", pedidoCompra.getCodigo());
        } catch (NotFoundException | NotValidDataException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao incluir pedido de compra", ex);
            throw new NotValidDataException("Erro ao incluir o pedido de compra - " + ex.getMessage());
        }

        return this.toDTO(pedidoCompra);
    }

    public PedidoCompraDTO alterar(PedidoCompraDTO pedidoCompraDTO) throws NotValidDataException, NotFoundException {

        PedidoCompra pedidoCompra = this.toEntity(pedidoCompraDTO);
        String mensagemErros = this.validarPedido(pedidoCompra);
        if (!mensagemErros.isEmpty()) {
            logger.warn("Dados invalidos para alterar pedido de compra: {}", mensagemErros);
            throw new NotValidDataException(mensagemErros);
        }
        try {
            if (pedidoCompraRepository.findById(pedidoCompra.getCodigo()).isEmpty()) {
                logger.warn("Tentativa de alterar pedido de compra inexistente: {}", pedidoCompra.getCodigo());
                throw new NotFoundException("Nao existe esse pedido de compra");
            }
            this.prepararRelacionamentos(pedidoCompra);
            pedidoCompra = pedidoCompraRepository.save(pedidoCompra);
            logger.info("Pedido de compra alterado com codigo {}", pedidoCompra.getCodigo());
        } catch (NotFoundException | NotValidDataException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao alterar pedido de compra", ex);
            throw new NotValidDataException("Erro ao alterar o pedido de compra - " + ex.getMessage());
        }
        return this.toDTO(pedidoCompra);
    }

    public void excluir(int codigo) throws NotValidDataException, NotFoundException {
        try {
            PedidoCompra pedidoCompra = pedidoCompraRepository.findById(codigo)
                    .orElseThrow(() -> new NotFoundException("Nao existe esse pedido de compra"));
            pedidoCompraRepository.delete(pedidoCompra);
            logger.info("Pedido de compra excluido com codigo {}", codigo);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao excluir pedido de compra", ex);
            throw new NotValidDataException("Erro ao excluir o pedido de compra - " + ex.getMessage());
        }
    }

    public List<PedidoCompraDTO> pesquisaTodos() throws NotFoundException {
        try {
            logger.info("Pesquisando todos os pedidos de compra");
            return this.toDTOAll(pedidoCompraRepository.findAll());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar pedidos de compra", ex);
            throw new NotFoundException("Erro ao pesquisar pedidos de compra - " + ex.getMessage());
        }
    }

    public PedidoCompraDTO pesquisaPorNotaFiscal(String numNotaFiscal) throws NotFoundException {
        try {
            logger.info("Pesquisando pedido de compra pela nota fiscal {}", numNotaFiscal);
            return this.toDTO(pedidoCompraRepository.findByNumNotaFiscal(numNotaFiscal));
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar pedido de compra pela nota fiscal", ex);
            throw new NotFoundException("Erro ao pesquisar pedido de compra pela nota fiscal - " + ex.getMessage());
        }
    }

    public PedidoCompraDTO pesquisaCodigo(int codigo) throws NotFoundException {
        try {
            logger.info("Pesquisando pedido de compra pelo codigo {}", codigo);
            return this.toDTO(pedidoCompraRepository.findById(codigo).get());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar pedido de compra pelo codigo", ex);
            throw new NotFoundException("Erro ao pesquisar pedido de compra pelo codigo - " + ex.getMessage());
        }
    }

    @Transactional
    public PedidoCompraDTO conferir(int codigo) throws NotFoundException, TransicaoEstadoInvalidaException {
        PedidoCompra pedidoCompra = this.pesquisarPedidoCompra(codigo);
        this.validarTransicao(pedidoCompra, DIGITADA, CONFERIDA);
        pedidoCompra.setStatus(CONFERIDA);
        pedidoCompra = pedidoCompraRepository.save(pedidoCompra);
        logger.info("Pedido de compra {} alterado para {}", codigo, CONFERIDA);
        return this.toDTO(pedidoCompra);
    }

    @Transactional
    public PedidoCompraDTO processar(int codigo)
            throws NotFoundException, TransicaoEstadoInvalidaException, EstoqueInsuficienteException {
        PedidoCompra pedidoCompra = this.pesquisarPedidoCompra(codigo);
        this.validarTransicao(pedidoCompra, CONFERIDA, PROCESSADA);

        for (ItemPedidoCompra itemPedidoCompra : pedidoCompra.getListaItemPedidoCompra()) {
            Produto produto = itemPedidoCompra.getProduto();
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + this.quantidadeInteira(itemPedidoCompra));
            produtoRepository.save(produto);
        }

        pedidoCompra.setStatus(PROCESSADA);
        pedidoCompra = pedidoCompraRepository.save(pedidoCompra);
        logger.info("Pedido de compra {} processado com entrada de estoque", codigo);
        return this.toDTO(pedidoCompra);
    }

    private String validarPedido(PedidoCompra pedidoCompra) {
        String retorno = pedidoCompra.validar();

        if (pedidoCompra.getListaItemPedidoCompra() != null) {
            for (ItemPedidoCompra itemPedidoCompra : pedidoCompra.getListaItemPedidoCompra()) {
                retorno += itemPedidoCompra.validar();
            }
        }
        return retorno;
    }

    private void prepararRelacionamentos(PedidoCompra pedidoCompra) throws NotFoundException {
        Fornecedor fornecedor = fornecedorRepository.findById(pedidoCompra.getFornecedor().getCodigo())
                .orElseThrow(() -> new NotFoundException("Nao existe esse fornecedor"));

        pedidoCompra.setFornecedor(fornecedor);

        for (ItemPedidoCompra itemPedidoCompra : pedidoCompra.getListaItemPedidoCompra()) {
            Produto produto = produtoRepository.findById(itemPedidoCompra.getProduto().getCodigo())
                    .orElseThrow(() -> new NotFoundException("Nao existe esse produto"));
            itemPedidoCompra.setProduto(produto);
            itemPedidoCompra.setPedidoCompra(pedidoCompra);
        }
    }

    private PedidoCompra pesquisarPedidoCompra(int codigo) throws NotFoundException {
        return pedidoCompraRepository.findById(codigo)
                .orElseThrow(() -> new NotFoundException("Nao existe esse pedido de compra"));
    }

    private void validarTransicao(PedidoCompra pedidoCompra, String statusAtual, String statusNovo)
            throws TransicaoEstadoInvalidaException {
        if (pedidoCompra.getStatus() == null || !pedidoCompra.getStatus().equalsIgnoreCase(statusAtual)) {
            logger.warn("Transicao invalida do pedido de compra {}: {} para {}", pedidoCompra.getCodigo(),
                    pedidoCompra.getStatus(), statusNovo);
            throw new TransicaoEstadoInvalidaException(
                    "Transicao invalida de " + pedidoCompra.getStatus() + " para " + statusNovo);
        }
    }

    private int quantidadeInteira(ItemPedidoCompra itemPedidoCompra) throws EstoqueInsuficienteException {
        try {
            return itemPedidoCompra.getQuantidade().intValueExact();
        } catch (ArithmeticException ex) {
            throw new EstoqueInsuficienteException("Quantidade do item deve ser inteira para movimentar estoque");
        }
    }

    public List<PedidoCompraDTO> toDTOAll(List<PedidoCompra> listaPedidoCompra) {
        List<PedidoCompraDTO> listaDTO = new ArrayList<PedidoCompraDTO>();

        for (PedidoCompra pedidoCompra : listaPedidoCompra) {
            listaDTO.add(this.toDTO(pedidoCompra));
        }
        return listaDTO;
    }

    public PedidoCompraDTO toDTO(PedidoCompra pedidoCompra) {
        PedidoCompraDTO pedidoCompraDTO = new PedidoCompraDTO();
        pedidoCompraDTO.setCodigo(pedidoCompra.getCodigo());
        pedidoCompraDTO.setDataEntrada(pedidoCompra.getDataEntrada());
        pedidoCompraDTO.setNumNotaFiscal(pedidoCompra.getNumNotaFiscal());
        pedidoCompraDTO.setStatus(pedidoCompra.getStatus());
        pedidoCompraDTO.setFornecedor(this.modelMapper.map(pedidoCompra.getFornecedor(), ifmt.cba.sgcvp.dto.FornecedorDTO.class));

        List<ItemPedidoCompraDTO> listaItemPedidoCompraDTO = new ArrayList<ItemPedidoCompraDTO>();
        for (ItemPedidoCompra itemPedidoCompra : pedidoCompra.getListaItemPedidoCompra()) {
            ItemPedidoCompraDTO itemPedidoCompraDTO = new ItemPedidoCompraDTO();
            itemPedidoCompraDTO.setCodigo(itemPedidoCompra.getCodigo());
            itemPedidoCompraDTO.setQuantidade(itemPedidoCompra.getQuantidade());
            itemPedidoCompraDTO.setProduto(this.modelMapper.map(itemPedidoCompra.getProduto(), ifmt.cba.sgcvp.dto.ProdutoDTO.class));
            listaItemPedidoCompraDTO.add(itemPedidoCompraDTO);
        }
        pedidoCompraDTO.setListaItemPedidoCompra(listaItemPedidoCompraDTO);
        return pedidoCompraDTO;
    }

    public PedidoCompra toEntity(PedidoCompraDTO pedidoCompraDTO) {
        PedidoCompra pedidoCompra = new PedidoCompra();
        pedidoCompra.setCodigo(pedidoCompraDTO.getCodigo());
        pedidoCompra.setDataEntrada(pedidoCompraDTO.getDataEntrada());
        pedidoCompra.setNumNotaFiscal(pedidoCompraDTO.getNumNotaFiscal());
        pedidoCompra.setStatus(pedidoCompraDTO.getStatus());
        if (pedidoCompraDTO.getFornecedor() != null) {
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setCodigo(pedidoCompraDTO.getFornecedor().getCodigo());
            pedidoCompra.setFornecedor(fornecedor);
        }

        List<ItemPedidoCompra> listaItemPedidoCompra = new ArrayList<ItemPedidoCompra>();
        if (pedidoCompraDTO.getListaItemPedidoCompra() != null) {
            for (ItemPedidoCompraDTO itemPedidoCompraDTO : pedidoCompraDTO.getListaItemPedidoCompra()) {
                ItemPedidoCompra itemPedidoCompra = new ItemPedidoCompra();
                itemPedidoCompra.setCodigo(itemPedidoCompraDTO.getCodigo());
                itemPedidoCompra.setQuantidade(itemPedidoCompraDTO.getQuantidade());
                if (itemPedidoCompraDTO.getProduto() != null) {
                    Produto produto = new Produto();
                    produto.setCodigo(itemPedidoCompraDTO.getProduto().getCodigo());
                    itemPedidoCompra.setProduto(produto);
                }
                itemPedidoCompra.setPedidoCompra(pedidoCompra);
                listaItemPedidoCompra.add(itemPedidoCompra);
            }
        }
        pedidoCompra.setListaItemPedidoCompra(listaItemPedidoCompra);
        return pedidoCompra;
    }
}
