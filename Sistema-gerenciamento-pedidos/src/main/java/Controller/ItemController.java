package Controller;

import model.Item;
import model.Produto;
import repository.ItemRepository;
import java.util.List;

// cuida de toda a logica dos itens do pedido (estoque, calcular valor)
public class ItemController {

    private final ItemRepository   itemRepo    = new ItemRepository();
    private final PedidoController pedidoCtrl  = new PedidoController();
    private final ProdutoController produtoCtrl = new ProdutoController();

    // joga um item pra dentro do pedido
    public String incluir(int codPedido, int codProduto, int qtdeItens) {

        // pedido nao existe, barra
        if (!pedidoCtrl.existe(codPedido)) {
            return "O pedido " + codPedido + " nao existe.";
        }

        // produto nao existe, barra
        Produto produto = produtoCtrl.consultar(codProduto);
        if (produto == null) {
            return "Produto " + codProduto + " nao achado no sistema.";
        }

        // qtd negativa ou 0 nao tem como
        if (qtdeItens <= 0) {
            return "Coloca uma quantidade direito (maior que 0).";
        }

        // confere se tem estoque
        if (!produtoCtrl.temEstoque(codProduto, qtdeItens)) {
            return "Ixi, faltou estoque. So tem: " + produto.getQtde_produto()
                 + " | Vc pediu: " + qtdeItens;
        }

        // gera o numero da sequencia do item na lista
        int seqItem = itemRepo.proximaSequencia(codPedido);

        // a gente tranca o preco na hora que vende (se subir amanha, nao altera aqui)
        double precoUni = produto.getPreco();

        // monta o item e salva (o preco total ele calcula sozinho la dentro)
        Item novoItem = new Item(codPedido, seqItem, codProduto, qtdeItens, precoUni);
        itemRepo.incluir(novoItem);

        // tira do estoque pq a gente vendeu
        produtoCtrl.deduzirEstoque(codProduto, qtdeItens);

        // atualiza o vlr_total do pedido 
        pedidoCtrl.recalcularTotal(codPedido);

        return "";
    }

    // quando for mudar a qtd de um item que ja ta la
    public String alterarQuantidade(int codPedido, int seqItem, int novaQtde) {

        Item item = itemRepo.consultar(codPedido, seqItem);
        if (item == null) {
            return "Esse item ai sumiu (pedido=" + codPedido + ", seq=" + seqItem + ").";
        }

        if (novaQtde <= 0) {
            return "Quantidade tem que ser mais que zero ne.";
        }

        int qtdeAntiga = item.getQtde_itens();
        // ve a diferenca. ex: tinha 2, quer 5 = precisa de 3. / tinha 5 quer 2 = sobrou 3.
        int diferenca  = novaQtde - qtdeAntiga;

        // se quer mais, tem que ver se sobrou estoque
        if (diferenca > 0 && !produtoCtrl.temEstoque(item.getCod_produto(), diferenca)) {
            Produto p = produtoCtrl.consultar(item.getCod_produto());
            int disponivel = (p != null) ? p.getQtde_produto() : 0;
            return "Sem estoque pra isso. Tem " + disponivel
                 + " | Vc quer colocar mais " + diferenca;
        }

        // atualiza (ele calcula o total na hora)
        item.setQtde_itens(novaQtde);
        itemRepo.alterar(item);

        // mexe no estoque usando a diferenca
        produtoCtrl.ajustarEstoque(item.getCod_produto(), -diferenca);

        // atualiza o total da compra dnv
        pedidoCtrl.recalcularTotal(codPedido);

        return "";
    }

    // tira o item da compra
    public String excluir(int codPedido, int seqItem) {

        Item item = itemRepo.consultar(codPedido, seqItem);
        if (item == null) {
            return "Item (pedido=" + codPedido + ", seq=" + seqItem + ") nao achado.";
        }

        // volta pro estoque antes de apagar, senao a gente perde a mercadoria
        produtoCtrl.devolverEstoque(item.getCod_produto(), item.getQtde_itens());

        // deleta
        itemRepo.excluir(codPedido, seqItem);

        // reculcula o pedido dnv
        pedidoCtrl.recalcularTotal(codPedido);

        return "";
    }

    // atalhos pra view usar
    public model.Item consultar(int codPedido, int seqItem) {
        return itemRepo.consultar(codPedido, seqItem);
    }

    public List<model.Item> listarPorPedido(int codPedido) {
        return itemRepo.listarPorPedido(codPedido);
    }

    public List<model.Item> listar() {
        return itemRepo.listar();
    }
}