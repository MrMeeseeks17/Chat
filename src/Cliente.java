import javax.swing.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class Cliente {

	public static void main(String[] args) {
		MarcoCliente mimarco = new MarcoCliente();
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

class MarcoCliente extends JFrame {
	public MarcoCliente() {
		setBounds(600, 300, 280, 350);

		String nickUsuario = JOptionPane.showInputDialog("Ingrese el nick");

		LaminaMarcoCliente milamina = new LaminaMarcoCliente(nickUsuario);
		add(milamina);
		setVisible(true);

		addWindowListener(new EnvioOnline(nickUsuario));
	}

	// ----------------- ENVIO DE SEÑAL ONLINE -------------------------//
	class EnvioOnline extends WindowAdapter {
		String nick;

		public EnvioOnline(String nickParam) {
			this.nick = nickParam;
		}

		// Aca se ejecuta todo al abrir la ventana
		public void windowOpened(WindowEvent e) {
			try {
				Socket socket = new Socket("192.168.0.105", 9999);
				PaqueteEnvio datos = new PaqueteEnvio();
				datos.setSeñal(datos.PRIMERACONEXION);
				datos.setNick(nick);
				ObjectOutputStream paqueteDatos = new ObjectOutputStream(socket.getOutputStream());
				paqueteDatos.writeObject(datos);
				socket.close();
			} catch (Exception e2) {
			}
		}
	}

	// -------------------------------------------------------------------//
	class LaminaMarcoCliente extends JPanel implements Runnable {

		private Hashtable<String, String> usuarios_Chats;

		public LaminaMarcoCliente(String nickUsuario) {
			JLabel nnick = new JLabel("nick:");
			add(nnick);

			nick = new JLabel();
			// nick.setText("Programmerz");
			nick.setText(nickUsuario);
			add(nick);

			JLabel texto = new JLabel("Online:");
			add(texto);

			ComboUsuarios = new JComboBox<String>();
			add(ComboUsuarios);

			CambiarChat EventoCambiarChat = new CambiarChat();
			ComboUsuarios.addActionListener(EventoCambiarChat);

			campoChat = new JTextArea(12, 20);
			add(campoChat);

			campo1 = new JTextField(20);

			add(campo1);

			btnEnviarMSJ = new JButton("Enviar");

			EnviaTexto mievento = new EnviaTexto();
			btnEnviarMSJ.addActionListener(mievento);
			add(btnEnviarMSJ);

			btnCerrarSesion = new JButton("Cerrar Sesion");

			FinalizarSesion EventoFinalizarSession = new FinalizarSesion();

			btnCerrarSesion.addActionListener(EventoFinalizarSession);
			add(btnCerrarSesion);

			Thread miHilo = new Thread(this);
			miHilo.start();

		}

		// como es una clase que se va a encargar de gestionar eventos del
		// boton, debe implementar
		// action listener
		private class EnviaTexto implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (ComboUsuarios.getSelectedItem().toString() == "TODOS") {
					try {
						Socket socket = new Socket("192.168.0.105", 9999);
						PaqueteEnvio datos = new PaqueteEnvio();

						datos.setNick(nick.getText());
						datos.setIp(ComboUsuarios.getSelectedItem().toString());
						datos.setMensaje(campo1.getText());
						datos.setSeñal(datos.TODOS);
						ObjectOutputStream pequeteDeDatos = new ObjectOutputStream(socket.getOutputStream());

						pequeteDeDatos.writeObject(datos);

						socket.close();

					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						// ip mal , desconectada, error en la red, etc
						System.out.println(e.getMessage());
					}
				} else {
					try {
						Socket socket = new Socket("192.168.0.105", 9999);
						PaqueteEnvio datos = new PaqueteEnvio();

						datos.setNick(nick.getText());
						datos.setIp(ComboUsuarios.getSelectedItem().toString());
						datos.setMensaje(campo1.getText());

						datos.setSeñal(datos.TODOS);
						ObjectOutputStream pequeteDeDatos = new ObjectOutputStream(socket.getOutputStream());

						pequeteDeDatos.writeObject(datos);

						socket.close();

					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						// ip mal , desconectada, error en la red, etc
						System.out.println(e.getMessage());
					}
				}
			}
		}

		// como es una clase que se va a encargar de gestionar eventos del
		// boton,
		// debe implementar
		private class FinalizarSesion implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Socket socket = new Socket("192.168.0.105", 9999);
					PaqueteEnvio datos = new PaqueteEnvio();
					datos.setSeñal(datos.DESCONEXION);
					datos.setNick(nick.getText());
					ObjectOutputStream paqueteDatos = new ObjectOutputStream(socket.getOutputStream());
					paqueteDatos.writeObject(datos);
					socket.close();
					System.exit(0);
				} catch (Exception e2) {
				}
			}
		}

		private class CambiarChat implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					campoChat.setText("");
					campoChat.append(usuarios_Chats.get(ComboUsuarios.getSelectedItem()));
				} catch (Exception e2) {
				}
			}
		}

		private JTextField campo1;

		private JComboBox<String> ComboUsuarios;

		private JLabel nick;
		private JTextArea campoChat, txtIpServidor;
		private JButton btnEnviarMSJ, btnCerrarSesion, btnPropiedades;

		@Override
		public void run() {
			usuarios_Chats = new Hashtable<String, String>();

			usuarios_Chats.put("TODOS", "");

			try {
				ServerSocket servidor_cliente = new ServerSocket(9090);

				Socket cliente;

				PaqueteEnvio paqueteRecibido;

				while (true) {
					cliente = servidor_cliente.accept();

					ObjectInputStream flujoEntrada = new ObjectInputStream(cliente.getInputStream());

					paqueteRecibido = (PaqueteEnvio) flujoEntrada.readObject();
					if (paqueteRecibido.getSeñal() == PaqueteEnvio.ONLINE) {
						campoChat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());

						String nick = paqueteRecibido.getNick();

						String mensaje = paqueteRecibido.getMensaje();

						String UserQueRecibeMensaje = paqueteRecibido.getIp();

						if (usuarios_Chats.get(UserQueRecibeMensaje) != null) {
							usuarios_Chats.put(UserQueRecibeMensaje,
									usuarios_Chats.get(UserQueRecibeMensaje) + nick + ":" + mensaje + "\n");
						} else {
							usuarios_Chats.put(UserQueRecibeMensaje, "");
							usuarios_Chats.put(UserQueRecibeMensaje,
									usuarios_Chats.get(nick) + nick + ":" + mensaje + "\n");
						}
					} else {
						if (paqueteRecibido.getSeñal() == PaqueteEnvio.PRIMERACONEXION) {
							// System.out.println("Si ES la primera coneccion
							// del usuario CLIENTE");
							// campoChat.append("\n" + paqueteRecibido.getIp());

							ArrayList<String> UsuariosMenu = new ArrayList<String>();

							UsuariosMenu = paqueteRecibido.getUsuarios();
							ComboUsuarios.removeAllItems();
							for (String z : UsuariosMenu) {
								ComboUsuarios.addItem(z);
							}
						}
					}
					if (paqueteRecibido.getSeñal() == PaqueteEnvio.TODOS) {

						String nick = paqueteRecibido.getNick();

						String mensaje = paqueteRecibido.getMensaje();

						String UserQueRecibeMensaje = paqueteRecibido.getIp();

							usuarios_Chats.put(
											"TODOS",
											usuarios_Chats.get(nick) + 
											nick + ":" + mensaje + "\n");
							
						if(ComboUsuarios.getSelectedItem().toString() == "TODOS"){
							campoChat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
						}
					}
					flujoEntrada.close();
					cliente.close();
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}
	}
}