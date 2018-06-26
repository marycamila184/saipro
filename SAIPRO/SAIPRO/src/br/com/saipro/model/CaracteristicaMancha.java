package br.com.saipro.model;


public class CaracteristicaMancha {

	public int id;
	public int largura;
	public int altura;
	public int posicaoX;
	public int posicaoY;
	public int posicaoXRelativa;
	public int posicaoYRelativa;
	public boolean anomalia;
	public Textura textura;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLargura() {
		return largura;
	}

	public void setLargura(int largura) {
		this.largura = largura;
	}

	public int getAltura() {
		return altura;
	}

	public void setAltura(int altura) {
		this.altura = altura;
	}

	public int getPosicaoX() {
		return posicaoX;
	}

	public void setPosicaoX(int posicaoX) {
		this.posicaoX = posicaoX;
	}

	public int getPosicaoY() {
		return posicaoY;
	}

	public void setPosicaoY(int posicaoY) {
		this.posicaoY = posicaoY;
	}

	public Textura getTextura() {
		return textura;
	}

	public void setTextura(Textura textura) {
		this.textura = textura;
	}

	public int getPosicaoXRelativa() {
		return posicaoXRelativa;
	}

	public void setPosicaoXRelativa(int posicaoXRelativa) {
		this.posicaoXRelativa = posicaoXRelativa;
	}

	public int getPosicaoYRelativa() {
		return posicaoYRelativa;
	}

	public void setPosicaoYRelativa(int posicaoYRelativa) {
		this.posicaoYRelativa = posicaoYRelativa;
	}

	public boolean isAnomalia() {
		return anomalia;
	}

	public void setAnomalia(boolean anomalia) {
		this.anomalia = anomalia;
	}
	
	

}
