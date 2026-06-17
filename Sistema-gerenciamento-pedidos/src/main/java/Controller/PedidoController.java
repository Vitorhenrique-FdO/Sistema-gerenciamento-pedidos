package Controller;

import Util.ArquivoUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import repository.ItemRepository;
import repository.PedidoRepository;

// faz as validacoes do pedido antes de salvar
public class PedidoController {

    private final PedidoRepository pedidoRepo = new PedidoRepository();
    private final ItemRepository   itemRepo   = new ItemRepository();

    // padrao da data
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // cadastra pedido
    public String incluir(int idCliente, String dtEntrega) {

        // ve se o cliente existe de verdade
        repository.ClienteRepository clienteRepo = new repository.ClienteRepository();
        boolean clienteExiste = false;
        for (model.Cliente c : clienteRepo.listar()) {
            if (c.getId_cliente() == idCliente) {
                clienteExiste = true;
                break;
            }
        }
        if (!clienteExiste) {
            return "Putz, cliente " + idCliente + " nao existe.";
        }

        // data de hoje automatica
        String dtPedido = LocalDate.now().format(FMT);

        // checa a data de entrega se o cara digitou algo dasd
        String dtEntregaFinal = "";
        if (dtEntrega != null && !dtEntrega.trim().isEmpty()) {
            String erro = validarDatas(dtPedido, dtEntrega.trim());
            if (!erro.isEmpty()) return erro;
            dtEntregaFinal = dtEntrega.trim();
        }

        // acha o proximo id e salva
        int proximoCod = pedidoRepo.proximoCodigo();
        model.Pedido novoPedido = new model.Pedido(proximoCod, idCliente, dtPedido, dtEntregaFinal, 0.0);
        pedidoRepo.incluir(novoPedido);
        return ""; // se voltar vazio eh pq deu certo
    }

    // editar o pedido
    public String alterar(int codPedido, int idCliente, String dtEntrega) {

        // pedido tem que existir
        model.Pedido pedido = pedidoRepo.consultar(codPedido);
        if (pedido == null) {
            return "Nao achei o pedido " + codPedido + ".";
        }

        // o novo cliente tem que existir tbm
        repository.ClienteRepository clienteRepo = new repository.ClienteRepository();
        boolean clienteExiste = false;
        for (model.Cliente c : clienteRepo.listar()) {
            if (c.getId_cliente() == idCliente) {
                clienteExiste = true;
                break;
            }
        }
        if (!clienteExiste) {
            return "Esse cliente " + idCliente + " nao ta cadastrado.";
        }

        // checando data
        String dtEntregaFinal = "";
        if (dtEntrega != null && !dtEntrega.trim().isEmpty()) {
            String erro = validarDatas(pedido.getDt_pedido(), dtEntrega.trim());
            if (!erro.isEmpty()) return erro;
            dtEntregaFinal = dtEntrega.trim();
        }

        // salva as alteracoes
        pedido.setId_cliente(idCliente);
        pedido.setDt_entrega(dtEntregaFinal);
        pedidoRepo.alterar(pedido);
        return "";
    }

    // apagar o pedido (e volta o estoque dos itens)
    public String excluir(int codPedido) {

        model.Pedido pedido = pedidoRepo.consultar(codPedido);
        if (pedido == null) {
            return "Pedido " + codPedido + " nao achado.";
        }

        // devolve o estoque dos itens antes de jogar tudo fora
        ProdutoController produtoCtrl = new ProdutoController();
        List<model.Item> itens = itemRepo.listarPorPedido(codPedido);
        for (model.Item item : itens) {
            produtoCtrl.devolverEstoque(item.getCod_produto(), item.getQtde_itens());
        }

        // apaga itens e depois o pedido
        itemRepo.excluirTodosDoPedido(codPedido);
        pedidoRepo.excluir(codPedido);
        return "";
    }

    // busca direta
    public model.Pedido consultar(int codPedido) {
        return pedidoRepo.consultar(codPedido);
    }

    public boolean existe(int codPedido) {
        return pedidoRepo.existe(codPedido);
    }

    public List<model.Pedido> listar() {
        return pedidoRepo.listar();
    }

    // barra de pesquisa (pesquisa por qualquer coisa)
    public List<model.Pedido> buscar(String termo) {
        List<model.Pedido> todos     = pedidoRepo.listar();
        List<model.Pedido> filtrados = new ArrayList<>();
        String t = termo.trim().toLowerCase();
        for (model.Pedido p : todos) {
            if (String.valueOf(p.getCod_pedido()).equals(t)
                    || String.valueOf(p.getId_cliente()).equals(t)
                    || p.getDt_pedido().contains(termo.trim())
                    || p.getDt_entrega().contains(termo.trim())) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    // soma todos os itens e atualiza o total (chamo isso toda hora que mecho num item)
    public void recalcularTotal(int codPedido) {
        model.Pedido pedido = pedidoRepo.consultar(codPedido);
        if (pedido == null) return;

        double total = 0.0;
        for (model.Item item : itemRepo.listarPorPedido(codPedido)) {
            total += item.getPreco_total();
        }

        pedido.setVlr_total(total);
        pedidoRepo.alterar(pedido);
    }

    // so pra ver se as datas fazem sentido
    private String validarDatas(String dtPedido, String dtEntrega) {
        try {
            LocalDate dataPedido  = LocalDate.parse(dtPedido,  FMT);
            LocalDate dataEntrega = LocalDate.parse(dtEntrega, FMT);
            if (dataEntrega.isBefore(dataPedido)) {
                return "Data de entrega nao pode ser antes do pedido (" + dtPedido + ").";
            }
            return ""; 
        } catch (DateTimeParseException e) {
            return "A data ta zoada. Usa dd/MM/yyyy.";
        }
    }
}
