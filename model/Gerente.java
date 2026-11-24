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
    public void setPerfil(String perfil) {
        if (perfil == null || !"GERENTE".equalsIgnoreCase(perfil.trim())) {
            throw new IllegalArgumentException("Uma conta Gerente deve ter o perfil GERENTE.");
        }
        super.setPerfil("GERENTE");
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
