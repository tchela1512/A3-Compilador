package Semantico;
import java.util.*;

public class TabelaSimbolos {
    private Deque<Map<String, Simbolo>> escopos = new ArrayDeque<>();

    public TabelaSimbolos() {
        escopos.push(new HashMap<>());
    }

    public void pushEscopo() {
        escopos.push(new HashMap<>());
    }

    public void popEscopo() {
        if (escopos.size() > 1) {
            escopos.pop();
        }
    }

    public void adicionarSimbolo(String nome, String tipo) throws Exception{
        Map<String, Simbolo> topo = escopos.peek();
        if (topo.containsKey(nome)){
            throw new Exception("Variável '" + nome + "' já declarada.");
        }
        topo.put(nome, new Simbolo(nome, tipo));
    }

    public Simbolo buscar(String nome){
        for (Map<String, Simbolo> escopo : escopos){
            Simbolo s = escopo.get(nome);
            if (s != null) return s;
        }
        return null;
    }

    public List<Simbolo> listarSimbolos() {
        LinkedHashMap<String, Simbolo> acumulado = new LinkedHashMap<>();
        for (Map<String, Simbolo> escopo : escopos) {
            for (Map.Entry<String, Simbolo> e : escopo.entrySet()) {
                acumulado.putIfAbsent(e.getKey(), e.getValue());
            }
        }
        return new ArrayList<>(acumulado.values());
    }
}
