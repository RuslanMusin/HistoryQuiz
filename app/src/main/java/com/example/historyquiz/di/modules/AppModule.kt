package com.example.historyquiz.di.modules
import com.example.historyquiz.ui.auth.fragments.login.LoginFragment
import com.example.historyquiz.ui.auth.fragments.signup.SignUpFragment
import com.example.historyquiz.ui.cards.add_card.AddCardFragment
import com.example.historyquiz.ui.cards.add_card_list.AddCardListFragment
import com.example.historyquiz.ui.cards.card_item.CardFragment
import com.example.historyquiz.ui.cards.card_list.CardListFragment
import com.example.historyquiz.ui.cards.wiki_page.WikiPageFragment
import com.example.historyquiz.ui.navigation.NavigationActivity
import com.example.historyquiz.ui.profile.item.ProfileFragment
import com.example.historyquiz.ui.tests.add_test.main.AddMainTestFragment
import com.example.historyquiz.ui.tests.add_test.question.AddQuestionTestFragment
import com.example.historyquiz.ui.tests.add_test.question.AddQuestionTestPresenter
import com.example.historyquiz.ui.tests.test_item.check_answers.AnswersFragment
import com.example.historyquiz.ui.tests.test_item.finish.FinishFragment
import com.example.historyquiz.ui.tests.test_item.main.TestFragment
import com.example.historyquiz.ui.tests.test_item.question.QuestionFragment
import com.example.historyquiz.ui.tests.test_item.winned_card.TestCardFragment
import com.example.historyquiz.ui.tests.test_list.TestListFragment
import com.example.studentapp.di.scopes.ActivityScope
import com.example.studentapp.di.scopes.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [AndroidSupportInjectionModule::class, FirebaseModule::class, RepositoryModule::class])
interface AppModule {

    @ActivityScope
    @ContributesAndroidInjector()
    fun navigationActivityInjector(): NavigationActivity

    @FragmentScope
    @ContributesAndroidInjector()
    fun loginFragmentInjector(): LoginFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun signUpFragmentInjector(): SignUpFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun profileFragmentInjector(): ProfileFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun addCardFragmentInjector(): AddCardFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun addCardListFragmentInjector(): AddCardListFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun addMainTestFragmentInjector(): AddMainTestFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun addQuestionTestFragmentInjector(): AddQuestionTestFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun testFragmentInjector(): TestFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun questionFragmentInjector(): QuestionFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun finishFragmentInjector(): FinishFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun testCardFragmentInjector(): TestCardFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun answersFragmentInjector(): AnswersFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun testListFragmentInjector(): TestListFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun cardListFragmentInjector(): CardListFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun cardFragmentInjector(): CardFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun wikiPageFragmentInjector(): WikiPageFragment
}

