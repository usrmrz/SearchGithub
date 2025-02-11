package dev.usrmrz.searchgithub.domain.repository

interface TimeProvider {

    fun now(): Long
}