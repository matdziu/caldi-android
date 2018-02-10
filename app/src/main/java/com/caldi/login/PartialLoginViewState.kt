package com.caldi.login

sealed class PartialLoginViewState {

    class InProgressState : PartialLoginViewState()
}