package it.disco.unimib.stan.webapp;

import it.disco.unimib.stan.core.CSVTable;
import it.disco.unimib.stan.core.FileResource;
import it.disco.unimib.stan.core.WorkingAreaPaths;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
//import javax.swing.JOptionPane;


public class PreviewPage implements ApplicationHandler {

	@Override
	public String route() {
		return "preview";
	}

	@Override
	public String process(Communication requestAndResponse) throws Exception {
		String error = requestAndResponse.getParameter("error");
		CookieManager cookieManager = new CookieManager(requestAndResponse);
		WorkingTable workingTable = new WorkingTable(cookieManager.getTable(), cookieManager.getUser());
		
		if(!nullOrEmpty(error) || nullOrEmpty(workingTable.getTableName())){
			requestAndResponse.setResponseStatus(400);
			if(nullOrEmpty(error)) error = "Missing table";
			return new ErrorTemplate("preview", "", error).page();
		}
		else{
			String tableName = workingTable.getTableName();
			
			String path = new WorkingAreaPaths().tables(cookieManager.getUser()).file(tableName).path();
			CSVTable table = new CSVTable(new FileResource(path))
											.withSeparator(workingTable.getSeparator())
											.withTextDelimiter(workingTable.getDelimiter());
			if(workingTable.hasHeader()) table.withHeader();

			return new Template("preview").breadcrumb("Preview of " + urlEncode(tableName))
											 .header(table.getHeader().asAnnotatedList(workingTable))
											 .separator(workingTable.getSeparator())
											 .delimiter(workingTable.getDelimiter())
											 .tablename(urlEncode(tableName))
											 .hasHeader(workingTable.hasHeader())
											 .info(requestAndResponse.getParameter("info"))
											 .page();
		}
	}

	private boolean nullOrEmpty(String value) {
		return (value == null || value.isEmpty());
	}

	private String urlEncode(String toEncode) throws UnsupportedEncodingException {
		return URLEncoder.encode(toEncode, "UTF-8");
	}
}