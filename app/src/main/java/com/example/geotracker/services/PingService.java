package com.example.geotracker.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.geotracker.R;
import com.example.geotracker.helper_classes.DateConvertor;
import com.example.geotracker.model.LocationEntity;
import com.example.geotracker.model.LocationRepository;
import com.example.geotracker.model.ReminderEntity;
import com.example.geotracker.helper_classes.DistanceCalculator;
import com.example.geotracker.model.ReminderRecordEntity;

import java.util.Date;
import java.util.List;
import java.util.Objects;

// Service for checking if user is close to reminder location
public class PingService extends Service implements LifecycleOwner {
    private static final String LOG_TAG = PingService.class.getSimpleName(); // Log tag

    // Notification constants
    private static final int NOTIFICATION_ID = 2; // Notification ID for the notification
    private static final String NOTIFICATION_CHANNEL_ID = "ReminderChannel"; // Channel ID for the notification
    private LocationRepository repository; // Repository for the database
    private LiveData<List<LocationEntity>> locations; // Live data for locations
    private LiveData<List<ReminderRecordEntity>> reminderRecords; // Live data for reminder records
    private LiveData<List<ReminderEntity>> reminders; // Live data for reminders

    private final IBinder binder = new PingService.LocalBinder(); // Binder for clients to access the service

    // Current latitude and longitude
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;

    // Declare a LifecycleRegistry
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    // Binder class for clients to access the service
    public class LocalBinder extends Binder {
        public PingService getService() {
            return PingService.this;
        }
    }

    // Binder given to clients
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // Constructor
    @Override
    public void onCreate() {
        super.onCreate();

        // Get all locations from the database
        repository = new LocationRepository(getApplication());
        locations = repository.getAllLocations();

        // Define the service's lifecycle
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);

        // Create a notification channel for the foreground service
        createNotificationChannel();
    }

    // Called when the service is started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "PingService started");

        // Observe the locations to see if the user is close to any reminders
        locations.observeForever(locationEntities -> {
            if (locationEntities != null && !locationEntities.isEmpty()) {
                // Get the current location
                LocationEntity latestLocation = locationEntities.get(locationEntities.size() - 1);
                currentLatitude = latestLocation.getLatitude();
                currentLongitude = latestLocation.getLongitude();

                // Check if the user is close to any reminders
                checkReminders();
            }
        });

        reminders = repository.getAllReminders();

        // Define the service's lifecycle as started
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);

        // Start the service as a foreground service
        return super.onStartCommand(intent, flags, startId);
    }

    // Called when the service is destroyed
    @Override
    public void onDestroy() {
        stopForeground(true);

        // Define the service's lifecycle as destroyed
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);

        super.onDestroy();
        Log.d(LOG_TAG, "PingService destroyed");

    }

    // Check if the user is close to any reminders
    public void checkReminders() {
        // Observe the reminders
        reminders.observe(PingService.this, new Observer<List<ReminderEntity>>() {
            @Override
            public void onChanged(List<ReminderEntity> reminderEntities) {
                if (reminderEntities != null && !reminderEntities.isEmpty()) { // There are reminders in the database
                    Log.d(LOG_TAG, "Checking reminders...");
                    for (ReminderEntity reminder : reminderEntities) {
                        // Calculate the distance between the user and the reminder
                        double reminderLatitude = reminder.getLatitude();
                        double reminderLongitude = reminder.getLongitude();
                        double distance = DistanceCalculator.calculateDistance(currentLatitude, currentLongitude,
                                reminderLatitude, reminderLongitude);

                        if (distance < 100) { // The user is close to the reminder
                            // The user is close to the reminder
                            long reminderID = reminder.getId();

                            // get today's date
                            String date = DateConvertor.getCurrentDate();
                            int[] dateArray = DateConvertor.parseDate(date);

                            // Check if the reminder has already been recorded today
                            reminderRecords = repository.getReminderRecordsFromToday(dateArray[0], dateArray[1], dateArray[2]);
                            reminderRecords.observe(PingService.this, new Observer<List<ReminderRecordEntity>>() {
                                @Override
                                public void onChanged(List<ReminderRecordEntity> reminderRecordEntities) {
                                    boolean reminderRecordExists = false;
                                    if (reminderRecordEntities != null) { // There are reminder records in the database

                                        //Search for a reminder record with the same reminder ID
                                        for (ReminderRecordEntity reminderRecord : reminderRecordEntities) {

                                            if (reminderRecord.getReminderId() == reminderID) { // The reminder record exists
                                                Log.d(LOG_TAG, "Reminder record exists "
                                                        + reminderRecordEntities.size() + " id " + reminderID);
                                                reminderRecordExists = true;
                                                break; // Exit the loop since you found a match
                                            }
                                        }

                                        if (!reminderRecordExists) { // The reminder record does not exist
                                            Log.d(LOG_TAG, "Reminder record does not exist "
                                                    + reminderRecordEntities.size() + " id: " + reminderID);
                                            // Send a notification to the user
                                            sendNotification(reminder.getTitle());

                                            // Delete any duplicate reminder records
                                            repository.deleteDuplicateReminderRecords(dateArray[0], dateArray[1], dateArray[2], reminderID);

                                            // Insert a new reminder record into the database
                                            repository.insertReminderRecord(new ReminderRecordEntity(reminder.getId(),
                                                    dateArray[0], dateArray[1], dateArray[2]));
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    // Stop checking reminders
    public void stopCheckingReminders() {
        Log.d(LOG_TAG, "stopCheckingReminders called");
        reminders.removeObservers(PingService.this);
    }

    // Create a notification channel for the foreground service
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Reminder Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            Log.d(LOG_TAG, "Notification channel created");
        }
    }

    // Create a notification for the foreground service (not used)
    private Notification createNotification() {
        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("GeoTracker")
                .setContentText("Checking reminders...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .build();
        Log.d(LOG_TAG, "Notification created");
        return notification;
    }

    // Send a notification to the user
    private void sendNotification(String reminderTitle) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Reminder Alert")
                .setContentText(reminderTitle)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    // get the service's lifecycle
    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}