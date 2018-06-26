package br.com.saipro.viewmodel;

import java.util.List;

import br.com.saipro.model.CaracteristicaMancha;

public class CaracteristicaManchaViewModel {

	public List<CaracteristicaMancha> caracteristicaManchas;
	
	public List<CaracteristicaMancha> getCaracteristicaManchas() {
		return caracteristicaManchas;
	}
	public void setCaracteristicaManchas(List<CaracteristicaMancha> caracteristicaManchas) {
		this.caracteristicaManchas = caracteristicaManchas;
	}
	
}
