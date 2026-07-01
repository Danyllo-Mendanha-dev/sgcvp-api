package ifmt.cba.sgcvp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ifmt.cba.sgcvp.entity.CategoriaProduto;
import ifmt.cba.sgcvp.entity.Cliente;
import ifmt.cba.sgcvp.entity.Fornecedor;
import ifmt.cba.sgcvp.entity.ItemPedidoCompra;
import ifmt.cba.sgcvp.entity.ItemPedidoVenda;
import ifmt.cba.sgcvp.entity.LancamentoComissao;
import ifmt.cba.sgcvp.entity.Municipio;
import ifmt.cba.sgcvp.entity.PedidoCompra;
import ifmt.cba.sgcvp.entity.PedidoVenda;
import ifmt.cba.sgcvp.entity.Produto;
import ifmt.cba.sgcvp.entity.Promotor;
import ifmt.cba.sgcvp.repository.CategoriaProdutoRepository;
import ifmt.cba.sgcvp.repository.ClienteRepository;
import ifmt.cba.sgcvp.repository.FornecedorRepository;
import ifmt.cba.sgcvp.repository.LancamentoComissaoRepository;
import ifmt.cba.sgcvp.repository.MunicipioRepository;
import ifmt.cba.sgcvp.repository.PedidoCompraRepository;
import ifmt.cba.sgcvp.repository.PedidoVendaRepository;
import ifmt.cba.sgcvp.repository.ProdutoRepository;
import ifmt.cba.sgcvp.repository.PromotorRepository;

