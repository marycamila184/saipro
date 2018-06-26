
import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;

public class WekaWrapper extends AbstractClassifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Returns only the toString() method.
	 *
	 * @return a string describing the classifier
	 */
	public String globalInfo() {
		return toString();
	}

	/**
	 * Returns the capabilities of this classifier.
	 *
	 * @return the capabilities
	 */
	public Capabilities getCapabilities() {
		weka.core.Capabilities result = new weka.core.Capabilities(this);

		result.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
		result.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
		result.enable(weka.core.Capabilities.Capability.DATE_ATTRIBUTES);
		result.enable(weka.core.Capabilities.Capability.MISSING_VALUES);
		result.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
		result.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);

		result.setMinimumNumberInstances(0);

		return result;
	}

	/**
	 * only checks the data against its capabilities.
	 *
	 * @param i
	 *            the training data
	 */
	public void buildClassifier(Instances i) throws Exception {
		// can classifier handle the data?
		getCapabilities().testWithFail(i);
	}

	/**
	 * Classifies the given instance.
	 *
	 * @param i
	 *            the instance to classify
	 * @return the classification result
	 */
	public double classifyInstance(Instance i) throws Exception {
		Object[] s = new Object[i.numAttributes()];

		for (int j = 0; j < s.length; j++) {
			if (!i.isMissing(j)) {
				if (i.attribute(j).isNominal())
					s[j] = new String(i.stringValue(j));
				else if (i.attribute(j).isNumeric())
					s[j] = new Double(i.value(j));
			}
		}

		// set class value to missing
		s[i.classIndex()] = null;

		return WekaClassifier.classify(s);
	}

	/**
	 * Returns the revision string.
	 * 
	 * @return the revision
	 */
	public String getRevision() {
		return RevisionUtils.extract("1.0");
	}

	/**
	 * Returns only the classnames and what classifier it is based on.
	 *
	 * @return a short description
	 */
	public String toString() {
		return "Auto-generated classifier wrapper, based on weka.classifiers.trees.J48Consolidated (generated with Weka 3.8.1).\n"
				+ this.getClass().getName() + "/WekaClassifier";
	}
}

class WekaClassifier {

	public static double classify(Object[] i) throws Exception {

		double p = Double.NaN;
		p = WekaClassifier.N76baae0822(i);
		return p;
	}

	static double N76baae0822(Object[] i) {
		double p = Double.NaN;
		if (i[9] == null) {
			p = 1;
		} else if (((Double) i[9]).doubleValue() <= 0.53147) {
			p = WekaClassifier.N132bc94a23(i);
		} else if (((Double) i[9]).doubleValue() > 0.53147) {
			p = WekaClassifier.N6a3d183237(i);
		}
		return p;
	}

	static double N132bc94a23(Object[] i) {
		double p = Double.NaN;
		if (i[14] == null) {
			p = 1;
		} else if (((Double) i[14]).doubleValue() <= 0.932297) {
			p = 1;
		} else if (((Double) i[14]).doubleValue() > 0.932297) {
			p = WekaClassifier.N7ce07ff024(i);
		}
		return p;
	}

	static double N7ce07ff024(Object[] i) {
		double p = Double.NaN;
		if (i[9] == null) {
			p = 1;
		} else if (((Double) i[9]).doubleValue() <= 0.200989) {
			p = WekaClassifier.N1ed2914025(i);
		} else if (((Double) i[9]).doubleValue() > 0.200989) {
			p = WekaClassifier.N2fd8eb9c31(i);
		}
		return p;
	}

	static double N1ed2914025(Object[] i) {
		double p = Double.NaN;
		if (i[14] == null) {
			p = 1;
		} else if (((Double) i[14]).doubleValue() <= 0.97485) {
			p = 1;
		} else if (((Double) i[14]).doubleValue() > 0.97485) {
			p = WekaClassifier.N3dbba67c26(i);
		}
		return p;
	}

	static double N3dbba67c26(Object[] i) {
		double p = Double.NaN;
		if (i[9] == null) {
			p = 1;
		} else if (((Double) i[9]).doubleValue() <= 0.051695) {
			p = 1;
		} else if (((Double) i[9]).doubleValue() > 0.051695) {
			p = WekaClassifier.N7017591827(i);
		}
		return p;
	}

	static double N7017591827(Object[] i) {
		double p = Double.NaN;
		if (i[5] == null) {
			p = 0;
		} else if (((Double) i[5]).doubleValue() <= 0.65953) {
			p = 0;
		} else if (((Double) i[5]).doubleValue() > 0.65953) {
			p = WekaClassifier.Nf77a71528(i);
		}
		return p;
	}

	static double Nf77a71528(Object[] i) {
		double p = Double.NaN;
		if (i[13] == null) {
			p = 0;
		} else if (((Double) i[13]).doubleValue() <= 0.02012) {
			p = 0;
		} else if (((Double) i[13]).doubleValue() > 0.02012) {
			p = WekaClassifier.N5257f2a29(i);
		}
		return p;
	}

