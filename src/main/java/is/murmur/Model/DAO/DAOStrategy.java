package is.murmur.Model.DAO;

public class DAOStrategy<T> {

    private final String tableName;
    private final Class<T> targetEntity;
    private final String[] keys;

    public DAOStrategy(String tableName, Class<T> targetEntity, String[] keys) {
        this.tableName = tableName;
        this.targetEntity = targetEntity;
        this.keys = keys;
    }

    public String getTableName() {
        return tableName;
    }

    public Class<T> getTargetEntity() {
        return targetEntity;
    }

    public String[] getKeys() {
        return keys;
    }
}