package com.example.aidruginteractionchecker

import android.app.Application
import android.util.Log
import kotlinx.coroutines.*

class MyApp : Application() {

    lateinit var word2vecEmbeddings: Map<String, FloatArray>
        private set

    lateinit var word2vecKey: Map<String, Int>
        private set

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()

        // Load in the background to avoid blocking the UI thread
        GlobalScope.launch(Dispatchers.IO) {

            // Load vocabulary_with_index.txt
            val keyMap = mutableMapOf<String, Int>()
            assets.open("vocabulary_with_index.txt").bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val parts = line.trim().split(Regex("\\s+"))
                    if (parts.size == 2) {
                        val word = parts[0]
                        val index = parts[1].toIntOrNull()
                        if (index != null) {
                            keyMap[word] = index
                        }
                    }
                }
            }
            word2vecKey = keyMap

            // Load word_embeddings.txt
            val embedMap = mutableMapOf<String, FloatArray>()
            assets.open("word_embeddings.txt").bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val parts = line.trim().split("\\s+".toRegex())
                    if (parts.size > 1) {
                        val word = parts[0]
                        val vector = FloatArray(parts.size - 1) { i ->
                            parts[i + 1].toFloat()
                        }
                        embedMap[word] = vector
                    }
                }
            }
            word2vecEmbeddings = embedMap

            Log.d("Embeddings", "Loaded ${word2vecEmbeddings.size} words")
            Log.d("Vocabulary", "Loaded ${word2vecKey.size} keys")
        }
    }
}
