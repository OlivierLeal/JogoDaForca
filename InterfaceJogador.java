package JogoDaForca;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Interface remota de callback do jogador. O servidor chama esses métodos remotamente. */
public interface InterfaceJogador extends Remote {
    void receberEstado(String palavraOculta, String letrasErradas, int erros) throws RemoteException;
    void ganhou(String palavra) throws RemoteException;
    void perdeu(String palavra) throws RemoteException;
}
