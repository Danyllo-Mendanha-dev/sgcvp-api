package ifmt.cba.sgcvp.negocio;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ifmt.cba.sgcvp.dto.PromotorDTO;
import ifmt.cba.sgcvp.dto.MunicipioDTO;
import ifmt.cba.sgcvp.entity.Municipio;
import ifmt.cba.sgcvp.entity.Promotor;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.NotValidDataException;
import ifmt.cba.sgcvp.repository.ClienteRepository;
import ifmt.cba.sgcvp.repository.MunicipioRepository;
import ifmt.cba.sgcvp.repository.PromotorRepository;

@Service
// Centraliza as regras de cadastro e consulta de promotores.
public class PromotorNegocio {

    private static final Logger logger = LoggerFactory.getLogger(PromotorNegocio.class);

    private ModelMapper modelMapper;

    @Autowired
    private PromotorRepository promotorRepository;

    @Autowired
    private MunicipioRepository municipioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public PromotorNegocio() {
        this.modelMapper = new ModelMapper();
    }

    // Valida e cadastra um novo promotor.
    public PromotorDTO inserir(PromotorDTO promotorDTO) throws NotValidDataException, NotFoundException {

        Promotor promotor = this.toEntity(promotorDTO);
        String mensagemErros = promotor.validar();

        if (!mensagemErros.isEmpty()) {
            logger.warn("Dados invalidos para incluir promotor: {}", mensagemErros);
            throw new NotValidDataException(mensagemErros);
        }

        try {
            if (promotorRepository.findByNomeIgnoreCaseStartingWith(promotor.getNome()) != null) {
                logger.warn("Tentativa de incluir promotor duplicado: {}", promotor.getNome());
                throw new NotValidDataException("Ja existe esse promotor");
            }
            promotor.setListaMunicipio(this.pesquisarMunicipios(promotor.getListaMunicipio()));
            promotor = promotorRepository.save(promotor);
            logger.info("Promotor incluido com codigo {}", promotor.getCodigo());
        } catch (NotFoundException | NotValidDataException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao incluir promotor", ex);
            throw new NotValidDataException("Erro ao incluir o promotor - " + ex.getMessage());
        }

        return this.toDTO(promotor);
    }

    // Valida e atualiza um promotor existente.
    public PromotorDTO alterar(PromotorDTO promotorDTO) throws NotValidDataException, NotFoundException {

        Promotor promotor = this.toEntity(promotorDTO);
        String mensagemErros = promotor.validar();
        if (!mensagemErros.isEmpty()) {
            logger.warn("Dados invalidos para alterar promotor: {}", mensagemErros);
            throw new NotValidDataException(mensagemErros);
        }
        try {
            if (promotorRepository.findById(promotor.getCodigo()).isEmpty()) {
                logger.warn("Tentativa de alterar promotor inexistente: {}", promotor.getCodigo());
                throw new NotFoundException("Nao existe esse promotor");
            }
            promotor.setListaMunicipio(this.pesquisarMunicipios(promotor.getListaMunicipio()));
            promotor = promotorRepository.save(promotor);
            logger.info("Promotor alterado com codigo {}", promotor.getCodigo());
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao alterar promotor", ex);
            throw new NotValidDataException("Erro ao alterar o promotor - " + ex.getMessage());
        }
        return this.toDTO(promotor);
    }

    // Exclui um promotor se ele nao estiver vinculado a clientes.
    public void excluir(int codigo) throws NotValidDataException, NotFoundException {
        try {
            Promotor promotor = promotorRepository.findById(codigo)
                    .orElseThrow(() -> new NotFoundException("Nao existe esse promotor"));

            if (clienteRepository.findByPromotor(promotor).size() > 0) {
                logger.warn("Tentativa de excluir promotor relacionado a clientes: {}", codigo);
                throw new NotValidDataException("Promotor esta relacionado a clientes");
            }
            promotorRepository.delete(promotor);
            logger.info("Promotor excluido com codigo {}", codigo);
        } catch (NotFoundException | NotValidDataException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao excluir promotor", ex);
            throw new NotValidDataException("Erro ao excluir o promotor - " + ex.getMessage());
        }
    }

    // Retorna todos os promotores cadastrados.
    public List<PromotorDTO> pesquisaTodos() throws NotFoundException {
        try {
            logger.info("Pesquisando todos os promotores");
            return this.toDTOAll(promotorRepository.findAll());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar promotores", ex);
            throw new NotFoundException("Erro ao pesquisar promotores - " + ex.getMessage());
        }
    }

    // Busca um promotor pelo inicio do nome.
    public PromotorDTO pesquisaPorNome(String parteNome) throws NotFoundException {
        try {
            logger.info("Pesquisando promotor pelo nome {}", parteNome);
            return this.toDTO(promotorRepository.findByNomeIgnoreCaseStartingWith(parteNome));
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar promotor pelo nome", ex);
            throw new NotFoundException("Erro ao pesquisar promotor pelo nome - " + ex.getMessage());
        }
    }

    // Busca um promotor pelo codigo informado.
    public PromotorDTO pesquisaCodigo(int codigo) throws NotFoundException {
        try {
            logger.info("Pesquisando promotor pelo codigo {}", codigo);
            return this.toDTO(promotorRepository.findById(codigo).get());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar promotor pelo codigo", ex);
            throw new NotFoundException("Erro ao pesquisar promotor pelo codigo - " + ex.getMessage());
        }
    }

    // Lista os municipios atendidos por um promotor.
    public List<MunicipioDTO> pesquisaMunicipiosAtendidos(int codigoPromotor) throws NotFoundException {
        try {
            if (promotorRepository.findById(codigoPromotor).isEmpty()) {
                throw new NotFoundException("Nao existe esse promotor");
            }
            logger.info("Pesquisando municipios atendidos pelo promotor {}", codigoPromotor);
            List<MunicipioDTO> listaDTO = new ArrayList<MunicipioDTO>();
            for (Municipio municipio : municipioRepository.findMunicipiosByPromotorOrderByNome(codigoPromotor)) {
                listaDTO.add(this.modelMapper.map(municipio, MunicipioDTO.class));
            }
            return listaDTO;
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar municipios atendidos pelo promotor", ex);
            throw new NotFoundException("Erro ao pesquisar municipios atendidos pelo promotor - " + ex.getMessage());
        }
    }

    private List<Municipio> pesquisarMunicipios(List<Municipio> listaMunicipio) throws NotFoundException {
        List<Municipio> listaMunicipioTemp = new ArrayList<Municipio>();

        for (Municipio municipio : listaMunicipio) {
            Municipio municipioTemp = municipioRepository.findById(municipio.getCodigo())
                    .orElseThrow(() -> new NotFoundException("Nao existe esse municipio"));
            listaMunicipioTemp.add(municipioTemp);
        }
        return listaMunicipioTemp;
    }

    public List<PromotorDTO> toDTOAll(List<Promotor> listaPromotor) {
        List<PromotorDTO> listaDTO = new ArrayList<PromotorDTO>();

        for (Promotor promotor : listaPromotor) {
            listaDTO.add(this.toDTO(promotor));
        }
        return listaDTO;
    }

    public PromotorDTO toDTO(Promotor promotor) {
        return this.modelMapper.map(promotor, PromotorDTO.class);
    }

    public Promotor toEntity(PromotorDTO promotorDTO) {
        return this.modelMapper.map(promotorDTO, Promotor.class);
    }
}
