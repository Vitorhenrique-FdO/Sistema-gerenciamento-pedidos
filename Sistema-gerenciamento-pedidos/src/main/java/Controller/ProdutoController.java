package Controller;

import model.Produto;
import repository.ProdutoRepository;
import java.util.ArrayList;
import java.util.List;

// faz as validacoes do produto e do estoque dele
public class ProdutoController {

    private final ProdutoRepository produtoRepo = new ProdutoRepository();

    // cria produto novo
    public String incluir(String nome, double preco, int qtde) {

        if (nome == null || nome.trim().isEmpty()) {
            return "Voce tem que dar um nome pro produto.";
        }
        if (preco < 0) {
            return "O preco nao pode ser negativo.";
        }
        if (qtde < 0) {
            return "Nao da pra ter quantidade negativa ne.";
        }

        // gera id automatico e salva
        int cod = produtoRepo.proximoCodigo();
        Produto novo = new Produto(cod, nome.trim(), preco, qtde);
        produtoRepo.incluir(novo);
        return "";
    }

    // salva edicoes do produto
    public String alterar(int codProduto, String nome, double preco, int qtde) {

        Produto produto = produtoRepo.consultar(codProduto);
        if (produto == null) {
            return "Nao tem produto com o cod " + codProduto;
        }
        if (preco < 0) {
            return "Preco negativo? Ai o patrao chora.";
        }
        if (qtde < 0) {
            return "A quantidade nao pode ser negativa.";
        }

        produto.setNome(nome.trim());
        produto.setPreco(preco);
        produto.setQtde_produto(qtde);
        produtoRepo.alterar(produto);
        return "";
    }

    // apaga
    public String excluir(int codProduto) {

        if (produtoRepo.consultar(codProduto) == null) {
            return "Esse produto ai nem existe " + codProduto;
        }

        // ve se alguem ja comprou o produto, se sim, trava a exclusao senao buga o sistema
        repository.ItemRepository itemRepo = new repository.ItemRepository();
        if (itemRepo.existeItemDoProduto(codProduto)) {
            return "Nao da pra apagar: tem pedido usando esse produto.";
        }

        produtoRepo.excluir(codProduto);
        return "";
    }

    // busca direta
    public Produto consultar(int codProduto) {
        return produtoRepo.consultar(codProduto);
    }

    // traz a lista toda
    public List<Produto> listar() {
        return produtoRepo.listar();
    }

    // barra de pesquisa
    public List<Produto> buscar(String termo) {
        List<Produto> todos     = produtoRepo.listar();
        List<Produto> filtrados = new ArrayList<>();
        String t = termo.trim().toLowerCase();
        for (Produto p : todos) {
            // compara se contem na string
            if (String.valueOf(p.getCod_produto()).equals(t)
                    || p.getNome().toLowerCase().contains(t)) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    // metodos pro ItemController brincar de colocar e tirar coisa do estoque:
    
    // confere se vai dar boa a venda
    public boolean temEstoque(int codProduto, int qtde) {
        Produto p = produtoRepo.consultar(codProduto);
        return p != null && p.getQtde_produto() >= qtde;
    }

    // tira do estoque
    public void deduzirEstoque(int codProduto, int qtde) {
        ajustarEstoque(codProduto, qtde); 
    }

    // devolve pro estoque
    public void devolverEstoque(int codProduto, int qtde) {
        ajustarEstoque(codProduto, -qtde); 
    }

    // metodo q faz a matematica do estoque 
    public void ajustarEstoque(int codProduto, int delta) {
        Produto p = produtoRepo.consultar(codProduto);
        if (p == null) return;
        // delta positivo significa que tirou (vendeu), delta negativo eh que devolveu
        int novaQtde = p.getQtde_produto() - delta; 
        if (novaQtde < 0) novaQtde = 0; // protecao extra pra nao ficar negativo
        p.setQtde_produto(novaQtde);
        produtoRepo.alterar(p);
    }
}
