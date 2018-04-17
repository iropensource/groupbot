package com.iranopensourcecommunity.utils;

import com.iranopensourcecommunity.exceptions.DatabaseRolesViolationException;
import com.iranopensourcecommunity.repository.util.Range;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.MalformedParametersException;
import java.util.List;
import java.util.Map;

@Component
public class HibernateFacade {
    private static volatile SessionFactory sessionInstance;

    private HibernateFacade() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionInstance == null) {
            synchronized (HibernateFacade.class) {
                if (sessionInstance == null) {
                    try {
                        StandardServiceRegistry standardRegistry =
                                new StandardServiceRegistryBuilder().configure("hibernate.configs.xml").build();
                        Metadata metaData = new MetadataSources(standardRegistry).getMetadataBuilder().build();
                        sessionInstance = metaData.getSessionFactoryBuilder().build();

                    } catch (Throwable th) {
                        System.err.println("Enitial SessionFactory creation failed" + th);
                        throw new ExceptionInInitializerError(th);
                    }
                }
            }
        }
        return sessionInstance;
    }

    @Transactional
    public Boolean updateEntity(Object entity) throws DatabaseRolesViolationException {
        Session hibernateSession;
        try {
            hibernateSession = getSessionFactory().getCurrentSession();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        Transaction tx = null;

        try {
            tx = hibernateSession.beginTransaction();
            hibernateSession.update(entity);
            tx.commit();
        } catch (ConstraintViolationException e) {
            String messageFormat = "You're trying to update the object [%s] into database with same constraint";
            if (tx != null)
                tx.rollback();
            throw new DatabaseRolesViolationException(
                    String.format(messageFormat, entity.toString()));
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            return false;
        }

        return true;
    }

    @Transactional
    public <T> T saveEntity(Class<T> entityClass, T object) throws DatabaseRolesViolationException {
        Session hibernateSession;
        try {
            hibernateSession = getSessionFactory().getCurrentSession();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Transaction tx = null;

        try {
            tx = hibernateSession.beginTransaction();
            hibernateSession.save(entityClass.getSimpleName(), object);
            tx.commit();

        } catch (ConstraintViolationException e) {
            String messageFormat = "You're trying to save the object [%s] into database with same constraint";
            throw new DatabaseRolesViolationException(
                    String.format(messageFormat, entityClass.getSimpleName()));
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null)
                tx.rollback();
            return null;
        }

        return object;
    }

    @Transactional
    public <T> List<T> fetchAllByParameterMap(String nameQuery, Class<T> entityClass, Map<String, Object> parameterMap) {
        Session hibernateSession;
        try {
            hibernateSession = getSessionFactory().getCurrentSession();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Transaction tx = null;

        List<T> rows;

        try {
            tx = hibernateSession.beginTransaction();
            Query<T> namedQuery = hibernateSession.createNamedQuery(nameQuery, entityClass);

            for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
                namedQuery.setParameter(entry.getKey(), entry.getValue());
            }

            rows = namedQuery.list();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null)
                tx.rollback();
            return null;
        }

        return rows;
    }

    @Transactional
    public <T> List<T> fetchAllEntity(String nameQuery, Class<T> entityClass) {
        return fetchAllEntity(nameQuery, null, entityClass);
    }

    @Transactional
    public <T> List<T> fetchAllEntity(String nameQuery, Range range, Class<T> entityClass) {
        return fetchAllEntity(nameQuery, range, entityClass, null);
    }

    @Transactional
    public <T> List<T> fetchAllEntity(String nameQuery, Range range, Class<T> entityClass, Map<String, Object> parameterMap) {
        Session hibernateSession;
        try {
            hibernateSession = getSessionFactory().getCurrentSession();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Transaction tx = null;

        List<T> rows;

        try {
            tx = hibernateSession.beginTransaction();
            Query<T> namedQuery = hibernateSession.createNamedQuery(nameQuery, entityClass);

            if (parameterMap != null) {
                for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
                    namedQuery.setParameter(entry.getKey(), entry.getValue());
                }
            }

            setRange(range, namedQuery);

            rows = namedQuery.list();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null)
                tx.rollback();
            return null;
        }

        return rows;
    }

    @Transactional
    public <T> List<T> fetchAllEntityBySqlQuery(String sqlQuery, Class<T> entityClass) {
        return fetchAllEntityBySqlQuery(sqlQuery, null, entityClass);
    }

    @Transactional
    public <T> List<T> fetchAllEntityBySqlQuery(String sqlQuery, Range range, Class<T> entityClass) {
        return fetchAllEntityBySqlQuery(sqlQuery, range, entityClass, null);
    }

    @Transactional
    public <T> List<T> fetchAllEntityBySqlQuery(String sqlQuery, Range range, Class<T> entityClass, Map<String, Object> parameterMap) {
        Session hibernateSession;
        try {
            hibernateSession = getSessionFactory().getCurrentSession();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Transaction tx = null;

        List<T> rows;

        try {
            tx = hibernateSession.beginTransaction();
            NativeQuery<T> nativeQuery = hibernateSession.createNativeQuery(sqlQuery, entityClass);
            if (range != null)
                setRange(range, nativeQuery);

            if (parameterMap != null) {
                for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
                    nativeQuery.setParameter(entry.getKey(), entry.getValue());
                }
            }

            rows = nativeQuery.list();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null)
                tx.rollback();
            return null;
        }

        return rows;
    }

    public <T> int updateEntitySqlQuery(String sqlQuery, Class<T> entityClass, Map<String, Object> parameterMap) {
        Session hibernateSession;
        try {
            hibernateSession = getSessionFactory().getCurrentSession();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        Transaction tx = null;

        int effectedRows = -1;

        try {
            tx = hibernateSession.beginTransaction();
            NativeQuery<T> nativeQuery = hibernateSession.createNativeQuery(sqlQuery, entityClass);

            for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
                nativeQuery.setParameter(entry.getKey(), entry.getValue());
            }
            effectedRows = nativeQuery.executeUpdate();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null)
                tx.rollback();
        }

        return effectedRows;
    }

    public <T> Boolean removeEntities(String tableName, String tableIdName, T... entities) {
        Session hibernateSession;
        try {
            hibernateSession = getSessionFactory().getCurrentSession();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        Transaction tx = null;


        try {
            tx = hibernateSession.beginTransaction();
            String nativeQueryStr = createDeleteQuery(tableName, tableIdName, entities);
            NativeQuery query = hibernateSession.createNativeQuery(nativeQueryStr);
            for (int i = 0; i < entities.length; i++) {
                query.setParameter(i + 1, getIdValue(entities[i]));
            }

            query.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null)
                tx.rollback();
            return false;
        }

        return true;
    }

    @Transactional
    public <T> T fetchEntityById(Class<T> entityClass, Object entityId) {
        Session hibernateSession;
        try {
            hibernateSession = getSessionFactory().getCurrentSession();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Transaction tx = null;

        T entity;
        try {
            tx = hibernateSession.beginTransaction();
            entity = hibernateSession.find(entityClass, entityId);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null)
                tx.rollback();
            return null;
        }

        return entity;
    }

    @Transactional
    public <T> Boolean createOrUpdateAll(Class<T> entityClass, T... entities) throws DatabaseRolesViolationException {
        Session hibernateSession;
        try {
            hibernateSession = getSessionFactory().getCurrentSession();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        Transaction tx = null;


        try {
            tx = hibernateSession.beginTransaction();

            for (T elem : entities) {
                hibernateSession.saveOrUpdate(entityClass.getSimpleName(), elem);
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ConstraintViolationException) {
                String messageFormat = "You're trying to persist an object [%s] into database with same constraint";
                throw new DatabaseRolesViolationException(
                        String.format(messageFormat, entityClass.getSimpleName()));
            }
            if (tx != null)
                tx.rollback();
            return false;
        }

        return true;
    }

    private <T> String createDeleteQuery(String tableName, String tableIdName, T... entities) {
        StringBuilder sb = new StringBuilder(String.format("DELETE FROM %s WHERE %s IN ( ", tableName, tableIdName));
        for (int i = 0; i < entities.length; i++) {
            sb.append("?" + " , ");
        }
        String queryStr = sb.toString();
        queryStr = queryStr.substring(0, queryStr.length() - 2);
        queryStr += ")";
        return queryStr;
    }

    private <T> Long getIdValue(T entity) {
        for (Field f : entity.getClass().getDeclaredFields()) {

            if (f.isAnnotationPresent(Id.class)) {
                try {
                    return f.getLong(entity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1L;
    }

    private <T> void setRange(Range range, Query<T> namedQuery) {
        if (range != null) {
            int from = range.getFrom();
            int to = range.getTo();
            if (from >= 0 && to >= 0 && to > from) {
                namedQuery.setParameter("from", range.getFrom());
                namedQuery.setParameter("to", range.getTo());
            } else {
                try {
                    throw new MalformedParametersException();
                } catch (MalformedParametersException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }
}
