package com.fighttactix

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import butterknife.bindView

/**
 * Created by Andrew on 11/1/2015.
 */
public class SocialActivity: AppCompatActivity() {

    val toolbar: Toolbar by bindView(R.id.toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social)

        initToolbar()

    }

    private fun initToolbar() {

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        actionBar?.title = "Connections"
        actionBar?.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
