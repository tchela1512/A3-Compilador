package Semantico;

import Analisar.*;
import java.util.List;

public class AnalisadorSemantico {
    private List<Token> tokens;
    private TabelaSimbolos tabelaSimbolos;
    private int currentIndex;

    public AnalisadorSemantico(List<Token> tokens, TabelaSimbolos tabelaSimbolos) {
        this.tokens = tokens;
        this.tabelaSimbolos = tabelaSimbolos;
        this.currentIndex = 0;
    }

    public void analisar() throws Exception {
        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        currentIndex = 0;

        if (analisarDeclaracaoVariavelInt()) {
            return;
        }
        if (analisarDeclaracaoVariavelString()) {
            return;
        }
        if (analisarDeclaracaoVariavelBoolean()) {
            return;
        }
        if (analisarIf()) {
            return;
        }
        if (analisarAtribuicao()) {
            return;
        }
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

    private boolean analisarDeclaracaoVariavelInt() throws Exception {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "int")) {
            currentIndex = savedIndex;
            return false;
        }

        if (!verificarToken(TokenType.IDENTIFIER)) {
            currentIndex = savedIndex;
            return false;
        }

        String nomeVariavel = tokens.get(savedIndex + 1).getValue();

        if (verificarTokenEspecifico(TokenType.OPERATOR, "=")) {
            String tipoExpressao = analisarExpressao();
            if (tipoExpressao != null && !tipoExpressao.equals("int")) {
                throw new Exception("Tipo incompatível: esperado 'int', encontrado '" + tipoExpressao + "'");
            }
        }

        if (verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
            tabelaSimbolos.adicionarSimbolo(nomeVariavel, "int");
            return true;
        }

        currentIndex = savedIndex;
        return false;
    }

    private boolean analisarDeclaracaoVariavelString() throws Exception {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "String")) {
            currentIndex = savedIndex;
            return false;
        }

        if (!verificarToken(TokenType.IDENTIFIER)) {
            currentIndex = savedIndex;
            return false;
        }

        String nomeVariavel = tokens.get(savedIndex + 1).getValue();

        if (verificarTokenEspecifico(TokenType.OPERATOR, "=")) {
            String tipoExpressao = analisarExpressao();
            if (tipoExpressao != null && !tipoExpressao.equals("String")) {
                throw new Exception("Tipo incompatível: esperado 'String', encontrado '" + tipoExpressao + "'");
            }
        }

        if (verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
            tabelaSimbolos.adicionarSimbolo(nomeVariavel, "String");
            return true;
        }

        currentIndex = savedIndex;
        return false;
    }

    private boolean analisarDeclaracaoVariavelBoolean() throws Exception {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "boolean")) {
            currentIndex = savedIndex;
            return false;
        }

        if (!verificarToken(TokenType.IDENTIFIER)) {
            currentIndex = savedIndex;
            return false;
        }

        String nomeVariavel = tokens.get(savedIndex + 1).getValue();

        if (!verificarTokenEspecifico(TokenType.OPERATOR, "=")) {
            currentIndex = savedIndex;
            return false;
        }

        Token valorToken = peekToken();
        if (valorToken == null || 
            (valorToken.getType() != TokenType.IDENTIFIER) ||
            (!valorToken.getValue().equals("true") && !valorToken.getValue().equals("false"))) {
            currentIndex = savedIndex;
            return false;
        }
        verificarToken(TokenType.IDENTIFIER);

        if (verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
            tabelaSimbolos.adicionarSimbolo(nomeVariavel, "boolean");
            return true;
        }

        currentIndex = savedIndex;
        return false;
    }

    private boolean analisarIf() throws Exception {
        int savedIndex = currentIndex;

        if (!verificarTokenEspecifico(TokenType.KEYWORD, "if")) {
            currentIndex = savedIndex;
            return false;
        }

        if (!verificarTokenEspecifico(TokenType.PUNCTUATION, "(")) {
            currentIndex = savedIndex;
            return false;
        }

        if (!verificarToken(TokenType.IDENTIFIER)) {
            currentIndex = savedIndex;
            return false;
        }

        String nomeVariavel = tokens.get(savedIndex + 2).getValue();

        Simbolo simbolo = tabelaSimbolos.buscar(nomeVariavel);
        if (simbolo == null) {
            throw new Exception("Variável '" + nomeVariavel + "' não declarada na condição do if");
        }

        if (!simbolo.getTipo().equals("boolean")) {
            throw new Exception("Condição do if deve ser do tipo boolean, mas '" + nomeVariavel + "' é do tipo '" + simbolo.getTipo() + "'");
        }

        if (!verificarTokenEspecifico(TokenType.PUNCTUATION, ")")) {
            currentIndex = savedIndex;
            return false;
        }

        if (!verificarTokenEspecifico(TokenType.PUNCTUATION, "{")) {
            currentIndex = savedIndex;
            return false;
        }

        if (verificarTokenEspecifico(TokenType.PUNCTUATION, "}")) {
            return true;
        }

        currentIndex = savedIndex;
        return false;
    }

    private boolean analisarAtribuicao() throws Exception {
        int savedIndex = currentIndex;

        if (!verificarToken(TokenType.IDENTIFIER)) {
            currentIndex = savedIndex;
            return false;
        }

        String nomeVariavel = tokens.get(savedIndex).getValue();

        Simbolo simbolo = tabelaSimbolos.buscar(nomeVariavel);
        if (simbolo == null) {
            throw new Exception("Variável '" + nomeVariavel + "' não declarada");
        }

        if (!verificarTokenEspecifico(TokenType.OPERATOR, "=")) {
            currentIndex = savedIndex;
            return false;
        }

        String tipoExpressao = analisarExpressao();
        if (tipoExpressao != null && !tipoExpressao.equals(simbolo.getTipo())) {
            throw new Exception("Tipo incompatível na atribuição: variável '" + nomeVariavel + "' é do tipo '" + 
                             simbolo.getTipo() + "', mas a expressão é do tipo '" + tipoExpressao + "'");
        }

        if (verificarTokenEspecifico(TokenType.PUNCTUATION, ";")) {
            return true;
        }

        currentIndex = savedIndex;
        return false;
    }

    private String analisarExpressao() throws Exception {
        if (currentIndex >= tokens.size()) {
            return null;
        }

        Token token = peekToken();
        if (token == null) {
            return null;
        }

        if (token.getType() == TokenType.LITERAL) {
            verificarToken(TokenType.LITERAL);
            return "int";
        }

        if (token.getType() == TokenType.IDENTIFIER) {
            String nomeVariavel = token.getValue();
            verificarToken(TokenType.IDENTIFIER);

            Simbolo simbolo = tabelaSimbolos.buscar(nomeVariavel);
            if (simbolo == null) {
                throw new Exception("Variável '" + nomeVariavel + "' não declarada");
            }

            return simbolo.getTipo();
        }

        if (token.getType() == TokenType.IDENTIFIER && 
            (token.getValue().equals("true") || token.getValue().equals("false"))) {
            verificarToken(TokenType.IDENTIFIER);
            return "boolean";
        }

        return null;
    }
}

