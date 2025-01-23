package is.murmur.Model.Entities;
import java.util.UUID;

public class Guest extends GenericUser{
    private long tempId;

    public Guest() {
        this.tempId =  Math.abs(UUID.randomUUID().getMostSignificantBits());;
    }

    public long getTempId() {
        return tempId;
    }

    public void setTempId(long tempId) {
        this.tempId = tempId;
    }
}
