package com.example.fitnesstracker.ui.loginScreen

import androidx.lifecycle.ViewModel
import com.example.fitnesstracker.data.repo.AuthRepository
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class LoginScreenViewModel @Inject constructor(private val repository: AuthRepository,val onTapClient: SignInClient): ViewModel(){
    suspend fun signInWithGoogle() : BeginSignInResult?{
        return repository.signInWithGoogle()
    }

    suspend fun signInWithCredential(credential: SignInCredential){
        repository.signInWithCredential(credential)
    }
    suspend fun addUserToFirestore(credential: SignInCredential){
        repository.addUserToFirestore(credential)
    }

}