package JogoDaForca;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Inicia o servidor do Jogo da Forca via RMI.
 *
 * Como executar:
 *   java -Djava.rmi.server.hostname=<IP> JogoDaForca.ServidorForca
 */
public class ServidorForca {

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            ImplementacaoForca forca = new ImplementacaoForca();
            Naming.rebind("rmi://localhost:1099/JogoDaForca", forca);
            System.out.println("Servidor Jogo da Forca iniciado na porta 1099.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
