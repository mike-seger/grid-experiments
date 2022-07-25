package com.net128.app.jpa.adminux;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
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
		@RequestParam(name="entity", required = false)
		List<String> entities,
		@Schema( description = "true: TSV output, false: CSV output", required = true, defaultValue = "true" )
		@RequestParam(name="tabSeparated", required = false, defaultValue = "true")
		Boolean tabSeparated,
		@RequestParam(name="zippedSingleTable", required = false, defaultValue = "false")
		Boolean zippedSingleTable,
		HttpServletResponse response
	) throws IOException {
		try (OutputStream os = response.getOutputStream()) {
			if(CollectionUtils.isEmpty(entities)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.setContentType(MediaType.TEXT_PLAIN_VALUE);
				var osw = new OutputStreamWriter(os);
				osw.write("No entity given. Expected at least one of:\n"+csvDbService.getEntities());
			} else {
				response.setStatus(HttpServletResponse.SC_OK);
				//response.setContentType(tabSeparated?TEXT_TSV:TEXT_CSV);
				writeCsv(os, entities, tabSeparated, zippedSingleTable, response);
			}
			os.flush();
		}
	}

	@PutMapping(consumes = { TEXT_TSV, TEXT_CSV, MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<String> putCsv(
		@RequestParam("entity")
		String entity,
		@Schema( allowableValues = {"true", "false"}, description = "true: TSV output, false: CSV output" )
		@RequestParam(name="tabSeparated", required = false)
		Boolean tabSeparated,
		@RequestBody
		String csvData
	) {
		String message;
		var status = HttpStatus.OK;
		try (InputStream is = new ByteArrayInputStream(csvData.getBytes())) {
			csvDbService.readCsv(is, entity, tabSeparated);
			message = "Upload the file successfully: "+entity;
		} catch(Exception e) {
			status = HttpStatus.BAD_REQUEST;
			message = "Could not upload the file: "+entity+"\n"+e.getMessage();
		}
		return ResponseEntity.status(status).body(message);
	}

	@PostMapping(consumes = { TEXT_TSV, TEXT_CSV, MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<String> putCsv(
		@Schema( allowableValues = {"true", "false"}, description = "true: TSV output, false: CSV output" )
		@RequestParam(name="tabSeparated", required = false)
		Boolean tabSeparated,
		@RequestParam("file")
		MultipartFile file
	) {
		String message;
		String fileName="";
		try (InputStream is = file.getInputStream()) {
			if(file.getOriginalFilename() == null) throw new IllegalArgumentException(
				"file.originalFileName must not be empty"
			);
			fileName = file.getOriginalFilename();
			var entity = fileName.replaceAll("[.].*", "");
			csvDbService.readCsv(is, entity, tabSeparated);
			message = "Upload the file successfully: "+fileName;
		} catch(IOException e) {
			message = "Could not upload the file: "+fileName+"\n"+e.getMessage();
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
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
