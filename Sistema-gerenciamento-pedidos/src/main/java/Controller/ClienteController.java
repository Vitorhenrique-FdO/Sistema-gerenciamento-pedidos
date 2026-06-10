/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Controller;
import java.util.List;
import model.Cliente;
import repository.ClienteRepository;
import java.util.ArrayList;

public class ClienteController {
    private ClienteRepository repo = new ClienteRepository();

    public List<Cliente> buscarTodos() { return repo.listar(); }

    public void salvar(int id, String nome) { //verificacao de exitencia do ID, se existe edita e senão add novo
        List<Cliente> lista = repo.listar();
        //lista.add(new Cliente(id, nome));
        boolean encontrado = false;
        
        for (Cliente c :lista){
            if(c.getId_cliente()==id){
                c.setNome(nome); //se ID existe muda o nome
                encontrado = true;
                break;
            }
        }
        
        if (!encontrado){
            lista.add(new Cliente(id, nome)); //se nao exite, add novo
        }
        repo.salvar(lista);
    }
    
    public void excluir(int id){
        List<Cliente> lista = repo.listar();
        lista.removeIf(c -> c.getId_cliente()==id);
        repo.salvar(lista);
    }
    
    public List<Cliente> buscar(String termo){
        List<Cliente> todos = repo.listar();
        List<Cliente> filtrados = new ArrayList<>();
        for(Cliente c : todos){
            if (String.valueOf(c.getId_cliente()).equals(termo) || c.getNome().toLowerCase().contains(termo.toLowerCase())){
                filtrados.add(c);
            }
        }
        return filtrados;
    }
    
    public int gerarProximoId() { //descobre o maior ID da lista pra gerar o prox
        List<Cliente> lista = repo.listar();
        if (lista.isEmpty()) return 1;
        
        int maxId=0;
        for (Cliente c:lista){
            if(c.getId_cliente() > maxId){
                maxId = c.getId_cliente();
            }
        }
      return maxId +1;
    }
    
}
