package Semantico;


public class AtribuicaoNode implements Node {
    public final String nomeVariavel;
    public final ExpressaoNode expressao;
    public AtribuicaoNode(String nomeVariavel, ExpressaoNode expressao){
        this.nomeVariavel = nomeVariavel; this.expressao = expressao;
    }
    public <T> T accept(NodeVisitor<T> visitor) throws Exception{
        return visitor.visit(this);
    }
}
