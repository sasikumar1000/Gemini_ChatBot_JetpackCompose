package com.learning.mychatbotapp

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel:ViewModel() {


    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    val genativeModel :GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = Constants.apiKey
    )
    fun sendMessage(question: String) {

        viewModelScope.launch {
            try {
                val chat = genativeModel.startChat(
                    history = messageList.map {
                        content(it.role){
                            text(it.message)
                        }
                    }.toList()
                )
                messageList.add(MessageModel(question,"user"))
                messageList.add(MessageModel("Typing..","model"))
                val response = chat.sendMessage(question)
                messageList.removeLast()
                messageList.add(MessageModel(response.text.toString(),"model"))
//                val res = genativeModel.generateContent(question)
            } catch (e:Exception) {
                messageList.removeLast()
                messageList.add(MessageModel("Error"+ e.message.toString(),"model"))
                e.printStackTrace()
            }
        }
    }
}