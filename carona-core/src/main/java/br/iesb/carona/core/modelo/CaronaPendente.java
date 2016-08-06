package br.iesb.carona.core.modelo;

public class CaronaPendente {

	private String solicitante;
	private String aprovador;
	private long idCarona;
	
	public String getSolicitante() {
		return solicitante;
	}
	public void setSolicitante(String solicitante) {
		this.solicitante = solicitante;
	}
	public String getAprovador() {
		return aprovador;
	}
	public void setAprovador(String aprovador) {
		this.aprovador = aprovador;
	}
	public long getIdCarona() {
		return idCarona;
	}
	public void setIdCarona(long idCarona) {
		this.idCarona = idCarona;
	}
	
}
