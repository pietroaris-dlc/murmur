package is.murmur.Model.Helpers;

import is.murmur.Model.Beans.*;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface Collision {
    static boolean detect(Registereduser user, LocalDate day, TimeInterval interval) {
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<Daily> dailies = entityManager
                    .createQuery("select d from Planner p join Daily d on p.schedule = d.schedule where p.user = :user and d.day = :day", Daily.class)
                    .setParameter("day", day)
                    .setParameter("user", user)
                    .getResultList();
            for (Daily daily : dailies) {
                LocalTime dailyStart = daily.getStartHour();
                LocalTime dailyEnd = daily.getEndHour();
                if (
                        (dailyStart.isBefore(interval.getStart()) && dailyEnd.isAfter(interval.getStart()))
                        || (dailyStart.isAfter(interval.getStart())) && dailyStart.isBefore(interval.getEnd())
                        || (dailyStart.equals(interval.getStart()))
                        || (dailyEnd.equals(interval.getEnd()))
                ) return true;
            }
            return false;
        } finally {
            entityManager.close();
        }
    }

    static boolean detect(Registereduser user, Long scheduleId){
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try{
            Schedule schedule = em.find(Schedule.class, scheduleId);
            if(schedule != null){
                if(schedule.getType().equals("DAILY")){
                    Daily daily = em.find(Daily.class, scheduleId);
                    return Collision.detect(user, daily.getDay(), new TimeInterval(daily.getStartHour(), daily.getEndHour()));
                } else if(schedule.getType().equals("WEEKLY")){
                    Weekly weekly = em.find(Weekly.class, scheduleId);
                    List<Weekday> weekdays = em.createQuery(
                            "select wd from Weekday wd where wd.weekly = :weekly",
                            Weekday.class)
                            .setParameter("weekly", weekly)
                            .getResultList();
                    for(Weekday wd : weekdays){
                        for (LocalDate date = weekly.getStartDate(); !date.isAfter(weekly.getEndDate()); date = date.plusDays(1)) {
                            String dayOfWeek = date.getDayOfWeek().toString();
                            if (wd.getId().getDayOfWeek().equals(dayOfWeek)) {
                                if(Collision.detect(user, date, new TimeInterval(wd.getStartHour(), wd.getEndHour()))) return true;
                            }
                        }
                    }
                    return false;
                }
            }
            return true;
        } finally {
            em.close();
        }
    }
}
