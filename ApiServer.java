import Lexico.*;
import Semantico.AnalisadorSemantico;
import Semantico.TabelaSimbolos;
import Sintatico.VerificadorEstruturas;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ApiServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/analisar", new AnalyzeHandler());
        server.createContext("/lexico", new LexicoHandler());
        server.createContext("/sintatico", new SintaticoHandler());
        server.createContext("/semantico", new SemanticoHandler());
        server.createContext("/resultado", new ResultadoHandler());
        server.setExecutor(null);
        server.start();
    }

    static class AnalyzeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                ApiServer.addCORS(exchange);
                ApiServer.sendResponse(exchange, 200, "{}");
                return;
            }
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                ApiServer.sendResponse(exchange, 405, "{\"error\":\"Método não permitido\"}");
                return;
            }
            String body = ApiServer.readBody(exchange.getRequestBody());
            String[] linhas = body.split("\r?\n");
            TabelaSimbolos tabela = new TabelaSimbolos();
            List<String> resultados = new ArrayList<>();
            for (String linha : linhas) {
                try {
                    Lexer lexer = new Lexer(linha);
                    List<Token> tokens = lexer.tokenize();
                    VerificadorEstruturas verificador = new VerificadorEstruturas(tokens);
                    boolean sintaticoOk;
                    try {
                        sintaticoOk = verificador.verificarEstrutura();
                    } catch (Exception e) {
                        sintaticoOk = false;
                    }
                    String semanticoStatus;
                    try {
                        AnalisadorSemantico sem = new AnalisadorSemantico(tokens, tabela);
                        sem.analisar();
                        semanticoStatus = "SUCESSO";
                    } catch (Exception e) {
                        semanticoStatus = e.getMessage();
                    }
                    StringBuilder toks = new StringBuilder();
                    toks.append("[");
                    for (int i = 0; i < tokens.size(); i++) {
                        Token t = tokens.get(i);
                        toks.append("{\"type\":\"").append(t.getType()).append("\",");
                        toks.append("\"value\":\"").append(ApiServer.escape(t.getValue())).append("\"}");
                        if (i < tokens.size() - 1) toks.append(",");
                    }
                    toks.append("]");
                    String item = "{\"linha\":\"" + ApiServer.escape(linha) + "\"," +
                            "\"tokens\":" + toks + "," +
                            "\"sintatico\":\"" + (sintaticoOk ? "SUCESSO" : "FALHA") + "\"," +
                            "\"semantico\":\"" + ApiServer.escape(semanticoStatus) + "\"}";
                    resultados.add(item);
                } catch (Exception e) {
                    String item = "{\"linha\":\"" + ApiServer.escape(linha) + "\"," +
                            "\"erro\":\"" + ApiServer.escape(e.getMessage()) + "\"}";
                    resultados.add(item);
                }
            }
            String json = "[" + String.join(",", resultados) + "]";
            ApiServer.addCORS(exchange);
            ApiServer.sendResponse(exchange, 200, json);
        }
    }

    static class ResultadoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                ApiServer.addCORS(exchange);
                ApiServer.sendResponse(exchange, 200, "{}");
                return;
            }
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                ApiServer.sendResponse(exchange, 405, "{\"error\":\"Método não permitido\"}");
                return;
            }
            String body = ApiServer.readBody(exchange.getRequestBody());
            String[] linhas = body.split("\r?\n");
            Semantico.TabelaSimbolos tabela = new Semantico.TabelaSimbolos();
            int sucessos = 0;
            int erros = 0;
            for (String linha : linhas) {
                try {
                    Lexer lexer = new Lexer(linha);
                    java.util.List<Token> tokens = lexer.tokenize();
                    try {
                        Semantico.AnalisadorSemantico sem = new Semantico.AnalisadorSemantico(tokens, tabela);
                        sem.analisar();
                        sucessos++;
                    } catch (Exception e) {
                        erros++;
                    }
                } catch (Exception e) {
                    erros++;
                }
            }
            StringBuilder simbs = new StringBuilder();
            simbs.append("[");
            java.util.List<Semantico.Simbolo> lista = tabela.listarSimbolos();
            for (int i = 0; i < lista.size(); i++) {
                Semantico.Simbolo s = lista.get(i);
                simbs.append("{\"nome\":\"").append(ApiServer.escape(s.getNome())).append("\",");
                simbs.append("\"tipo\":\"").append(ApiServer.escape(s.getTipo())).append("\"}");
                if (i < lista.size() - 1) simbs.append(",");
            }
            simbs.append("]");
            String json = "{\"sucessos\":" + sucessos + ",\"erros\":" + erros + ",\"simbolos\":" + simbs + "}";
            ApiServer.addCORS(exchange);
            ApiServer.sendResponse(exchange, 200, json);
        }
    }

    private static String readBody(InputStream is) throws IOException {
        byte[] buf = is.readAllBytes();
        return new String(buf, StandardCharsets.UTF_8);
    }

    private static void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void addCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    static class LexicoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                ApiServer.addCORS(exchange);
                ApiServer.sendResponse(exchange, 200, "{}");
                return;
            }
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                ApiServer.sendResponse(exchange, 405, "{\"error\":\"Método não permitido\"}");
                return;
            }
            String body = ApiServer.readBody(exchange.getRequestBody());
            String[] linhas = body.split("\r?\n");
            List<String> resultados = new ArrayList<>();
            for (String linha : linhas) {
                Lexer lexer = new Lexer(linha);
                List<Token> tokens = lexer.tokenize();
                StringBuilder toks = new StringBuilder();
                toks.append("[");
                for (int i = 0; i < tokens.size(); i++) {
                    Token t = tokens.get(i);
                    toks.append("{\"type\":\"").append(t.getType()).append("\",");
                    toks.append("\"value\":\"").append(escape(t.getValue())).append("\"}");
                    if (i < tokens.size() - 1) toks.append(",");
                }
                toks.append("]");
                resultados.add("{\"linha\":\"" + escape(linha) + "\",\"tokens\":" + toks + "}");
            }
            String json = "[" + String.join(",", resultados) + "]";
            ApiServer.addCORS(exchange);
            ApiServer.sendResponse(exchange, 200, json);
        }
    }

    static class SintaticoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                ApiServer.addCORS(exchange);
                ApiServer.sendResponse(exchange, 200, "{}");
                return;
            }
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                ApiServer.sendResponse(exchange, 405, "{\"error\":\"Método não permitido\"}");
                return;
            }
            String body = ApiServer.readBody(exchange.getRequestBody());
            String[] linhas = body.split("\r?\n");
            List<String> resultados = new ArrayList<>();
            for (String linha : linhas) {
                try {
                    Lexer lexer = new Lexer(linha);
                    List<Token> tokens = lexer.tokenize();
                    VerificadorEstruturas verificador = new VerificadorEstruturas(tokens);
                    boolean ok = verificador.verificarEstrutura();
                    resultados.add("{\"linha\":\"" + escape(linha) + "\",\"sintatico\":\"" + (ok?"SUCESSO":"FALHA") + "\"}");
                } catch (Exception e) {
                    resultados.add("{\"linha\":\"" + escape(linha) + "\",\"erro\":\"" + escape(e.getMessage()) + "\"}");
                }
            }
            String json = "[" + String.join(",", resultados) + "]";
            ApiServer.addCORS(exchange);
            ApiServer.sendResponse(exchange, 200, json);
        }
    }

    static class SemanticoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                addCORS(exchange);
                sendResponse(exchange, 200, "{}");
                return;
            }
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "{\"error\":\"Método não permitido\"}");
                return;
            }
            String body = readBody(exchange.getRequestBody());
            String[] linhas = body.split("\r?\n");
            TabelaSimbolos tabela = new TabelaSimbolos();
            List<String> resultados = new ArrayList<>();
            for (String linha : linhas) {
                try {
                    Lexer lexer = new Lexer(linha);
                    List<Token> tokens = lexer.tokenize();
                    String semStatus;
                    try {
                        AnalisadorSemantico sem = new AnalisadorSemantico(tokens, tabela);
                        sem.analisar();
                        semStatus = "SUCESSO";
                    } catch (Exception e) {
                        semStatus = e.getMessage();
                    }
                    resultados.add("{\"linha\":\"" + escape(linha) + "\",\"semantico\":\"" + escape(semStatus) + "\"}");
                } catch (Exception e) {
                    resultados.add("{\"linha\":\"" + escape(linha) + "\",\"erro\":\"" + escape(e.getMessage()) + "\"}");
                }
            }
            String json = "[" + String.join(",", resultados) + "]";
            addCORS(exchange);
            sendResponse(exchange, 200, json);
        }
    }
}
