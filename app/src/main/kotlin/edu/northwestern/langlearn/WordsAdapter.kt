package edu.northwestern.langlearn

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

data class Word(val norm: String, val audio_url: String, val word: String)

class WordsAdapter {
    fun ParseJson(jsonStr: String?): List<Word> {
        val json = jsonStr ?: """
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
        // val Words: Array<Word> = Array(0) { idx -> Word(name = "$idx") } // Array(0, { idx -> Word(name = "$idx") })
        // val Words: Array<Word> = Array(0) { Word(name = "$it") }

        try {
            val jsonObj = JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1))
            val wordJson = jsonObj.getJSONArray("words") // JSONException ?

            for (i in 0..wordJson!!.length() - 1) {
                val n = wordJson.getJSONObject(i).getString("norm")
                val url = wordJson.getJSONObject(i).getString("audio_url")
                val w = wordJson.getJSONObject(i).getString("word")
                val word = Word(n, url, w)
                // word.word = w // word is immutable, so we can't do this, not a var

                Log.d(javaClass.simpleName, "$i $word")
                Words.add(word)
            }
        } catch (e: JSONException) {
            Log.e(javaClass.simpleName, e.message)
        }

        return Words
    }
}