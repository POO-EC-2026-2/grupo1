package controller;

import java.math.BigDecimal;

import exception.DadosInvalidosException;
import exception.EstoqueInsuficienteException;
import exception.OperacaoNaoPermitidaException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;

import model.Categoria;
import model.Fornecedor;
import model.Gerente;
import model.Movimentacao;
import model.Operador;
import model.Produto;
import model.Usuario;
import repository.CategoriaRepositorio;
import repository.Database;
import repository.FornecedorRepositorio;
import repository.MovimentacaoRepositorio;
import repository.ProdutoRepositorio;
import repository.UsuarioRepositorio;

public class EstoqueController {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter
            .ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private CategoriaRepositorio categoriaRepositorio;
    private FornecedorRepositorio fornecedorRepositorio;
    private UsuarioRepositorio usuarioRepositorio;
    private ProdutoRepositorio produtoRepositorio;
    private MovimentacaoRepositorio movimentacaoRepositorio;
    private Usuario usuarioLogado;
    private int idSessao;

    public EstoqueController() throws SQLException {
        categoriaRepositorio = new CategoriaRepositorio();
        fornecedorRepositorio = new FornecedorRepositorio();
        usuarioRepositorio = new UsuarioRepositorio();
        produtoRepositorio = new ProdutoRepositorio();
        movimentacaoRepositorio = new MovimentacaoRepositorio();
    }

    public Usuario iniciarSessao(String nome, String login, String senha, String perfil)
            throws SQLException, DadosInvalidosException {
        try {
            Usuario autenticado = Database.emTransacao(Database.getConnection(),
                    () -> autenticarOuCadastrar(nome, login, senha, perfil));
            usuarioLogado = autenticado;
            idSessao = autenticado.getId();
            return autenticado;
        } catch (SQLException e) {
            if (e.getCause() instanceof DadosInvalidosException causa) throw causa;
            throw e;
        }
    }

