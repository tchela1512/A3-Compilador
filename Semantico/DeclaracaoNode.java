package Semantico;



public class DeclaracaoNode implements Node {
    public final String nome;
    public final String tipo;
    public DeclaracaoNode(String nome, String tipo){
        this.nome = nome; this.tipo = tipo;
    }
    public <T> T accept(NodeVisitor<T> visitor) throws Exception{
        return visitor.visit(this);
    }
}
