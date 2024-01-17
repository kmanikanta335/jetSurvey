package com.example.surveyquestions.survey

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SurveyViewModel(
    private val photoUriManager: PhotoUriManager,
   val context: Context
) : ViewModel() {

    val  sub1 = SubQuestion(0,"how much is your income ?","bool", listOf("YES","NO"))
    val data = Question(0,"Are you employeed ?","bool",sub1,listOf("YES","NO"))
    val string = Json.encodeToString(data)

    val myObject: Question = Json.decodeFromString<Question>(string)
    var questions: List<Question> = listOf(myObject,myObject)

    val fileName = "questionnnaire.json"
    init {
        viewModelScope.launch {
            loadQuestions()
            viewModelScope.coroutineContext[Job]?.join()

        }
    }
    private  fun assetFromAssets(fileName: String): InputStream? =  context.assets.open(fileName)

    // Obtain the InputStream for the file from the assets folder.
    @SuppressLint("SuspiciousIndentation")
    private fun loadQuestions() {

        try {
            // Attempt to obtain the InputStream for the file from the assets folder.
            val inputStream: InputStream? = assetFromAssets(fileName)

            // Check if the InputStream is not null before reading the content.
            if (inputStream != null) {
                var jsonString = inputStream.bufferedReader().use { it.readText() }

                questions = Json.decodeFromString(jsonString)

                // Now, you can use the 'questions' list as needed.
                questions.forEach { println(it) }
            } else {
                println("Error: InputStream is null. Make sure '$fileName' is in the correct location in the assets folder.")

            }

        } catch (e: Exception) {
            println("Error: ${e.message}")
            e.printStackTrace()
        }

    }

    private fun downloadModelIfNotAvailable(
        languageTranslator: Translator,
        context: Context
    ) {
//        _state.value = state.value.copy(
//            isButtonEnabled = false
//        )

        val conditions = DownloadConditions
            .Builder()
            .requireWifi()
            .build()


        languageTranslator
            .downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Downloaded model successfully..",
                    Toast.LENGTH_SHORT
                ).show()

//                _state.value = state.value.copy(
//                    isButtonEnabled = true
//                )
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Some error occurred couldn't download language model..",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
    private suspend fun Task<String>.await(): String {
        return suspendCoroutine { continuation ->
            addOnSuccessListener { result -> continuation.resume(result) }
            addOnFailureListener { e -> continuation.resumeWithException(e) }
        }
    }

    private val _languageCode = MutableLiveData<String>()
    val languageCode: LiveData<String> get() = _languageCode
    fun setLanguageCode(code: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _languageCode.value = code
        }
    }

    private val questionOrder: List<Question> = questions

    private var questionIndex = 0

    // ----- Responses exposed as State -----

    private val _freeTimeResponse = mutableStateListOf<Int>()
    val freeTimeResponse: List<Int>
        get() = _freeTimeResponse

//    private val _superheroResponse = mutableStateOf<Superhero?>(null)
//    val superheroResponse: Superhero?
//        get() = _superheroResponse.value

    private val _takeawayResponse = mutableStateOf<Long?>(null)
    val takeawayResponse: Long?
        get() = _takeawayResponse.value

    private val _feelingAboutSelfiesResponse = mutableStateOf<Float?>(null)
    val feelingAboutSelfiesResponse: Float?
        get() = _feelingAboutSelfiesResponse.value

    private val _selfieUri = mutableStateOf<Uri?>(null)
    val selfieUri
        get() = _selfieUri.value

    // ----- Survey status exposed as State -----

    private val _surveyScreenData = mutableStateOf(createSurveyScreenData())
    val surveyScreenData: SurveyScreenData?
        get() = _surveyScreenData.value

    private val _isNextEnabled = mutableStateOf(false)
    val isNextEnabled: Boolean
        get() = _isNextEnabled.value

    /**
     * Returns true if the ViewModel handled the back press (i.e., it went back one question)
     */
    fun onBackPressed(): Boolean {
        if (questionIndex == 0) {
            return false
        }
        changeQuestion(questionIndex - 1)
        return true
    }

    fun onPreviousPressed() {
        if (questionIndex == 0) {
            throw IllegalStateException("onPreviousPressed when on question 0")
        }
        changeQuestion(questionIndex - 1)
    }

    fun onNextPressed() {
        changeQuestion(questionIndex + 1)
    }

    private fun changeQuestion(newQuestionIndex: Int) {
        questionIndex = newQuestionIndex
        _isNextEnabled.value = getIsNextEnabled(newQuestionIndex)
        _surveyScreenData.value = createSurveyScreenData()
    }

    fun onDonePressed(onSurveyComplete: () -> Unit) {
        // Here is where you could validate that the requirements of the survey are complete
        onSurveyComplete()
    }

    //    fun onFreeTimeResponse(selected: Boolean, answer: Int) {
