package is.murmur.Storage.Services;

import is.murmur.Storage.DAO.Career;
import is.murmur.Storage.DAO.User;
import is.murmur.Storage.Helpers.Collision;
import is.murmur.Storage.Helpers.Criteria;
import is.murmur.Storage.Helpers.JPAUtil;
import is.murmur.Storage.Helpers.TimeInterval;
import is.murmur.Storage.Services.SearchStrategy.WeeklyOnsiteSearchStrategy;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SearchWeeklyHomeDeliveryTest {

    private Map<String, TimeInterval> getValidWeekdays() {
        Map<String, TimeInterval> weekdays = new HashMap<>();
        weekdays.put("MONDAY", new TimeInterval(LocalTime.of(10, 0), LocalTime.of(10, 0)));
        return weekdays;
    }

    private Map<String, TimeInterval> getInvalidWeekdays() {
        Map<String, TimeInterval> weekdays = new HashMap<>();
        weekdays.put("MONDAY", new TimeInterval(LocalTime.of(10, 0), LocalTime.of(11, 0)));
        return weekdays;
    }

    private String validCity() {
        return "Fisciano";
    }

    private String validStreet() {
        return "Via Giovanni Paolo II";
    }

    private String validDistrict() {
        return "Salerno";
    }

    private String validRegion() {
        return "Campania";
    }

    private String validCountry() {
        return "Italia";
    }

    @Test
    void testProfessionNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", null, 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession is required", json.getString("results"));
    }

    @Test
    void testProfessionEmpty() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession is required", json.getString("results"));
    }

    @Test
    void testProfessionContainsDigits() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico23", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession contains digits", json.getString("results"));
    }

    @Test
    void testProfessionContainsSpecialCharacters() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idr@ulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession contains special characters", json.getString("results"));
    }

    @Test
    void testHourlyRateMaxLessThanMin() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 30, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("the hourlyRateMax must be greater than or equal to the hourlyRateMin", json.getString("results"));
    }

    @Test
    void testStartDateNull() {
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(null, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("StartDate cannot be null", json.getString("results"));
    }

    @Test
    void testEndDateNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, null, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("EndDate cannot be null", json.getString("results"));
    }

    @Test
    void testEndDateBeforeStartDate() {
        LocalDate startDate = LocalDate.of(2025, 3, 15);
        LocalDate endDate = LocalDate.of(2025, 2, 10);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("EndDate cannot be before startDate", json.getString("results"));
    }

    @Test
    void testWeeklyIntervalsNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, null)
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("WeeklyIntervals cannot be null", json.getString("results"));
    }

    @Test
    void testWeeklyIntervalsEmpty() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Map<String, TimeInterval> emptyWeekdays = new HashMap<>();
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, emptyWeekdays)
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("WeeklyIntervals cannot be empty", json.getString("results"));
    }

    @Test
    void testWeeklyIntervalInvalid() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getInvalidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("the endHour must be after the StartHour", json.getString("results"));
    }

    @Test
    void testCityNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(null, validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("City cannot be null", json.getString("results"));
    }

    @Test
    void testCityContainsDigits() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote("Fisc14no", validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("City contains digits", json.getString("results"));
    }

    @Test
    void testCityContainsSpecialCharacters() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote("Fis!ciano", validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("City contains special characters", json.getString("results"));
    }

    @Test
    void testStreetNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), null, validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Street cannot be null", json.getString("results"));
    }

    @Test
    void testStreetEmpty() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), "", validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Street cannot be empty", json.getString("results"));
    }

    @Test
    void testStreetTooLong() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), "a".repeat(130), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Street contains more than 128 characters", json.getString("results"));
    }

    @Test
    void testDistrictNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), null, validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("District cannot be null", json.getString("results"));
    }

    @Test
    void testDistrictEmpty() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), "", validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("District cannot be empty", json.getString("results"));
    }

    @Test
    void testDistrictContainsDigits() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), "Sal3rno", validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("District contains digits", json.getString("results"));
    }

    @Test
    void testDistrictContainsSpecialCharacters() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), "Sal!erno", validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("District contains special characters", json.getString("results"));
    }

    @Test
    void testRegionNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), null, validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Region cannot be null", json.getString("results"));
    }

    @Test
    void testRegionEmpty() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), "", validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Region cannot be empty", json.getString("results"));
    }

    @Test
    void testRegionContainsDigits() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), "Camp4nia", validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Region contains digits", json.getString("results"));
    }

    @Test
    void testRegionContainsSpecialCharacters() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), "Camp@nia", validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Region contains special characters", json.getString("results"));
    }

    @Test
    void testCountryNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), null)
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Country cannot be null", json.getString("results"));
    }

    @Test
    void testCountryEmpty() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), "")
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Country cannot be empty", json.getString("results"));
    }

    @Test
    void testCountryContainsDigits() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), "Itali4")
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Country contains digits", json.getString("results"));
    }

    @Test
    void testCountryContainsSpecialCharacters() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), "Ital!a")
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Country contains special characters", json.getString("results"));
    }

    @Test
    void testNoResultsFound() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote("NonexistentCity", "SomeStreet", "SomeDistrict", "SomeRegion", "SomeCountry")
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("No Results Found", json.getString("results"));
    }

    private final EntityManager emStub = new EntityManager() {
        @Override
        public Query createQuery(String qlString) {
            return query;
        }
        @Override public void persist(Object entity) { throw new UnsupportedOperationException(); }
        @Override public <T> T merge(T entity) { throw new UnsupportedOperationException(); }
        @Override public void remove(Object entity) { throw new UnsupportedOperationException(); }
        @Override public <T> T find(Class<T> entityClass, Object primaryKey) { throw new UnsupportedOperationException(); }
        @Override public <T> T getReference(Class<T> entityClass, Object primaryKey) { throw new UnsupportedOperationException(); }
        @Override public void flush() { throw new UnsupportedOperationException(); }
        @Override public void setFlushMode(FlushModeType flushMode) { throw new UnsupportedOperationException(); }
        @Override public FlushModeType getFlushMode() { throw new UnsupportedOperationException(); }
        @Override public void lock(Object entity, LockModeType lockMode) { throw new UnsupportedOperationException(); }
        @Override public void refresh(Object entity) { throw new UnsupportedOperationException(); }
        @Override public void clear() { throw new UnsupportedOperationException(); }
        @Override public boolean contains(Object entity) { throw new UnsupportedOperationException(); }
        @Override public Query createNamedQuery(String name) { throw new UnsupportedOperationException(); }
        @Override public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) { throw new UnsupportedOperationException(); }
        @Override public Query createNativeQuery(String sqlString) { throw new UnsupportedOperationException(); }
        @Override public Query createNativeQuery(String sqlString, Class resultClass) { throw new UnsupportedOperationException(); }
        @Override public Query createNativeQuery(String sqlString, String resultSetMapping) { throw new UnsupportedOperationException(); }
        @Override public void joinTransaction() { throw new UnsupportedOperationException(); }

        @Override
        public boolean isJoinedToTransaction() {
            return false;
        }

        @Override
        public <T> T unwrap(Class<T> aClass) {
            return null;
        }

        @Override public Object getDelegate() { throw new UnsupportedOperationException(); }
        @Override public void close() { return; }
        @Override public boolean isOpen() { return true; }
        @Override public EntityTransaction getTransaction() { throw new UnsupportedOperationException(); }
        @Override public EntityManagerFactory getEntityManagerFactory() { throw new UnsupportedOperationException(); }
        @Override public CriteriaBuilder getCriteriaBuilder() { throw new UnsupportedOperationException(); }
        @Override public Metamodel getMetamodel() { throw new UnsupportedOperationException(); }
        @Override public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) { throw new UnsupportedOperationException(); }
        @Override public EntityGraph<?> createEntityGraph(String graphName) { throw new UnsupportedOperationException(); }

        @Override
        public EntityGraph<?> getEntityGraph(String s) {
            return null;
        }

        @Override public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) { throw new UnsupportedOperationException(); }
        @Override
        public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
            throw new UnsupportedOperationException("find(Class, Object, Map) is not supported in this test stub.");
        }
        @Override
        public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
            throw new UnsupportedOperationException("find(Class, Object, LockModeType) is not supported in this test stub.");
        }
        @Override
        public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
            throw new UnsupportedOperationException("find(Class, Object, LockModeType, Map) is not supported in this test stub.");
        }
        @Override
        public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
            throw new UnsupportedOperationException("lock(Object, LockModeType, Map) is not supported in this test stub.");
        }
        @Override
        public void refresh(Object entity, Map<String, Object> properties) {
            throw new UnsupportedOperationException("refresh(Object, Map) is not supported in this test stub.");
        }
        @Override
        public void refresh(Object entity, LockModeType lockMode) {
            throw new UnsupportedOperationException("refresh(Object, LockModeType) is not supported in this test stub.");
        }
        @Override
        public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
            throw new UnsupportedOperationException("refresh(Object, LockModeType, Map) is not supported in this test stub.");
        }
        @Override
        public void detach(Object entity) {
            throw new UnsupportedOperationException("detach(Object) is not supported in this test stub.");
        }
        @Override
        public LockModeType getLockMode(Object entity) {
            throw new UnsupportedOperationException("getLockMode(Object) is not supported in this test stub.");
        }
        @Override
        public void setProperty(String propertyName, Object value) {
            throw new UnsupportedOperationException("setProperty(String, Object) is not supported in this test stub.");
        }
        @Override
        public Map<String, Object> getProperties() {
            return new HashMap<>();
        }
        @Override
        public Query createQuery(CriteriaUpdate update) {
            throw new UnsupportedOperationException("createQuery(CriteriaUpdate) is not supported in this test stub.");
        }
        @Override
        public Query createQuery(CriteriaDelete deleteQuery) {
            throw new UnsupportedOperationException("createQuery(CriteriaDelete) is not supported in this test stub.");
        }
        @Override
        public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
            throw new UnsupportedOperationException("createQuery(String, Class<T>) is not supported in this test stub.");
        }
        @Override
        public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
            throw new UnsupportedOperationException("createNamedQuery(String, Class<T>) is not supported in this test stub.");
        }
        @Override
        public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
            throw new UnsupportedOperationException("createNamedStoredProcedureQuery(String) is not supported in this test stub.");
        }

        @Override
        public StoredProcedureQuery createStoredProcedureQuery(String s) {
            return null;
        }

        @Override
        public StoredProcedureQuery createStoredProcedureQuery(String s, Class... classes) {
            return null;
        }

        @Override
        public StoredProcedureQuery createStoredProcedureQuery(String s, String... strings) {
            return null;
        }
    };

    private final EntityManagerFactory emfStub = new EntityManagerFactory() {
        public Query createQuery(String qlString) {

            final Map<String, Object> parameters = new HashMap<>();
            return new Query() {
                @Override
                public Query setParameter(String name, Object value) {
                    parameters.put(name, value);
                    return this;
                }

                @Override
                public Query setParameter(String s, Calendar calendar, TemporalType temporalType) {
                    return this;
                }

                @Override
                public Query setParameter(String s, Date date, TemporalType temporalType) {
                    return this;
                }

                @Override
                public Query setParameter(int i, Object o) {
                    return this;
                }

                @Override
                public Query setParameter(int i, Calendar calendar, TemporalType temporalType) {
                    return this;
                }

                @Override
                public Query setParameter(int i, Date date, TemporalType temporalType) {
                    return this;
                }

                @Override
                public Set<Parameter<?>> getParameters() {
                    return Set.of();
                }

                @Override
                public Parameter<?> getParameter(String s) {
                    return null;
                }

                @Override
                public <T> Parameter<T> getParameter(String s, Class<T> aClass) {
                    return null;
                }

                @Override
                public Parameter<?> getParameter(int i) {
                    return null;
                }

                @Override
                public <T> Parameter<T> getParameter(int i, Class<T> aClass) {
                    return null;
                }

                @Override
                public boolean isBound(Parameter<?> parameter) {
                    return false;
                }

                @Override
                public <T> T getParameterValue(Parameter<T> parameter) {
                    return null;
                }

                @Override
                public Object getParameterValue(String s) {
                    return null;
                }

                @Override
                public Object getParameterValue(int i) {
                    return null;
                }

                @Override
                public Query setFlushMode(FlushModeType flushModeType) {
                    return null;
                }

                @Override
                public FlushModeType getFlushMode() {
                    return null;
                }

                @Override
                public Query setLockMode(LockModeType lockModeType) {
                    return null;
                }

                @Override
                public LockModeType getLockMode() {
                    return null;
                }

                @Override
                public <T> T unwrap(Class<T> aClass) {
                    return null;
                }

                @Override public List getResultList() { return new ArrayList(); }
                @Override public Object getSingleResult() { throw new UnsupportedOperationException(); }
                @Override public int executeUpdate() { throw new UnsupportedOperationException(); }
                @Override public Query setMaxResults(int maxResult) { throw new UnsupportedOperationException(); }
                @Override public int getMaxResults() { throw new UnsupportedOperationException(); }
                @Override public Query setFirstResult(int startPosition) { throw new UnsupportedOperationException(); }
                @Override public int getFirstResult() { throw new UnsupportedOperationException(); }
                @Override public Query setHint(String hintName, Object value) { throw new UnsupportedOperationException(); }
                @Override public Map<String, Object> getHints() { throw new UnsupportedOperationException(); }

                @Override
                public <T> Query setParameter(Parameter<T> parameter, T t) {
                    return null;
                }

                @Override
                public Query setParameter(Parameter<Calendar> parameter, Calendar calendar, TemporalType temporalType) {
                    return null;
                }

                @Override
                public Query setParameter(Parameter<Date> parameter, Date date, TemporalType temporalType) {
                    return null;
                }
            };
        }
        @Override
        public EntityManager createEntityManager() {
            return emStub;
        }
        @Override
        public EntityManager createEntityManager(Map map) {
            return emStub;
        }
        @Override
        public EntityManager createEntityManager(SynchronizationType synchronizationType) {
            return emStub;
        }
        @Override
        public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
            return emStub;
        }
        @Override
        public void close() {}
        @Override
        public boolean isOpen() { return true; }
        @Override
        public Map<String, Object> getProperties() { return new HashMap<>(); }
        @Override
        public Cache getCache() { throw new UnsupportedOperationException(); }
        @Override
        public CriteriaBuilder getCriteriaBuilder() { throw new UnsupportedOperationException(); }
        @Override
        public Metamodel getMetamodel() { throw new UnsupportedOperationException(); }
        @Override
        public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
            throw new UnsupportedOperationException("addNamedEntityGraph is not supported in this test stub.");
        }
        @Override
        public PersistenceUnitUtil getPersistenceUnitUtil() {
            throw new UnsupportedOperationException("getPersistenceUnitUtil is not supported in this test stub.");
        }
        @Override
        public void addNamedQuery(String name, Query query) {
            throw new UnsupportedOperationException("addNamedQuery is not supported in this test stub.");
        }
        @Override
        public <T> T unwrap(Class<T> cls) {
            throw new UnsupportedOperationException("unwrap is not supported in this test stub.");
        }
    };

    @Mock
    private Query query;

    @InjectMocks
    private WeeklyOnsiteSearchStrategy strategy;

    private MockedStatic<JPAUtil> jpaUtilMock;
    private MockedStatic<Collision> collisionMock;

    @BeforeEach
    void setUpMocks() {
        jpaUtilMock = Mockito.mockStatic(JPAUtil.class);
        jpaUtilMock.when(JPAUtil::getEntityManagerFactory).thenReturn(emfStub);

        collisionMock = Mockito.mockStatic(Collision.class);
        collisionMock.when(() -> Collision.detect(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(false);
        Mockito.lenient().when(query.setParameter(Mockito.anyString(), Mockito.any())).thenReturn(query);
    }


    @AfterEach
    void tearDownMocks() {
        if (jpaUtilMock != null) {
            jpaUtilMock.close();
        }
        if (collisionMock != null) {
            collisionMock.close();
        }
    }

    @Test
    void testQueryAndCollisionDetection() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote("NonexistentCity", "SomeStreet", "SomeDistrict", "SomeRegion", "SomeCountry")
                .build();

        Mockito.when(emStub.createQuery(Mockito.anyString())).thenReturn(query);

        User fakeUser1 = Mockito.mock(User.class);
        Career fakeCareer1 = Mockito.mock(Career.class);
        Short streetNumber1 = 1;

        User fakeUser2 = Mockito.mock(User.class);
        Career fakeCareer2 = Mockito.mock(Career.class);
        Short streetNumber2 = 2;

        List<Object[]> fakeResults = new ArrayList<>();
        fakeResults.add(new Object[]{fakeUser1, fakeCareer1, streetNumber1});
        fakeResults.add(new Object[]{fakeUser2, fakeCareer2, streetNumber2});
        Mockito.when(query.getResultList()).thenReturn(fakeResults);

        collisionMock.when(() -> Collision.detect(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> {
                    Object worker = invocation.getArgument(0);
                    return worker == fakeUser1;
                });

        String jsonResult = strategy.search(testCriteria);
        JSONObject json = new JSONObject(jsonResult);

        assertTrue(json.has("results"), "Il JSON deve contenere il campo 'results'");
        int resultCount = json.getJSONArray("results").length();
        assertEquals(1, resultCount, "La ricerca deve restituire solo 1 risultato (worker senza collisione)");

        ArgumentCaptor<String> queryStringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(emStub).createQuery(queryStringCaptor.capture());
        String queryString = queryStringCaptor.getValue();
        assertTrue(queryString.contains("u.type = 'WORKER'"), "La query deve filtrare gli utenti di tipo WORKER");
        assertTrue(queryString.contains("c.profession.name = :profession"), "La query deve usare il parametro 'profession'");
        assertTrue(queryString.contains("c.hourlyRate > :hourlyRateMin"), "La query deve usare il parametro 'hourlyRateMin'");
        assertTrue(queryString.contains("c.hourlyRate < :hourlyRateMax"), "La query deve usare il parametro 'hourlyRateMax'");
        assertTrue(queryString.contains("aa.location.city = :city"), "La query deve usare il parametro 'city'");
        assertTrue(queryString.contains("aa.location.street = :street"), "La query deve usare il parametro 'street'");
        assertTrue(queryString.contains("aa.location.district = :district"), "La query deve usare il parametro 'district'");
        assertTrue(queryString.contains("aa.location.region = :region"), "La query deve usare il parametro 'region'");
        assertTrue(queryString.contains("aa.location.country = :country"), "La query deve usare il parametro 'country'");
    }
}