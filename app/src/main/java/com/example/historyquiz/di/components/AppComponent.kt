package com.example.historyquiz.di.components

import android.app.Application
import com.example.historyquiz.di.modules.AppModule
import com.example.historyquiz.ui.base.CastleApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Component(
    modules = [AppModule::class]
)
@Singleton
interface AppComponent: AndroidInjector<CastleApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<CastleApplication>() {

        @BindsInstance
        abstract fun application(application: Application): Builder
    }
}
/*
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
    fun inject(presenter: SignInPresenter)

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

    fun inject(presenter: CardPresenter)
    fun inject(presenter: CardListPresenter)
    fun inject(presenter: WikiPagePresenter)

    fun inject(presenter: CommentPresenter)





}*/
