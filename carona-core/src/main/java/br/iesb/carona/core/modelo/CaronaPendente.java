package br.iesb.carona.core.modelo;

public class CaronaPendente {

	private String solicitante;
	private String aprovador;
	private long idCarona;
	private long idCaronaPendente;
	
	public long getIdCaronaPendente(){
		return this.idCaronaPendente;
	}
	public void setIdCaronaPendente(final long idCaronaPendente){
		this.idCaronaPendente = idCaronaPendente;
	}
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
	
	@Override
	public boolean equals(final Object object){
		if(object instanceof CaronaPendente){
			CaronaPendente caronaCompare = (CaronaPendente) object;
			if((caronaCompare.getAprovador() == null && this.aprovador == null) ||
					caronaCompare.getAprovador().equals(this.aprovador)){
				if((caronaCompare.getSolicitante() == null && this.solicitante == null) || 
						caronaCompare.getSolicitante().equals(this.solicitante)){
					if(caronaCompare.getIdCarona() == this.idCarona){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){

		String hashRetorno = new String();
		if(this.aprovador != null){
			hashRetorno = hashRetorno.concat(this.aprovador);
		}
		else{
			hashRetorno = hashRetorno.concat("null");
		}
		
		if(this.solicitante != null){
			hashRetorno = hashRetorno.concat(this.aprovador);
		}
		else{
			hashRetorno = hashRetorno.concat("null");
		}
		
		hashRetorno = hashRetorno.concat(String.valueOf(this.idCarona));
		
		return hashRetorno.hashCode();
	}
	
}
