package edu.northwestern.langlearn

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log

import org.json.JSONObject

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray

import java.net.URL

data class Word(val norm: String, val audio_url: String, val word: String)

interface WordsProviderUpdate {
    val wordsProviderUpdateActivity: AppCompatActivity

    fun updateJSONWords(json: String)
    fun openMessageActivity(errorMsg: String) {
        val msgIntent = Intent(wordsProviderUpdateActivity, MessageActivity::class.java)
        val MESSAGE_INTENT_EXTRA = "message"

        msgIntent.putExtra(MESSAGE_INTENT_EXTRA, errorMsg)
        wordsProviderUpdateActivity.startActivity(msgIntent)
    }
}

class WordsProvider(val jsonUrl: String) {
    var jsonStartDelay: Long = SleepMode.DEFAULT_START_WORDS_DELAY_MILLIS
        private set
    var jsonWordDelay: Long = SleepMode.DEFAULT_BETWEEN_WORDS_DELAY_MILLIS
        private set
    var jsonSham: Boolean = SleepMode.PLAY_ONLY_WHITE_NOISE_SHAM
        private set
    var jsonError: String = SleepMode.JSON_ERROR_MESSAGE
        private set

    private val TAG = javaClass.simpleName

    fun fetchJSONWords(updateImpl: WordsProviderUpdate): Unit {
        doAsync {
            URL(jsonUrl).readText { text, error ->
                if (text.isNotEmpty()) {
                    Log.d(javaClass.simpleName, text.length.toString())
                    uiThread { updateImpl.updateJSONWords(text) }
                } else {
                    uiThread { updateImpl.openMessageActivity(error ?: "The exception message was null") }
                }
            }
        }
    }

    fun parseJSONWords(wordsJSON: String?): List<Word> {
        val json = wordsJSON ?: """
        {
            "words": [
                {
                    "norm": "velvet",
                    "audio_url": "http://someplace.cool",
                    "word": "velvet"
                }
            ]
        }
        """
        val Words: MutableList<Word> = mutableListOf()
        val jsonObj = JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1))

        //jsonObj.getIt<Int>("start_delay") { jsonStartDelay = it.toLong() * 1000 }
        //jsonObj.getIt<Int>("word_delay") { jsonWordDelay = it.toLong() * 1000 }
        //jsonObj.getIt<Boolean>("sham") { jsonSham = it }
        //jsonObj.getIt<String>("error") { jsonError = it }

        jsonObj.unless {
            jsonStartDelay = getLong("start_delay") * 1000
            jsonWordDelay = getLong("word_delay") * 1000
        }
        jsonObj.unless { jsonSham = getBoolean("sham") }
        jsonObj.unless { jsonError = getString("error") }


        jsonObj.getIt<JSONArray>("words") {
            var n: String = ""
            var url: String = ""
            var w: String = ""

            for (i in 0..it.length() - 1) {
                it.getJSONObject(i).unless {
                    n = getString("norm")
                    url = getString("audio_url")
                    w = getString("word")
                }
                //it.getJSONObject(i).getIt<String>("norm") { n = it }
                //it.getJSONObject(i).getIt<String>("audio_url") { url = it }
                //it.getJSONObject(i).getIt<String>("word") { w = it }

                val word = Word(n, url, w)

                Log.d(TAG, "$i $word")
                Words.add(word)
            }
        }

        return Words
    }
}
