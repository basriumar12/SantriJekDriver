package com.santrijek.driver.service;

import com.santrijek.driver.network.Log;

import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.google.firebase.iid.FirebaseInstanceId.*;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

   private static final String TAG = "MyFirebaseIIDService";
   private static final String FRIENDLY_ENGAGE_TOPIC = "friendly_engage";
  
   /**
    * The Application's current Instance ID token is no longer valid 
    * and thus a new one must be requested.
    */
   @Override
   public void onTokenRefresh() {
       // If you need to handle the generation of a token, initially or
       // after a refresh this is where you should do that.
       String token = getInstance().getToken();
       Log.d(TAG, "FCM Token: " + token);
//       new UserPreference(this).updateGCMID(token);

//       sendRegistrationToServer(refreshedToken);
   }
}