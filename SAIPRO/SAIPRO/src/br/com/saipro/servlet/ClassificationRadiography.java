package br.com.saipro.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;

import br.com.saipro.model.CaracteristicaMancha;
import br.com.saipro.viewmodel.CaracteristicaManchaViewModel;
import br.com.saipro.viewmodel.ImagemCaracteristicasViewModel;

public class ClassificationRadiography extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String jsonString = (String) (request.getParameter("object"));

			Gson gson = new Gson();
			ImagemCaracteristicasViewModel model = gson.fromJson(jsonString, ImagemCaracteristicasViewModel.class);

			CaracteristicaManchaViewModel modelClassificar = new CaracteristicaManchaViewModel();
			modelClassificar.setCaracteristicaManchas(model.getCaracteristicaManchas());

			System.out.println(gson.toJson(modelClassificar));

			String url = "https://saiproclassifier.azurewebsites.net/rest/classify/instances";
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			String result = null;

			try {
				StringEntity stringEntity = new StringEntity(gson.toJson(modelClassificar));
				httpPost.getRequestLine();
				httpPost.setEntity(stringEntity);

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity entity = httpResponse.getEntity();

				if (entity != null) {

					InputStream instream = entity.getContent();
					result = convertStreamToString(instream);
					instream.close();
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						CaracteristicaManchaViewModel manchasClassificadas = new CaracteristicaManchaViewModel();
						manchasClassificadas = gson.fromJson(result, CaracteristicaManchaViewModel.class);

						List<CaracteristicaMancha> listaManchas = new ArrayList<>();
						listaManchas = manchasClassificadas.getCaracteristicaManchas();

						model.setCaracteristicaManchas(listaManchas);

						response.setContentType("application/json");
						response.setCharacterEncoding("UTF-8");
						response.getWriter().write(gson.toJson(model));
					} else {
						System.out.println("Erro na request!" + httpResponse.getStatusLine().getStatusCode());
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} catch (Exception e) {
			System.out.println("Erro" + e.getMessage());
		}
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
