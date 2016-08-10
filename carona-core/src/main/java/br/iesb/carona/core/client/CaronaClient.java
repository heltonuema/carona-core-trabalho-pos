package br.iesb.carona.core.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

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

	public static final String COMO_SOLICITANTE = "/carona-iesb/api/caronas/listaAguardandoAprovacao/";
	public static final String COMO_APROVADOR = "/carona-iesb/api/caronas/listaSolicitacoes/";
	
	public CaronaClient(final Usuario usuario, final String serverAdress) {
		this.usuario = usuario;
		this.serverAddress = serverAdress;
	}


	/**
	 * Faz o cadastro de um usuário novo no servidor e retorna a mensagem de
	 * erro ou sucesso
	 * @throws Exception 
	 */
	public String cadastrarUsuario() throws Exception {

		URI uri = URI.create(serverAddress + "/carona-iesb/api/usuarios/inclui");
		return new String(CaronaClient.getStringFromInputStream(doPost(uri, new Gson().toJson(usuario))));
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
			final String bairroDestino, final int maximoPassageiros) throws Exception {

		Destino destino = new Destino();
		destino.setLocal(localDestino);
		destino.setBairro(bairroDestino);

		Carona carona = new Carona();
		carona.setMotorista(usuario);
		carona.setMaximoPassageiros(maximoPassageiros);
		carona.setPontoDePartida(pontoPartida);
		carona.setHorario(horario);
		carona.setDestino(destino);

		URI uri = URI.create(serverAddress + "/carona-iesb/api/caronas/oferecer");

		String retorno = getStringFromInputStream(doPost(uri, new Gson().toJson(carona)));
		return new Gson().fromJson(retorno, Carona.class);
	}
	
	/**
	 * Faz solicitação para inclusão do usuário em uma carona e retorna a
	 * mensagem devolvida pelo servidor
	 * 
	 * @param idCarona
	 * @return
	 * @throws Exception 
	 */
	public String solicitarParticipacaoCarona(final String idCarona) throws Exception {

		
		URI uri = URI.create(serverAddress + "/carona-iesb/api/caronas/solicitarParticipacao/" + idCarona + "/"
				+ usuario.getEmail());
		
		return getStringFromInputStream(doPost(uri,""));
	}

	/**
	 * Aprova ou reprova o pedido de carona
	 * 
	 * @param idCaronaPendente
	 * @param aprovar
	 * @return Mensagem devolvida pelo servidor
	 * @throws Exception 
	 */
	public String despacharSolicitacaoCarona(final long idCaronaPendente, final boolean aprovar)
			throws Exception {

		URI uri = URI.create(serverAddress + "/carona-iesb/api/caronas/despacharSolicitacao/"
				+ String.valueOf(idCaronaPendente) + "/" + String.valueOf(aprovar));

		return getStringFromInputStream(doPost(uri, ""));
	}

	/**
	 * Cancela um pedido de carona
	 * 
	 * @param idCaronaPendente
	 * @return Mensagem devolvida pelo servidor
	 * @throws Exception 
	 */
	public String cancelarSolicitacaoCarona(final long idCaronaPendente) throws Exception {
		
		URI uri = URI.create(serverAddress + "/despacharSolicitacao/" + String.valueOf(idCaronaPendente) + "/false");
		
		return getStringFromInputStream(doPost(uri,""));
	}

	public List<CaronaPendente> consultaSolicitacaoPendente(final String solicitanteOuAprovador) throws Exception {

		List<CaronaPendente> retorno = new ArrayList<CaronaPendente>();

		URI uri = URI.create(serverAddress + solicitanteOuAprovador + this.usuario.getEmail());

		String caronaJson = getStringFromInputStream(doGet(uri));
		Gson gson = new Gson();

		Type type = new TypeToken<List<CaronaPendente>>() {
		}.getType();
		retorno.addAll(gson.fromJson(caronaJson, type));

		return retorno;
	}

	public List<Carona> consultaCarona(final String partida, final String localDestino, final String bairroDestino, final boolean somenteDisponiveis)
			throws Exception {

		List<Carona> retorno = new ArrayList<Carona>();

		String queryParam = "somenteDisponiveis=" + String.valueOf(somenteDisponiveis);
		if (!isNullOrEmpty(partida)) {
			queryParam = queryParam.concat("&partida=" + URLEncoder.encode(partida, "UTF-8"));
		}
		if (!isNullOrEmpty(localDestino)) {
			queryParam = queryParam.concat("&localDestino=" + URLEncoder.encode(localDestino, "UTF-8"));
		}
		if (!isNullOrEmpty(bairroDestino)) {
			queryParam = queryParam.concat("&bairroDestino=" + URLEncoder.encode(bairroDestino, "UTF-8"));
		}

		URI uri = URI.create(serverAddress + "/carona-iesb/api/caronas/consulta?" + queryParam);

		String caronaJson = getStringFromInputStream(doGet(uri));
		Gson gson = new Gson();

		Type type = new TypeToken<List<Carona>>() {
		}.getType();
		retorno.addAll(gson.fromJson(caronaJson, type));

		return retorno;
	}

	public static boolean isNullOrEmpty(final String string) {
		if (string == null || string.isEmpty()) {
			return true;
		}
		return false;
	}

	private InputStream doGet(final URI uri)
			throws Exception {
		URL url = uri.toURL();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
		} finally {
			conn.disconnect();
		}

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			
			throw new RuntimeException(conn.getResponseMessage());
		}

		return conn.getInputStream();
	}
	
	private InputStream doPost(final URI uri, final String content)throws Exception{
		URL url = uri.toURL();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestProperty("Content-type", "application/json");
			conn.setDoOutput(true);
			
			OutputStream outputStream = conn.getOutputStream();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
			bw.write(content);
			bw.flush();
			bw.close();
			conn.connect();
		} finally {
			conn.disconnect();
		}

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			
			throw new RuntimeException(
					"Carona não incluída");
		}

		return conn.getInputStream();
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
