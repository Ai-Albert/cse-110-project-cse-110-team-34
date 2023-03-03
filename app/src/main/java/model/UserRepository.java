package model;

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

    public LiveData<User> getSynced(String name) {
        MediatorLiveData<User> user = new MediatorLiveData<User>();

        Observer<User> updateFromRemote = theirUser -> {
            User ourUser = user.getValue();
            if (theirUser == null) return; // do nothing
            if (ourUser == null || ourUser.version < theirUser.version) {
                upsertLocal(theirUser);
            }
        };

        // If we get a local update, pass it on.
        user.addSource(getLocal(name), user::postValue);
        // If we get a remote update, update the local version (triggering the above observer)
        user.addSource(getRemote(name), updateFromRemote);

        return user;
    }

    public void upsertSynced(User user) {
        upsertLocal(user);
        upsertRemote(user);
    }

    public LiveData<User> getLocal(String name) {
        return dao.get(name);
    }

    public LiveData<List<User>> getAllLocal() {
        return dao.getAll();
    }

    public void upsertLocal(User user) {
        user.version = Instant.now().getEpochSecond();
        dao.upsert(user);
    }

    public void deleteLocal(User user) {
        dao.delete(user);
    }

    public boolean existsLocal(String name) {
        return dao.exists(name);
    }

    public LiveData<User> getRemote(String name) {
        if (clockFuture != null) {
            clockFuture.cancel(true);
        }
        User user = api.get(name);
        if (user != null) {
            upsertSynced(user);
        }
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        clockFuture = executor.scheduleAtFixedRate(() -> {
            User temp = api.get(name);
            if (temp != null) {
                realLiveContent.postValue(temp);
            }
        }, 0, 3, TimeUnit.SECONDS);
        return liveContent;
    }

    public void upsertRemote(User user) {
        api.put(user.uid, user);
    }
}