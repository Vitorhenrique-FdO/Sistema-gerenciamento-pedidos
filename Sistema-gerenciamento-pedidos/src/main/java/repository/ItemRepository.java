package repository;

import Util.ArquivoUtil;
import model.Item;
import java.util.ArrayList;
import java.util.List;

// le e salva os itens no csv
public class ItemRepository {

    private static final String ARQUIVO = "data/itens.csv";

    public ItemRepository() {
        // garante que a pasta data ta criada
        ArquivoUtil.garantirDiretorioDados();
    }

    // pega todos os itens salvos
    public List<Item> listar() {
        List<Item> itens = new ArrayList<>();
        for (String linha : ArquivoUtil.lerLinhas(ARQUIVO)) {
            Item item = linhaParaItem(linha);
            if (item != null) {
                itens.add(item);
            }
        }
        return itens;
    }

    // filtra so os itens de um pedido especifico
    public List<Item> listarPorPedido(int codPedido) {
        List<Item> resultado = new ArrayList<>();
        for (Item item : listar()) {
            if (item.getCod_pedido() == codPedido) {
                resultado.add(item);
            }
        }
        return resultado;
    }

    // acha o item usando o codigo do pedido e a sequencia dele
    public Item consultar(int codPedido, int seqItem) {
        for (Item item : listar()) {
            if (item.getCod_pedido() == codPedido && item.getSeq_item() == seqItem) {
                return item;
            }
        }
        return null; // nao achou
    }

    // salva um item novo
    public void incluir(Item item) {
        List<Item> itens = listar();
        itens.add(item);
        salvarTodos(itens);
    }

    // atualiza o item (ex: se mudou a quantidade)
    public void alterar(Item itemAlterado) {
        List<Item> itens = listar();
        for (int i = 0; i < itens.size(); i++) {
            Item atual = itens.get(i);
            // procura pela chave composta (pedido + sequencia)
            if (atual.getCod_pedido() == itemAlterado.getCod_pedido()
                    && atual.getSeq_item() == itemAlterado.getSeq_item()) {
                itens.set(i, itemAlterado);
                break;
            }
        }
        salvarTodos(itens);
    }

    // deleta so um item especifico
    public void excluir(int codPedido, int seqItem) {
        List<Item> itens = listar();
        itens.removeIf(item -> item.getCod_pedido() == codPedido
                            && item.getSeq_item()   == seqItem);
        salvarTodos(itens);
    }

    // limpa todos os itens de um pedido (quando o pedido eh excluido)
    public void excluirTodosDoPedido(int codPedido) {
        List<Item> itens = listar();
        itens.removeIf(item -> item.getCod_pedido() == codPedido);
        salvarTodos(itens);
    }

    // calcula qual vai ser o numero de sequencia do proximo item do pedido
    public int proximaSequencia(int codPedido) {
        int maior = 0;
        for (Item item : listarPorPedido(codPedido)) {
            if (item.getSeq_item() > maior) {
                maior = item.getSeq_item();
            }
        }
        return maior + 1; // o proximo eh sempre o maior mais um
    }

    // ve se o produto ta sendo usado em algum lugar (pra nao deixar excluir o produto a toa)
    public boolean existeItemDoProduto(int codProduto) {
        for (Item item : listar()) {
            if (item.getCod_produto() == codProduto) {
                return true;
            }
        }
        return false;
    }

    // transforma a linha do csv no item de volta
    private Item linhaParaItem(String linha) {
        try {
            String[] campos = linha.split(";");
            int    codPedido  = Integer.parseInt(campos[0].trim());
            int    seqItem    = Integer.parseInt(campos[1].trim());
            int    codProduto = Integer.parseInt(campos[2].trim());
            int    qtde       = Integer.parseInt(campos[3].trim());
            double precoUni   = Double.parseDouble(campos[4].trim());
            double precoTotal = Double.parseDouble(campos[5].trim());
            return new Item(codPedido, seqItem, codProduto, qtde, precoUni, precoTotal);
        } catch (Exception e) {
            System.err.println("ignorando linha quebrada no csv de itens: " + linha);
            return null;
        }
    }

    // salva todos de volta no arquivo
    private void salvarTodos(List<Item> itens) {
        List<String> linhas = new ArrayList<>();
        for (Item item : itens) {
            linhas.add(item.toCSV());
        }
        ArquivoUtil.escreverLinhas(ARQUIVO, linhas);
    }
}
