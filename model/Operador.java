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
    public void setPerfil(String perfil) {
        if (perfil == null || !"OPERADOR".equalsIgnoreCase(perfil.trim())) {
            throw new IllegalArgumentException("Uma conta Operador deve ter o perfil OPERADOR.");
        }
        super.setPerfil("OPERADOR");
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
