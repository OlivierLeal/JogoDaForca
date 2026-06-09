package JogoDaForca;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Interface remota do servidor do Jogo da Forca. */
public interface InterfaceForca extends Remote {
    void entrar(String nome, InterfaceJogador jogador) throws RemoteException;
    void tentarLetra(String nome, char letra) throws RemoteException;
    void sair(String nome) throws RemoteException;
}