	static double N5257f2a29(Object[] i) {
		double p = Double.NaN;
		if (i[5] == null) {
			p = 1;
		} else if (((Double) i[5]).doubleValue() <= 0.752833) {
			p = WekaClassifier.N654da1b430(i);
		} else if (((Double) i[5]).doubleValue() > 0.752833) {
			p = 1;
		}
		return p;
	}

	static double N654da1b430(Object[] i) {
		double p = Double.NaN;
		if (i[4] == null) {
			p = 0;
		} else if (((Double) i[4]).doubleValue() <= 0.274929) {
			p = 0;
		} else if (((Double) i[4]).doubleValue() > 0.274929) {
			p = 1;
		}
		return p;
	}

	static double N2fd8eb9c31(Object[] i) {
		double p = Double.NaN;
		if (i[13] == null) {
			p = 0;
		} else if (((Double) i[13]).doubleValue() <= 0.072697) {
			p = 0;
		} else if (((Double) i[13]).doubleValue() > 0.072697) {
			p = WekaClassifier.N1da5ae7d32(i);
		}
		return p;
	}

	static double N1da5ae7d32(Object[] i) {
		double p = Double.NaN;
		if (i[9] == null) {
			p = 1;
		} else if (((Double) i[9]).doubleValue() <= 0.298916) {
			p = 1;
		} else if (((Double) i[9]).doubleValue() > 0.298916) {
			p = WekaClassifier.N33d75ecc33(i);
		}
		return p;
	}

	static double N33d75ecc33(Object[] i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 0;
		} else if (((Double) i[0]).doubleValue() <= 44.0) {
			p = WekaClassifier.N3b9f090334(i);
		} else if (((Double) i[0]).doubleValue() > 44.0) {
			p = 0;
		}
		return p;
	}

	static double N3b9f090334(Object[] i) {
		double p = Double.NaN;
		if (i[2] == null) {
			p = 1;
		} else if (((Double) i[2]).doubleValue() <= 636.0) {
			p = 1;
		} else if (((Double) i[2]).doubleValue() > 636.0) {
			p = WekaClassifier.N53f391e735(i);
		}
		return p;
	}

	static double N53f391e735(Object[] i) {
		double p = Double.NaN;
		if (i[13] == null) {
			p = 0;
		} else if (((Double) i[13]).doubleValue() <= 0.115451) {
			p = 0;
		} else if (((Double) i[13]).doubleValue() > 0.115451) {
			p = WekaClassifier.N63150ba236(i);
		}
		return p;
	}

	static double N63150ba236(Object[] i) {
		double p = Double.NaN;
		if (i[2] == null) {
			p = 1;
		} else if (((Double) i[2]).doubleValue() <= 1486.0) {
			p = 1;
		} else if (((Double) i[2]).doubleValue() > 1486.0) {
			p = 0;
		}
		return p;
	}

	static double N6a3d183237(Object[] i) {
		double p = Double.NaN;
		if (i[13] == null) {
			p = 0;
		} else if (((Double) i[13]).doubleValue() <= 0.20998) {
			p = 0;
		} else if (((Double) i[13]).doubleValue() > 0.20998) {
			p = WekaClassifier.N29a9d36738(i);
		}
		return p;
	}

	static double N29a9d36738(Object[] i) {
		double p = Double.NaN;
		if (i[9] == null) {
			p = 1;
		} else if (((Double) i[9]).doubleValue() <= 1.056498) {
			p = WekaClassifier.N661a031c39(i);
		} else if (((Double) i[9]).doubleValue() > 1.056498) {
			p = WekaClassifier.N2fe73d6f41(i);
		}
		return p;
	}

	static double N661a031c39(Object[] i) {
		double p = Double.NaN;
		if (i[13] == null) {
			p = 0;
		} else if (((Double) i[13]).doubleValue() <= 0.273558) {
			p = WekaClassifier.N2d994c6640(i);
		} else if (((Double) i[13]).doubleValue() > 0.273558) {
			p = 1;
		}
		return p;
	}

	static double N2d994c6640(Object[] i) {
		double p = Double.NaN;
		if (i[9] == null) {
			p = 1;
		} else if (((Double) i[9]).doubleValue() <= 0.735701) {
			p = 1;
		} else if (((Double) i[9]).doubleValue() > 0.735701) {
			p = 0;
		}
		return p;
	}

	static double N2fe73d6f41(Object[] i) {
		double p = Double.NaN;
		if (i[13] == null) {
			p = 0;
		} else if (((Double) i[13]).doubleValue() <= 0.340836) {
			p = 0;
		} else if (((Double) i[13]).doubleValue() > 0.340836) {
			p = WekaClassifier.N48b2f11f42(i);
		}
		return p;
	}

	static double N48b2f11f42(Object[] i) {
		double p = Double.NaN;
		if (i[2] == null) {
			p = 1;
		} else if (((Double) i[2]).doubleValue() <= 1131.0) {
			p = 1;
		} else if (((Double) i[2]).doubleValue() > 1131.0) {
			p = 0;
		}
		return p;
	}
}
