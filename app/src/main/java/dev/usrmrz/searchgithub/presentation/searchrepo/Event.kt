package dev.usrmrz.searchgithub.presentation.searchrepo

sealed class Event {
    data class Search(val query: String) : Event()
}



















