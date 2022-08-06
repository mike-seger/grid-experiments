package com.net128.lib.spring.jpa.csv;

import com.net128.lib.spring.jpa.csv.util.Attribute;
import com.net128.lib.spring.jpa.csv.util.JpaMapper;
import com.net128.lib.spring.jpa.csv.util.Props;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JpaService {
    private final JpaMapper jpaMapper;

    public JpaService(JpaMapper jpaMapper) {
        this.jpaMapper = jpaMapper;
    }

    public List<String> getEntities() {
        return jpaMapper.getEntities();
    }

    public Map<String, Attribute> getAttributes(String entity) {
        return jpaMapper.getAttributes(entity);
    }

    @SuppressWarnings("unchecked")
    public <T> int deleteIds(String entityName, List<Long> ids) {
        Class<T> entityClass = (Class<T>) jpaMapper.getEntityClass(entityName);
        JpaRepository<T, Long> jpaRepository =
                (JpaRepository<T, Long>) jpaMapper.getEntityRepository(entityClass);
        jpaRepository.deleteAllById(ids);
        return ids.size();
    }

    public JpaService.Configuration getConfiguration() {
        var configuration = new JpaService.Configuration();
        jpaMapper.getEntities().forEach(e -> {
            var idFieldName = jpaMapper.getIdFieldName(e);
            var entity = jpaMapper.getEntityClass(e);
            var attributes = jpaMapper.getAttributes(e);
            configuration.addEntity(
                entity.getName(), e,
                "?tabSeparated=false&entity="+e,
                "?tabSeparated=false&entity="+e,
                "?entity="+e+"&",
                attributes.get(idFieldName)!=null?idFieldName:null,
                Props.isSortable(entity),
                attributes
            );
        });
        return configuration;
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
            String idField;
            boolean sortable;
            List<Attribute> attributes;
        }
        void addEntity(String id, String name, String getUri, String putUri, String deleteUri,
               String idField, boolean sortable, LinkedHashMap<String, Attribute> attributeMap) {
            entities.put(id, new Entity(id, name, getUri, putUri, deleteUri, idField, sortable, new ArrayList<>(attributeMap.values())));
        }
    }
}
