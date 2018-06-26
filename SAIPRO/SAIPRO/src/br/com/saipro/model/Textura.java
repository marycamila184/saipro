package br.com.saipro.model;

import java.util.List;

public class Textura {

	public double mediaEntropia;
	public double mediaSegundoMomentoAng;
	public double mediaCorrelacao;
	public double mediaEnergia;
	public double mediaCorrelacaoMedia;
	public double mediaVariancia;
	public double mediaDesvioPadrao;
	public double mediaContraste;
	public double mediaHomogeneidade;
	public double mediaTonsCinza;
	public double mediaDissimilaridade;
	public List<AtributosTextura> atributosTextura;

	public List<AtributosTextura> getAtributosTextura() {
		return atributosTextura;
	}

	public void setAtributosTextura(List<AtributosTextura> atributosTextura) {
		this.atributosTextura = atributosTextura;
	}

	public double getMediaEntropia() {
		return mediaEntropia;
	}

	public void setMediaEntropia(double mediaEntropia) {
		this.mediaEntropia = mediaEntropia;
	}

	public double getMediaSegundoMomentoAng() {
		return mediaSegundoMomentoAng;
	}

	public void setMediaSegundoMomentoAng(double mediaSegundoMomentoAng) {
		this.mediaSegundoMomentoAng = mediaSegundoMomentoAng;
	}

	public double getMediaCorrelacao() {
		return mediaCorrelacao;
	}

	public void setMediaCorrelacao(double mediaCorrelacao) {
		this.mediaCorrelacao = mediaCorrelacao;
	}

	public double getMediaEnergia() {
		return mediaEnergia;
	}

	public void setMediaEnergia(double mediaEnergia) {
		this.mediaEnergia = mediaEnergia;
	}

	
	public double getMediaContraste() {
		return mediaContraste;
	}

	public void setMediaContraste(double mediaContraste) {
		this.mediaContraste = mediaContraste;
	}

	public double getMediaHomogeneidade() {
		return mediaHomogeneidade;
	}

	public void setMediaHomogeneidade(double mediaHomogeneidade) {
		this.mediaHomogeneidade = mediaHomogeneidade;
	}

	public double getMediaTonsCinza() {
		return mediaTonsCinza;
	}

	public void setMediaTonsCinza(double mediaTonsCinza) {
		this.mediaTonsCinza = mediaTonsCinza;
	}

	public double getMediaCorrelacaoMedia() {
		return mediaCorrelacaoMedia;
	}

	public void setMediaCorrelacaoMedia(double mediaCorrelacaoMedia) {
		this.mediaCorrelacaoMedia = mediaCorrelacaoMedia;
	}

	public double getMediaVariancia() {
		return mediaVariancia;
	}

	public void setMediaVariancia(double mediaVariancia) {
		this.mediaVariancia = mediaVariancia;
	}

	public double getMediaDesvioPadrao() {
		return mediaDesvioPadrao;
	}

	public void setMediaDesvioPadrao(double mediaDesvioPadrao) {
		this.mediaDesvioPadrao = mediaDesvioPadrao;
	}

	public double getMediaDissimilaridade() {
		return mediaDissimilaridade;
	}

	public void setMediaDissimilaridade(double mediaDissimilaridade) {
		this.mediaDissimilaridade = mediaDissimilaridade;
	}
	
	
}
