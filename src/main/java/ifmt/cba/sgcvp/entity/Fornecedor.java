package ifmt.cba.sgcvp.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "fornecedor")
// Representa um fornecedor usado em pedidos de compra.
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fornecedor")
    private int codigo;

    @Column(name = "razao_social", length = 200, nullable = false)
    private String razaoSocial;

    @Column(name = "cnpj", length = 14, nullable = false, unique = true)
    private String CNPJ;

    @Column(name = "endereco", length = 255, nullable = false)
    private String endereco;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    // Valida dados cadastrais obrigatorios do fornecedor.
    public String validar() {
        String retorno = "";

        if (this.razaoSocial == null || this.razaoSocial.length() < 3 || this.razaoSocial.length() > 200) {
            retorno += "Razao social invalida";
        }

        if (this.CNPJ == null || !this.CNPJ.matches("\\d{14}")) {
            retorno += "CNPJ invalido";
        }

        if (this.endereco == null || this.endereco.length() < 3 || this.endereco.length() > 255) {
            retorno += "Endereco invalido";
        }

        return retorno;
    }
}
