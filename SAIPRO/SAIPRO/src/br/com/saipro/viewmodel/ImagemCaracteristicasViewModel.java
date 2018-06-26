package br.com.saipro.viewmodel;

import java.util.List;

import br.com.saipro.model.CaracteristicaMancha;

public class ImagemCaracteristicasViewModel {

	public List<CaracteristicaMancha> caracteristicaManchas;
	public String imagem;
	
	public List<CaracteristicaMancha> getCaracteristicaManchas() {
		return caracteristicaManchas;
	}
	public void setCaracteristicaManchas(List<CaracteristicaMancha> caracteristicaManchas) {
		this.caracteristicaManchas = caracteristicaManchas;
	}
	public String getImagem() {
		return imagem;
	}
	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
}
