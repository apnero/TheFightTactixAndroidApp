package com.fighttactix

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import butterknife.bindView
import de.hdodenhof.circleimageview.CircleImageView
import com.facebook.login.widget.ProfilePictureView;
import com.fighttactix.model.Attendance
import com.fighttactix.model.Cards
import com.fighttactix.model.Meeting
import com.parse.*
import mehdi.sakout.fancybuttons.FancyButton
import org.json.JSONException
import java.util.*

/**
 * Created by Andrew on 11/1/2015.
 */
public class MainActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val mProfileImage:ProfilePictureView by bindView(R.id.userProfilePicture)
    //val mProfileImage: CircleImageView by bindView(R.id.profile_image)
    val mUsername: TextView by bindView(R.id.txt_name)
    val mEmailID:TextView by bindView(R.id.txt_email)

    val classHistoryButton:FancyButton by bindView(R.id.class_button)
    val punchHistoryButton:FancyButton by bindView(R.id.punch_button)
    val creditsRemaining:TextView by bindView(R.id.credits_remaining)

    val contentView: View by bindView(R.id.drawer_layout)
    val toolbar: Toolbar by bindView(R.id.toolbar)
    val navigationView: NavigationView by bindView(R.id.navigation_view)
    val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)
    private val DRAWER_CLOSE_DELAY_MS: Long = 350
    private val drawerActionHandler = Handler()

    val parseUser: ParseUser = ParseUser.getCurrentUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()
        navigationView.setNavigationItemSelectedListener(this)

        countUserMeetings()
    }

    override fun onResume(){
        super.onResume()
        countUserMeetings()
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
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        drawerLayout.closeDrawer(GravityCompat.START)
        drawerActionHandler.postDelayed(object : Runnable {
            override fun run() {
//                when(menuItem.title) {
//                    "Schedule" -> startScheduleActivity(contentView)
//                    "Contact Us" -> startContactActivity(contentView)
//                    "Social Links" -> startSocialActivity(contentView)
//                    "Check-In" -> startCheckinActivity(contentView)
 //               }
            }
        }, DRAWER_CLOSE_DELAY_MS)

       return true
    }



    fun startSocialActivity(view: View){
        var intent: Intent = Intent(this, SocialActivity::class.java)
        startActivity(intent)
    }

    fun startScheduleActivity(view: View){
        var intent: Intent = Intent(this, MeetingActivity::class.java)
        intent.putExtra("title", "Schedule")
        startActivity(intent)
    }

    fun startScheduleActivityForClassHistory(view: View){
        var intent: Intent = Intent(this, MeetingActivity::class.java)
        intent.putExtra("title", "Class History")
        startActivity(intent)
    }

    fun startPunchCardActivity(view: View){
        var intent: Intent = Intent(this, PunchCardActivity::class.java)
        startActivity(intent)
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
                    mProfileImage.setProfileId(userProfile.getString("facebookId"))
                }
                else
                {
                    // Show the default, blank user profile picture
                    mProfileImage.setProfileId(null)
                }
                if (userProfile.has("name"))
                {
                    mUsername.setText(userProfile.getString("name"))
                }
                else
                {
                    mUsername.setText("")
                }
                if (userProfile.has("email"))
                {
                    mEmailID.setText(userProfile.getString("email"))
                }
                else
                {
                    mEmailID.setText("")
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

    fun countUserMeetings() {

        val query: ParseQuery<Attendance> = ParseQuery.getQuery("Attendance")
        query.whereEqualTo("user", ParseUser.getCurrentUser())
        query.orderByAscending("date")
        query.countInBackground(object: CountCallback  {
            override fun done(count:Int, e: ParseException?) {
                if (e == null)
                {
                    Log.v("no exception", count.toString())

                    classHistoryButton.setText("Class History: " + count.toString())
                    getUserPunchCards(count)
                }
                else
                {
                    Log.v("exception", e.toString())
                }

            }
        })

    }

    fun getUserPunchCards(count:Int) {

        val query: ParseQuery<Cards> = ParseQuery.getQuery("Cards")
        query.whereEqualTo("user", ParseUser.getCurrentUser())
        query.findInBackground(object: FindCallback<Cards> {
            override fun done(objects:List<Cards>, e: ParseException?) {
                if (e == null)
                {
                    Log.v("getUserPunchCards No exception", objects.toString())
                    var sum:Int = 0
                    for (creditCount in objects){
                        sum += creditCount.credits
                    }
                    punchHistoryButton.setText("PunchCard History: " + sum.toString())
                    var total:Int = sum-count
                    creditsRemaining.setText("Credits Remaining: " + total.toString())
                    if (total>0){
                        creditsRemaining.setTextColor(Color.BLUE)
                    }
                    else if (total<0)
                    {
                        creditsRemaining.setTextColor(Color.RED)
                    }
                }
                else
                {
                    Log.v("exception", e.toString())
                }

            }
        })

    }


}

