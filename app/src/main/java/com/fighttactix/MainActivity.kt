package com.fighttactix

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.CalendarContract
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import butterknife.bindView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.widget.ProfilePictureView;
import com.fighttactix.cloud.CloudCalls
import com.fighttactix.cloud.CloudQueries
import com.fighttactix.model.*
import com.orhanobut.dialogplus.*
import com.parse.*
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Andrew on 11/1/2015.
 */
public class MainActivity: AppCompatActivity() {//, NavigationView.OnNavigationItemSelectedListener {

    val mProfileImage: ProfilePictureView by bindView(R.id.userProfilePicture)
    val mUsername: TextView by bindView(R.id.txt_name)
    val mEmailID: TextView by bindView(R.id.txt_email)

    val classHistoryButton: TextView by bindView(R.id.class_text)
    val punchHistoryButton: TextView by bindView(R.id.punch_text)
    val creditsRemaining: TextView by bindView(R.id.credits_remaining)

    val toolbar: Toolbar by bindView(R.id.toolbar)
    val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()

    }

    override fun onStart() {

        super.onStart()

        if (CloudQueries.userAdministrator) {
            val view = this.findViewById(android.R.id.content)
            val adminCheckInView: CardView = view.findViewById(R.id.admin_checkin_view) as CardView
            val adminCardView: CardView = view.findViewById(R.id.admin_card_view) as CardView
            val adminScheduleView: CardView = view.findViewById(R.id.admin_schedule_view) as CardView
            val adminPushView: CardView = view.findViewById(R.id.admin_push_view) as CardView
            adminCheckInView.visibility = View.VISIBLE
            adminCardView.visibility = View.VISIBLE
            adminScheduleView.visibility = View.VISIBLE
            adminPushView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {

        super.onResume()

        AppEventsLogger.activateApp(this)

        CloudQueries.checkinClass()
        CloudQueries.userPunchCards()
        CloudQueries.currentSchedule()
        CloudQueries.userClassHistory()
        CloudQueries.locations()

        countdown(500)

    }

    override fun onPause() {
        super.onPause()
        AppEventsLogger.deactivateApp(this);
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
                punchHistoryButton.text = "Credit History: " + CloudQueries.numOfPunchCardCredits.toString()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun startPunchCardActivity(view: View?) {

        drawerLayout.closeDrawer(GravityCompat.START)

        var adapter: ArrayAdapter<Cards> =
                PunchCardAdapter(this, CloudQueries.userPunchCards!!)

        var dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.header)
                .setFooter(R.layout.footer)
                .setOutAnimation(R.anim.abc_fade_out)
                .setOnCancelListener(object : OnCancelListener {
                    override fun onCancel(dialog: DialogPlus) {
                        dialog.dismiss()
                    }
                }).setOnBackPressListener(object : OnBackPressListener {
            override fun onBackPressed(dialog: DialogPlus) {
                dialog.dismiss()
            }
        }).setOnClickListener(object : OnClickListener {
            override fun onClick(dialog: DialogPlus, view: View?) {
                dialog.dismiss()
            }
        }).create()

        dialogPlus.show()

        var headerTitleView: TextView = dialogPlus.headerView.findViewById(R.id.header_title) as TextView
        headerTitleView.text = "Punch Cards"

    }


    fun startScheduleActivity(view: View?) {


        var adapter: ArrayAdapter<Meeting> =
                MeetingAdapter(this, CloudQueries.currentSchedule!!, CloudQueries.userClassHistory!!)

        var dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.header)
                .setFooter(R.layout.footer)
                .setOutAnimation(R.anim.abc_fade_out)
                .setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(dialog: DialogPlus, item: Any, view: View, position: Int) {
                        var hmap = HashMap<String, Date>()
                        hmap.put("date", CloudQueries.currentSchedule!![position - 1].date)

                        val checkedInTextView: TextView = view.findViewById(R.id.third_text) as TextView
                        if ( checkedInTextView.text == "Open for Registration") {
                            checkedInTextView.text = "Registered"
                            checkedInTextView.setTextColor(Color.DKGRAY)
                            CloudCalls.registerForClass(hmap)
                            SweetAlertDialog(view.context, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Add to Schedule?")
                                    .setContentText("You have Registered! You can unregister up to 4 hours before class.")
                                    .setConfirmText("Add To My Schedule!")
                                    .setCancelText("Ok")
                                    .setConfirmClickListener(object : SweetAlertDialog.OnSweetClickListener {
                                        override fun onClick(sDialog: SweetAlertDialog) {
                                            addToCalendar(CloudQueries.currentSchedule!![position - 1])

                                        }
                                    }).show()
                        } else if (checkedInTextView.text == "Registered") {
                            checkedInTextView.text = "Open for Registration"
                            checkedInTextView.setTextColor(Color.GRAY)
                            CloudCalls.unRegisterForClass(hmap)
                        }


                    }
                })
                .setOnCancelListener(object : OnCancelListener {
                    override fun onCancel(dialog: DialogPlus) {
                        dialog.dismiss()
                    }
                }).setOnBackPressListener(object : OnBackPressListener {
            override fun onBackPressed(dialog: DialogPlus) {
                dialog.dismiss()
            }
        }).setOnClickListener(object : OnClickListener {
            override fun onClick(dialog: DialogPlus, view: View?) {
                dialog.dismiss()
            }
        }).create()

        dialogPlus.show()

        var headerTitleView: TextView = dialogPlus.headerView.findViewById(R.id.header_title) as TextView
        headerTitleView.text = "Schedule"

    }

    fun addToCalendar(meeting: Meeting){

        var twohours = 1000*60*60*2
        val calIntent = Intent(Intent.ACTION_INSERT)
        calIntent.setType("vnd.android.cursor.item/event")
        calIntent.putExtra(CalendarContract.Events.TITLE, "Krav Maga")
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, meeting.location)
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                meeting.date.time)
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                meeting.date.time + twohours)
        startActivity(calIntent)
    }

    fun startCheckInActivity(view: View?) {

        var boolean: Boolean = false
        for (attendance in CloudQueries.userClassHistory!!) {
            if (attendance.date == (CloudQueries.checkinClass)?.date) {
                if (!attendance.checkedin!!) {
                    val sdf: SimpleDateFormat = SimpleDateFormat("hh:mm aaa")
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("You Checked-In!")
                            .setContentText("Class is at " + attendance.location + " at " + sdf.format(attendance.date))
                            .show()
                    var hmap = HashMap<String, String>()
                    hmap.put("username", attendance.username)
                    CloudCalls.adminCheckInSave(hmap)

                    boolean = true
                    break
                } else {
                    val sdf: SimpleDateFormat = SimpleDateFormat("hh:mm aaa")
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("You Already Checked-In!")
                            .setContentText("Class is at " + attendance.location + " at " + sdf.format(attendance.date))
                            .show()
                    boolean = true
                    break
                }
            }
        }
        if (!boolean) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("No Class Open For Check-In")
                    .setContentText("Check-In Opens 1 Hour Before Class!")
                    .show()
        }


    }


    fun startClassHistoryActivity(view: View?) {

        drawerLayout.closeDrawer(GravityCompat.START)

        var adapter: ArrayAdapter<Attendance> =
                ClassHistoryAdapter(this, CloudQueries.userClassHistory!!)

        var dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.header)
                .setFooter(R.layout.footer)
                .setOutAnimation(R.anim.abc_fade_out)
                .setOnCancelListener(object : OnCancelListener {
                    override fun onCancel(dialog: DialogPlus) {
                        dialog.dismiss()
                    }
                }).setOnBackPressListener(object : OnBackPressListener {
            override fun onBackPressed(dialog: DialogPlus) {
                dialog.dismiss()
            }
        }).setOnClickListener(object : OnClickListener {
            override fun onClick(dialog: DialogPlus, view: View?) {
                dialog.dismiss()
            }
        }).create()

        dialogPlus.show()

        var headerTitleView: TextView = dialogPlus.headerView.findViewById(R.id.header_title) as TextView
        headerTitleView.text = "Attendance History"

    }


    fun startAdminScheduleActivity(view: View?) {

        var adapter: ArrayAdapter<Meeting> =
                AdminMeetingAdapter(this, CloudQueries.currentSchedule!!)

        var dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.header)
                .setFooter(R.layout.admin_schedule_footer)
                .setOutAnimation(R.anim.abc_fade_out)
                .setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(dialog: DialogPlus, item: Any, view: View, position: Int) {
                        adminDialog2()
                    }
                })
                .setOnCancelListener(object : OnCancelListener {
                    override fun onCancel(dialog: DialogPlus) {
                        dialog.dismiss()
                    }
                }).setOnBackPressListener(object : OnBackPressListener {
            override fun onBackPressed(dialog: DialogPlus) {
                dialog.dismiss()
            }
        }).setOnClickListener(object : OnClickListener {
            override fun onClick(dialog: DialogPlus, view: View?) {
                dialog.dismiss()
            }
        }).create()

        dialogPlus.show()

        var headerTitleView: TextView = dialogPlus.headerView.findViewById(R.id.header_title) as TextView
        headerTitleView.text = "Admin Schedule"

    }

    fun adminDialog2(){

        MaterialDialog.Builder(this).title("alabash").items(R.array.items).itemsCallback(object:MaterialDialog.ListCallback {
            override fun onSelection(dialog:MaterialDialog, view:View, which:Int, text:CharSequence) {}
        }).show()
    }

    fun startAdminPushActivity(view: View?) {
        pushDialog("Send Notification", "To All Users:", null, "Tactix")
    }

    fun pushDialog(title: String, content: String?, prefill: String?, channel: String) {
        MaterialDialog.Builder(this)
                .title(title)
                .content(content)
                .positiveText("SEND")
                .negativeText("Don't Send")
                .input(null, prefill, object : MaterialDialog.InputCallback {
                    override fun onInput(dialog: MaterialDialog, input: CharSequence) {
                        // Do something
                        var hmap = HashMap<String, String>()
                        hmap.put("channel", channel)
                        hmap.put("msg", input.toString())
                        CloudCalls.push(hmap)
                    }
                })
                .onNegative(object : MaterialDialog.SingleButtonCallback {
                    override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                        dialog.dismiss()
                    }
                })
                .show()
    }


    fun startAdminCheckInActivity(view: View?) {

        var adapter: ArrayAdapter<Attendance> =
                AdminCheckInAdapter(this, CloudQueries.registeredNextClass!!)

        var dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.admin_checkin_header)
                .setFooter(R.layout.footer)
                .setOutAnimation(R.anim.abc_fade_out)
                .setOnDismissListener(object : OnDismissListener {
                    override fun onDismiss(dialog: DialogPlus) {
                    }
                })
                .setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(dialog: DialogPlus, item: Any, view: View, position: Int) {
                        var hmap = HashMap<String, String>()
                        val userName = CloudQueries.registeredNextClass!![position - 1].username
                        hmap.put("username", userName)

                        val checkedInTextView: TextView = view.findViewById(R.id.admin_checkin_text) as TextView
                        if (CloudQueries.registeredNextClass!![position - 1].checkedin == true) {
                            checkedInTextView.text = "Not Checked In"
                            checkedInTextView.setTextColor(Color.DKGRAY)
                            CloudCalls.adminCheckInSave(hmap)
                        } else {
                            checkedInTextView.text = "CHECKED IN"
                            checkedInTextView.setTextColor(Color.BLUE)
                            CloudCalls.adminCheckInSave(hmap)
                        }


                    }
                })
                .setOnCancelListener(object : OnCancelListener {
                    override fun onCancel(dialog: DialogPlus) {
                        dialog.dismiss()
                    }
                }).setOnBackPressListener(object : OnBackPressListener {
            override fun onBackPressed(dialog: DialogPlus) {
                dialog.dismiss()
            }
        }).setOnClickListener(object : OnClickListener {
            override fun onClick(dialog: DialogPlus, view: View?) {
                dialog.dismiss()
            }
        }).create()

        dialogPlus.show()

        var location: TextView = dialogPlus.headerView.findViewById(R.id.admin_class_location) as TextView
        location.text = CloudQueries.nextClass!!.location

        var date: TextView = dialogPlus.headerView.findViewById(R.id.admin_class_date) as TextView
        val sdf: SimpleDateFormat = SimpleDateFormat("EEE, MMM d, hh:mm aaa");
        date.text = sdf.format(CloudQueries.nextClass!!.date)

    }

    fun startAdminCardActivity(view: View?) {

        var adapter: ArrayAdapter<AdminCard> =
                AdminCardAdapter(this, CloudQueries.findBalance())

        var dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.header)
                .setFooter(R.layout.footer)
                .setOutAnimation(R.anim.abc_fade_out)
                .setOnDismissListener(object : OnDismissListener {
                    override fun onDismiss(dialog: DialogPlus) {
                    }
                })
                .setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(dialog: DialogPlus, item: Any, view: View, position: Int) {
                        var card: AdminCard = item as AdminCard
                        SweetAlertDialog(view.context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Add 10 credits?")
                                .setContentText(card.username + "'s Account!")
                                .setConfirmText("Yes!")
                                .setCancelText("Cancel")
                                .setConfirmClickListener(object : SweetAlertDialog.OnSweetClickListener {
                                    override fun onClick(sDialog: SweetAlertDialog) {
                                        var hmap = HashMap<String, String>()
                                        hmap.put("userName", card.username)
                                        hmap.put("credits", 10.toString())
                                        CloudCalls.saveNewCard(hmap)

                                        val creditsTextView: TextView = view.findViewById(R.id.second_text) as TextView
                                        creditsTextView.text = (card.credits!! + 10).toString()
                                        creditsTextView.setTextColor(Color.BLUE)

                                        sDialog.setTitleText("Success!")
                                                .setContentText("10 Credits Have Been Added!")
                                                .setConfirmText("OK")
                                                .showCancelButton(false)
                                                .setConfirmClickListener(object : SweetAlertDialog.OnSweetClickListener {
                                                    override fun onClick(sDialog: SweetAlertDialog) {
                                                        sDialog.dismissWithAnimation()
                                                        val username: String = card.username.replace(" ", "")
                                                        pushDialog("Send Notification", "To " + card.username + ":", "A Punch Card Has Been Added To Your Account.", username)
                                                    }
                                                }
                                                )
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                    }
                        }).show()

                    }
                })
                .setOnCancelListener(object : OnCancelListener {
                    override fun onCancel(dialog: DialogPlus) {
                        dialog.dismiss()
                    }
                }).setOnBackPressListener(object : OnBackPressListener {
            override fun onBackPressed(dialog: DialogPlus) {
                dialog.dismiss()
            }
        }).setOnClickListener(object : OnClickListener {
            override fun onClick(dialog: DialogPlus, view: View?) {
                dialog.dismiss()
            }
        }).create()

        dialogPlus.show()

        var headerTitleView: TextView = dialogPlus.headerView.findViewById(R.id.header_title) as TextView
        headerTitleView.text = "Card Credits Remaining"

    }

    fun startURL(view:View?) {
        val urlTextView: TextView = view?.findViewById(R.id.third_text) as TextView
        val browserIntent:Intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlTextView.tag as String))
        startActivity(browserIntent)
    }

    fun startMsgDialog(view: View?){
        val smsIntent = Intent(Intent.ACTION_VIEW)
        smsIntent.setType("vnd.android-dir/mms-sms")
        smsIntent.putExtra("address", "2035454694")
        try {
            startActivity(smsIntent);
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(view?.getContext(), "SMS not available", Toast.LENGTH_LONG).show()
        }

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

    fun countdown(interval:Long){

        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.setTitleText("Loading")
        pDialog.setCancelable(false)
        pDialog.show()

        var i:Int = -1
        object: CountDownTimer(interval * 7, interval) {
            override fun onTick(millisUntilFinished:Long) {
                // you can change the progress bar color by ProgressHelper every 800 millis
                i++
                when (i) {
                    0 -> pDialog.progressHelper.barColor = resources.getColor(R.color.blue_btn_bg_color)
                    1 -> pDialog.progressHelper.barColor = resources.getColor(R.color.material_deep_teal_50)
                    2 -> pDialog.progressHelper.barColor = resources.getColor(R.color.success_stroke_color)
                    3 -> pDialog.progressHelper.barColor = resources.getColor(R.color.material_deep_teal_20)
                    4 -> pDialog.progressHelper.barColor = resources.getColor(R.color.material_blue_grey_80)
                    5 -> pDialog.progressHelper.barColor = resources.getColor(R.color.warning_stroke_color)
                    6 -> pDialog.progressHelper.barColor = resources.getColor(R.color.success_stroke_color)
                }
            }
            override fun onFinish() {
                i = -1
                pDialog.dismiss()
            }
        }.start()

    }



    fun startConnectionsActivity(view: View?){
        var adapter: ArrayAdapter<Location> =
                ConnectionsAdapter(this, CloudQueries.locations!!)

        var dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.connections_header)
                .setFooter(R.layout.footer)
                .setOutAnimation(R.anim.abc_fade_out)
                .setOnCancelListener(object: OnCancelListener {
                    override fun onCancel(dialog:DialogPlus) {
                        dialog.dismiss()
                    }
                })
                .setOnBackPressListener(object:OnBackPressListener {
                override fun onBackPressed(dialog:DialogPlus) {
                    dialog.dismiss()
                }
                })
                .setOnClickListener(object:OnClickListener {
                    override fun onClick(dialog:DialogPlus, view:View?) {
                        dialog.dismiss()
                    }
                }).create()

        dialogPlus.show()



    }


}

