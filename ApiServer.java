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
import Analisar.*;
import Sintatico.VerificadorEstruturas;
import Semantico.AnalisadorSemantico;
import Semantico.TabelaSimbolos;

public class ApiServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/analisar", new AnalyzeHandler());
        server.setExecutor(null);
        server.start();
    }

    static class AnalyzeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
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
                        toks.append("\"value\":\"").append(escape(t.getValue())).append("\"}");
                        if (i < tokens.size() - 1) toks.append(",");
                    }
                    toks.append("]");
                    String item = "{\"linha\":\"" + escape(linha) + "\"," +
                            "\"tokens\":" + toks + "," +
                            "\"sintatico\":\"" + (sintaticoOk ? "SUCESSO" : "FALHA") + "\"," +
                            "\"semantico\":\"" + escape(semanticoStatus) + "\"}";
                    resultados.add(item);
                } catch (Exception e) {
                    String item = "{\"linha\":\"" + escape(linha) + "\"," +
                            "\"erro\":\"" + escape(e.getMessage()) + "\"}";
                    resultados.add(item);
                }
            }
            String json = "[" + String.join(",", resultados) + "]";
            addCORS(exchange);
            sendResponse(exchange, 200, json);
        }

        private String readBody(InputStream is) throws IOException {
            byte[] buf = is.readAllBytes();
            return new String(buf, StandardCharsets.UTF_8);
        }

        private void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private void addCORS(HttpExchange exchange) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        }

        private String escape(String s) {
            return s.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }
}
