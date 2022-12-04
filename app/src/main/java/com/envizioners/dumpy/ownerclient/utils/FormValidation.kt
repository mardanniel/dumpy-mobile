package com.envizioners.dumpy.ownerclient.utils

import com.envizioners.dumpy.ownerclient.response.shared.LoginFields

object FormValidation {

    fun handleLoginValidation(loginFields: LoginFields): Pair<String, Boolean>{
        var mainString = "\n"
        if(loginFields.email.isNotEmpty()) mainString += loginFields.email + "\n"
        if(loginFields.password.isNotEmpty()) mainString += loginFields.password + "\n"
        if(loginFields.checkPass.isNotEmpty()) mainString += loginFields.checkPass + "\n"
        return if (mainString.length > 2){
            Pair(mainString, false)
        }else{
            Pair(mainString, true)
        }
    }

}