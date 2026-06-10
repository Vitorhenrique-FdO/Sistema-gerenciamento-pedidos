package Controller;

import model.Item;
import model.Produto;
import repository.ItemRepository;

import java.util.List;

public class Item {

    private final ItemRepository   itemRepo    = new ItemRepository();
    private final PedidoController pedidoCtrl  = new PedidoController();
    private final ProdutoController produtoCtrl = new ProdutoController();

    // incluir
   
    public String incluir(int codPedido, int codProduto, int qtdeItens) {

        // pedido existe?
        if (!pedidoCtrl.existe(codPedido)) {
            return "Pedido " + codPedido + " não encontrado.";
        }

        // produto existe?
        Produto produto = produtoCtrl.consultar(codProduto);
        if (produto == null) {
            return "Produto " + codProduto + " não encontrado.";
        }

        // quantidade deve ser positiva
        if (qtdeItens <= 0) {
            return "Quantidade deve ser maior que zero.";
        }

        // estoque suficiente?
        if (!produtoCtrl.temEstoque(codProduto, qtdeItens)) {
            return "Estoque insuficiente. Disponível: " + produto.getQtdeProduto()
                 + " | Solicitado: " + qtdeItens;
        }

        // determina a proxima sequencia                                                            
        int seqItem = itemRepo.proximaSequencia(codPedido);

        // preço unitário = preço do produto AGORA 
        double precoUni = produto.getPreco();

        // cria o item (preco_total calculado no construtor)
        Item novoItem = new Item(codPedido, seqItem, codProduto, qtdeItens, precoUni);

        // salva o item
        itemRepo.incluir(novoItem);

        // deduz do estoque
        produtoCtrl.deduzirEstoque(codProduto, qtdeItens);

        // recalcula o total do pedido
        pedidoCtrl.recalcularTotal(codPedido);

        return ""; // sucesso
    }

    //ALTERAR QUANTIDADE 
   
    public String alterarQuantidade(int codPedido, int seqItem, int novaQtde) {

        // item deve existir
        Item item = itemRepo.consultar(codPedido, seqItem);
        if (item == null) {
            return "Item (pedido=" + codPedido + ", seq=" + seqItem + ") não encontrado.";
        }

        // quantidade deve ser positiva
        if (novaQtde <= 0) {
            return "Quantidade deve ser maior que zero.";
        }

        int qtdeAntiga = item.getQtdeItens();
        int diferenca  = novaQtde - qtdeAntiga; // positivo = precisa mais, negativo = vai devolver

        // se vai pedir mais, verifica se tem estoque para a diferença
        if (diferenca > 0 && !produtoCtrl.temEstoque(item.getCodProduto(), diferenca)) {
            Produto p = produtoCtrl.consultar(item.getCodProduto());
            int disponivel = (p != null) ? p.getQtdeProduto() : 0;
            return "Estoque insuficiente para o acréscimo. Disponível: " + disponivel
                 + " | Acréscimo solicitado: " + diferenca;
        }

        // atualiza a quantidade no item (preco_total recalculado pelo setter)
        item.setQtdeItens(novaQtde);
        itemRepo.alterar(item);

        // ajusta o estoque pela diferença
        //    diferenca positivo  precisa deduzir mais
        //    diferenca negativo  devolve ao estoque
        produtoCtrl.ajustarEstoque(item.getCodProduto(), -diferenca);

       //calculo do total

        return "";
    }

    // EXCLUIR 
    
    public String excluir(int codPedido, int seqItem) {

        Item item = itemRepo.consultar(codPedido, seqItem);
        if (item == null) {
            return "Item (pedido=" + codPedido + ", seq=" + seqItem + ") não encontrado.";
        }

        // devolve a quantidade ao estoque antes de excluir
        produtoCtrl.devolverEstoque(item.getCodProduto(), item.getQtdeItens());

        // Remove o item
        itemRepo.excluir(codPedido, seqItem);

        // Recalcula o total do pedido
        pedidoCtrl.recalcularTotal(codPedido);

        return "";
    }

    // CONSULTAR 
    public Item consultar(int codPedido, int seqItem) {
        return itemRepo.consultar(codPedido, seqItem);
    }

    //  LISTAR ITENS DE UM PEDIDO 
    public List<Item> listarPorPedido(int codPedido) {
        return itemRepo.listarPorPedido(codPedido);
    }
}