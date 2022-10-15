package com.net128.lib.spring.jpa.csv;

import com.net128.lib.spring.jpa.csv.util.Attribute;
import com.net128.lib.spring.jpa.csv.util.JpaMapper;
import com.net128.lib.spring.jpa.csv.util.Props;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JpaService {
    private final JpaMapper jpaMapper;
    private final JpaCsvConfiguration jpaCsvConfiguration;

    public JpaService(JpaMapper jpaMapper, JpaCsvConfiguration jpaCsvConfiguration) {
        this.jpaMapper = jpaMapper;
        this.jpaCsvConfiguration = jpaCsvConfiguration;
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
           // .get(e.toLowerCase());
            var entity = jpaMapper.getEntityClass(e);
            var attributes = orderedAttributes(e);

            configuration.addEntity(
                entity.getName(), e,
                "/"+e+".csv",
                "?entity="+e,
                "?entity="+e+"&",
                attributes.get(idFieldName)!=null?idFieldName:null,
                Props.isSortable(entity),
                attributes
            );
        });
        return configuration;
    }

    private LinkedHashMap<String, Attribute> orderedAttributes(String entity) {
        var attributeOrderMap = jpaCsvConfiguration.getAttributeOrders();
        var attributeOrders = attributeOrderMap.get(entity.replace("_", "").toLowerCase());
        if(attributeOrders==null) return jpaMapper.getAttributes(entity);
        Comparator<Map.Entry<String, Attribute>> attributeComparator = (e1, e2) -> {
            var pos1 = attributeOrders.indexOf(e1.getKey().toLowerCase());
            var pos2 = attributeOrders.indexOf(e2.getKey().toLowerCase());
            if(pos1==pos2) return e1.getKey().compareTo(e2.getKey());
            if(pos1<0) return 1;
            if(pos2<0) return -1;
            return Integer.compare(pos1, pos2);
        };

        return jpaMapper.getAttributes(entity).entrySet().stream().sorted(attributeComparator)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (x, y) -> y, LinkedHashMap::new));
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
