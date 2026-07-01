package ifmt.cba.sgcvp.exception;

// Indica que nao ha estoque suficiente para concluir a operacao.
public class EstoqueInsuficienteException extends Exception {

    // Cria a excecao com mensagem padrao de estoque.
    public EstoqueInsuficienteException() {
        super("Estoque insuficiente");
    }

    // Cria a excecao com mensagem especifica de estoque.
    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
