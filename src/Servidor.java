
import javax.swing.*;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class Servidor {

	// Despertador 5:30

	public static void main(String[] args) {

		MarcoServidor mimarco = new MarcoServidor();
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

// como quremos que el servidor haga mas de una cosa a la vez, necesitamos
// hilos. Por lo tanto implementamos Runnable para que lo haga
class MarcoServidor extends JFrame implements Runnable {

	public MarcoServidor() {

		setBounds(1200, 300, 280, 350);

		JPanel milamina = new JPanel();

		milamina.setLayout(new BorderLayout());

		areatexto = new JTextArea();

		milamina.add(areatexto, BorderLayout.CENTER);

		add(milamina);

		setVisible(true);

		Thread miHilo = new Thread(this);
		miHilo.start();

	}

	private JTextArea areatexto;

	@Override
	public void run() {
		try {
			ServerSocket servidor = new ServerSocket(9999);
			String ip, mensaje, nick;
			int señal;
			boolean conectado;
			PaqueteEnvio paqueteRecibido;
			Hashtable<String, String> Usuario_Ip = new Hashtable<String, String>();

			ArrayList<String> listaUsuarios = new ArrayList<String>();

			listaUsuarios.add("TODOS");
			Usuario_Ip.put("TODOS", "localhost");
			while (true) {
				Socket enviarDestinatario = null;
				ObjectOutputStream paqueteReenvio = null;

				// hago que este a la escucha y acepto las conecciones que le
				// vengan
				Socket socket = servidor.accept();

				ObjectInputStream paqueteDatos = new ObjectInputStream(socket.getInputStream());

				paqueteRecibido = (PaqueteEnvio) paqueteDatos.readObject();

				nick = paqueteRecibido.getNick();
				ip = paqueteRecibido.getIp();
				mensaje = paqueteRecibido.getMensaje();
				señal = paqueteRecibido.getSeñal();
				paqueteDatos.close();

				if (señal == PaqueteEnvio.ONLINE) {
					
					areatexto.append("user: " + nick + " ip: " + ip + "  Mensaje: " + mensaje + "\n");
					
					
					enviarDestinatario = new Socket(Usuario_Ip.get(nick), 9090);
//					enviarDestinatario = new Socket(ip, 9090);
					paqueteReenvio = new ObjectOutputStream(enviarDestinatario.getOutputStream());

					paqueteReenvio.writeObject(paqueteRecibido);

					paqueteReenvio.close();
					enviarDestinatario.close();
				} else {

					if (señal == PaqueteEnvio.PRIMERACONEXION) {
						// ----------------- DETECTA USUARIOS ONLINE
						// ----------------//
						// almaceno en localizacion la informacion del que se
						// conecta
						InetAddress localizacion = socket.getInetAddress();
						// obtengo la IP del cliente que se conecta
						String ipRemota = localizacion.getHostAddress();

						
						Usuario_Ip.put(nick, ipRemota);

						areatexto.append("Se conecto la IP: " + ipRemota + " con nick: " + nick + "\n");
						areatexto.append("Usuarios Conectados: " + Usuario_Ip.size() + "\n");
						areatexto.append("Mostrando LIsta De Usuarios Online: " + Usuario_Ip + "\n");

						// paqueteRecibido.setUsuarios_Ips(Usuario_Ip);

						listaUsuarios.add(nick);

						paqueteRecibido.setUsuarios(listaUsuarios);

						for (String z : listaUsuarios) {
							
							enviarDestinatario = new Socket(Usuario_Ip.get(z), 9090);

							paqueteReenvio = new ObjectOutputStream(enviarDestinatario.getOutputStream());

							paqueteReenvio.writeObject(paqueteRecibido);
							enviarDestinatario.close();
							paqueteReenvio.close();

						}
					}
					if (señal == PaqueteEnvio.DESCONEXION) {
						Usuario_Ip.remove(nick);
						listaUsuarios.remove(nick);
						areatexto.append("DESCONECCION DE USUARIO: " + nick + "\n");
						areatexto.append("Reenviando lista actualizada a todos los usuarios \n");

						areatexto.append("Usuarios Conectados: " + Usuario_Ip.size() + "\n");
						areatexto.append("Mostrando LIsta De Usuarios Online: " + Usuario_Ip + "\n");
						for (String z : listaUsuarios) {

							if(z != "TODOS"){
								try {
									enviarDestinatario = new Socket(Usuario_Ip.get(z), 11111);
	
								} catch (Exception e) {
									System.out.println(Usuario_Ip.get(z));
									System.out.println(e.getMessage());
								}
	
								paqueteReenvio = new ObjectOutputStream(enviarDestinatario.getOutputStream());
	
								paqueteReenvio.writeObject(paqueteRecibido);
								enviarDestinatario.close();
								paqueteReenvio.close();
							}
						}
					}else if (señal == PaqueteEnvio.TODOS) {
						System.out.println("Enviando a todos los ususairos este mensaje");
						for (String z : listaUsuarios) {
							if(z != "TODOS"){
								enviarDestinatario = new Socket(Usuario_Ip.get(z), 9090);
	
								paqueteReenvio = new ObjectOutputStream(enviarDestinatario.getOutputStream());
	
								paqueteReenvio.writeObject(paqueteRecibido);
								enviarDestinatario.close();
								paqueteReenvio.close();
							}
						}
					}
				}
				socket.close();

				// Ahora creo un flujo de datos que se va a funcionar como medio
				// de transporte
				// DataInputStream flujoEntrada = new
				// DataInputStream(socket.getInputStream());
				// ahora necesito donde guardar la info
				// String mensajeTexto = flujoEntrada.readUTF();
				// areatexto.append("\n" + mensajeTexto);
				// llego el mensaje , asi que cierro la coneccion
			}

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}