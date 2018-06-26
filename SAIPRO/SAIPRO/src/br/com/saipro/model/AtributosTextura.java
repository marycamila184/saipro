package br.com.saipro.model;


public class AtributosTextura {

	public double matrizCO[][];
	public double entropia;
	public double segundoMomentoAng;
	public double correlacao;
	public double energia;
	public double media;
	public double variancia;
	public double desvioPadrao;
	public double dissimilaridade;
	public double contraste;
	public double homogeneidade;
	public double mediaTonsCinza;
	public int graus;

	public double[][] getMatrizCO() {
		return matrizCO;
	}

	public void setMatrizCO(double[][] matrizCO) {
		this.matrizCO = matrizCO;
	}

	public double getEntropia() {
		return entropia;
	}

	public void setEntropia(double entropia) {
		this.entropia = entropia;
	}

	public double getSegundoMomentoAng() {
		return segundoMomentoAng;
	}

	public void setSegundoMomentoAng(double segundoMomentoAng) {
		this.segundoMomentoAng = segundoMomentoAng;
	}

	public double getCorrelacao() {
		return correlacao;
	}

	public void setCorrelacao(double correlacao) {
		this.correlacao = correlacao;
	}

	public double getHomogeneidade() {
		return homogeneidade;
	}

	public void setHomogeneidade(double homogeneidade) {
		this.homogeneidade = homogeneidade;
	}

	public int getGraus() {
		return graus;
	}

	public void setGraus(int graus) {
		this.graus = graus;
	}

	public double getMediaTonsCinza() {
		return mediaTonsCinza;
	}

	public void setMediaTonsCinza(double mediaTonsCinza) {
		this.mediaTonsCinza = mediaTonsCinza;
	}

	public double getContraste() {
		return contraste;
	}

	public void setContraste(double contraste) {
		this.contraste = contraste;
	}

	public double getEnergia() {
		return energia;
	}

	public void setEnergia(double energia) {
		this.energia = energia;
	}

	public double getMedia() {
		return media;
	}

	public void setMedia(double media) {
		this.media = media;
	}

	public double getVariancia() {
		return variancia;
	}

	public void setVariancia(double variancia) {
		this.variancia = variancia;
	}

	public double getDesvioPadrao() {
		return desvioPadrao;
	}

	public void setDesvioPadrao(double desvioPadrao) {
		this.desvioPadrao = desvioPadrao;
	}

	public double getDissimilaridade() {
		return dissimilaridade;
	}

	public void setDissimilaridade(double dissimilaridade) {
		this.dissimilaridade = dissimilaridade;
	}
	
}
