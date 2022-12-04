package com.envizioners.dumpy.ownerclient.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "loginSession")

object UserPreference{

    suspend fun saveUserType(context: Context, key: String, value: String){
        val userTypeKey = stringPreferencesKey(key)
        context.dataStore.edit {
            it[userTypeKey] = value
        }
    }

    suspend fun saveUserEmail(context: Context, key: String, value: String){
        val userEmailKey = stringPreferencesKey(key)
        context.dataStore.edit {
            it[userEmailKey] = value
        }
    }
    suspend fun saveUserPassword(context: Context, key: String, value: String){
        val userPasswordKey = stringPreferencesKey(key)
        context.dataStore.edit {
            it[userPasswordKey] = value
        }
    }

    suspend fun saveUserID(context: Context, key: String, value: Int){
        val userIDKey = intPreferencesKey(key)
        context.dataStore.edit {
            it[userIDKey] = value
        }
    }

    suspend fun saveUserBusinessID(context: Context, key: String, value: Int){
        val userBusinessIDKey = intPreferencesKey(key)
        context.dataStore.edit {
            it[userBusinessIDKey] = value
        }
    }

    suspend fun saveUserEmployerID(context: Context, key: String, value: Int){
        val userEmployerIDKey = intPreferencesKey(key)
        context.dataStore.edit {
            it[userEmployerIDKey] = value
        }
    }

    suspend fun saveUserAuthorization(context: Context, key: String, value: String){
        val userAuthorizationKey = stringPreferencesKey(key)
        context.dataStore.edit {
            it[userAuthorizationKey] = value
        }
    }

    suspend fun saveUDT(context: Context, key: String, value: String){
        val userUDTKey = stringPreferencesKey(key)
        context.dataStore.edit {
            it[userUDTKey] = value
        }
    }

    suspend fun getUserType(context: Context, key: String): String{
        val userTypeKey = stringPreferencesKey(key)
        val valueFlow: Flow<String> = context.dataStore.data.map {
            it[userTypeKey] ?: ""
        }
        return valueFlow.first()
    }

    suspend fun getUserEmail(context: Context, key: String): String{
        val userEmailKey = stringPreferencesKey(key)
        val valueFlow: Flow<String> = context.dataStore.data.map {
            it[userEmailKey] ?: ""
        }
        return valueFlow.first()
    }
    suspend fun getUserPassword(context: Context, key: String): String{
        val userPasswordKey = stringPreferencesKey(key)
        val valueFlow: Flow<String> = context.dataStore.data.map {
            it[userPasswordKey] ?: ""
        }
        return valueFlow.first()
    }

    suspend fun getUserID(context: Context, key: String): Int{
        val userIDKey = intPreferencesKey(key)
        val valueFlow: Flow<Int> = context.dataStore.data.map {
            it[userIDKey] ?: 0
        }
        return valueFlow.first()
    }

    suspend fun getUserBusinessID(context: Context, key: String): Int{
        val userBusinessIDKey = intPreferencesKey(key)
        val valueFlow: Flow<Int> = context.dataStore.data.map {
            it[userBusinessIDKey] ?: 0
        }
        return valueFlow.first()
    }

    suspend fun getUserEmployerID(context: Context, key: String): Int{
        val userEmployerIDKey = intPreferencesKey(key)
        val valueFlow: Flow<Int> = context.dataStore.data.map {
            it[userEmployerIDKey] ?: 0
        }
        return valueFlow.first()
    }

    suspend fun getUserAuthorization(context: Context, key: String): String{
        val userAuthorizationKey = stringPreferencesKey(key)
        val valueFlow: Flow<String> = context.dataStore.data.map {
            it[userAuthorizationKey] ?: "Default-Api-Key"
        }
        return valueFlow.first()
    }

    suspend fun getUDT(context: Context, key: String): String{
        val userUDTKey = stringPreferencesKey(key)
        val valueFlow: Flow<String> = context.dataStore.data.map {
            it[userUDTKey] ?: ""
        }
        return valueFlow.first()
    }

    suspend fun clearPreferences(context: Context) {
        context.dataStore.edit {
            it.clear()
        }
    }
}