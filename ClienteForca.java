package JogoDaForca;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/**
 * Cliente do Jogo da Forca via RMI.
 *
 * Como executar:
 *   java -Djava.rmi.server.hostname=<SEU_IP> JogoDaForca.ClienteForca
 */
public class ClienteForca extends UnicastRemoteObject implements InterfaceJogador {

    volatile boolean jogoEncerrado = false;

    public ClienteForca() throws RemoteException {
        super();
    }

    @Override
    public void receberEstado(String palavraOculta, String letrasErradas, int erros) throws RemoteException {
        System.out.println("\n" + forca(erros));
        System.out.println("Palavra : " + espacar(palavraOculta));
        System.out.println("Erradas : " + letrasErradas);
        System.out.println("Erros   : " + erros + "/" + 6);
        System.out.print("Digite uma letra: ");
    }

    @Override
    public void ganhou(String palavra) throws RemoteException {
        System.out.println("\nParabéns! Você acertou a palavra: " + palavra);
        jogoEncerrado = true;
    }

    @Override
    public void perdeu(String palavra) throws RemoteException {
        System.out.println("\n" + forca(6));
        System.out.println("Você perdeu! A palavra era: " + palavra);
        jogoEncerrado = true;
    }

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        System.out.print("Seu nome: ");
        String nome = teclado.nextLine().trim();

        System.out.print("Host do servidor [localhost]: ");
        String host = teclado.nextLine().trim();
        if (host.isEmpty()) host = "localhost";

        try {
            InterfaceForca servidor = (InterfaceForca) Naming.lookup("rmi://" + host + ":1099/JogoDaForca");

            ClienteForca jogador = new ClienteForca();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { servidor.sair(nome); } catch (RemoteException ignored) {}
            }));

            boolean continuar = true;
            while (continuar) {
                jogador.jogoEncerrado = false;
                servidor.entrar(nome, jogador);

                while (!jogador.jogoEncerrado && teclado.hasNextLine()) {
                    String entrada = teclado.nextLine().trim();
                    if (entrada.isEmpty()) continue;
                    servidor.tentarLetra(nome, entrada.charAt(0));
                }

                System.out.print("\nJogar novamente? (s/n): ");
                continuar = teclado.hasNextLine() && teclado.nextLine().trim().equalsIgnoreCase("s");
            }

            servidor.sair(nome);
            teclado.close();

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String espacar(String s) {
        return s.chars().mapToObj(c -> String.valueOf((char) c)).reduce("", (a, b) -> a + " " + b).trim();
    }

    private static String forca(int erros) {
        String[] linhas = {
            "  +---+",
            "  |   |",
            "  |   " + (erros >= 1 ? "O" : " "),
            "  |  " + (erros >= 3 ? "/" : " ") + (erros >= 2 ? "|" : " ") + (erros >= 4 ? "\\" : " "),
            "  |  " + (erros >= 5 ? "/" : " ") + " " + (erros >= 6 ? "\\" : " "),
            "  |",
            "======="
        };
        StringBuilder sb = new StringBuilder();
        for (String l : linhas) sb.append(l).append("\n");
        return sb.toString();
    }
}
