package com.net128.lib.spring.jpa.csv;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.net128.lib.spring.jpa.csv.util.JpaMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
		readerMapper = csvMapper()
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
		var jsonFactory = new CsvFactory()
			.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)	;
		var writerMapper = csvMapper().schemaFor(entityClass)
			.withLineSeparator("\n").withHeader()
			.withColumnReordering(true)
			.sortedBy(jpaMapper.getAttributes(entity).keySet().toArray(new String[0]));
		if(tabSeparated) writerMapper = writerMapper.withColumnSeparator('\t').withoutQuoteChar();
		try (var cos = os) {
			var writer = configureMapper(new ObjectMapper(jsonFactory))
				.writer(writerMapper).writeValues(cos);

			AtomicInteger count = new AtomicInteger();
			AtomicBoolean hasErrors = new AtomicBoolean();
			jpaRepository.findAll().forEach(e -> {
				try {
					writer.write(e);
					count.getAndIncrement();
				} catch (IOException ex) {
					if(!hasErrors.get()) {
						log.error("Failed to write entity", ex);
						hasErrors.set(true);
					}
				}
			});
			log.info("Loaded {} items of {}", count.get(), entityClass.getSimpleName());
		}
	}

	@SuppressWarnings("unchecked")
	public <T> int readCsv(InputStream inputStream, String entityName,
			Boolean tabSeparated) throws IOException {
		if(tabSeparated==null) {
			SvInputStream svInputStream = new SvInputStream(inputStream, 2048);
			tabSeparated = svInputStream.isTsv();
			inputStream = svInputStream;
		}
		Class<T> entityClass = (Class<T>) jpaMapper.getEntityClass(entityName);
		JpaRepository<T, Long> jpaRepository =
			(JpaRepository<T, Long>) jpaMapper.getEntityRepository(entityClass);
		jpaRepository.deleteAll();
		return saveEntities(inputStream, jpaRepository, entityClass, tabSeparated);
	}

	@SuppressWarnings("unchecked")
	public <T> void deleteIds(String entityName, List<Long> ids) {
		Class<T> entityClass = (Class<T>) jpaMapper.getEntityClass(entityName);
		JpaRepository<T, Long> jpaRepository =
			(JpaRepository<T, Long>) jpaMapper.getEntityRepository(entityClass);
		jpaRepository.deleteAllById(ids);
	}

	private <T> int saveEntities(InputStream inputStream, JpaRepository<T, Long> jpaRepository,
			 Class<T> entityClass, boolean tabSeparated) throws IOException {
		try (InputStream is = inputStream) {
			var reader = genericCsvReader(entityClass, is, tabSeparated);
			var count = 0;
			if (reader != null) {
				while (reader.hasNext()) {
					var item = reader.next();
					try {
						jpaRepository.save(item);
						count++;
					} catch(Exception e) {
						throw new ValidationException(String.format(
							"Line: %d.\nAttempted to save: %s.\nEncountered error: %s",
							count+2, item, e.getMessage()), e);
					}
				}
				log.info("Saved {} items of {}", count, entityClass.getSimpleName());
			}
			return count;
		}
	}

	private <T> MappingIterator<T> genericCsvReader(Class<T> clazz,
			InputStream inputStream, boolean tabSeparated) throws IOException {
		return readerMapper.readerFor(clazz)
			.with(tabSeparated?readerTsvSchema:readerSchema)
			.readValues(inputStream);
	}

	private ObjectMapper configureMapper(ObjectMapper mapper) {
		final DeserializationConfig originalConfig = mapper.getDeserializationConfig();
		final DeserializationConfig newConfig = originalConfig
			.with(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
			.with(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
		mapper.setConfig(newConfig);
		return mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.findAndRegisterModules()
			.registerModule(new JavaTimeModule())
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
	}

	private CsvMapper csvMapper() {
		CsvMapper csvMapper = new CsvMapper();
		csvMapper = (CsvMapper)configureMapper(csvMapper);
		return csvMapper;
	}
}
