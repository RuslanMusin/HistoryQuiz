package com.example.historyquiz.di.components

import android.content.Context
import com.example.historyquiz.di.modules.AppModule
import com.example.historyquiz.ui.auth.fragments.login.LoginFragmentPresenter
import com.example.historyquiz.ui.auth.fragments.signup.SignUpFragment
import com.example.historyquiz.ui.auth.fragments.signup.SignUpPresenter
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.cards.add_card.AddCardPresenter
import com.example.historyquiz.ui.cards.add_card_list.AddCardListPresenter
import com.example.historyquiz.ui.profile.item.ProfilePresenter
import com.example.historyquiz.ui.tests.add_test.main.AddMainTestPresenter
import com.example.historyquiz.ui.tests.add_test.question.AddQuestionTestPresenter
import com.example.historyquiz.ui.tests.test_item.check_answers.AnswersPresenter
import com.example.historyquiz.ui.tests.test_item.finish.FinishPresenter
import com.example.historyquiz.ui.tests.test_item.main.TestPresenter
import com.example.historyquiz.ui.tests.test_item.question.QuestionPresenter
import com.example.historyquiz.ui.tests.test_item.winned_card.TestCardPresenter
import com.example.historyquiz.ui.tests.test_list.TestListPresenter
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)

    //Presenters
    fun inject(presenter: LoginFragmentPresenter)

    fun inject(presenter: SignUpPresenter)

    fun inject(presenter: ProfilePresenter)

    fun inject(presenter: AddCardListPresenter)
    fun inject(presenter: AddCardPresenter)
    fun inject(presenter: AddMainTestPresenter)
    fun inject(presenter: AddQuestionTestPresenter)

    fun inject(presenter: TestListPresenter)
    fun inject(presenter: TestPresenter)
    fun inject(presenter: FinishPresenter)
    fun inject(presenter: QuestionPresenter)
    fun inject(presenter: TestCardPresenter)
    fun inject(presenter: AnswersPresenter)




}