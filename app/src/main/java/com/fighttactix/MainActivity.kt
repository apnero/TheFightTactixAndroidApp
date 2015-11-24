package com.fighttactix

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
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
import android.view.ViewGroup
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
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import java.text.DateFormat


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


    lateinit var datePickerDialog:DatePickerDialog
    lateinit var timePickerDialog:TimePickerDialog


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

        CloudQueries.maxClassSize()
        CloudQueries.checkinClass()
        CloudQueries.userPunchCards()
        CloudQueries.currentSchedule()
        CloudQueries.userClassHistory()
        CloudQueries.locations()

        if (CloudQueries.userAdministrator) {
            CloudQueries.allUserAttendance()
            CloudQueries.allUserCards()
        }

        countdown(500)

    }

    override fun onPause() {
        super.onPause()
        AppEventsLogger.deactivateApp(this);
    }

    private fun initToolbar() {

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar.setDisplayShowTitleEnabled(false)
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
                                            sDialog.dismissWithAnimation()
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
                .setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(dialog: DialogPlus, item: Any, view: View, position: Int) {
                        meeting = CloudQueries.currentSchedule!![position - 1]
                        lookAtClassDialog = true
                        dialog.dismiss()
                    }
                })
                .setOnCancelListener(object : OnCancelListener {
                    override fun onCancel(dialog: DialogPlus) {
                        dialog.dismiss()
                    }
                })
                .setOnBackPressListener(object : OnBackPressListener {
                    override fun onBackPressed(dialog: DialogPlus) {
                        dialog.dismiss()
                    }
                }).setOnClickListener(object : OnClickListener {
                    override fun onClick(dialog: DialogPlus, view: View?) {
                        if(view?.tag == "add_class") lookAtClassDialog = true
                        dialog.dismiss()
                    }
                })
                .setOnDismissListener(object : OnDismissListener {
                    override fun onDismiss(dialog: DialogPlus) {
                        if(lookAtClassDialog) startAdminClassDialog(meeting)
                    }
                })
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
                                        .setConfirmClickListener(object : SweetAlertDialog.OnSweetClickListener {
                                            override fun onClick(sDialog: SweetAlertDialog) {
                                                sDialog.dismissWithAnimation()
                                                dialog.dismiss()
                                            }
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
                                        .setConfirmClickListener(object : SweetAlertDialog.OnSweetClickListener {
                                            override fun onClick(sDialog: SweetAlertDialog) {
                                                sDialog.dismissWithAnimation()
                                                dialog.dismiss()
                                                if (!userList.isEmpty()) pushDialog(userList, sdf.format(meeting?.date) + " at " + meeting?.location + " has been changed!")
                                            }
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
                                .setConfirmClickListener(object : SweetAlertDialog.OnSweetClickListener {
                                    override fun onClick(sDialog: SweetAlertDialog) {
                                        var hmap = HashMap<String, String>()
                                        hmap.put("objectId", meeting!!.objectId)
                                        CloudCalls.adminDeleteMeeting(hmap, attendanceList)
                                        dialog.dismiss()
                                        sDialog.setTitleText("Success!")
                                            .setContentText("Class has been deleted!")
                                            .setConfirmText("OK")
                                            .showCancelButton(false)
                                            .setConfirmClickListener(object : SweetAlertDialog.OnSweetClickListener {
                                                override fun onClick(sDialog: SweetAlertDialog) {
                                                    sDialog.dismissWithAnimation()
                                                    if (!userList.isEmpty()) pushDialog(userList, sdf.format(meeting?.date) + " at " + meeting?.location + " has been cancelled!")
                                                }
                                            }
                                            )
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                    }
                                }).show()


                        }
                        else if (view?.tag == "send_notification")
                            if (!userList.isEmpty()) pushDialog(userList, null)
                    }

                })
                .setOnDismissListener(object : OnDismissListener {
                    override fun onDismiss(dialog: DialogPlus) {

                    }
                })
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
        locationPicker.setAdapter(adapter)

        if (userList.isEmpty())
            sendNotification.visibility = View.GONE

        var sdfDate = SimpleDateFormat("EEE, MMM d")
        var sdfTime = SimpleDateFormat("hh:mm a")
        if (meeting == null) {
            openPicker.text = "Registration Closed"
            deleteClassButton.visibility = View.INVISIBLE
            saveClassButton.visibility = View.INVISIBLE
            //datePicker.text = sdfDate.format(Date())
            //timePicker.text = sdfTime.format(Date())
        } else{
            datePicker.text = sdfDate.format(meeting?.date)
            timePicker.text = sdfTime.format(meeting?.date)
            locationPicker.setSelection(locationList.indexOfRaw(meeting.location))
            openPicker.text = if(meeting?.open!!) "Registration Open" else "Registration Closed"
        }


        val myTimePickerCallback = object:TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view:TimePicker, hour:Int, minute:Int) {// do stuff with the time from the picker
                val s:String = hour.toString() + ":" + minute.toString() // timePicker.text = hourOfDay.toString() + ":" + minute.toString()
                val sdf:SimpleDateFormat = SimpleDateFormat("HH:mm")
                val d:Date = sdf.parse(s)
                val sdf2:SimpleDateFormat = SimpleDateFormat("hh:mm aaa")
                timePicker.text = sdf2.format(d)
                datehmap.put("hour", hour.toString())
                datehmap.put("minute", minute.toString())
                if(datehmap.size == 5)
                    saveClassButton.visibility = View.VISIBLE

            }
        }
        timePickerDialog = TimePickerDialog(this,
                myTimePickerCallback,
                Calendar.getInstance().get(Calendar.HOUR),
                Calendar.getInstance().get(Calendar.MINUTE),
                false)

        val myDatePickerCallback = object:DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view:DatePicker, year:Int, month:Int, day:Int) {// do stuff with date from picker
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
                .input(null, prefill, object : MaterialDialog.InputCallback {
                    override fun onInput(dialog: MaterialDialog, input: CharSequence) {
                        // Do something
                        for (channel in channels) {
                            var hmap = HashMap<String, String>()
                            hmap.put("channel", channel)
                            hmap.put("msg", input.toString())
                            CloudCalls.push(hmap)
                        }
                    }
                })
                .onNegative(object : MaterialDialog.SingleButtonCallback {
                    override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                        dialog.dismiss()
                    }
                })
                .show()
        dia.inputEditText.setSingleLine(false);
        dia.inputEditText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);

    }



    fun startAdminPushActivity(view: View?) {
        var names:ArrayList<String> = ArrayList<String>()
        names.add("All")
        pushDialog(names, null)
    }


    fun startAdminCheckInActivity(view: View?) {

        if(CloudQueries.nextClass != null) {

            var adapter: ArrayAdapter<Attendance> =
                    AdminCheckInAdapter(this, CloudQueries.registeredNextClass)

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
            location.text = CloudQueries.nextClass?.location

            var date: TextView = dialogPlus.headerView.findViewById(R.id.admin_class_date) as TextView
            val sdf: SimpleDateFormat = SimpleDateFormat("EEE, MMM d, hh:mm aaa");
            date.text = sdf.format(CloudQueries.nextClass?.date)
        }
        else {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("No Class!")
                .setContentText("There are no classes scheduled!")
                .show()
        }

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
                                                        var names:ArrayList<String> = ArrayList<String>()
                                                        names.add(card.username)
                                                        pushDialog(names, "A Punch Card Has Been Added To Your Account.")
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

