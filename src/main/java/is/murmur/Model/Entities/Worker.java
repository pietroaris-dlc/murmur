package is.murmur.Model.Entities;

import java.time.LocalDateTime;

public class Worker extends Client{
    private WorkerComponent workerComponent;
    public Worker(Long id, int password, String firstName, String last_name, LocalDateTime birthDate, String birthCity, String birthCountry, String taxCode, double totalExpenditure, boolean admin, boolean lock, WorkerComponent workerComponent) {
        super(id, password, firstName, last_name, birthDate, birthCity, birthCountry, taxCode, totalExpenditure, admin, lock);
        setType(Type.WORKER);
        this.workerComponent = workerComponent;
    }
    public WorkerComponent getWorkerComponent() { return workerComponent;}
    public void setWorkerComponent(WorkerComponent workerComponent) { this.workerComponent = workerComponent;}
}
