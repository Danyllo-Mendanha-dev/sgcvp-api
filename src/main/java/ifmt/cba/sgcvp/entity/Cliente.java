package ifmt.cba.sgcvp.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "cliente")
// Representa um cliente atendido por um promotor.
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private int codigo;

    @Column(name = "razao_social", length = 200, nullable = false)
    private String razaoSocial;

    @Column(name = "nome_fantasia", length = 200, nullable = false)
    private String nomeFantasia;

    @Column(name = "cnpj", length = 14, nullable = false, unique = true)
    private String CNPJ;

    @Column(name = "inscricao_estadual", length = 20)
    private String inscricaoEstadual;

    @Column(name = "endereco", length = 255, nullable = false)
    private String endereco;

    @Column(name = "uf", length = 2, nullable = false)
    private String UF;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_promotor", nullable = false)
    private Promotor promotor;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    // Valida dados cadastrais e vinculo com promotor.
    public String validar() {
        String retorno = "";

        if (this.razaoSocial == null || this.razaoSocial.length() < 3 || this.razaoSocial.length() > 200) {
            retorno += "Razao social invalida";
        }

        if (this.nomeFantasia == null || this.nomeFantasia.length() < 3 || this.nomeFantasia.length() > 200) {
            retorno += "Nome fantasia invalido";
        }

        if (this.CNPJ == null || !this.CNPJ.matches("\\d{14}")) {
            retorno += "CNPJ invalido";
        }

        if (this.inscricaoEstadual != null && this.inscricaoEstadual.length() > 20) {
            retorno += "Inscricao estadual invalida";
        }

        if (this.endereco == null || this.endereco.length() < 3 || this.endereco.length() > 255) {
            retorno += "Endereco invalido";
        }

        if (this.UF == null || !this.UF.matches("[A-Z]{2}")) {
            retorno += "UF invalida";
        }

        if (this.promotor == null || this.promotor.getCodigo() <= 0) {
            retorno += "Promotor invalido";
        }

        return retorno;
    }
}
