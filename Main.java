import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import Lexico.*;
import Semantico.AnalisadorSemantico;
import Semantico.TabelaSimbolos;
import Sintatico.analisadorSintatico;

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

        
        //AnalisadorSemantico interpreter = new AnalisadorSemantico("teste.txt", TabelaSimbolos);
        //interpreter.analisar();
    }
}
