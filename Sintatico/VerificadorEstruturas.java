package Sintatico;

import java.util.List;
import Analisar.*;

public class VerificadorEstruturas {
    private List<Token> tokens;
    private int currentIndex;

    public VerificadorEstruturas(List<Token> tokens) {
        this.tokens = tokens;
        this.currentIndex = 0;
    }

    // função para verificar a estrutura de
    public boolean verificarEstrutura() {
        if (tokens == null || tokens.isEmpty()) {
            return false;
        }

        currentIndex = 0;

        // Tenta verificar diferentes tipos de estruturas
        if (verificarDeclaracaoVariavel()) {
            return true;
        } else {
            return false;
        }

    }

    // verifica se o token é o tipo esperado e compara ao token lexer
    private boolean verificarTokenEspecifico(TokenType type, String value) {
        if (currentIndex >= tokens.size()) {
            return false;
        }

        Token token = tokens.get(currentIndex);
        if (token.getType() == type && token.getValue().equals(value)) {
            currentIndex++;
            return true;
        }
        return false;
    }

    private boolean verificarToken(TokenType type) {
        if (currentIndex >= tokens.size()) {
            return false;
        }

        Token token = tokens.get(currentIndex);
        if (token.getType() == type) {
            currentIndex++;
            return true;
        }
        return false;
    }

    // verifica o tokne para analisar
    private Token peekToken() {
        if (currentIndex >= tokens.size()) {
            return null;
        }
        return tokens.get(currentIndex);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    // verifica a eclaração de uma variavel

    public boolean verificarDeclaracaoVariavel() {
        int savedIndex = currentIndex;

        if(!verificarToken(TokenType.KEYWORD)){
            return false;
        }
        if(!verificarToken(TokenType.IDENTIFIER)){
            return false;
        }
        if(verificarTokenEspecifico(TokenType.OPERATOR, "=")) {
            verificaExpressao();
        }
        if(verificarTokenEspecifico(TokenType.PUNCTUATION, ";")){
            return true;
        }
        currentIndex = savedIndex;
        return false;
        
    }

    public boolean verificaExpressao() {
        if(verificarToken(TokenType.IDENTIFIER) || verificarToken(TokenType.LITERAL)){
            return true;
        }
        return false;
    }

}

    