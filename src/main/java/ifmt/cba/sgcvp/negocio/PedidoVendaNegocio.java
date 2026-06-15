package ifmt.cba.sgcvp.negocio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ifmt.cba.sgcvp.dto.ItemPedidoVendaDTO;
import ifmt.cba.sgcvp.dto.PedidoVendaDTO;
import ifmt.cba.sgcvp.entity.Cliente;
import ifmt.cba.sgcvp.entity.ItemPedidoVenda;
import ifmt.cba.sgcvp.entity.LancamentoComissao;
import ifmt.cba.sgcvp.entity.PedidoVenda;
import ifmt.cba.sgcvp.entity.Produto;
import ifmt.cba.sgcvp.entity.Promotor;
import ifmt.cba.sgcvp.exception.EstoqueInsuficienteException;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.NotValidDataException;
import ifmt.cba.sgcvp.exception.TransicaoEstadoInvalidaException;
import ifmt.cba.sgcvp.repository.ClienteRepository;
import ifmt.cba.sgcvp.repository.LancamentoComissaoRepository;
import ifmt.cba.sgcvp.repository.PedidoVendaRepository;
import ifmt.cba.sgcvp.repository.ProdutoRepository;
import ifmt.cba.sgcvp.repository.PromotorRepository;

@Service
public class PedidoVendaNegocio {

    private static final Logger logger = LoggerFactory.getLogger(PedidoVendaNegocio.class);
    private static final String SOLICITADO = "SOLICITADO";
    private static final String APROVADO_ESTOQUE = "APROVADO_ESTOQUE";
    private static final String PENDENTE_ESTOQUE = "PENDENTE_ESTOQUE";
    private static final String APROVADO_VENDA = "APROVADO_VENDA";
    private static final String REPROVADO_VENDA = "REPROVADO_VENDA";
    private static final String PROCESSADO = "PROCESSADO";
    private static final String LANCADA = "LANCADA";

    private ModelMapper modelMapper;

    @Autowired
    private PedidoVendaRepository pedidoVendaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PromotorRepository promotorRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private LancamentoComissaoRepository lancamentoComissaoRepository;

    public PedidoVendaNegocio() {
        this.modelMapper = new ModelMapper();
    }

    public PedidoVendaDTO inserir(PedidoVendaDTO pedidoVendaDTO) throws NotValidDataException, NotFoundException {

        PedidoVenda pedidoVenda = this.toEntity(pedidoVendaDTO);
        String mensagemErros = this.validarPedido(pedidoVenda);

        if (!mensagemErros.isEmpty()) {
            logger.warn("Dados invalidos para incluir pedido de venda: {}", mensagemErros);
            throw new NotValidDataException(mensagemErros);
        }

        try {
            this.prepararRelacionamentos(pedidoVenda);
            pedidoVenda = pedidoVendaRepository.save(pedidoVenda);
            logger.info("Pedido de venda incluido com codigo {}", pedidoVenda.getCodigo());
        } catch (NotFoundException | NotValidDataException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao incluir pedido de venda", ex);
            throw new NotValidDataException("Erro ao incluir o pedido de venda - " + ex.getMessage());
        }

        return this.toDTO(pedidoVenda);
    }

    public PedidoVendaDTO alterar(PedidoVendaDTO pedidoVendaDTO) throws NotValidDataException, NotFoundException {

        PedidoVenda pedidoVenda = this.toEntity(pedidoVendaDTO);
        String mensagemErros = this.validarPedido(pedidoVenda);
        if (!mensagemErros.isEmpty()) {
            logger.warn("Dados invalidos para alterar pedido de venda: {}", mensagemErros);
            throw new NotValidDataException(mensagemErros);
        }
        try {
            if (pedidoVendaRepository.findById(pedidoVenda.getCodigo()).isEmpty()) {
                logger.warn("Tentativa de alterar pedido de venda inexistente: {}", pedidoVenda.getCodigo());
                throw new NotFoundException("Nao existe esse pedido de venda");
            }
            this.prepararRelacionamentos(pedidoVenda);
            pedidoVenda = pedidoVendaRepository.save(pedidoVenda);
            logger.info("Pedido de venda alterado com codigo {}", pedidoVenda.getCodigo());
        } catch (NotFoundException | NotValidDataException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao alterar pedido de venda", ex);
            throw new NotValidDataException("Erro ao alterar o pedido de venda - " + ex.getMessage());
        }
        return this.toDTO(pedidoVenda);
    }

    public void excluir(int codigo) throws NotValidDataException, NotFoundException {
        try {
            PedidoVenda pedidoVenda = pedidoVendaRepository.findById(codigo)
                    .orElseThrow(() -> new NotFoundException("Nao existe esse pedido de venda"));
            pedidoVendaRepository.delete(pedidoVenda);
            logger.info("Pedido de venda excluido com codigo {}", codigo);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao excluir pedido de venda", ex);
            throw new NotValidDataException("Erro ao excluir o pedido de venda - " + ex.getMessage());
        }
    }

