package com.example.skeleton.redux

import redux.api.Reducer
import redux.api.Store
import com.example.skeleton.client.Login
import com.example.skeleton.client.WebClient
import com.example.skeleton.client.WebClient.Companion.client
import com.example.skeleton.model.MyLoginSession

@Suppress("unused")
class ClientStore {
    // Generalized APIState
    data class APIState<out T>(
            val inprogress: Boolean = false,
            val result: WebClient.Result = WebClient.Result.Success,
            val data: T? = null
    )
    // State
    data class State(
        val login: APIState<Login.Output> = APIState(),
        val me: MyLoginSession? = null
    ) {
        val isLogined: Boolean
            get() = this.me != null
    }
    // Actions
    sealed class Action {
        class LoginBegin : Action()
        class LoginComplete(val result: WebClient.Result, val output: Login.Output?) : Action()
    }
    companion object {
        // Reducer
        private val reducer = Reducer<State> { state, action ->
            when (action) {
                is Action.LoginBegin -> {
                    state.copy(
                            login = APIState(inprogress = true)
                    )
                }
                is Action.LoginComplete -> {
                    state.copy(
                            login = APIState(inprogress = false, result = action.result, data = action.output),
                            me = action.output?.me
                    )
                }
                else -> state
            }
        }
        fun createStore(): Store<State> {
            return redux.createStore(reducer, State())
        }
        // Action Creator
        fun login(store: AppStore, input: Login.Input): Boolean {
            if (store.client.state.login.inprogress) return false
            store.dispatch(Action.LoginBegin())
            client().login(input, { result, output ->
                store.dispatch(Action.LoginComplete(result, output))
            })
            return true
        }
    }
}
