package Semantico;
import java.util.HashMap;
import java.util.Map;

public class TabelaSimbolos {
    private Map<String, Simbolo> tabela = new HashMap<>();

    public void adicionarSimbolo(String nome, String tipo) throws Exception{
        if (tabela.containsKey(nome)){
            throw new Exception("Variável '" + nome + "' já declarada.");
        }
        tabela.put(nome, new Simbolo(nome, tipo));
    }

    public Simbolo buscar(String nome) throws Exception{
        if (!tabela.containsKey(nome)){
            throw new Exception("Variável '" + nome + "' não declarada.");
        }
        return tabela.get(nome);
    }
}
