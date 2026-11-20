package model;

public class Operador extends Usuario {

    public Operador() {
        super();
        setPerfil("OPERADOR");
    }

    public Operador(String nome, String login, String senha) {
        super(nome, login, senha, "OPERADOR");
    }

    @Override
    public boolean podeGerenciarUsuarios() {
        return false;
    }

    @Override
    public boolean podeEstornarMovimentacao() {
        return false;
    }
}
