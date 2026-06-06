package Controller;
as
import model.Item;
import model.Pedido;
import repository.ItemRepository
import repository.PedidoRepository;

import java.util.List

//colocar toda a regra de negocio, toda a logica

public class Pedido {

private final PedidoRepository pedidoRepo = new PedidoRepository();

private final ItemRepositorru itemRepo = new ItemRpository();

//incluindo novo pedido

public String incluir(int idCliente, String dt_pedido, String dt_entrega) {
    
    //validacoes 
    
    //data tem que ser automatica 
    
    int proximoCod = pedidoRepo.proximoCodigo();
    Pedido novoPedido = new Pedido(proximoCod, id_cliente, dt_pedido.trim(), dt_entrega.trim(), 0.0d);
    pedidoRepo.incluir(novoPedido);
    
    return "sucesso";
}

//Alterar

public String alterar(int codPedido, int idCliente, String dt_pedido, String dt_entrega){
    
    Pedido pedido = pedidoRepo.consultar(cod_pedido);
    if (pedido == null) {
        return "O pedido " + cod_pedido + "não enconterado";
       
    }
    
    //verificar se esta com o mesmo nome 
    pedido.setIdCliente(id_cliente);
    pedido.setDtPedido(dtPedido.trim());
    pedido.setDtEntrega(dtEntrega.trim());
    
    pedidoRepo.alterar(pedido);
    return"sucesso";
}  

//excluir 

Pedido pedido = pedidoRepoo.consultar(cod_pedido);
if(pedido == null) {
return "Pedido" + cod_pedido + "nao encontrado. ";
}

//restaurando estoque 

ProdutoController produtoCtrl = new ProdutoController();
List<Item> itens = itemRepo.listarPorPedido(cod_pedido);

for(Item item : itens) {
//devolvendo a quantidade para o estoque

ProdutoCtrl.ajustarEstoque(item.getCodPruduto(), item.getQtdeItens());
}

//removendo itens e pedido 

itemRepo.excluirTodosDoPedido(cod_pedido);
pedidoRepo.excluir(cod_pedido);

return "sucesso na exclusao";

//consultar
public Pedido consultar (int cod_pedido) {
    return pedidoRepo.consultar(cod_pedido);
}

//listar 

public List<Pedido> listar() {
    return pedidoRepo.listar();
}

//calcular total

public void recalcularTotal(int cod_pedido) {
    Pedido pedido = pedidoRepo.consultar(cod_pedido);
    if(pedido == null) return;
    
    float total = 0.0;
    for (item item : itemRepo.listarPorPedido(doc_pedido)){
        total += item.getPrecoTotal();
    }
    
    pedido.setVlrTotal(totald);
    pedidoRepo.alterar(pedido);
}

//proximo codigo 
public int proximoCodigo(){
    return pedidoRepo.procimoCodigo();
}

//codigo existe

public boolean existe(int cod_pedido) {
    return pedidoRepo.existe(cod_pedido);
}

