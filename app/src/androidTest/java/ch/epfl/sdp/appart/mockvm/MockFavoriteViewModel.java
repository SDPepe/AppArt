package ch.epfl.sdp.appart.mockvm;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.favorites.FavoriteViewModel;
import ch.epfl.sdp.appart.login.LoginService;
import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * Mock ViewModel for FavoriteActivity. Overrides initHome so that it returns an
 * exceptionally completed future.
 */
@HiltViewModel
public class MockFavoriteViewModel extends FavoriteViewModel {

    @Inject
    public MockFavoriteViewModel(DatabaseService database, LoginService loginService, LocalDatabaseService localdb) {
        super(database, loginService, localdb);
    }

    @Override
    public CompletableFuture<Void> initHome() {
        CompletableFuture<Void> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("mock"));
        return res;
    }
}