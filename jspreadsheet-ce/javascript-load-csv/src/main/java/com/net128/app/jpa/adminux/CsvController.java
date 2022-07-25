package com.net128.app.jpa.adminux;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/csv")
public class CsvController {
	private final CsvDbService csvDbService;
	private final String appName;

	public CsvController(CsvDbService csvDbService,
		 @Value("${spring.application.name}") String appName) {
		this.csvDbService = csvDbService;
		this.appName = appName;
	}

	@GetMapping(produces = { APPLICATION_ZIP, TEXT_TSV, TEXT_CSV, MediaType.TEXT_PLAIN_VALUE })
	public void getCsv(
		@RequestParam(name = "entity[]", required = false, defaultValue = "[]")
		List<String> entities,
		@RequestParam(name = "tabSeparated", defaultValue = "true")
		boolean tabSeparated,
		@RequestParam(name = "zippedSingleTable", required = false, defaultValue = "false")
		boolean zippedSingleTable,
		HttpServletResponse response
	) throws IOException {
		try (OutputStream os = response.getOutputStream()) {
			if(entities.size() > 0) {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType(MediaType.TEXT_PLAIN_VALUE);
				var osw = new OutputStreamWriter(os);
				osw.write("No entity given. Expected at least one of:\n"+csvDbService.getEntities());
			} else {
				response.setContentType(tabSeparated?TEXT_TSV:TEXT_CSV);
				writeCsv(os, entities, tabSeparated, zippedSingleTable, response);
			}
			os.flush();
		}
	}

	@GetMapping(path = "/entities")
	public List<String> getTables() {
		return csvDbService.getEntities();
	}

	private void writeCsv(OutputStream os,
		List<String> entities, boolean tabSeparated,
		boolean zippedSingleTable, HttpServletResponse response) throws IOException {
		var realEntities = entities.contains("*")? getTables():entities;
		if(!zippedSingleTable && realEntities.size() == 1) {
			response.setContentType(tabSeparated?TEXT_TSV:TEXT_CSV);
			csvDbService.writeCsv(os, realEntities.get(0), tabSeparated);
		} else {
			var fileName = appName + "-data-export-" + timestampNow() + ".zip";
			response.setContentType(APPLICATION_ZIP);
			response.addHeader("Content-Disposition", "attackment; filename=\""+fileName+"\"");
			csvDbService.writeAllCsvZipped(os, realEntities, tabSeparated);
		}
	}

	private String timestampNow() { return isoTimeStampNow()
		.replaceAll("\\..*", "").replaceAll("[^0-9]", ""); }
	private String isoTimeStampNow() { return isoTimeStamp(LocalDateTime.now());}
	private String isoTimeStamp(LocalDateTime ts) { return ts.format(DateTimeFormatter.ISO_INSTANT); }

	private final static String APPLICATION_ZIP = "application/zip";
	private final static String TEXT_TSV = "text/tab-separated-values";
	private final static String TEXT_CSV = "text/csv";
}
