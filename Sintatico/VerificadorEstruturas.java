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
        if (verificarInteiro()) {
            return true;
        }
        if (verificarTexto()) {
            return true;
        }
        if (verificarLogico()) {
            return true;
        }
        if (verificarSe()) {
            return true;
        }
        if (verificarPara()) {
            return true;
        }
        if (verificarSenao()) {
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

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public boolean verificarInteiro() {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "INTEIRO")) {
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

    public boolean verificarTexto() {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "TEXTO")) {
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

    public boolean verificarLogico() {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "LOGICO")) {
            return false;
        }
        if (!verificarToken(TokenType.IDENTIFIER)) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.OPERATOR, "=")) {
            return false;
        }
        if (!verificarToken(TokenType.BOOLEAN_LITERAL)) {
            return false;
        }
        if (verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;

    }

    public boolean verificarSe() {
        int savedIndex = currentIndex;

        if (verificarTokenEspecifico(TokenType.KEYWORD, "SE") &&
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

    public boolean verificarSenao() {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "SENAO")) {
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

    public boolean verificarPara() {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "PARA")) {
            return false;
        }
        if (!verificarTokenEspecifico(TokenType.KEYWORD, "INTEIRO")) {
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
        if (!verificarTokenEspecifico(TokenType.KEYWORD, "INTEIRO")) {
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

    public boolean verificaPontuacao() {
        int savedIndex = currentIndex;
        if (verificarTokenEspecifico(TokenType.PUNCTUATION, "}")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;
    }
}
