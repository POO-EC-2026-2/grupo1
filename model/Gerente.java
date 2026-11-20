package model;

public class Gerente extends Usuario {

    public Gerente() {
        super();
        setPerfil("GERENTE");
    }

    public Gerente(String nome, String login, String senha) {
        super(nome, login, senha, "GERENTE");
    }

    @Override
    public boolean podeGerenciarUsuarios() {
        return true;
    }

    @Override
    public boolean podeEstornarMovimentacao() {
        return true;
    }
}