@Component
// Popula dados iniciais para permitir testes manuais no Swagger.
public class GeradorBaseDados implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(GeradorBaseDados.class);

    @Autowired
    private CategoriaProdutoRepository categoriaProdutoRepository;

    @Autowired
    private MunicipioRepository municipioRepository;

    @Autowired
    private PromotorRepository promotorRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoCompraRepository pedidoCompraRepository;

    @Autowired
    private PedidoVendaRepository pedidoVendaRepository;

    @Autowired
    private LancamentoComissaoRepository lancamentoComissaoRepository;

    @Override
    // Executa a carga inicial logo apos a subida do contexto Spring.
    public void run(String... args) throws Exception {
        logger.info("Verificando dados iniciais para testes manuais no Swagger...");

        CategoriaProduto calcados = obterOuCriarCategoria("Calcados", "Sapatos, sandalias e tenis", "5.00", "10.00");
        CategoriaProduto acessorios = obterOuCriarCategoria("Acessorios", "Meias, cintos e itens complementares",
                "3.00", "5.00");
        CategoriaProduto bebidas = obterOuCriarCategoria("Bebidas", "Produtos liquidos destinados a venda", "5.00",
                "10.00");

        Municipio cuiaba = obterOuCriarMunicipio("Cuiaba", "MT");
        Municipio varzeaGrande = obterOuCriarMunicipio("Varzea Grande", "MT");
        Municipio rondonopolis = obterOuCriarMunicipio("Rondonopolis", "MT");

        Promotor joao = obterOuCriarPromotor("Joao Leme", Arrays.asList(cuiaba, varzeaGrande));
        Promotor maria = obterOuCriarPromotor("Maria Souza", Arrays.asList(cuiaba, rondonopolis));
        Promotor carla = obterOuCriarPromotor("Carla Nunes", Arrays.asList(varzeaGrande, rondonopolis));

        Cliente mercadoModelo = obterOuCriarCliente("Mercado Modelo Ltda", "Mercado Modelo", "12345678000190",
                "Rua das Flores, 100", "MT", joao);
        Cliente padariaCentral = obterOuCriarCliente("Padaria Central Ltda", "Padaria Central", "22345678000191",
                "Avenida Brasil, 250", "MT", maria);
        Cliente lojaBomPreco = obterOuCriarCliente("Loja Bom Preco Ltda", "Bom Preco", "32345678000192",
                "Rua Comercial, 80", "MT", carla);

        Fornecedor distribuidoraCentro = obterOuCriarFornecedor("Distribuidora Centro Ltda", "42345678000193",
                "Distrito Industrial, 10");
        Fornecedor calcadosNorte = obterOuCriarFornecedor("Calcados Norte Ltda", "52345678000194",
                "Rua dos Operarios, 45");
        Fornecedor bebidasSul = obterOuCriarFornecedor("Bebidas Sul Ltda", "62345678000195",
                "Avenida das Industrias, 77");

        Produto tenis = obterOuCriarProduto("Tenis Corrida", "Tenis esportivo para venda", "180.00", "120.00",
                "35.00", "0.00", 50, 10, 120, 0, "5.00", calcados);
        Produto cinto = obterOuCriarProduto("Cinto Couro", "Cinto de couro masculino", "75.00", "42.00", "30.00",
                "0.00", 35, 8, 90, 0, "3.00", acessorios);
        Produto cafe = obterOuCriarProduto("Cafe Torrado 500g", "Cafe torrado e moido em embalagem de 500g",
                "18.90", "12.50", "30.00", "0.00", 120, 20, 240, 0, "5.00", bebidas);

        criarPedidosCompraSeNecessario(distribuidoraCentro, calcadosNorte, bebidasSul, tenis, cinto, cafe);
        List<PedidoVenda> pedidosVenda = criarPedidosVendaSeNecessario(mercadoModelo, padariaCentral, lojaBomPreco,
                joao, maria, carla, tenis, cinto, cafe);
        criarLancamentosComissaoSeNecessario(pedidosVenda, joao, maria, carla);

        logger.info("Dados iniciais para testes manuais verificados com sucesso.");
    }

    // Recupera uma categoria existente ou cria uma nova para os testes.
    private CategoriaProduto obterOuCriarCategoria(String nome, String descricao, String percentualComissao,
            String percentualDesconto) {
        CategoriaProduto categoriaProduto = categoriaProdutoRepository.findByNomeIgnoreCaseStartingWith(nome);
        if (categoriaProduto != null) {
            return categoriaProduto;
        }

        categoriaProduto = new CategoriaProduto();
        categoriaProduto.setNome(nome);
        categoriaProduto.setDescricao(descricao);
        categoriaProduto.setPercentualComissao(new BigDecimal(percentualComissao));
        categoriaProduto.setPercentualDesconto(new BigDecimal(percentualDesconto));
        return categoriaProdutoRepository.save(categoriaProduto);
    }

    // Recupera um municipio existente ou cria um novo para os testes.
    private Municipio obterOuCriarMunicipio(String nome, String uf) {
        Municipio municipio = municipioRepository.findByNomeIgnoreCaseStartingWith(nome);
        if (municipio != null) {
            return municipio;
        }

        municipio = new Municipio();
        municipio.setNome(nome);
        municipio.setUF(uf);
        return municipioRepository.save(municipio);
    }

    // Recupera um promotor existente ou cria um novo com municipios atendidos.
    private Promotor obterOuCriarPromotor(String nome, List<Municipio> municipios) {
        Promotor promotor = promotorRepository.findByNomeIgnoreCaseStartingWith(nome);
        if (promotor != null) {
            return promotor;
        }

        promotor = new Promotor();
        promotor.setNome(nome);
        promotor.setListaMunicipio(municipios);
        return promotorRepository.save(promotor);
    }

    // Recupera um cliente existente ou cria um novo vinculado ao promotor.
    private Cliente obterOuCriarCliente(String razaoSocial, String nomeFantasia, String cnpj, String endereco,
            String uf, Promotor promotor) {
        Cliente cliente = clienteRepository.findByCNPJ(cnpj);
        if (cliente != null) {
            return cliente;
        }

        cliente = new Cliente();
        cliente.setRazaoSocial(razaoSocial);
        cliente.setNomeFantasia(nomeFantasia);
        cliente.setCNPJ(cnpj);
        cliente.setInscricaoEstadual("ISENTO");
        cliente.setEndereco(endereco);
        cliente.setUF(uf);
        cliente.setPromotor(promotor);
        return clienteRepository.save(cliente);
    }

    // Recupera um fornecedor existente ou cria um novo para pedidos de compra.
    private Fornecedor obterOuCriarFornecedor(String razaoSocial, String cnpj, String endereco) {
        Fornecedor fornecedor = fornecedorRepository.findByCNPJ(cnpj);
        if (fornecedor != null) {
            return fornecedor;
        }

        fornecedor = new Fornecedor();
        fornecedor.setRazaoSocial(razaoSocial);
        fornecedor.setCNPJ(cnpj);
        fornecedor.setEndereco(endereco);
        return fornecedorRepository.save(fornecedor);
    }

    // Recupera um produto existente ou cria um novo com dados de estoque.
    private Produto obterOuCriarProduto(String nome, String descricao, String precoVenda, String valorCusto,
            String margemLucro, String percentualPromocao, int quantidadeEstoque, int estoqueMinimo,
            int quantidadeMaximaEstoque, int quantidadeReservadaPedido, String percentualComissao,
            CategoriaProduto categoriaProduto) {
        Produto produto = produtoRepository.findByNomeIgnoreCaseStartingWith(nome);
        if (produto != null) {
            return produto;
        }

        produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPrecoVenda(new BigDecimal(precoVenda));
        produto.setValorCusto(new BigDecimal(valorCusto));
        produto.setMargemLucro(new BigDecimal(margemLucro));
        produto.setPercentualPromocao(new BigDecimal(percentualPromocao));
        produto.setQuantidadeEstoque(quantidadeEstoque);
        produto.setEstoqueMinimo(estoqueMinimo);
        produto.setQuantidadeMaximaEstoque(quantidadeMaximaEstoque);
        produto.setQuantidadeReservadaPedido(quantidadeReservadaPedido);
        produto.setPercentualComissao(new BigDecimal(percentualComissao));
        produto.setCategoriaProduto(categoriaProduto);
        return produtoRepository.save(produto);
    }

    // Cria pedidos de compra basicos quando ainda nao ha massa minima.
    private void criarPedidosCompraSeNecessario(Fornecedor distribuidoraCentro, Fornecedor calcadosNorte,
            Fornecedor bebidasSul, Produto tenis, Produto cinto, Produto cafe) {
        if (pedidoCompraRepository.count() >= 3) {
            return;
        }

        criarPedidoCompra("NF-1001", "DIGITADA", distribuidoraCentro, tenis, "12");
        criarPedidoCompra("NF-1002", "CONFERIDA", calcadosNorte, cinto, "20");
        criarPedidoCompra("NF-1003", "PROCESSADA", bebidasSul, cafe, "40");
    }

    // Cria um pedido de compra com um item vinculado ao produto.
    private void criarPedidoCompra(String numeroNotaFiscal, String status, Fornecedor fornecedor, Produto produto,
            String quantidade) {
        if (pedidoCompraRepository.findByNumNotaFiscal(numeroNotaFiscal) != null) {
            return;
        }

        PedidoCompra pedidoCompra = new PedidoCompra();
        pedidoCompra.setDataEntrada(LocalDate.now());
        pedidoCompra.setNumNotaFiscal(numeroNotaFiscal);
        pedidoCompra.setStatus(status);
        pedidoCompra.setFornecedor(fornecedor);

        ItemPedidoCompra itemPedidoCompra = new ItemPedidoCompra();
        itemPedidoCompra.setQuantidade(new BigDecimal(quantidade));
        itemPedidoCompra.setProduto(produto);
        itemPedidoCompra.setPedidoCompra(pedidoCompra);
        pedidoCompra.getListaItemPedidoCompra().add(itemPedidoCompra);

        pedidoCompraRepository.save(pedidoCompra);
    }

    // Cria pedidos de venda basicos quando ainda nao ha massa minima.
    private List<PedidoVenda> criarPedidosVendaSeNecessario(Cliente mercadoModelo, Cliente padariaCentral,
            Cliente lojaBomPreco, Promotor joao, Promotor maria, Promotor carla, Produto tenis, Produto cinto,
            Produto cafe) {
        if (pedidoVendaRepository.count() >= 3) {
            return pedidoVendaRepository.findAll();
        }

        PedidoVenda pedido1 = criarPedidoVenda("PROCESSADO", "BOLETO", mercadoModelo, joao, tenis, "2", "180.00");
        PedidoVenda pedido2 = criarPedidoVenda("PROCESSADO", "PIX", padariaCentral, maria, cafe, "10", "18.90");
        PedidoVenda pedido3 = criarPedidoVenda("PROCESSADO", "CARTAO", lojaBomPreco, carla, cinto, "4", "75.00");

        return Arrays.asList(pedido1, pedido2, pedido3);
    }

    // Cria um pedido de venda com um item vinculado ao produto.
    private PedidoVenda criarPedidoVenda(String status, String formaPagamento, Cliente cliente, Promotor promotor,
            Produto produto, String quantidade, String valorUnitario) {
        PedidoVenda pedidoVenda = new PedidoVenda();
        pedidoVenda.setDataPedido(LocalDate.now());
        pedidoVenda.setStatus(status);
        pedidoVenda.setFormaPagamento(formaPagamento);
        pedidoVenda.setDataExecucaoPrevista(LocalDate.now().plusDays(3));
        pedidoVenda.setCliente(cliente);
        pedidoVenda.setPromotor(promotor);

        ItemPedidoVenda itemPedidoVenda = new ItemPedidoVenda();
        itemPedidoVenda.setQuantidade(new BigDecimal(quantidade));
        itemPedidoVenda.setValorUnitario(new BigDecimal(valorUnitario));
        itemPedidoVenda.setProduto(produto);
        itemPedidoVenda.setPedidoVenda(pedidoVenda);
        pedidoVenda.getListaItemPedidoVenda().add(itemPedidoVenda);

        return pedidoVendaRepository.save(pedidoVenda);
    }

    // Cria lancamentos de comissao para pedidos processados.
    private void criarLancamentosComissaoSeNecessario(List<PedidoVenda> pedidosVenda, Promotor joao, Promotor maria,
            Promotor carla) {
        if (lancamentoComissaoRepository.count() >= 3 || pedidosVenda.size() < 3) {
            return;
        }

        criarLancamentoComissao(pedidosVenda.get(0), joao, "LANCADA", "5.00");
        criarLancamentoComissao(pedidosVenda.get(1), maria, "QUITADA", "5.00");
        criarLancamentoComissao(pedidosVenda.get(2), carla, "LANCADA", "3.00");
    }

    // Cria um lancamento de comissao para um pedido especifico.
    private void criarLancamentoComissao(PedidoVenda pedidoVenda, Promotor promotor, String status,
            String percentualComissao) {
        if (lancamentoComissaoRepository.findByPedidoVenda(pedidoVenda) != null) {
            return;
        }

        LancamentoComissao lancamentoComissao = new LancamentoComissao();
        lancamentoComissao.setDataLancamento(LocalDate.now());
        lancamentoComissao.setValor(calcularValorComissao(pedidoVenda, new BigDecimal(percentualComissao)));
        lancamentoComissao.setStatus(status);
        lancamentoComissao.setPromotor(promotor);
        lancamentoComissao.setPedidoVenda(pedidoVenda);
        lancamentoComissaoRepository.save(lancamentoComissao);
    }

    // Calcula a comissao com base no total do pedido e percentual informado.
    private BigDecimal calcularValorComissao(PedidoVenda pedidoVenda, BigDecimal percentualComissao) {
        BigDecimal totalPedido = BigDecimal.ZERO;
        for (ItemPedidoVenda itemPedidoVenda : pedidoVenda.getListaItemPedidoVenda()) {
            totalPedido = totalPedido.add(itemPedidoVenda.getQuantidade().multiply(itemPedidoVenda.getValorUnitario()));
        }
        return totalPedido.multiply(percentualComissao).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
