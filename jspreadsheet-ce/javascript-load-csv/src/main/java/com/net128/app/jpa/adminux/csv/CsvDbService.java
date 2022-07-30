package com.net128.app.jpa.adminux.csv;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.net128.app.jpa.adminux.data.util.JpaMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class CsvDbService {
	private final CsvMapper readerMapper;
	private final CsvSchema readerSchema ;
	private final CsvSchema readerTsvSchema;
	private final JpaMapper jpaMapper;

	public CsvDbService(JpaMapper jpaMapper) {
		this.jpaMapper = jpaMapper;
		readerMapper = new CsvMapper()
			.enable(CsvParser.Feature.TRIM_SPACES)
			.enable(CsvParser.Feature.SKIP_EMPTY_LINES)
			.enable(CsvParser.Feature.EMPTY_STRING_AS_NULL);
		readerSchema = CsvSchema.emptySchema()
				.withHeader().withLineSeparator(new String(CsvSchema.DEFAULT_LINEFEED));
		readerTsvSchema = readerSchema.withColumnSeparator('\t').withoutQuoteChar();
	}

	public void writeAllCsvZipped(OutputStream os,
		  List<String> entities, Boolean tabSeparated) throws IOException {
		try (var zos = new ZipOutputStream(os)) {
			for(var entity : entities)
				writeCsvZipEntry(zos, entity, tabSeparated);
			zos.flush();
			zos.finish();
		}
	}

	private void writeCsvZipEntry(ZipOutputStream zipOutputStream, String entity, Boolean tabSeparated) throws IOException {
		if(tabSeparated==null) tabSeparated=true;
		try (var bos = new ByteArrayInputStream(writeCsv(entity, tabSeparated).getBytes())) {
			var fileName = entity + (tabSeparated?".tsv":".csv");
			zipOutputStream.putNextEntry(new ZipEntry(fileName));
			IOUtils.copy(bos, zipOutputStream);
		}
		zipOutputStream.closeEntry();
	}

	public String writeCsv(String entity, Boolean tabSeparated) throws IOException {
		try (var bos = new ByteArrayOutputStream()) {
			writeCsv(bos, entity, tabSeparated);
			return bos.toString();
		}
	}

	public void writeCsv(OutputStream os, String entity, Boolean tabSeparated) throws IOException {
		if(tabSeparated==null) tabSeparated=true;
		var entityClass = jpaMapper.getEntityClass(entity);
		var jpaRepository = jpaMapper.getEntityRepository(entityClass);
		var jsonFactory = new CsvFactory().configure(
			JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		var mapper = new CsvMapper().schemaFor(entityClass)
			.withLineSeparator("\n").withHeader();
		if(tabSeparated) mapper = mapper.withColumnSeparator('\t').withoutQuoteChar();
		try (var cos = os) {
			var writer = new ObjectMapper(jsonFactory).writer(mapper).writeValues(cos);
			AtomicInteger count = new AtomicInteger();
			jpaRepository.findAll().forEach(e -> {
				try { writer.write(e);
				} catch (IOException ex) { log.error("Failed to write entity");}
				count.getAndIncrement();
			});
			log.info("Loaded {} items of {}", count.get(), entityClass.getSimpleName());
		}
	}

	@SuppressWarnings("unchecked")
	public <T> void readCsv(InputStream inputStream, String entityName, Boolean tabSeparated) throws IOException {
		if(tabSeparated==null) {
			SvInputStream svInputStream = new SvInputStream(inputStream, 2048);
			tabSeparated = svInputStream.isTsv();
			inputStream = svInputStream;
		}
		Class<T> entityClass = (Class<T>) jpaMapper.getEntityClass(entityName);
		JpaRepository<T, Long> jpaRepository =
			(JpaRepository<T, Long>) jpaMapper.getEntityRepository(entityClass);
		jpaRepository.deleteAll();
		saveEntities(inputStream, jpaRepository, entityClass, tabSeparated);
	}

	private <T> int saveEntities(InputStream inputStream, JpaRepository<T, Long> jpaRepository,
		Class<T> entityClass, boolean tabSeparated) throws IOException {
		try (InputStream is = inputStream) {
			var reader = genericCsvReader(entityClass, is, tabSeparated);
			var count = 0;
			if (reader != null) {
				while (reader.hasNext()) {
					var item = reader.next();
					jpaRepository.save(item);
					count++;
				}
				log.info("Saved {} items of {}", count, entityClass.getSimpleName());
			}
			return count;
		}
	}

	public <T> MappingIterator<T> genericCsvReader(Class<T> clazz,
			InputStream inputStream, boolean tabSeparated) throws IOException {
		return readerMapper.readerFor(clazz)
			.with(tabSeparated?readerTsvSchema:readerSchema)
			.readValues(inputStream);
	}
}
