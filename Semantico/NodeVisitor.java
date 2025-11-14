package Semantico;

public interface NodeVisitor<T> {
    T visit(DeclaracaoNode node) throws Exception;
    T visit(AtribuicaoNode node) throws Exception;
    T visit(ExpressaoNode node) throws Exception;
}
