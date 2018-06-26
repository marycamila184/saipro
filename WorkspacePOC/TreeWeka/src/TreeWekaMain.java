import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class TreeWekaMain {

	public static void main(String[] args) throws Exception {
		// load dataset
		/*
		 * DataSource source = new DataSource(
		 * "C:/Users/boty1/Desktop/WorkspacePOC/HaarCascade/atributos.arff");
		 * Instances datasetTree = source.getDataSet(); // set class index to
		 * the last attribute
		 * datasetTree.setClassIndex(datasetTree.numAttributes() - 1); // create
		 * and build the classifier! NaiveBayes nb = new NaiveBayes();
		 * nb.buildClassifier(datasetTree);
		 * 
		 * SMO svm = new SMO(); svm.buildClassifier(datasetTree);
		 * 
		 * J48Consolidated tree = new J48Consolidated();
		 * 
		 * tree.setRMnewDistrMinClass(50); tree.setRMnumberSamples(99);
		 * tree.setRMbagSizePercent(-2);
		 * 
		 * String[] option = tree.getOptions(); for (int i = 0; i <
		 * option.length; i++) { System.out.println(option[i]); }
		 * 
		 * tree.buildClassifier(datasetTree); System.out.println(tree.graph());
		 */

		// Declaring attributes
		Attribute largura = new Attribute("largura");
		Attribute altura = new Attribute("altura");
		Attribute posicaoX = new Attribute("posicaoX");
		Attribute posicaoY = new Attribute("posicaoY");
		Attribute entropia = new Attribute("entropia");
		Attribute sma = new Attribute("segundo-momento-ang");
		Attribute energia = new Attribute("energia");
		Attribute mediaI = new Attribute("media-i");
		Attribute mediaJ = new Attribute("media-j");
		Attribute varianciaI = new Attribute("variancia-i");
		Attribute varianciaJ = new Attribute("variancia-j");
		Attribute desvioPadraoI = new Attribute("desvio-padrao-i");
		Attribute desvioPadraoJ = new Attribute("desvio-padrao-j");
		Attribute contraste = new Attribute("contraste");
		Attribute homogeneidade = new Attribute("homogeneidade");
		Attribute mediaTonsCinza = new Attribute("media-tons-cinza");

		ArrayList<String> fvClassVal = new ArrayList<>();
		fvClassVal.add("atencao");
		fvClassVal.add("normal");
		Attribute classe = new Attribute("class", fvClassVal);

		ArrayList<Attribute> atts = new ArrayList<Attribute>();

		// Add attributes
		atts.add(largura);
		atts.add(altura);
		atts.add(posicaoX);
		atts.add(posicaoY);
		atts.add(entropia);
		atts.add(sma);
		atts.add(energia);
		atts.add(mediaI);
		atts.add(mediaJ);
		atts.add(varianciaI);
		atts.add(varianciaJ);
		atts.add(desvioPadraoI);
		atts.add(desvioPadraoJ);
		atts.add(contraste);
		atts.add(homogeneidade);
		atts.add(mediaTonsCinza);
		atts.add(classe);
		// Declare Instances which is required since I want to use
		// classification/Prediction
		Instances dataset = new Instances("atributos", atts, 0);

		Instance instance = new DenseInstance(dataset.numAttributes());
		/*
		 * instance.setValue((Attribute)atts.get(0), 83);
		 * instance.setValue((Attribute)atts.get(1), 73);
		 * instance.setValue((Attribute)atts.get(2), 727);
		 * instance.setValue((Attribute)atts.get(3), 453);
		 * instance.setValue((Attribute)atts.get(4), 0.769089);
		 * instance.setValue((Attribute)atts.get(5), 0.229862);
		 * instance.setValue((Attribute)atts.get(6), 0.479239);
		 * instance.setValue((Attribute)atts.get(7), 5.080449);
		 * instance.setValue((Attribute)atts.get(8), 5.080449);
		 * instance.setValue((Attribute)atts.get(9), 0.714843);
		 * instance.setValue((Attribute)atts.get(10), 0.714843);
		 * instance.setValue((Attribute)atts.get(11), 0.845448);
		 * instance.setValue((Attribute)atts.get(12), 0.845448);
		 * instance.setValue((Attribute)atts.get(13), 0.174736);
		 * instance.setValue((Attribute)atts.get(14), 0.912632);
		 */

		instance.setValue((Attribute) atts.get(0), 87);
		instance.setValue((Attribute) atts.get(1), 55);
		instance.setValue((Attribute) atts.get(2), 1662);
		instance.setValue((Attribute) atts.get(3), 1051);
		instance.setValue((Attribute) atts.get(4), 0.702898);
		instance.setValue((Attribute) atts.get(5), 0.317633);
		instance.setValue((Attribute) atts.get(6), 0.563395);
		instance.setValue((Attribute) atts.get(7), 5.251202);
		instance.setValue((Attribute) atts.get(8), 5.251202);
		instance.setValue((Attribute) atts.get(9), 0.519155);
		instance.setValue((Attribute) atts.get(10), 0.519155);
		instance.setValue((Attribute) atts.get(11), 0.72052);
		instance.setValue((Attribute) atts.get(12), 0.72052);
		instance.setValue((Attribute) atts.get(13), 0.193526);
		instance.setValue((Attribute) atts.get(14), 0.903811);
		instance.setValue((Attribute) atts.get(15), 92);
		dataset.add(instance);
		// Define class attribute position
		dataset.setClassIndex(dataset.numAttributes() - 1);

		System.out.println(dataset.instance(0));
		// Will print 0 if it's a "yes", and 1 if it's a "no"
		// System.out.print(new WekaWrapper().runClassifier(new WekaWrapper(),
		// null));

		WekaWrapper weka = new WekaWrapper();
		weka.buildClassifier(dataset);
		System.out.println(weka.classifyInstance(dataset.instance(0)));

		// 0 para atencao e 1 para normal
	}
}
