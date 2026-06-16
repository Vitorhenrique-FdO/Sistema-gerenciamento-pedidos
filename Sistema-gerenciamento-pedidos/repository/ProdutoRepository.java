
package repository;

/**
 *
 * @author vitor
 */

import model.Produto;
import util.ArquivoUtil;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepository {
    
    private static final String ARQUIVO = "data/produtos.csv";

    
    public List<Produto> listar() {
        List<Produto> lista = new ArrayList<>();
        for (String linha : ArquivoUtil.lerLinhas(ARQUIVO)) {
            Produto p = linhaParaProduto(linha);
            if (p != null) {
                lista.add(p);
            }
        }
        return lista;
    }

    public Produto consultar(int codProduto) {
        for (Produto p : listar()) {
            if (p.getCodProduto() == codProduto) {
                return p;
            }
        }
        return null;
    }

    public void incluir(Produto produto) {
        List<Produto> lista = listar();
        lista.add(produto);
        salvarTodos(lista);
    }

    public void alterar(Produto produtoAlterado) {
        List<Produto> lista = listar();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getCodProduto() == produtoAlterado.getCodProduto()) {
                lista.set(i, produtoAlterado);
                break;
            }
        }
        salvarTodos(lista);
    }

    public void excluir(int codProduto) {
        List<Produto> lista = listar();
        lista.removeIf(p -> p.getCodProduto() == codProduto);
        salvarTodos(lista);
    }

    public boolean existe(int codProduto) {
        return consultar(codProduto) != null;
    }

    public int proximoCodigo() {
        List<Produto> lista = listar();
        if (lista.isEmpty()) return 1;
        int maior = 0;
        for (Produto p : lista) {
            if (p.getCodProduto() > maior) {
                maior = p.getCodProduto();
            }
        }
        return maior + 1;
    }

    public void deduzirEstoque(int codProduto, int quantidade) {
        Produto p = consultar(codProduto);
        if (p != null) {
            p.setQtdeProduto(p.getQtdeProduto() - quantidade);
            alterar(p);
        }
    }

    public void devolverEstoque(int codProduto, int quantidade) {
        Produto p = consultar(codProduto);
        if (p != null) {
            p.setQtdeProduto(p.getQtdeProduto() + quantidade);
            alterar(p);
        }
    }

    private Produto linhaParaProduto(String linha) {
        try {
            String[] campos    = linha.split(";");
            int    codProduto  = Integer.parseInt(campos[0].trim());
            String nome        = campos[1].trim();
            float  preco       = Float.parseFloat(campos[2].trim());  // float igual ao Model
            int    qtdeProduto = Integer.parseInt(campos[3].trim());
            return new Produto(codProduto, nome, preco, qtdeProduto);
        } catch (Exception e) {
            System.err.println("Linha inválida ignorada no CSV de produtos: " + linha);
            return null;
        }
    }

    private void salvarTodos(List<Produto> lista) {
        List<String> linhas = new ArrayList<>();
        for (Produto p : lista) {
            linhas.add(p.toCSV());
        }
        ArquivoUtil.escreverLinhas(ARQUIVO, linhas);
    }
}
