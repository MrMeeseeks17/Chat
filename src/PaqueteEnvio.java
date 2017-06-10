import java.io.Serializable;
import java.util.ArrayList;

public class PaqueteEnvio implements Serializable{
	private String nick, ip,mensaje,usuarioQueRecibeMensaje;
	public String getUsuarioQueRecibeMensaje() {
		return usuarioQueRecibeMensaje;
	}

	public void setUsuarioQueRecibeMensaje(String usuarioQueRecibeMensaje) {
		this.usuarioQueRecibeMensaje = usuarioQueRecibeMensaje;
	}

	private ArrayList Usuarios;
	private int se�al;
	
	public int getSe�al() {
		return se�al;
	}

	public void setSe�al(int se�al) {
		this.se�al = se�al;
	}

	static final int PRIMERACONEXION = 0; // me desconecto del servidor
	static final int ONLINE = 1; //ya estoy online en el servidor
	static final int DESCONEXION =2;//se�al de desconexion
	static final int TODOS=5;//para enviar a todos los usuarios el mensaje

	public ArrayList getUsuarios() {
		return Usuarios;
	}

	public void setUsuarios(ArrayList usuarios) {
		Usuarios = usuarios;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
}