    private Usuario autenticarOuCadastrar(String nome, String login, String senha, String perfil)
            throws SQLException, DadosInvalidosException {
        validarTexto(login, "Informe o login do usuario.");
        validarTexto(senha, "Informe a senha.");
        String loginPadronizado = login.trim();
        Usuario existente = usuarioRepositorio.buscarPorLogin(loginPadronizado);

        if (existente != null) {
            if (!existente.getSenha().equals(senha)) {
                throw new DadosInvalidosException("Senha incorreta para o login informado.");
            }
            return existente;
        }

        validarTexto(nome, "Informe o nome para cadastro.");
        String perfilPadronizado = validarPerfil(perfil);
        boolean primeiroAcesso = usuarioRepositorio.listar().isEmpty();
        if (primeiroAcesso != "GERENTE".equals(perfilPadronizado)) {
            throw new DadosInvalidosException(primeiroAcesso
                    ? "A primeira conta deve ser de gerente."
                    : "Outro gerente deve cadastrar contas com esse perfil.");
        }
        Usuario novoUsuario;
        if ("GERENTE".equals(perfilPadronizado)) {
            novoUsuario = new Gerente(nome.trim(), loginPadronizado, senha);
        } else {
            novoUsuario = new Operador(nome.trim(), loginPadronizado, senha);
        }
        usuarioRepositorio.salvar(novoUsuario);
        return novoUsuario;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public Categoria cadastrarCategoria(String nome, String descricao)
            throws SQLException, DadosInvalidosException {
        conferirSessao();
        validarTexto(nome, "Informe o nome da categoria.");
        Categoria categoria = new Categoria(nome.trim(), descricao == null ? "" : descricao.trim());
        return categoriaRepositorio.salvar(categoria);
    }

    public void atualizarCategoria(Categoria categoria, String nome, String descricao)
            throws SQLException, DadosInvalidosException {
        conferirSessao();
        validarTexto(nome, "Informe o nome da categoria.");
        Categoria atual = categoria == null ? null : categoriaRepositorio.buscarPorId(categoria.getId());
        if (atual == null) throw new DadosInvalidosException("Registro inexistente.");
        atual.setNome(nome.trim());
        atual.setDescricao(descricao == null ? "" : descricao.trim());
        categoriaRepositorio.salvar(atual);
    }

    public void excluirCategoria(Categoria categoria)
            throws SQLException, OperacaoNaoPermitidaException {
        conferirSessao();
        if (produtoRepositorio.possuiCategoria(categoria)) {
            throw new OperacaoNaoPermitidaException(
                    "Nao e possivel excluir uma categoria usada por produtos.");
        }
        categoriaRepositorio.excluir(categoria);
    }

    public Fornecedor cadastrarFornecedor(String empresa, String cnpj, String telefone)
            throws SQLException, DadosInvalidosException {
        conferirSessao();
        validarTexto(empresa, "Informe a empresa do fornecedor.");
        validarTexto(cnpj, "Informe o CNPJ do fornecedor.");
        Fornecedor fornecedor = new Fornecedor(empresa.trim(), cnpj.trim(),
                telefone == null ? "" : telefone.trim());
        return fornecedorRepositorio.salvar(fornecedor);
    }

    public void atualizarFornecedor(Fornecedor fornecedor, String empresa, String cnpj, String telefone)
            throws SQLException, DadosInvalidosException {
        conferirSessao();
        validarTexto(empresa, "Informe a empresa do fornecedor.");
        validarTexto(cnpj, "Informe o CNPJ do fornecedor.");
        Fornecedor atual = fornecedor == null ? null : fornecedorRepositorio.buscarPorId(fornecedor.getId());
        if (atual == null) throw new DadosInvalidosException("Registro inexistente.");
        atual.setEmpresa(empresa.trim());
        atual.setCnpj(cnpj.trim());
        atual.setTelefone(telefone == null ? "" : telefone.trim());
        fornecedorRepositorio.salvar(atual);
    }

    public void excluirFornecedor(Fornecedor fornecedor)
            throws SQLException, OperacaoNaoPermitidaException {
        conferirSessao();
        if (produtoRepositorio.possuiFornecedor(fornecedor)) {
            throw new OperacaoNaoPermitidaException(
                    "Nao e possivel excluir um fornecedor usado por produtos.");
        }
        fornecedorRepositorio.excluir(fornecedor);
    }

    public Usuario cadastrarUsuario(String nome, String login, String senha, String perfil)
            throws SQLException, DadosInvalidosException, OperacaoNaoPermitidaException {
        exigirGerente("Somente gerente pode cadastrar usuarios.");
        validarTexto(nome, "Informe o nome do usuario.");
        validarTexto(login, "Informe o login do usuario.");
        validarTexto(senha, "Informe a senha do usuario.");
        String perfilPadronizado = validarPerfil(perfil);
        if (usuarioRepositorio.buscarPorLogin(login.trim()) != null) {
            throw new DadosInvalidosException("Login ja cadastrado.");
        }
        Usuario usuario;
        if ("GERENTE".equals(perfilPadronizado)) {
            usuario = new Gerente(nome.trim(), login.trim(), senha);
        } else {
            usuario = new Operador(nome.trim(), login.trim(), senha);
        }
        return usuarioRepositorio.salvar(usuario);
    }

    public void excluirUsuario(Usuario usuario)
            throws SQLException, OperacaoNaoPermitidaException {
        exigirGerente("Somente gerente pode excluir usuarios.");
        if (usuario == null || usuario.getId() == idSessao) {
            throw new OperacaoNaoPermitidaException(
                    "O usuario logado nao pode excluir a propria conta.");
        }
        if (movimentacaoRepositorio.possuiUsuario(usuario.getId())) {
            throw new OperacaoNaoPermitidaException("O usuario possui movimentacoes no historico.");
        }
        usuarioRepositorio.excluir(usuario);
    }

    public void atualizarUsuario(Usuario usuario, String nome, String login, String senha, String perfil)
            throws SQLException, DadosInvalidosException, OperacaoNaoPermitidaException {
        try {
            Usuario gravado = Database.emTransacao(Database.getConnection(),
                    () -> editarConta(usuario, nome, login, senha, perfil));
            if (gravado.getId() == idSessao) usuarioLogado = gravado;
        } catch (SQLException e) {
            if (e.getCause() instanceof DadosInvalidosException causa) throw causa;
            if (e.getCause() instanceof OperacaoNaoPermitidaException causa) throw causa;
            throw e;
        }
    }

    private Usuario editarConta(Usuario usuario, String nome, String login, String senha, String perfil)
            throws SQLException, DadosInvalidosException, OperacaoNaoPermitidaException {
        exigirGerente("Somente gerente pode atualizar usuarios.");
        validarTexto(nome, "Informe o nome do usuario.");
        validarTexto(login, "Informe o login do usuario.");
        String perfilPadronizado = validarPerfil(perfil);
        Usuario atual = usuario == null ? null : usuarioRepositorio.buscarPorId(usuario.getId());
        if (atual == null) throw new DadosInvalidosException("Usuario inexistente.");
        Usuario mesmoLogin = usuarioRepositorio.buscarPorLogin(login.trim());
        if (mesmoLogin != null && mesmoLogin.getId() != atual.getId()) {
            throw new DadosInvalidosException("Login ja cadastrado.");
        }
        if (atual.podeGerenciarUsuarios() && "OPERADOR".equals(perfilPadronizado)
                && usuarioRepositorio.contarGerentes() <= 1) {
            throw new OperacaoNaoPermitidaException("O ultimo gerente deve manter seu perfil.");
        }
        String senhaFinal = senha == null || senha.isBlank() ? atual.getSenha() : senha;
        Usuario editado = "GERENTE".equals(perfilPadronizado)
                ? new Gerente(nome.trim(), login.trim(), senhaFinal)
                : new Operador(nome.trim(), login.trim(), senhaFinal);
        editado.setId(atual.getId());
        usuarioRepositorio.salvar(editado);
        return editado;
    }

    public Produto cadastrarProduto(String codigo, String nome, Categoria categoria, Fornecedor fornecedor,
            int quantidade, double preco, int estoqueMinimo, String validade)
            throws SQLException, DadosInvalidosException {
        conferirSessao();
        validarProduto(codigo, nome, categoria, fornecedor, quantidade, preco, estoqueMinimo, validade);
        if (produtoRepositorio.buscarPorCodigo(codigo.trim()) != null) {
            throw new DadosInvalidosException("Codigo de produto ja cadastrado.");
        }
        Produto produto = new Produto(codigo.trim(), nome.trim(), categoria, fornecedor,
                quantidade, preco, estoqueMinimo, validade.trim());
        return produtoRepositorio.salvar(produto);
    }

    public void atualizarProduto(Produto produto, String codigo, String nome, Categoria categoria,
            Fornecedor fornecedor, int quantidade, double preco, int estoqueMinimo, String validade)
            throws SQLException, DadosInvalidosException {
        try {
            Database.emTransacao(Database.getConnection(), () -> {
                editarProdutoPersistido(produto, codigo, nome, categoria, fornecedor, quantidade, preco, estoqueMinimo, validade);
                return null;
            });
        } catch (SQLException e) {
            if (e.getCause() instanceof DadosInvalidosException causa) throw causa;
            throw e;
        }
    }

    private void editarProdutoPersistido(Produto produto, String codigo, String nome, Categoria categoria,
            Fornecedor fornecedor, int quantidade, double preco, int estoqueMinimo, String validade)
            throws SQLException, DadosInvalidosException {
        conferirSessao();
        validarProduto(codigo, nome, categoria, fornecedor, quantidade, preco, estoqueMinimo, validade);
        Produto atual = produto == null ? null : produtoRepositorio.buscarPorId(produto.getId());
        if (atual == null) throw new DadosInvalidosException("Registro inexistente.");
        if (produto.getQuantidade() != atual.getQuantidade()) {
            throw new DadosInvalidosException("Saldo alterado desde a consulta. Atualize a lista antes de editar.");
        }
        Produto mesmoCodigo = produtoRepositorio.buscarPorCodigo(codigo.trim());
        if (mesmoCodigo != null && mesmoCodigo.getId() != atual.getId()) {
            throw new DadosInvalidosException("Codigo de produto ja cadastrado.");
        }
        atual.setCodigo(codigo.trim());
        atual.setNome(nome.trim());
        atual.setCategoria(categoria);
        atual.setFornecedor(fornecedor);
        atual.setQuantidade(quantidade);
        atual.setPreco(preco);
        atual.setEstoqueMinimo(estoqueMinimo);
        atual.setValidade(validade.trim());
        produtoRepositorio.salvar(atual);
    }

    public void excluirProduto(Produto produto) throws SQLException {
        conferirSessao();
        if (produto == null || !movimentacaoRepositorio.listarPorProduto(produto).isEmpty()) {
            throw new SQLException("Selecione um produto sem movimentacoes no historico.");
        }
        produtoRepositorio.excluir(produto);
    }

    public Movimentacao registrarEntrada(Produto produto, int quantidade, String observacao)
            throws SQLException, DadosInvalidosException {
        try {
            return movimentar(produto, quantidade, observacao, true);
        } catch (EstoqueInsuficienteException e) {
            throw new SQLException("Saldo persistido inconsistente.", e);
        }
    }

    public Movimentacao registrarSaida(Produto produto, int quantidade, String observacao)
            throws SQLException, DadosInvalidosException, EstoqueInsuficienteException {
        return movimentar(produto, quantidade, observacao, false);
    }

    private Movimentacao movimentar(Produto produto, int quantidade, String observacao, boolean entrada)
            throws SQLException, DadosInvalidosException, EstoqueInsuficienteException {
        validarMovimentacao(produto, quantidade);
        try {
            Movimentacao movimento = Database.emTransacao(Database.getConnection(),
                    () -> gravarMovimento(produto, quantidade, observacao, entrada));
            // A TableView recebe o novo saldo somente depois do commit do SQLite.
            produto.setQuantidade(movimento.getProduto().getQuantidade());
            return movimento;
        } catch (SQLException e) {
            if (e.getCause() instanceof DadosInvalidosException causa) throw causa;
            if (e.getCause() instanceof EstoqueInsuficienteException causa) throw causa;
            throw e;
        }
    }

    private Movimentacao gravarMovimento(Produto produto, int quantidade, String observacao, boolean entrada)
            throws SQLException, DadosInvalidosException, EstoqueInsuficienteException {
        exigirUsuarioLogado();
        Produto atual = produtoRepositorio.buscarPorId(produto.getId());
        if (atual == null) {
            throw new DadosInvalidosException("Produto removido ou inexistente.");
        }

        String tipo;
        int variacao;
        if (entrada) {
            tipo = "ENTRADA";
            variacao = quantidade;
        } else {
            tipo = "SAIDA";
            variacao = -quantidade;
        }
        int novoSaldo = calcularSaldo(atual, variacao);
        atual.setQuantidade(novoSaldo);
        produtoRepositorio.salvar(atual);

        Movimentacao movimento = criarMovimentacao(atual, tipo, quantidade, observacao);
        return movimentacaoRepositorio.salvar(movimento);
    }

    private int calcularSaldo(Produto produto, int variacao)
            throws DadosInvalidosException, EstoqueInsuficienteException {
        long novo = (long) produto.getQuantidade() + variacao;
        if (novo < 0) throw new EstoqueInsuficienteException("Saldo insuficiente para a operacao.");
        if (novo > Integer.MAX_VALUE) throw new DadosInvalidosException("Quantidade excede o limite inteiro.");
        if (!Double.isFinite(produto.getPreco() * novo)) {
            throw new DadosInvalidosException("O valor do produto em estoque excede o limite permitido.");
        }
        return (int) novo;
    }

    public void estornarMovimentacao(Movimentacao selecionada)
            throws SQLException, OperacaoNaoPermitidaException, EstoqueInsuficienteException {
        try {
            Produto atualizado = Database.emTransacao(Database.getConnection(), () -> {
                exigirEstorno();
                Movimentacao registro = selecionada == null ? null
                        : movimentacaoRepositorio.buscarPorId(selecionada.getId());
                if (registro == null) throw new OperacaoNaoPermitidaException("Movimentacao inexistente ou ja estornada.");
                Produto atual = registro.getProduto();
                if (atual == null || registro.getQuantidade() <= 0) throw new SQLException("Historico inconsistente.");
                int variacao = switch (registro.getTipo()) {
                    case "ENTRADA" -> -registro.getQuantidade();
                    case "SAIDA" -> registro.getQuantidade();
                    default -> throw new SQLException("Tipo de movimentacao invalido.");
                };
                atual.setQuantidade(calcularSaldo(atual, variacao));
                produtoRepositorio.salvar(atual);
                movimentacaoRepositorio.excluir(registro);
                return atual;
            });
            Produto exibido = selecionada.getProduto();
            if (exibido != null && exibido.getId() == atualizado.getId()) {
                exibido.setQuantidade(atualizado.getQuantidade());
            }
        } catch (SQLException e) {
            if (e.getCause() instanceof OperacaoNaoPermitidaException causa) throw causa;
            if (e.getCause() instanceof EstoqueInsuficienteException causa) throw causa;
            throw e;
        }
    }

    private Movimentacao criarMovimentacao(Produto produto, String tipo, int quantidade,
            String observacao) {
        String data = LocalDate.now().format(FORMATADOR_DATA);
        String textoObservacao = observacao == null ? "" : observacao.trim();
        return new Movimentacao(produto, tipo, quantidade, data, usuarioLogado, textoObservacao);
    }

    private void validarProduto(String codigo, String nome, Categoria categoria, Fornecedor fornecedor,
            int quantidade, double preco, int estoqueMinimo, String validade)
            throws DadosInvalidosException, SQLException {
        validarTexto(codigo, "Informe o codigo do produto.");
        validarTexto(nome, "Informe o nome do produto.");
        validarTexto(validade, "Informe a validade no formato dd/MM/yyyy.");
        try {
            LocalDate.parse(validade.trim(), FORMATADOR_DATA);
        } catch (DateTimeParseException e) {
            throw new DadosInvalidosException("Informe uma data valida no formato dd/MM/yyyy.");
        }
        if (categoria == null || fornecedor == null || categoriaRepositorio.buscarPorId(categoria.getId()) == null
                || fornecedorRepositorio.buscarPorId(fornecedor.getId()) == null) {
            throw new DadosInvalidosException("Selecione categoria e fornecedor.");
        }
        if (quantidade < 0 || estoqueMinimo < 0 || !Double.isFinite(preco) || preco <= 0 || !Double.isFinite(preco * quantidade)) {
            throw new DadosInvalidosException(
                    "Preco deve ser maior que zero e quantidades nao podem ser negativas.");
        }
    }

    private void validarMovimentacao(Produto produto, int quantidade)
            throws DadosInvalidosException {
        if (produto == null) {
            throw new DadosInvalidosException("Selecione um produto.");
        }
        if (quantidade <= 0) {
            throw new DadosInvalidosException(
                    "A quantidade da movimentacao deve ser maior que zero.");
        }
    }

    private String validarPerfil(String perfil) throws DadosInvalidosException {
        if (perfil == null) {
            throw new DadosInvalidosException("O perfil deve ser GERENTE ou OPERADOR.");
        }
        String perfilPadronizado = perfil.trim().toUpperCase();
        if (!"GERENTE".equals(perfilPadronizado) && !"OPERADOR".equals(perfilPadronizado)) {
            throw new DadosInvalidosException("O perfil deve ser GERENTE ou OPERADOR.");
        }
        return perfilPadronizado;
    }

    private void validarTexto(String texto, String mensagem) throws DadosInvalidosException {
        if (texto == null || texto.isBlank()) {
            throw new DadosInvalidosException(mensagem);
        }
    }

    private void conferirSessao() throws SQLException {
        usuarioLogado = usuarioRepositorio.buscarPorId(idSessao);
        if (usuarioLogado == null) throw new SQLException("Identifique um usuario ativo.");
    }

    private void exigirUsuarioLogado() throws DadosInvalidosException, SQLException {
        usuarioLogado = usuarioRepositorio.buscarPorId(idSessao);
        if (usuarioLogado == null) {
            throw new DadosInvalidosException("Identifique o usuario antes de movimentar o estoque.");
        }
    }

    private void exigirGerente(String mensagem) throws OperacaoNaoPermitidaException, SQLException {
        usuarioLogado = usuarioRepositorio.buscarPorId(idSessao);
        if (usuarioLogado == null || !usuarioLogado.podeGerenciarUsuarios()) {
            throw new OperacaoNaoPermitidaException(mensagem);
        }
    }

    private void exigirEstorno() throws OperacaoNaoPermitidaException, SQLException {
        usuarioLogado = usuarioRepositorio.buscarPorId(idSessao);
        if (usuarioLogado == null || !usuarioLogado.podeEstornarMovimentacao()) {
            throw new OperacaoNaoPermitidaException(
                    "Somente gerente pode estornar movimentacoes.");
        }
    }

    public ArrayList<Categoria> listarCategorias() throws SQLException {
        return categoriaRepositorio.listar();
    }

    public ArrayList<Fornecedor> listarFornecedores() throws SQLException {
        return fornecedorRepositorio.listar();
    }

    public ArrayList<Usuario> listarUsuarios() throws SQLException {
        conferirSessao();
        ArrayList<Usuario> usuarios = usuarioRepositorio.listar();
        for (Usuario usuario : usuarios) usuario.setSenha(null);
        return usuarios;
    }

    public ArrayList<Produto> listarProdutos() throws SQLException {
        return produtoRepositorio.listar();
    }

    public ArrayList<Produto> buscarProdutos(String texto) throws SQLException {
        return produtoRepositorio.buscarPorNome(texto == null ? "" : texto);
    }

    public ArrayList<Produto> listarProdutosEmFalta() throws SQLException {
        return produtoRepositorio.listarEmFalta();
    }

    public ArrayList<Produto> listarProdutosVencidos() throws SQLException {
        return produtoRepositorio.listarVencidos();
    }

    public ArrayList<Movimentacao> listarMovimentacoes() throws SQLException {
        ArrayList<Movimentacao> historico = movimentacaoRepositorio.listar();
        for (Movimentacao item : historico) {
            if (item.getUsuarioResponsavel() != null) item.getUsuarioResponsavel().setSenha(null);
        }
        return historico;
    }

    public double getValorTotalEstoque() throws SQLException {
        return produtoRepositorio.calcularValorTotalEstoque();
    }

    public BigDecimal getValorTotalEstoqueDecimal() throws SQLException {
        return produtoRepositorio.calcularValorTotalEstoqueDecimal();
    }

    public long getUnidadesEstoqueLong() throws SQLException {
        return produtoRepositorio.calcularUnidadesEstoqueLong();
    }

    public int getUnidadesEstoque() throws SQLException {
        return produtoRepositorio.calcularUnidadesEstoque();
    }
}
