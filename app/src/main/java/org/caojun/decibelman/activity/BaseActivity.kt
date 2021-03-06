package org.caojun.decibelman.activity

import android.preference.PreferenceManager
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import org.caojun.activity.BaseAppCompatActivity
import org.caojun.decibelman.Constant
import org.caojun.decibelman.R
import org.caojun.decibelman.room.DIBmob
import org.caojun.decibelman.room.DecibelInfo
import org.caojun.decibelman.room.DecibelInfoDatabase
import org.caojun.utils.ActivityUtils
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import java.util.Date

/**
 * Created by CaoJun on 2017/9/25.
 */
open class BaseActivity: BaseAppCompatActivity() {

    companion object {
        val PREFER_NAME = "org.caojun.decibelman.settings"
        private const val Key_Confirm_Dialog = "sp_confirm_dialog"
        const val Key_Switch_LP = "sp_switch_lp"
        const val Key_Switch_Welcome = "Key_Switch_Welcome"
    }

    interface OnDatabaseListener {
        fun onSuccess()
        fun onError()
    }

    private var onDatabaseListener: OnDatabaseListener? = null
    fun setOnDatabaseListener(onDatabaseListener: OnDatabaseListener) {
        this.onDatabaseListener = onDatabaseListener
    }

    fun getDecibelInfos(): List<DecibelInfo> = DecibelInfoDatabase.getDatabase(this).getDao().queryAll()

    fun alertSaveDecibelInfo() {
        if (Constant.min.toString().indexOf(".") < 0 || Constant.max.toString().indexOf(".") < 0 || Constant.average.toString().indexOf(".") < 0) {
            return
        }
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        if (settings.getBoolean(Key_Confirm_Dialog, true)) {
            alert(R.string.alert_save_data) {
                positiveButton(R.string.yes) {
                    saveDecibelInfo()
                }
                negativeButton(R.string.no) {}
                neutralPressed(R.string.yes_no_alert) {
                    saveDecibelInfo()
                    saveSharedPreferences()
                }
            }.show()
        } else {
            saveDecibelInfo()
        }
    }

    fun getSavedDecibelInfo(): DecibelInfo {
        val di = DecibelInfo()
        di.latitude = Constant.latitude
        di.longitude = Constant.longitude
        di.imei = Constant.decibelInfo0!!.imei
        di.random_id = Constant.decibelInfo0!!.random_id
        di.database_time = Constant.decibelInfo0!!.database_time
        di.time = Date().time
        di.decibel_min = Constant.min
        di.decibel_max = Constant.max
        di.decibel_average = Constant.average
        return di
    }

    private fun saveDecibelInfo() {
        doAsync {
            val di = getSavedDecibelInfo()
            DecibelInfoDatabase.getDatabase(this@BaseActivity).getDao().insert(di)

            val diBmob = DIBmob(di)
            diBmob.save(object : SaveListener<String>() {

                override fun done(o: String, e: BmobException?) {
//                    if (e == null) {
//                        KLog.d(javaClass.name, "Bomb saved ok")
//                    } else {
//                        KLog.d(javaClass.name, "Bomb saved error: " + e.toString())
//                    }
                }
            })
            onDatabaseListener?.onSuccess()
        }
    }

    private fun saveSharedPreferences() {
        doAsync {
            val settings = PreferenceManager.getDefaultSharedPreferences(this@BaseActivity).edit()
            settings.putBoolean(Key_Confirm_Dialog, false)
            settings.apply()
        }
    }

    fun askRecordAudioPermission() {
        alert(R.string.ask_record_audio_permission) {
            positiveButton(android.R.string.ok) {
                ActivityUtils.gotoPermission(this@BaseActivity)
            }
            negativeButton(R.string.quit) {
                finish()
            }
            onCancelled {
                finish()
            }
        }.show()
    }
}