package viewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import database.Database;
import database.UserRepository;
import model.User;

public class LocationViewModel extends AndroidViewModel {
    private LiveData<List<User>> users;
    private final UserRepository repo;

    public LocationViewModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();
        this.repo = new UserRepository(Database.getInstance(context).getUserDao());
    }

    /**
     * Load all notes from the database.
     * @return a LiveData object that will be updated when any notes change.
     */
    public LiveData<List<User>> getUsers() {
        if (users == null) {
            users = repo.getAllLocal();
        }
        return users;
    }

    public LiveData<User> getUserLive(String public_uid) {
        return repo.getLocalLive(public_uid);
    }

    public User getUser(String public_uid) {
        return repo.getLocal(public_uid);
    }
}