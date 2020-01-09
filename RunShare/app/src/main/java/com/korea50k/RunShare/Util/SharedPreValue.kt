package com.korea50k.RunShare.Util

import android.content.Context

class SharedPreValue {
    companion object {
        val INTENT_ID_DATA = "utils.id"
        val INTENT_PWD_DATA = "utils.pwd"
        val INTENT_EMAIL_DATA = "utils.email"
        val INTENT_AUTO_LOGIN_DATA = "utils.auto.login"


        fun setIDData(ctx: Context, value : String){
            val preferences = ctx.getSharedPreferences(INTENT_ID_DATA, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString(INTENT_ID_DATA, value)
            editor.commit()
        }

        fun getIDData(ctx: Context) : String?{
            val preferences = ctx.getSharedPreferences(INTENT_ID_DATA, Context.MODE_PRIVATE)
            return preferences.getString(INTENT_ID_DATA, "")
        }

        fun setPWDData(ctx: Context, value : String){
            val preferences = ctx.getSharedPreferences(INTENT_PWD_DATA, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString(INTENT_PWD_DATA, value)
            editor.commit()
        }

        fun getPWDData(ctx: Context) : String?{
            val preferences = ctx.getSharedPreferences(INTENT_PWD_DATA, Context.MODE_PRIVATE)
            return preferences.getString(INTENT_PWD_DATA, "")
        }

        fun setEMAILData(ctx: Context, value : String){
            val preferences = ctx.getSharedPreferences(INTENT_EMAIL_DATA, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString(INTENT_EMAIL_DATA, value)
            editor.commit()
        }

        fun getEMAILData(ctx: Context) : String?{
            val preferences = ctx.getSharedPreferences(INTENT_EMAIL_DATA, Context.MODE_PRIVATE)
            return preferences.getString(INTENT_EMAIL_DATA, "")
        }

        fun setAutoLogin(ctx: Context, value: Boolean){
            val preferences = ctx.getSharedPreferences(INTENT_AUTO_LOGIN_DATA, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putBoolean(INTENT_AUTO_LOGIN_DATA, value)
            editor.commit()
        }

        fun getAutoLogin(ctx: Context) : Boolean{
            val preferences = ctx.getSharedPreferences(INTENT_AUTO_LOGIN_DATA, Context.MODE_PRIVATE)
            return preferences.getBoolean(INTENT_AUTO_LOGIN_DATA, false)
        }
    }
}