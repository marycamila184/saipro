import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class HaarCascade {

	public static String returnNameFile(File file) {
		if (file.isDirectory()) {
			return "Erro: Imagem n�o encontrada!";
		} else {
			return file.getName();
		}
	}

	public static void main(String[] args) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		int resultadoMedia = 0;

		// Cria�ao do CSV
		PrintWriter pw = new PrintWriter(new File("atributos-mancha.csv"));
		String fileCSV = "largura,altura,posicaoX-relativa,posicaoY-relativa,entropia,segundo-momento-ang,correlacao,"
				+ "energia,media,variancia,desvio-padrao,contraste,homogeneidade,dissimilaridade,media-tons-cinza,class";
		fileCSV += "\n";

		System.out.println(fileCSV);
		// Carrego todas as imagens
		File[] files = new File("imagens").listFiles();

		// Fa�o os testes imagem por imagem
		for (File file : files) {
			String completeName = returnNameFile(file);
			if ((completeName.toLowerCase().contains(".jpg") || completeName.toLowerCase().contains(".png")
					|| completeName.toLowerCase().contains(".jpeg") || completeName.toLowerCase().contains(".jpeg"))
					&& !completeName.contains("Erro")) {

				if (completeName.contains("panoramica8")) {

					double limiar = 0;
					int numPixel = 0;

					String name[] = completeName.split("\\.");

					// Verificar caso da panoramica 7 juntamente as bordas por
					// enquanto a
					// solu��o foi retirar alguns pixels manualmente
					Mat source = Highgui.imread("imagens/" + completeName, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
					Mat destination = new Mat(source.rows(), source.cols(), source.type());

					Imgproc.equalizeHist(source, destination);
					Highgui.imwrite("resultado/Histograma-" + name[0] + ".jpg", destination);

					// Retirar bordas para entrar na segmenta��o e sairem
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

					// Acho a media para segmenta��o
					// Procurar a jutificativa no artigo do pulm�o
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
					Highgui.imwrite("resultado/Metade-" + name[0] + ".jpg", part1);

					// Verificar o tamanho da matriz para gauss
					Imgproc.GaussianBlur(part1, part1, new Size(45, 45), 0);
					Highgui.imwrite("resultado/Gaussian-" + name[0] + ".jpg", part1);

					// Incrementar ate nao encontrar a posi��o X e Y para corte
					// (desta forma
					// fica dinamico de exame para exame)
					int contraste = 1;

					// Posi��o Final para corte
					int posXLateral = 0;
					int posYLateral = 0;

					// Dist�ncia dos pixels pretos
					// Inicializo a variavel com o tamanho total da linha
					int distanciaPixelsPretos = part1.cols();
					int pixelLonge = 0;

					// Variaveis para compara��o

					int posXLateralR = 0;
					int posYLateralR = 0;
					int distanciaPixelsPretosR = part1.cols();
					int pixelLongeR = 0;

					// La�o para contraste dinamico
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
						
						Highgui.imwrite("resultado/THRES-" +contraste+ "-" + name[0] + ".jpg", metade);

						Imgproc.threshold(metade, metade, resultadoMedia, 255, Imgproc.THRESH_BINARY);

						// Encontro a regi�o lateral para segmenta��o
						// Crit�rios para encontrar a regi�o laterial para corte
						// na
						// mand�bula
						// 1 - Sair da borda da esquerda pois e o lado mais
						// perto da
						// mandibula e de uma linha com pixel branco
						// 2 - Verificar a menor dist�ncia do primeiro pixel
						// preto
						// at� o
						// pr�ximo
						// pixel branco
						// 3 - Verificar qual pixel preto � o mais longe da
						// borda
						// branca.

						// -------------------------------------------- CORTE
						// LATERAL
						// ---------------------------------------------------
						for (int i = 0; i < metade.rows(); i++) {
							// Verificar se esta na metade para baixo da imagem
							if (i >= metade.rows() / 2) {
								double[] borda = metade.get(i, 0);

								// Acho a borda esquerda branca para come�ar o
								// algoritmo
								if (borda[0] == 255) {

									// Posi��o do primeiro e do segundo X para
									// compara��o da
									// dist�ncia
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

										// Verifica��o de dist�ncia mais longe e
										// de
										// menor
										// caminho de pretos
										if (posX1 != 0 && posX2 != 0 && distanciaPixelsPretosR > posX2 - posX1
												&& pixelLongeR < posX1 && k < metade.cols() / 3) {
											// Verifica��o do mais longe
											posXLateralR = posX1;
											posYLateralR = i;
											distanciaPixelsPretosR = posX2 - posX1;
											pixelLongeR = posX1;
										}
									}
								}
							}
						}
						
						Highgui.imwrite("resultado/BIN-" +contraste+ "-" + name[0] + ".jpg", metade);

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
					Highgui.imwrite("resultado/Contraste-" + name[0] + ".jpg", part1);

					Imgproc.threshold(part1, part1, resultadoMedia, 255, Imgproc.THRESH_BINARY);
					Highgui.imwrite("resultado/Segmentacao-" + name[0] + ".jpg", part1);

					System.out.println(" ---------------------------- Informa��es da imagem " + name[0]
							+ " ---------------------------- ");
					// System.out.println("Media para segmenta��o: " +
					// resultadoMedia);
					// System.out.println("Metade da imagemm em colunas: " +
					// part1.cols() / 2);
					// System.out.println("Metade da imagem em linhas: " +
					// part1.rows() / 2);
					// System.out.println("Valor do ultimo contraste aplicado
					// com resultados: " + (contraste - 1));

					System.out.println(
							" ---------------------------- Informa��es de corte lateral da imagem ---------------------------- ");
					// System.out.println("Pixel mais longe com a menor
					// distancia: " + pixelLonge);
					// System.out.println("Menor distancia: " +
					// distanciaPixelsPretos);
					// System.out.println("Posi��o X para corte: " +
					// posXLateral);
					// System.out.println("Posi��o Y para corte: " +
					// posYLateral);

					// -------------------------------------------- CORTE
					// INFERIOR E
					// SUPERIOR
					// ---------------------------------------------------

					// Posi��o Final para corte
					int posYInferior = 0;
					int posYSuperior = 0;
					int distanciaPixelsPretosInferior = 0;
					int distanciaPixelsPretosSuperior = 0;

					// Encontro a regi�o de cima para segmenta��o
					// Crit�rios para encontrar a regi�o de cima para corte na
					// mand�bula
					// 1 - Sair da borda direita da imagem de uma linha com
					// pixel
					// branco
					// 2 - Verificar a maior dist�ncia do primeiro pixel preto
					// at� o
					// pr�ximo pixel branco

					// Encontro a regi�o de baixo para segmenta��o
					// Crit�rios para encontrar a regi�o laterial para corte na
					// mand�bula
					// 1 - Sair da borda direita da imagem de uma linha com
					// pixel
					// branco
					// 2 - Verificar a maior dist�ncia do primeiro pixel preto
					// at� o
					// pr�ximo pixel branco

					// Outro la�o para o corte inferior e superior percorrendo a
					// imagem
					// da direita para a esquerda
					for (int i = 0; i < part1.rows(); i++) {

						// Posi��o do primeiro e do segundo X para
						// compara��o da
						// dist�ncia
						int distComp = 0;
						int distCompBot = 0;

						// Percorro a linha com a borda branca
						for (int k = part1.cols() - 1; k >= 0; k--) {
							double[] pixelLinha = part1.get(i, k);

							// Verificar se esta na metade para cima da imagem
							if (i <= part1.rows() / 2) {

								// Utilizado para compara��o superior que
								// percorre
								// ate o
								// final da linha
								if (pixelLinha[0] == 0) {
									distComp++;
								} else {
									distComp = 0;
								}

								// Verifica��o de maior caminho de pretos
								// superior
								// Retirar o 10 e estabalecer um parametro de
								// corte
								// na linha
								if (distComp != 0 && distanciaPixelsPretosSuperior < distComp && k > part1.cols() / 2
										&& i > 10) {
									posYSuperior = i;
									distanciaPixelsPretosSuperior = distComp;
								}
								// Caso esteja na metade para baixo
							} else {

								// Utilizado para compara��o superior que
								// percorre
								// ate o
								// final da linha
								if (pixelLinha[0] == 0) {
									distCompBot++;
								} else {
									distCompBot = 0;
								}

								// Verifica��o de maior caminho de pretos
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

					Highgui.imwrite("resultado/CorteFinal-" + name[0] + ".jpg", part1);

					System.out.println(
							" ---------------------------- Informa��es de corte superior da imagem ---------------------------- ");
					// System.out.println("Maior distancia: " +
					// distanciaPixelsPretosSuperior);
					// System.out.println("Posi��o Y para corte: " +
					// posYSuperior);

					System.out.println(
							" ---------------------------- Informa��es de corte inferior da imagem ---------------------------- ");
					// System.out.println("Maior distancia: " +
					// distanciaPixelsPretosInferior);
					// System.out.println("Posi��o Y para corte: " +
					// posYInferior);

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
					// Parte final do algoritmo de segmenta��o de mand�bula que
					// retorna a mandibula cortada em uma nova imagem

					int qtdeCols = source.cols() - (2 * posXLateral);
					int qtdeRows = source.rows() - (posYSuperior + (source.rows() - posYInferior));

					Rect rectCrop = new Rect(posXLateral, posYSuperior, qtdeCols, qtdeRows);
					Mat imagemResultado = new Mat(source, rectCrop);

					Highgui.imwrite("resultado/Exame-" + name[0] + ".jpg", imagemResultado);

					// ----------------- INICIO DA PARTE DE MANCHAS
					// ---------------

					// Equalizando o histograma da imagem

					Mat imagemCortada = imagemResultado.clone();

					Imgproc.equalizeHist(imagemResultado, imagemCortada);

					// Highgui.imwrite("resultado/equalizeHist-" + name[0] +
					// ".jpg", imagemCortada);

					// ----------------- INICIO DA PARTE DE MANCHAS
					// ---------------

					// Elemento para dilata��o
					int gauss_size = 9;
					int dilate_size = 18;

					Mat elementDilate = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE,
							new Size(2 * dilate_size + 1, 2 * dilate_size + 1));
					
					System.out.println("");
					
					for (int i = 0; i < elementDilate.rows(); i++) {
						for (int j = 0; j < elementDilate.cols(); j++) {
							double teste[] = elementDilate.get(i, j);
							System.out.print(" "+ (int) teste[0]+ " ");
						}
						System.out.println("");
					}
					
					System.out.println("");

					limiar = 0;
					numPixel = 0;

					for (int i = 0; i < imagemCortada.rows(); i++) {
						for (int j = 0; j < imagemCortada.cols(); j++) {
							double[] testeLimiar = imagemCortada.get(i, j);
							if (testeLimiar[0] >= 50 && testeLimiar[0] <= 250) {
								limiar = limiar + testeLimiar[0];
								numPixel++;
							}
						}
					}
					int resultadoMediaMandibula = (int) limiar / numPixel;

					// ----------------- INICIO DA CRIA��O DA M�SCARA
					// ---------------

					// Corte da mandibula para aplicar na mascara
					Mat maskMandibula = new Mat(imagemCortada.rows(), imagemCortada.cols(), imagemCortada.type());

					maskMandibula = imagemCortada.clone();

					Imgproc.GaussianBlur(maskMandibula, maskMandibula, new Size(gauss_size * 2 + 1, gauss_size * 2 + 1),
							0);

					Imgproc.dilate(maskMandibula, maskMandibula, elementDilate);

					System.out.println(
							" ---------------------------- Informa��es da imagem das anomalias ---------------------------- ");
					// System.out.println("M�dia de corte da mand�bula: " +
					// resultadoMediaMandibula);

					Imgproc.threshold(maskMandibula, maskMandibula, resultadoMediaMandibula, 255,
							Imgproc.THRESH_BINARY);

					for (int i = 0; i < maskMandibula.rows(); i++) {
						for (int j = 0; j < maskMandibula.cols(); j++) {
							double[] pixelBorda = maskMandibula.get(i, j);

							// Borda inferior preta da mascara
							if (i == (maskMandibula.rows() - 1) && pixelBorda[0] == 0) {
								boolean pixelBranco = false;

								for (int k = maskMandibula.rows() - 1; k >= 0; k--) {
									double[] pixelLinha = maskMandibula.get(k, j);

									if (!pixelBranco && pixelLinha[0] == 0) {
										maskMandibula.put(k, j, 255);
									} else if (!pixelBranco && pixelLinha[0] == 255) {
										pixelBranco = true;
									}
								}
								
								//Highgui.imwrite("resultado/MASK1-" + name[0] + ".jpg", maskMandibula);
							}

							// Descarto metade para cima por enquanto
							if (i <= (maskMandibula.rows() / 2) + (posXLateral / 2)) {
								maskMandibula.put(i, j, 255);
							}

							// Borda esquerda preta da mascara
							if (j == 0 && pixelBorda[0] == 0) {
								boolean pixelBrancoEsq = false;

								for (int k = 0; k < maskMandibula.cols(); k++) {
									double[] pixelLinha = maskMandibula.get(i, k);

									if (!pixelBrancoEsq && pixelLinha[0] == 0) {
										maskMandibula.put(i, k, 255);
									} else if (!pixelBrancoEsq && pixelLinha[0] == 255) {
										pixelBrancoEsq = true;
									}
								}
								
								//Highgui.imwrite("resultado/MASK2-" + name[0] + ".jpg", maskMandibula);
							}

							// Borda direita preta da mascara
							if (j == (maskMandibula.cols() - 1) && pixelBorda[0] == 0) {
								boolean pixelBrancoDir = false;
								for (int k = maskMandibula.cols() - 1; k >= 0; k--) {
									double[] pixelLinha = maskMandibula.get(i, k);

									if (!pixelBrancoDir && pixelLinha[0] == 0) {
										maskMandibula.put(i, k, 255);
									} else if (!pixelBrancoDir && pixelLinha[0] == 255) {
										pixelBrancoDir = true;
									}
								}
								
								//Highgui.imwrite("resultado/MASK3-" + name[0] + ".jpg", maskMandibula);
							}
						}
					}

					Highgui.imwrite("resultado/Mascara-" + name[0] + ".jpg", maskMandibula);

					// ------------- INICIO DA PARTE QUE DESTACA AS MANCHAS
					// -----------------
					Mat imagemAnomalia = new Mat(imagemCortada.rows(), imagemCortada.cols(), imagemCortada.type());
					Mat imagemPosSubtracao = new Mat(imagemCortada.rows(), imagemCortada.cols(), imagemCortada.type());

					// Dilata��o para ver anomalias
					Imgproc.dilate(imagemCortada, imagemAnomalia, elementDilate);

					// Segmenta��o para destacar
					Imgproc.threshold(imagemAnomalia, imagemAnomalia, resultadoMediaMandibula, 255,
							Imgproc.THRESH_TOZERO);

					Highgui.imwrite("resultado/Mask2-" + name[0] + ".jpg", imagemAnomalia);

					// Metade esquerda da imagem para analise
					Mat parteEsq = new Mat(imagemAnomalia.rows(), imagemAnomalia.cols() / 2, imagemAnomalia.type());
					Imgproc.getRectSubPix(imagemAnomalia, new Size(imagemAnomalia.cols() / 2, imagemAnomalia.rows()),
							new Point(imagemAnomalia.rows() / 2, imagemAnomalia.rows() / 2), parteEsq);

					// Metade direita da imagem para analise
					Mat parteDir = new Mat(imagemAnomalia.rows(), imagemAnomalia.cols() / 2, imagemAnomalia.type());
					Imgproc.getRectSubPix(imagemAnomalia, new Size(imagemAnomalia.cols() / 2, imagemAnomalia.rows()),
							new Point(imagemAnomalia.rows() / 2 + imagemAnomalia.cols() / 2, imagemAnomalia.rows() / 2),
							parteDir);
					Core.flip(parteDir, parteDir, 1);

					// Subtra��o
					Mat subtracaoEsq = new Mat(imagemAnomalia.rows() / 2, imagemAnomalia.cols() / 2,
							imagemAnomalia.type());
					Core.subtract(parteDir, parteEsq, subtracaoEsq);

					Mat subtracaoDir = new Mat(imagemAnomalia.rows() / 2, imagemAnomalia.cols() / 2,
							imagemAnomalia.type());
					Core.subtract(parteEsq, parteDir, subtracaoDir);
					Core.flip(subtracaoDir, subtracaoDir, 1);

					// Medias para corte posterior
					double mediaCorteEsq = 0;
					int divCorteEsq = 0;

					double mediaCorteDir = 0;
					int divCorteDir = 0;

					for (int i = 0; i < subtracaoEsq.rows(); i++) {
						for (int j = 0; j < subtracaoEsq.cols(); j++) {
							double[] pixelCorte = subtracaoEsq.get(i, j);
							mediaCorteEsq = mediaCorteEsq + pixelCorte[0];
							divCorteEsq++;
						}
					}

					for (int i = 0; i < subtracaoDir.rows(); i++) {
						for (int j = 0; j < subtracaoDir.cols(); j++) {
							double[] pixelCorte = subtracaoDir.get(i, j);
							mediaCorteDir = mediaCorteDir + pixelCorte[0];
							divCorteDir++;
						}
					}

					int mediaEsqBranco = (int) mediaCorteEsq / divCorteEsq;
					int mediaDirBranco = (int) mediaCorteDir / divCorteDir;

					Imgproc.threshold(subtracaoEsq, subtracaoEsq, mediaEsqBranco, 255, Imgproc.THRESH_TOZERO);
					Imgproc.threshold(subtracaoDir, subtracaoDir, mediaDirBranco, 255, Imgproc.THRESH_TOZERO);

					// Junto os dois resultados em uma unica imagem
					for (int i = 0; i < imagemPosSubtracao.rows(); i++) {
						for (int j = 0; j < imagemPosSubtracao.cols(); j++) {
							if (j < imagemPosSubtracao.cols() / 2) {
								double[] pixelCorte = subtracaoEsq.get(i, j);
								imagemPosSubtracao.put(i, j, pixelCorte[0]);
							} else if (j < (imagemPosSubtracao.cols() - 1)) {
								double[] pixelCorte = subtracaoDir.get(i, j - subtracaoDir.cols());
								imagemPosSubtracao.put(i, j, pixelCorte[0]);
							}
						}
					}

					Imgproc.threshold(imagemPosSubtracao, imagemPosSubtracao, resultadoMediaMandibula, 255,
							Imgproc.THRESH_BINARY_INV);

					Imgproc.GaussianBlur(imagemPosSubtracao, imagemPosSubtracao,
							new Size(gauss_size * 2 + 1, gauss_size * 2 + 1), 0);

					// Junto os dois resultados em uma unica imagem
					for (int i = 0; i < imagemPosSubtracao.rows(); i++) {
						for (int j = 0; j < imagemPosSubtracao.cols(); j++) {
							double[] pixelCorteMask = maskMandibula.get(i, j);
							double[] pixelCorteSub = imagemPosSubtracao.get(i, j);
							if (pixelCorteMask[0] != pixelCorteSub[0]) {
								imagemPosSubtracao.put(i, j, 255);
							}
						}
					}

					Highgui.imwrite("resultado/possub-" + name[0] + ".jpg", imagemPosSubtracao);

					for (int i = 0; i < imagemPosSubtracao.rows(); i++) {
						for (int j = 0; j < imagemPosSubtracao.cols(); j++) {
							double[] pixelCorteSub = imagemPosSubtracao.get(i, j);
							if (pixelCorteSub[0] == 0) {
								imagemPosSubtracao.put(i, j, 255);
							} else {
								imagemPosSubtracao.put(i, j, 0);
							}
						}
					}

					Highgui.imwrite("resultado/MascaraFinal-" + name[0] + ".jpg", imagemPosSubtracao);

					// ----------- INICIO PARAMETRIZA��O
					// ------------------------
					// Aplica��o de canny para extra��o dos contornos
					Imgproc.Canny(imagemPosSubtracao, imagemPosSubtracao, 50, resultadoMediaMandibula, 3, true);
					// Highgui.imwrite("resultado/Canny-" + name[0] + ".jpg",
					// imagemPosSubtracao);

					List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
					Mat hierarchy = new Mat();

					Imgproc.findContours(imagemPosSubtracao, contours, hierarchy, Imgproc.RETR_CCOMP,
							Imgproc.CHAIN_APPROX_SIMPLE);

					hierarchy.release();

					// Evito qualquer tipo de ruido
					Mat mask = new Mat(imagemPosSubtracao.rows(), imagemPosSubtracao.cols(), imagemPosSubtracao.type());
					for (int i = 0; i < mask.rows(); i++) {
						for (int j = 0; j < mask.cols(); j++) {
							mask.put(i, j, 0);
						}
					}

					System.out.println("Quantidade de manchas encontradas no total: " + contours.size());

					// La�o para desenhar os contornos encontrados e agrupar os
					// contornos q est�o um dentro do outro
					for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
						MatOfPoint2f approxCurve = new MatOfPoint2f();
						MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(contourIdx).toArray());
						double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
						Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

						MatOfPoint points = new MatOfPoint(approxCurve.toArray());

						Rect rect = Imgproc.boundingRect(points);

						if (rect.width > 6 && rect.height > 6) {
							// Desenhando o retangulo
							Core.rectangle(mask, new Point(rect.x, rect.y),
									new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 255, 255),
									-1);
						}
					}

					// CANNY NO RESULTADO FINAL
					Mat maskFinalCanny = new Mat(mask.rows(), mask.cols(), mask.type());
					Imgproc.Canny(mask, maskFinalCanny, 0, 255, 3, true);
					Highgui.imwrite("resultado/CannyFinal-" + name[0] + ".jpg", maskFinalCanny);

					// RESULTADO FINAL
					Mat result = imagemCortada.clone();

					for (int i = 0; i < maskFinalCanny.rows(); i++) {
						for (int j = 0; j < maskFinalCanny.cols(); j++) {
							double[] testeMask = maskFinalCanny.get(i, j);
							if (testeMask[0] == 255) {
								result.put(i, j, 255);
							}
						}
					}

					Highgui.imwrite("resultado/ResultadoFinal-" + name[0] + ".jpg", result);

					// PARAMETRIZA��O
					// Aplico a mascara de quadrados a imagem cortada para
					// extra��o de parametros

					// Come�o a parametriza��o com os retangulos encontrados na
					// mascara
					// Largura e altura
					// ponto x e y do come�o
					contours = new ArrayList<MatOfPoint>();
					hierarchy = new Mat();

					Mat maskCaracteristicas = mask.clone();

					Imgproc.findContours(maskCaracteristicas, contours, hierarchy, Imgproc.RETR_CCOMP,
							Imgproc.CHAIN_APPROX_SIMPLE);

					hierarchy.release();

					// System.out.println("Quantidade de manchas destacadas: " +
					// contours.size());
					System.out.println(
							" ---------------------------- Detalhes das manchas encontradas ---------------------------- ");

					List<CaracteristicaMancha> caracteristica = new ArrayList<>();

					for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
						MatOfPoint2f approxCurve = new MatOfPoint2f();
						MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(contourIdx).toArray());
						// Processing on mMOP2f1 which is in type MatOfPoint2f
						double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
						Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

						// Convert back to MatOfPoint
						MatOfPoint points = new MatOfPoint(approxCurve.toArray());

						// Get bounding rect of contour
						Rect rect = Imgproc.boundingRect(points);

						if (rect.width > 6 && rect.height > 6) {

							// Carrego uma lista com as caracteristicas das
							// manchas
							// de uma imagem
							CaracteristicaMancha caracteristicaObj = new CaracteristicaMancha();

							caracteristicaObj.setLargura(rect.width);
							caracteristicaObj.setAltura(rect.height);
							caracteristicaObj.setId(contourIdx + 1);
							caracteristicaObj.setPosicaoX(rect.x);
							caracteristicaObj.setPosicaoY(rect.y);

							// Fundamentar AQUI
							// Achar a posi��o relativa a 10 parte em q a matriz
							// come�a
							int contPixelIncrement = 0;
							for (int i = 1; i < 10; i++) {
								if (rect.x >= contPixelIncrement
										&& rect.x <= (contPixelIncrement + result.cols() / 10)) {
									caracteristicaObj.setPosicaoXRelativa(i);
								}
								contPixelIncrement = contPixelIncrement + result.cols() / 10;
							}

							contPixelIncrement = 0;
							for (int i = 1; i < 10; i++) {
								if (rect.y >= contPixelIncrement
										&& rect.y <= (contPixelIncrement + result.rows() / 10)) {
									caracteristicaObj.setPosicaoYRelativa(i);
								}
								contPixelIncrement = contPixelIncrement + result.rows() / 10;
							}

							caracteristica.add(caracteristicaObj);
						}

						// System.out.println(
						// "Mancha " + (contourIdx + 1) + " Posi��o X :" +
						// rect.x + " Posi��o Y :" + rect.y);
						// System.out.println("Largura :" + rect.width + "
						// Altura :" + rect.height);
					}

					// Matriz com as manchas dentro dos seus retangulos
					// Preencho com preto para evitar ruidos
					Mat matManchas = new Mat(imagemCortada.rows(), imagemCortada.cols(), imagemCortada.type());

					// Matriz somente com o canny aplicado as manchas para
					// parametriza��es
					for (CaracteristicaMancha caracteristicaMancha : caracteristica) {

						int linhas = caracteristicaMancha.getAltura();
						int colunas = caracteristicaMancha.getLargura();

						int posX = caracteristicaMancha.getPosicaoX();
						int posY = caracteristicaMancha.getPosicaoY();

						for (int i = 0; i < imagemCortada.rows(); i++) {
							for (int j = 0; j < imagemCortada.cols(); j++) {

								if (i == posY && j == posX) {
									for (int k = 0; k < linhas; k++) {
										for (int l = 0; l < colunas; l++) {
											double[] pixel = imagemResultado.get(k + i, l + j);
											matManchas.put(k + i, l + j, pixel);
										}
									}
								}

							}
						}
					}

					Highgui.imwrite("resultado/MatManchas-" + name[0] + ".jpg", matManchas);

					System.out.println(
							" ---------------------------- ATRIBUTOS DE TEXTURA  ---------------------------- ");

					// Calculo da Matriz de CO-OCORRENCIA
					// Padr�o de 8 tons de cinza para o MATLAB
					// Primeiro passo - convers�o da matriz para escala de 8
					// tons de cinza - a cada 32 altera-se valor
					// Segundo passo - calculo das matrizes para os graus - 0,
					// 45, 90, 135, 180, 225, 270, 315
					// Gerar cada matriz de co-ocorrencia com a distancia de 1
					// para cada um desses
					// Normalizar a matriz para calculo da textura - somar e
					// descobrir o total depois dividr cada item pelo numero
					// total - soma nao pode passar de 1

					// Matriz somente com o canny aplicado as manchas para
					// parametriza��es

					System.out.println(caracteristica.size());
					for (CaracteristicaMancha caracteristicaMancha : caracteristica) {

						List<AtributosTextura> listaAtributos = new ArrayList<>();

						System.out.println(" ---------------------------- MANCHA ID = " + caracteristicaMancha.getId()
								+ " ---------------------------- ");

						int linhas = caracteristicaMancha.getAltura();
						int colunas = caracteristicaMancha.getLargura();

						int posX = caracteristicaMancha.getPosicaoX();
						int posY = caracteristicaMancha.getPosicaoY();

						// Matriz utilizada para calculos de co-ocorrencia
						double[][] matriz32Tons = new double[linhas][colunas];

						// Calculo para gerar a matriz convertida para somente 8
						// tons de cinza para aplicar textura

						for (int i = 0; i < imagemResultado.rows(); i++) {
							for (int j = 0; j < imagemResultado.cols(); j++) {

								if (i == posY && j == posX) {
									for (int k = 0; k < linhas; k++) {
										for (int l = 0; l < colunas; l++) {
											double[] pixel = imagemResultado.get(k + i, l + j);
											if (pixel[0] >= 0 && pixel[0] <= 8) {
												matriz32Tons[k][l] = 0;
											} else if (pixel[0] >= 9 && pixel[0] <= 16) {
												matriz32Tons[k][l] = 1;
											} else if (pixel[0] >= 17 && pixel[0] <= 24) {
												matriz32Tons[k][l] = 2;
											} else if (pixel[0] >= 25 && pixel[0] <= 32) {
												matriz32Tons[k][l] = 3;
											} else if (pixel[0] >= 33 && pixel[0] <= 40) {
												matriz32Tons[k][l] = 4;
											} else if (pixel[0] >= 41 && pixel[0] <= 48) {
												matriz32Tons[k][l] = 5;
											} else if (pixel[0] >= 49 && pixel[0] <= 56) {
												matriz32Tons[k][l] = 6;
											} else if (pixel[0] >= 57 && pixel[0] <= 64) {
												matriz32Tons[k][l] = 7;
											} else if (pixel[0] >= 65 && pixel[0] <= 72) {
												matriz32Tons[k][l] = 8;
											} else if (pixel[0] >= 73 && pixel[0] <= 80) {
												matriz32Tons[k][l] = 9;
											} else if (pixel[0] >= 81 && pixel[0] <= 88) {
												matriz32Tons[k][l] = 10;
											} else if (pixel[0] >= 89 && pixel[0] <= 96) {
												matriz32Tons[k][l] = 11;
											} else if (pixel[0] >= 97 && pixel[0] <= 104) {
												matriz32Tons[k][l] = 12;
											} else if (pixel[0] >= 105 && pixel[0] <= 112) {
												matriz32Tons[k][l] = 13;
											} else if (pixel[0] >= 113 && pixel[0] <= 120) {
												matriz32Tons[k][l] = 14;
											} else if (pixel[0] >= 121 && pixel[0] <= 128) {
												matriz32Tons[k][l] = 15;
											} else if (pixel[0] >= 129 && pixel[0] <= 136) {
												matriz32Tons[k][l] = 16;
											} else if (pixel[0] >= 137 && pixel[0] <= 144) {
												matriz32Tons[k][l] = 17;
											} else if (pixel[0] >= 145 && pixel[0] <= 152) {
												matriz32Tons[k][l] = 18;
											} else if (pixel[0] >= 153 && pixel[0] <= 160) {
												matriz32Tons[k][l] = 19;
											} else if (pixel[0] >= 161 && pixel[0] <= 168) {
												matriz32Tons[k][l] = 20;
											} else if (pixel[0] >= 169 && pixel[0] <= 176) {
												matriz32Tons[k][l] = 21;
											} else if (pixel[0] >= 177 && pixel[0] <= 184) {
												matriz32Tons[k][l] = 22;
											} else if (pixel[0] >= 185 && pixel[0] <= 192) {
												matriz32Tons[k][l] = 23;
											} else if (pixel[0] >= 193 && pixel[0] <= 200) {
												matriz32Tons[k][l] = 24;
											} else if (pixel[0] >= 201 && pixel[0] <= 208) {
												matriz32Tons[k][l] = 25;
											} else if (pixel[0] >= 209 && pixel[0] <= 216) {
												matriz32Tons[k][l] = 26;
											} else if (pixel[0] >= 217 && pixel[0] <= 224) {
												matriz32Tons[k][l] = 27;
											} else if (pixel[0] >= 225 && pixel[0] <= 232) {
												matriz32Tons[k][l] = 28;
											} else if (pixel[0] >= 233 && pixel[0] <= 240) {
												matriz32Tons[k][l] = 29;
											} else if (pixel[0] >= 241 && pixel[0] <= 248) {
												matriz32Tons[k][l] = 30;
											} else if (pixel[0] >= 249 && pixel[0] <= 256) {
												matriz32Tons[k][l] = 31;
											}
										}
									}
								}

							}
						}

						// For para gera��o da matriz de co-ocorrencia de 45 em
						// 45 graus
						for (int grau = 0; grau <= 315; grau += 45) {

							// VARIAVEIS PARA EXTRA��O DE TEXTURA
							// Sao utilizadas e zeradas em todos os graus
							double divisorMatriz = 0;
							double entropia = 0;
							double segundoMomentoAng = 0;
							double mediaTonsCinza = 0;
							double correlacao = 0;
							double mediaI = 0;
							double varianciaI = 0;
							double desvioPadraoI = 0;
							double contrasteT = 0;
							double homogeneidade = 0;
							double dissimilaridade = 0;

							// COME�ANDO COM ANGULO 0
							AtributosTextura atributosTexturaGrau = new AtributosTextura();
							atributosTexturaGrau.setGraus(grau);
							atributosTexturaGrau.setMatrizCO(new double[32][32]);

							// Calculo para Matriz de Co-ocorrencia com dist 1
							for (int i = 0; i < 32; i++) {
								for (int j = 0; j < 32; j++) {

									for (int k = 0; k < linhas; k++) {
										for (int l = 0; l < colunas; l++) {

											// Condicional para nao passar a
											// borda
											// direita para o angulo 0
											if (l < (colunas - 1) && grau == 0) {
												double valorLinha = matriz32Tons[k][l];
												double valorLinha0 = matriz32Tons[k][l + 1];

												if (valorLinha == i && valorLinha0 == j) {
													atributosTexturaGrau.getMatrizCO()[i][j] = atributosTexturaGrau
															.getMatrizCO()[i][j] + 1;
												}
											} else if (k > 0 && l < (colunas - 1) && grau == 45) {
												double valorLinha = matriz32Tons[k][l];
												double valorLinha45 = matriz32Tons[k - 1][l + 1];

												if (valorLinha == i && valorLinha45 == j) {
													atributosTexturaGrau.getMatrizCO()[i][j] = atributosTexturaGrau
															.getMatrizCO()[i][j] + 1;
												}
											} else if (k > 0 && grau == 90) {
												double valorLinha = matriz32Tons[k][l];
												double valorLinha90 = matriz32Tons[k - 1][l];

												if (valorLinha == i && valorLinha90 == j) {
													atributosTexturaGrau.getMatrizCO()[i][j] = atributosTexturaGrau
															.getMatrizCO()[i][j] + 1;
												}
											} else if (k > 0 && l > 0 && grau == 135) {
												double valorLinha = matriz32Tons[k][l];
												double valorLinha135 = matriz32Tons[k - 1][l - 1];

												if (valorLinha == i && valorLinha135 == j) {
													atributosTexturaGrau.getMatrizCO()[i][j] = atributosTexturaGrau
															.getMatrizCO()[i][j] + 1;
												}
											} else if (l > 0 && grau == 180) {
												double valorLinha = matriz32Tons[k][l];
												double valorLinha180 = matriz32Tons[k][l - 1];

												if (valorLinha == i && valorLinha180 == j) {
													atributosTexturaGrau.getMatrizCO()[i][j] = atributosTexturaGrau
															.getMatrizCO()[i][j] + 1;
												}
											} else if (k < (linhas - 1) && l > 0 && grau == 225) {
												double valorLinha = matriz32Tons[k][l];
												double valorLinha225 = matriz32Tons[k + 1][l - 1];

												if (valorLinha == i && valorLinha225 == j) {
													atributosTexturaGrau.getMatrizCO()[i][j] = atributosTexturaGrau
															.getMatrizCO()[i][j] + 1;
												}
											} else if (k < (linhas - 1) && grau == 270) {
												double valorLinha = matriz32Tons[k][l];
												double valorLinha270 = matriz32Tons[k + 1][l];

												if (valorLinha == i && valorLinha270 == j) {
													atributosTexturaGrau.getMatrizCO()[i][j] = atributosTexturaGrau
															.getMatrizCO()[i][j] + 1;
												}
											} else if (k < (linhas - 1) && l < (colunas - 1) && grau == 315) {
												double valorLinha = matriz32Tons[k][l];
												double valorLinha315 = matriz32Tons[k + 1][l + 1];

												if (valorLinha == i && valorLinha315 == j) {
													atributosTexturaGrau.getMatrizCO()[i][j] = atributosTexturaGrau
															.getMatrizCO()[i][j] + 1;
												}
											}
										}
									}
								}
							}

							// NORMALIZA��O DA MATRIZ
							for (int i = 0; i < 32; i++) {
								for (int j = 0; j < 32; j++) {
									divisorMatriz = divisorMatriz + atributosTexturaGrau.getMatrizCO()[i][j];
								}
							}

							// Matriz encontrada final
							for (int i = 0; i < 32; i++) {
								for (int j = 0; j < 32; j++) {
									atributosTexturaGrau.getMatrizCO()[i][j] = atributosTexturaGrau.getMatrizCO()[i][j]
											/ divisorMatriz;
								}
							}

							System.out.println(" ---------------------------- Matriz normalizada - D = 1 e Ang = "
									+ grau + " ---------------------------- ");

							for (int i = 0; i < 32; i++) {
								for (int j = 0; j < 32; j++) {
									// System.out.print(" " +
									// atributosTexturaGrau.getMatrizCO()[i][j]
									// + " ");
								}
								// System.out.println(" ");
							}

							// EXTRA��O da M�dia de Tons de Cinza
							for (int i = 0; i < imagemResultado.rows(); i++) {
								for (int j = 0; j < imagemResultado.cols(); j++) {

									if (i == posY && j == posX) {
										for (int k = 0; k < linhas; k++) {
											for (int l = 0; l < colunas; l++) {
												double[] pixel = imagemResultado.get(k + i, l + j);
												mediaTonsCinza = mediaTonsCinza + pixel[0];
											}
										}
									}
								}
							}

							System.out.println("M�dia de tons de cinza : " + mediaTonsCinza / (linhas * colunas));
							atributosTexturaGrau.setMediaTonsCinza((int) mediaTonsCinza / (linhas * colunas));

							// EXTRA��O do SMA Grau 0
							// SMA e definida como soma de todos os elementos ao
							// quadrado
							for (int i = 0; i < 32; i++) {
								for (int j = 0; j < 32; j++) {
									double valor = Math.pow(atributosTexturaGrau.getMatrizCO()[i][j], 2);
									segundoMomentoAng = segundoMomentoAng + valor;
								}
							}

							System.out.println("Segundo Momento Angular : " + segundoMomentoAng);
							atributosTexturaGrau.setSegundoMomentoAng(segundoMomentoAng);

							// EXTRA��O da ENERGIA Grau 0
							System.out.println("Energia : " + Math.sqrt(segundoMomentoAng));
							atributosTexturaGrau.setEnergia(Math.sqrt(segundoMomentoAng));

							// System.out.println("Segundo Momento Angular : " +
							// segundoMomentoAng);
							atributosTexturaGrau.setSegundoMomentoAng(segundoMomentoAng);

							// EXTRA��O da ENTROPIA Grau 0
							// ENTROPIA e definida como soma dos elementos a log
							// do
							// proprio elemento de todos os elementos ao
							// quadrado
							for (int i = 0; i < 32; i++) {
								for (int j = 0; j < 32; j++) {
									double valor = atributosTexturaGrau.getMatrizCO()[i][j]
											* Math.log10(atributosTexturaGrau.getMatrizCO()[i][j]);
									if (!Double.isNaN(valor)) {
										entropia = entropia + valor;
									}
								}
							}

							System.out.println("Entropia : " + entropia * -1);
							atributosTexturaGrau.setEntropia(entropia * -1);

							// EXTRA��O da CONTRASTE Grau 0
							for (int i = 0; i < 32; i++) {
								for (int j = 0; j < 32; j++) {
									double valor = (Math.pow(i - j, 2)) * atributosTexturaGrau.getMatrizCO()[i][j];
									contrasteT = contrasteT + valor;
								}
							}

							System.out.println("Contraste : " + contrasteT);
							atributosTexturaGrau.setContraste(contrasteT);

							// EXTRA��O da HOMOGENEIDADE Grau 0
							for (int i = 0; i < 32; i++) {
								for (int j = 0; j < 32; j++) {
									double valor = atributosTexturaGrau.getMatrizCO()[i][j] / (1 + Math.abs(i - j));
									homogeneidade = homogeneidade + valor;
								}
							}

							System.out.println("Homogeneidade : " + homogeneidade);
							atributosTexturaGrau.setHomogeneidade(homogeneidade);

							// EXTRA��O da dissimilaridade Grau 0
							for (int i = 0; i < 32; i++) {
								for (int j = 0; j < 32; j++) {
									double valor = Math.abs(i - j) * atributosTexturaGrau.getMatrizCO()[i][j];
									dissimilaridade = dissimilaridade + valor;
								}
							}

							System.out.println("Dissimilaridade : " + dissimilaridade);
							atributosTexturaGrau.setDissimilaridade(dissimilaridade);

							// INICIO DO CALCULO DE CORRELA��O
							// Calculo das m�dias
							// Media i
							for (int i = 0; i < 32; i++) {
								for (int j = 0; j < 32; j++) {
									double valor = i * atributosTexturaGrau.getMatrizCO()[i][j];
									mediaI = mediaI + valor;
								}
							}

							System.out.println("M�dia i : " + mediaI);
							atributosTexturaGrau.setMedia(mediaI);

							// Calculo da variancia
							// Variancia i ao quadrado
							for (int i = 0; i < 32; i++) {
								for (int j = 0; j < 32; j++) {
									double valor = atributosTexturaGrau.getMatrizCO()[i][j]
											* (Math.pow((i - atributosTexturaGrau.getMedia()), 2));
									varianciaI = varianciaI + valor;
								}
							}

							System.out.println("Vari�ncia i^2 : " + varianciaI);
							atributosTexturaGrau.setVariancia(varianciaI);

							// Calculo da Desvio Padr�o
							// Desvio Padr�o i
							desvioPadraoI = Math.sqrt(varianciaI);
							System.out.println("Desvio Padr�o i : " + desvioPadraoI);
							atributosTexturaGrau.setDesvioPadrao(desvioPadraoI);

							// Calculo da correla��o
							if (atributosTexturaGrau.getDesvioPadrao() != 0) {
								for (int i = 0; i < 32; i++) {
									for (int j = 0; j < 32; j++) {
										double valor = ((i - atributosTexturaGrau.getMedia())
												* (j - atributosTexturaGrau.getMedia()))
												/ (atributosTexturaGrau.getDesvioPadrao()
														* atributosTexturaGrau.getDesvioPadrao());
										valor = valor * atributosTexturaGrau.getMatrizCO()[i][j];
										correlacao = correlacao + valor;
									}
								}
							}

							System.out.println("Correla��o : " + correlacao);
							atributosTexturaGrau.setCorrelacao(correlacao);

							// Verifico para qual mancha os resultado foram
							// nulos
							listaAtributos.add(atributosTexturaGrau);
						}

						Textura textura = new Textura();

						textura.setAtributosTextura(new ArrayList<AtributosTextura>());
						textura.setAtributosTextura(listaAtributos);

						double mediaEntropia = 0;
						double mediaSegundoMomentoAng = 0;
						double mediaCorrelacao = 0;
						double mediaEnergia = 0;
						double mediaCorrelacaoMediaI = 0;
						double mediaVarianciaI = 0;
						double mediaDesvioPadraoI = 0;
						double mediaContraste = 0;
						double mediaHomogeneidade = 0;
						double mediaTonsCinza = 0;
						double mediaDissimilaridade = 0;

						caracteristicaMancha.setTextura(textura);

						for (AtributosTextura atributosTextura : textura.getAtributosTextura()) {
							mediaEntropia = mediaEntropia + atributosTextura.getEntropia();
							mediaSegundoMomentoAng = mediaSegundoMomentoAng + atributosTextura.getSegundoMomentoAng();
							mediaCorrelacao = mediaCorrelacao + atributosTextura.getCorrelacao();
							mediaEnergia = mediaEnergia + atributosTextura.getEnergia();
							mediaCorrelacaoMediaI = mediaCorrelacaoMediaI + atributosTextura.getMedia();
							mediaVarianciaI = mediaVarianciaI + atributosTextura.getVariancia();
							mediaDesvioPadraoI = mediaDesvioPadraoI + atributosTextura.getDesvioPadrao();
							mediaContraste = mediaContraste + atributosTextura.getContraste();
							mediaHomogeneidade = mediaHomogeneidade + atributosTextura.getHomogeneidade();
							mediaTonsCinza = mediaTonsCinza + atributosTextura.getMediaTonsCinza();
							mediaDissimilaridade = mediaDissimilaridade + atributosTextura.getDissimilaridade();
						}

						caracteristicaMancha.getTextura().setMediaEntropia(mediaEntropia / 8);
						caracteristicaMancha.getTextura().setMediaSegundoMomentoAng(mediaSegundoMomentoAng / 8);
						caracteristicaMancha.getTextura().setMediaCorrelacao(mediaCorrelacao / 8);
						caracteristicaMancha.getTextura().setMediaEnergia(mediaEnergia / 8);
						caracteristicaMancha.getTextura().setMediaCorrelacaoMedia(mediaCorrelacaoMediaI / 8);
						caracteristicaMancha.getTextura().setMediaVariancia(mediaVarianciaI / 8);
						caracteristicaMancha.getTextura().setMediaDesvioPadrao(mediaDesvioPadraoI / 8);
						caracteristicaMancha.getTextura().setMediaContraste(mediaContraste / 8);
						caracteristicaMancha.getTextura().setMediaHomogeneidade(mediaHomogeneidade / 8);
						caracteristicaMancha.getTextura().setMediaDissimilaridade(mediaDissimilaridade / 8);
						caracteristicaMancha.getTextura().setMediaTonsCinza(mediaTonsCinza / 8);

						fileCSV += caracteristicaMancha.getLargura() + ",";
						fileCSV += caracteristicaMancha.getAltura() + ",";
						fileCSV += caracteristicaMancha.getPosicaoXRelativa() + ",";
						fileCSV += caracteristicaMancha.getPosicaoYRelativa() + ",";
						fileCSV += caracteristicaMancha.getTextura().getMediaEntropia() + ",";
						fileCSV += caracteristicaMancha.getTextura().getMediaSegundoMomentoAng() + ",";
						fileCSV += caracteristicaMancha.getTextura().getMediaCorrelacao() + ",";
						fileCSV += caracteristicaMancha.getTextura().getMediaEnergia() + ",";
						fileCSV += caracteristicaMancha.getTextura().getMediaCorrelacaoMedia() + ",";
						fileCSV += caracteristicaMancha.getTextura().getMediaVariancia() + ",";
						fileCSV += caracteristicaMancha.getTextura().getMediaDesvioPadrao() + ",";
						fileCSV += caracteristicaMancha.getTextura().getMediaContraste() + ",";
						fileCSV += caracteristicaMancha.getTextura().getMediaHomogeneidade() + ",";
						fileCSV += caracteristicaMancha.getTextura().getMediaDissimilaridade() + ",";
						fileCSV += caracteristicaMancha.getTextura().getMediaTonsCinza() + ",";

						// Fundamentar com a valida��o
						if (caracteristicaMancha.getTextura().getMediaCorrelacao() > 0.85) {
							fileCSV += "atencao";
						} else {
							fileCSV += "normal";
						}

						fileCSV += "\n";
					}
				}
			}
		}
		pw.write(fileCSV);
		pw.close();
	}
}