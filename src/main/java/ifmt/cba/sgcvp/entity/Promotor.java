package ifmt.cba.sgcvp.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
@Table(name = "promotor")
public class Promotor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promotor")
    private int codigo;

    @Column(name = "nome", length = 150, nullable = false)
    private String nome;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "promotor_municipio",
            joinColumns = @JoinColumn(name = "id_promotor"),
            inverseJoinColumns = @JoinColumn(name = "id_municipio"))
    private List<Municipio> listaMunicipio = new ArrayList<Municipio>();

    @OneToMany(mappedBy = "promotor", fetch = FetchType.LAZY)
    private List<Cliente> listaCliente = new ArrayList<Cliente>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public String validar() {
        String retorno = "";

        if (this.nome == null || this.nome.length() < 3 || this.nome.length() > 150) {
            retorno += "Nome invalido";
        }

        if (this.listaMunicipio == null || this.listaMunicipio.isEmpty()) {
            retorno += "Municipio de atuacao invalido";
        }

        return retorno;
    }
}
