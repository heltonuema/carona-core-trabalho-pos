package br.iesb.carona.core.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.iesb.carona.core.modelo.Carona;
import br.iesb.carona.core.modelo.CaronaPendente;
import br.iesb.carona.core.modelo.Destino;
import br.iesb.carona.core.modelo.Horario;
import br.iesb.carona.core.modelo.Usuario;

public class CaronaClient {

	private final Usuario usuario;
	private String serverAddress;

	public CaronaClient(final Usuario usuario, final String serverAdress) {
		this.usuario = usuario;
		this.serverAddress = serverAdress;
	}


	/**
	 * Faz o cadastro de um usuário novo no servidor e retorna a mensagem de
	 * erro ou sucesso
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String cadastrarUsuario() throws ClientProtocolException, IOException {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost();
		HttpEntity entity = new StringEntity(new Gson().toJson(usuario), "UTF-8");

		URI uri = URI.create(serverAddress + "/carona-iesb/api/usuarios/inclui");
		post.setEntity(entity);
		post.addHeader("Content-type", "application/json");
		post.setURI(uri);
		HttpResponse response = client.execute(post);

		return new String(CaronaClient.getStringFromInputStream(response.getEntity().getContent()));
	}

	/**
	 * Inluci uma carona no servidor e retorna a carona criada
	 * 
	 * @param pontoPartida
	 * @param horario
	 * @param localDestino
	 * @param bairroDestino
	 * @param maximoPassageiros
	 * @return A carona oferecida
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws RuntimeException
	 *             se o servidor retornar algum status diferente de 200
	 */

	public Carona oferecerCarona(final String pontoPartida, final Horario horario, final String localDestino,
			final String bairroDestino, final int maximoPassageiros) throws ClientProtocolException, IOException {

		Destino destino = new Destino();
		destino.setLocal(localDestino);
		destino.setBairro(bairroDestino);

		Carona carona = new Carona();
		carona.setMotorista(usuario);
		carona.setMaximoPassageiros(maximoPassageiros);
		carona.setPontoDePartida(pontoPartida);
		carona.setHorario(horario);
		carona.setDestino(destino);

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost();
		HttpEntity entity = new StringEntity(new Gson().toJson(carona), "UTF-8");

		URI uri = URI.create(serverAddress + "/carona-iesb/api/caronas/oferecer");
		post.setEntity(entity);
		post.addHeader("Content-type", "application/json");
		post.setURI(uri);
		HttpResponse response = client.execute(post);

		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			throw new RuntimeException(
					"Carona não incluída: " + CaronaClient.getStringFromInputStream(response.getEntity().getContent()));
		}

		return new Gson().fromJson(CaronaClient.getStringFromInputStream(response.getEntity().getContent()),
				Carona.class);
	}

	/**
	 * Faz solicitação para inclusão do usuário em uma carona e retorna a
	 * mensagem devolvida pelo servidor
	 * 
	 * @param idCarona
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String solicitarParticipacaoCarona(final String idCarona) throws ClientProtocolException, IOException {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost();
		URI uri = URI.create(serverAddress + "/carona-iesb/api/caronas/solicitarParticipacao/" + idCarona + "/"
				+ usuario.getEmail());
		post.setURI(uri);
		HttpResponse response = client.execute(post);

		return getStringFromInputStream(response.getEntity().getContent());
	}

	/**
	 * Aprova ou reprova o pedido de carona
	 * 
	 * @param idCaronaPendente
	 * @param aprovar
	 * @return Mensagem devolvida pelo servidor
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String despacharSolicitacaoCarona(final long idCaronaPendente, final boolean aprovar)
			throws ClientProtocolException, IOException {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost();
		URI uri = URI.create(serverAddress + "/carona-iesb/api/caronas/despacharSolicitacao/" + 
							String.valueOf(idCaronaPendente) + "/"+ String.valueOf(aprovar));
		post.setURI(uri);
		HttpResponse response = client.execute(post);

		return getStringFromInputStream(response.getEntity().getContent());
	}

	/**
	 * Cancela um pedido de carona
	 * 
	 * @param idCaronaPendente
	 * @return Mensagem devolvida pelo servidor
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String cancelarSolicitacaoCarona(final long idCaronaPendente) throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost();
		URI uri = URI.create(serverAddress + "/despacharSolicitacao/" + String.valueOf(idCaronaPendente) + "/false");
		post.setURI(uri);
		HttpResponse response = client.execute(post);

		return getStringFromInputStream(response.getEntity().getContent());
	}

	public List<CaronaPendente> consultaSolicitacaoAprovador()
			throws ClientProtocolException, IOException {

		List<CaronaPendente> retorno = new ArrayList<CaronaPendente>();

		URI uri = URI.create(serverAddress + "/carona-iesb/api/caronas/listaSolicitacoes/" + this.usuario.getEmail());

		String caronaJson = getStringFromInputStream(executaRequisicao(new HttpGet(), uri).getEntity().getContent());
		Gson gson = new Gson();

		Type type = new TypeToken<List<CaronaPendente>>() {}.getType();
		retorno.addAll(gson.fromJson(caronaJson, type));

		return retorno;
	}

	public List<Carona> consultaCarona(final String partida, final String localDestino, final String bairroDestino)
			throws ClientProtocolException, IOException {

		List<Carona> retorno = new ArrayList<Carona>();

		String queryParam = new String();
		if (!isNullOrEmpty(partida)) {
			queryParam = queryParam.concat("partida=" + URLEncoder.encode(partida, "UTF-8"));
		}
		if (!isNullOrEmpty(localDestino)) {
			if (!queryParam.isEmpty()) {
				queryParam = queryParam.concat("&");
			}
			queryParam = queryParam.concat("localDestino=" + URLEncoder.encode(localDestino, "UTF-8"));
		}
		if (!isNullOrEmpty(bairroDestino)) {
			if (!queryParam.isEmpty()) {
				queryParam = queryParam.concat("&");
			}
			queryParam = queryParam.concat("bairroDestino=" + URLEncoder.encode(bairroDestino, "UTF-8"));
		}

		URI uri = URI.create(serverAddress + "/carona-iesb/api/caronas/consulta?" + queryParam);

		String caronaJson = getStringFromInputStream(executaRequisicao(new HttpGet(), uri).getEntity().getContent());
		Gson gson = new Gson();

		Type type = new TypeToken<List<Carona>>() {
		}.getType();
		retorno.addAll(gson.fromJson(caronaJson, type));

		return retorno;
	}
	
	private HttpResponse executaRequisicao(final HttpRequestBase method, final URI uri)
			throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		method.setURI(uri);
		return client.execute(method);
	}

	public static boolean isNullOrEmpty(final String string) {
		if (string == null || string.isEmpty()) {
			return true;
		}
		return false;
	}

	public static String getStringFromInputStream(final InputStream inputStream) throws IOException {
		StringBuilder retorno = new StringBuilder();

		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String line = bufferedReader.readLine();
		while (line != null) {
			retorno.append(line);
			line = bufferedReader.readLine();
		}

		inputStreamReader.close();
		bufferedReader.close();

		return retorno.toString();
	}

}
