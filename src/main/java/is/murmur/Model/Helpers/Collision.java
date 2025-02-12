package is.murmur.Model.Helpers;

import is.murmur.Model.Entities.Daily;
import is.murmur.Model.Entities.Registereduser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public interface Collision {
    static boolean detect(Registereduser user, Long scheduleId){
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Daily> query = em.createQuery(
                    "select d from Planner p join Daily d on p.schedule.id = d.id WHERE p.user.id = :userId",
                    Daily.class
            );
            query.setParameter("userId", user.getId());
            List<Daily> dailyList = query.getResultList();

            Daily newSchedule = em.find(Daily.class, scheduleId);

            for(Daily existing : dailyList){
                if(existing.getDay().equals(newSchedule.getDay())){
                    if(
                            existing.getStartHour().isBefore(newSchedule.getStartHour())
                                    && existing.getEndHour().isBefore(newSchedule.getStartHour())
                            || existing.getStartHour().isAfter(newSchedule.getEndHour())
                    ){
                        return true;
                    }
                } else return true;
            }
            return false;
        } finally {
            em.close();
        }
    }
}
