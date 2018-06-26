package br.com.saipro.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 * Servlet implementation class UploadServlet
 */
@MultipartConfig
public class SegmentMandibleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		int resultadoMedia = 0;

		Part file = request.getPart("file");

		InputStream filecontent = file.getInputStream();

		File image = new File("imagem-analise.jpg");
		FileOutputStream outStream = new FileOutputStream(image);
		copyStream(filecontent, outStream);
		outStream.close();

		Mat source = Highgui.imread(image.getAbsolutePath(), Highgui.CV_LOAD_IMAGE_GRAYSCALE);

		double limiar = 0;
		int numPixel = 0;

		// Verificar caso da panoramica 7 juntamente as bordas por
		// enquanto a
		// solução foi retirar alguns pixels manualmente
		Mat destination = new Mat(source.rows(), source.cols(), source.type());

		Imgproc.equalizeHist(source, destination);
		// Highgui.imwrite("resultado/Histograma-" + name[0] +
		// ".jpg",
		// destination);

		// Retirar bordas para entrar na segmentação e sairem
		for (int i = 0; i < destination.rows(); i++) {
			for (int j = 0; j < destination.cols(); j++) {
				double[] pixel = destination.get(i, j);
				if (pixel[0] <= 50) {
					destination.put(i, j, 51);
				}
			}
		}
		// Highgui.imwrite("resultado/Bordas-" + name[0] + ".jpg",
		// destination);

		// Acho a media para segmentação
		// Procurar a jutificativa no artigo do pulmão
		for (int i = 0; i < source.rows(); i++) {
			for (int j = 0; j < source.cols(); j++) {
				double[] testeLimiar = source.get(i, j);
				if (testeLimiar[0] >= 50 && testeLimiar[0] <= 250) {
					limiar = limiar + testeLimiar[0];
					numPixel++;
				}
			}
		}
		resultadoMedia = (int) limiar / numPixel;

		// Procurar justificativa para metade do exame
		Mat part1 = new Mat(destination.rows(), destination.cols() / 2, destination.type());
		// Pegar metade de imagem - parte 1
		for (int i = 0; i < part1.rows(); i++) {
			for (int j = 0; j < part1.cols(); j++) {
				double[] posicao = destination.get(i, j);
				part1.put(i, j, posicao);
			}
		}
		// Highgui.imwrite("resultado/Metade-" + name[0] + ".jpg",
		// part1);

		// Verificar o tamanho da matriz para gauss
		Imgproc.GaussianBlur(part1, part1, new Size(45, 45), 0);
		// Highgui.imwrite("resultado/Gaussian-" + name[0] + ".jpg",
		// part1);

		// Incrementar ate nao encontrar a posição X e Y para corte
		// (desta forma
		// fica dinamico de exame para exame)
		int contraste = 1;

		// Posição Final para corte
		int posXLateral = 0;
		int posYLateral = 0;

		// Distância dos pixels pretos
		// Inicializo a variavel com o tamanho total da linha
		int distanciaPixelsPretos = part1.cols();
		int pixelLonge = 0;

		// Variaveis para comparação

		int posXLateralR = 0;
		int posYLateralR = 0;
		int distanciaPixelsPretosR = part1.cols();
		int pixelLongeR = 0;

		// Laço para contraste dinamico
		do {
			contraste++;

			posXLateralR = 0;
			posYLateralR = 0;
			distanciaPixelsPretosR = part1.cols();
			pixelLongeR = 0;

			Mat metade = new Mat(part1.rows(), part1.cols(), part1.type());
			for (int i = 0; i < metade.rows(); i++) {
				for (int j = 0; j < metade.cols(); j++) {
					double[] testeContraste = part1.get(i, j);
					metade.put(i, j, testeContraste[0] * contraste);
				}
			}

			Imgproc.threshold(metade, metade, resultadoMedia, 255, Imgproc.THRESH_BINARY);

			// Encontro a região lateral para segmentação
			// Critérios para encontrar a região laterial para corte
			// na
			// mandíbula
			// 1 - Sair da borda da esquerda pois e o lado mais
			// perto da
			// mandibula e de uma linha com pixel branco
			// 2 - Verificar a menor distância do primeiro pixel
			// preto
			// até o
			// próximo
			// pixel branco
			// 3 - Verificar qual pixel preto é o mais longe da
			// borda
			// branca.

			// -------------------------------------------- CORTE
			// LATERAL
			// ---------------------------------------------------
			for (int i = 0; i < metade.rows(); i++) {
				// Verificar se esta na metade para baixo da imagem
				if (i >= metade.rows() / 2) {
					double[] borda = metade.get(i, 0);

					// Acho a borda esquerda branca para começar o
					// algoritmo
					if (borda[0] == 255) {

						// Posição do primeiro e do segundo X para
						// comparação da
						// distância
						int posX1 = 0;
						int posX2 = 0;

						// Percorro a linha com a borda branca
						for (int k = 0; k < metade.cols(); k++) {
							double[] pixelLinhaBordaBranca = metade.get(i, k);

							if (posX1 == 0 && pixelLinhaBordaBranca[0] == 0) {
								posX1 = k;
							} else if (posX1 != 0 && posX2 == 0 && pixelLinhaBordaBranca[0] == 255) {
								posX2 = k;
							}

							// Verificação de distância mais longe e
							// de
							// menor
							// caminho de pretos
							if (posX1 != 0 && posX2 != 0 && distanciaPixelsPretosR > posX2 - posX1
									&& pixelLongeR < posX1 && k < metade.cols() / 3) {
								// Verificação do mais longe
								posXLateralR = posX1;
								posYLateralR = i;
								distanciaPixelsPretosR = posX2 - posX1;
								pixelLongeR = posX1;
							}
						}
					}
				}
			}

			// Atribuo o ultimo valor diferente de 0 as minhas
			// variaveis
			// finais
			if (posXLateralR != 0 && posYLateralR != 0) {
				posXLateral = posXLateralR;
				posYLateral = posYLateralR;
				distanciaPixelsPretos = distanciaPixelsPretosR;
				pixelLonge = pixelLongeR;
			}
		} while (posXLateralR != 0 && posYLateralR != 0);

		for (int i = 0; i < part1.rows(); i++) {
			for (int j = 0; j < part1.cols(); j++) {
				double[] testeContraste = part1.get(i, j);
				part1.put(i, j, testeContraste[0] * (contraste - 1));
			}
		}
		// Highgui.imwrite("resultado/Contraste-" + name[0] +
		// ".jpg",
		// part1);

		Imgproc.threshold(part1, part1, resultadoMedia, 255, Imgproc.THRESH_BINARY);
		// Highgui.imwrite("resultado/Segmentacao-" + name[0] +
		// ".jpg", part1);

		System.out.println(
				" ---------------------------- Informações da imagem ---------------------------- ");
		System.out.println("Media para segmentação: " + resultadoMedia);
		System.out.println("Metade da imagemm em colunas: " + part1.cols() / 2);
		System.out.println("Metade da imagem em linhas: " + part1.rows() / 2);
		System.out.println("Valor do ultimo contraste aplicado com resultados: " + (contraste - 1));

		System.out.println(
				" ---------------------------- Informações de corte lateral da imagem ---------------------------- ");
		System.out.println("Pixel mais longe com a menor distancia: " + pixelLonge);
		System.out.println("Menor distancia: " + distanciaPixelsPretos);
		System.out.println("Posição X para corte: " + posXLateral);
		System.out.println("Posição Y para corte: " + posYLateral);

		// -------------------------------------------- CORTE
		// INFERIOR E
		// SUPERIOR
		// ---------------------------------------------------

		// Posição Final para corte
		int posYInferior = 0;
		int posYSuperior = 0;
		int distanciaPixelsPretosInferior = 0;
		int distanciaPixelsPretosSuperior = 0;

		// Encontro a região de cima para segmentação
		// Critérios para encontrar a região de cima para corte na
		// mandíbula
		// 1 - Sair da borda direita da imagem de uma linha com
		// pixel
		// branco
		// 2 - Verificar a maior distância do primeiro pixel preto
		// até o
		// próximo pixel branco

		// Encontro a região de baixo para segmentação
		// Critérios para encontrar a região laterial para corte na
		// mandíbula
		// 1 - Sair da borda direita da imagem de uma linha com
		// pixel
		// branco
		// 2 - Verificar a maior distância do primeiro pixel preto
		// até o
		// próximo pixel branco

		// Outro laço para o corte inferior e superior percorrendo a
		// imagem
		// da direita para a esquerda
		for (int i = 0; i < part1.rows(); i++) {

			// Posição do primeiro e do segundo X para
			// comparação da
			// distância
			int distComp = 0;
			int distCompBot = 0;

			// Percorro a linha com a borda branca
			for (int k = part1.cols() - 1; k >= 0; k--) {
				double[] pixelLinha = part1.get(i, k);

				// Verificar se esta na metade para cima da imagem
				if (i <= part1.rows() / 2) {

					// Utilizado para comparação superior que
					// percorre
					// ate o
					// final da linha
					if (pixelLinha[0] == 0) {
						distComp++;
					} else {
						distComp = 0;
					}

					// Verificação de maior caminho de pretos
					// superior
					// Retirar o 10 e estabalecer um parametro de
					// corte
					// na linha
					if (distComp != 0 && distanciaPixelsPretosSuperior < distComp && k > part1.cols() / 2 && i > 10) {
						posYSuperior = i;
						distanciaPixelsPretosSuperior = distComp;
					}
					// Caso esteja na metade para baixo
				} else {

					// Utilizado para comparação superior que
					// percorre
					// ate o
					// final da linha
					if (pixelLinha[0] == 0) {
						distCompBot++;
					} else {
						distCompBot = 0;
					}

					// Verificação de maior caminho de pretos
					// superior
					// Retirar o 10 e estabalecer um parametro de
					// corte
					// na linha
					if (distCompBot != 0 && distanciaPixelsPretosInferior < distCompBot) {
						posYInferior = i;
						distanciaPixelsPretosInferior = distCompBot;
					}
				}
			}
		}

		// Achar o corte de imagem com as coordenadas encontradas
		for (int i = 0; i < part1.rows(); i++) {
			for (int j = 0; j < part1.cols(); j++) {
				if (i == posYSuperior) {
					part1.put(i, j, 0);
				}
				if (i == posYInferior) {
					part1.put(i, j, 0);
				}
				if (j == posXLateral) {
					part1.put(i, j, 0);
				}
			}
		}

		System.out.println(
				" ---------------------------- Informações de corte superior da imagem ---------------------------- ");
		System.out.println("Maior distancia: " + distanciaPixelsPretosSuperior);
		System.out.println("Posição Y para corte: " + posYSuperior);

		System.out.println(
				" ---------------------------- Informações de corte inferior da imagem ---------------------------- ");
		System.out.println("Maior distancia: " + distanciaPixelsPretosInferior);
		System.out.println("Posição Y para corte: " + posYInferior);

		int posXLateralDir = source.cols() - posXLateral;

		for (int i = 0; i < source.rows(); i++) {
			for (int j = 0; j < source.cols(); j++) {
				if (i == posYSuperior || i == posYInferior) {
					source.put(i, j, 0);
				}
				if (j == posXLateral) {
					source.put(i, j, 0);
				}
				if (j == posXLateralDir) {
					source.put(i, j, 0);
				}
			}
		}
		// Parte final do algoritmo de segmentação de mandíbula que
		// retorna a mandibula cortada em uma nova imagem

		int qtdeCols = source.cols() - (2 * posXLateral);
		int qtdeRows = source.rows() - (posYSuperior + (source.rows() - posYInferior));

		Rect rectCrop = new Rect(posXLateral, posYSuperior, qtdeCols, qtdeRows);
		Mat imagemResultado = new Mat(source, rectCrop);

		// ----------------------------------------- FIM DO CORTE DE
		// MANDIBULA ------------------------------
		// RESULTADO FINAL

		Highgui.imwrite("imagem-analise.jpg", imagemResultado);

		response.setContentType("APPLICATION/OCTET-STREAM");
		response.setHeader("Content-Disposition", "attachment; filename=\"imagem-analise.jpg\"");

		FileInputStream fileInputStream = new FileInputStream("imagem-analise.jpg");

		PrintWriter out = response.getWriter();
		int i;
		while ((i = fileInputStream.read()) != -1) {
			out.write(i);
		}
		fileInputStream.close();
		out.close();
	}

	public static void copyStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

}
