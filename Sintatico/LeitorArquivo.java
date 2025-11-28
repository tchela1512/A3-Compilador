package Sintatico;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LeitorArquivo {
    private String nomeArquivo;

    public LeitorArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

     // le todas as linhas do arquivo e retorna como uma lista
    
    public List<String> lerLinhas() throws IOException {
        List<String> linhas = new ArrayList<>();
        File file = new File(nomeArquivo);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String linha;

        while ((linha = br.readLine()) != null) {
            if (!linha.trim().isEmpty()) {
                linhas.add(linha);
            }
        }

        br.close();
        return linhas;
    }

    
     //lê e retorna todas as linhas do arquivo como uma única string
     
    public String lerConteudoCompleto() throws IOException {
        StringBuilder conteudo = new StringBuilder();
        File file = new File(nomeArquivo);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String linha;

        while ((linha = br.readLine()) != null) {
            conteudo.append(linha).append("\n");
        }

        br.close();
        return conteudo.toString();
    }

   // Verifica se o arquivo existe
    
    public boolean arquivoExiste() {
        File file = new File(nomeArquivo);
        return file.exists();
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }
}
