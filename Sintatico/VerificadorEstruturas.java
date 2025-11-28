package Sintatico;

import java.util.List;

import Lexico.*;

public class VerificadorEstruturas {
    private List<Token> tokens;
    private int currentIndex;

    public VerificadorEstruturas(List<Token> tokens) {
        this.tokens = tokens;
        this.currentIndex = 0;
    }

    public boolean verificarEstrutura() {
        if (tokens == null || tokens.isEmpty()) {
            return false;
        }
        currentIndex = 0;
        if (verificarInt()) {
            return true;
        }
        if (verificarString()) {
            return true;
        }
        if (verificarBoolean()) {
            return true;
        }
        if (verificarIf()) {
            return true;
        }
        if (verificarFor()) {
            return true;
        }
        if (verificarElse()) {
            return true;
        }
        if (verificaPontuacao()) {
            return true;
        }
        if (verificaConta()) {
            return true;
        }
        return false;

    }

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

    public boolean verificarInt() {
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

    public boolean verificarString() {
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

    public boolean verificarBoolean() {
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
        if (verificarTokenEspecifico(TokenType.IDENTIFIER, "false")
                || verificarTokenEspecifico(TokenType.IDENTIFIER, "true")) {
            return true;
        }
        if (verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;

    }

    public boolean verificarIf() {
        int savedIndex = currentIndex;

        if (verificarTokenEspecifico(TokenType.KEYWORD, "if") &&
                verificarTokenEspecifico(TokenType.PUNCTUATION, "(") &&
                verificarToken(TokenType.IDENTIFIER) &&
                verificarToken(TokenType.OPERATOR) &&
                verificarToken(TokenType.IDENTIFIER) &&
                verificarTokenEspecifico(TokenType.PUNCTUATION, ")") &&
                verificarTokenEspecifico(TokenType.PUNCTUATION, "{")) {
            return true;
        }

        currentIndex = savedIndex;
        return false;
    }

    public boolean verificarElse() {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "else")) {
            return false;
        }
        if (!verificarToken(TokenType.IDENTIFIER)) {
            return false;
        }
        if (verificarTokenEspecifico(TokenType.PUNCTUATION, "{")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;
    }

    public boolean verificarFor() {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "for")) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.KEYWORD, "int")) {
            return false;
        }
        if (!verificarToken(TokenType.IDENTIFIER)) {
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
        if (!verificarTokenEspecifico(TokenType.PUNCTUATION, ")")) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.PUNCTUATION, "{")) {
            return false;
        }
        if (verificarTokenEspecifico(TokenType.PUNCTUATION, "}")) {
            return false;
        }
        currentIndex = savedIndex;
        return true;

    }

    public boolean verificaConta() {
        int savedIndex = currentIndex;
        if (!verificarTokenEspecifico(TokenType.KEYWORD, "int")) {
            return false;
        }
        if (!verificarToken(TokenType.IDENTIFIER)) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.OPERATOR, "=")) {
            return false;
        }
        if (verificarToken(TokenType.IDENTIFIER) || verificarToken(TokenType.LITERAL)) {
            return true;
        }
        if (!verificarToken(TokenType.OPERATOR)) {
            return false;
        }
        if (verificarToken(TokenType.IDENTIFIER) || verificarToken(TokenType.LITERAL)) {
            return true;
        }
        if (!verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
            return false;
        }
        currentIndex = savedIndex;
        return true;
    }

    public boolean verificaExpressao() {
        if (verificarToken(TokenType.IDENTIFIER) || verificarToken(TokenType.LITERAL)) {
            return true;
        }
        return false;
    }

    public boolean verificarAtribuicao() {
        int savedIndex = currentIndex;

        if (verificarToken(TokenType.IDENTIFIER) &&
                verificarTokenEspecifico(TokenType.OPERATOR, "=") &&
                verificaExpressao() &&
                verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
            return true;
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

            if (!verificarAtribuicao() && !verificarString()) {
                break;
            }
        }
        return true;
    }

    public boolean verificaPontuacao() {
        int savedIndex = currentIndex;
        if (verificarTokenEspecifico(TokenType.PUNCTUATION, "}")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;
    }
}
