package is.murmur.Model.Entities;

public class Rejected_component {

    private Long application_id;
    private String rejection_note;

    public Rejected_component(Long application_id, String rejection_note) {
        this.application_id = application_id;
        this.rejection_note = rejection_note;
    }

    public Long getApplication_id() { return application_id; }
    public void setApplication_id(Long application_id) { this.application_id = application_id; }

    public String getRejection_note() { return rejection_note; }
    public void setRejection_note(String rejection_note) { this.rejection_note = rejection_note; }
}
