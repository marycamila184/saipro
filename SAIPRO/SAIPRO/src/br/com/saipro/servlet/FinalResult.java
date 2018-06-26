package br.com.saipro.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import com.google.gson.Gson;

import br.com.saipro.model.CaracteristicaMancha;
import br.com.saipro.viewmodel.ImagemCaracteristicasViewModel;

/**
 * Servlet implementation class FinalResult
 */
@MultipartConfig
@WebServlet("/FinalResult")
public class FinalResult extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {

			Gson gson = new Gson();
			ImagemCaracteristicasViewModel model = gson.fromJson((String) request.getParameter("object"),
					ImagemCaracteristicasViewModel.class);

			byte dearr[] = Base64.decodeBase64(model.getImagem().replace(" ", "+"));
			FileOutputStream fos = new FileOutputStream(new File("imagem.jpeg"));
			fos.write(dearr);
			fos.close();

			Mat source = Highgui.imread("imagem.jpeg", Highgui.CV_LOAD_IMAGE_COLOR);

			// Colocando os contornos
			for (CaracteristicaMancha caracteristica : model.getCaracteristicaManchas()) {
				if (caracteristica.isAnomalia()) {
					for (int i = 0; i < source.rows(); i++) {
						for (int j = 0; j < source.cols(); j++) {
							// Acho a posição x e y
							if (i == caracteristica.getPosicaoY() && j == caracteristica.getPosicaoX()) {

								// For para colorir
								for (int k = 0; k < caracteristica.getAltura(); k++) {
									for (int l = 0; l < caracteristica.getLargura(); l++) {
										if (k <= 2 || (k >= caracteristica.getAltura() - 2)) {
											source.put(i + k, j + l, new double[] { 0, 255, 255 });
										}

										if (l <= 2 || (l >= caracteristica.getLargura() - 2)) {
											source.put(i + k, j + l, new double[] { 0, 255, 255 });
										}
									}
								}
							}
						}
					}
				}
			}

			Highgui.imwrite("imagemfinalprocessada.jpeg", source);

			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", "attachment; filename=\"imagemfinalprocessada.jpeg\"");

			FileInputStream fileInputStream = new FileInputStream("imagemfinalprocessada.jpeg");

			PrintWriter out = response.getWriter();
			int i;
			while ((i = fileInputStream.read()) != -1) {
				out.write(i);
			}
			fileInputStream.close();
			out.close();
		} catch (Exception e) {
			System.out.println("Erro " + e.getMessage());
			System.out.println("Erro " + e.getCause());
		}

	}

}
