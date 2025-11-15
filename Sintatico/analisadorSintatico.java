package Sintatico;

import java.io.IOException;
import java.util.List;
import Analisar.*;

public class analisadorSintatico {
    private LeitorArquivo leitorArquivo;

    public analisadorSintatico(String nomeArquivo) {
        this.leitorArquivo = new LeitorArquivo(nomeArquivo);
    }

    /**
     * Método principal para analisar o arquivo
     */
    public void analisar() {
        try {
            // Verifica se o arquivo existe
            if (!leitorArquivo.arquivoExiste()) {
                System.err.println("Erro: Arquivo '" + leitorArquivo.getNomeArquivo() + "' não encontrado!");
                return;
            }

            // Lê todas as linhas do arquivo
            List<String> linhas = leitorArquivo.lerLinhas();

            // Processa cada linha
            for (String linha : linhas) {
                System.out.println("\n Analisando linha: " + linha);

                // Tokeniza a linha usando o Lexer
                Lexer lexer = new Lexer(linha);
                List<Token> tokens = lexer.tokenize();

                // Verifica a estrutura sintática
                VerificadorEstruturas verificador = new VerificadorEstruturas(tokens);
                try {
                    if (verificador.verificarEstrutura()) {
                        System.out.println("Análise sintática: SUCESSO");
                    } else {
                        System.out.println(" Estrutura não reconhecida ou incompleta");
                    }
                } catch (Exception e) {
                    System.out.println("Erro na análise sintática: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String arquivo = "teste.txt";
        analisadorSintatico parser = new analisadorSintatico(arquivo);
        parser.analisar();
    }
}