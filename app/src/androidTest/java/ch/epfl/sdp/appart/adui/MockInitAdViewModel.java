package ch.epfl.sdp.appart.adui;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.ad.AdViewModel;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for testing AdActivity in the case init fails.
 */
@HiltViewModel
public class MockInitAdViewModel extends AdViewModel {

    @Inject
    public MockInitAdViewModel(DatabaseService db) {
        super(db);
    }

    @Override
    public CompletableFuture<Void> initAd(String id) {
        CompletableFuture<Void> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("mock)"));
        return res;
    }
}