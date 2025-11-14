package Semantico;

public class ExpressaoNode implements Node {
    public final String valor;

    public ExpressaoNode(String valor) {
        this.valor = valor;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws Exception {
        return visitor.visit(this);
    }
}