    public List<PedidoVendaDTO> pesquisaTodos() throws NotFoundException {
        try {
            logger.info("Pesquisando todos os pedidos de venda");
            return this.toDTOAll(pedidoVendaRepository.findAll());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar pedidos de venda", ex);
            throw new NotFoundException("Erro ao pesquisar pedidos de venda - " + ex.getMessage());
        }
    }

    public List<PedidoVendaDTO> pesquisaPorStatus(String status) throws NotFoundException {
        try {
            logger.info("Pesquisando pedidos de venda pelo status {}", status);
            return this.toDTOAll(pedidoVendaRepository.findByStatusIgnoreCase(status));
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar pedido de venda pelo status", ex);
            throw new NotFoundException("Erro ao pesquisar pedido de venda pelo status - " + ex.getMessage());
        }
    }

    public PedidoVendaDTO pesquisaCodigo(int codigo) throws NotFoundException {
        try {
            logger.info("Pesquisando pedido de venda pelo codigo {}", codigo);
            return this.toDTO(pedidoVendaRepository.findById(codigo).get());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar pedido de venda pelo codigo", ex);
            throw new NotFoundException("Erro ao pesquisar pedido de venda pelo codigo - " + ex.getMessage());
        }
    }

    @Transactional
    public PedidoVendaDTO aprovarEstoque(int codigo)
            throws NotFoundException, TransicaoEstadoInvalidaException, EstoqueInsuficienteException {
        PedidoVenda pedidoVenda = this.pesquisarPedidoVenda(codigo);
        this.validarTransicao(pedidoVenda, SOLICITADO, APROVADO_ESTOQUE);
        this.validarEstoqueDisponivel(pedidoVenda);
        pedidoVenda.setStatus(APROVADO_ESTOQUE);
        pedidoVenda = pedidoVendaRepository.save(pedidoVenda);
        logger.info("Pedido de venda {} alterado para {}", codigo, APROVADO_ESTOQUE);
        return this.toDTO(pedidoVenda);
    }

    @Transactional
    public PedidoVendaDTO pendenteEstoque(int codigo)
            throws NotFoundException, TransicaoEstadoInvalidaException {
        PedidoVenda pedidoVenda = this.pesquisarPedidoVenda(codigo);
        this.validarTransicao(pedidoVenda, SOLICITADO, PENDENTE_ESTOQUE);
        pedidoVenda.setStatus(PENDENTE_ESTOQUE);
        pedidoVenda = pedidoVendaRepository.save(pedidoVenda);
        logger.info("Pedido de venda {} alterado para {}", codigo, PENDENTE_ESTOQUE);
        return this.toDTO(pedidoVenda);
    }

    @Transactional
    public PedidoVendaDTO aprovarVenda(int codigo)
            throws NotFoundException, TransicaoEstadoInvalidaException {
        PedidoVenda pedidoVenda = this.pesquisarPedidoVenda(codigo);
        this.validarTransicao(pedidoVenda, APROVADO_ESTOQUE, APROVADO_VENDA);
        pedidoVenda.setStatus(APROVADO_VENDA);
        pedidoVenda = pedidoVendaRepository.save(pedidoVenda);
        logger.info("Pedido de venda {} alterado para {}", codigo, APROVADO_VENDA);
        return this.toDTO(pedidoVenda);
    }

    @Transactional
    public PedidoVendaDTO reprovarVenda(int codigo)
            throws NotFoundException, TransicaoEstadoInvalidaException {
        PedidoVenda pedidoVenda = this.pesquisarPedidoVenda(codigo);
        this.validarTransicao(pedidoVenda, APROVADO_ESTOQUE, REPROVADO_VENDA);
        pedidoVenda.setStatus(REPROVADO_VENDA);
        pedidoVenda = pedidoVendaRepository.save(pedidoVenda);
        logger.info("Pedido de venda {} alterado para {}", codigo, REPROVADO_VENDA);
        return this.toDTO(pedidoVenda);
    }

    @Transactional
    public PedidoVendaDTO processar(int codigo, BigDecimal percentualComissao)
            throws NotFoundException, TransicaoEstadoInvalidaException, EstoqueInsuficienteException,
            NotValidDataException {
        PedidoVenda pedidoVenda = this.pesquisarPedidoVenda(codigo);
        this.validarTransicao(pedidoVenda, APROVADO_VENDA, PROCESSADO);
        this.validarEstoqueDisponivel(pedidoVenda);

        for (ItemPedidoVenda itemPedidoVenda : pedidoVenda.getListaItemPedidoVenda()) {
            Produto produto = itemPedidoVenda.getProduto();
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - this.quantidadeInteira(itemPedidoVenda));
            produtoRepository.save(produto);
        }

