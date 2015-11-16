package com.fighttactix

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import butterknife.bindView
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.login.widget.ProfilePictureView;
import com.fighttactix.cloud.CloudCalls
import com.fighttactix.cloud.CloudQueries
import com.fighttactix.model.*
import com.orhanobut.dialogplus.*
import com.parse.*
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Andrew on 11/1/2015.
 */
public class MainActivity: AppCompatActivity(){//, NavigationView.OnNavigationItemSelectedListener {

    val mProfileImage:ProfilePictureView by bindView(R.id.userProfilePicture)
    val mUsername: TextView by bindView(R.id.txt_name)
    val mEmailID:TextView by bindView(R.id.txt_email)

    val classHistoryButton:TextView by bindView(R.id.class_text)
    val punchHistoryButton:TextView by bindView(R.id.punch_text)
    val creditsRemaining:TextView by bindView(R.id.credits_remaining)

    val toolbar: Toolbar by bindView(R.id.toolbar)
    val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)
    //private val DRAWER_CLOSE_DELAY_MS: Long = 350
    //private val drawerActionHandler = Handler()

    val parseUser: ParseUser = ParseUser.getCurrentUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()

    }

   override fun onResume(){

       super.onResume()
       //CloudQueries.numOfClassesUserAttended()
       //CloudQueries.numOfPunchCardCredits()
       CloudQueries.nextClass()
       CloudQueries.registeredNextClass()
       CloudQueries.currentSchedule()
   }

    private fun initToolbar() {

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        actionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        actionBar?.setDisplayHomeAsUpEnabled(true)

    }

    //enable navigation drawer
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                updateViewsWithProfileInfo()
                drawerLayout.openDrawer(GravityCompat.START)
                classHistoryButton.text = "Class History: " + CloudQueries.numOfClassesUserAttended.toString()
                creditsRemaining.text = "Credits Remaining: " + (CloudQueries.numOfPunchCardCredits - CloudQueries.numOfClassesUserAttended).toString()
                punchHistoryButton.text = "Punch Card History: " + CloudQueries.numOfPunchCardCredits.toString()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
//
//        drawerLayout.closeDrawer(GravityCompat.START)
//        drawerActionHandler.postDelayed(object : Runnable {
//            override fun run() {
//
//            }
//        }, DRAWER_CLOSE_DELAY_MS)
//
//       return true
//    }


    fun startSocialActivity(view: View?){
        var intent: Intent = Intent(this, SocialActivity::class.java)
        startActivity(intent)
    }

    fun startScheduleActivity(view: View?){
        var intent: Intent = Intent(this, MeetingActivity::class.java)
        intent.putExtra("title", "Schedule")
        startActivity(intent)
    }

    fun startScheduleActivityForClassHistory(view: View?){
        var intent: Intent = Intent(this, MeetingActivity::class.java)
        intent.putExtra("title", "Class History")
        startActivity(intent)
    }

    fun startPunchCardActivity(view: View?){
        var intent: Intent = Intent(this, PunchCardActivity::class.java)
        startActivity(intent)
    }

