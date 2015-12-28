package com.fighttactix

import android.view.ViewGroup
import android.widget.Toast


import com.bumptech.glide.Glide

import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.materialdrawer.*
import com.mikepenz.materialdrawer.holder.BadgeStyle
import com.mikepenz.materialdrawer.interfaces.ICrossfader
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.SectionDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.mikepenz.materialdrawer.model.interfaces.Nameable
import com.mikepenz.materialdrawer.util.DrawerUIUtils
import com.mikepenz.materialize.util.UIUtils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.CalendarContract
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import butterknife.bindView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.widget.ProfilePictureView
import com.facebook.share.widget.ShareDialog
import com.fighttactix.cloud.CloudCalls
import com.fighttactix.cloud.CloudQueries
import com.fighttactix.model.*
import com.orhanobut.dialogplus.*
import com.parse.*
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*
import android.view.inputmethod.EditorInfo
import com.afollestad.materialdialogs.DialogAction
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.share.model.ShareLinkContent
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import com.mikepenz.materialdrawer.holder.StringHolder
import org.json.JSONObject
import java.text.DateFormat

public class MainActivity: AppCompatActivity() {


    val toolbar: Toolbar by bindView(R.id.toolbar)


    lateinit var datePickerDialog:DatePickerDialog
    lateinit var timePickerDialog:TimePickerDialog

