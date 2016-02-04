package it.disco.unimib.stan.webapp;

import it.disco.unimib.stan.core.WorkingAreaPaths;

//import javax.swing.JOptionPane;

public class PreviewTablePage implements ApplicationHandler {

	@Override
	public String route() {
		return "preview-table";
	}

	@Override
	public String process(Communication requestAndResponse) throws Exception {
		ParameterValidator parameterValidator = new ParameterValidator("table", "separator");
		
		if(parameterValidator.isCorrectRequest(requestAndResponse)){
			UploadedResource table = requestAndResponse.getUploadedFile("table");
			String separator = requestAndResponse.getParameter("separator");
			String delimiter = requestAndResponse.getParameter("delimiter");
			boolean hasHeader = requestAndResponse.isCheckBoxEnabled("header");

			CookieManager cookieManager = new CookieManager(requestAndResponse);
			cookieManager.setTable(table.name());
			String user = cookieManager.setUserIfMissing();
			
			table.save(new WorkingAreaPaths().tables(user).file(table.name()).path());
			
			new WorkingTable(table.name(), user)
							  .withSeparator(separator)
							  .withDelimiter(delimiter)
							  .withHeader(hasHeader)
							  .save();
			
			requestAndResponse.sendRedirect("/preview", "info", table.name() + " successfully uploaded");
		}
		else{
			requestAndResponse.sendRedirect("/preview", "error", "Error uploading table");
		}
		
		return "Redirected";
	}
}
