package at.fhv.se.banking.infrastructure.db.utils;

import java.util.List;
import java.util.Optional;

import javax.persistence.TypedQuery;

public class Utils {

    public static <T> Optional<T> getOptionalResult(TypedQuery<T> q) {
        // note: getSingleResult throws an error if there is none
        List<T> result = q.getResultList();
        if (1 != result.size()) {
            return Optional.empty();
        }

        return Optional.of(result.get(0));
    }
}
