package Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import repository.ItemRepository;
import repository.PedidoRepository;

//colocar toda a regra de negocio, toda a logica

public class PedidoController {

private final PedidoRepository pedidoRepo = new PedidoRepository();

private final ItemRepository itemRepo = new ItemRepository();

private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

//incluindo novo pedido

public String incluir(int idCliente, String dt_pedido, String dt_entrega) {
    
    // Valida existência do cliente
        repository.ClienteRepository clienteRepo = new repository.ClienteRepository();
        boolean clienteExiste = false;
        for (model.Cliente c : clienteRepo.listar()) {
            if (c.getId_cliente() == idCliente) {
                clienteExiste = true;
                break;
            }
        }
        if (!clienteExiste) {
            return "Cliente " + idCliente + " não encontrado.";
        }

        // Data automática = hoje
        String dtPedido = LocalDate.now().format(FMT);

        // Valida dt_entrega se informada
        if (dtEntrega != null && !dtEntrega.trim().isEmpty()) {
            String erro = validarDatas(dtPedido, dtEntrega.trim());
            if (!erro.isEmpty()) return erro;
            dtEntrega = dtEntrega.trim();
        } else {
            dtEntrega = "";
        }

        int proximoCod = pedidoRepo.proximoCodigo();
        model.Pedido novoPedido = new model.Pedido(proximoCod, idCliente, dtPedido, dtEntrega, 0.0);
        pedidoRepo.incluir(novoPedido);
        return "";
    }

}

//Alterar

 public String alterar(int codPedido, int idCliente, String dtEntrega) {
        
     model.Pedido pedido = pedidoRepo.consultar(codPedido);
        if (pedido == null) {
            return "Pedido " + codPedido + " não encontrado.";
        }

        // Valida existência do novo cliente
        repository.ClienteRepository clienteRepo = new repository.ClienteRepository();
        boolean clienteExiste = false;
        for (model.Cliente c : clienteRepo.listar()) {
            if (c.getId_cliente() == idCliente) {
                clienteExiste = true;
                break;
            }
        }
        if (!clienteExiste) {
            return "Cliente " + idCliente + " não encontrado.";
        }

        // Valida dt_entrega se informada
        String dtEntregaFinal = "";
        if (dtEntrega != null && !dtEntrega.trim().isEmpty()) {
            String erro = validarDatas(pedido.getDtPedido(), dtEntrega.trim());
            if (!erro.isEmpty()) return erro;
            dtEntregaFinal = dtEntrega.trim();
        }

        pedido.setIdCliente(idCliente);
        pedido.setDtEntrega(dtEntregaFinal);
        pedidoRepo.alterar(pedido);
        return "";
    }


//excluir 

public String excluir(int codPedido) {
        model.Pedido pedido = pedidoRepo.consultar(codPedido);
        if (pedido == null) {
            return "Pedido " + codPedido + " não encontrado.";
        }

        // Restaura estoque dos itens antes de excluir
        ProdutoController produtoCtrl = new ProdutoController();
        List<model.Item> itens = itemRepo.listarPorPedido(codPedido);
        for (model.Item item : itens) {
            produtoCtrl.devolverEstoque(item.getCodProduto(), item.getQtdeItens());
        }

        // Remove os itens e o pedido
        itemRepo.excluirTodosDoPedido(codPedido);
        pedidoRepo.excluir(codPedido);
        return "";
    }


//consultar
public model.Pedido consultar(int codPedido) {
        return pedidoRepo.consultar(codPedido);
    }

    /** Busca pedidos por código exato, id de cliente ou data parcial. */
    public List<model.Pedido> buscar(String termo) {
        List<model.Pedido> todos     = pedidoRepo.listar();
        List<model.Pedido> filtrados = new ArrayList<>();
        String t = termo.trim().toLowerCase();
        for (model.Pedido p : todos) {
            if (String.valueOf(p.getCodPedido()).equals(t)
                    || String.valueOf(p.getIdCliente()).equals(t)
                    || p.getDtPedido().contains(termo.trim())
                    || p.getDtEntrega().contains(termo.trim())) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }
    
//listar 

    public List<model.Pedido> listar() {
        return pedidoRepo.listar();
    }
    
//calcular total

public void recalcularTotal(int codPedido) {
        model.Pedido pedido = pedidoRepo.consultar(codPedido);
        if (pedido == null) return;

        double total = 0.0;
        for (model.Item item : itemRepo.listarPorPedido(codPedido)) {
            total += item.getPrecoTotal();
        }

        pedido.setVlrTotal(total);
        pedidoRepo.alterar(pedido);
    
}



