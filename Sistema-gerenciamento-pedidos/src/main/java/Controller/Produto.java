/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.Produto;
import repository.ProdutoRepository;
import java.util.List;

public class Produto {

    private ProdutoRepository repository = new ProdutoRepository();

    public List<Produto> listarProdutos() {
        return repository.listar();
    }

    public Produto consultarProduto(int codProduto) {
        return repository.consultar(codProduto);
    }

    public String incluirProduto(String nome, String precoStr, String estoqueStr) {
        if (nome == null || nome.trim().isEmpty()) {
            return "Nome não pode ser vazio.";
        }
        float preco;
        int estoque;
        try {
            preco = Float.parseFloat(precoStr.replace(",", "."));
        } catch (NumberFormatException e) {
            return "Preço inválido.";
        }
        try {
            estoque = Integer.parseInt(estoqueStr);
        } catch (NumberFormatException e) {
            return "Estoque inválido.";
        }
        if (preco < 0) return "Preço não pode ser negativo.";
        if (estoque < 0) return "Estoque não pode ser negativo.";

        int cod = repository.proximoCodigo();
        Produto p = new Produto(cod, nome.trim(), preco, estoque);
        repository.incluir(p);
        return null;
    }

    public String alterarProduto(int codProduto, String nome, String precoStr, String estoqueStr) {
        Produto p = repository.consultar(codProduto);
        if (p == null) return "Produto não encontrado.";
        if (nome == null || nome.trim().isEmpty()) return "Nome não pode ser vazio.";
        float preco;
        int estoque;
        try {
            preco = Float.parseFloat(precoStr.replace(",", "."));
        } catch (NumberFormatException e) {
            return "Preço inválido.";
        }
        try {
            estoque = Integer.parseInt(estoqueStr);
        } catch (NumberFormatException e) {
            return "Estoque inválido.";
        }
        if (preco < 0) return "Preço não pode ser negativo.";
        if (estoque < 0) return "Estoque não pode ser negativo.";

        p.setNome(nome.trim());
        p.setPreco(preco);
        p.setQtdeProduto(estoque);
        repository.alterar(p);
        return null;
    }

    public String excluirProduto(int codProduto) {
        if (repository.consultar(codProduto) == null) return "Produto não encontrado.";
        repository.excluir(codProduto);
        return null;
    }

    public int proximoCodigo() {
        return repository.proximoCodigo();
    }
}