package ifmt.cba.exemplo.controller;

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

import java.util.List;

import ifmt.cba.exemplo.dto.BairroDTO;
import ifmt.cba.exemplo.exception.NotFoundException;
import ifmt.cba.exemplo.exception.NotValidDataException;
import ifmt.cba.exemplo.negocio.BairroNegocio;

@RestController()
@RequestMapping("/bairro")
public class BairroController {

    @Autowired
    private BairroNegocio bairroNegocio;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BairroDTO> buscarTodos() throws NotFoundException, NotValidDataException {
        List<BairroDTO> listaBairroTempDTO = bairroNegocio.pesquisaTodos();      
        return listaBairroTempDTO;
    }

    @GetMapping(value = "/codigo/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BairroDTO buscarPorID(@PathVariable("codigo") int codigo) throws NotFoundException, NotValidDataException {
        BairroDTO bairroTempDTO = bairroNegocio.pesquisaCodigo(codigo);
        return bairroTempDTO;
    }

    @GetMapping(value = "/nome/{nome}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BairroDTO buscarPorNome(@PathVariable("nome") String nome) throws NotFoundException, NotValidDataException {
        BairroDTO bairroTempDTO = bairroNegocio.pesquisaPorNome(nome);
        return bairroTempDTO;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BairroDTO inserirBairro(@RequestBody BairroDTO bairroDTO) throws NotFoundException, NotValidDataException {
        BairroDTO bairroTempDTO = bairroNegocio.inserir(bairroDTO);
        return bairroTempDTO;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BairroDTO alterarBairro(@RequestBody BairroDTO bairroDTO) throws NotFoundException, NotValidDataException {
        BairroDTO bairroTempDTO = bairroNegocio.alterar(bairroDTO);
        return bairroTempDTO;
    }

    @DeleteMapping(value = "/{codigo}")
    public ResponseEntity<?> excluirBairro(@PathVariable("codigo") int codigo) throws NotFoundException, NotValidDataException {
        bairroNegocio.excluir(codigo);
        return ResponseEntity.noContent().build();
    }
}
