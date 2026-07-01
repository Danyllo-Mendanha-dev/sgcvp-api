package ifmt.cba.sgcvp.negocio;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ifmt.cba.sgcvp.dto.ClienteDTO;
import ifmt.cba.sgcvp.dto.ClienteValorVendidoDTO;
import ifmt.cba.sgcvp.entity.Cliente;
import ifmt.cba.sgcvp.entity.Promotor;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.NotValidDataException;
import ifmt.cba.sgcvp.repository.ClienteRepository;
import ifmt.cba.sgcvp.repository.PromotorRepository;

@Service
// Centraliza as regras de cadastro e consulta de clientes.
public class ClienteNegocio {

    private static final Logger logger = LoggerFactory.getLogger(ClienteNegocio.class);

    private ModelMapper modelMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PromotorRepository promotorRepository;

    public ClienteNegocio() {
        this.modelMapper = new ModelMapper();
    }

    // Valida e cadastra um novo cliente.
    public ClienteDTO inserir(ClienteDTO clienteDTO) throws NotValidDataException, NotFoundException {

        Cliente cliente = this.toEntity(clienteDTO);
        String mensagemErros = cliente.validar();

        if (!mensagemErros.isEmpty()) {
            logger.warn("Dados invalidos para incluir cliente: {}", mensagemErros);
            throw new NotValidDataException(mensagemErros);
        }

        try {
            if (clienteRepository.findByCNPJ(cliente.getCNPJ()) != null) {
                logger.warn("Tentativa de incluir cliente duplicado: {}", cliente.getCNPJ());
                throw new NotValidDataException("Ja existe esse cliente");
            }
            cliente.setPromotor(this.pesquisarPromotor(cliente.getPromotor().getCodigo()));
            cliente = clienteRepository.save(cliente);
            logger.info("Cliente incluido com codigo {}", cliente.getCodigo());
        } catch (NotFoundException | NotValidDataException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao incluir cliente", ex);
            throw new NotValidDataException("Erro ao incluir o cliente - " + ex.getMessage());
        }
        return this.toDTO(cliente);
    }

    // Valida e atualiza um cliente existente.
    public ClienteDTO alterar(ClienteDTO clienteDTO) throws NotValidDataException, NotFoundException {

        Cliente cliente = this.toEntity(clienteDTO);
        String mensagemErros = cliente.validar();
        if (!mensagemErros.isEmpty()) {
            logger.warn("Dados invalidos para alterar cliente: {}", mensagemErros);
            throw new NotValidDataException(mensagemErros);
        }
        try {
            if (clienteRepository.findById(cliente.getCodigo()).isEmpty()) {
                logger.warn("Tentativa de alterar cliente inexistente: {}", cliente.getCodigo());
                throw new NotFoundException("Nao existe esse cliente");
            }
            cliente.setPromotor(this.pesquisarPromotor(cliente.getPromotor().getCodigo()));
            cliente = clienteRepository.save(cliente);
            logger.info("Cliente alterado com codigo {}", cliente.getCodigo());
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao alterar cliente", ex);
            throw new NotValidDataException("Erro ao alterar o cliente - " + ex.getMessage());
        }
        return this.toDTO(cliente);
    }

    // Exclui um cliente pelo codigo informado.
    public void excluir(int codigo) throws NotValidDataException, NotFoundException {

        try {
            Cliente cliente = clienteRepository.findById(codigo)
                    .orElseThrow(() -> new NotFoundException("Nao existe esse cliente"));
            clienteRepository.delete(cliente);
            logger.info("Cliente excluido com codigo {}", codigo);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao excluir cliente", ex);
            throw new NotValidDataException("Erro ao excluir o cliente - " + ex.getMessage());
        }
    }

    // Retorna todos os clientes cadastrados.
    public List<ClienteDTO> pesquisaTodos() throws NotFoundException {
        try {
            logger.info("Pesquisando todos os clientes");
            return this.toDTOAll(clienteRepository.findAll());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar clientes", ex);
            throw new NotFoundException("Erro ao pesquisar clientes - " + ex.getMessage());
        }
    }

    // Busca um cliente pelo inicio da razao social.
    public ClienteDTO pesquisaPorRazaoSocial(String parteRazaoSocial) throws NotFoundException {
        try {
            logger.info("Pesquisando cliente pela razao social {}", parteRazaoSocial);
            return this.toDTO(clienteRepository.findByRazaoSocialIgnoreCaseStartingWith(parteRazaoSocial));
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar cliente pela razao social", ex);
            throw new NotFoundException("Erro ao pesquisar cliente pela razao social - " + ex.getMessage());
        }
    }

    // Busca um cliente pelo CNPJ exato.
    public ClienteDTO pesquisaPorCNPJ(String CNPJ) throws NotFoundException {
        try {
            logger.info("Pesquisando cliente pelo CNPJ {}", CNPJ);
            return this.toDTO(clienteRepository.findByCNPJ(CNPJ));
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar cliente pelo CNPJ", ex);
            throw new NotFoundException("Erro ao pesquisar cliente pelo CNPJ - " + ex.getMessage());
        }
    }

    // Busca um cliente pelo codigo informado.
    public ClienteDTO pesquisaCodigo(int codigo) throws NotFoundException {
        try {
            logger.info("Pesquisando cliente pelo codigo {}", codigo);
            return this.toDTO(clienteRepository.findById(codigo).get());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar cliente pelo codigo", ex);
            throw new NotFoundException("Erro ao pesquisar cliente pelo codigo - " + ex.getMessage());
        }
    }

    // Lista clientes de um promotor ordenados pelo valor vendido.
    public List<ClienteValorVendidoDTO> pesquisaClientesPorPromotorValorVendido(int codigoPromotor,
            LocalDate dataInicial, LocalDate dataFinal) throws NotFoundException {
        try {
            this.pesquisarPromotor(codigoPromotor);
            logger.info("Pesquisando clientes do promotor {} por valor vendido entre {} e {}", codigoPromotor,
                    dataInicial, dataFinal);
            return clienteRepository.findClientesPorPromotorOrderByValorVendidoDesc(codigoPromotor, dataInicial,
                    dataFinal);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar clientes por promotor e valor vendido", ex);
            throw new NotFoundException("Erro ao pesquisar clientes por promotor e valor vendido - " + ex.getMessage());
        }
    }

    private Promotor pesquisarPromotor(int codigo) throws NotFoundException {
        return promotorRepository.findById(codigo)
                .orElseThrow(() -> new NotFoundException("Nao existe esse promotor"));
    }

    public List<ClienteDTO> toDTOAll(List<Cliente> listaCliente) {
        List<ClienteDTO> listaDTO = new ArrayList<ClienteDTO>();

        for (Cliente cliente : listaCliente) {
            listaDTO.add(this.toDTO(cliente));
        }
        return listaDTO;
    }

    public ClienteDTO toDTO(Cliente cliente) {
        return this.modelMapper.map(cliente, ClienteDTO.class);
    }

    public Cliente toEntity(ClienteDTO clienteDTO) {
        return this.modelMapper.map(clienteDTO, Cliente.class);
    }
}
