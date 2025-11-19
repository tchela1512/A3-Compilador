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
        if (verificarDeclaracaoVariavelInt()) {
            return true;
        }
        if (verificarDeclaracaoVariavelString()) {
            return true;
        }
        if (verificarDeclaracaoVariavelBoolean()){
            return true;
        }
        if (verificarIf()){
            return true;
        }
        if (verificarFor()){
            return true;
        }
        return false;

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

    public boolean verificarDeclaracaoVariavelInt() {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "int")) {
            return false;
        }
        if (!verificarToken(TokenType.IDENTIFIER)) {
            return false;
        }
        if (verificarTokenEspecifico(TokenType.OPERATOR, "=")) {
            verificaExpressao();
        }
        if (verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;

    }

    public boolean verificarDeclaracaoVariavelString() {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "String")) {
            return false;
        }
        if (!verificarToken(TokenType.IDENTIFIER)) {
            return false;
        }
        if (verificarTokenEspecifico(TokenType.OPERATOR, "=")) {
            verificaExpressao();
        }
        if (verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;

    }

    public boolean verificarDeclaracaoVariavelBoolean() {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "boolean")) {
            return false;
        }
        if (!verificarToken(TokenType.IDENTIFIER)) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.OPERATOR, "=")) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.OPERATOR, "false") || !verificarTokenEspecifico(TokenType.OPERATOR, "true") ) {
            return false;
        }
        if (verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;

    }

    public boolean verificarIf(){
         int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "if")) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.PUNCTUATION, "(")) {
            return false;
        }
        if (!verificarToken(TokenType.IDENTIFIER)) {
            return false;
        }
        if (!verificarToken(TokenType.OPERATOR)) {
            return false;
        }
        if (!verificarToken(TokenType.LITERAL)) {
            return false; 
        }
        if (!verificarTokenEspecifico(TokenType.PUNCTUATION, ")")) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.PUNCTUATION, "{")) {
            return false;
        }
        if (verificarTokenEspecifico(TokenType.PUNCTUATION, "}")) {
            return true;
        }
        
        currentIndex = savedIndex;
        return false;

    }
      public boolean verificarFor(){
         int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "for")) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.PUNCTUATION, "(")) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.KEYWORD, "int")) {
            return false;
        }
        if (!verificarToken(TokenType.IDENTIFIER)) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.OPERATOR, "=")) {
            return false;
        }
        if (!verificarToken(TokenType.LITERAL)) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
            return false;
        }
        if (!verificarToken(TokenType.IDENTIFIER)) {
            return false;
        }
        if (!verificarToken(TokenType.OPERATOR)) {
            return false;
        }
        if (!verificarToken(TokenType.LITERAL)) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
            return false;
        }
        if (!verificarToken(TokenType.IDENTIFIER)) {
            return false;
        }
        if (!verificarToken(TokenType.OPERATOR)) {
            return false;
        }
        if (!verificarToken(TokenType.OPERATOR)) {
            return false;
        }
        if (verificarTokenEspecifico(TokenType.PUNCTUATION, ")")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;


    }
    

    public boolean verificaExpressao() {
        if (verificarToken(TokenType.IDENTIFIER) || verificarToken(TokenType.LITERAL)) {
            return true;
        }
        return false;
    }

      private boolean verificarAtribuicao() {
        int savedIndex = currentIndex;
        
        if (verificarToken(TokenType.IDENTIFIER)) {
            if (verificarTokenEspecifico(TokenType.OPERATOR, "=")) {
                if (verificaExpressao()) {
                    if (verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
                        return true;
                    }
                }
            }
        }
        
        currentIndex = savedIndex;
        return false;
    }

    private boolean verificarBloco() {
        while (currentIndex < tokens.size()) {
            Token current = peekToken();
            if (current == null) {
                break;
            }

            if (current.getType() == TokenType.PUNCTUATION && current.getValue().equals("}")) {
                break;
            }

            if (!verificarAtribuicao() && !verificarDeclaracaoVariavelString()) {
                break;
            }
        }
        return true;
    }


}
