package com.example.historyquiz.ui.tests.test_item.finish

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.model.test.result.AnswerResult
import com.example.historyquiz.model.test.result.QuestionResult
import com.example.historyquiz.model.test.result.TestResult
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.repository.epoch.UserEpochRepository
import com.example.historyquiz.repository.test.TestRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.AppHelper.Companion.currentUser
import javax.inject.Inject

@InjectViewState
class FinishPresenter @Inject constructor() : BasePresenter<FinishView>() {

    @Inject
    lateinit var testRepository: TestRepository
    @Inject
    lateinit var cardRepository: CardRepository
    @Inject
    lateinit var userEpochRepository: UserEpochRepository

    fun finishTest(test: Test) {
        testRepository.finishTest(test, currentUser).subscribe()
    }

    fun updateAfterTest(test: Test) {
        userEpochRepository.updateAfterTest(currentId, test).subscribe()
        testRepository.saveUserAnswers(currentId, mapTestToAnswers(test)).subscribe()
    }

    fun findUserAnswers(test: Test) {
        val dis = testRepository.findUserAnswers(currentId, test.id)
            .subscribe { it ->
                viewState.checkAnswers(mapAnswersToTest(test, it))
            }
        compositeDisposable.add(dis)
    }

    private fun mapAnswersToTest(test: Test, testResult: TestResult): Test {
        var i: Int = 0
        var j: Int = 0
        for(question in testResult.questions) {
            test.questions[i].userRight = question.userRight
            for(answer in question.answers) {
                test.questions[i].answers[j].userClicked = answer.userClicked
                j++
            }
            j = 0
            i++
        }
        return test
    }

    private fun mapTestToAnswers(test: Test): TestResult {
        var i: Int = 0
        var j: Int = 0
        val testResult = TestResult()
        testResult.id = test.id
        var questionResult: QuestionResult
        var answerResult: AnswerResult
        for(question in test.questions) {
            questionResult = QuestionResult()
            questionResult.userRight = question.userRight
            for(answer in question.answers) {
                answerResult = AnswerResult()
                answerResult.userClicked = answer.userClicked
                questionResult.answers.add(answerResult)
                j++
            }
            testResult.questions.add(questionResult)
            j = 0
            i++
        }
        return testResult
    }

    fun checkUserCard(test: Test) {
        val dis = cardRepository.isUserHasCard(currentId, test.cardId!!)
            .subscribe { it ->
                viewState.setCardData(it)
            }
        compositeDisposable.add(dis)
    }

}