//        if (selected) {
//            _freeTimeResponse.add(answer)
//        } else {
//            _freeTimeResponse.remove(answer)
//        }
//        _isNextEnabled.value = getIsNextEnabled()
//    }
//
//    fun onSuperheroResponse(superhero: Superhero) {
//        _superheroResponse.value = superhero
//        _isNextEnabled.value = getIsNextEnabled(-1)
//    }
    //
//    fun onTakeawayResponse(timestamp: Long) {
//        _takeawayResponse.value = timestamp
//        _isNextEnabled.value = getIsNextEnabled()
//    }
//
//    fun onFeelingAboutSelfiesResponse(feeling: Float) {
//        _feelingAboutSelfiesResponse.value = feeling
//        _isNextEnabled.value = getIsNextEnabled()
//    }
//
//    fun onSelfieResponse(uri: Uri) {
//        _selfieUri.value = uri
//        _isNextEnabled.value = getIsNextEnabled()
//    }
//
//    fun getNewSelfieUri() = photoUriManager.buildNewUri()
    private var translatedQuestion : Map<Int, String> = (emptyMap())


    private var _translatedQuestions = MutableStateFlow<Map<Int, String>>(emptyMap())
    val translatedQuestions: StateFlow<Map<Int, String>> get() = _translatedQuestions

    private var _translatedSubQuestions = MutableStateFlow<Map<Int, String>>(emptyMap())
    val translatedSubQuestions: StateFlow<Map<Int, String>> get() = _translatedSubQuestions

    private val _allQuestionsAnswered = mutableStateOf(false)
    val allQuestionsAnswered: State<Boolean> = _allQuestionsAnswered

    // Existing code...
    fun getTranslatedQuestion(index: Int): String? {
        return _translatedQuestions.value?.get(index)
    }
    fun addTranslatedQuestion(questionId: Int, translatedText: String) {
        _translatedQuestions.value = _translatedQuestions.value + (questionId to translatedText)
    }

    fun addTranslatedSubQuestion(subQuestionId: Int, translatedText: String) {
        _translatedSubQuestions.value = _translatedSubQuestions.value + (subQuestionId to translatedText)
    }

    private var _translatedOptions = MutableStateFlow<Map<Int, List<String>>>(emptyMap())
    val translatedOptions: StateFlow<Map<Int, List<String>>> get() = _translatedOptions
    fun getTranslatedOptions(index: Int): List<String>? {
        return _translatedOptions.value[index]
    }
    fun addTranslatedOptions(questionId: Int, options: List<String>?) {
        val currentMap = _translatedOptions.value.toMutableMap()
        currentMap[questionId] = options ?: emptyList()
        _translatedOptions.value = currentMap
    }
    private var _translatedSubOptions = MutableStateFlow<Map<Int, List<String>>>(emptyMap())
    val translatedSubOptions: StateFlow<Map<Int, List<String>>> get() = _translatedSubOptions

    fun addTranslatedSubQuestionOptions(id:Int,options: List<String>?){
        val currentMap = _translatedSubOptions.value.toMutableMap()
        currentMap[id] = options ?: emptyList()
        _translatedSubOptions.value = currentMap
    }

    fun setTranslatedQuestions(questions: List<String>) {
        _translatedQuestions.value = emptyMap()
    }
    // Example function to update a text field


    private val _responses = mutableMapOf<Int, String>()
    fun setResponse(questionId: Int, response: String) {
        _responses[questionId] = response
        _isNextEnabled.value = getIsNextEnabled(questionId)
    }
    fun getResponse(questionId: Int): String? {
        return _responses[questionId]
    }
    fun handleQuestionResponse(question: Question, response: String) {


        when (question.type) {
            "obj" -> {
                // Handle response for Objective type (e.g., Int)

                setResponse(question.id, response)

            }
            "num" -> {
                // Handle response for Number type (e.g., Int)

                setResponse(question.id, response)

            }
            "bool" -> {
                // Handle response for Superhero type

                setResponse(question.id, response)

            }
        }

    }

    private fun getIsNextEnabled(id:Int): Boolean {
        // return  _responses[id]!=null
        return true

    }

    private fun createSurveyScreenData(): SurveyScreenData {
        return SurveyScreenData(
            questionIndex = questionIndex,
            questionCount = questionOrder.size,
            shouldShowPreviousButton = questionIndex > 0,
            shouldShowDoneButton = questionIndex == questionOrder.size - 1,
            Question = questionOrder[questionIndex]
        )
    }
    fun translateQuestion(
        question: Question,
        language: String,
        targetLanguage: String,
        context: Context
    ) {
        setLanguageCode(targetLanguage)

        // Translate boolean question and options (e.g., "Yes" and "No")
        translateAndAdd(context,language,targetLanguage,question,"ques")

        question.options?.let { options ->
            translateAndAdd(context,language,targetLanguage,question,"option")
        }

        question.sub1?.let{
            translateAndAdd(context,language,targetLanguage,question,"sub")
        }

        question.sub1?.options?.let {
            translateAndAdd(context,language,targetLanguage,question,"subOption")
        }

        // Handle other types as needed

    }

    private fun translateAndAdd(context: Context, language: String, targetLanguage: String, question: Question, type:String): String {
        setLanguageCode(targetLanguage)
        val options = TranslatorOptions
            .Builder()
            .setSourceLanguage(language)
            .setTargetLanguage(targetLanguage)
            .build()

        val languageTranslator = Translation
            .getClient(options)
        var t = question.ques
        when(type) {
            "ques"-> {
                languageTranslator.translate(question.ques)
                    .addOnSuccessListener { translatedText ->
                        t = translatedText
                        addTranslatedQuestion(question.id,translatedText)
                        translatedQuestion = translatedQuestion + (question.id to translatedText)

                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Downloading started..",
                            Toast.LENGTH_SHORT
                        ).show()
                        downloadModelIfNotAvailable(languageTranslator, context)
                    }
            }
            "option"->{
                val translatedOptionsList = mutableListOf<String>()

                for(option in question.options!!){
                    languageTranslator.translate(option)
                        .addOnSuccessListener { translatedText ->
                            t = translatedText
                            translatedOptionsList.add(translatedText)
                            println(translatedText)
                            if (translatedOptionsList.size == question.options.size) {
                                addTranslatedOptions(question.id, translatedOptionsList)
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Downloading started..",
                                Toast.LENGTH_SHORT
                            ).show()
                            downloadModelIfNotAvailable(languageTranslator, context)
                        }
                }
            }
            "sub"->{
                question.sub1?.let {
                    languageTranslator.translate(it.ques)
                        .addOnSuccessListener { translatedText ->
                            t = translatedText
                            addTranslatedSubQuestion(it.id,translatedText)
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Downloading started..",
                                Toast.LENGTH_SHORT
                            ).show()
                            downloadModelIfNotAvailable(languageTranslator, context)
                        }
                }
            }
            "subOption"->{
                val translatedOptionsList = mutableListOf<String>()

                for(option in question.sub1?.options!!){
                    languageTranslator.translate(option)
                        .addOnSuccessListener { translatedText ->
                            t = translatedText
                            translatedOptionsList.add(translatedText)

                            if (translatedOptionsList.size == question.sub1.options.size) {
                                addTranslatedSubQuestionOptions(question.sub1.id, translatedOptionsList)
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Downloading started..",
                                Toast.LENGTH_SHORT
                            ).show()
                            downloadModelIfNotAvailable(languageTranslator, context)
                        }
                }
            }
//            else ->{
//                state.value.textFields.forEachIndexed { index, s ->
//                    languageTranslator.translate(s)
//                        .addOnSuccessListener { translatedText ->
//                            t = translatedText
//                            resultMap = resultMap+(index to translatedText)
//                        }
//                        .addOnFailureListener {
//                            Toast.makeText(
//                                context,
//                                "Downloading started..",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            downloadModelIfNotAvailable(languageTranslator, context)
//                        }
//                }
//            }
        }
        return t
    }



}
class SurveyViewModelFactory(
    private val photoUriManager: PhotoUriManager,
    val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SurveyViewModel::class.java)) {
            return SurveyViewModel(photoUriManager, context = context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


enum class SurveyQuestion {
    FREE_TIME,
    SUPERHERO,
    LAST_TAKEAWAY,
    FEELING_ABOUT_SELFIES,
    TAKE_SELFIE,
}

data class SurveyScreenData(
    val questionIndex: Int,
    val questionCount: Int,
    val shouldShowPreviousButton: Boolean,
    val shouldShowDoneButton: Boolean,
    val Question: Question
)

