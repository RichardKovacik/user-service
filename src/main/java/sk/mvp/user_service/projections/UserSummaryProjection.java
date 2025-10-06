package sk.mvp.user_service.projections;

import sk.mvp.user_service.model.Gender;

public interface UserSummaryProjection {
    String getUsername();
    String getLastName();
    Gender getGender();
    // ukazka ze sa da takto robit zlozena projections
    ContactProjection getContact();

    interface ContactProjection {
        String getEmail();
    }
}
