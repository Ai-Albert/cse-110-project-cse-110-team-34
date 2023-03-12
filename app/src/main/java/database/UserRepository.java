package database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import model.User;

public class UserRepository {
    private final UserDao dao;
    private final UserAPI api;
    private final MutableLiveData<User> realLiveContent;

    private final MediatorLiveData<User> liveContent;

    private ScheduledFuture<?> clockFuture;

    public UserRepository(UserDao dao) {
        this.dao = dao;

        api = new UserAPI();
        realLiveContent = new MediatorLiveData<>();

        liveContent = new MediatorLiveData<>();
        liveContent.addSource(realLiveContent, liveContent::postValue);
    }

    /**
     * Gets the latest data from either the local database or remotely, whichever was
     * more recently updated.
     **/
    public LiveData<User> getSynced(String public_code) {
        MediatorLiveData<User> user = new MediatorLiveData<User>();

        Observer<User> updateFromRemote = theirUser -> {
            User ourUser = user.getValue();
            if (theirUser == null) return; // do nothing
            if (ourUser == null || ourUser.version < theirUser.version) {
                upsertLocal(theirUser);
            }
        };

        // If we get a local update, pass it on.
        user.addSource(getLocal(public_code), user::postValue);
        // If we get a remote update, update the local version (triggering the above observer)
        user.addSource(getRemote(public_code), updateFromRemote);

        return user;
    }

    public void upsertSynced(String private_code, User user) {
        upsertLocal(user);
        upsertRemote(private_code, user);
    }

    public LiveData<User> getLocal(String public_code) {
        return dao.get(public_code);
    }

    public LiveData<List<User>> getAllLocal() {
        return dao.getAll();
    }

    public void upsertLocal(User user) {
        dao.upsert(user);
    }

    public void deleteLocal(User user) {
        dao.delete(user);
    }

    public boolean existsLocal(String public_code) {
        return dao.exists(public_code);
    }

    /**
     * Gets the latest data from the API, if it exists.
     **/
    public LiveData<User> getRemote(String public_code) {
        if (clockFuture != null) {
            clockFuture.cancel(true);
        }
        User user = api.get(public_code);

        if (user != null) {
            upsertLocal(user);
        }
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        clockFuture = executor.scheduleAtFixedRate(() -> {
            User temp = api.get(public_code);
            if (temp != null) {
                realLiveContent.postValue(temp);
            }
        }, 0, 3, TimeUnit.SECONDS);
        return liveContent;
    }

    public void upsertRemote(String private_code, User user) {
        api.put(private_code, user);
    }
}