        if (lancamentoComissaoRepository.findByPedidoVenda(pedidoVenda) != null) {
            logger.warn("Tentativa de processar pedido de venda {} com comissao ja lancada", codigo);
            throw new NotValidDataException("Ja existe lancamento de comissao para esse pedido de venda");
        }

        LancamentoComissao lancamentoComissao = new LancamentoComissao();
        lancamentoComissao.setDataLancamento(java.time.LocalDate.now());
        lancamentoComissao.setPedidoVenda(pedidoVenda);
        lancamentoComissao.setPromotor(pedidoVenda.getPromotor());
        lancamentoComissao.setStatus(LANCADA);
        lancamentoComissao.setValor(this.calcularValorComissao(pedidoVenda, percentualComissao));
        lancamentoComissaoRepository.save(lancamentoComissao);

        pedidoVenda.setStatus(PROCESSADO);
        pedidoVenda = pedidoVendaRepository.save(pedidoVenda);
        logger.info("Pedido de venda {} processado com baixa de estoque e lancamento de comissao", codigo);
        return this.toDTO(pedidoVenda);
    }

    private String validarPedido(PedidoVenda pedidoVenda) {
        String retorno = pedidoVenda.validar();

        if (pedidoVenda.getListaItemPedidoVenda() != null) {
            for (ItemPedidoVenda itemPedidoVenda : pedidoVenda.getListaItemPedidoVenda()) {
                retorno += itemPedidoVenda.validar();
            }
        }
        return retorno;
    }

    private void prepararRelacionamentos(PedidoVenda pedidoVenda) throws NotFoundException {
        Cliente cliente = clienteRepository.findById(pedidoVenda.getCliente().getCodigo())
                .orElseThrow(() -> new NotFoundException("Nao existe esse cliente"));
        Promotor promotor = promotorRepository.findById(pedidoVenda.getPromotor().getCodigo())
                .orElseThrow(() -> new NotFoundException("Nao existe esse promotor"));

        pedidoVenda.setCliente(cliente);
        pedidoVenda.setPromotor(promotor);

        for (ItemPedidoVenda itemPedidoVenda : pedidoVenda.getListaItemPedidoVenda()) {
            Produto produto = produtoRepository.findById(itemPedidoVenda.getProduto().getCodigo())
                    .orElseThrow(() -> new NotFoundException("Nao existe esse produto"));
            itemPedidoVenda.setProduto(produto);
            itemPedidoVenda.setPedidoVenda(pedidoVenda);
        }
    }

    private PedidoVenda pesquisarPedidoVenda(int codigo) throws NotFoundException {
        return pedidoVendaRepository.findById(codigo)
                .orElseThrow(() -> new NotFoundException("Nao existe esse pedido de venda"));
    }

    private void validarTransicao(PedidoVenda pedidoVenda, String statusAtual, String statusNovo)
            throws TransicaoEstadoInvalidaException {
        if (pedidoVenda.getStatus() == null || !pedidoVenda.getStatus().equalsIgnoreCase(statusAtual)) {
            logger.warn("Transicao invalida do pedido de venda {}: {} para {}", pedidoVenda.getCodigo(),
                    pedidoVenda.getStatus(), statusNovo);
            throw new TransicaoEstadoInvalidaException(
                    "Transicao invalida de " + pedidoVenda.getStatus() + " para " + statusNovo);
        }
    }

    private void validarEstoqueDisponivel(PedidoVenda pedidoVenda) throws EstoqueInsuficienteException {
        for (ItemPedidoVenda itemPedidoVenda : pedidoVenda.getListaItemPedidoVenda()) {
            Produto produto = itemPedidoVenda.getProduto();
            int quantidade = this.quantidadeInteira(itemPedidoVenda);
            if (produto.getQuantidadeEstoque() < quantidade) {
                logger.warn("Estoque insuficiente do produto {} para o pedido de venda {}", produto.getCodigo(),
                        pedidoVenda.getCodigo());
                throw new EstoqueInsuficienteException("Estoque insuficiente para o produto " + produto.getNome());
            }
        }
    }

    private int quantidadeInteira(ItemPedidoVenda itemPedidoVenda) throws EstoqueInsuficienteException {
        try {
            return itemPedidoVenda.getQuantidade().intValueExact();
        } catch (ArithmeticException ex) {
            throw new EstoqueInsuficienteException("Quantidade do item deve ser inteira para movimentar estoque");
        }
    }

    private BigDecimal calcularValorComissao(PedidoVenda pedidoVenda, BigDecimal percentualComissao) {
        BigDecimal totalPedido = BigDecimal.ZERO;
        for (ItemPedidoVenda itemPedidoVenda : pedidoVenda.getListaItemPedidoVenda()) {
            totalPedido = totalPedido.add(itemPedidoVenda.getQuantidade().multiply(itemPedidoVenda.getValorUnitario()));
        }
        return totalPedido.multiply(percentualComissao)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public List<PedidoVendaDTO> toDTOAll(List<PedidoVenda> listaPedidoVenda) {
        List<PedidoVendaDTO> listaDTO = new ArrayList<PedidoVendaDTO>();

        for (PedidoVenda pedidoVenda : listaPedidoVenda) {
            listaDTO.add(this.toDTO(pedidoVenda));
        }
        return listaDTO;
    }

    public PedidoVendaDTO toDTO(PedidoVenda pedidoVenda) {
        PedidoVendaDTO pedidoVendaDTO = new PedidoVendaDTO();
        pedidoVendaDTO.setCodigo(pedidoVenda.getCodigo());
        pedidoVendaDTO.setDataPedido(pedidoVenda.getDataPedido());
        pedidoVendaDTO.setStatus(pedidoVenda.getStatus());
        pedidoVendaDTO.setCliente(this.modelMapper.map(pedidoVenda.getCliente(), ifmt.cba.sgcvp.dto.ClienteDTO.class));
        pedidoVendaDTO.setPromotor(this.modelMapper.map(pedidoVenda.getPromotor(), ifmt.cba.sgcvp.dto.PromotorDTO.class));

        List<ItemPedidoVendaDTO> listaItemPedidoVendaDTO = new ArrayList<ItemPedidoVendaDTO>();
        for (ItemPedidoVenda itemPedidoVenda : pedidoVenda.getListaItemPedidoVenda()) {
            ItemPedidoVendaDTO itemPedidoVendaDTO = new ItemPedidoVendaDTO();
            itemPedidoVendaDTO.setCodigo(itemPedidoVenda.getCodigo());
            itemPedidoVendaDTO.setQuantidade(itemPedidoVenda.getQuantidade());
            itemPedidoVendaDTO.setValorUnitario(itemPedidoVenda.getValorUnitario());
            itemPedidoVendaDTO.setProduto(this.modelMapper.map(itemPedidoVenda.getProduto(), ifmt.cba.sgcvp.dto.ProdutoDTO.class));
            listaItemPedidoVendaDTO.add(itemPedidoVendaDTO);
        }
        pedidoVendaDTO.setListaItemPedidoVenda(listaItemPedidoVendaDTO);
        return pedidoVendaDTO;
    }

    public PedidoVenda toEntity(PedidoVendaDTO pedidoVendaDTO) {
        PedidoVenda pedidoVenda = new PedidoVenda();
        pedidoVenda.setCodigo(pedidoVendaDTO.getCodigo());
        pedidoVenda.setDataPedido(pedidoVendaDTO.getDataPedido());
        pedidoVenda.setStatus(pedidoVendaDTO.getStatus());
        if (pedidoVendaDTO.getCliente() != null) {
            Cliente cliente = new Cliente();
            cliente.setCodigo(pedidoVendaDTO.getCliente().getCodigo());
            pedidoVenda.setCliente(cliente);
        }
        if (pedidoVendaDTO.getPromotor() != null) {
            Promotor promotor = new Promotor();
            promotor.setCodigo(pedidoVendaDTO.getPromotor().getCodigo());
            pedidoVenda.setPromotor(promotor);
        }

        List<ItemPedidoVenda> listaItemPedidoVenda = new ArrayList<ItemPedidoVenda>();
        if (pedidoVendaDTO.getListaItemPedidoVenda() != null) {
            for (ItemPedidoVendaDTO itemPedidoVendaDTO : pedidoVendaDTO.getListaItemPedidoVenda()) {
                ItemPedidoVenda itemPedidoVenda = new ItemPedidoVenda();
                itemPedidoVenda.setCodigo(itemPedidoVendaDTO.getCodigo());
                itemPedidoVenda.setQuantidade(itemPedidoVendaDTO.getQuantidade());
                itemPedidoVenda.setValorUnitario(itemPedidoVendaDTO.getValorUnitario());
                if (itemPedidoVendaDTO.getProduto() != null) {
                    Produto produto = new Produto();
                    produto.setCodigo(itemPedidoVendaDTO.getProduto().getCodigo());
                    itemPedidoVenda.setProduto(produto);
                }
                itemPedidoVenda.setPedidoVenda(pedidoVenda);
                listaItemPedidoVenda.add(itemPedidoVenda);
            }
        }
        pedidoVenda.setListaItemPedidoVenda(listaItemPedidoVenda);
        return pedidoVenda;
    }
}