    lateinit var headerResult:AccountHeader
    lateinit var result:Drawer
    lateinit var miniResult:MiniDrawer
    lateinit var crossfadeDrawerLayout:CrossfadeDrawerLayout
    lateinit var creditsRemainingMenuItem:PrimaryDrawerItem
    lateinit var creditsHistoryMenuItem:PrimaryDrawerItem
    lateinit var classHistoryMenuItem:PrimaryDrawerItem



    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_dark_toolbar)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        getSupportActionBar().setDisplayShowTitleEnabled(false)
        //getSupportActionBar().setTitle(R.string.drawer_item_crossfade_drawer_layout_drawer)


        CloudQueries.userAdministrator(findViewById(android.R.id.content))

        if (ParseInstallation.getCurrentInstallation() != null) {

            var subscribedChannels: List<String>? = ParseInstallation.getCurrentInstallation()?.getList("channels")
            if (subscribedChannels?.contains("All") == false) {
                ParsePush.subscribeInBackground("All")
            }
            val name: String? = (ParseUser.getCurrentUser()).get("name").toString().replace(" ", "")
            if (subscribedChannels?.contains(name) == false) {
                ParsePush.subscribeInBackground(name)
            }
            if (subscribedChannels == null) {
                ParsePush.subscribeInBackground("All")
                ParsePush.subscribeInBackground(name)
            }
        }

        val currentUser = ParseUser.getCurrentUser()
        if(ParseFacebookUtils.isLinked(currentUser) && !currentUser.has("profile"))
            makeGraphRequest()


        DrawerImageLoader.init(object: AbstractDrawerImageLoader() {
            override fun set(imageView:ImageView, uri:Uri, placeholder: Drawable) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView)
            }
            override fun cancel(imageView:ImageView) {
                Glide.clear(imageView)
            }
            override fun placeholder(ctx: Context, tag:String?):Drawable {
                DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name
                return super.placeholder(ctx, tag)
            }
        })



        var profile:ProfileDrawerItem
        if (currentUser.has("profile")) {
            val userProfile = currentUser.getJSONObject("profile")

                   profile = ProfileDrawerItem().withName(currentUser.getString("name"))
                           .withEmail(currentUser.getString("email"))
                            //.withIcon(Uri.parse("https://graph.facebook.com/924681527600383/picture?width=460&height=460"))
                           .withIcon(Uri.parse("https://graph.facebook.com/" + userProfile.getString("facebookId") + "/picture?width=460&height=460"))

        }
        else  profile = ProfileDrawerItem().withName(currentUser.getString("name")).withEmail(currentUser.getString("email"))
                .withIcon(Uri.parse("http://www.fighttactix.com/shield.png"))


        // Create the AccountHeader
        headerResult = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_img)
                .addProfiles(profile)
                .withOnlyMainProfileImageVisible(true)
                .withProfileImagesClickable(false)
                .withSelectionListEnabled(false)
         .withSavedInstance(savedInstanceState).build()
        //create the CrossfadeDrawerLayout which will be used as alternative DrawerLayout for the Drawer
        crossfadeDrawerLayout = CrossfadeDrawerLayout(this)
        //Create the drawer
        creditsRemainingMenuItem = PrimaryDrawerItem().withName(R.string.drawer_item_credits_remaining)
                .withIcon(FontAwesome.Icon.faw_bank)
                .withBadgeStyle(BadgeStyle(Color.RED, Color.RED)).withIdentifier(1).withSelectable(false)
        creditsHistoryMenuItem = PrimaryDrawerItem().withName(R.string.drawer_item_credit_history)
                .withIcon(FontAwesome.Icon.faw_credit_card)
                .withBadgeStyle(BadgeStyle(Color.BLUE, Color.BLUE)).withIdentifier(2)
        classHistoryMenuItem = PrimaryDrawerItem().withName(R.string.drawer_item_class_history)
                .withIcon(FontAwesome.Icon.faw_history)
                .withBadgeStyle(BadgeStyle(Color.BLUE, Color.BLUE)).withIdentifier(3)
        result = DrawerBuilder().withActivity(this)
                .withToolbar(toolbar)
                .withDrawerLayout(crossfadeDrawerLayout)
                .withHasStableIds(true)
                .withDrawerWidthDp(72)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                         creditsRemainingMenuItem,
                        creditsHistoryMenuItem,
                        classHistoryMenuItem,
                        SectionDrawerItem().withName(R.string.drawer_item_section_header),
                        SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(4)) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(object:Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(view:View?, position:Int, drawerItem:IDrawerItem<*>):Boolean {
                        if (drawerItem is Nameable<*>)
                        {
                            //Toast.makeText(this@MainActivity, (drawerItem as Nameable<*>).getName().getText(this@MainActivity), Toast.LENGTH_SHORT).show()
                            if (position == 2) startPunchCardActivity(view)
                            if (position == 3) startClassHistoryActivity(view)
                            if (position == 5) startLogout(view)
                        }
                        //IMPORTANT notify the MiniDrawer about the onItemClick
                        return miniResult.onItemClick(drawerItem)
                    }
                })
                .withSelectedItem(-1)
                .withOnDrawerListener(object:Drawer.OnDrawerListener {
                    override fun onDrawerOpened(drawerView:View) {
                        //Toast.makeText(this@MainActivity, "onDrawerOpened", Toast.LENGTH_SHORT).show()
                        updateViewsWithProfileInfo()
                    }
                    override fun onDrawerClosed(drawerView:View) {
                        //Toast.makeText(this@MainActivity, "onDrawerClosed", Toast.LENGTH_SHORT).show()
                    }
                    override fun onDrawerSlide(drawerView:View, slideOffset:Float) {
                    }
                })
                .withSavedInstance(savedInstanceState).withShowDrawerOnFirstLaunch(true).build()
        //define maxDrawerWidth
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this))
        //add second view (which is the miniDrawer)
        miniResult = MiniDrawer().withDrawer(result).withAccountHeader(headerResult)
        //build the view for the MiniDrawer
        val view = miniResult.build(this)
        //set the background of the MiniDrawer as this would be transparent
        view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, R.attr.material_drawer_background, R.color.material_drawer_dark_background));
        //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
        crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(object:ICrossfader {
            override fun crossfade() {
                val isFaded = isCrossfaded
                crossfadeDrawerLayout.crossfade(400)
                //only close the drawer if we were already faded and want to close it now
                if (isFaded) {
                    result.getDrawerLayout().closeDrawer(GravityCompat.START)
                }
            }

            override fun isCrossfaded(): Boolean {
                //get() {
                //   return crossfadeDrawerLayout.isCrossfaded()
                //}
                return crossfadeDrawerLayout.isCrossfaded()
            }
        })
    }

    override fun onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen())
        {
            result.closeDrawer()
        }
        else
        {
            super.onBackPressed()
        }
    }


    override fun onResume() {

        super.onResume()

        AppEventsLogger.activateApp(this)

        CloudQueries.maxClassSize()
        CloudQueries.checkinClass()
        CloudQueries.userPunchCards()
        CloudQueries.currentSchedule()
        CloudQueries.userClassHistory()
        CloudQueries.locations()
        CloudQueries.notifications()

        if (CloudQueries.userAdministrator) {
            CloudQueries.allUserAttendance()
            CloudQueries.allUserCards()
        }

        countdown(400)

    }

    override fun onPause() {
        super.onPause()
        AppEventsLogger.deactivateApp(this);
    }



    fun startLogout(view: View?){
        ParseUser.logOut()

        val intent = Intent(this@MainActivity,
                LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun startPunchCardActivity(view: View?) {

        crossfadeDrawerLayout.closeDrawer(GravityCompat.START)

        var adapter: ArrayAdapter<Cards> =
                PunchCardAdapter(this, CloudQueries.userPunchCards!!)

        var dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.header)
                .setFooter(R.layout.footer)
                .setOutAnimation(R.anim.abc_fade_out)
                .setOnCancelListener({ dialog -> dialog.dismiss() })
                .setOnBackPressListener({ dialog -> dialog.dismiss() })
                .setOnClickListener({ dialog, view -> dialog.dismiss() })
                .create()

        dialogPlus.show()

        var headerTitleView: TextView = dialogPlus.headerView.findViewById(R.id.header_title) as TextView
        headerTitleView.text = "Punch Cards"

    }


    fun startScheduleActivity(view: View?) {

        if (CloudQueries.currentSchedule!!.size == 0)
            SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE )
                    .setTitleText("No classes!")
                    .setContentText("There are no classes on the schedule.")
                    .show()
        else {
            val adapter: ArrayAdapter<Meeting> = MeetingAdapter(this, CloudQueries.currentSchedule!!, CloudQueries.userClassHistory!!)

            var dialogPlus = DialogPlus.newDialog(this)
                    .setAdapter(adapter)
                    .setCancelable(true)
                    .setGravity(Gravity.CENTER)
                    .setHeader(R.layout.header)
                    .setFooter(R.layout.footer)
                    .setOutAnimation(R.anim.abc_fade_out)
                    .setOnItemClickListener({ dialog, item, view, position ->
                        var hmap = HashMap<String, Date>()
                        hmap.put("date", CloudQueries.currentSchedule!![position - 1].date)

                        val checkedInTextView: TextView = view.findViewById(R.id.third_text) as TextView
                        if ( checkedInTextView.text == "Register") {
                            checkedInTextView.text = "Registered"
                            checkedInTextView.setTextColor(Color.DKGRAY)
                            CloudCalls.registerForClass(hmap)
                            SweetAlertDialog(view.context, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Add to Schedule?")
                                    .setContentText("You have Registered! You can unregister up to 4 hours before class.")
                                    .setConfirmText("Add To My Schedule!")
                                    .setCancelText("Ok")
                                    .setConfirmClickListener({ sDialog ->
                                        addToCalendar(CloudQueries.currentSchedule!![position - 1])
                                        sDialog.dismissWithAnimation()
                                    }).show()
                        } else if (checkedInTextView.text == "Registered") {
                            checkedInTextView.text = "Register"
                            checkedInTextView.setTextColor(Color.GRAY)
                            CloudCalls.unRegisterForClass(hmap)
                        }
                    })
                    .setOnCancelListener({ dialog -> dialog.dismiss() })
                    .setOnBackPressListener({ dialog -> dialog.dismiss() })
                    .setOnClickListener({ dialog, view -> dialog.dismiss() })
                    .create()

            dialogPlus.show()

            var headerTitleView: TextView = dialogPlus.headerView.findViewById(R.id.header_title) as TextView
            headerTitleView.text = "Schedule"
        }

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

    fun startNotificationsActivity(view: View?) {

        var adapter: ArrayAdapter<Notifications> =
                NotificationAdapter(this, CloudQueries.notifications!!)

        var dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.header)
                .setFooter(R.layout.footer)
                .setOutAnimation(R.anim.abc_fade_out)
                .setOnCancelListener({ dialog -> dialog.dismiss() })
                .setOnBackPressListener({ dialog -> dialog.dismiss() })
                .setOnClickListener({ dialog, view -> dialog.dismiss() })
                .create()

        dialogPlus.show()

        var headerTitleView: TextView = dialogPlus.headerView.findViewById(R.id.header_title) as TextView
        headerTitleView.text = "Messages"

    }



    fun startClassHistoryActivity(view: View?) {

        crossfadeDrawerLayout.closeDrawer(GravityCompat.START)

        var adapter: ArrayAdapter<Attendance> =
                ClassHistoryAdapter(this, CloudQueries.userClassHistory!!)

        var dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.header)
                .setFooter(R.layout.footer)
                .setOutAnimation(R.anim.abc_fade_out)
                .setOnCancelListener({ dialog -> dialog.dismiss() })
                .setOnBackPressListener({ dialog -> dialog.dismiss() })
                .setOnClickListener({ dialog, view -> dialog.dismiss() })
                .create()

        dialogPlus.show()

        var headerTitleView: TextView = dialogPlus.headerView.findViewById(R.id.header_title) as TextView
        headerTitleView.text = "Attendance History"

    }


    fun startAdminScheduleActivity(view: View?) {

        var meeting:Meeting? = null
        var lookAtClassDialog:Boolean = false
        var adapter: ArrayAdapter<Meeting> =
                AdminMeetingAdapter(this, CloudQueries.currentSchedule!!)

        var dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.header)
                .setFooter(R.layout.admin_schedule_footer)
                .setOutAnimation(R.anim.abc_fade_out)
                .setOnItemClickListener({ dialog, item, view, position ->
                    meeting = CloudQueries.currentSchedule!![position - 1]
                    lookAtClassDialog = true
                    dialog.dismiss()
                })
                .setOnCancelListener({ dialog -> dialog.dismiss() })
                .setOnBackPressListener({ dialog -> dialog.dismiss() })
                .setOnClickListener({ dialog, view ->
                    if(view?.tag == "add_class")
                        lookAtClassDialog = true
                    dialog.dismiss()
                })
                .setOnDismissListener({ if(lookAtClassDialog) startAdminClassDialog(meeting) })
                .create()

        dialogPlus.show()
        var headerTitleView: TextView = dialogPlus.headerView.findViewById(R.id.header_title) as TextView
        headerTitleView.text = "Admin Schedule"
    }


    fun startAdminClassDialog(meeting:Meeting?){

        var userList:ArrayList<String> = ArrayList<String>()
        var attendanceList:ArrayList<Attendance> = ArrayList<Attendance>()
        var datehmap = HashMap<String, String>()

        if (CloudQueries.allUserAttendance != null)
            for (attendance in CloudQueries.allUserAttendance!!)
                if (meeting?.date == attendance.date) {
                    userList.add(attendance.username)
                    attendanceList.add(attendance)
                }

        var dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(ArrayAdapter<String>(this, R.layout.user_item, userList))
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.admin_class_header)
                .setFooter(R.layout.admin_class_footer)
                .setOutAnimation(R.anim.abc_fade_out)
                .setOnCancelListener({ dialog -> dialog.dismiss() })
                .setOnBackPressListener({ dialog -> dialog.dismiss() })
                .setOnClickListener({ dialog, view ->
                    var openPicker: TextView = dialog.headerView.findViewById(R.id.open_picker) as TextView
                    var locationPicker:Spinner = dialog.headerView.findViewById(R.id.location_picker) as Spinner
                    if (view?.tag == "date_picker" ) datePickerDialog.show()
                    else if (view?.tag == "time_picker") timePickerDialog.show()
                    else if (view?.tag == "open_picker")
                        if (openPicker.text == "Registration Closed")
                            openPicker.text = "Registration Open"
                        else openPicker.text = "Registration Closed"
                    else if (view?.tag == "cancel") dialog.dismiss()
                    else if (view?.tag == "save_class"){
                        if (meeting == null){
                            datehmap.put("location", locationPicker.selectedItem.toString())
                            if(openPicker.text == "Registration Open")
                                datehmap.put("open", "true")
                            else datehmap.put("open", "false")
                            CloudCalls.adminAddMeeting(datehmap)
                            SweetAlertDialog(view?.context, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!")
                                    .setContentText("The new class has been created!")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener({ sDialog ->
                                        sDialog.dismissWithAnimation()
                                        dialog.dismiss()
                                    })
                                    .show()
                        }
                        else{
                            datehmap.put("meetingId", meeting.objectId)
                            datehmap.put("location", locationPicker.selectedItem.toString())
                            if(openPicker.text == "Registration Open")
                                datehmap.put("open", "true")
                            else datehmap.put("open", "false")
                            CloudCalls.adminModifyMeeting(datehmap)
                            val sdf:SimpleDateFormat = SimpleDateFormat("EEE, MMM d, hh:mm aaa")
                            SweetAlertDialog(view?.context, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!")
                                    .setContentText("The new information has been saved!")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener({ sDialog ->
                                        sDialog.dismissWithAnimation()
                                        dialog.dismiss()
                                        if (!userList.isEmpty()) pushDialog(userList, sdf.format(meeting.date) + " at " + meeting.location + " has been changed!")
                                    })
                                    .show()
                        }
                    }
                    else if (view?.tag == "delete_class"){
                        val sdf:SimpleDateFormat = SimpleDateFormat("EEE, MMM d, hh:mm aaa")
                        SweetAlertDialog(view?.context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Delete Class?")
                                .setContentText(sdf.format(meeting?.date) + " at " + meeting?.location + "!")
                                .setConfirmText("Yes!")
                                .setCancelText("Cancel")
                                .setConfirmClickListener({ sDialog ->
                                    var hmap = HashMap<String, String>()
                                    hmap.put("objectId", meeting!!.objectId)
                                    CloudCalls.adminDeleteMeeting(hmap, attendanceList)
                                    dialog.dismiss()
                                    sDialog.setTitleText("Success!")
                                            .setContentText("Class has been deleted!")
                                            .setConfirmText("OK")
                                            .showCancelButton(false)
                                            .setConfirmClickListener({ sDialog ->
                                                sDialog.dismissWithAnimation()
                                                if (!userList.isEmpty()) pushDialog(userList, sdf.format(meeting.date) + " at " + meeting.location + " has been cancelled!")
                                            }
                                            )
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                }).show()


                    }
                    else if (view?.tag == "send_notification")
                        if (!userList.isEmpty()) pushDialog(userList, null)


                })
                .setOnDismissListener({ })
                .create()

        dialogPlus.show()

        var datePicker: TextView = dialogPlus.headerView.findViewById(R.id.date_picker) as TextView
        var timePicker: TextView = dialogPlus.headerView.findViewById(R.id.time_picker) as TextView
        var locationPicker: Spinner = dialogPlus.headerView.findViewById(R.id.location_picker) as Spinner
        var openPicker: TextView = dialogPlus.headerView.findViewById(R.id.open_picker) as TextView
        var sendNotification: TextView = dialogPlus.footerView.findViewById(R.id.send_notification) as TextView
        var deleteClassButton: Button = dialogPlus.footerView.findViewById(R.id.delete_class_button) as Button
        var saveClassButton: Button = dialogPlus.footerView.findViewById(R.id.save_class_button) as Button

        datePicker.setTextColor(Color.BLUE)
        timePicker.setTextColor(Color.BLUE)
        openPicker.setTextColor(Color.BLUE)


        var locationList:ArrayList<String> = ArrayList<String>()
        if (CloudQueries.locations != null)
            for (location in CloudQueries.locations!!)
                locationList.add(location.name!!)

        var adapter = ArrayAdapter<String>(this, R.layout.user_item, locationList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationPicker.adapter = adapter

        if (userList.isEmpty())
            sendNotification.visibility = View.GONE

        var sdfDate = SimpleDateFormat("EEE, MMM d")
        var sdfTime = SimpleDateFormat("hh:mm a")
        if (meeting == null) {
            openPicker.text = "Registration Open"
            deleteClassButton.visibility = View.INVISIBLE
            saveClassButton.visibility = View.INVISIBLE
            locationPicker.setSelection(0)
        } else{
            datePicker.text = sdfDate.format(meeting.date)
            timePicker.text = sdfTime.format(meeting.date)
            locationPicker.setSelection(locationList.indexOfRaw(meeting.location))
            openPicker.text = if(meeting.open!!) "Registration Open" else "Registration Closed"
        }


        val myTimePickerCallback = TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            // do stuff with the time from the picker
            val s:String = hour.toString() + ":" + minute.toString()
            val sdf:SimpleDateFormat = SimpleDateFormat("HH:mm")
            val d:Date = sdf.parse(s)
            val sdf2:SimpleDateFormat = SimpleDateFormat("hh:mm aaa")
            timePicker.text = sdf2.format(d)
            datehmap.put("hour", hour.toString())
            datehmap.put("minute", minute.toString())
            if(datehmap.size == 5)
                saveClassButton.visibility = View.VISIBLE
        }
        timePickerDialog = TimePickerDialog(this,
                myTimePickerCallback,
                Calendar.getInstance().get(Calendar.HOUR),
                Calendar.getInstance().get(Calendar.MINUTE),
                false)

        val myDatePickerCallback = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            // do stuff with date from picker
            var sdf: DateFormat = SimpleDateFormat("EEE")
            var sdf2:DateFormat = SimpleDateFormat("MMM")
            val c = Calendar.getInstance()
            c.set(year, month, day)
            datePicker.text = sdf.format(c.time) + ", " + sdf2.format(c.time) + " " + day.toString()
            datehmap.put("year", year.toString())
            datehmap.put("month", month.toString())
            datehmap.put("day", day.toString())
            if(datehmap.size == 5)
                saveClassButton.visibility = View.VISIBLE
        }
        datePickerDialog = DatePickerDialog(this,
                myDatePickerCallback,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

    }


    fun pushDialog(names:ArrayList<String>, prefill:String?){

        var channels:ArrayList<String> = ArrayList<String>()
        var content:String = "To "

        for (name in names) {
            content += name + ", "
            channels.add(name.replace(" ", ""))
        }

        var dia = MaterialDialog.Builder(this)
                .title("Send Notification")
                .content(content)
                .positiveText("SEND")
                .negativeText("Don't Send")
                .input(null, prefill, { dialog, input ->
                    // Do something
                    for (channel in channels) {
                        var hmap = HashMap<String, String>()
                        hmap.put("channel", channel)
                        hmap.put("msg", input.toString())
                        CloudCalls.push(hmap)
                    }
                })
                .onNegative({ dialog, which -> dialog.dismiss() })
                .show()
        dia.inputEditText.setSingleLine(false);
        dia.inputEditText.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION;

    }



    fun startAdminPushActivity(view: View?) {
        var names:ArrayList<String> = ArrayList<String>()
        names.add("All")
        pushDialog(names, null)
    }


    fun startAdminCheckInActivity(view: View?) {

        var meeting: Meeting? = null
        var lookAtClassDialog: Boolean = false
        var adapter: ArrayAdapter<Meeting> =
                AdminMeetingAdapter(this, CloudQueries.currentSchedule!!)

        var dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.header)
                .setFooter(R.layout.admin_schedule_footer)
                .setOutAnimation(R.anim.abc_fade_out)
                .setOnItemClickListener({ dialog, item, view, position ->
                    meeting = CloudQueries.currentSchedule!![position - 1]
                    lookAtClassDialog = true
                    dialog.dismiss()
                })
                .setOnCancelListener({ dialog -> dialog.dismiss() })
                .setOnBackPressListener({ dialog -> dialog.dismiss() })
                .setOnClickListener({ dialog, view ->
                    if (view?.tag == "add_class")
                        lookAtClassDialog = true
                    dialog.dismiss()
                })
                .setOnDismissListener({ if (lookAtClassDialog) startAdminCheckInDialog(meeting) })
                .create()

        dialogPlus.show()
        var headerTitleView: TextView = dialogPlus.headerView.findViewById(R.id.header_title) as TextView
        headerTitleView.text = "Admin Checkin"

    }

    fun startAdminCheckInDialog(meeting: Meeting?) {

        if (meeting != null) {




        }


    }


//            var adapter: ArrayAdapter<Attendance> =
//                    AdminCheckInAdapter(this, CloudQueries.registeredNextClass)
//
//            var dialogPlus = DialogPlus.newDialog(this)
//                    .setAdapter(adapter)
//                    .setCancelable(true)
//                    .setGravity(Gravity.CENTER)
//                    .setHeader(R.layout.admin_checkin_header)
//                    .setFooter(R.layout.footer)
//                    .setOutAnimation(R.anim.abc_fade_out)
//                    .setOnDismissListener({ })
//                    .setOnItemClickListener({ dialog, item, view, position ->
//                        var hmap = HashMap<String, String>()
//                        val userName = CloudQueries.registeredNextClass!![position - 1].username
//                        hmap.put("username", userName)
//
//                        val checkedInTextView: TextView = view.findViewById(R.id.admin_checkin_text) as TextView
//                        if (CloudQueries.registeredNextClass!![position - 1].checkedin == true) {
//                            checkedInTextView.text = "Not Checked In"
//                            checkedInTextView.setTextColor(Color.DKGRAY)
//                            CloudCalls.adminCheckInSave(hmap)
//                        } else {
//                            checkedInTextView.text = "CHECKED IN"
//                            checkedInTextView.setTextColor(Color.BLUE)
//                            CloudCalls.adminCheckInSave(hmap)
//                        }
//                    })
//                    .setOnCancelListener({ dialog -> dialog.dismiss() })
//                    .setOnBackPressListener({ dialog -> dialog.dismiss() })
//                    .setOnClickListener({ dialog, view -> dialog.dismiss() })
//                    .create()
//
//            dialogPlus.show()
//
//            var location: TextView = dialogPlus.headerView.findViewById(R.id.admin_class_location) as TextView
//            location.text = CloudQueries.nextClass?.location
//
//            var date: TextView = dialogPlus.headerView.findViewById(R.id.admin_class_date) as TextView
//            val sdf: SimpleDateFormat = SimpleDateFormat("EEE, MMM d, hh:mm aaa");
//            date.text = sdf.format(CloudQueries.nextClass?.date)
//        }


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
                .setOnDismissListener({ })
                .setOnItemClickListener({ dialog, item, view, position ->
                    MaterialDialog.Builder(this)
                            .title("How Many Credits?")
                            .inputRangeRes(1, 2, R.color.design_textinput_error_color)
                            .inputType(InputType.TYPE_CLASS_NUMBER)
                            .positiveText("OK")
                            .negativeText("Cancel")
                            .input( null, "10", {dialog, which ->

                                var card: AdminCard = item as AdminCard
                                SweetAlertDialog(view.context, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Add $which Credits?")
                                        .setContentText(card.username + "'s Account!")
                                        .setConfirmText("Yes!")
                                        .setCancelText("Cancel")
                                        .setConfirmClickListener({ sDialog ->
                                            var hmap = HashMap<String, String>()
                                            hmap.put("userName", card.username)
                                            hmap.put("credits", which.toString())
                                            CloudCalls.saveNewCard(hmap)

                                            val creditsTextView: TextView = view.findViewById(R.id.second_text) as TextView
                                            creditsTextView.text = (card.credits!! + Integer.parseInt(which.toString()) ).toString()
                                            creditsTextView.setTextColor(Color.BLUE)

                                            sDialog.setTitleText("Success!")
                                                    .setContentText("$which Credits Have Been Added!")
                                                    .setConfirmText("OK")
                                                    .showCancelButton(false)
                                                    .setConfirmClickListener({ sDialog ->
                                                        sDialog.dismissWithAnimation()
                                                        var names:ArrayList<String> = ArrayList<String>()
                                                        names.add(card.username)
                                                        pushDialog(names, "A Punch Card With $which Credits Has Been Added To Your Account.")
                                                    }
                                                    )
                                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                        }).show()
                            })
                            .show()



                })
                .setOnCancelListener({ dialog -> dialog.dismiss() })
                .setOnBackPressListener({ dialog -> dialog.dismiss() })
                .setOnClickListener({ dialog, view -> dialog.dismiss() })
                .create()

        dialogPlus.show()

        var headerTitleView: TextView = dialogPlus.headerView.findViewById(R.id.header_title) as TextView
        headerTitleView.text = "Card Credits Remaining"

    }

    fun startURL(view:View?) {
        val browserIntent:Intent = Intent(Intent.ACTION_VIEW, Uri.parse(view?.tag as String))
        startActivity(browserIntent)
    }

    fun startMsgDialog(view: View?){
        val smsIntent = Intent(Intent.ACTION_VIEW)
        smsIntent.setType("vnd.android-dir/mms-sms")
        smsIntent.putExtra("address", "2035454694")
        try {
            startActivity(smsIntent);
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(view?.context, "SMS not available", Toast.LENGTH_LONG).show()
        }

    }



    fun countdown(interval:Long){

        var pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
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
                    0 -> pDialog.progressHelper.barColor = ContextCompat.getColor(applicationContext, R.color.blue_btn_bg_color)
                    1 -> pDialog.progressHelper.barColor = ContextCompat.getColor(applicationContext, R.color.material_deep_teal_50)
                    2 -> pDialog.progressHelper.barColor = ContextCompat.getColor(applicationContext, R.color.success_stroke_color)
                    3 -> pDialog.progressHelper.barColor = ContextCompat.getColor(applicationContext, R.color.material_deep_teal_20)
                    4 -> pDialog.progressHelper.barColor = ContextCompat.getColor(applicationContext, R.color.material_blue_grey_80)
                    5 -> pDialog.progressHelper.barColor = ContextCompat.getColor(applicationContext, R.color.warning_stroke_color)
                    6 -> pDialog.progressHelper.barColor = ContextCompat.getColor(applicationContext, R.color.success_stroke_color)
                }
            }
            override fun onFinish() {
                i = -1
                try
                {
                    if ((pDialog != null) && pDialog.isShowing)
                    {
                        pDialog.dismiss()
                    }
                }
                catch (e:IllegalArgumentException) {// Handle or log or ignore
                }
                catch (e:Exception) {// Handle or log or ignore
                }
                finally
                {
                    //pDialog = null
                }
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
                .setOnCancelListener({ dialog -> dialog.dismiss() })
                .setOnBackPressListener({ dialog -> dialog.dismiss() })
                .setOnClickListener({ dialog, view -> dialog.dismiss() })
                .create()

        dialogPlus.show()



    }


    private fun updateViewsWithProfileInfo() {
        creditsRemainingMenuItem.withBadge((CloudQueries.numOfPunchCardCredits - CloudQueries.numOfClassesUserAttended).toString())
        creditsHistoryMenuItem.withBadge(CloudQueries.numOfPunchCardCredits.toString())
        classHistoryMenuItem.withBadge(CloudQueries.numOfClassesUserAttended.toString())
        result.updateItem(creditsRemainingMenuItem)
        result.updateItem(creditsHistoryMenuItem)
        result.updateItem(classHistoryMenuItem)
        miniResult.updateItem(1)
        miniResult.updateItem(2)
        miniResult.updateItem(3)
    }

    fun makeGraphRequest() {
        val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                { jsonObject, graphResponse ->
                    if (jsonObject != null) {
                        val userProfile = JSONObject()
                        try {
                            val currentUser = ParseUser.getCurrentUser()

                            userProfile.put("facebookId", jsonObject.getLong("id"))
                            userProfile.put("name", jsonObject.getString("name"))
                            currentUser.put("name", jsonObject.getString("name"))
                            if (jsonObject.has("gender")) userProfile.put("gender", jsonObject.getString("gender"))
                            if (jsonObject.has("email")) {
                                userProfile.put("email", jsonObject.getString("email"))
                                currentUser.put("email", jsonObject.getString("email"))
                            }
                            // Save the user profile info in a user property
                            currentUser.put("profile", userProfile)
                            currentUser.saveInBackground()

                        } catch (e: JSONException) {
                            //Log.d("MyTagg",
                            //        "Error parsing returned user data. " + e)
                        }
                    } else if (graphResponse.getError() != null) {
                        //Log.d("My Tagg", "error: " + graphResponse.getError())

                    }
                })
        val parameters = Bundle()
        parameters.putString("fields", "id,email,gender,name")
        request.parameters = parameters
        request.executeAsync()
    }


    companion object {

        fun setupAdmin(view:View){

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




}


