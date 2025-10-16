package com.example.aidruginteractionchecker

import android.app.Application
import android.util.Log
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.coroutines.*
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.InputStreamReader

class MyApp : Application() {

    lateinit var interpreter: Interpreter
        private set

    lateinit var sideEffectMap: Map<String, Int>
        private set

    lateinit var word2vecKey: Map<String, Int>
        private set

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()

        // Load in the background to avoid blocking the UI thread
        GlobalScope.launch(Dispatchers.IO) {

            FirebaseModelDownloader.getInstance().getModel(
                "Drug_Interaction_Model",
                DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                CustomModelDownloadConditions.Builder().requireWifi().build()
            ).addOnSuccessListener {
                //Toast.makeText(requireContext(), "ML Model Successfully Downloaded!", Toast.LENGTH_SHORT).show()
                    model: CustomModel? ->
                val modelFile = model?.file
                if (modelFile != null) {
                    interpreter = Interpreter(modelFile)

                    // Log input tensors
                    val inputCount = interpreter.inputTensorCount
                    for (i in 0 until inputCount) {
                        val tensor = interpreter.getInputTensor(i)
                        val shape = tensor.shape().joinToString(", ")
                        val dtype = tensor.dataType()
                        Log.d("TFLite", "Input[$i] shape: [$shape], type: $dtype")
                    }

                    // Log output tensors
                    val outputCount = interpreter.outputTensorCount
                    for (i in 0 until outputCount) {
                        val tensor = interpreter.getOutputTensor(i)
                        val shape = tensor.shape().joinToString(", ")
                        val dtype = tensor.dataType()
                        Log.d("TFLite", "Output[$i] shape: [$shape], type: $dtype")
                    }
                }
            }

            // Load entity_vocab.json
            assets.open("entity_vocab.json").use { input ->
                InputStreamReader(input).use { reader ->
                    val chars = CharArray(8192)
                    val sb = StringBuilder()
                    var n: Int
                    while (reader.read(chars).also { n = it } != -1) sb.append(chars, 0, n)

                    val json = JSONObject(sb.toString())
                    val map = HashMap<String, Int>(json.length())
                    val keys = json.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        map[k] = json.getInt(k)
                    }
                   word2vecKey = map
                }
            }
            Log.d("Vocabulary", "Loaded ${word2vecKey.size} keys")

            // Load side_effects_vocab.json
            assets.open("side_effects_vocab.json").use { input ->
                InputStreamReader(input).use { reader ->
                    val chars = CharArray(8192)
                    val sb = StringBuilder()
                    var n: Int
                    while (reader.read(chars).also { n = it } != -1) sb.append(chars, 0, n)

                    val json = JSONObject(sb.toString())
                    val map = HashMap<String, Int>(json.length())
                    val keys = json.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        map[k] = json.getInt(k)
                    }
                    sideEffectMap = map
                }
            }
            Log.d("Side Effects", "Loaded ${sideEffectMap.size} keys")

        }
    }
}