//    fun startCheckInActivity(view: View?){
//
//        getUserNotCheckedIn()
//
//    }

    fun startAdminActivity(view: View?){

        var adapter: ArrayAdapter<Attendance> =
                AdminCheckInAdapter(this, CloudQueries.registeredNextClass)

        var dialogPlus = DialogPlus.newDialog(this)
            .setAdapter(adapter)
            .setCancelable(true)
            .setGravity(Gravity.CENTER)
            .setHeader(R.layout.admin_checkin_header)
            .setFooter(R.layout.admin_checkin_footer)
            .setOutAnimation(R.anim.abc_fade_out)
            .setOnDismissListener(object:OnDismissListener {
                override fun onDismiss(dialog:DialogPlus) {
                }
            })
            .setOnItemClickListener(object:OnItemClickListener {
                override fun onItemClick(dialog:DialogPlus, item:Any, view:View, position:Int) {
                    var hmap = HashMap<String, String>()
                    val userName = CloudQueries.registeredNextClass[position-1].userName
                    //val checkedIn = CloudQueries.registeredNextClass[position-1].checkedin.toString()
                    hmap.put("userName", userName)

                    val checkedInTextView:TextView = view.findViewById(R.id.admin_checkin_text) as TextView
                    if(CloudQueries.registeredNextClass[position-1].checkedin == true){
                        hmap.put("checkedIn", "false")
                        checkedInTextView.setText("Not Checked In")
                        checkedInTextView.setTextColor(Color.DKGRAY)
                    }
                    else {
                        hmap.put("checkedIn", "true")
                        checkedInTextView.setText("CHECKED IN")
                        checkedInTextView.setTextColor(Color.BLUE)
                    }
                    CloudCalls.adminCheckInSave(hmap)

                }
            })
            .setOnCancelListener(object: OnCancelListener {
                override fun onCancel(dialog:DialogPlus) {
                    dialog.dismiss()
                }
            }).setOnBackPressListener(object:OnBackPressListener {
                override fun onBackPressed(dialog:DialogPlus) {
                    dialog.dismiss()
                }
            }).setOnClickListener(object:OnClickListener {
                override fun onClick(dialog:DialogPlus, view:View?) {
                    dialog.dismiss()
                }
            }).create()

        dialogPlus.show()

        var location:TextView = dialogPlus.headerView.findViewById(R.id.admin_class_location) as TextView
        location.text = CloudQueries.nextClass.location

        var date:TextView = dialogPlus.headerView.findViewById(R.id.admin_class_date) as TextView
        val sdf:SimpleDateFormat = SimpleDateFormat("EEE, MMM d, hh:mm aaa");
        date.text = sdf.format(CloudQueries.nextClass.date)

    }


    fun startMsgDialog(view: View?){
        val smsIntent = Intent(Intent.ACTION_VIEW)
        smsIntent.setType("vnd.android-dir/mms-sms")
        smsIntent.putExtra("address", "2035454694")
        startActivity(smsIntent)

    }


    private fun updateViewsWithProfileInfo() {
        val currentUser = ParseUser.getCurrentUser()
        if (currentUser.has("profile"))
        {
            val userProfile = currentUser.getJSONObject("profile")
            try
            {
                if (userProfile.has("facebookId"))
                {
                    mProfileImage.profileId = userProfile.getString("facebookId")
                }
                else
                {
                    // Show the default, blank user profile picture
                    mProfileImage.profileId = null
                }
                if (userProfile.has("name"))
                {
                    mUsername.text = userProfile.getString("name")
                }
                else
                {
                    mUsername.text = ""
                }
                if (userProfile.has("email"))
                {
                    mEmailID.text = userProfile.getString("email")
                }
                else
                {
                    mEmailID.text = ""
                }
            }
            catch (e: JSONException) {
                Log.d("Myatagg", "Error parsing saved user data.")
            }
        }
    }

    fun onLogoutClick(v:View) {
        // Log the user out
        ParseUser.logOut();

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


//    fun getUserNotCheckedIn() {
//
//        val query: ParseQuery<Attendance> = ParseQuery.getQuery("Attendance")
//        query.whereEqualTo("user", ParseUser.getCurrentUser())
//        query.whereEqualTo("checkedin", false)
//        query.findInBackground(object: FindCallback<Attendance> {
//            override fun done(objects:List<Attendance>, e: ParseException?) {
//                if (e == null)
//                {
//                    Log.v("getUserAttendance No exception", objects.toString())
//                    getUserMeetings(objects)
//                }
//                else
//                {
//                    Log.v("exception", e.toString())
//                }
//
//            }
//        })
//
//    }

//    fun getUserMeetings(attendanceList: List<Attendance>) {
//
//        var queries: ArrayList<ParseQuery<Meeting>> = ArrayList<ParseQuery<Meeting>>()
//        for (attend in attendanceList) {
//            var query: ParseQuery<Meeting> = ParseQuery.getQuery("Meeting")
//            query.whereEqualTo("objectId", attend.meeting.getObjectId())
//            query.whereEqualTo("active", true)
//
//            val cal:Calendar = Calendar.getInstance(); // creates calendar
//            cal.setTime(Date()); // sets calendar time/date
//            cal.add(Calendar.MINUTE, -45);
//
//            query.whereGreaterThanOrEqualTo("date", cal.getTime())
//            queries.add(query)
//        }
//
//        if(!queries.isEmpty()) {
//            val mainQuery: ParseQuery<Meeting> = ParseQuery.or(queries)
//            mainQuery.orderByAscending("date")
//            mainQuery.findInBackground(object : FindCallback<Meeting> {
//                override fun done(objects: List<Meeting>, e: ParseException?) {
//                    if (e == null) {
//
//                        if(objects.isEmpty() ){
//
//                            emptyDialog()
//                        }
//                        else if (objects[0].date.time < Date().time + 45*60*60*1000 && objects[0].date.time > Date().time - 45*60*60*1000) {
//                            checkinDialog(objects[0])
//                        }
//                        else nocheckinyetDialog(objects[0])
//
//                    }
//                    else {
//                        Log.v("exception", e.toString())
//                    }
//
//                }
//            })
//        }
//        else{
//            emptyDialog()
//        }
//    }

    fun emptyDialog(){

        MaterialDialog.Builder(this)
                .title("Check-In")
                .content("You Are Not Registered For Any Classes")
                .positiveText(R.string.agree)
                .show()

    }


    fun nocheckinyetDialog(meet: Meeting){

        val sdf: SimpleDateFormat = SimpleDateFormat("EEE, MMM d, hh:mm aaa")

        MaterialDialog.Builder(this)
                .title("Check-In")
                .content("The Krav Maga class on " + sdf.format(meet.date) + " at " + meet.location + " is not yet open.  Check in will open 30 minutes before class." )
                .positiveText(R.string.agree)
                .show()
    }

    fun checkinDialog(meet: Meeting){

        val sdf: SimpleDateFormat = SimpleDateFormat("EEE, MMM d, hh:mm aaa")

        MaterialDialog.Builder(this)
                .title("Check-In")
                .content("You have Checked-In for The Krav Maga class on " + sdf.format(meet.date) + " at " + meet.location + "." )
                .positiveText(R.string.agree)
                .show()

        Toast.makeText(applicationContext, "Thank You for Checking In.", Toast.LENGTH_LONG).show();

        val query1: ParseQuery<Meeting> = ParseQuery.getQuery("Meeting")
        val query2: ParseQuery<Attendance> = ParseQuery.getQuery("Attendance")
        query2.whereEqualTo("user", ParseUser.getCurrentUser())
        val obj:Meeting =  query1.get(meet.objectId)
        query2.whereEqualTo("meeting",obj)

        query2.findInBackground(object: FindCallback<Attendance> {
            override fun done(objects:List<Attendance>, e: ParseException?) {
                if (e == null)
                {
                    Log.v("getcheckin no exceptions", objects.toString())
                    if(objects.isEmpty() == false){
                        val query: ParseQuery<Attendance> = ParseQuery.getQuery("Attendance")
                        query.getInBackground(objects[0].objectId, object: GetCallback<Attendance> {
                            override fun done(item: Attendance, e: ParseException?) {
                                if (e == null) {item
                                    item.put("checkedin", true)
                                    item.saveInBackground()
                                } else {
                                    // something went wrong
                                }
                            }
                        })
                    }
                }
                else
                {
                    Log.v("exception", e.toString())
                }

            }
        })

    }








//admin
//    fun getNextMeeting(){
//        var query: ParseQuery<Meeting> = ParseQuery.getQuery("Meeting")
//        query.whereEqualTo("active", true)
//        val cal:Calendar = Calendar.getInstance(); // creates calendar
//        cal.setTime(Date()); // sets calendar time/date
//        cal.add(Calendar.HOUR, -4);
//        query.whereGreaterThanOrEqualTo("date", cal.getTime())
//        query.orderByAscending("date")
//        query.findInBackground(object: FindCallback<Meeting> {
//            override fun done(objects:List<Meeting>, e: ParseException?) {
//                if (e == null)
//                {
//                    Log.v("getcheckin no exceptions", objects.toString())
//                    if(objects.isEmpty() == false){
//                        //we have class objects[0]
//                        val query1: ParseQuery<Meeting> = ParseQuery.getQuery("Meeting")
//                        val obj:Meeting =  query1.get(objects[0].objectId)
//
//                        val query2: ParseQuery<Attendance> = ParseQuery.getQuery("Attendance")
//                        query2.whereEqualTo("meeting",obj)
//                        query2.findInBackground(object: FindCallback<Attendance> {
//                            override fun done(objects:List<Attendance>, e: ParseException?) {
//                                if (e == null)
//                                {
//                                    Log.v("getcheckin no exceptions", objects.toString())
//                                    adminCheckInDialog(objects)
//
//
//                                }
//                                else
//                                {
//                                    Log.v("exception", e.toString())
//                                }
//
//                            }
//                        })
//                    }
//                }
//                else
//                {
//                    Log.v("exception", e.toString())
//                }
//
//            }
//        })
//
//    }
//
//    fun adminCheckInDialog(objects: List<Attendance>) {
//
//        MaterialDialog.Builder(this).title(R.string.multiChoice).items(R.array.socialNetworks).itemsCallbackMultiChoice(arrayOf<Int>(0, 1), object : MaterialDialog.ListCallbackMultiChoice {
//            override fun onSelection(dialog: MaterialDialog, which: Array<Int>, text: Array<CharSequence>): Boolean {
//                //                    val str = StringBuilder()
//                for (i in which.indices) {
//                    if (i == 1) {1}//addToCalendar(meetingCheckinButton.tag as Int)
//                }
//
//                return true // allow selection
//            }
//        }).onNeutral(object : MaterialDialog.SingleButtonCallback {
//            override fun onClick(@NonNull dialog: MaterialDialog, @NonNull which: DialogAction) {
//                dialog.dismiss()
//            }
//        }).onPositive(object: MaterialDialog.SingleButtonCallback {
//            override fun onClick(@NonNull dialog: MaterialDialog, @NonNull which: DialogAction) {
//                dialog.dismiss()
//                //Toast.makeText(getApplicationContext(), "You have successfully registered.", Toast.LENGTH_LONG).show()
//
//            }
//        }).positiveText(R.string.register)
//                .autoDismiss(false)
//                .neutralText(R.string.clear)
//                .show()
//
//    }

}

