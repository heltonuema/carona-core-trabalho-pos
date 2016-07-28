package br.iesb.carona.core.modelo;

public class Usuario {

	private int id;
	private String senha;
	private String nome;
	private String email;
	
	public String getSenha(){
		return senha;
	}
	
	public void setSenha(final String senha){
		this.senha = senha;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public boolean equals(final Object other){
		
		if(other instanceof Usuario){
			Usuario usuario = (Usuario) other;
			return usuario.getEmail().equals(this.getEmail());
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return this.getEmail().hashCode();
	}
	
}
