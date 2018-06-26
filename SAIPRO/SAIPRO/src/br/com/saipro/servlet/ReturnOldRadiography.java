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
import org.opencv.highgui.Highgui;

@MultipartConfig
public class ReturnOldRadiography extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Part file = request.getPart("file");
		String filename = getFilename(file);

		if ((filename.toLowerCase().contains(".jpg") || filename.toLowerCase().contains(".png")
				|| filename.toLowerCase().contains(".jpeg") || filename.toLowerCase().contains(".jpeg"))) {

			InputStream filecontent = file.getInputStream();

			File image = new File("imagem-" + filename);
			FileOutputStream outStream = new FileOutputStream(image);
			copyStream(filecontent, outStream);
			outStream.close();

			Mat source = Highgui.imread(image.getAbsolutePath(), Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			
			Highgui.imwrite(filename, source);

			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

			FileInputStream fileInputStream = new FileInputStream(filename);

			PrintWriter out = response.getWriter();
			int i;
			while ((i = fileInputStream.read()) != -1) {
				out.write(i);
			}
			fileInputStream.close();
			out.close();			
		}

	}
	
	private static String getFilename(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1);
			}
		}
		return null;
	}

	public static void copyStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

}