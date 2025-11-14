package Semantico;

public class AnalisadorSemantico implements NodeVisitor<Void>{

    private TabelaSimbolos tabela;

    public AnalisadorSemantico (TabelaSimbolos tabela){
        this.tabela = tabela;
    }
    public void analisadorAtribuicao(String variavel, String tipoValor) throws Exception {
        Simbolo s = tabela.buscar(variavel);
        if (!s.getTipo().equals(tipoValor)){
            throw new Exception("Tipo incompatível: variável '" + variavel +
                            "' é " + s.getTipo() + " mas recebeu" + tipoValor);
        }
    }

    @Override
    public Void visit(DeclaracaoNode node) throws Exception {
        return null;
    }

    @Override
    public Void visit(AtribuicaoNode node) throws Exception {
        Simbolo simbolo = tabela.buscar(node.nomeVariavel);
        String tipoExpr = inferirTipo(node.expressao);
        if (!simbolo.getTipo().equals(tipoExpr)) {
            throw new Exception("Tipo incompatível: variável '" + node.nomeVariavel +
                    "' é " + simbolo.getTipo() + " mas recebeu " + tipoExpr);
        }
        return null;
    }

    @Override
    public Void visit(ExpressaoNode node) throws Exception {
        return null;
    }

    private String inferirTipo(ExpressaoNode expressao) {

        if (expressao.valor.matches("\\d+")) {
            return "inteiro";
        } else if (expressao.valor.matches("\\d+\\.\\d+")) {
            return "real";
        } else if (expressao.valor.equals("true") || expressao.valor.equals("false")) {
            return "booleano";
        } else {
            return "desconhecido";
        }
    }
}
