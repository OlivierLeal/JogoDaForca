package JogoDaForca;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImplementacaoForca extends UnicastRemoteObject implements InterfaceForca {

    private static final int MAX_ERROS = 6;
    private static final String[] PALAVRAS = {
        "computador", "teclado", "monitor", "programacao", "internet",
        "servidor", "rede", "algoritmo", "banco", "sistema"
    };

    private static class PartidaJogador {
        final String palavra;
        final InterfaceJogador callback;
        final StringBuilder oculta;
        final StringBuilder erradas = new StringBuilder();
        int erros = 0;

        PartidaJogador(String palavra, InterfaceJogador callback) {
            this.palavra = palavra;
            this.callback = callback;
            this.oculta = new StringBuilder("_".repeat(palavra.length()));
        }
    }

    private final Map<String, PartidaJogador> partidas = new ConcurrentHashMap<>();

    public ImplementacaoForca() throws RemoteException {
        super();
    }

    @Override
    public void entrar(String nome, InterfaceJogador jogador) throws RemoteException {
        String palavra = PALAVRAS[(int) (Math.random() * PALAVRAS.length)];
        PartidaJogador partida = new PartidaJogador(palavra, jogador);
        partidas.put(nome, partida);
        System.out.println("[+] " + nome + " entrou. Palavra: " + palavra);
        jogador.receberEstado(partida.oculta.toString(), "", 0);
    }

    @Override
    public void tentarLetra(String nome, char letra) throws RemoteException {
        PartidaJogador partida = partidas.get(nome);
        if (partida == null) return;

        letra = Character.toLowerCase(letra);

        if (partida.palavra.indexOf(letra) >= 0) {
            for (int i = 0; i < partida.palavra.length(); i++) {
                if (partida.palavra.charAt(i) == letra) {
                    partida.oculta.setCharAt(i, letra);
                }
            }
        } else if (partida.erradas.indexOf(String.valueOf(letra)) < 0) {
            partida.erradas.append(letra);
            partida.erros++;
        }

        System.out.println("[" + nome + "] letra='" + letra + "' erros=" + partida.erros);

        if (!partida.oculta.toString().contains("_")) {
            partida.callback.ganhou(partida.palavra);
            partidas.remove(nome);
        } else if (partida.erros >= MAX_ERROS) {
            partida.callback.perdeu(partida.palavra);
            partidas.remove(nome);
        } else {
            partida.callback.receberEstado(partida.oculta.toString(), partida.erradas.toString(), partida.erros);
        }
    }

    @Override
    public void sair(String nome) throws RemoteException {
        partidas.remove(nome);
        System.out.println("[-] " + nome + " saiu.");
    }
}
