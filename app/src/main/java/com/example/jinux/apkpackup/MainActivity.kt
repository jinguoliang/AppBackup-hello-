@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package com.example.jinux.apkpackup

import android.app.ListActivity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import org.jetbrains.anko.*
import android.view.*
import org.example.ankodemo.util.ListItem
import org.example.ankodemo.util.ListItemAdapter
import org.jetbrains.anko.sdk25.coroutines.onClick


class MainActivity : ListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ui = MainActivityUI()
        ui.setContentView(this)

        val launchableApp = getLauchableApp()

        listAdapter = ApksAdapter(this, launchableApp)

        ui.searchTextView.addTextChangedListener(SearchListener(launchableApp) {
            listAdapter = ApksAdapter(this, it)
        })
    }

    private fun getLauchableApp(): List<ApkItem> {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(intent, 0).map { apk ->
            ApkItem(apk.loadIcon(packageManager),
                    apk.loadLabel(packageManager),
                    apk.activityInfo.packageName,
                    onStartClick = {
                        val intent = packageManager.getLaunchIntentForPackage(apk.activityInfo.packageName)
                        try {
                            startActivity(intent)
                        } catch (e: Exception) {
                            toast(R.string.app_cant_start)
                        }
                    },
                    onDetailClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", apk.activityInfo.packageName, null)
                        try {
                            startActivity(intent)
                        } catch (e: Exception) {
                            toast(R.string.app_cant_show_detail_page)
                        }
                    })
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

class SearchListener(val launchableApp: List<ApkItem>, val onSearch: (List<ApkItem>) -> Unit) : TextWatcher {
    override fun afterTextChanged(editable: Editable?) {
        val s = editable?.toString() ?: ""
        if (s.isEmpty()) launchableApp

        val filterd = launchableApp.filter { it.title.contains(s, ignoreCase = true)
                                                || s.toLowerCase() in it.pkg }
        onSearch(filterd)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

}

class MainActivityUI : AnkoComponent<MainActivity> {
    lateinit var searchTextView: EditText

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        verticalLayout {
            padding = dip(10)
            frameLayout {
                searchTextView = editText {
                    hint = resources.getString(R.string.search_apk)
                }.lparams {
                    margin = dip(5)
                }
            }
            listView {
                id = android.R.id.list
            }
            textView {
                id = android.R.id.empty
            }
        }
    }.view()
}

class ApkItem(val icon: Drawable, val title: CharSequence,
              val pkg: CharSequence,
              val onStartClick: ((v: View) -> Unit),
              val onDetailClick: ((v: View) -> Unit)) : ListItem {

    override fun apply(convertView: View) {

        val holder = getHolder(convertView)
        holder.icon.image = icon
        holder.title.text = title
        holder.start.onClick {
            onStartClick(holder.start)
        }
        holder.detail.onClick {
            onDetailClick(holder.detail)
        }
    }

    private fun getHolder(convertView: View): ApkViewHolder {
        return (convertView.tag as? ApkViewHolder
                ?: ApkViewHolder(convertView.find(android.R.id.icon),
                convertView.find(android.R.id.text1),
                convertView.find(android.R.id.button1),
                convertView.find(android.R.id.button2)).apply {
            convertView.tag = this
        })
    }

    override fun createView(ui: AnkoContext<ListItemAdapter>) =
            ui.apply {
                linearLayout {
                    padding = dip(10)

                    imageView {
                        id = android.R.id.icon
                        padding = dip(5)
                    }.lparams {
                        width = resources.getDimensionPixelSize(R.dimen.app_list_icon_size)
                        height = resources.getDimensionPixelSize(R.dimen.app_list_icon_size)
                    }

                    textView {
                        id = android.R.id.text1
                        padding = dip(20)
                    }.lparams {
                        weight = 1f
                    }

                    button {
                        dip(3)
                        id = android.R.id.button2
                        text = resources.getString(R.string.detail)
                    }

                    button {
                        dip(3)
                        id = android.R.id.button1
                        text = resources.getString(R.string.start)
                    }
                }
            }.view


    internal class ApkViewHolder(val icon: ImageView,
                                 val title: TextView,
                                 val start: Button, val detail: Button)

}

class ApksAdapter(ctx: Context, items: List<ListItem>) : ListItemAdapter(ctx, items) {
    override val listItemClasses: List<Class<out ListItem>>
        get() = listOf(ApkItem::class.java)
}
