package is.murmur.Model.Helpers;

import is.murmur.Model.Beans.*;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduleHandler implements Collision{

    public static Schedule createSchedule(String scheduleType) {
        Schedule schedule = new Schedule();
        if (scheduleType.equals("DAILY"))
            schedule.setType("DAILY");
        else
            schedule.setType("WEEKLY");
        return schedule;
    }

    public static Schedule saveSchedule(EntityManager em, Schedule schedule) {
        em.persist(schedule);
        em.flush();
        return schedule;
    }

    public static Daily createDaily(LocalDate day, TimeInterval interval) {
        Daily daily = new Daily();
        daily.setSchedule(createSchedule("DAILY"));
        daily.setDay(day);
        daily.setStartHour(interval.getStart());
        daily.setEndHour(interval.getEnd());
        return daily;
    }

    public static Daily saveDaily(EntityManager em, Registereduser user, Daily daily) {

        if(Collision.detect(user, daily.getDay(), new TimeInterval(daily.getStartHour(), daily.getEndHour())))
            return null;

        daily.setId(saveSchedule(em, daily.getSchedule()).getId());

        em.persist(daily);
        em.flush();
        return daily;
    }

    public static Weekly createWeekly(LocalDate startDate, LocalDate endDate) {
        Weekly weekly = new Weekly();
        weekly.setSchedule(createSchedule("WEEKLY"));
        weekly.setStartDate(startDate);
        weekly.setEndDate(endDate);
        return weekly;
    }

    public static Weekly saveWeekly(EntityManager em, Registereduser user,Weekly weekly) {
        saveSchedule(em, weekly.getSchedule());
        em.persist(weekly);
        em.flush();
        return weekly;
    }

    public static List<Weekday> createWeekdays(Weekly weekly, Map<String, TimeInterval> weekdaysMap) {
        List<Weekday> weekdays = new ArrayList<>();

        for (LocalDate date = weekly.getStartDate(); !date.isAfter(weekly.getEndDate()); date = date.plusDays(1)) {
            String dayOfWeek = date.getDayOfWeek().toString();
            if (weekdaysMap.containsKey(dayOfWeek)) {
                TimeInterval interval = weekdaysMap.get(dayOfWeek);

                Weekday weekday;
                weekday = new Weekday();
                WeekdayId weekdayId = new WeekdayId();
                weekdayId.setDayOfWeek(dayOfWeek);
                weekday.setId(weekdayId);

                weekday.setWeekly(weekly);
                weekday.setStartHour(interval.getStart());
                weekday.setEndHour(interval.getEnd());

                weekdays.add(weekday);
            }
        }
        return weekdays;
    }

    public static List<Weekday> saveWeekdays(EntityManager em, Registereduser user, List<Weekday> weekdays) {

        for(Weekday weekday : weekdays) {

            weekdays.remove(weekday);

            weekday.setWeekly(saveWeekly(em, user, weekday.getWeekly()));
            weekday.getId().setWeeklyId(weekday.getWeekly().getId());

            em.persist(weekday);
            em.flush();

            weekdays.add(weekday);
        }

        return weekdays;
    }

    public static List<Daily> addDailyToPlanner(EntityManager em, Registereduser user, Daily daily) {

        if(Collision.detect(user, daily.getDay(), new TimeInterval(daily.getStartHour(), daily.getEndHour()))){
            em.remove(daily);
            em.getTransaction().rollback();
            return null;
        }

        PlannerId plannerId = new PlannerId();
        plannerId.setUserId(user.getId());
        plannerId.setScheduleId(daily.getSchedule().getId());
        em.persist(plannerId);
        em.flush();

        Planner planner = new Planner();
        planner.setUser(user);
        planner.setSchedule(daily.getSchedule());
        planner.setId(plannerId);
        em.persist(planner);
        em.flush();

        return getDailyPlanner(em, user);
    }

    public static List<Daily> getDailyPlanner(EntityManager em, Registereduser user){
        return em.createQuery(
                        "select d from Daily d join Planner p on p.schedule = d.schedule where p.user = :user",
                        Daily.class)
                .setParameter("user", user)
                .getResultList();
    }

    public static List<Weekday> addWeekdayToPlanner(EntityManager em, Registereduser user, Weekday weekday) {

        PlannerId plannerId = new PlannerId();
        plannerId.setUserId(user.getId());
        plannerId.setScheduleId(weekday.getWeekly().getSchedule().getId());
        em.persist(plannerId);
        em.flush();

        Planner planner = new Planner();
        planner.setUser(user);
        planner.setSchedule(weekday.getWeekly().getSchedule());
        planner.setId(plannerId);
        em.persist(planner);
        em.flush();

        return getWeeklyPlanner(em, user);
    }

    public static List<Weekday> getWeeklyPlanner(EntityManager em, Registereduser user){
        return em.createQuery(
                "select wd from Weekday wd join Planner p on p.schedule = wd.weekly.schedule where p.user = :user",
                Weekday.class).getResultList();
    }
}
