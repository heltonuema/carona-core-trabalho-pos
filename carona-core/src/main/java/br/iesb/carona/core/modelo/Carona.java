package br.iesb.carona.core.modelo;

import java.util.ArrayList;
import java.util.List;

public class Carona {

	private final List<Usuario> passageiros = new ArrayList<Usuario>();
	private int maximoPassageiros;
	private Usuario motorista;
	private Horario horario;
	private String pontoDePartida;
	private Destino destino;
	
	public List<Usuario> getPassageiros(){
		return this.passageiros;
	}
	
	public void incluiCaroneiro(final Usuario caroneiro) throws RuntimeException { 
		if(passageiros.contains(caroneiro)){
			throw new RuntimeException("Passageiro já participa da carona");
		}
		if(!(passageiros.size() < maximoPassageiros)){
			throw new RuntimeException("Carona já estå lotada");
		}
		passageiros.add(caroneiro);
	}
	
	public int getMaximoPasageiros(){
		return this.maximoPassageiros;
	}
	
	public Usuario getMotorista(){
		return this.motorista;
	}
	
	public Horario getHorario(){
		return this.horario;
	}
	
	public String getPontoDePartida(){
		return this.pontoDePartida;
	}
	
	public Destino getDestino(){
		
		return this.destino;
	}
	
}
