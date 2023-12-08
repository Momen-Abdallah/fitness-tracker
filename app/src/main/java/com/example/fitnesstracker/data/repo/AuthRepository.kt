package com.example.fitnesstracker.data.repo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.fitnesstracker.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val context: Context,
    private val fireStore : FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val signInRequest: BeginSignInRequest,
    private val onTapClient: SignInClient,

){
    suspend fun signInWithGoogle() : BeginSignInResult?{
//        signInClient.beginSignIn(signInRequest).await()

        try {
//            val result = Identity.getSignInClient(context).beginSignIn(
//                BeginSignInRequest.builder()
//                    .setGoogleIdTokenRequestOptions(
//                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                            .setSupported(true)
//                            .setServerClientId(context.getString(R.string.web_client_id))
//                            .setFilterByAuthorizedAccounts(false)
//                            .build()
//                    )
//                    .setAutoSelectEnabled(true)
//                    .build()).await()
//
//            return result

            return onTapClient.beginSignIn(signInRequest).await()
        }catch (e : Exception){
            return null
        }

    }


    suspend fun signInWithCredential(credential: SignInCredential){
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(credential.googleIdToken,null)).await()
    }



    suspend fun addUserToFirestore(credential: SignInCredential){
        fireStore.collection("users").document(firebaseAuth.uid!!).set(
            mapOf(
                "name" to credential.givenName,
                "profile_picture" to credential.profilePictureUri
            )
        ).await()
    }



}