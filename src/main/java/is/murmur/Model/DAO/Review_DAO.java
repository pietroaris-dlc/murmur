package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Review;

import java.util.List;

public abstract class Review_DAO implements Generic_DAO {
    private static final String[] KEYS = {"contract_id"};

    public static Review selectByKeys(Long contractId) {
        Object[] keysValues = {contractId};
        return Generic_DAO.selectByKeys(Review.class, KEYS, keysValues);
    }

    public static boolean insert(Review review) {
        return Generic_DAO.doInsert(review);
    }

    public static boolean delete(Review review) {
        Object[] keysValues = {review.getContractId()};
        return Generic_DAO.doDelete(Review.class, KEYS, keysValues);
    }

    public static boolean update(Review review, String fieldName, Object value) {
        Object[] keysValues = {review.getContractId()};
        if (fieldName.equals("contract_id")) return false;
        return Generic_DAO.doUpdate(Review.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Review> getAll() {
        return Generic_DAO.selectAll(Review.class);
    }

    public static List<Review> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Review.class, fields, values);
    }

    public static List<Review> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Review.class, field, fieldValue);
    }
}
