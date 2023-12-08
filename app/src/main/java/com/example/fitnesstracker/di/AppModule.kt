package com.example.fitnesstracker.di

import android.content.Context
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.repo.AuthRepository
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun providesAuth() = Firebase.auth
    @Provides
    @Singleton
    fun providesFireStore() = Firebase.firestore
//    @Provides
//    @Singleton
//    fun providesContext(@ApplicationContext context: ApplicationContext) = context

    @Provides
    @Singleton
    fun provideRepository(
        @ApplicationContext context: Context,
        fireStore : FirebaseFirestore,
        auth : FirebaseAuth,
        signInRequest: BeginSignInRequest,
        signInClient: SignInClient
    ) = AuthRepository(context ,fireStore,auth,signInRequest,signInClient)

    @Singleton
    @Provides
    fun provideSignInRequest(
        @ApplicationContext context: Context
    ) =
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()
             )
            .setAutoSelectEnabled(true)
            .build()

    @Singleton
    @Provides
    fun provideOnTapClient(@ApplicationContext context: Context)=
        Identity.getSignInClient(context)

}