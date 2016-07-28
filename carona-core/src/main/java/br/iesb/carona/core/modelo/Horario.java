package br.iesb.carona.core.modelo;

public class Horario {

	private int hora;
	private int minuto;
	
	public Horario(final int hora, final int minuto){
		this.hora = hora;
		this.minuto = minuto;
	}
	
	public int getMinuto() {
		return minuto;
	}
	public void setMinuto(int minuto) {
		this.minuto = minuto;
	}
	public int getHora() {
		return hora;
	}
	public void setHora(int hora) {
		this.hora = hora;
	}
	
}
