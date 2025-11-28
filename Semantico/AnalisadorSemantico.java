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

        if (verificarTokenEspecifico(TokenType.OPERATOR, "=")) {
            String tipoExpressao = analisarExpressao();
            if (tipoExpressao != null && !tipoExpressao.equals("boolean")) {
                throw new Exception("Tipo incompatível: esperado 'boolean', encontrado '" + tipoExpressao + "'");
            }
        }

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

        String tipoCondicao = analisarExpressao();
        if (tipoCondicao == null || !tipoCondicao.equals("boolean")) {
            throw new Exception("Condição do if deve ser do tipo boolean");
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

        if (token.getType() == TokenType.KEYWORD &&
            (token.getValue().equalsIgnoreCase("true") || token.getValue().equalsIgnoreCase("false"))) {
            currentIndex++;
            return "boolean";
        }

        if (token.getType() == TokenType.IDENTIFIER &&
            (token.getValue().equalsIgnoreCase("true") || token.getValue().equalsIgnoreCase("false"))) {
            currentIndex++;
            return "boolean";
        }

        if (token.getType() == TokenType.LITERAL) {
            currentIndex++;
            return consumirOperacaoNumerica("int");
        }

        if (token.getType() == TokenType.IDENTIFIER) {
            String nomeVariavel = token.getValue();
            currentIndex++;
            Simbolo simbolo = tabelaSimbolos.buscar(nomeVariavel);
            if (simbolo == null) {
                throw new Exception("Variável '" + nomeVariavel + "' não declarada");
            }
            String tipoBase = simbolo.getTipo();
            if (peekToken() != null && peekToken().getType() == TokenType.OPERATOR) {
                return consumirOperacaoComTipoBase(tipoBase);
            }
            return tipoBase;
        }

        return null;
    }

    private String consumirOperacaoNumerica(String tipoBase) throws Exception {
        if (peekToken() == null || peekToken().getType() != TokenType.OPERATOR) {
            return tipoBase;
        }
        Token op1 = tokens.get(currentIndex);
        currentIndex++;
        Token op2 = peekToken();
        boolean operadorRelacional = false;
        if (op1.getValue().equals("<") || op1.getValue().equals(">") || op1.getValue().equals("!") || op1.getValue().equals("=")) {
            if (op2 != null && op2.getType() == TokenType.OPERATOR && op2.getValue().equals("=")) {
                currentIndex++;
            }
            operadorRelacional = true;
        }
        Token prox = peekToken();
        if (prox == null) {
            return tipoBase;
        }
        String tipoDireita = null;
        if (prox.getType() == TokenType.LITERAL) {
            currentIndex++;
            tipoDireita = "int";
        } else if (prox.getType() == TokenType.IDENTIFIER) {
            String nome = prox.getValue();
            currentIndex++;
            Simbolo s = tabelaSimbolos.buscar(nome);
            if (s == null) {
                throw new Exception("Variável '" + nome + "' não declarada");
            }
            tipoDireita = s.getTipo();
        } else {
            return tipoBase;
        }
        if (!tipoBase.equals("int") || !tipoDireita.equals("int")) {
            throw new Exception("Operação inválida entre tipos '" + tipoBase + "' e '" + tipoDireita + "'");
        }
        if (operadorRelacional) {
            return "boolean";
        }
        return "int";
    }

    private String consumirOperacaoComTipoBase(String tipoBase) throws Exception {
        if (tipoBase.equals("int")) {
            return consumirOperacaoNumerica("int");
        }
        return tipoBase;
    }
}

