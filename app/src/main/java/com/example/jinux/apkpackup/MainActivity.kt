package com.example.jinux.apkpackup

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.*
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.content.Intent
import android.net.Uri
import android.provider.Settings


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apkListView = listView {
            padding = dip(6)

            val installApks =  packageManager.getInstalledPackages(0)

            adapter = MListAdapter(this@MainActivity, installApks)

            onItemClickListener = object : AdapterView.OnItemClickListener {
                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = Uri.fromParts("package", installApks[position].packageName, null)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}

class MListAdapter : BaseAdapter {

    private var mContext: Context

    private var mData: MutableList<PackageInfo>

    constructor(context: Context, data: MutableList<PackageInfo>) {
        mContext = context
        mData = data
    }

    override fun getItem(position: Int): Any {
        return mData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val apkName = mData[position]
        val apkNameTextView = TextView(mContext)
        apkNameTextView.text = apkName.packageName
        apkNameTextView.padding = apkNameTextView.dip(20)
        return apkNameTextView
    }
}
