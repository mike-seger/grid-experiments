package com.net128.lib.spring.jpa.csv;

import com.net128.lib.spring.jpa.csv.util.Attribute;
import com.net128.lib.spring.jpa.csv.util.JpaMapper;
import com.net128.lib.spring.jpa.csv.util.Props;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/csv")
@ComponentScan(basePackageClasses = CsvController.class)
public class CsvController {
	private final CsvDbService csvDbService;
	private final JpaMapper jpaMapper;
	private final String appName;

	private final static String uploadMsg = "Successfully uploaded: ";
	private final static String uploadFailedMsg = "Failed uploading: ";
	private final static String deleteMsg = "Successfully deleted: ";
	private final static String deleteFailedMsg = "Failed deleting: ";

	public CsvController(CsvDbService csvDbService, JpaMapper jpaMapper, @Value("${spring.application.name}") String appName) {
		this.csvDbService = csvDbService;
		this.appName = appName;
		this.jpaMapper = jpaMapper;
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
				osw.write("No entity given. Expected at least one of:\n"+jpaMapper.getEntities());
			} else {
				response.setStatus(HttpServletResponse.SC_OK);
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
			var count = csvDbService.readCsv(is, entity, tabSeparated);
			message = uploadMsg+entity+" (count="+count+")";
		} catch(Exception e) {
			status = HttpStatus.BAD_REQUEST;
			message = uploadFailedMsg+entity+"\n"+e.getMessage();
		}
		return ResponseEntity.status(status).body(message);
	}

	@PostMapping(consumes = { TEXT_TSV, TEXT_CSV, MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<String> postCsv(
		@Schema( allowableValues = {"true", "false"}, description = "true: TSV output, false: CSV output" )
		@RequestParam(name="tabSeparated", required = false)
		Boolean tabSeparated,
		@RequestParam("file")
		MultipartFile file
	) {
		String message;
		var fileName="";
		var status = HttpStatus.OK;
		try (InputStream is = file.getInputStream()) {
			if(file.getOriginalFilename() == null)
				throw new IllegalArgumentException("file.originalFileName must not be empty");
			fileName = file.getOriginalFilename();
			var entity = fileName.replaceAll("[.].*", "");
			csvDbService.readCsv(is, entity, tabSeparated);
			message = uploadMsg+fileName;
		} catch(IOException e) {
			message = uploadFailedMsg+fileName+"\n"+e.getMessage();
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(message);
	}

	@DeleteMapping
	public ResponseEntity<String> deleteIds(
		@RequestParam("entity")
		String entity,
		@RequestParam(name="id")
		List<Long> ids) {
		var status = HttpStatus.OK;
		String message;
		try {
			csvDbService.deleteIds(entity, ids);
			message = deleteMsg;
		} catch(Exception e) {
			message = deleteFailedMsg+"\n"+e.getMessage();
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(message);
	}

	@GetMapping(path = "/entities")
	public List<String> getEntities() {
		return jpaMapper.getEntities();
	}

	@GetMapping(path = "/attributes")
	public Map<String, Attribute> getAttributes(@RequestParam("entity") String entity) {
		return jpaMapper.getAttributes(entity);
	}

	@GetMapping(path = "/configuration")
	public Configuration getConfiguration() {
		var configuration = new Configuration();
		jpaMapper.getEntities().forEach(e -> {
			var attributes = jpaMapper.getAttributes(e);
			var entity = jpaMapper.getEntityClass(e);
			configuration.addEntity(
				e, capitalizeWords(e),
				"?tabSeparated=false&entity="+e,
				"?tabSeparated=false&entity="+e,
				"?entity="+e+"&",
				Props.isSortable(entity),
				Props.isIdentifiable(entity),
				jpaMapper.getAttributes(e)
			);
		});
		return configuration;
	}

	private void writeCsv(OutputStream os,
		List<String> entities, boolean tabSeparated,
		boolean zippedSingleTable, HttpServletResponse response) throws IOException {
		var realEntities = entities.contains("*")? getEntities():entities;
		if(!zippedSingleTable && realEntities.size() == 1) {
			response.setContentType(tabSeparated?TEXT_TSV:TEXT_CSV);
			csvDbService.writeCsv(os, realEntities.get(0), tabSeparated);
		} else {
			var fileName = appName + "-data-export-" + timestampNow() + ".zip";
			response.setContentType(APPLICATION_ZIP);
			response.addHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
			csvDbService.writeAllCsvZipped(os, realEntities, tabSeparated);
		}
	}

	@Data
	static class Configuration {
		TreeMap<String, Entity> entities = new TreeMap<>();
		@Data
		@AllArgsConstructor
		static class Entity{
			String id;
			String name;
			String getUri;
			String putUri;
			String deleteUri;
			boolean sortable;
			boolean identifiable;
			List<Attribute> attributes;
		}
		void addEntity(String id, String name, String getUri, String putUri, String deleteUri,
			   boolean sortable, boolean identifiable, LinkedHashMap<String, Attribute> attributeMap) {
			entities.put(id, new Entity(id, name, getUri, putUri, deleteUri, sortable, identifiable, new ArrayList<>(attributeMap.values())));
		}
	}

	private String capitalizeWords(String s) {
		return Arrays.stream(s.split("[_.-]")).map(w -> {
			if(w.isEmpty()) return w;
			else return w.substring(0,1).toUpperCase()+w.substring(1).trim().toLowerCase(); }
		).collect(Collectors.joining(" "));
	}

	private String timestampNow() { return isoTimeStampNow()
		.replaceAll("\\..*", "").replaceAll("[^0-9]", ""); }
	private String isoTimeStampNow() { return isoTimeStamp(Instant.now());}
	private String isoTimeStamp(Instant ts) { return ts.toString(); }

	private final static String APPLICATION_ZIP = "application/zip";
	private final static String TEXT_TSV = "text/tab-separated-values";
	private final static String TEXT_CSV = "text/csv";
}
