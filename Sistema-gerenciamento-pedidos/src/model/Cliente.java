/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



package model;

public class Cliente {
  
    private int id_cliente;
    private String nome;

 
    public Cliente(int id_cliente, String nome) {
        this.id_cliente = id_cliente;
        this.nome = nome;
    }

    //get e set pra permitir a mudança de nome/id
    public int getId_cliente() {
        return id_cliente;
    }
    public void setId_cliente(int id_cliente) { this.id_cliente=id_cliente; }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) { this.nome= nome; }

    //ajuda no save do CSV
    @Override
    public String toString() {
        return id_cliente + ";" + nome;
    }
}


