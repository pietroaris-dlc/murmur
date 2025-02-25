package is.murmur.Services.SearchStrategy;

public class SearchStrategyFactory {

    /**
     * Restituisce l'istanza della strategia di ricerca in base ai parametri.
     *
     * @param scheduleType "daily" o "weekly"
     * @param serviceMode ad es. "remote", "onsite", "homedelivery"
     * @return Una implementazione di SearchStrategy.
     */
    public static SearchStrategy getStrategy(String scheduleType, String serviceMode) {
        boolean isWeekly = scheduleType.equalsIgnoreCase("weekly");
        serviceMode = serviceMode.toLowerCase().replace(" ", "");

        if (!isWeekly) { // daily
            switch (serviceMode) {
                case "remote":
                    return new DailyRemoteSearchStrategy();
                case "onsite":
                    return new DailyOnsiteSearchStrategy();
                case "homedelivery":
                    return new DailyHomeDeliverySearchStrategy();
                default:
                    throw new IllegalArgumentException("Service mode non supportato per Daily: " + serviceMode);
            }
        } else { // weekly
            switch (serviceMode) {
                case "remote":
                    return new WeeklyRemoteSearchStrategy();
                case "onsite":
                    return new WeeklyOnsiteSearchStrategy();
                case "homedelivery":
                    return new WeeklyHomeDeliverySearchStrategy();
                default:
                    throw new IllegalArgumentException("Service mode non supportato per Weekly: " + serviceMode);
            }
        }
    }
}
