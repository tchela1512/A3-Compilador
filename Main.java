import Lexico.*;
import Semantico.AnalisadorSemantico;
import Semantico.TabelaSimbolos;
import Sintatico.analisadorSintatico;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        
        System.out.println(" ");
        System.out.println("__________Analisador Lexico_____________");
        File file = new File("teste.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while((st=br.readLine()) != null){
                System.out.println(st);
                Lexer lexer = new Lexer(st);
                List<Token> tokens = lexer.tokenize();

                for (Token token : tokens) {
                    System.out.println(token);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(" ");
        System.out.println("__________Analisador Sintatico__________");
        System.out.println(" ");

        analisadorSintatico parser = new analisadorSintatico("teste.txt");
        parser.analisar();

        System.out.println(" ");
        System.out.println("__________Analisador Semântico__________");
        System.out.println(" ");

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("teste.txt")));
            String st;
            TabelaSimbolos tabela = new TabelaSimbolos();
            while ((st = br.readLine()) != null) {
                Lexer lexer = new Lexer(st);
                List<Token> tokens = lexer.tokenize();
                try {
                    AnalisadorSemantico sem = new AnalisadorSemantico(tokens, tabela);
                    sem.analisar();
                    System.out.println("Linha: " + st);
                    System.out.println("Semântico: SUCESSO");
                } catch (Exception e) {
                    System.out.println("Linha: " + st);
                    System.out.println("Erro semântico: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Erro semântico (geral): " + e.getMessage());
        }
    }
}
