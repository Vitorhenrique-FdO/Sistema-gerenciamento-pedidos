package Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Utilitário para leitura e escrita de arquivos csv

//todas as operações são estáticas e usam encoding UTF-8
 
public class ArquivoUtil {

    /**
     * lê todas as linhas de um arquivo CSV.
        ignora linhas em branco.
     */
    public static List<String> lerLinhas(String nomeArquivo) {
        List<String> linhas = new ArrayList<>();
        File arquivo = new File(nomeArquivo);
        if (!arquivo.exists()) {
            return linhas;
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(arquivo), "UTF-8"))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    linhas.add(linha);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo " + nomeArquivo + ": " + e.getMessage());
        }
        return linhas;
    }

    // Escreve uma lista de strings em um arquivo CSV, sobrescrevendo o conteúdo anterior.
     //Cria o diretório pai se não existir.
     
    public static void escreverLinhas(String nomeArquivo, List<String> linhas) {
        File arquivo = new File(nomeArquivo);
        // Garante que o diretório existe
        File diretorio = arquivo.getParentFile();
        if (diretorio != null && !diretorio.exists()) {
            diretorio.mkdirs();
        }

        // salva em arquivo temporário e renomeia
        File temp = new File(nomeArquivo + ".tmp");
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(temp), "UTF-8"))) {
            for (String linha : linhas) {
                bw.write(linha);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo " + nomeArquivo + ": " + e.getMessage());
            return;
        }

        // Renomeia: arquivo antigo -> backup e temp -> definitivo
        if (arquivo.exists()) {
            arquivo.delete();
        }
        temp.renameTo(arquivo);
    }

    // ve se diretório "data/" existe, criando-o se necessário.
     
    public static void garantirDiretorioDados() {
        new File("data").mkdirs();
    }
}