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
@Table(name = "municipio")
// Representa um municipio atendido por promotores.
public class Municipio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_municipio")
    private int codigo;

    @Column(name = "nome", length = 150)
    private String nome;

    @Column(name = "uf", length = 2)
    private String UF;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    // Valida nome e UF do municipio.
    public String validar() {
        String retorno = "";

        if (this.codigo <= 0 && (this.nome == null || this.nome.length() < 3)) {
            retorno += "Municipio invalido";
        }

        if (this.UF != null && this.UF.length() != 2) {
            retorno += "UF invalida";
        }

        return retorno;
    }
}
