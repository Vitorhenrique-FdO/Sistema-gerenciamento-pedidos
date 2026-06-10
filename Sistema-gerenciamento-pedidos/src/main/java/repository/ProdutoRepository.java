package repository;

import Util.ArquivoUtil;
import model.Produto;
import java.util.ArrayList;
import java.util.List;

// le e salva os produtos no txt/csv
public class ProdutoRepository {

    private static final String ARQUIVO = "data/produtos.csv";

    public ProdutoRepository() {
        ArquivoUtil.garantirDiretorioDados();
    }

    // traz a lista toda
    public List<Produto> listar() {
        List<Produto> produtos = new ArrayList<>();
        for (String linha : ArquivoUtil.lerLinhas(ARQUIVO)) {
            Produto p = Produto.fromCSV(linha);
            if (p != null) {
                produtos.add(p);
            }
        }
        return produtos;
    }

    // procura o produto pelo codigo
    public Produto consultar(int codProduto) {
        for (Produto p : listar()) {
            if (p.getCod_produto() == codProduto) {
                return p;
            }
        }
        return null;
    }

    // cria produto novo
    public void incluir(Produto produto) {
        List<Produto> produtos = listar();
        produtos.add(produto);
        salvarTodos(produtos);
    }

    // altera produto existente
    public void alterar(Produto produtoAlterado) {
        List<Produto> produtos = listar();
        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i).getCod_produto() == produtoAlterado.getCod_produto()) {
                produtos.set(i, produtoAlterado);
                break;
            }
        }
        salvarTodos(produtos);
    }

    // deleta o produto
    public void excluir(int codProduto) {
        List<Produto> produtos = listar();
        produtos.removeIf(p -> p.getCod_produto() == codProduto);
        salvarTodos(produtos);
    }

    // gera o proximo codigo
    public int proximoCodigo() {
        List<Produto> produtos = listar();
        if (produtos.isEmpty()) return 1;
        int maior = 0;
        for (Produto p : produtos) {
            if (p.getCod_produto() > maior) {
                maior = p.getCod_produto();
            }
        }
        return maior + 1;
    }

    // ve se o produto ta cadastrado
    public boolean existe(int codProduto) {
        return consultar(codProduto) != null;
    }

    // salva no arquivo
    private void salvarTodos(List<Produto> produtos) {
        List<String> linhas = new ArrayList<>();
        for (Produto p : produtos) {
            linhas.add(p.toCSV());
        }
        ArquivoUtil.escreverLinhas(ARQUIVO, linhas);
    }
}
