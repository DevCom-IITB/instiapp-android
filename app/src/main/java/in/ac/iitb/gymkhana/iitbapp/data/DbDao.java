package in.ac.iitb.gymkhana.iitbapp.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by mrunz on 13/3/18.
 */
@Dao
public interface DbDao {
    @Query("SELECT * FROM events")
    List<Event> getAllEvents();

    @Query("SELECT * FROM bodies")
    List<Body> getAllBodies();

    @Query("SELECT * FROM venues")
    List<Venue> getAllVenues();

    @Query("SELECT * FROM bodies WHERE id == :id")
    public Body[] getBody(String id);

    @Query("SELECT COUNT(*) from events")
    int countEvents();

    @Query("SELECT COUNT(*) from venues")
    int countVenues();

    @Query("SELECT COUNT(*) from bodies")
    int countBodies();

    @Insert
    void insertEvents(List<Event> events);

    @Insert
    void insertBodies(List<Body> bodies);

    @Insert
    void insertBody(Body body);

    @Insert
    void insertVenues(List<Venue> venues);

    @Delete
    void deleteEvent(Event event);

    @Delete
    void deleteVenue(Venue venue);

    @Delete
    void deleteBody(Body body);

    @Query("DELETE from events")
    void deleteEvents();

    @Query("DELETE from venues")
    void deleteVenues();

    @Query("DELETE from bodies")
    void deleteBodies();
}
