package dev.usrmrz.searchgithub.presentation.repo

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import javax.inject.Inject

@HiltViewModel
class RepoViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
) : ViewModel() { }
