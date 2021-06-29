import java.io.File;

//import fiji.threshold.Auto_Threshold;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

public class Automation_Threshold implements PlugIn {

	private static final String SELECIONE_UMA_PASTA = "Selecione uma pasta";

	String matrizDirectoryName;
	String newFolderName = "matriz_plugin";
	boolean autoThresholdMethod;

	public void run(String arg) {
		this.macroCamilaIc();
	}

	private void macroCamilaIc() {
		String directoryName = IJ.getDirectory(SELECIONE_UMA_PASTA);
		matrizDirectoryName = directoryName.concat(newFolderWithBars());
		File folder = new File(directoryName);
		File[] listOfFiles = folder.listFiles();
		File matrizDirectory = new File(matrizDirectoryName);
		matrizDirectory.mkdir();

		for (File file : listOfFiles) {
			if (!file.isDirectory()) {
				process(file);
			}
		}

		IJ.log("Novas imagens salvas em " + this.matrizDirectoryName);
	}

	private String newFolderWithBars() {
		return newFolderName + "\\";
	}

	private void process(File file) {
		String fileName = file.getName();
		String newFileName = "matriz-" + fileName;
		ImagePlus imp = openImage(file, fileName);
		convertImage(fileName, imp); 
		
		saveConvertedImage(newFileName, imp);
	}

	private ImagePlus openImage(File file, String fileName) {
		IJ.log("Abrindo arquivo " + fileName);
		ImagePlus imp = IJ.openImage(file.getAbsolutePath());
		return imp;
	}

	private void convertImage(String fileName, ImagePlus imp) {
		IJ.run(imp, "Set Scale...", "distance=398 known=2 pixel=1 unit=mm");
		IJ.run(imp, "8-bit", "");
		IJ.run(imp, "Enhance Contrast...", "saturated=0.4 normalize equalize");
		IJ.run(imp, "Set Measurements...",
				"add display area mean min perimeter area_fraction limit redirect=None decimal=3");
		IJ.log("Usando threshold minimo 158 e maximo 215.");
		IJ.setRawThreshold(imp, 158, 215, null);
		
		imp.updateAndDraw();
		IJ.run(imp, "Measure", "");
	}

	private void saveConvertedImage(String newFileName, ImagePlus imp) {
		IJ.log("Salvando arquivo " + newFileName);
		IJ.saveAs(imp, "jpeg", matrizDirectoryName + newFileName);
	}

}
