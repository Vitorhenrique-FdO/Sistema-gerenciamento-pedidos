/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package repository;
import java.io.*;
import java.util.*;
import model.Cliente;

public class ClienteRepository {
    private final String PATH = "data/clientes.csv";
    public ClienteRepository() { new File("data").mkdir(); } //cria a pasta "data" se não existir

    public List<Cliente> listar() { //transformar o texto->lista de objetos p java entender
        List<Cliente> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] d = linha.split(";");
                lista.add(new Cliente(Integer.parseInt(d[0]), d[1]));
            }
        } catch (Exception e) {}
        return lista;
    }

    public void salvar(List<Cliente> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PATH))) {
            for (Cliente c : lista) pw.println(c.toString());
        } catch (Exception e) {}
    }
}
