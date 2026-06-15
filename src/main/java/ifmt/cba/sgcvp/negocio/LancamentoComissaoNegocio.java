package ifmt.cba.sgcvp.negocio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ifmt.cba.sgcvp.dto.LancamentoComissaoDTO;
import ifmt.cba.sgcvp.entity.LancamentoComissao;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.TransicaoEstadoInvalidaException;
import ifmt.cba.sgcvp.repository.LancamentoComissaoRepository;
import ifmt.cba.sgcvp.repository.PromotorRepository;

@Service
public class LancamentoComissaoNegocio {

    private static final Logger logger = LoggerFactory.getLogger(LancamentoComissaoNegocio.class);
    private static final String LANCADA = "LANCADA";
    private static final String QUITADA = "QUITADA";

    private ModelMapper modelMapper;

    @Autowired
    private LancamentoComissaoRepository lancamentoComissaoRepository;

    @Autowired
    private PromotorRepository promotorRepository;

    public LancamentoComissaoNegocio() {
        this.modelMapper = new ModelMapper();
    }

    public List<LancamentoComissaoDTO> pesquisaTodos() throws NotFoundException {
        try {
            logger.info("Pesquisando todos os lancamentos de comissao");
            return this.toDTOAll(lancamentoComissaoRepository.findAll());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar lancamentos de comissao", ex);
            throw new NotFoundException("Erro ao pesquisar lancamentos de comissao - " + ex.getMessage());
        }
    }

    public List<LancamentoComissaoDTO> pesquisaPorStatus(String status) throws NotFoundException {
        try {
            logger.info("Pesquisando lancamentos de comissao pelo status {}", status);
            return this.toDTOAll(lancamentoComissaoRepository.findByStatusIgnoreCaseStartingWith(status));
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar lancamento de comissao pelo status", ex);
            throw new NotFoundException("Erro ao pesquisar lancamento de comissao pelo status - " + ex.getMessage());
        }
    }

    public LancamentoComissaoDTO pesquisaCodigo(int codigo) throws NotFoundException {
        try {
            logger.info("Pesquisando lancamento de comissao pelo codigo {}", codigo);
            return this.toDTO(lancamentoComissaoRepository.findById(codigo).get());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar lancamento de comissao pelo codigo", ex);
            throw new NotFoundException("Erro ao pesquisar lancamento de comissao pelo codigo - " + ex.getMessage());
        }
    }

    public List<LancamentoComissaoDTO> pesquisaPorStatusPeriodoPromotor(String status, LocalDate dataInicial,
            LocalDate dataFinal, int codigoPromotor) throws NotFoundException {
        try {
            if (promotorRepository.findById(codigoPromotor).isEmpty()) {
                throw new NotFoundException("Nao existe esse promotor");
            }
            logger.info("Pesquisando lancamentos de comissao status {} do promotor {} entre {} e {}", status,
                    codigoPromotor, dataInicial, dataFinal);
            return this.toDTOAll(lancamentoComissaoRepository.findByStatusDataLancamentoAndPromotor(status,
                    dataInicial, dataFinal, codigoPromotor));
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar lancamentos de comissao por status, periodo e promotor", ex);
            throw new NotFoundException(
                    "Erro ao pesquisar lancamentos de comissao por status, periodo e promotor - " + ex.getMessage());
        }
    }

    public List<LancamentoComissaoDTO> pesquisaLancadasPeriodoPromotor(LocalDate dataInicial, LocalDate dataFinal,
            int codigoPromotor) throws NotFoundException {
        return this.pesquisaPorStatusPeriodoPromotor(LANCADA, dataInicial, dataFinal, codigoPromotor);
    }

    public List<LancamentoComissaoDTO> pesquisaQuitadasPeriodoPromotor(LocalDate dataInicial, LocalDate dataFinal,
            int codigoPromotor) throws NotFoundException {
        return this.pesquisaPorStatusPeriodoPromotor(QUITADA, dataInicial, dataFinal, codigoPromotor);
    }

    @Transactional
    public LancamentoComissaoDTO quitar(int codigo) throws NotFoundException, TransicaoEstadoInvalidaException {
        LancamentoComissao lancamentoComissao = lancamentoComissaoRepository.findById(codigo)
                .orElseThrow(() -> new NotFoundException("Nao existe esse lancamento de comissao"));

        if (lancamentoComissao.getStatus() == null || !lancamentoComissao.getStatus().equalsIgnoreCase(LANCADA)) {
            logger.warn("Transicao invalida do lancamento de comissao {}: {} para {}", codigo,
                    lancamentoComissao.getStatus(), QUITADA);
            throw new TransicaoEstadoInvalidaException(
                    "Transicao invalida de " + lancamentoComissao.getStatus() + " para " + QUITADA);
        }

        lancamentoComissao.setStatus(QUITADA);
        lancamentoComissao = lancamentoComissaoRepository.save(lancamentoComissao);
        logger.info("Lancamento de comissao {} alterado para {}", codigo, QUITADA);
        return this.toDTO(lancamentoComissao);
    }

    public List<LancamentoComissaoDTO> toDTOAll(List<LancamentoComissao> listaLancamentoComissao) {
        List<LancamentoComissaoDTO> listaDTO = new ArrayList<LancamentoComissaoDTO>();

        for (LancamentoComissao lancamentoComissao : listaLancamentoComissao) {
            listaDTO.add(this.toDTO(lancamentoComissao));
        }
        return listaDTO;
    }

    public LancamentoComissaoDTO toDTO(LancamentoComissao lancamentoComissao) {
        return this.modelMapper.map(lancamentoComissao, LancamentoComissaoDTO.class);
    }
}
