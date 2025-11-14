import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import Analisar.*;
import Semantico.*;

public class Main {
    public static void main(String[] args) {
        

        File file = new File("teste.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while((st=br.readLine()) != null){
            //    System.out.println(st);
                Lexer lexer = new Lexer(st);
                List<Token> tokens = lexer.tokenize();

                for (Token token : tokens) {
                    System.out.println(token);
                }

            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        String code = "if (x > 10) { y = x + 5; }";
        Lexer lexer = new Lexer(sc);
        List<Token> tokens = lexer.tokenize();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
