package com.example.skeleton

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import com.facebook.drawee.backends.pipeline.Fresco
import com.example.skeleton.redux.ClientStore
import com.example.skeleton.redux.ViewStore
import com.example.skeleton.ui.LoginScreen
import com.example.skeleton.ui.MainScreen
import com.example.skeleton.ui.SplashScreen
import com.example.skeleton.MainApplication.Companion.store

@Suppress("ConstantConditionIf")
class MainActivity : AppCompatActivity() {
    private val mSubscriptions = CompositeDisposable()
    private var mRouter: Router? = null
    private var mStoreRetained: Boolean = false
    private var mInSplash: Boolean = false
    private var mShowingMain: Boolean = false

    //region Lifecycle
    //---------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        if (!mStoreRetained) {
            mStoreRetained = true
            MainApplication.retainStore(this)
        }
        val layout = FrameLayout(this)
        setContentView(layout)
        mRouter = Conductor.attachRouter(this, layout, savedInstanceState)
        mInSplash = true
        mShowingMain = false
        if (AppConfig.SPLASH_DURATION == 0L) {
            showAppContent()
        } else {
            mRouter?.setRoot(RouterTransaction.with(SplashScreen()))
        }
    }
    override fun onStart() {
        // NOTE: We need to get store ready before super.onStart(),
        //       otherwise Conductor will re-create our view and cause NPE upon using store
        if (!mStoreRetained) {
            mStoreRetained = true
            MainApplication.retainStore(this)
        }
        super.onStart()
    }
    override fun onResume() {
        super.onResume()
        mSubscriptions.add(store().observe(store().client)
                .observeOn(AndroidSchedulers.mainThread())
                .map(mapLoginState)
                .distinctUntilChanged()
                .subscribe(consumeLoginState)
        )
        if (mInSplash) {
            // Show main screen after a delay
            window.decorView.postDelayed({ showAppContent() }, AppConfig.SPLASH_DURATION)
        }
        // TODO: resume login session here
    }
    override fun onPause() {
        super.onPause()
        mSubscriptions.clear()
        // TODO: We going background, release resource here
    }
    override fun onStop() {
        super.onStop()
        if (mStoreRetained) {
            mStoreRetained = false
            MainApplication.releaseStore(this)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
    //---------------------------------------------------------------
    //endregion

    //region UI Events
    //---------------------------------------------------------------
    override fun onBackPressed() {
        if (mRouter?.handleBack() != true) {
            super.onBackPressed()
        }
    }
    //---------------------------------------------------------------
    //endregion

    //region Redux
    //---------------------------------------------------------------
    private val mapLoginState = Function<ClientStore.State, Boolean> { state ->
        state.isLogined
    }
    private val consumeLoginState = Consumer<Boolean> { mapped ->
        if (mInSplash) return@Consumer // skip if in splash screen
        if (mShowingMain != mapped) {
            mShowingMain = mapped
            mRouter?.replaceTopController(RouterTransaction.with(if (mShowingMain) MainScreen() else LoginScreen()))
        }
    }
    //---------------------------------------------------------------
    //endregion

    private fun showAppContent() {
        if (mInSplash) {
            mInSplash = false
            mShowingMain = store().client.state.isLogined
            mRouter?.replaceTopController(RouterTransaction.with(if (mShowingMain) MainScreen() else LoginScreen()))
            store().dispatch(ViewStore.Action.SetTitle("Hello World"))
        }
    }
}
