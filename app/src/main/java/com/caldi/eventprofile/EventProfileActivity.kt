package com.caldi.eventprofile

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.constants.EVENT_ID_KEY
import com.caldi.eventprofile.list.QuestionsAdapter
import com.caldi.eventprofile.list.QuestionsViewModel
import com.caldi.eventprofile.models.Question
import com.caldi.factories.EventProfileViewModelFactory
import com.caldi.factories.QuestionsViewModelFactory
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_event_profile.createProfilePromptTextView
import kotlinx.android.synthetic.main.activity_event_profile.nameEditText
import kotlinx.android.synthetic.main.activity_event_profile.questionsRecyclerView
import kotlinx.android.synthetic.main.activity_event_profile.saveProfileButton
import javax.inject.Inject

class EventProfileActivity : BaseDrawerActivity(), EventProfileView {

    private var eventId = ""

    private lateinit var eventProfileViewModel: EventProfileViewModel

    private lateinit var questionsViewModel: QuestionsViewModel

    @Inject
    lateinit var eventProfileViewModelFactory: EventProfileViewModelFactory

    @Inject
    lateinit var questionsViewModelFactory: QuestionsViewModelFactory

    private val questionsAdapter: QuestionsAdapter = QuestionsAdapter()

    companion object {

        fun start(context: Context, eventId: String) {
            val intent = Intent(context, EventProfileActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_event_profile)
        super.onCreate(savedInstanceState)
        setNavigationSelection(R.id.event_profile_item)
        setPromptText()

        eventId = intent.getStringExtra(EVENT_ID_KEY)

        eventProfileViewModel = ViewModelProviders.of(this, eventProfileViewModelFactory)[EventProfileViewModel::class.java]
        questionsViewModel = ViewModelProviders.of(this, questionsViewModelFactory)[QuestionsViewModel::class.java]

        questionsAdapter.questionsViewModel = questionsViewModel
        questionsRecyclerView.layoutManager = LinearLayoutManager(this)
        questionsRecyclerView.adapter = questionsAdapter

        questionsAdapter.setQuestionsList(listOf(Question("0", "What do you do?"),
                Question("1", "What do you do?"),
                Question("2", "What do you do?")))
    }

    override fun onStart() {
        super.onStart()
        eventProfileViewModel.bind(this)
    }

    override fun onStop() {
        eventProfileViewModel.unbind()
        super.onStop()
    }

    override fun onDestroy() {
        questionsViewModel.unbind()
        super.onDestroy()
    }

    private fun setPromptText() {
        val promptString = getString(R.string.create_profile_prompt)
        val wordsToBeColored = getString(R.string.create_profile_words_to_be_colored)
        val startOfColoring = promptString.indexOf(wordsToBeColored)
        if (startOfColoring > 0) {
            val endOfColoring = startOfColoring + wordsToBeColored.length
            val spannableString = SpannableString(promptString)
            spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorLightGreen)),
                    startOfColoring, endOfColoring, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            createProfilePromptTextView.setText(spannableString, TextView.BufferType.SPANNABLE)
        }
    }

    override fun emitInputData(): Observable<InputData> {
        return RxView.clicks(saveProfileButton)
                .flatMap { questionsAdapter.emitAnswers() }
                .flatMap { Observable.just(InputData(nameEditText.text.toString(), it)) }
    }

    override fun render(eventProfileViewState: EventProfileViewState) {
        Toast.makeText(this, eventProfileViewState.success.toString(), Toast.LENGTH_SHORT).show()
    }
}